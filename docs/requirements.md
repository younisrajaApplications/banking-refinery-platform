# Requirements - Banking Refinery Platform

## 1. Business Problem

The organisation receives raw banking transaction data from multiple source systems. This data needs to be validated, cleaned, transformed and made available for reporting and analytics.

The goal of this project is to build a small but realistic banking-style data refinery platform that demonstrates controlled data ingestion, validation, transformation and deployment using Java, SQL, BigQuery, Terraform, Jenkins and GitHub.

## 2. Users / Stakeholders

### Data Analysts
Need clean transaction data for reporting.

### Data Engineers
Need reliable pipelines and SQL transformations.

### Software Engineers
Need a maintainable Java ingestion service.

### Platform / DevOps Engineers
Need repeatable infrastructure and CI/CD automation.

### Business / Risk Teams
Need accurate reports on transaction activity, failed payments and high-risk transactions.

## 3. Functional Requirements

The system should:

1. Accept raw transaction data files.
2. Validate each transaction record.
3. Separate accepted and rejected records.
4. Store raw transaction data.
5. Transform raw data into cleaned datasets.
6. Produce business-ready reporting tables.
7. Run SQL-based data quality checks.
8. Provision warehouse resources using Terraform.
9. Run build, test and deployment steps through Jenkins.
10. Track all changes through GitHub.

## 4. Non-Functional Requirements

The system should be:

### Maintainable
Code, SQL and infrastructure should be organised clearly.

### Repeatable
Infrastructure should be recreated using Terraform rather than manual clicks.

### Testable
Java logic and SQL transformations should have validation checks.

### Auditable
Rejected rows and data quality failures should be visible.

### Secure
Secrets, credentials and sensitive files must not be committed to GitHub.

### Cost-Conscious
The project should use free-tier or local tooling where possible.

## 5. Out of Scope for Now

The first version will not include:

1. Real customer data.
2. Real banking systems.
3. Monitoring and alerting.
4. Kubernetes deployment.
5. Production-grade security controls.
6. Paid cloud resources.

## 6. Success Criteria

The project is successful when:

1. A transaction file can be processed.
2. Bad records are rejected with reasons.
3. Clean records can be loaded into BigQuery.
4. SQL transformations create reporting tables.
5. Terraform manages the BigQuery structure.
6. Jenkins runs build and validation steps.
7. The project can be explained clearly in an interview.
