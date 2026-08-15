#!/bin/bash

set -euo pipefail

APP_DIR="${APP_DIR:-app/java-ingestion-service}"
APP_PORT="${APP_PORT:-18080}"
APP_BASE_URL="${APP_BASE_URL:-http://localhost:${APP_PORT}}"
SAMPLE_FILE="${SAMPLE_FILE:-data/sample/transactions_sample.csv}"

DB_CONTAINER="${DB_CONTAINER:-banking-refinery-postgres-ci}"
DB_USER="${DB_USER:-refinery_user}"
DB_NAME="${DB_NAME:-banking_refinery}"

SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL:-jdbc:postgresql://localhost:5433/banking_refinery}"
SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME:-refinery_user}"
SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-refinery_password}"

CI_COMPOSE_FILE="${CI_COMPOSE_FILE:-docker-compose.ci.yml}"

APP_LOG="e2e-app.log"
RESPONSE_FILE="e2e-ingestion-response.json"
DETAIL_FILE="e2e-ingestion-detail.json"

rm -f "$APP_LOG" "$RESPONSE_FILE" "$DETAIL_FILE"

APP_PID=""

cleanup() {
  if [[ -n "$APP_PID" ]]; then
    echo "Stopping Java app..."
    kill "$APP_PID" >/dev/null 2>&1 || true
    wait "$APP_PID" >/dev/null 2>&1 || true
  fi
}

trap cleanup EXIT

echo "Checking existing Java processes..."
ps -ef | grep '[j]ava' || true

if curl -sf "$APP_BASE_URL/health" >/dev/null 2>&1; then
    echo "ERROR: An application is already responding on $APP_BASE_URL"
    exit 1
fi

echo "Starting Java app for end-to-end ingestion check..."

(
  cd "$APP_DIR"

  SERVER_PORT="$APP_PORT" \
  SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
  SPRING_DATASOURCE_USERNAME="$SPRING_DATASOURCE_USERNAME" \
  SPRING_DATASOURCE_PASSWORD="$SPRING_DATASOURCE_PASSWORD" \
  ./mvnw spring-boot:run

) > "$APP_LOG" 2>&1 &

APP_PID=$!

echo "Started background process PID: $APP_PID"

echo "Waiting for Java app to become healthy..."

for attempt in {1..60}; do
  if curl -sf "$APP_BASE_URL/health" >/dev/null; then
    echo "Java app is healthy."

    echo "Application startup log:"
    tail -50 "$APP_LOG"

    break
  fi

  if ! kill -0 "$APP_PID" >/dev/null 2>&1; then
    echo "Java app exited before becoming healthy."
    cat "$APP_LOG"
    exit 1
  fi

  if [[ "$attempt" -eq 60 ]]; then
    echo "Java app did not become healthy in time."
    cat "$APP_LOG"
    exit 1
  fi

  sleep 2
done

echo "Uploading sample CSV..."

curl -sSf \
  -F "file=@${SAMPLE_FILE}" \
  "$APP_BASE_URL/transactions/upload" \
  > "$RESPONSE_FILE"

cat "$RESPONSE_FILE"
echo ""

echo "Validating upload response..."

grep -q '"fileName":"transactions_sample.csv"' "$RESPONSE_FILE"
grep -q '"totalRows":5' "$RESPONSE_FILE"
grep -q '"acceptedRows":2' "$RESPONSE_FILE"
grep -q '"rejectedRows":3' "$RESPONSE_FILE"
grep -q '"reconciled":true' "$RESPONSE_FILE"
grep -q '"processingStatus":"COMPLETED_WITH_REJECTIONS"' "$RESPONSE_FILE"

INGESTION_ID=$(
  grep -o '"ingestionFileId":[0-9]*' "$RESPONSE_FILE" \
    | head -1 \
    | cut -d ":" -f 2
)

if [[ -z "$INGESTION_ID" ]]; then
  echo "Could not extract ingestionFileId from response."
  exit 1
fi

echo "Created ingestionFileId: $INGESTION_ID"

echo "Querying ingestion detail API..."

curl -sSf \
  "$APP_BASE_URL/ingestions/${INGESTION_ID}" \
  > "$DETAIL_FILE"

cat "$DETAIL_FILE"
echo ""

echo "Validating ingestion detail response..."

grep -q '"ingestionFileId":'"$INGESTION_ID" "$DETAIL_FILE"
grep -q '"acceptedRows":2' "$DETAIL_FILE"
grep -q '"rejectedRows":3' "$DETAIL_FILE"

echo "Validating database records..."

ACCEPTED_COUNT=$(
  docker compose -f "$CI_COMPOSE_FILE" exec -T postgres \
    psql -U "$DB_USER" -d "$DB_NAME" -t -A \
    -c "SELECT COUNT(*) FROM accepted_transaction WHERE ingestion_file_id = ${INGESTION_ID};"
)

REJECTED_COUNT=$(
  docker compose -f "$CI_COMPOSE_FILE" exec -T postgres \
    psql -U "$DB_USER" -d "$DB_NAME" -t -A \
    -c "SELECT COUNT(*) FROM rejected_transaction WHERE ingestion_file_id = ${INGESTION_ID};"
)

REPORT_COUNT=$(
  docker compose -f "$CI_COMPOSE_FILE" exec -T postgres \
    psql -U "$DB_USER" -d "$DB_NAME" -t -A \
    -c "SELECT COUNT(*) FROM reconciliation_report WHERE ingestion_file_id = ${INGESTION_ID};"
)

if [[ "$ACCEPTED_COUNT" != "2" ]]; then
  echo "Expected 2 accepted transactions, got $ACCEPTED_COUNT"
  exit 1
fi

if [[ "$REJECTED_COUNT" != "3" ]]; then
  echo "Expected 3 rejected transactions, got $REJECTED_COUNT"
  exit 1
fi

if [[ "$REPORT_COUNT" != "1" ]]; then
  echo "Expected 1 reconciliation report, got $REPORT_COUNT"
  exit 1
fi

echo "End-to-end ingestion check passed."