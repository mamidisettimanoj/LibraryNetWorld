package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StronglyConnectedComponents {
    private StronglyConnectedComponents() { }

    public static List<List<Integer>> kosaraju(int vertices, List<Edge> edges) {
        List<List<GraphUtils.Neighbor>> graph = GraphUtils.adjacency(vertices, edges, true);
        List<Edge> reversedEdges = new ArrayList<>();
        for (Edge edge : edges) reversedEdges.add(new Edge(edge.getDestination(), edge.getSource(), edge.getWeight()));
        List<List<GraphUtils.Neighbor>> reverse = GraphUtils.adjacency(vertices, reversedEdges, true);
        boolean[] visited = new boolean[vertices];
        List<Integer> finishOrder = new ArrayList<>();
        for (int i = 0; i < vertices; i++) if (!visited[i]) finishDfs(i, graph, visited, finishOrder);
        Collections.reverse(finishOrder);
        java.util.Arrays.fill(visited, false);
        List<List<Integer>> components = new ArrayList<>();
        for (int vertex : finishOrder) {
            if (visited[vertex]) continue;
            List<Integer> component = new ArrayList<>();
            collect(vertex, reverse, visited, component);
            components.add(component);
        }
        return components;
    }

    private static void finishDfs(int vertex, List<List<GraphUtils.Neighbor>> graph, boolean[] visited, List<Integer> order) {
        visited[vertex] = true;
        for (GraphUtils.Neighbor neighbor : graph.get(vertex)) if (!visited[neighbor.vertex]) finishDfs(neighbor.vertex, graph, visited, order);
        order.add(vertex);
    }

    private static void collect(int vertex, List<List<GraphUtils.Neighbor>> graph, boolean[] visited, List<Integer> component) {
        visited[vertex] = true;
        component.add(vertex);
        for (GraphUtils.Neighbor neighbor : graph.get(vertex)) if (!visited[neighbor.vertex]) collect(neighbor.vertex, graph, visited, component);
    }
}
