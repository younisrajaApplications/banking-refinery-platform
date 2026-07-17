CREATE TABLE ingestion_file(
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    total_rows BIGINT NOT NULL,
    accepted_rows BIGINT NOT NULL,
    rejected_rows BIGINT NOT NULL,
    reconciled BOOLEAN NOT NULL,
    processing_status VARCHAR(255) NOT NULL,
    accepted_output_path TEXT,
    rejected_output_path TEXT,
    reconciliation_report_path TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accepted_transaction (
    id BIGSERIAL PRIMARY KEY,
    ingestion_file_id BIGINT NOT NULL REFERENCES ingestion_file(id),
    transaction_id VARCHAR(100) NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    account_id VARCHAR(100) NOT NULL,
    transaction_timestamp TIMESTAMP NOT NULL,
    merchant_id VARCHAR(100) NOT NULL,
    merchant_category VARCHAR(100) NOT NULL,
    amount NUMERIC(18, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    country VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    risk_score INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rejected_transaction(
    id BIGSERIAL PRIMARY KEY,
    ingestion_file_id BIGINT NOT NULL REFERENCES ingestion_file(id),
    row_number BIGINT NOT NULL,
    transaction_id VARCHAR(255) NOT NULL,
    rejection_reason TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reconciliation_report (
    id BIGSERIAL PRIMARY KEY,
    ingestion_file_id BIGINT NOT NULL REFERENCES ingestion_file(id),
    report_path TEXT NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);