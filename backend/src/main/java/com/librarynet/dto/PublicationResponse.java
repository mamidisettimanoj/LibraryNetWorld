package com.librarynet.dto;

import com.librarynet.domain.PublicationEntity;

public record PublicationResponse(
        Long id,
        Integer publicationCode,
        String title,
        String author,
        Integer publicationYear,
        String type,
        String resourceUrl
) {
    public static PublicationResponse from(PublicationEntity item) {
        return new PublicationResponse(item.getId(), item.getPublicationCode(), item.getTitle(),
                item.getAuthor(), item.getPublicationYear(), item.getType(), item.getResourceUrl());
    }
}
