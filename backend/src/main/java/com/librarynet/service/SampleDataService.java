package com.librarynet.service;

import com.librarynet.domain.*;
import com.librarynet.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class SampleDataService {
    private final BookRepository books;
    private final MemberRepository members;
    private final PublicationRepository publications;
    private final BorrowTransactionRepository loans;
    private final KnowledgeEdgeRepository edges;

    public SampleDataService(BookRepository books, MemberRepository members, PublicationRepository publications,
                             BorrowTransactionRepository loans, KnowledgeEdgeRepository edges) {
        this.books = books;
        this.members = members;
        this.publications = publications;
        this.loans = loans;
        this.edges = edges;
    }

    @Transactional
    public void seedIfEmpty() {
        if (books.count() == 0) seed();
    }

    @Transactional
    public void reset() {
        loans.deleteAll();
        edges.deleteAll();
        publications.deleteAll();
        members.deleteAll();
        books.deleteAll();
        seed();
    }

    private void seed() {
        List<BookEntity> catalog = List.of(
                new BookEntity(0, "Introduction to Algorithms", "Cormen et al.", "Algorithms", 2022, "9780262046305"),
                new BookEntity(1, "Data Structures Using Java", "Reema Thareja", "Data Structures", 2021, "9780190124083"),
                new BookEntity(2, "Database System Concepts", "Silberschatz", "Databases", 2019, "9780078022159"),
                new BookEntity(3, "Operating System Concepts", "Silberschatz", "Operating Systems", 2020, "9781119456339"),
                new BookEntity(4, "Computer Networks", "Andrew S. Tanenbaum", "Networks", 2021, "9780136764052"),
                new BookEntity(5, "Artificial Intelligence", "Stuart Russell", "Artificial Intelligence", 2021, "9780134610993"),
                new BookEntity(6, "Machine Learning", "Tom Mitchell", "Artificial Intelligence", 2017, "9780070428072"),
                new BookEntity(7, "Clean Code", "Robert C. Martin", "Software Engineering", 2008, "9780132350884"),
                new BookEntity(8, "Design Patterns", "Erich Gamma", "Software Engineering", 1994, "9780201633610"),
                new BookEntity(9, "Computer Architecture", "David Patterson", "Computer Architecture", 2020, "9780128201091"),
                new BookEntity(10, "Cloud Computing", "Thomas Erl", "Cloud", 2018, "9780133387520"),
                new BookEntity(11, "Deep Learning", "Ian Goodfellow", "Artificial Intelligence", 2016, "9780262035613")
        );
        books.saveAll(catalog);

        List<MemberEntity> people = List.of(
                new MemberEntity("M1001", "Manoj Mamidisetti", "manoj@librarynet.edu", "CSE", 4),
                new MemberEntity("M1002", "Harshitha J", "harshitha@librarynet.edu", "CSE", 3),
                new MemberEntity("M1003", "Ram Sri Charan", "ram@librarynet.edu", "CSE", 3),
                new MemberEntity("M1004", "Aarav Sharma", "aarav@librarynet.edu", "ECE", 2),
                new MemberEntity("M1005", "Isha Reddy", "isha@librarynet.edu", "AI & DS", 4)
        );
        members.saveAll(people);

        publications.saveAll(List.of(
                new PublicationEntity(201, "Knowledge Graphs for Education", "A. Kumar", 2022, "JOURNAL", "https://example.edu/201"),
                new PublicationEntity(202, "Efficient B+ Tree Indexing", "R. Singh", 2020, "JOURNAL", "https://example.edu/202"),
                new PublicationEntity(203, "Graph Search in Digital Libraries", "S. Rao", 2021, "CONFERENCE", "https://example.edu/203"),
                new PublicationEntity(204, "Borrowing Trend Analytics", "P. Das", 2023, "JOURNAL", "https://example.edu/204"),
                new PublicationEntity(205, "Large Language Models", "N. Patel", 2024, "EBOOK", "https://example.edu/205"),
                new PublicationEntity(206, "Data Structures Laboratory Manual", "KL University", 2025, "PDF", "https://example.edu/206"),
                new PublicationEntity(207, "Recommendation Systems", "J. Lee", 2019, "JOURNAL", "https://example.edu/207"),
                new PublicationEntity(208, "Modern Database Engineering", "M. Stone", 2018, "EBOOK", "https://example.edu/208")
        ));

        Map<Integer, BookEntity> byCatalogId = new HashMap<>();
        books.findAll().forEach(book -> byCatalogId.put(book.getCatalogId(), book));
        int[][] graph = {
                {0,1,2}, {0,2,5}, {1,3,4}, {1,5,7}, {2,4,3}, {2,10,6},
                {3,4,2}, {3,9,5}, {4,10,4}, {5,6,2}, {5,11,3}, {6,11,2},
                {7,8,1}, {8,9,6}, {9,10,3}, {10,11,4}
        };
        List<KnowledgeEdgeEntity> links = new ArrayList<>();
        for (int[] edge : graph) {
            links.add(new KnowledgeEdgeEntity(byCatalogId.get(edge[0]), byCatalogId.get(edge[1]), edge[2], "RELATED"));
        }
        edges.saveAll(links);

        List<MemberEntity> savedMembers = members.findAllByOrderByNameAsc();
        LocalDate now = LocalDate.now();
        BorrowTransactionEntity returned1 = new BorrowTransactionEntity(savedMembers.get(0), byCatalogId.get(0), now.minusMonths(4), now.minusMonths(4).plusDays(14));
        returned1.setStatus(LoanStatus.RETURNED);
        returned1.setReturnedAt(now.minusMonths(4).plusDays(8));
        BorrowTransactionEntity returned2 = new BorrowTransactionEntity(savedMembers.get(1), byCatalogId.get(5), now.minusMonths(2), now.minusMonths(2).plusDays(14));
        returned2.setStatus(LoanStatus.RETURNED);
        returned2.setReturnedAt(now.minusMonths(2).plusDays(10));
        BorrowTransactionEntity active = new BorrowTransactionEntity(savedMembers.get(2), byCatalogId.get(2), now.minusDays(5), now.plusDays(9));
        BorrowTransactionEntity overdue = new BorrowTransactionEntity(savedMembers.get(3), byCatalogId.get(7), now.minusDays(25), now.minusDays(11));
        loans.saveAll(List.of(returned1, returned2, active, overdue));
        byCatalogId.get(0).setBorrowCount(1);
        byCatalogId.get(5).setBorrowCount(1);
        byCatalogId.get(2).setBorrowCount(1);
        byCatalogId.get(7).setBorrowCount(1);
        byCatalogId.get(2).setAvailable(false);
        byCatalogId.get(7).setAvailable(false);
        books.saveAll(byCatalogId.values());
    }
}
