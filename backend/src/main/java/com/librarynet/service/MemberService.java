package com.librarynet.service;

import com.librarynet.domain.MemberEntity;
import com.librarynet.dto.MemberRequest;
import com.librarynet.dto.MemberResponse;
import com.librarynet.exception.ConflictException;
import com.librarynet.exception.ResourceNotFoundException;
import com.librarynet.repository.BorrowTransactionRepository;
import com.librarynet.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {
    private final MemberRepository members;
    private final BorrowTransactionRepository loans;

    public MemberService(MemberRepository members, BorrowTransactionRepository loans) {
        this.members = members;
        this.loans = loans;
    }

    public List<MemberResponse> list() {
        return members.findAllByOrderByNameAsc().stream().map(MemberResponse::from).toList();
    }

    public MemberEntity getEntity(Long id) {
        return members.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
    }

    @Transactional
    public MemberResponse create(MemberRequest request) {
        if (members.existsByMemberCode(request.memberCode().trim())) {
            throw new ConflictException("Member code already exists: " + request.memberCode());
        }
        if (members.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ConflictException("Email already exists: " + request.email());
        }
        MemberEntity member = new MemberEntity(request.memberCode().trim(), request.name().trim(),
                request.email().trim(), request.department().trim(), request.maxBorrowLimit());
        member.setActive(request.active() == null || request.active());
        return MemberResponse.from(members.save(member));
    }

    @Transactional
    public MemberResponse update(Long id, MemberRequest request) {
        MemberEntity member = getEntity(id);
        members.findByMemberCode(request.memberCode().trim()).filter(other -> !other.getId().equals(id)).ifPresent(other -> {
            throw new ConflictException("Member code already exists: " + request.memberCode());
        });
        members.findAll().stream()
                .filter(other -> request.email().trim().equalsIgnoreCase(other.getEmail()))
                .filter(other -> !other.getId().equals(id))
                .findFirst()
                .ifPresent(other -> { throw new ConflictException("Email already exists: " + request.email()); });
        member.setMemberCode(request.memberCode().trim());
        member.setName(request.name().trim());
        member.setEmail(request.email().trim());
        member.setDepartment(request.department().trim());
        member.setMaxBorrowLimit(request.maxBorrowLimit());
        member.setActive(request.active() == null || request.active());
        return MemberResponse.from(members.save(member));
    }

    @Transactional
    public void delete(Long id) {
        MemberEntity member = getEntity(id);
        if (loans.existsByMemberId(id)) {
            throw new ConflictException("Members with borrowing history cannot be deleted");
        }
        members.delete(member);
    }
}
