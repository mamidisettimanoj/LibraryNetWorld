package com.librarynet.dsa.trees;

import com.librarynet.dsa.model.Book;

public final class TreeSearchResult {
    private final Book book;
    private final int comparisons;

    public TreeSearchResult(Book book, int comparisons) {
        this.book = book;
        this.comparisons = comparisons;
    }

    public Book getBook() { return book; }
    public int getComparisons() { return comparisons; }
    public boolean isFound() { return book != null; }
}
