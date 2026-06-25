package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BellmanFord {
    public static final long INF = Long.MAX_VALUE / 4;

    public static final class Result {
        private final int source;
        private final long[] distances;
        private final int[] parents;
        private final boolean negativeCycle;
        Result(int source, long[] distances, int[] parents, boolean negativeCycle) {
            this.source = source;
            this.distances = distances;
            this.parents = parents;
            this.negativeCycle = negativeCycle;
        }
        public boolean hasNegativeCycle() { return negativeCycle; }
        public long distanceTo(int vertex) { return distances[vertex]; }
        public long[] getDistances() { return Arrays.copyOf(distances, distances.length); }
        public List<Integer> pathTo(int destination) {
            if (negativeCycle || distances[destination] >= INF) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            boolean[] seen = new boolean[parents.length];
            for (int at = destination; at != -1; at = parents[at]) {
                if (seen[at]) return Collections.emptyList();
                seen[at] = true;
                path.add(at);
            }
            Collections.reverse(path);
            return path;
        }
        public int getSource() { return source; }
    }

    private BellmanFord() { }

    public static Result shortestPaths(int vertices, List<Edge> edges, int source, boolean directed) {
        GraphUtils.validateStart(vertices, source);
        List<Edge> working = new ArrayList<>(edges);
        for (Edge edge : edges) {
            GraphUtils.validateEdge(vertices, edge);
            if (!directed) working.add(new Edge(edge.getDestination(), edge.getSource(), edge.getWeight()));
        }
        long[] distance = new long[vertices];
        int[] parent = new int[vertices];
        Arrays.fill(distance, INF);
        Arrays.fill(parent, -1);
        distance[source] = 0;
        for (int i = 1; i < vertices; i++) {
            boolean changed = false;
            for (Edge edge : working) {
                if (distance[edge.getSource()] >= INF) continue;
                long candidate = distance[edge.getSource()] + edge.getWeight();
                if (candidate < distance[edge.getDestination()]) {
                    distance[edge.getDestination()] = candidate;
                    parent[edge.getDestination()] = edge.getSource();
                    changed = true;
                }
            }
            if (!changed) break;
        }
        boolean negativeCycle = false;
        for (Edge edge : working) {
            if (distance[edge.getSource()] < INF && distance[edge.getSource()] + edge.getWeight() < distance[edge.getDestination()]) {
                negativeCycle = true;
                break;
            }
        }
        return new Result(source, distance, parent, negativeCycle);
    }
}
