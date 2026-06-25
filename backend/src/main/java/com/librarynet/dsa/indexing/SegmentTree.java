package com.librarynet.dsa.indexing;

import java.util.Arrays;

/** Segment Tree supporting range sum/min/max, point assignment, and lazy range addition. */
public class SegmentTree {
    private final int n;
    private final long[] sum;
    private final long[] min;
    private final long[] max;
    private final long[] lazy;

    public SegmentTree(int[] data) {
        if (data == null || data.length == 0) throw new IllegalArgumentException("Segment Tree requires non-empty data");
        n = data.length;
        int capacity = 4 * n;
        sum = new long[capacity];
        min = new long[capacity];
        max = new long[capacity];
        lazy = new long[capacity];
        build(1, 0, n - 1, data);
    }

    public int size() { return n; }

    private void build(int node, int left, int right, int[] data) {
        if (left == right) {
            sum[node] = min[node] = max[node] = data[left];
            return;
        }
        int mid = (left + right) >>> 1;
        build(node * 2, left, mid, data);
        build(node * 2 + 1, mid + 1, right, data);
        pull(node);
    }

    public long querySum(int left, int right) { normalizeAndCheck(left, right); return querySum(1, 0, n - 1, Math.min(left, right), Math.max(left, right)); }
    public long queryMin(int left, int right) { normalizeAndCheck(left, right); return queryMin(1, 0, n - 1, Math.min(left, right), Math.max(left, right)); }
    public long queryMax(int left, int right) { normalizeAndCheck(left, right); return queryMax(1, 0, n - 1, Math.min(left, right), Math.max(left, right)); }

    public void rangeAdd(int left, int right, long delta) {
        normalizeAndCheck(left, right);
        rangeAdd(1, 0, n - 1, Math.min(left, right), Math.max(left, right), delta);
    }

    public void pointSet(int index, long value) {
        checkIndex(index);
        long current = querySum(index, index);
        rangeAdd(index, index, value - current);
    }

    private long querySum(int node, int left, int right, int ql, int qr) {
        if (ql <= left && right <= qr) return sum[node];
        push(node, left, right);
        int mid = (left + right) >>> 1;
        long result = 0;
        if (ql <= mid) result += querySum(node * 2, left, mid, ql, qr);
        if (qr > mid) result += querySum(node * 2 + 1, mid + 1, right, ql, qr);
        return result;
    }

    private long queryMin(int node, int left, int right, int ql, int qr) {
        if (ql <= left && right <= qr) return min[node];
        push(node, left, right);
        int mid = (left + right) >>> 1;
        long result = Long.MAX_VALUE;
        if (ql <= mid) result = Math.min(result, queryMin(node * 2, left, mid, ql, qr));
        if (qr > mid) result = Math.min(result, queryMin(node * 2 + 1, mid + 1, right, ql, qr));
        return result;
    }

    private long queryMax(int node, int left, int right, int ql, int qr) {
        if (ql <= left && right <= qr) return max[node];
        push(node, left, right);
        int mid = (left + right) >>> 1;
        long result = Long.MIN_VALUE;
        if (ql <= mid) result = Math.max(result, queryMax(node * 2, left, mid, ql, qr));
        if (qr > mid) result = Math.max(result, queryMax(node * 2 + 1, mid + 1, right, ql, qr));
        return result;
    }

    private void rangeAdd(int node, int left, int right, int ql, int qr, long delta) {
        if (ql <= left && right <= qr) {
            apply(node, left, right, delta);
            return;
        }
        push(node, left, right);
        int mid = (left + right) >>> 1;
        if (ql <= mid) rangeAdd(node * 2, left, mid, ql, qr, delta);
        if (qr > mid) rangeAdd(node * 2 + 1, mid + 1, right, ql, qr, delta);
        pull(node);
    }

    private void apply(int node, int left, int right, long delta) {
        sum[node] += delta * (right - left + 1L);
        min[node] += delta;
        max[node] += delta;
        lazy[node] += delta;
    }

    private void push(int node, int left, int right) {
        if (lazy[node] == 0 || left == right) return;
        int mid = (left + right) >>> 1;
        apply(node * 2, left, mid, lazy[node]);
        apply(node * 2 + 1, mid + 1, right, lazy[node]);
        lazy[node] = 0;
    }

    private void pull(int node) {
        sum[node] = sum[node * 2] + sum[node * 2 + 1];
        min[node] = Math.min(min[node * 2], min[node * 2 + 1]);
        max[node] = Math.max(max[node * 2], max[node * 2 + 1]);
    }

    public long[] toArray() {
        long[] result = new long[n];
        for (int i = 0; i < n; i++) result[i] = querySum(i, i);
        return result;
    }

    private void normalizeAndCheck(int left, int right) { checkIndex(left); checkIndex(right); }
    private void checkIndex(int index) {
        if (index < 0 || index >= n) throw new IndexOutOfBoundsException("Index " + index + " outside 0.." + (n - 1));
    }

    @Override public String toString() { return Arrays.toString(toArray()); }
}
