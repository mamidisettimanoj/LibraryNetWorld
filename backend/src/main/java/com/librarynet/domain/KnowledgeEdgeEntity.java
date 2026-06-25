package com.librarynet.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "knowledge_edges", uniqueConstraints = @UniqueConstraint(
        name = "uk_knowledge_edge", columnNames = {"source_book_id", "destination_book_id"}))
public class KnowledgeEdgeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "source_book_id", nullable = false)
    private BookEntity sourceBook;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_book_id", nullable = false)
    private BookEntity destinationBook;

    @Column(nullable = false)
    private int weight = 1;

    @Column(name = "relation_type", nullable = false, length = 40)
    private String relationType = "RELATED";

    public KnowledgeEdgeEntity() { }

    public KnowledgeEdgeEntity(BookEntity sourceBook, BookEntity destinationBook, int weight, String relationType) {
        this.sourceBook = sourceBook;
        this.destinationBook = destinationBook;
        this.weight = weight;
        this.relationType = relationType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BookEntity getSourceBook() { return sourceBook; }
    public void setSourceBook(BookEntity sourceBook) { this.sourceBook = sourceBook; }
    public BookEntity getDestinationBook() { return destinationBook; }
    public void setDestinationBook(BookEntity destinationBook) { this.destinationBook = destinationBook; }
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
}
