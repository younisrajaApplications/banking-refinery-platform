-- BigQuery curated transactions table
-- Purpose:
-- Store cleaned and standardised transaction records for analytics.

CREATE TABLE IF NOT EXISTS `<PROJECT_ID>.banking_refinery_curated.curated_transactions` (
    ingestion_file_id INT64,
    transaction_id STRING,
    customer_id STRING,
    account_id STRING,
    transaction_timestamp TIMESTAMP,
    transaction_date DATE,
    merchant_id STRING,
    merchant_category STRING,
    amount NUMERIC,
    currency STRING,
    country STRING,
    status STRING,
    risk_score INT64,
    is_high_risk BOOL,
    is_approved BOOL,
    loaded_at TIMESTAMP,
    curated_at TIMESTAMP
);


