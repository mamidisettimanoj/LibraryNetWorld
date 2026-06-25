package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public final class LibraryBFS {
    private LibraryBFS() { }

    public static List<Integer> traversal(int vertices, List<Edge> edges, int start, boolean directed) {
        GraphUtils.validateStart(vertices, start);
        List<List<GraphUtils.Neighbor>> graph = GraphUtils.adjacency(vertices, edges, directed);
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new ArrayDeque<>();
        visited[start] = true;
        queue.add(start);
        while (!queue.isEmpty()) {
            int vertex = queue.remove();
            order.add(vertex);
            for (GraphUtils.Neighbor neighbor : graph.get(vertex)) {
                if (!visited[neighbor.vertex]) {
                    visited[neighbor.vertex] = true;
                    queue.add(neighbor.vertex);
                }
            }
        }
        return order;
    }

    public static List<Integer> shortestPathUnweighted(int vertices, List<Edge> edges, int source, int destination, boolean directed) {
        GraphUtils.validateStart(vertices, source);
        GraphUtils.validateStart(vertices, destination);
        List<List<GraphUtils.Neighbor>> graph = GraphUtils.adjacency(vertices, edges, directed);
        int[] parent = new int[vertices];
        java.util.Arrays.fill(parent, -1);
        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        visited[source] = true;
        while (!queue.isEmpty()) {
            int current = queue.remove();
            if (current == destination) break;
            for (GraphUtils.Neighbor neighbor : graph.get(current)) {
                if (!visited[neighbor.vertex]) {
                    visited[neighbor.vertex] = true;
                    parent[neighbor.vertex] = current;
                    queue.add(neighbor.vertex);
                }
            }
        }
        if (!visited[destination]) return Collections.emptyList();
        List<Integer> path = new ArrayList<>();
        for (int at = destination; at != -1; at = parent[at]) path.add(at);
        Collections.reverse(path);
        return path;
    }

    public static List<List<Integer>> connectedComponents(int vertices, List<Edge> edges) {
        List<List<GraphUtils.Neighbor>> graph = GraphUtils.adjacency(vertices, edges, false);
        boolean[] visited = new boolean[vertices];
        List<List<Integer>> components = new ArrayList<>();
        for (int start = 0; start < vertices; start++) {
            if (visited[start]) continue;
            List<Integer> component = new ArrayList<>();
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            visited[start] = true;
            while (!queue.isEmpty()) {
                int current = queue.remove();
                component.add(current);
                for (GraphUtils.Neighbor neighbor : graph.get(current)) {
                    if (!visited[neighbor.vertex]) {
                        visited[neighbor.vertex] = true;
                        queue.add(neighbor.vertex);
                    }
                }
            }
            components.add(component);
        }
        return components;
    }
}
