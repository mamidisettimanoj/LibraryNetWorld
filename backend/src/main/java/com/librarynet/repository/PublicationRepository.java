package com.librarynet.repository;

import com.librarynet.domain.PublicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublicationRepository extends JpaRepository<PublicationEntity, Long> {
    Optional<PublicationEntity> findByPublicationCode(Integer publicationCode);
    boolean existsByPublicationCode(Integer publicationCode);
    List<PublicationEntity> findByPublicationYearBetweenOrderByPublicationYearAscTitleAsc(Integer start, Integer end);
    List<PublicationEntity> findAllByOrderByPublicationYearDescTitleAsc();
}
