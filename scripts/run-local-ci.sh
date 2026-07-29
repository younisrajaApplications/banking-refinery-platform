#!/bin/bash

set -euo pipefail

echo "Running local CI simulation..."

echo ""
echo "Starting Postgres..."
docker compose up -d postgres
docker compose ps

echo ""
echo "Running Java tests..."
cd app/java-ingestion-service
./mvnw clean test

echo ""
echo "Returning to repo root..."
cd ../..

echo ""
echo "Running SQL data quality checks..."
./scripts/run-postgres-quality-checks-ci.sh

echo ""
echo "Local CI simulation completed successfully."