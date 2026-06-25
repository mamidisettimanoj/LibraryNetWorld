package com.librarynet.controller;

import com.librarynet.dto.RecommendationResponse;
import com.librarynet.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {
    private final RecommendationService recommendations;

    public DiscoveryController(RecommendationService recommendations) {
        this.recommendations = recommendations;
    }

    @GetMapping("/recommendations/{catalogId}")
    public List<RecommendationResponse> recommend(@PathVariable Integer catalogId,
                                                   @RequestParam(defaultValue = "5") int limit) {
        return recommendations.recommend(catalogId, limit);
    }
}
