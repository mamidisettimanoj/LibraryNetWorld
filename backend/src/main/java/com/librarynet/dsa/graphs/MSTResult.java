package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.Collections;
import java.util.List;

public final class MSTResult {
    private final List<Edge> edges;
    private final long totalWeight;
    private final boolean spanning;

    public MSTResult(List<Edge> edges, long totalWeight, boolean spanning) {
        this.edges = List.copyOf(edges);
        this.totalWeight = totalWeight;
        this.spanning = spanning;
    }

    public List<Edge> getEdges() { return Collections.unmodifiableList(edges); }
    public long getTotalWeight() { return totalWeight; }
    public boolean isSpanning() { return spanning; }
}
