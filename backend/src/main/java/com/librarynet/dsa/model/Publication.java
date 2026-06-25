package com.librarynet.dsa.model;

import java.util.Objects;

public final class Publication {
    private final int id;
    private final int year;
    private final String title;
    private final String author;

    public Publication(int id, int year, String title) {
        this(id, year, title, "Unknown");
    }

    public Publication(int id, int year, String title, String author) {
        if (id < 0) throw new IllegalArgumentException("Publication ID cannot be negative");
        if (year < 1000 || year > 2100) throw new IllegalArgumentException("Publication year must be between 1000 and 2100");
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Publication title cannot be empty");
        if (author == null || author.trim().isEmpty()) throw new IllegalArgumentException("Publication author cannot be empty");
        this.id = id;
        this.year = year;
        this.title = title.trim();
        this.author = author.trim();
    }

    public int getId() { return id; }
    public int getYear() { return year; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }

    @Override
    public String toString() {
        return String.format("Publication{id=%d, year=%d, title='%s', author='%s'}", id, year, title, author);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Publication)) return false;
        Publication p = (Publication) other;
        return id == p.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
