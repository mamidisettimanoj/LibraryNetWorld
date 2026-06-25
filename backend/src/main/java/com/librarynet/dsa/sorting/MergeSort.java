package com.librarynet.dsa.sorting;

public final class MergeSort {
    private MergeSort() { }

    public static void sort(int[] array) {
        if (array == null) throw new IllegalArgumentException("Array cannot be null");
        if (array.length < 2) return;
        int[] temp = new int[array.length];
        sort(array, temp, 0, array.length - 1);
    }

    private static void sort(int[] array, int[] temp, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) >>> 1;
        sort(array, temp, left, mid);
        sort(array, temp, mid + 1, right);
        if (array[mid] <= array[mid + 1]) return;
        merge(array, temp, left, mid, right);
    }

    private static void merge(int[] array, int[] temp, int left, int mid, int right) {
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) temp[k++] = array[i] <= array[j] ? array[i++] : array[j++];
        while (i <= mid) temp[k++] = array[i++];
        while (j <= right) temp[k++] = array[j++];
        System.arraycopy(temp, left, array, left, right - left + 1);
    }
}
