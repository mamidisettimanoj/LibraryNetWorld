package com.librarynet.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "books", indexes = {
        @Index(name = "idx_books_title", columnList = "title"),
        @Index(name = "idx_books_category", columnList = "category")
})
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "catalog_id", nullable = false, unique = true)
    private Integer catalogId;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 120)
    private String author;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(unique = true, length = 32)
    private String isbn;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "borrow_count", nullable = false)
    private int borrowCount = 0;

    public BookEntity() { }

    public BookEntity(Integer catalogId, String title, String author, String category,
                      Integer publicationYear, String isbn) {
        this.catalogId = catalogId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getCatalogId() { return catalogId; }
    public void setCatalogId(Integer catalogId) { this.catalogId = catalogId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public int getBorrowCount() { return borrowCount; }
    public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }
}
