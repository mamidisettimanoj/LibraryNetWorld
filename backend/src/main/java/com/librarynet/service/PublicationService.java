package com.librarynet.service;

import com.librarynet.domain.PublicationEntity;
import com.librarynet.dto.PublicationRequest;
import com.librarynet.dto.PublicationResponse;
import com.librarynet.exception.ConflictException;
import com.librarynet.exception.ResourceNotFoundException;
import com.librarynet.repository.PublicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublicationService {
    private final PublicationRepository publications;

    public PublicationService(PublicationRepository publications) {
        this.publications = publications;
    }

    public List<PublicationResponse> list(Integer startYear, Integer endYear) {
        List<PublicationEntity> result;
        if (startYear != null && endYear != null) {
            if (startYear > endYear) throw new IllegalArgumentException("Start year cannot be after end year");
            result = publications.findByPublicationYearBetweenOrderByPublicationYearAscTitleAsc(startYear, endYear);
        } else {
            result = publications.findAllByOrderByPublicationYearDescTitleAsc();
        }
        return result.stream().map(PublicationResponse::from).toList();
    }

    public PublicationEntity getEntity(Long id) {
        return publications.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found: " + id));
    }

    @Transactional
    public PublicationResponse create(PublicationRequest request) {
        if (publications.existsByPublicationCode(request.publicationCode())) {
            throw new ConflictException("Publication code already exists: " + request.publicationCode());
        }
        PublicationEntity item = new PublicationEntity(request.publicationCode(), request.title().trim(),
                request.author().trim(), request.publicationYear(), request.type().trim(), normalize(request.resourceUrl()));
        return PublicationResponse.from(publications.save(item));
    }

    @Transactional
    public PublicationResponse update(Long id, PublicationRequest request) {
        PublicationEntity item = getEntity(id);
        publications.findByPublicationCode(request.publicationCode()).filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new ConflictException("Publication code already exists: " + request.publicationCode()); });
        item.setPublicationCode(request.publicationCode());
        item.setTitle(request.title().trim());
        item.setAuthor(request.author().trim());
        item.setPublicationYear(request.publicationYear());
        item.setType(request.type().trim());
        item.setResourceUrl(normalize(request.resourceUrl()));
        return PublicationResponse.from(publications.save(item));
    }

    @Transactional
    public void delete(Long id) {
        publications.delete(getEntity(id));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
