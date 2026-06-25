package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class LibraryKruskalsMST {
    private LibraryKruskalsMST() { }

    public static MSTResult compute(int vertices, List<Edge> edges) {
        GraphUtils.validateVertices(vertices);
        for (Edge edge : edges) GraphUtils.validateEdge(vertices, edge);
        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.naturalOrder());
        UnionFind unionFind = new UnionFind(vertices);
        List<Edge> selected = new ArrayList<>();
        long weight = 0;
        for (Edge edge : sorted) {
            if (unionFind.union(edge.getSource(), edge.getDestination())) {
                selected.add(edge);
                weight += edge.getWeight();
                if (selected.size() == vertices - 1) break;
            }
        }
        return new MSTResult(selected, weight, selected.size() == vertices - 1);
    }
}
