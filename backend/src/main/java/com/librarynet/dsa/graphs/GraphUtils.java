package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayList;
import java.util.List;

final class GraphUtils {
    static final class Neighbor {
        final int vertex;
        final int weight;
        Neighbor(int vertex, int weight) { this.vertex = vertex; this.weight = weight; }
    }

    private GraphUtils() { }

    static List<List<Neighbor>> adjacency(int vertices, List<Edge> edges, boolean directed) {
        validateVertices(vertices);
        List<List<Neighbor>> graph = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) graph.add(new ArrayList<>());
        for (Edge edge : edges) {
            validateEdge(vertices, edge);
            graph.get(edge.getSource()).add(new Neighbor(edge.getDestination(), edge.getWeight()));
            if (!directed) graph.get(edge.getDestination()).add(new Neighbor(edge.getSource(), edge.getWeight()));
        }
        return graph;
    }

    static void validateVertices(int vertices) {
        if (vertices <= 0) throw new IllegalArgumentException("Graph must contain at least one vertex");
    }

    static void validateEdge(int vertices, Edge edge) {
        if (edge.getSource() >= vertices || edge.getDestination() >= vertices) {
            throw new IllegalArgumentException("Edge contains vertex outside 0.." + (vertices - 1) + ": " + edge);
        }
    }

    static void validateStart(int vertices, int start) {
        if (start < 0 || start >= vertices) throw new IllegalArgumentException("Start vertex must be in 0.." + (vertices - 1));
    }
}
