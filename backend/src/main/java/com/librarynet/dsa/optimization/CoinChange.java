package com.librarynet.dsa.optimization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CoinChange {
    public static final class Result {
        private final int coinCount;
        private final List<Integer> coins;
        private final boolean possible;

        Result(int coinCount, List<Integer> coins, boolean possible) {
            this.coinCount = coinCount;
            this.coins = List.copyOf(coins);
            this.possible = possible;
        }
        public int getCoinCount() { return coinCount; }
        public List<Integer> getCoins() { return coins; }
        public boolean isPossible() { return possible; }
    }

    private CoinChange() { }

    public static Result greedy(int[] denominations, int amount) {
        validate(denominations, amount);
        int[] sorted = Arrays.copyOf(denominations, denominations.length);
        Arrays.sort(sorted);
        List<Integer> used = new ArrayList<>();
        int remaining = amount;
        for (int i = sorted.length - 1; i >= 0; i--) {
            while (remaining >= sorted[i]) {
                remaining -= sorted[i];
                used.add(sorted[i]);
            }
        }
        return new Result(used.size(), used, remaining == 0);
    }

    public static Result dynamicProgramming(int[] denominations, int amount) {
        validate(denominations, amount);
        int[] dp = new int[amount + 1];
        int[] chosen = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        Arrays.fill(chosen, -1);
        dp[0] = 0;
        for (int current = 1; current <= amount; current++) {
            for (int coin : denominations) {
                if (coin <= current && dp[current - coin] + 1 < dp[current]) {
                    dp[current] = dp[current - coin] + 1;
                    chosen[current] = coin;
                }
            }
        }
        if (dp[amount] > amount) return new Result(0, Collections.emptyList(), false);
        List<Integer> used = new ArrayList<>();
        for (int current = amount; current > 0; current -= chosen[current]) used.add(chosen[current]);
        return new Result(dp[amount], used, true);
    }

    public static int waysToMakeAmount(int[] denominations, int amount) {
        validate(denominations, amount);
        long[] ways = new long[amount + 1];
        ways[0] = 1;
        for (int coin : denominations) {
            for (int value = coin; value <= amount; value++) ways[value] += ways[value - coin];
        }
        if (ways[amount] > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) ways[amount];
    }

    private static void validate(int[] denominations, int amount) {
        if (denominations == null || denominations.length == 0) throw new IllegalArgumentException("Provide at least one denomination");
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
        for (int coin : denominations) if (coin <= 0) throw new IllegalArgumentException("Coin denominations must be positive");
    }
}
