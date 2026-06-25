package com.librarynet.dsa.optimization;

public final class EditDistance {
    private EditDistance() { }

    public static int tabulation(String first, String second) {
        validate(first, second);
        int[][] dp = new int[first.length() + 1][second.length() + 1];
        for (int i = 0; i <= first.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= second.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
                else dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
        return dp[first.length()][second.length()];
    }

    public static int memoization(String first, String second) {
        validate(first, second);
        Integer[][] memo = new Integer[first.length() + 1][second.length() + 1];
        return memo(first, second, first.length(), second.length(), memo);
    }

    private static int memo(String a, String b, int i, int j, Integer[][] memo) {
        if (i == 0) return j;
        if (j == 0) return i;
        if (memo[i][j] != null) return memo[i][j];
        if (a.charAt(i - 1) == b.charAt(j - 1)) return memo[i][j] = memo(a, b, i - 1, j - 1, memo);
        return memo[i][j] = 1 + Math.min(memo(a, b, i - 1, j - 1, memo), Math.min(memo(a, b, i - 1, j, memo), memo(a, b, i, j - 1, memo)));
    }

    private static void validate(String first, String second) {
        if (first == null || second == null) throw new IllegalArgumentException("Strings cannot be null");
    }
}
