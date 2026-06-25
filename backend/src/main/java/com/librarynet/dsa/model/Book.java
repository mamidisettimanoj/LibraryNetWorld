package com.librarynet.dsa.model;

import java.util.Objects;

public final class Book {
    private final int id;
    private final String title;
    private final String author;
    private final String category;

    public Book(int id, String title) {
        this(id, title, "Unknown", "General");
    }

    public Book(int id, String title, String author, String category) {
        if (id < 0) throw new IllegalArgumentException("Book ID cannot be negative");
        this.id = id;
        this.title = requireText(title, "title");
        this.author = requireText(author, "author");
        this.category = requireText(category, "category");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Book " + field + " cannot be empty");
        }
        return value.trim();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return String.format("Book{id=%d, title='%s', author='%s', category='%s'}", id, title, author, category);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Book)) return false;
        Book book = (Book) other;
        return id == book.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
