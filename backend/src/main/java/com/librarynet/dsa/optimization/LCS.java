package com.librarynet.dsa.optimization;

public final class LCS {
    public static final class Result {
        private final int length;
        private final String sequence;
        Result(int length, String sequence) { this.length = length; this.sequence = sequence; }
        public int getLength() { return length; }
        public String getSequence() { return sequence; }
    }

    private LCS() { }

    public static Result tabulation(String first, String second) {
        validate(first, second);
        int[][] dp = new int[first.length() + 1][second.length() + 1];
        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) dp[i][j] = 1 + dp[i - 1][j - 1];
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        StringBuilder sequence = new StringBuilder();
        int i = first.length(), j = second.length();
        while (i > 0 && j > 0) {
            if (first.charAt(i - 1) == second.charAt(j - 1)) {
                sequence.append(first.charAt(i - 1)); i--; j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) i--;
            else j--;
        }
        return new Result(dp[first.length()][second.length()], sequence.reverse().toString());
    }

    public static int memoization(String first, String second) {
        validate(first, second);
        Integer[][] memo = new Integer[first.length() + 1][second.length() + 1];
        return memo(first, second, first.length(), second.length(), memo);
    }

    private static int memo(String a, String b, int i, int j, Integer[][] memo) {
        if (i == 0 || j == 0) return 0;
        if (memo[i][j] != null) return memo[i][j];
        if (a.charAt(i - 1) == b.charAt(j - 1)) return memo[i][j] = 1 + memo(a, b, i - 1, j - 1, memo);
        return memo[i][j] = Math.max(memo(a, b, i - 1, j, memo), memo(a, b, i, j - 1, memo));
    }

    private static void validate(String first, String second) {
        if (first == null || second == null) throw new IllegalArgumentException("Strings cannot be null");
    }
}
