#!/usr/bin/env bash

YEAR=$(date +%Y)
MONDAY=${1:-$(date -v -monday +%Y-%m-%d)}

OWNER=${OWNER:-redquarx}
SEASON=../data/${YEAR}/season

for POS in S R H ; do
echo ${POS}
grep  week:${MONDAY} ${SEASON}/scores.report | grep -f ${SEASON}/${OWNER}.ids | grep ":${POS}" | sort -r -n
done
