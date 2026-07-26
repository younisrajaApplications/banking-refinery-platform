#!/bin/bash

set -euo pipefail

DB_CONTAINER="banking-refinery-postgres"
DB_USER="refinery_user"
DB_NAME="banking_refinery"
CHECK_DIR="sql/postgres/data-quality"

echo "Running Postgres data quality checks..."

for file in "$CHECK_DIR"/*.sql; do
  echo ""
  echo "==========================="
  echo "Running: $file"
  echo "==========================="

  docker exec -i "$DB_CONTAINER" \
    psql -U "$DB_USER" -d "$DB_NAME" \
    < "$file"
done

echo ""
echo "All SQL data quality checks completed."