# Data Flow - Banking Refinery Platform

## 1. Overview

This document explains how transaction data moves through the platform from raw input to business-ready reporting.

## 2. Data Flow

### Step 1 - Raw File Received

A raw transaction file is received by the Java ingestion service.

Example file:

transactions_2026_06_21.csv

### Step 2 - Java Validation

The Java service reads each row and validates the transaction fields.

Validation checks include:

- transaction ID exists
- customer ID exists
- amount is numeric
- currency is supported
- transaction timestamp is valid
- risk score is between 0 and 100

### Step 3 - Accepted and Rejected Records

Valid records are written to the accepted output.

Invalid records are written to the rejected output with a reason.

Example rejection reasons:

- missing_transaction_id
- invalid_amount
- unsupported_currency
- missing_customer_id
- future_transaction_date

### Step 4 - Raw BigQuery Load

Accepted records are loaded into the raw BigQuery dataset.

Dataset:

banking_raw

Table:

transactions_raw

### Step 5 - SQL Transformation

SQL is used to clean and standardise the raw data.

Examples:

- convert timestamps to dates
- standardise currency codes
- remove duplicates
- join transactions to customer and merchant data

### Step 6 - Curated Dataset

Cleaned data is stored in the curated layer.

Dataset:

banking_curated

Table:

transactions_clean

### Step 7 - Reporting Marts

Business-ready tables are created for analysis.

Examples:

- daily transaction summary
- failed payment summary
- high-risk transaction report
- customer activity summary

## 3. Reconciliation

The platform should track:

- number of source rows
- number of accepted rows
- number of rejected rows
- rejection reasons
- total transaction value before and after processing

## 4. Why This Matters

Financial systems require control, traceability and accuracy.

A data pipeline is not just about moving data. It must prove that data was processed correctly.
