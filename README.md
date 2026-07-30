# Banking Refinery Platform

## Overview

The Banking Refinery Platform is a hands-on engineering project that simulates a transaction data ingestion and quality validation platform for a banking environment.

The project is designed to demonstrate how raw transaction files can be ingested, validated, reconciled, stored and checked through a repeatable engineering workflow.

Current capabilities include:

* Java-based transaction ingestion service
* CSV upload and row-level validation
* Accepted and rejected transaction output files
* Reconciliation report generation
* Postgres persistence for ingestion audit history
* Query API for previous ingestion runs
* SQL-based post-ingestion data quality checks
* CI-friendly quality gate scripts
* Jenkins pipeline definition as code

## Business Problem

Banking data platforms often receive transaction data from multiple source systems. Before that data can be trusted for reporting, analytics or downstream processing, it needs to be validated and reconciled.

This project solves a simplified version of that problem by taking raw transaction CSV files and applying a controlled ingestion process.

The platform separates valid and invalid records, records why rows were rejected, generates reconciliation evidence and stores the ingestion outcome in Postgres for auditability.

## Architecture

The current platform flow is:

```text
Raw transaction CSV
        ↓
Java Ingestion Service
        ↓
Validation Layer
        ↓
Accepted and Rejected Outputs
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
```

The Java service handles the ingestion workflow, while Postgres stores the structured audit trail. SQL checks are used after ingestion to verify that the persisted data is complete, valid and explainable.

## Tech Stack

* Java
* Spring Boot
* Maven
* Postgres
* Flyway
* SQL
* Docker Compose
* Bash
* Jenkins
* GitHub

Future planned additions include:

* GCP BigQuery
* Terraform
* Jenkins pipeline expansion
* Raw, curated and reporting data layers

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
│   └── postgres/
│       └── data-quality/
├── terraform/
├── docker-compose.yml
└── README.md
```

### Key folders

* `app/` contains application code.
* `data/` contains sample files, schemas and generated local outputs.
* `docs/` contains project documentation and architecture decisions.
* `sql/` contains Postgres data quality checks.
* `scripts/` contains repeatable local and CI helper scripts.
* `jenkins/` contains Jenkins-related documentation.
* `terraform/` is reserved for future infrastructure-as-code work.

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

The Java Ingestion Service is responsible for receiving transaction data, validating records, writing accepted and rejected outputs, generating reconciliation reports and persisting ingestion results to Postgres.

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

Postgres stores:

* ingestion metadata
* accepted transactions
* rejected transactions
* reconciliation report references

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

* ingestion reconciliation
* duplicate accepted transactions
* invalid accepted currencies
* invalid accepted statuses
* invalid accepted amounts
* rejected records without reasons
* missing reconciliation reports

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

* verifies required tools
* starts Postgres using Docker Compose
* runs Java tests
* runs CI-friendly SQL data quality checks
* publishes test results
* archives Postgres logs

The pipeline represents the project validation process as code.

For more detail, see:

```text
jenkins/README.md
```

## Project Documentation

* [Requirements](docs/requirements.md)
* [Architecture](docs/architecture.md)
* [Data Flow](docs/data-flow.md)
* [Runbook](docs/runbook.md)
* [Database](docs/database.md)
* [Data Quality](docs/data-quality.md)
* [ADR-001](docs/decisions/ADR-001.md)
* [ADR-002 Repository Structure](docs/decisions/ADR-002-repository-structure.md)
* [Raw Transaction Schema](data/schemas/transactions_raw_schema.md)

## Current Status

The project currently supports a local end-to-end ingestion workflow using Java, Postgres, SQL quality checks and Jenkins pipeline-as-code.

The next major stage is to introduce cloud-style data warehousing concepts using BigQuery and infrastructure-as-code with Terraform.

## Future Improvements

Planned improvements include:

* BigQuery raw, curated and mart datasets
* Terraform-managed cloud resources
* Jenkins pipeline execution in a local Jenkins server
* automated database test setup
* richer ingestion status endpoints
* duplicate file detection
* file checksum tracking
* improved reconciliation reporting
* dashboard-ready reporting tables