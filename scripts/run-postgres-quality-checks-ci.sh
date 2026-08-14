#!/bin/bash

set -euo pipefail

DB_CONTAINER="${DB_CONTAINER:-banking-refinery-postgres}"
DB_USER="${DB_USER:-refinery_user}"
DB_NAME="${DB_NAME:-banking_refinery}"
CHECK_DIR="${CHECK_DIR:-sql/postgres/data-quality}"

FAILED=0

echo "Running CI-friendly Postgres data quality checks..."

for file in "$CHECK_DIR"/0[1-7]_*.sql; do
  echo ""
  echo "======================================"
  echo "Running check: $file"
  echo "======================================"

  RESULT=$(
    docker exec -i "$DB_CONTAINER" \
      psql \
        -U "$DB_USER" \
        -d "$DB_NAME" \
        -q \
        -t \
        -A \
        -v ON_ERROR_STOP=1 \
        < "$file"
  )

  if [[ -n "$RESULT" ]]; then
    echo "FAILED: $file returned failing rows"
    echo ""
    echo "$RESULT"
    FAILED=1
  else
    echo "PASSED: no failing rows returned"
  fi
done

echo ""
echo "======================================"

if [[ "$FAILED" -eq 1 ]]; then
  echo "One or more data quality checks failed."
  exit 1
fi

echo "All data quality checks passed."
exit 0