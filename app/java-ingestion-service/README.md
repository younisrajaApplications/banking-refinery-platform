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
