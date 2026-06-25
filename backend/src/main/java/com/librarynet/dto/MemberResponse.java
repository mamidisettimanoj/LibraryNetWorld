package com.librarynet.dto;

import com.librarynet.domain.MemberEntity;

public record MemberResponse(
        Long id,
        String memberCode,
        String name,
        String email,
        String department,
        boolean active,
        int maxBorrowLimit
) {
    public static MemberResponse from(MemberEntity member) {
        return new MemberResponse(member.getId(), member.getMemberCode(), member.getName(),
                member.getEmail(), member.getDepartment(), member.isActive(), member.getMaxBorrowLimit());
    }
}
