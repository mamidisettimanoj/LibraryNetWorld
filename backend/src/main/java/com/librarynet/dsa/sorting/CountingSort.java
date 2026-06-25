package com.librarynet.dsa.sorting;

public final class CountingSort {
    private static final int MAX_RANGE = 10_000_000;

    private CountingSort() { }

    public static void sort(int[] array) {
        if (array == null) throw new IllegalArgumentException("Array cannot be null");
        if (array.length < 2) return;
        int min = array[0], max = array[0];
        for (int value : array) { min = Math.min(min, value); max = Math.max(max, value); }
        long rangeLong = (long) max - min + 1;
        if (rangeLong > MAX_RANGE) {
            throw new IllegalArgumentException("Counting Sort range is too large (" + rangeLong + "). Use Merge/Quick/Heap Sort.");
        }
        int[] counts = new int[(int) rangeLong];
        for (int value : array) counts[value - min]++;
        int index = 0;
        for (int i = 0; i < counts.length; i++) {
            while (counts[i]-- > 0) array[index++] = i + min;
        }
    }
}
