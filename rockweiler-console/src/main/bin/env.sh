#!/usr/bin/env bash

YEAR=${1:-$(date +%Y)}

DB=$(cd ../data/${YEAR}; pwd)
SEASON=${DB}/season
EVENTS=${DB}/events
OOOL=${DB}/oool

ROCKWEILER_BIN=$(dirname ${0})
ROCKWEILER_RUBY=$( cd ${ROCKWEILER_BIN}/../ruby ; pwd)

