#!/bin/bash

# Brug: ./call-user-demo.sh <token>
TOKEN=$1

if [ -z "$TOKEN" ]; then
  echo "Brug: $0 <token>"
  exit 1
fi

curl -s -X GET https://hotel.brino.dk/api/v1/protected/user_demo \
  -H "Authorization: Bearer $TOKEN"
