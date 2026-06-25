package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayList;
import java.util.List;

public final class LibraryDFS {
    private LibraryDFS() { }

    public static List<Integer> traversalRecursive(int vertices, List<Edge> edges, int start, boolean directed) {
        GraphUtils.validateStart(vertices, start);
        List<List<GraphUtils.Neighbor>> graph = GraphUtils.adjacency(vertices, edges, directed);
        boolean[] visited = new boolean[vertices];
        List<Integer> order = new ArrayList<>();
        dfs(start, graph, visited, order);
        return order;
    }

    private static void dfs(int vertex, List<List<GraphUtils.Neighbor>> graph, boolean[] visited, List<Integer> order) {
        visited[vertex] = true;
        order.add(vertex);
        for (GraphUtils.Neighbor neighbor : graph.get(vertex)) {
            if (!visited[neighbor.vertex]) dfs(neighbor.vertex, graph, visited, order);
        }
    }

    public static boolean hasDirectedCycle(int vertices, List<Edge> edges) {
        List<List<GraphUtils.Neighbor>> graph = GraphUtils.adjacency(vertices, edges, true);
        int[] state = new int[vertices];
        for (int vertex = 0; vertex < vertices; vertex++) {
            if (state[vertex] == 0 && cycleDfs(vertex, graph, state)) return true;
        }
        return false;
    }

    private static boolean cycleDfs(int vertex, List<List<GraphUtils.Neighbor>> graph, int[] state) {
        state[vertex] = 1;
        for (GraphUtils.Neighbor neighbor : graph.get(vertex)) {
            if (state[neighbor.vertex] == 1) return true;
            if (state[neighbor.vertex] == 0 && cycleDfs(neighbor.vertex, graph, state)) return true;
        }
        state[vertex] = 2;
        return false;
    }
}
