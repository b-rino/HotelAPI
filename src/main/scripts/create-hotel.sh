#!/bin/bash

#Delete payload.json after done
trap 'rm --f payload.json' EXIT
 
# Create JSON payload for hotel
cat > payload.json <<EOF
{
  "name": "Hotel Helsingør",
  "address": "Strandvejen 25"
}
EOF

# Send POST request to your backend API
curl -X POST \
     -H "Content-Type: application/json" \
     -d @payload.json \
     http://localhost:7000/api/v1/hotel

