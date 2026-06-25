package com.librarynet.service;

import com.librarynet.domain.*;
import com.librarynet.dto.BorrowRequest;
import com.librarynet.dto.LoanResponse;
import com.librarynet.exception.ConflictException;
import com.librarynet.exception.ResourceNotFoundException;
import com.librarynet.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {
    private final BorrowTransactionRepository loans;
    private final BookRepository books;
    private final MemberRepository members;

    public LoanService(BorrowTransactionRepository loans, BookRepository books, MemberRepository members) {
        this.loans = loans;
        this.books = books;
        this.members = members;
    }

    public List<LoanResponse> list() {
        return loans.findAllByOrderByBorrowedAtDescIdDesc().stream().map(LoanResponse::from).toList();
    }

    @Transactional
    public LoanResponse borrow(BorrowRequest request) {
        MemberEntity member = members.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + request.memberId()));
        BookEntity book = books.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + request.bookId()));
        if (!member.isActive()) throw new ConflictException("Member account is inactive");
        if (!book.isAvailable() || loans.existsByBookIdAndStatus(book.getId(), LoanStatus.BORROWED)) {
            throw new ConflictException("Book is already borrowed");
        }
        long activeCount = loans.countByMemberIdAndStatus(member.getId(), LoanStatus.BORROWED);
        if (activeCount >= member.getMaxBorrowLimit()) {
            throw new ConflictException("Member reached the borrowing limit of " + member.getMaxBorrowLimit());
        }

        LocalDate now = LocalDate.now();
        BorrowTransactionEntity loan = new BorrowTransactionEntity(member, book, now, now.plusDays(14));
        book.setAvailable(false);
        book.setBorrowCount(book.getBorrowCount() + 1);
        books.save(book);
        return LoanResponse.from(loans.save(loan));
    }

    @Transactional
    public LoanResponse returnBook(Long loanId) {
        BorrowTransactionEntity loan = loans.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));
        if (loan.getStatus() == LoanStatus.RETURNED) throw new ConflictException("Book was already returned");
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(LocalDate.now());
        loan.getBook().setAvailable(true);
        books.save(loan.getBook());
        return LoanResponse.from(loans.save(loan));
    }
}
