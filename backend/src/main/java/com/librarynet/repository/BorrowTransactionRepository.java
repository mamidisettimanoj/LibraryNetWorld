package com.librarynet.repository;

import com.librarynet.domain.BorrowTransactionEntity;
import com.librarynet.domain.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowTransactionRepository extends JpaRepository<BorrowTransactionEntity, Long> {
    List<BorrowTransactionEntity> findAllByOrderByBorrowedAtDescIdDesc();
    List<BorrowTransactionEntity> findByStatusOrderByDueAtAsc(LoanStatus status);
    long countByStatus(LoanStatus status);
    long countByMemberIdAndStatus(Long memberId, LoanStatus status);
    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);
    boolean existsByBookId(Long bookId);
    boolean existsByMemberId(Long memberId);
}
