# Data Quality Checks

## Purpose

The Banking Refinery Platform uses SQL data quality checks to validate ingestion results after they have been persisted to Postgres.

Java validation prevents bad records from being accepted during ingestion.

SQL data quality checks verify that the stored data remains consistent, reconciled and explainable.

## Check Location

Postgres data quality checks live in:

`sql/postgres/data-quality/`

## Checks

### 01_ingestion_reconciliation_check.sql

Checks that:

`total_rows = accepted_rows + rejected_rows`

Expected result:

zero rows

### 02_duplicate_transaction_check.sql

Checks that accepted transactions do not contain duplicate transaction IDs within the same ingestion file.

Expected result:

zero rows

### 03_invalid_currency_check.sql

Checks that accepted transactions only contain supported currencies.

Expected result:

zero rows

### 04_invalid_status_check.sql

Checks that accepted transactions only contain supported statuses.

Expected result:

zero rows

### 05_invalid_amount_check.sql

Checks that accepted transaction amounts are greater than zero.

Expected result:

zero rows

### 06_rejected_without_reason_check.sql

Checks that rejected transactions always include a rejection reason.

Expected result:

zero rows

### 07_missing_reconciliation_report_check.sql

Checks that each ingestion file has a reconciliation report record.

Expected result:

zero rows

### 99_ingestion_quality_summary.sql

Produces a summary of recent ingestion runs, including accepted percentage, rejected percentage and processing status.

Expected result:

one or more rows when ingestion data exists

## Running Checks

Start Postgres:

```bash
docker compose up -d
```
Run All Checks:
```bash
./scripts/run-postgres-quality-checks.sh
```
## Quality Check Pattern
Most checks follow this pattern:

`zero rows = pass`

`one or more rows = investigate`

## Why This Matters

In banking-style data systems, it is not enough for data to be loaded. The platform must prove that the data is complete, valid and explainable after ingestion.