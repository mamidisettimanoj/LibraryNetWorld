package com.librarynet.dto;

public record RecommendationResponse(
        BookResponse book,
        int score,
        String reason
) { }
