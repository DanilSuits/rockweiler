#!/usr/bin/env bash

YEAR=${1:-$(date +%Y)}

DB=../data/${YEAR}
SEASON=${DB}/season
BBREF=${DB}/bbref

for PROBLEM in "Gateway Time-out" "Internal Server Error" ; do
    find ${BBREF} -type f -exec grep -l "${PROBLEM}" {} \; | while read FILE; do
        echo "${FILE} : ${PROBLEM}"
        rm ${FILE}
    done
done

set -e

#TODO: make these transactional
ruby rockweiler-console/src/main/ruby/parse_game_logs.rb  ${YEAR} > ${SEASON}/gamelogs.json
ruby rockweiler-console/src/main/ruby/scores_report.rb    ${YEAR} > ${SEASON}/scores.report
ruby rockweiler-console/src/main/ruby/aggregate_scores.rb ${YEAR} > ${SEASON}/scores.json
ruby rockweiler-console/src/main/ruby/season_report.rb    ${YEAR}

find ${SEASON} -type f -ls | sort -n -k 7
echo
ls -lrt ${SEASON}