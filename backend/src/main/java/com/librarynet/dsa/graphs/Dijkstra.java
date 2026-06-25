package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class Dijkstra {
    public static final long INF = Long.MAX_VALUE / 4;

    public static final class Result {
        private final int source;
        private final long[] distances;
        private final int[] parents;
        Result(int source, long[] distances, int[] parents) {
            this.source = source;
            this.distances = distances;
            this.parents = parents;
        }
        public long distanceTo(int vertex) { return distances[vertex]; }
        public long[] getDistances() { return Arrays.copyOf(distances, distances.length); }
        public List<Integer> pathTo(int destination) {
            if (distances[destination] >= INF) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            for (int at = destination; at != -1; at = parents[at]) path.add(at);
            Collections.reverse(path);
            return path;
        }
        public int getSource() { return source; }
    }

    private static final class State {
        final int vertex;
        final long distance;
        State(int vertex, long distance) { this.vertex = vertex; this.distance = distance; }
    }

    private Dijkstra() { }

    public static Result shortestPaths(int vertices, List<Edge> edges, int source, boolean directed) {
        GraphUtils.validateStart(vertices, source);
        for (Edge edge : edges) {
            if (edge.getWeight() < 0) throw new IllegalArgumentException("Dijkstra cannot process negative edge weights");
        }
        List<List<GraphUtils.Neighbor>> graph = GraphUtils.adjacency(vertices, edges, directed);
        long[] distance = new long[vertices];
        int[] parent = new int[vertices];
        Arrays.fill(distance, INF);
        Arrays.fill(parent, -1);
        distance[source] = 0;
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.distance));
        queue.add(new State(source, 0));
        while (!queue.isEmpty()) {
            State state = queue.remove();
            if (state.distance != distance[state.vertex]) continue;
            for (GraphUtils.Neighbor neighbor : graph.get(state.vertex)) {
                long candidate = state.distance + neighbor.weight;
                if (candidate < distance[neighbor.vertex]) {
                    distance[neighbor.vertex] = candidate;
                    parent[neighbor.vertex] = state.vertex;
                    queue.add(new State(neighbor.vertex, candidate));
                }
            }
        }
        return new Result(source, distance, parent);
    }
}
