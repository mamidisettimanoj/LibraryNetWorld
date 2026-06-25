package com.librarynet.service;

import com.librarynet.domain.BorrowTransactionEntity;
import com.librarynet.domain.LoanStatus;
import com.librarynet.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class AnalyticsService {
    private final BookRepository books;
    private final MemberRepository members;
    private final BorrowTransactionRepository loans;
    private final PublicationRepository publications;

    public AnalyticsService(BookRepository books, MemberRepository members,
                            BorrowTransactionRepository loans, PublicationRepository publications) {
        this.books = books;
        this.members = members;
        this.loans = loans;
        this.publications = publications;
    }

    public Map<String, Object> dashboard() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("bookCount", books.count());
        result.put("availableBooks", books.findAll().stream().filter(book -> book.isAvailable()).count());
        result.put("memberCount", members.count());
        result.put("publicationCount", publications.count());
        result.put("activeLoans", loans.countByStatus(LoanStatus.BORROWED));
        result.put("returnedLoans", loans.countByStatus(LoanStatus.RETURNED));
        result.put("overdueLoans", loans.findByStatusOrderByDueAtAsc(LoanStatus.BORROWED).stream()
                .filter(BorrowTransactionEntity::isOverdue).count());
        result.put("popularBooks", books.findTop5ByOrderByBorrowCountDescTitleAsc().stream()
                .map(book -> Map.of("title", book.getTitle(), "borrowCount", book.getBorrowCount(),
                        "catalogId", book.getCatalogId())).toList());
        result.put("categoryDistribution", categoryDistribution());
        result.put("monthlyLoans", monthlyLoans(6));
        return result;
    }

    public List<Map<String, Object>> monthlyLoans(int months) {
        if (months < 1 || months > 24) throw new IllegalArgumentException("Months must be between 1 and 24");
        YearMonth end = YearMonth.now();
        YearMonth start = end.minusMonths(months - 1L);
        Map<YearMonth, Long> counts = new LinkedHashMap<>();
        for (int i = 0; i < months; i++) counts.put(start.plusMonths(i), 0L);
        for (BorrowTransactionEntity loan : loans.findAll()) {
            YearMonth month = YearMonth.from(loan.getBorrowedAt());
            if (counts.containsKey(month)) counts.put(month, counts.get(month) + 1);
        }
        return counts.entrySet().stream().map(entry -> {
            YearMonth month = entry.getKey();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("month", month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + month.getYear());
            row.put("count", entry.getValue());
            return row;
        }).toList();
    }

    public List<Map<String, Object>> categoryDistribution() {
        Map<String, Long> counts = new TreeMap<>();
        books.findAll().forEach(book -> counts.merge(book.getCategory(), 1L, Long::sum));
        return counts.entrySet().stream().map(entry -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("category", entry.getKey());
            row.put("count", entry.getValue());
            return row;
        }).toList();
    }
}
