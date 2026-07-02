# Java Ingestion Service

## Purpose

This service is responsible for receiving raw banking transaction data, validating records and preparing accepted and rejected outputs for the wider Banking Refinery Platform.

## Current Capabilities

- Starts as a Spring Boot application
- Exposes a custom health endpoint at `/health`
- Exposes Spring Actuator health endpoint at `/actuator/health`
- Includes a basic unit test

## Run Locally

### Bash
```bash
mvn spring-boot:run
```
### PowerShell
```bash
.\mvnw.cmd spring-boot:run
```
## Transaction Validation Endpoint

### Endpoint

`POST /transactions/validate`

### Purpose

Validates a single transaction request before it is accepted into the refinery platform.

### Validation Rules

The service currently checks:

- transaction ID is required
- customer ID is required
- account ID is required
- transaction timestamp is required
- amount must be greater than zero
- currency must be supported
- status must be supported
- risk score must be between 0 and 100
- transaction timestamp cannot be in the future

### Example Request

```json
{
  "transactionId": "TXN-1001",
  "customerId": "CUST-001",
  "accountId": "ACC-001",
  "transactionTimestamp": "2026-06-27T10:15:30",
  "merchantId": "MERCH-001",
  "merchantCategory": "GROCERY",
  "amount": 42.50,
  "currency": "GBP",
  "country": "GB",
  "status": "APPROVED",
  "riskScore": 20
}

## CSV Upload Endpoint

### Endpoint

`POST /transactions/upload`

### Purpose

Accepts a CSV file containing transaction records, validates each row and returns an ingestion summary.

### Expected CSV Headers

```text
transaction_id,customer_id,account_id,transaction_timestamp,merchant_id,merchant_category,amount,currency,country,status,risk_score