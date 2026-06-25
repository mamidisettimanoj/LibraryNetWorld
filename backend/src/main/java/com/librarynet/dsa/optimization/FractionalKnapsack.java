package com.librarynet.dsa.optimization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FractionalKnapsack {
    public static final class Item {
        private final String name;
        private final double value;
        private final double weight;

        public Item(String name, double value, double weight) {
            if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Item name cannot be empty");
            if (value < 0 || weight <= 0) throw new IllegalArgumentException("Value must be non-negative and weight positive");
            this.name = name.trim();
            this.value = value;
            this.weight = weight;
        }

        public String getName() { return name; }
        public double getValue() { return value; }
        public double getWeight() { return weight; }
        public double ratio() { return value / weight; }
    }

    public static final class Selection {
        private final Item item;
        private final double fraction;
        Selection(Item item, double fraction) { this.item = item; this.fraction = fraction; }
        public Item getItem() { return item; }
        public double getFraction() { return fraction; }
        public double contributedValue() { return item.getValue() * fraction; }
    }

    public static final class Result {
        private final double totalValue;
        private final List<Selection> selections;
        Result(double totalValue, List<Selection> selections) {
            this.totalValue = totalValue;
            this.selections = List.copyOf(selections);
        }
        public double getTotalValue() { return totalValue; }
        public List<Selection> getSelections() { return selections; }
    }

    private FractionalKnapsack() { }

    public static Result maximize(double capacity, List<Item> items) {
        if (capacity < 0) throw new IllegalArgumentException("Capacity cannot be negative");
        List<Item> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparingDouble(Item::ratio).reversed());
        List<Selection> selections = new ArrayList<>();
        double remaining = capacity;
        double value = 0;
        for (Item item : sorted) {
            if (remaining <= 0) break;
            double fraction = Math.min(1.0, remaining / item.getWeight());
            selections.add(new Selection(item, fraction));
            value += item.getValue() * fraction;
            remaining -= item.getWeight() * fraction;
        }
        return new Result(value, selections);
    }
}
