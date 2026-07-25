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