package com.librarynet.dsa.benchmark;

import com.librarynet.dsa.model.Book;
import com.librarynet.dsa.sorting.CountingSort;
import com.librarynet.dsa.sorting.HeapSort;
import com.librarynet.dsa.sorting.MergeSort;
import com.librarynet.dsa.sorting.QuickSort;
import com.librarynet.dsa.sorting.RadixSort;
import com.librarynet.dsa.trees.AVLTree;
import com.librarynet.dsa.trees.BSTLibrary;
import com.librarynet.dsa.trees.TreeSearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class BenchmarkEngine {
    @FunctionalInterface
    private interface SortFunction { void sort(int[] data); }

    public static final class TreeBenchmarkResult {
        public final int records;
        public final int bstHeight;
        public final int avlHeight;
        public final int bstComparisons;
        public final int avlComparisons;
        public final long bstMedianNanos;
        public final long avlMedianNanos;

        TreeBenchmarkResult(int records, int bstHeight, int avlHeight, int bstComparisons,
                            int avlComparisons, long bstMedianNanos, long avlMedianNanos) {
            this.records = records;
            this.bstHeight = bstHeight;
            this.avlHeight = avlHeight;
            this.bstComparisons = bstComparisons;
            this.avlComparisons = avlComparisons;
            this.bstMedianNanos = bstMedianNanos;
            this.avlMedianNanos = avlMedianNanos;
        }
    }

    public static final class SortBenchmarkResult {
        public final String algorithm;
        public final long medianNanos;
        public final boolean correct;
        SortBenchmarkResult(String algorithm, long medianNanos, boolean correct) {
            this.algorithm = algorithm;
            this.medianNanos = medianNanos;
            this.correct = correct;
        }
    }

    private BenchmarkEngine() { }

    public static TreeBenchmarkResult benchmarkBSTvsAVL(int records) {
        if (records < 2) throw new IllegalArgumentException("Use at least two records");
        BSTLibrary bst = new BSTLibrary();
        AVLTree avl = new AVLTree();
        for (int id = 1; id <= records; id++) {
            Book book = new Book(id, "Book " + id);
            bst.addResource(book);
            avl.addResource(book);
        }
        int target = records;
        TreeSearchResult bstStats = bst.searchWithStats(target);
        TreeSearchResult avlStats = avl.searchWithStats(target);
        long bstTime = medianSearchTime(() -> bst.searchResource(target));
        long avlTime = medianSearchTime(() -> avl.searchResource(target));
        return new TreeBenchmarkResult(records, bst.height(), avl.height(), bstStats.getComparisons(),
                avlStats.getComparisons(), bstTime, avlTime);
    }

    private static long medianSearchTime(Runnable search) {
        for (int i = 0; i < 1000; i++) search.run();
        long[] samples = new long[31];
        for (int i = 0; i < samples.length; i++) {
            long start = System.nanoTime();
            for (int repeat = 0; repeat < 1000; repeat++) search.run();
            samples[i] = System.nanoTime() - start;
        }
        Arrays.sort(samples);
        return samples[samples.length / 2] / 1000;
    }

    public static List<SortBenchmarkResult> benchmarkSorting(int[] source, int repetitions) {
        if (source == null) throw new IllegalArgumentException("Source array cannot be null");
        if (repetitions < 3) repetitions = 3;
        int[] expected = Arrays.copyOf(source, source.length);
        Arrays.sort(expected);
        List<SortBenchmarkResult> results = new ArrayList<>();
        runSortBenchmark("Merge Sort", source, expected, repetitions, MergeSort::sort, results);
        runSortBenchmark("Quick Sort (Median-3)", source, expected, repetitions,
                data -> QuickSort.sort(data, QuickSort.PivotStrategy.MEDIAN_OF_THREE), results);
        runSortBenchmark("Heap Sort", source, expected, repetitions, HeapSort::sort, results);
        runSortBenchmark("Counting Sort", source, expected, repetitions, CountingSort::sort, results);
        runSortBenchmark("Radix Sort", source, expected, repetitions, RadixSort::sort, results);
        results.sort(Comparator.comparingLong(result -> result.medianNanos));
        return results;
    }

    private static void runSortBenchmark(String name, int[] source, int[] expected, int repetitions,
                                         SortFunction function, List<SortBenchmarkResult> results) {
        for (int i = 0; i < 3; i++) function.sort(Arrays.copyOf(source, source.length));
        long[] timings = new long[repetitions];
        boolean correct = true;
        for (int i = 0; i < repetitions; i++) {
            int[] data = Arrays.copyOf(source, source.length);
            long start = System.nanoTime();
            function.sort(data);
            timings[i] = System.nanoTime() - start;
            correct &= Arrays.equals(data, expected);
        }
        Arrays.sort(timings);
        results.add(new SortBenchmarkResult(name, timings[timings.length / 2], correct));
    }

    public static int[] generateDataset(int size, String type, long seed) {
        if (size < 0) throw new IllegalArgumentException("Size cannot be negative");
        int[] data = new int[size];
        Random random = new Random(seed);
        switch (type.toLowerCase()) {
            case "sorted":
                for (int i = 0; i < size; i++) data[i] = i;
                break;
            case "reverse":
                for (int i = 0; i < size; i++) data[i] = size - i;
                break;
            case "duplicates":
                for (int i = 0; i < size; i++) data[i] = random.nextInt(20);
                break;
            case "random":
            default:
                for (int i = 0; i < size; i++) data[i] = random.nextInt(Math.max(100, size * 10)) - size * 5;
        }
        return data;
    }
}
