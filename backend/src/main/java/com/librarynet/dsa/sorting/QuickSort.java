package com.librarynet.dsa.sorting;

import java.util.concurrent.ThreadLocalRandom;

public final class QuickSort {
    public enum PivotStrategy { FIRST, LAST, RANDOM, MEDIAN_OF_THREE }

    private QuickSort() { }

    public static void sort(int[] array) { sort(array, PivotStrategy.MEDIAN_OF_THREE); }

    public static void sort(int[] array, PivotStrategy strategy) {
        if (array == null) throw new IllegalArgumentException("Array cannot be null");
        quickSort(array, 0, array.length - 1, strategy);
    }

    private static void quickSort(int[] array, int low, int high, PivotStrategy strategy) {
        while (low < high) {
            if (high - low < 16) {
                insertionSort(array, low, high);
                return;
            }
            int pivot = partition(array, low, high, strategy);
            if (pivot - low < high - pivot) {
                quickSort(array, low, pivot - 1, strategy);
                low = pivot + 1;
            } else {
                quickSort(array, pivot + 1, high, strategy);
                high = pivot - 1;
            }
        }
    }

    private static int partition(int[] array, int low, int high, PivotStrategy strategy) {
        int pivotIndex = choosePivot(array, low, high, strategy);
        swap(array, pivotIndex, high);
        int pivotValue = array[high];
        int store = low;
        for (int i = low; i < high; i++) {
            if (array[i] <= pivotValue) swap(array, i, store++);
        }
        swap(array, store, high);
        return store;
    }

    private static int choosePivot(int[] array, int low, int high, PivotStrategy strategy) {
        switch (strategy) {
            case FIRST: return low;
            case LAST: return high;
            case RANDOM: return ThreadLocalRandom.current().nextInt(low, high + 1);
            case MEDIAN_OF_THREE:
            default:
                int mid = (low + high) >>> 1;
                int a = array[low], b = array[mid], c = array[high];
                if ((a <= b && b <= c) || (c <= b && b <= a)) return mid;
                if ((b <= a && a <= c) || (c <= a && a <= b)) return low;
                return high;
        }
    }

    private static void insertionSort(int[] array, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int value = array[i];
            int j = i - 1;
            while (j >= low && array[j] > value) array[j + 1] = array[j--];
            array[j + 1] = value;
        }
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i]; array[i] = array[j]; array[j] = temp;
    }
}
