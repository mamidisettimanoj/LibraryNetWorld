package com.librarynet.dsa.graphs;

import com.librarynet.dsa.model.Edge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FloydWarshall {
    public static final long INF = Long.MAX_VALUE / 4;

    public static final class Result {
        private final long[][] distance;
        private final int[][] next;
        private final boolean negativeCycle;
        Result(long[][] distance, int[][] next, boolean negativeCycle) {
            this.distance = distance;
            this.next = next;
            this.negativeCycle = negativeCycle;
        }
        public long distance(int source, int destination) { return distance[source][destination]; }
        public boolean hasNegativeCycle() { return negativeCycle; }
        public long[][] getDistances() {
            long[][] copy = new long[distance.length][];
            for (int i = 0; i < distance.length; i++) copy[i] = distance[i].clone();
            return copy;
        }
        public List<Integer> path(int source, int destination) {
            if (next[source][destination] == -1 || negativeCycle) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            path.add(source);
            int current = source;
            while (current != destination) {
                current = next[current][destination];
                if (current == -1) return Collections.emptyList();
                path.add(current);
                if (path.size() > next.length + 1) return Collections.emptyList();
            }
            return path;
        }
    }

    private FloydWarshall() { }

    public static Result allPairsShortestPaths(int vertices, List<Edge> edges, boolean directed) {
        GraphUtils.validateVertices(vertices);
        long[][] distance = new long[vertices][vertices];
        int[][] next = new int[vertices][vertices];
        for (int i = 0; i < vertices; i++) {
            java.util.Arrays.fill(distance[i], INF);
            java.util.Arrays.fill(next[i], -1);
            distance[i][i] = 0;
            next[i][i] = i;
        }
        for (Edge edge : edges) {
            GraphUtils.validateEdge(vertices, edge);
            if (edge.getWeight() < distance[edge.getSource()][edge.getDestination()]) {
                distance[edge.getSource()][edge.getDestination()] = edge.getWeight();
                next[edge.getSource()][edge.getDestination()] = edge.getDestination();
            }
            if (!directed && edge.getWeight() < distance[edge.getDestination()][edge.getSource()]) {
                distance[edge.getDestination()][edge.getSource()] = edge.getWeight();
                next[edge.getDestination()][edge.getSource()] = edge.getSource();
            }
        }
        for (int k = 0; k < vertices; k++) {
            for (int i = 0; i < vertices; i++) {
                if (distance[i][k] >= INF) continue;
                for (int j = 0; j < vertices; j++) {
                    if (distance[k][j] >= INF) continue;
                    long candidate = distance[i][k] + distance[k][j];
                    if (candidate < distance[i][j]) {
                        distance[i][j] = candidate;
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
        boolean negativeCycle = false;
        for (int i = 0; i < vertices; i++) if (distance[i][i] < 0) negativeCycle = true;
        return new Result(distance, next, negativeCycle);
    }
}
