# Database Design - Banking Refinery Platform

## Purpose

The local Postgres database stores ingestion metadata, accepted transactions, rejected transactions and reconciliation report references.

## Why Postgres?

Postgres is used locally to model the operational database layer of the platform. It gives the Java ingestion service a structured place to persist processing results before data is later prepared for analytical warehouse loading.

## Tables

### ingestion_file

Stores one row per uploaded source file.

Used to track:

- file name
- total row count
- accepted row count
- rejected row count
- reconciliation status
- processing status
- output file paths

### accepted_transaction

Stores rows that passed validation.

Each accepted transaction links back to an ingestion file.

### rejected_transaction

Stores rows that failed validation.

Each rejected row includes:

- source row number
- transaction ID if available
- rejection reason

### reconciliation_report

Stores the generated reconciliation report path for each ingestion file.

## Migration Strategy

Database schema changes are managed through Flyway migrations.

Migration files live in:

`app/java-ingestion-service/src/main/resources/db/migration`

The first migration is:

`V1__create_ingestion_tables.sql`

## Design Principle

The database schema is version-controlled so it can be recreated consistently across environments.

## Persistence Flow

When a transaction CSV is uploaded, the Java Ingestion Service persists the result of the ingestion run into Postgres.

The flow is:

1. A record is inserted into `ingestion_file`.
2. Accepted records are inserted into `accepted_transaction`.
3. Rejected records are inserted into `rejected_transaction`.
4. The reconciliation report path is inserted into `reconciliation_report`.

All inserts happen inside a database transaction.

This means if one insert fails, the full database save is rolled back. The database should not end up with half of an ingestion run saved.

## Why This Matters

The database acts as the operational audit trail for the ingestion process.

The generated CSV and JSON files are useful output artefacts, but Postgres gives us a structured way to query what happened during each upload.

For example, we can answer:

- Which files were processed?
- How many rows were accepted?
- How many rows were rejected?
- Why were rows rejected?
- Where is the reconciliation report?
- When did the ingestion run happen?

This is important in banking-style systems because ingestion should be traceable, auditable and explainable.

## Query API

The Java Ingestion Service exposes read endpoints backed by Postgres.

These endpoints allow users to inspect previous ingestion runs without connecting directly to the database.

The query flow is:

1. The client calls an API endpoint such as `GET /ingestions`.
2. The controller receives the HTTP request.
3. The service applies query behaviour and not-found handling.
4. The repository runs SQL against Postgres.
5. The response is returned as JSON.

This keeps database access inside the application instead of exposing direct database access to users.

## Query Indexes

The migration `V2__add_ingestion_query_indexes.sql` adds indexes for common query paths:

- ingestion file creation time
- ingestion processing status
- accepted transactions by ingestion file ID
- rejected transactions by ingestion file ID
- reconciliation reports by ingestion file ID

These indexes support faster lookups as ingestion history grows.