# Banking Refinery Platform

A local-first data ingestion, reconciliation and quality validation platform using transaction data as the example domain.

The project demonstrates how raw business data can be ingested, validated, reconciled, persisted, queried and checked through a repeatable engineering workflow.

Although the sample data is transaction-based, the design patterns are transferable to many domains, including retail, logistics, finance, SaaS, public sector data platforms and internal business reporting systems.

## Overview

This project simulates a data engineering platform that processes incoming CSV files, validates each row, separates accepted and rejected records, stores ingestion metadata and runs SQL-based data quality checks.

The main purpose of the project is to demonstrate practical engineering skills across backend development, database design, SQL quality checks, CI/CD and data warehouse modelling.

Current capabilities include:

- Java-based ingestion service
- CSV upload and row-level validation
- accepted and rejected output generation
- reconciliation report generation
- Postgres persistence for ingestion audit history
- query API for previous ingestion runs
- SQL-based post-ingestion data quality checks
- CI-friendly quality gate scripts
- Jenkins pipeline-as-code
- local Jenkins execution
- end-to-end ingestion validation
- initial BigQuery warehouse layer design

## Problem Statement

Many organisations receive data from multiple source systems. Before that data can be trusted for reporting, analytics or downstream processing, it needs to be validated, reconciled and made auditable.

This project solves a simplified version of that problem.

It takes raw CSV data, validates each record, separates valid and invalid rows, records rejection reasons, generates reconciliation evidence and persists the processing outcome to Postgres.

The platform focuses on a common engineering problem:

```text
Can this data be trusted after it has been ingested?
```

## Architecture

The current platform flow is:

```text
CSV File
   ↓
Spring Boot Ingestion API
   ↓
Validation Layer
   ↓
Accepted / Rejected Outputs
   ↓
Reconciliation Report
   ↓
Postgres Audit Store
   ↓
Ingestion Query API
   ↓
SQL Data Quality Checks
   ↓
Jenkins CI Pipeline
   ↓
BigQuery Warehouse Design
```

The Java service handles ingestion and validation.

Postgres stores the operational audit trail.

SQL checks verify that persisted data is complete, valid and explainable.

Jenkins runs the validation workflow as a repeatable pipeline.

BigQuery is designed as the future analytical warehouse layer.

## Tech Stack

- Java
- Spring Boot
- Maven
- Postgres
- Flyway
- SQL
- Docker Compose
- Bash
- Jenkins
- GitHub
- BigQuery SQL

Planned future additions:

- Terraform
- GCP BigQuery deployment
- automated warehouse loading
- scheduled data transformations
- dashboard-ready reporting marts

## Repository Structure

```text
banking-refinery-platform/
├── Jenkinsfile
├── app/
│   └── java-ingestion-service/
├── data/
│   ├── sample/
│   ├── processed/
│   └── schemas/
├── docs/
│   └── decisions/
├── jenkins/
├── scripts/
├── sql/
│   ├── postgres/
│   │   └── data-quality/
│   └── bigquery/
│       ├── ddl/
│       └── transformations/
├── terraform/
├── docker-compose.yml
├── docker-compose.ci.yml
├── docker-compose.jenkins.yml
└── README.md
```

### Key folders

- `app/` contains application code.
- `data/` contains sample files, schemas and generated local outputs.
- `docs/` contains project documentation and architecture decisions.
- `sql/postgres/` contains Postgres data quality checks.
- `sql/bigquery/` contains warehouse DDL and transformation SQL.
- `scripts/` contains repeatable local and CI helper scripts.
- `jenkins/` contains Jenkins-related documentation.
- `terraform/` is reserved for future infrastructure-as-code work.

## Getting Started

### 1. Start Postgres

```bash
docker compose up -d
```

Check the container is running:

```bash
docker compose ps
```

### 2. Run the Java tests

```bash
cd app/java-ingestion-service
./mvnw clean test
```

### 3. Start the Java ingestion service

```bash
./mvnw spring-boot:run
```

The service runs on:

```text
http://localhost:8080
```

### 4. Upload a sample CSV

From the repo root:

```bash
curl -F "file=@data/sample/transactions_sample.csv" http://localhost:8080/transactions/upload
```

### 5. Query ingestion history

```bash
curl -s http://localhost:8080/ingestions | python3 -m json.tool
```

Example endpoints:

```text
GET /ingestions
GET /ingestions/{id}
GET /ingestions/{id}/accepted
GET /ingestions/{id}/rejected
```

## Java Ingestion Service

Location:

```text
app/java-ingestion-service
```

The Java Ingestion Service receives CSV data, validates each row, writes accepted and rejected outputs, generates reconciliation reports and persists ingestion results to Postgres.

Current endpoints include:

```text
GET  /health
GET  /actuator/health
POST /transactions/validate
POST /transactions/upload
GET  /ingestions
GET  /ingestions/{id}
GET  /ingestions/{id}/accepted
GET  /ingestions/{id}/rejected
```

## Local Database

The project uses a local Postgres database through Docker Compose.

Postgres acts as the operational audit store.

It stores:

- ingestion metadata
- accepted records
- rejected records
- reconciliation report references

Connect to Postgres:

```bash
docker exec -it banking-refinery-postgres psql -U refinery_user -d banking_refinery
```

Database schema changes are managed through Flyway migrations located in:

```text
app/java-ingestion-service/src/main/resources/db/migration
```

## SQL Data Quality Checks

Postgres data quality checks live in:

```text
sql/postgres/data-quality/
```

Run all checks manually:

```bash
./scripts/run-postgres-quality-checks.sh
```

Most checks use the pattern:

```text
zero rows = pass
one or more rows = investigate
```

The checks validate:

- ingestion reconciliation
- duplicate accepted records
- invalid currencies
- invalid statuses
- invalid amounts
- rejected records without reasons
- missing reconciliation reports

## CI-Friendly Data Quality Checks

Run the CI-friendly quality gate:

```bash
./scripts/run-postgres-quality-checks-ci.sh
```

This script exits with:

```text
0 = all checks passed
1 = one or more checks returned failing rows
```

The summary query `99_ingestion_quality_summary.sql` is excluded from the CI gate because it is expected to return rows.

## Jenkins CI Pipeline

This repository includes a root-level `Jenkinsfile`.

The pipeline currently:

- verifies required tools
- starts a clean CI Postgres container
- runs Flyway database migrations
- runs Java tests
- starts the Java service
- uploads a sample CSV
- validates the API response
- checks expected records were written to Postgres
- runs CI-friendly SQL data quality checks
- publishes test results
- archives logs and E2E output files

The pipeline represents the project validation process as code.

### Running Jenkins Locally

Start Jenkins locally with Docker:

```bash
docker compose -f docker-compose.jenkins.yml up -d --build
```

Open Jenkins at:

```text
http://localhost:8081
```

The local Jenkins setup uses a custom Jenkins image with Docker CLI installed so the pipeline can start containers and run the quality checks.

For more detail, see:

```text
jenkins/README.md
```

## End-to-End Validation

The project includes an end-to-end ingestion validation script:

```bash
./scripts/run-e2e-ingestion-check.sh
```

This script:

1. starts the Java service
2. waits for the health endpoint
3. uploads the sample CSV
4. validates the upload response
5. queries the ingestion detail API
6. checks accepted, rejected and reconciliation records in Postgres

This proves that the API, validation layer, database persistence and query API work together.

## BigQuery Warehouse Design

The project includes an initial BigQuery warehouse design with three layers:

```text
raw → curated → mart
```

BigQuery SQL lives in:

```text
sql/bigquery/
```

The warehouse design separates operational ingestion tracking from analytical reporting.

Postgres is used for ingestion audit history.

BigQuery is designed for future analytical reporting and dashboard-ready datasets.

For more detail, see:

```text
docs/warehouse-design.md
docs/bigquery.md
```

## Project Documentation

- [Requirements](docs/requirements.md)
- [Architecture](docs/architecture.md)
- [Data Flow](docs/data-flow.md)
- [Runbook](docs/runbook.md)
- [Database](docs/database.md)
- [Data Quality](docs/data-quality.md)
- [Warehouse Design](docs/warehouse-design.md)
- [BigQuery Design](docs/bigquery.md)
- [ADR-001](docs/decisions/ADR-001.md)
- [ADR-002 Repository Structure](docs/decisions/ADR-002-repository-structure.md)
- [Raw Data Schema](data/schemas/transactions_raw_schema.md)

## Current Status

This project currently implements a local end-to-end ingestion and data quality workflow.

Completed features include:

- Spring Boot CSV ingestion API
- row-level validation
- accepted and rejected output generation
- reconciliation report generation
- Postgres audit persistence
- ingestion query API
- Flyway database migrations
- SQL data quality checks
- CI-friendly quality gate scripts
- Jenkins pipeline-as-code
- local Jenkins execution
- end-to-end ingestion validation
- initial BigQuery warehouse layer design

The project is intentionally local-first and zero-cost.

BigQuery and Terraform implementation are documented as future extensions.

## What This Demonstrates

This project demonstrates:

- backend API development with Java and Spring Boot
- database schema management with Flyway
- operational audit design using Postgres
- SQL-based data quality validation
- CI/CD pipeline design with Jenkins
- Docker-based local development
- end-to-end validation using shell scripts
- layered warehouse design using raw, curated and mart concepts
- separation of operational storage and analytical warehouse design

## Transferable Engineering Patterns

Although the sample dataset is transaction-based, the engineering patterns are sector-neutral.

The same approach could be applied to:

- order ingestion
- customer data validation
- product catalogue feeds
- supplier data processing
- inventory updates
- event logs
- reporting extracts
- operational data quality checks

The core platform pattern is:

```text
ingest data
validate data
separate accepted and rejected records
record rejection reasons
persist audit metadata
expose query APIs
run SQL quality checks
enforce checks through CI/CD
design analytical warehouse layers
```

## Future Improvements

Planned improvements include:

- deploy BigQuery datasets with Terraform
- load accepted records into BigQuery
- add scheduled warehouse transformations
- add reporting dashboards
- add Testcontainers-based integration tests
- add file checksum tracking
- add duplicate file detection
- improve reconciliation reporting
- add configurable validation rules
- support multiple dataset types
