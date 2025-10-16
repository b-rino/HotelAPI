#!/bin/bash

# Brug: ./register-user.sh <username> <password>
USERNAME=$1
PASSWORD=$2

if [ $# -ne 2 ]; then
  echo "Brug: $0 <username> <password>"
  exit 1
fi

echo "Registrerer bruger '$USERNAME'..."

curl -s -X POST https://hotel.brino.dk/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}"
