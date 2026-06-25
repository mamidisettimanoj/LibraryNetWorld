package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public final class TopologicalSort {
    public static final class Result {
        private final List<Integer> order;
        private final boolean dag;
        Result(List<Integer> order, boolean dag) { this.order = List.copyOf(order); this.dag = dag; }
        public List<Integer> getOrder() { return Collections.unmodifiableList(order); }
        public boolean isDag() { return dag; }
    }

    private TopologicalSort() { }

    public static Result kahn(int vertices, List<Edge> edges) {
        List<List<GraphUtils.Neighbor>> graph = GraphUtils.adjacency(vertices, edges, true);
        int[] indegree = new int[vertices];
        for (Edge edge : edges) indegree[edge.getDestination()]++;
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < vertices; i++) if (indegree[i] == 0) queue.add(i);
        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int current = queue.remove();
            order.add(current);
            for (GraphUtils.Neighbor neighbor : graph.get(current)) {
                if (--indegree[neighbor.vertex] == 0) queue.add(neighbor.vertex);
            }
        }
        return new Result(order, order.size() == vertices);
    }
}
