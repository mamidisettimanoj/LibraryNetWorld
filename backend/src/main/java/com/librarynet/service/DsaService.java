package com.librarynet.service;

import com.librarynet.domain.BookEntity;
import com.librarynet.domain.KnowledgeEdgeEntity;
import com.librarynet.domain.PublicationEntity;
import com.librarynet.dsa.graphs.Dijkstra;
import com.librarynet.dsa.graphs.LibraryBFS;
import com.librarynet.dsa.graphs.LibraryKruskalsMST;
import com.librarynet.dsa.graphs.MSTResult;
import com.librarynet.dsa.indexing.BPlusTree;
import com.librarynet.dsa.indexing.BTree;
import com.librarynet.dsa.model.Edge;
import com.librarynet.dsa.optimization.CoinChange;
import com.librarynet.dsa.optimization.LCS;
import com.librarynet.dsa.optimization.LIS;
import com.librarynet.dsa.optimization.ZeroOneKnapsack;
import com.librarynet.dsa.sorting.*;
import com.librarynet.dsa.trees.AVLTree;
import com.librarynet.dsa.trees.BSTLibrary;
import com.librarynet.dsa.trees.TreeSearchResult;
import com.librarynet.repository.BookRepository;
import com.librarynet.repository.KnowledgeEdgeRepository;
import com.librarynet.repository.PublicationRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DsaService {
    private final BookRepository books;
    private final KnowledgeEdgeRepository knowledgeEdges;
    private final PublicationRepository publications;

    public DsaService(BookRepository books, KnowledgeEdgeRepository knowledgeEdges,
                      PublicationRepository publications) {
        this.books = books;
        this.knowledgeEdges = knowledgeEdges;
        this.publications = publications;
    }

    public Map<String, Object> treeComparison(int catalogId) {
        BSTLibrary bst = new BSTLibrary();
        AVLTree avl = new AVLTree();
        for (BookEntity entity : sortedBooks()) {
            com.librarynet.dsa.model.Book book = toDsaBook(entity);
            bst.addResource(book);
            avl.addResource(book);
        }
        TreeSearchResult bstResult = bst.searchWithStats(catalogId);
        TreeSearchResult avlResult = avl.searchWithStats(catalogId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("catalogId", catalogId);
        result.put("found", bstResult.isFound());
        result.put("title", bstResult.isFound() ? bstResult.getBook().getTitle() : null);
        result.put("bstComparisons", bstResult.getComparisons());
        result.put("avlComparisons", avlResult.getComparisons());
        result.put("bstHeight", bst.height());
        result.put("avlHeight", avl.height());
        result.put("avlRotations", avl.getRotationCount());
        result.put("bstValid", bst.isValidBST());
        result.put("avlBalanced", avl.isBalanced());
        result.put("sortedCatalogIds", avl.inorder().stream().map(com.librarynet.dsa.model.Book::getId).toList());
        return result;
    }

    public Map<String, Object> bfs(int startCatalogId) {
        GraphSnapshot graph = graphSnapshot();
        Integer start = graph.vertexByCatalogId().get(startCatalogId);
        if (start == null) throw new IllegalArgumentException("Unknown start catalog ID: " + startCatalogId);
        List<Integer> traversal = LibraryBFS.traversal(graph.books().size(), graph.edges(), start, false);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("algorithm", "Breadth-First Search");
        result.put("startCatalogId", startCatalogId);
        result.put("traversal", traversal.stream().map(vertex -> bookLabel(graph.books().get(vertex))).toList());
        return result;
    }

    public Map<String, Object> shortestPath(int sourceCatalogId, int targetCatalogId) {
        GraphSnapshot graph = graphSnapshot();
        Integer source = graph.vertexByCatalogId().get(sourceCatalogId);
        Integer target = graph.vertexByCatalogId().get(targetCatalogId);
        if (source == null || target == null) throw new IllegalArgumentException("Source or target catalog ID does not exist");
        Dijkstra.Result paths = Dijkstra.shortestPaths(graph.books().size(), graph.edges(), source, false);
        List<Integer> vertices = paths.pathTo(target);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("algorithm", "Dijkstra");
        result.put("sourceCatalogId", sourceCatalogId);
        result.put("targetCatalogId", targetCatalogId);
        result.put("reachable", !vertices.isEmpty());
        result.put("distance", vertices.isEmpty() ? null : paths.distanceTo(target));
        result.put("path", vertices.stream().map(vertex -> bookLabel(graph.books().get(vertex))).toList());
        return result;
    }

    public Map<String, Object> minimumSpanningTree() {
        GraphSnapshot graph = graphSnapshot();
        MSTResult mst = LibraryKruskalsMST.compute(graph.books().size(), graph.edges());
        List<Map<String, Object>> selected = new ArrayList<>();
        for (Edge edge : mst.getEdges()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("source", bookLabel(graph.books().get(edge.getSource())));
            row.put("destination", bookLabel(graph.books().get(edge.getDestination())));
            row.put("weight", edge.getWeight());
            selected.add(row);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("algorithm", "Kruskal Minimum Spanning Tree");
        result.put("spanning", mst.isSpanning());
        result.put("totalWeight", mst.getTotalWeight());
        result.put("edges", selected);
        return result;
    }

    public Map<String, Object> sortCatalog(String algorithm) {
        int[] values = sortedBooks().stream().mapToInt(BookEntity::getCatalogId).toArray();
        int[] unsorted = reverseCopy(values);
        long start = System.nanoTime();
        switch (algorithm.toLowerCase(Locale.ROOT)) {
            case "merge" -> MergeSort.sort(unsorted);
            case "quick" -> QuickSort.sort(unsorted);
            case "heap" -> HeapSort.sort(unsorted);
            case "counting" -> CountingSort.sort(unsorted);
            case "radix" -> RadixSort.sort(unsorted);
            default -> throw new IllegalArgumentException("Use merge, quick, heap, counting, or radix");
        }
        long elapsed = System.nanoTime() - start;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("algorithm", algorithm.toLowerCase(Locale.ROOT));
        result.put("input", reverseCopy(values));
        result.put("sorted", unsorted);
        result.put("elapsedNanoseconds", elapsed);
        return result;
    }

    public Map<String, Object> publicationRange(int startYear, int endYear) {
        if (startYear > endYear) throw new IllegalArgumentException("Start year cannot be after end year");
        BTree bTree = new BTree();
        BPlusTree bPlusTree = new BPlusTree();
        for (PublicationEntity entity : publications.findAll()) {
            com.librarynet.dsa.model.Publication item = new com.librarynet.dsa.model.Publication(
                    entity.getPublicationCode(), entity.getPublicationYear(), entity.getTitle(), entity.getAuthor());
            bTree.insert(item);
            bPlusTree.insert(item);
        }
        List<Map<String, Object>> matches = bPlusTree.rangeSearch(startYear, endYear).stream().map(item -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("publicationCode", item.getId());
            row.put("title", item.getTitle());
            row.put("author", item.getAuthor());
            row.put("year", item.getYear());
            return row;
        }).toList();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("startYear", startYear);
        result.put("endYear", endYear);
        result.put("matches", matches);
        result.put("bTreeHeight", bTree.height());
        result.put("bTreeValid", bTree.isValid());
        result.put("bPlusTreeValid", bPlusTree.isValid());
        return result;
    }

    public Map<String, Object> optimizationDemo() {
        int[] costs = {10, 20, 30, 15};
        int[] values = {60, 100, 120, 75};
        ZeroOneKnapsack.Result knapsack = ZeroOneKnapsack.tabulation(costs, values, 50);
        LIS.Result readingTrend = LIS.longestIncreasingSubsequence(new int[]{3, 5, 4, 8, 6, 9, 11, 10, 14});
        CoinChange.Result coins = CoinChange.dynamicProgramming(new int[]{1, 3, 4}, 6);
        LCS.Result similarity = LCS.tabulation("ALGORITHM", "LOGARITHM");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("acquisitionKnapsack", Map.of(
                "budget", 50,
                "maximumValue", knapsack.getMaximumValue(),
                "selectedIndices", knapsack.getSelectedIndices()));
        result.put("readingTrendLIS", Map.of(
                "length", readingTrend.getLength(),
                "sequence", readingTrend.getSequence()));
        result.put("coinChange", Map.of(
                "amount", 6,
                "minimumCoins", coins.getCoinCount(),
                "coins", coins.getCoins()));
        result.put("titleSimilarityLCS", Map.of(
                "length", similarity.getLength(),
                "sequence", similarity.getSequence()));
        return result;
    }

    private List<BookEntity> sortedBooks() {
        return books.findAllByOrderByCatalogIdAsc();
    }

    private com.librarynet.dsa.model.Book toDsaBook(BookEntity book) {
        return new com.librarynet.dsa.model.Book(book.getCatalogId(), book.getTitle(), book.getAuthor(), book.getCategory());
    }

    private String bookLabel(BookEntity book) {
        return book.getCatalogId() + " - " + book.getTitle();
    }

    private int[] reverseCopy(int[] sorted) {
        int[] result = new int[sorted.length];
        for (int i = 0; i < sorted.length; i++) result[i] = sorted[sorted.length - 1 - i];
        return result;
    }

    private GraphSnapshot graphSnapshot() {
        List<BookEntity> bookList = sortedBooks();
        if (bookList.isEmpty()) throw new IllegalStateException("Add books before running graph algorithms");
        Map<Long, Integer> vertexByDatabaseId = new HashMap<>();
        Map<Integer, Integer> vertexByCatalogId = new HashMap<>();
        for (int i = 0; i < bookList.size(); i++) {
            vertexByDatabaseId.put(bookList.get(i).getId(), i);
            vertexByCatalogId.put(bookList.get(i).getCatalogId(), i);
        }
        List<Edge> edgeList = new ArrayList<>();
        for (KnowledgeEdgeEntity edge : knowledgeEdges.findAll()) {
            Integer source = vertexByDatabaseId.get(edge.getSourceBook().getId());
            Integer destination = vertexByDatabaseId.get(edge.getDestinationBook().getId());
            if (source != null && destination != null) edgeList.add(new Edge(source, destination, edge.getWeight()));
        }
        return new GraphSnapshot(bookList, edgeList, vertexByCatalogId);
    }

    private record GraphSnapshot(List<BookEntity> books, List<Edge> edges,
                                 Map<Integer, Integer> vertexByCatalogId) { }
}
