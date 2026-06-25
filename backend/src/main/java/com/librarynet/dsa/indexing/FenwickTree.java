package com.librarynet.dsa.indexing;

import java.util.Arrays;

public class FenwickTree {
    private final long[] tree;
    private final long[] values;

    public FenwickTree(int size) {
        if (size <= 0) throw new IllegalArgumentException("Size must be positive");
        this.tree = new long[size + 1];
        this.values = new long[size];
    }

    public FenwickTree(int[] data) {
        if (data == null || data.length == 0) throw new IllegalArgumentException("Data must be non-empty");
        this.tree = new long[data.length + 1];
        this.values = new long[data.length];
        for (int index = 0; index < data.length; index++) {
            values[index] = data[index];
            for (int i = index + 1; i < tree.length; i += i & -i) tree[i] += data[index];
        }
    }

    public int size() { return values.length; }

    /** Add delta at zero-based index. */
    public void add(int index, long delta) {
        checkIndex(index);
        values[index] += delta;
        for (int i = index + 1; i < tree.length; i += i & -i) tree[i] += delta;
    }

    public void set(int index, long newValue) {
        checkIndex(index);
        add(index, newValue - values[index]);
    }

    public long get(int index) { checkIndex(index); return values[index]; }

    /** Prefix sum from index 0 through index inclusive. */
    public long prefixSum(int index) {
        if (index < 0) return 0;
        if (index >= values.length) throw new IndexOutOfBoundsException("Index " + index + " outside 0.." + (values.length - 1));
        long sum = 0;
        for (int i = index + 1; i > 0; i -= i & -i) sum += tree[i];
        return sum;
    }

    public long rangeSum(int left, int right) {
        if (left > right) { int temp = left; left = right; right = temp; }
        checkIndex(left); checkIndex(right);
        return prefixSum(right) - prefixSum(left - 1);
    }

    public long[] toArray() { return Arrays.copyOf(values, values.length); }

    private void checkIndex(int index) {
        if (index < 0 || index >= values.length) throw new IndexOutOfBoundsException("Index " + index + " outside 0.." + (values.length - 1));
    }

    public static long countInversions(int[] data) {
        if (data == null) throw new IllegalArgumentException("Data cannot be null");
        int[] sorted = Arrays.stream(data).distinct().sorted().toArray();
        FenwickTree frequencies = new FenwickTree(Math.max(1, sorted.length));
        long inversions = 0;
        for (int i = data.length - 1; i >= 0; i--) {
            int rank = Arrays.binarySearch(sorted, data[i]);
            inversions += frequencies.prefixSum(rank - 1);
            frequencies.add(rank, 1);
        }
        return inversions;
    }
}
