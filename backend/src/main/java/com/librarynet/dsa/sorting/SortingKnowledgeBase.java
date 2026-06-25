package com.librarynet.dsa.sorting;

import java.util.List;

public final class SortingKnowledgeBase {
    private SortingKnowledgeBase() { }

    public static List<String[]> complexityRows() {
        return List.of(
            new String[]{"Merge Sort", "O(n log n)", "O(n log n)", "O(n log n)", "O(n)", "Yes"},
            new String[]{"Quick Sort", "O(n log n)", "O(n log n)", "O(n^2)", "O(log n)*", "No"},
            new String[]{"Heap Sort", "O(n log n)", "O(n log n)", "O(n log n)", "O(1)", "No"},
            new String[]{"Counting Sort", "O(n+k)", "O(n+k)", "O(n+k)", "O(k)", "Can be"},
            new String[]{"Radix Sort", "O(d(n+b))", "O(d(n+b))", "O(d(n+b))", "O(n+b)", "Yes"}
        );
    }
}
