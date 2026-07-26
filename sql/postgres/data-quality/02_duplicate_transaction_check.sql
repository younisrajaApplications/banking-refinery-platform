-- Check: accepted transactions should not have duplicate transaction IDs within the same ingestion file
-- Expected result: zero rows
-- If rows are returned, the same transaction_id appeared more than once in accepted records.

SELECT
    ingestion_file_id,
    transaction_id,
    COUNT(*) AS duplicate_results
FROM accepted_transaction
GROUP BY ingestion_file_id, transaction_id
HAVING COUNT(*) > 1;