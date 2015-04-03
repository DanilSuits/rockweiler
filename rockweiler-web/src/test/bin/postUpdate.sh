#!/bin/sh

DESTINATION=${TMPDIR}kershaw.html

read -r -d '' REQUEST <<JSON
{ "remoteUri" : "http://www.rotoworld.com/player/mlb/4517/clayton-kershaw" , "localDestination" : "${DESTINATION}" }
JSON

curl -X POST -H "Content-Type: application/json" --data "${REQUEST}" http://localhost:8080/updates
