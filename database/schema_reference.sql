-- Reference-only schema. Spring Data JPA creates/updates these tables automatically.
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    catalog_id INTEGER NOT NULL UNIQUE,
    title VARCHAR(180) NOT NULL,
    author VARCHAR(120) NOT NULL,
    category VARCHAR(80) NOT NULL,
    publication_year INTEGER,
    isbn VARCHAR(32) UNIQUE,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    borrow_count INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS members (
    id BIGSERIAL PRIMARY KEY,
    member_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    max_borrow_limit INTEGER NOT NULL DEFAULT 3
);

CREATE TABLE IF NOT EXISTS publications (
    id BIGSERIAL PRIMARY KEY,
    publication_code INTEGER NOT NULL UNIQUE,
    title VARCHAR(180) NOT NULL,
    author VARCHAR(120) NOT NULL,
    publication_year INTEGER NOT NULL,
    type VARCHAR(40) NOT NULL,
    resource_url VARCHAR(400)
);

CREATE TABLE IF NOT EXISTS borrow_transactions (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES members(id),
    book_id BIGINT NOT NULL REFERENCES books(id),
    borrowed_at DATE NOT NULL,
    due_at DATE NOT NULL,
    returned_at DATE,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS knowledge_edges (
    id BIGSERIAL PRIMARY KEY,
    source_book_id BIGINT NOT NULL REFERENCES books(id),
    destination_book_id BIGINT NOT NULL REFERENCES books(id),
    weight INTEGER NOT NULL,
    relation_type VARCHAR(40) NOT NULL,
    CONSTRAINT uk_knowledge_edge UNIQUE(source_book_id, destination_book_id)
);
