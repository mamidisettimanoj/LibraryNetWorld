package com.librarynet.dto;

import com.librarynet.domain.BorrowTransactionEntity;
import com.librarynet.domain.LoanStatus;
import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Long memberId,
        String memberCode,
        String memberName,
        Long bookId,
        Integer catalogId,
        String bookTitle,
        LocalDate borrowedAt,
        LocalDate dueAt,
        LocalDate returnedAt,
        LoanStatus status,
        boolean overdue
) {
    public static LoanResponse from(BorrowTransactionEntity loan) {
        return new LoanResponse(loan.getId(), loan.getMember().getId(), loan.getMember().getMemberCode(),
                loan.getMember().getName(), loan.getBook().getId(), loan.getBook().getCatalogId(),
                loan.getBook().getTitle(), loan.getBorrowedAt(), loan.getDueAt(), loan.getReturnedAt(),
                loan.getStatus(), loan.isOverdue());
    }
}
