-- Transformation: raw transactions to curated transactions
-- Purpose:
-- Standardise accepted raw transaction records for analytical use.

INSERT INTO `<PROJECT_ID>.banking_refinery_curated.curated_transactions` (
    ingestion_file_id,
    transaction_id,
    customer_id,
    account_id,
    transaction_timestamp,
    transaction_date,
    merchant_id,
    merchant_category,
    amount,
    currency,
    country,
    status,
    risk_score,
    is_high_risk,
    is_approved,
    loaded_at,
    curated_at
)
SELECT
    ingestion_file_id,
    transaction_id,
    customer_id,
    account_id,
    transaction_timestamp,
    DATE(transaction_timestamp) AS transaction_date,
    merchant_id,
    UPPER(TRIM(merchant_category)) AS merchant_category,
    amount,
    UPPER(TRIM(currency)) AS currency,
    UPPER(TRIM(country)) AS country,
    UPPER(TRIM(status)) AS status,
    risk_score,
    risk_score >= 70 AS is_high_risk,
    UPPER(TRIM(status)) = 'APPROVED' AS is_approved,
    loaded_at,
    CURRENT_TIMESTAMP() AS curated_at
FROM `<PROJECT_ID>.banking_refinery_raw.raw_transactions`