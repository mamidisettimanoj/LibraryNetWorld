package com.librarynet.dto;

import jakarta.validation.constraints.*;

public record BookRequest(
        @NotNull @PositiveOrZero Integer catalogId,
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 120) String author,
        @NotBlank @Size(max = 80) String category,
        @Min(1000) @Max(2100) Integer publicationYear,
        @Size(max = 32) String isbn
) { }
