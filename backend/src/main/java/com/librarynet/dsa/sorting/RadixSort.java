package com.librarynet.dsa.sorting;

import java.util.Arrays;

/** Stable LSD radix sort supporting both negative and non-negative integers. */
public final class RadixSort {
    private RadixSort() { }

    public static void sort(int[] array) {
        if (array == null) throw new IllegalArgumentException("Array cannot be null");
        int negativeCount = 0;
        for (int value : array) if (value < 0) negativeCount++;
        int[] negatives = new int[negativeCount];
        int[] positives = new int[array.length - negativeCount];
        int ni = 0, pi = 0;
        for (int value : array) {
            if (value < 0) negatives[ni++] = -(value + 1);
            else positives[pi++] = value;
        }
        radixNonNegative(negatives);
        radixNonNegative(positives);
        int index = 0;
        for (int i = negatives.length - 1; i >= 0; i--) array[index++] = -(negatives[i] + 1);
        for (int value : positives) array[index++] = value;
    }

    private static void radixNonNegative(int[] array) {
        if (array.length < 2) return;
        int max = Arrays.stream(array).max().orElse(0);
        int[] output = new int[array.length];
        for (long exp = 1; max / exp > 0; exp *= 10) {
            int[] count = new int[10];
            for (int value : array) count[(int) ((value / exp) % 10)]++;
            for (int i = 1; i < 10; i++) count[i] += count[i - 1];
            for (int i = array.length - 1; i >= 0; i--) {
                int digit = (int) ((array[i] / exp) % 10);
                output[--count[digit]] = array[i];
            }
            System.arraycopy(output, 0, array, 0, array.length);
            if (exp > Integer.MAX_VALUE / 10L) break;
        }
    }
}
