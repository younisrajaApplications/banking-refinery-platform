# Banking Refinery Platform

## Overview

## Business Problem

## Architecture

## Tech Stack

## Repository Structure

## Getting Started

## Future Improvements

## Project Documentation

- [Requirements](docs/requirements.md)
- [Architecture](docs/architecture.md)
- [Data Flow](docs/data-flow.md)
- [Runbook](docs/runbook.md)
- [ADR-001](docs/decisions/ADR-001.md)
- [ADR-002 Repository Structure](docs/decisions/ADR-002-repository-structure.md)
- [Raw Transaction Schema](data/schemas/transactions_raw_schema.md)

## Application Services

### Java Ingestion Service

Location:

`app/java-ingestion-service`

Purpose:

The Java Ingestion Service receives raw banking transaction data, validates records and prepares accepted and rejected outputs for the wider data refinery platform.

Current endpoints:

- `/health`
- `/actuator/health`

The Java Ingestion Service can upload a transaction CSV file, validate each row, write accepted and rejected outputs, and generate a reconciliation report proving the processing outcome.

## Local Database

The project uses a local Postgres database through Docker Compose.

The database stores ingestion metadata, accepted transactions, rejected transactions and reconciliation report references.

Start the database:

```bash
docker compose up -d
```
Check running containers:
```bash
docker compose ps
```
Connect to postgres:
```bash
docker exec -it banking-refinery-postgres psql -U refinery_user -d banking_refinery
```
Database schema changes are managed through Flyway migrations located in:

app/java-ingestion-service/src/main/resources/db/migration

## SQL Data Quality Checks

Postgres data quality checks live in:

`sql/postgres/data-quality/`

Run all checks:

```bash
./scripts/run-postgres-quality-checks.sh
```

Most checks use the pattern:

`zero rows = pass`

`one or more rows = investigate`

These checks validate reconciliation, duplicate transactions, accepted transaction quality, rejected record explainability and reconciliation report coverage.

### CI-Friendly Data Quality Checks

Run the CI-friendly quality gate:

```bash
./scripts/run-postgres-quality-checks-ci.sh
```
This script runs the failure-check SQL files and exits with:

0 when all checks pass
1 when one or more checks return failing rows

The summary query 99_ingestion_quality_summary.sql is excluded from the CI gate because it is expected to return rows.

## Jenkins CI Pipeline

This repository includes a root-level `Jenkinsfile`.

The pipeline currently:

- verifies required tools
- starts Postgres using Docker Compose
- runs Java tests
- runs CI-friendly SQL data quality checks
- publishes test results
- archives Postgres logs

The pipeline represents the project validation process as code.

For more detail, see:

`jenkins/README.md`