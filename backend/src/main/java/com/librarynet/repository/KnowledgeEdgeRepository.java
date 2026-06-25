package com.librarynet.repository;

import com.librarynet.domain.KnowledgeEdgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KnowledgeEdgeRepository extends JpaRepository<KnowledgeEdgeEntity, Long> {
    List<KnowledgeEdgeEntity> findBySourceBookIdOrDestinationBookId(Long sourceBookId, Long destinationBookId);
}
