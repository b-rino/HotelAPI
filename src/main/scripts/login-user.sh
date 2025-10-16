#!/bin/bash

# Brug: ./login-user.sh <username> <password>
USERNAME=$1
PASSWORD=$2

if [ $# -ne 2 ]; then
  echo "Brug: $0 <username> <password>"
  exit 1
fi

echo "Logger ind som '$USERNAME'..."

curl -s -X POST https://hotel.brino.dk/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}"
