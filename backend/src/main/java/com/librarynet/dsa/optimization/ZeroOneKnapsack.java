package com.librarynet.dsa.optimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ZeroOneKnapsack {
    public static final class Result {
        private final int maximumValue;
        private final List<Integer> selectedIndices;
        Result(int maximumValue, List<Integer> selectedIndices) {
            this.maximumValue = maximumValue;
            this.selectedIndices = List.copyOf(selectedIndices);
        }
        public int getMaximumValue() { return maximumValue; }
        public List<Integer> getSelectedIndices() { return selectedIndices; }
    }

    private ZeroOneKnapsack() { }

    public static Result tabulation(int[] weights, int[] values, int capacity) {
        validate(weights, values, capacity);
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            for (int c = 0; c <= capacity; c++) {
                dp[i][c] = dp[i - 1][c];
                if (weights[i - 1] <= c) dp[i][c] = Math.max(dp[i][c], values[i - 1] + dp[i - 1][c - weights[i - 1]]);
            }
        }
        List<Integer> selected = new ArrayList<>();
        int c = capacity;
        for (int i = n; i > 0; i--) {
            if (dp[i][c] != dp[i - 1][c]) {
                selected.add(i - 1);
                c -= weights[i - 1];
            }
        }
        Collections.reverse(selected);
        return new Result(dp[n][capacity], selected);
    }

    public static int memoization(int[] weights, int[] values, int capacity) {
        validate(weights, values, capacity);
        Integer[][] memo = new Integer[weights.length + 1][capacity + 1];
        return solve(weights, values, weights.length, capacity, memo);
    }

    private static int solve(int[] weights, int[] values, int n, int capacity, Integer[][] memo) {
        if (n == 0 || capacity == 0) return 0;
        if (memo[n][capacity] != null) return memo[n][capacity];
        int result = solve(weights, values, n - 1, capacity, memo);
        if (weights[n - 1] <= capacity) {
            result = Math.max(result, values[n - 1] + solve(weights, values, n - 1, capacity - weights[n - 1], memo));
        }
        return memo[n][capacity] = result;
    }

    private static void validate(int[] weights, int[] values, int capacity) {
        if (weights == null || values == null || weights.length != values.length) throw new IllegalArgumentException("Weights and values must have equal length");
        if (capacity < 0) throw new IllegalArgumentException("Capacity cannot be negative");
        for (int weight : weights) if (weight <= 0) throw new IllegalArgumentException("Weights must be positive");
    }
}
