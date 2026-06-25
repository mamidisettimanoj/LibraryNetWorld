package com.librarynet.controller;

import com.librarynet.service.DsaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmController {
    private final DsaService dsa;

    public AlgorithmController(DsaService dsa) { this.dsa = dsa; }

    @GetMapping("/tree-comparison/{catalogId}")
    public Map<String, Object> treeComparison(@PathVariable int catalogId) {
        return dsa.treeComparison(catalogId);
    }

    @GetMapping("/bfs/{startCatalogId}")
    public Map<String, Object> bfs(@PathVariable int startCatalogId) {
        return dsa.bfs(startCatalogId);
    }

    @GetMapping("/shortest-path")
    public Map<String, Object> shortestPath(@RequestParam int source, @RequestParam int target) {
        return dsa.shortestPath(source, target);
    }

    @GetMapping("/mst")
    public Map<String, Object> mst() { return dsa.minimumSpanningTree(); }

    @GetMapping("/sort")
    public Map<String, Object> sort(@RequestParam(defaultValue = "merge") String algorithm) {
        return dsa.sortCatalog(algorithm);
    }

    @GetMapping("/publication-range")
    public Map<String, Object> publicationRange(@RequestParam int startYear, @RequestParam int endYear) {
        return dsa.publicationRange(startYear, endYear);
    }

    @GetMapping("/optimization-demo")
    public Map<String, Object> optimizationDemo() { return dsa.optimizationDemo(); }
}
