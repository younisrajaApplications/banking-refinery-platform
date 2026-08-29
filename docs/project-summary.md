# Project Summary

The Banking Refinery Platform is a local-first data ingestion and quality validation platform.

The project simulates how a data engineering team might ingest raw transaction files, validate records, persist ingestion metadata, run SQL quality checks and enforce those checks through CI/CD.

The main purpose of the project is to demonstrate backend engineering, SQL, database design, Jenkins pipeline-as-code, Docker-based local development and warehouse design principles.

## Completed Capabilities

- CSV upload API
- transaction validation
- accepted and rejected output files
- reconciliation reporting
- Postgres persistence
- ingestion query API
- Flyway migrations
- SQL data quality checks
- CI-friendly quality gate
- Jenkins pipeline
- end-to-end ingestion validation
- BigQuery warehouse design

## Future Improvements

- deploy BigQuery datasets with Terraform
- load accepted transactions into BigQuery
- add scheduled warehouse transformations
- add reporting dashboards
- add Testcontainers-based integration tests
- add file checksum and duplicate detection