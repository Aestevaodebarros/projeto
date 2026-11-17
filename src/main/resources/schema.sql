CREATE TABLE IF NOT EXISTS domain_documents (
    collection_name VARCHAR(80) NOT NULL,
    document_id VARCHAR(120) NOT NULL,
    localidade_id INTEGER,
    payload CLOB NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (collection_name, document_id)
);

CREATE INDEX IF NOT EXISTS idx_domain_documents_localidade
    ON domain_documents (collection_name, localidade_id);

CREATE TABLE IF NOT EXISTS domain_sequences (
    collection_name VARCHAR(80) PRIMARY KEY,
    next_value BIGINT NOT NULL
);
