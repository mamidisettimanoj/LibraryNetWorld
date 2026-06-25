package com.librarynet.service;

import com.librarynet.domain.BookEntity;
import com.librarynet.domain.LoanStatus;
import com.librarynet.dto.BookRequest;
import com.librarynet.dto.BookResponse;
import com.librarynet.exception.ConflictException;
import com.librarynet.exception.ResourceNotFoundException;
import com.librarynet.repository.BookRepository;
import com.librarynet.repository.BorrowTransactionRepository;
import com.librarynet.repository.KnowledgeEdgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    private final BookRepository books;
    private final BorrowTransactionRepository loans;
    private final KnowledgeEdgeRepository edges;

    public BookService(BookRepository books, BorrowTransactionRepository loans, KnowledgeEdgeRepository edges) {
        this.books = books;
        this.loans = loans;
        this.edges = edges;
    }

    public List<BookResponse> list(String query) {
        List<BookEntity> result = query == null || query.isBlank()
                ? books.findAllByOrderByCatalogIdAsc()
                : books.search(query.trim());
        return result.stream().map(BookResponse::from).toList();
    }

    public BookResponse get(Long id) {
        return BookResponse.from(getEntity(id));
    }

    public BookEntity getEntity(Long id) {
        return books.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    public BookEntity getByCatalogId(Integer catalogId) {
        return books.findByCatalogId(catalogId)
                .orElseThrow(() -> new ResourceNotFoundException("Book catalog ID not found: " + catalogId));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        if (books.existsByCatalogId(request.catalogId())) {
            throw new ConflictException("Catalog ID already exists: " + request.catalogId());
        }
        if (request.isbn() != null && !request.isbn().isBlank() && books.existsByIsbn(request.isbn().trim())) {
            throw new ConflictException("ISBN already exists: " + request.isbn());
        }
        BookEntity book = new BookEntity(request.catalogId(), request.title().trim(), request.author().trim(),
                request.category().trim(), request.publicationYear(), normalize(request.isbn()));
        return BookResponse.from(books.save(book));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        BookEntity book = getEntity(id);
        books.findByCatalogId(request.catalogId()).filter(other -> !other.getId().equals(id)).ifPresent(other -> {
            throw new ConflictException("Catalog ID already exists: " + request.catalogId());
        });
        if (request.isbn() != null && !request.isbn().isBlank()) {
            books.findAll().stream()
                    .filter(other -> request.isbn().trim().equalsIgnoreCase(other.getIsbn()))
                    .filter(other -> !other.getId().equals(id))
                    .findFirst()
                    .ifPresent(other -> { throw new ConflictException("ISBN already exists: " + request.isbn()); });
        }
        book.setCatalogId(request.catalogId());
        book.setTitle(request.title().trim());
        book.setAuthor(request.author().trim());
        book.setCategory(request.category().trim());
        book.setPublicationYear(request.publicationYear());
        book.setIsbn(normalize(request.isbn()));
        return BookResponse.from(books.save(book));
    }

    @Transactional
    public void delete(Long id) {
        BookEntity book = getEntity(id);
        if (loans.existsByBookId(id)) {
            throw new ConflictException("Books with borrowing history cannot be deleted");
        }
        edges.deleteAll(edges.findBySourceBookIdOrDestinationBookId(id, id));
        books.delete(book);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
