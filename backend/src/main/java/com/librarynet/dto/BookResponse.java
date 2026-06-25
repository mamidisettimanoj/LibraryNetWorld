package com.librarynet.dto;

import com.librarynet.domain.BookEntity;

public record BookResponse(
        Long id,
        Integer catalogId,
        String title,
        String author,
        String category,
        Integer publicationYear,
        String isbn,
        boolean available,
        int borrowCount
) {
    public static BookResponse from(BookEntity book) {
        return new BookResponse(book.getId(), book.getCatalogId(), book.getTitle(), book.getAuthor(),
                book.getCategory(), book.getPublicationYear(), book.getIsbn(), book.isAvailable(),
                book.getBorrowCount());
    }
}
