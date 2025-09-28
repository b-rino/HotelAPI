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

# Slet payload automatisk når scriptet afsluttes
trap 'rm -f room-payload.json' EXIT

# Opret JSON payload
cat > room-payload.json <<EOF
{
  "hotelId": $HOTEL_ID,
  "number": "$ROOM_NUMBER",
  "price": $PRICE
}
EOF

# Send POST-request til API
curl -X POST \
     -H "Content-Type: application/json" \
     -d @room-payload.json \
     http://localhost:7000/api/v1/room
