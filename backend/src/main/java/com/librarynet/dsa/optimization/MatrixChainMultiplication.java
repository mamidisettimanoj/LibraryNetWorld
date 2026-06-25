package com.librarynet.dsa.optimization;

public final class MatrixChainMultiplication {
    public static final class Result {
        private final long minimumCost;
        private final String parenthesization;
        Result(long minimumCost, String parenthesization) {
            this.minimumCost = minimumCost;
            this.parenthesization = parenthesization;
        }
        public long getMinimumCost() { return minimumCost; }
        public String getParenthesization() { return parenthesization; }
    }

    private MatrixChainMultiplication() { }

    public static Result tabulation(int[] dimensions) {
        validate(dimensions);
        int n = dimensions.length - 1;
        long[][] cost = new long[n][n];
        int[][] split = new int[n][n];
        for (int length = 2; length <= n; length++) {
            for (int i = 0; i + length - 1 < n; i++) {
                int j = i + length - 1;
                cost[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long candidate = cost[i][k] + cost[k + 1][j] + (long) dimensions[i] * dimensions[k + 1] * dimensions[j + 1];
                    if (candidate < cost[i][j]) { cost[i][j] = candidate; split[i][j] = k; }
                }
            }
        }
        return new Result(cost[0][n - 1], build(split, 0, n - 1));
    }

    public static long memoization(int[] dimensions) {
        validate(dimensions);
        int n = dimensions.length - 1;
        Long[][] memo = new Long[n][n];
        return memo(dimensions, 0, n - 1, memo);
    }

    private static long memo(int[] d, int i, int j, Long[][] memo) {
        if (i == j) return 0;
        if (memo[i][j] != null) return memo[i][j];
        long best = Long.MAX_VALUE;
        for (int k = i; k < j; k++) {
            best = Math.min(best, memo(d, i, k, memo) + memo(d, k + 1, j, memo) + (long) d[i] * d[k + 1] * d[j + 1]);
        }
        return memo[i][j] = best;
    }

    private static String build(int[][] split, int i, int j) {
        if (i == j) return "A" + (i + 1);
        int k = split[i][j];
        return "(" + build(split, i, k) + " x " + build(split, k + 1, j) + ")";
    }

    private static void validate(int[] dimensions) {
        if (dimensions == null || dimensions.length < 2) throw new IllegalArgumentException("At least one matrix is required");
        for (int dimension : dimensions) if (dimension <= 0) throw new IllegalArgumentException("Dimensions must be positive");
    }
}
