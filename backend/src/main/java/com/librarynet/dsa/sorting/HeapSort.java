package com.librarynet.dsa.sorting;

public final class HeapSort {
    private HeapSort() { }

    public static void sort(int[] array) {
        if (array == null) throw new IllegalArgumentException("Array cannot be null");
        for (int i = array.length / 2 - 1; i >= 0; i--) heapify(array, array.length, i);
        for (int end = array.length - 1; end > 0; end--) {
            int temp = array[0]; array[0] = array[end]; array[end] = temp;
            heapify(array, end, 0);
        }
    }

    private static void heapify(int[] array, int size, int root) {
        while (true) {
            int largest = root;
            int left = 2 * root + 1;
            int right = left + 1;
            if (left < size && array[left] > array[largest]) largest = left;
            if (right < size && array[right] > array[largest]) largest = right;
            if (largest == root) return;
            int temp = array[root]; array[root] = array[largest]; array[largest] = temp;
            root = largest;
        }
    }
}
