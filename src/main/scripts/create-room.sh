#!/bin/bash

# Brug: ./create-room.sh <hotelId> <roomNumber> <price>

HOTEL_ID=$1
ROOM_NUMBER=$2
PRICE=$3

# Tjek om alle argumenter er givet
if [ $# -ne 3 ]; then
  echo "Brug: $0 <hotelId> <roomNumber> <price>"
  exit 1
fi

# Send POST-request direkte med inline JSON
curl -s -X POST https://hotel.brino.dk/api/v1/room \
  -H "Content-Type: application/json" \
  -d "{
    \"hotelId\": $HOTEL_ID,
    \"number\": \"$ROOM_NUMBER\",
    \"price\": $PRICE
  }"
