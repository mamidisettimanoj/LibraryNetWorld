package com.librarynet.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "publications")
public class PublicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publication_code", nullable = false, unique = true)
    private Integer publicationCode;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 120)
    private String author;

    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(name = "resource_url", length = 400)
    private String resourceUrl;

    public PublicationEntity() { }

    public PublicationEntity(Integer publicationCode, String title, String author,
                             Integer publicationYear, String type, String resourceUrl) {
        this.publicationCode = publicationCode;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.type = type;
        this.resourceUrl = resourceUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getPublicationCode() { return publicationCode; }
    public void setPublicationCode(Integer publicationCode) { this.publicationCode = publicationCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getResourceUrl() { return resourceUrl; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }
}
