package com.librarynet.dto;

import jakarta.validation.constraints.*;

public record PublicationRequest(
        @NotNull @PositiveOrZero Integer publicationCode,
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 120) String author,
        @NotNull @Min(1000) @Max(2100) Integer publicationYear,
        @NotBlank @Size(max = 40) String type,
        @Size(max = 400) String resourceUrl
) { }
