package com.librarynet.repository;

import com.librarynet.domain.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findByCatalogId(Integer catalogId);
    boolean existsByCatalogId(Integer catalogId);
    boolean existsByIsbn(String isbn);

    @Query("""
            select b from BookEntity b
            where lower(b.title) like lower(concat('%', :query, '%'))
               or lower(b.author) like lower(concat('%', :query, '%'))
               or lower(b.category) like lower(concat('%', :query, '%'))
            order by b.title
            """)
    List<BookEntity> search(@Param("query") String query);

    List<BookEntity> findAllByOrderByCatalogIdAsc();
    List<BookEntity> findTop5ByOrderByBorrowCountDescTitleAsc();
}
