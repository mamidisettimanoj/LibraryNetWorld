package com.librarynet.dsa.optimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LIS {
    public static final class Result {
        private final int length;
        private final List<Integer> sequence;
        Result(int length, List<Integer> sequence) { this.length = length; this.sequence = List.copyOf(sequence); }
        public int getLength() { return length; }
        public List<Integer> getSequence() { return sequence; }
    }

    private LIS() { }

    public static Result longestIncreasingSubsequence(int[] values) {
        if (values == null) throw new IllegalArgumentException("Array cannot be null");
        if (values.length == 0) return new Result(0, Collections.emptyList());
        int n = values.length;
        int[] tails = new int[n];
        int[] previous = new int[n];
        int[] positions = new int[n + 1];
        java.util.Arrays.fill(previous, -1);
        int length = 0;
        for (int i = 0; i < n; i++) {
            int low = 1, high = length;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                if (values[positions[mid]] < values[i]) low = mid + 1;
                else high = mid - 1;
            }
            int newLength = low;
            previous[i] = newLength > 1 ? positions[newLength - 1] : -1;
            positions[newLength] = i;
            tails[newLength - 1] = values[i];
            if (newLength > length) length = newLength;
        }
        List<Integer> sequence = new ArrayList<>();
        for (int index = positions[length]; index >= 0; index = previous[index]) sequence.add(values[index]);
        Collections.reverse(sequence);
        return new Result(length, sequence);
    }
}
