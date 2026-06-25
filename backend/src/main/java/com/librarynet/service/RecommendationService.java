package com.librarynet.service;

import com.librarynet.domain.BookEntity;
import com.librarynet.domain.KnowledgeEdgeEntity;
import com.librarynet.dto.BookResponse;
import com.librarynet.dto.RecommendationResponse;
import com.librarynet.repository.BookRepository;
import com.librarynet.repository.KnowledgeEdgeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {
    private final BookRepository books;
    private final KnowledgeEdgeRepository edges;
    private final BookService bookService;

    public RecommendationService(BookRepository books, KnowledgeEdgeRepository edges, BookService bookService) {
        this.books = books;
        this.edges = edges;
        this.bookService = bookService;
    }

    public List<RecommendationResponse> recommend(Integer catalogId, int limit) {
        if (limit < 1 || limit > 20) throw new IllegalArgumentException("Limit must be between 1 and 20");
        BookEntity source = bookService.getByCatalogId(catalogId);
        Set<Long> relatedIds = new HashSet<>();
        for (KnowledgeEdgeEntity edge : edges.findBySourceBookIdOrDestinationBookId(source.getId(), source.getId())) {
            relatedIds.add(edge.getSourceBook().getId().equals(source.getId())
                    ? edge.getDestinationBook().getId() : edge.getSourceBook().getId());
        }

        List<RecommendationResponse> result = new ArrayList<>();
        for (BookEntity candidate : books.findAll()) {
            if (candidate.getId().equals(source.getId())) continue;
            int score = 0;
            List<String> reasons = new ArrayList<>();
            if (candidate.getCategory().equalsIgnoreCase(source.getCategory())) {
                score += 6;
                reasons.add("same category");
            }
            if (candidate.getAuthor().equalsIgnoreCase(source.getAuthor())) {
                score += 3;
                reasons.add("same author");
            }
            if (relatedIds.contains(candidate.getId())) {
                score += 5;
                reasons.add("knowledge-graph connection");
            }
            if (candidate.getBorrowCount() > 0) {
                score += Math.min(5, candidate.getBorrowCount());
                reasons.add("popular with readers");
            }
            if (score > 0) {
                result.add(new RecommendationResponse(BookResponse.from(candidate), score, String.join(", ", reasons)));
            }
        }
        return result.stream()
                .sorted(Comparator.comparingInt(RecommendationResponse::score).reversed()
                        .thenComparing(item -> item.book().title()))
                .limit(limit)
                .toList();
    }
}
