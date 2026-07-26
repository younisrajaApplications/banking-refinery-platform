-- Check: accepted transaction amounts must be greater than zero
-- Expected result: zero rows
-- If rows are returned, invalid financial values entered accepted transactions.

SELECT
    ingestion_file_id,
    transaction_id,
    amount,
    created_at
FROM accepted_transaction
WHERE amount <= 0;