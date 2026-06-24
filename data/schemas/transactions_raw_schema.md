# Raw Transaction Schema

This schema defines the expected structure of the raw transaction file.

| Column Name | Data Type | Required | Description |
|---|---|---|---|
| transaction_id | string | yes | Unique transaction identifier |
| customer_id | string | yes | Customer identifier |
| account_id | string | yes | Account identifier |
| transaction_timestamp | timestamp | yes | Date and time of transaction |
| merchant_id | string | yes | Merchant identifier |
| merchant_category | string | yes | Type of merchant |
| amount | decimal | yes | Transaction amount |
| currency | string | yes | Transaction currency |
| country | string | yes | Country of transaction |
| status | string | yes | Transaction status |
| risk_score | integer | no | Risk score from 0 to 100 |
| created_at | timestamp | yes | Time the record was created |

## Example Status Values

- APPROVED
- DECLINED
- PENDING
- REVERSED

## Example Currency Values

- GBP
- USD
- EUR

## Validation Rules

1. transaction_id must not be empty.
2. customer_id must not be empty.
3. account_id must not be empty.
4. amount must be numeric.
5. currency must be one of the approved values.
6. status must be one of the approved values.
7. risk_score must be between 0 and 100.
8. transaction_timestamp must not be in the future.
