# Architecture - Banking Refinery Platform

## 1. High-Level Architecture

The Banking Refinery Platform is designed as a small enterprise-style data platform for processing raw transaction data into business-ready analytics.

```text
Raw Transaction File
        ↓
Java Ingestion Service
        ↓
Validation Layer
        ↓
Accepted / Rejected Records
        ↓
BigQuery Raw Dataset
        ↓
SQL Transformation Layer
        ↓
BigQuery Curated Dataset
        ↓
BigQuery Mart Dataset
        ↓
Business Reports
