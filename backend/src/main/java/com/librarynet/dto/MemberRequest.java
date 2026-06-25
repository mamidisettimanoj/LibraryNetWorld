package com.librarynet.dto;

import jakarta.validation.constraints.*;

public record MemberRequest(
        @NotBlank @Size(max = 30) String memberCode,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 100) String department,
        @Min(1) @Max(10) int maxBorrowLimit,
        Boolean active
) { }
