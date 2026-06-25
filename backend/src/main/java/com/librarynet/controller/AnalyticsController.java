package com.librarynet.controller;

import com.librarynet.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analytics;

    public AnalyticsController(AnalyticsService analytics) { this.analytics = analytics; }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() { return analytics.dashboard(); }

    @GetMapping("/monthly-loans")
    public List<Map<String, Object>> monthlyLoans(@RequestParam(defaultValue = "12") int months) {
        return analytics.monthlyLoans(months);
    }

    @GetMapping("/categories")
    public List<Map<String, Object>> categories() { return analytics.categoryDistribution(); }
}
