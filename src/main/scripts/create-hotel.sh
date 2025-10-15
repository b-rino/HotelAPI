#!/bin/bash

# Brug: ./create-hotel.sh "<name>" "<address>"

NAME=$1
ADDRESS=$2

# Tjek om begge argumenter er givet
if [ $# -ne 2 ]; then
  echo "Brug: $0 \"<name>\" \"<address>\""
  exit 1
fi

# Send POST-request med JSON payload
curl -s -X POST https://hotel.brino.dk/api/v1/hotel \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"$NAME\",
    \"address\": \"$ADDRESS\"
  }"
