-- BigQuery daily transaction summary mart
-- Purpose:
-- Store dashboard-ready daily transaction metrics.

CREATE TABLE IF NOT EXISTS `<PROJECT_ID>.banking_refinery_mart.daily_transaction_summary` (
    transaction_date DATE,
    country STRING,
    currency STRING,
    merchant_category STRING,
    total_transactions INT64,
    approved_transactions INT64,
    declined_transactions INT64,
    total_amount NUMERIC,
    average_amount NUMERIC,
    high_risk_transactions INT64,
    approved_percentage NUMERIC,
    mart_created_at TIMESTAMP
);