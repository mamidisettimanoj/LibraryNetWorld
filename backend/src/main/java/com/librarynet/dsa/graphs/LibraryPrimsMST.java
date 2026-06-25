package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class LibraryPrimsMST {
    private static final class Candidate {
        final int from;
        final int to;
        final int weight;
        Candidate(int from, int to, int weight) { this.from = from; this.to = to; this.weight = weight; }
    }

    private LibraryPrimsMST() { }

    public static MSTResult compute(int vertices, List<Edge> edges, int start) {
        GraphUtils.validateStart(vertices, start);
        List<List<GraphUtils.Neighbor>> graph = GraphUtils.adjacency(vertices, edges, false);
        boolean[] visited = new boolean[vertices];
        PriorityQueue<Candidate> queue = new PriorityQueue<>(Comparator.comparingInt(c -> c.weight));
        List<Edge> selected = new ArrayList<>();
        long totalWeight = 0;
        visited[start] = true;
        for (GraphUtils.Neighbor neighbor : graph.get(start)) queue.add(new Candidate(start, neighbor.vertex, neighbor.weight));
        while (!queue.isEmpty() && selected.size() < vertices - 1) {
            Candidate candidate = queue.remove();
            if (visited[candidate.to]) continue;
            visited[candidate.to] = true;
            selected.add(new Edge(candidate.from, candidate.to, candidate.weight));
            totalWeight += candidate.weight;
            for (GraphUtils.Neighbor neighbor : graph.get(candidate.to)) {
                if (!visited[neighbor.vertex]) queue.add(new Candidate(candidate.to, neighbor.vertex, neighbor.weight));
            }
        }
        return new MSTResult(selected, totalWeight, selected.size() == vertices - 1);
    }
}
