package com.librarynet.dsa.model;

import java.util.Objects;

public final class Edge implements Comparable<Edge> {
    private final int source;
    private final int destination;
    private final int weight;

    public Edge(int source, int destination, int weight) {
        if (source < 0 || destination < 0) throw new IllegalArgumentException("Vertex IDs cannot be negative");
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public int getSource() { return source; }
    public int getDestination() { return destination; }
    public int getWeight() { return weight; }

    // Backward-compatible public accessors through methods should be preferred.
    @Override
    public int compareTo(Edge other) { return Integer.compare(this.weight, other.weight); }

    @Override
    public String toString() { return source + " -> " + destination + " (w=" + weight + ")"; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Edge)) return false;
        Edge edge = (Edge) other;
        return source == edge.source && destination == edge.destination && weight == edge.weight;
    }

    @Override
    public int hashCode() { return Objects.hash(source, destination, weight); }
}
