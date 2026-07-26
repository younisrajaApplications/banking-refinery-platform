-- Check: accepted transactions should only contain supported currencies
-- Expected result: zero rows
-- If rows are returned, unsupported currencies entered the accepted table.

SELECT
    ingestion_file_id,
    transaction_id,
    currency,
    created_at
FROM accepted_transaction
WHERE currency NOT IN ('GBP', 'USD', 'EUR');