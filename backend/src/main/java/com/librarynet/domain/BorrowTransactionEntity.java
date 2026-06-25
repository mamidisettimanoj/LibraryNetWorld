package com.librarynet.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_transactions", indexes = {
        @Index(name = "idx_loans_status", columnList = "status"),
        @Index(name = "idx_loans_borrowed_at", columnList = "borrowed_at")
})
public class BorrowTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(name = "borrowed_at", nullable = false)
    private LocalDate borrowedAt;

    @Column(name = "due_at", nullable = false)
    private LocalDate dueAt;

    @Column(name = "returned_at")
    private LocalDate returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status = LoanStatus.BORROWED;

    public BorrowTransactionEntity() { }

    public BorrowTransactionEntity(MemberEntity member, BookEntity book, LocalDate borrowedAt, LocalDate dueAt) {
        this.member = member;
        this.book = book;
        this.borrowedAt = borrowedAt;
        this.dueAt = dueAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MemberEntity getMember() { return member; }
    public void setMember(MemberEntity member) { this.member = member; }
    public BookEntity getBook() { return book; }
    public void setBook(BookEntity book) { this.book = book; }
    public LocalDate getBorrowedAt() { return borrowedAt; }
    public void setBorrowedAt(LocalDate borrowedAt) { this.borrowedAt = borrowedAt; }
    public LocalDate getDueAt() { return dueAt; }
    public void setDueAt(LocalDate dueAt) { this.dueAt = dueAt; }
    public LocalDate getReturnedAt() { return returnedAt; }
    public void setReturnedAt(LocalDate returnedAt) { this.returnedAt = returnedAt; }
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    @Transient
    public boolean isOverdue() {
        return status == LoanStatus.BORROWED && dueAt.isBefore(LocalDate.now());
    }
}
