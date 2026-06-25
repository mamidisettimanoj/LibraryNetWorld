package com.librarynet.dto;

import jakarta.validation.constraints.NotNull;

public record BorrowRequest(
        @NotNull Long memberId,
        @NotNull Long bookId
) { }
