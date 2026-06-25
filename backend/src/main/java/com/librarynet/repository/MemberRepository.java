package com.librarynet.repository;

import com.librarynet.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByMemberCode(String memberCode);
    boolean existsByMemberCode(String memberCode);
    boolean existsByEmailIgnoreCase(String email);
    List<MemberEntity> findAllByOrderByNameAsc();
}
