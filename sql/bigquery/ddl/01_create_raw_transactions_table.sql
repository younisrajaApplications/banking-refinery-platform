-- BigQuery raw transactions table
-- Purpose:
-- Store accepted transaction records in a source-like structure before analytical transformation.

CREATE TABLE IF NOT EXISTS `<PROJECT_ID>.banking_refinery_raw.raw_transactions` (
    ingestion_file_id INT64,
    transaction_id STRING,
    customer_id STRING,
    account_id STRING,
    transaction_timestamp TIMESTAMP,
    merchant_id STRING,
    merchant_category STRING,
    amount NUMERIC,
    currency STRING,
    country STRING,
    status STRING,
    risk_score INT64,
    source_file_name STRING,
    loaded_at TIMESTAMP
)

-- This table represents the first landing area in the warehouse.
--
-- The records are already accepted by the Java ingestion service, but the raw BigQuery table still stays close to the original transaction structure.
--
-- We include:
-- ingestion_file_id
-- source_file_name
-- loaded_at
-- because warehouse data should still be traceable back to the ingestion run.