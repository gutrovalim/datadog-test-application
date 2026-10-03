#!/usr/bin/env bash
# Generates a mix of successful, failing and read requests against orders-service.
URL=${1:-http://localhost:8080}
SKUS=(BOOK-1 MUG-2 SHIRT-3 OUT-OF-STOCK NOPE-9)
for i in $(seq 1 ${2:-60}); do
  sku=${SKUS[$RANDOM % ${#SKUS[@]}]}
  curl -s -o /dev/null -w "POST $sku -> %{http_code}\n" -X POST "$URL/orders" \
    -H 'Content-Type: application/json' -d "{\"sku\":\"$sku\",\"quantity\":1}"
  curl -s -o /dev/null "$URL/orders"
  sleep 0.3
done
