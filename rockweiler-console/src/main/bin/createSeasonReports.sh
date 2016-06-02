#!/usr/bin/env bash

YEAR=${1:-$(date +%Y)}

ROCKWEILER_BIN=$(dirname ${0})
ROCKWEILER_RUBY=$( cd ${ROCKWEILER_BIN}/../ruby ; pwd)

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

mkdir -p ${SEASON}

#TODO: make these transactional
ruby ${ROCKWEILER_RUBY}/parse_game_logs.rb   ${YEAR} > ${SEASON}/gamelogs.json
ruby ${ROCKWEILER_RUBY}/scores_report.rb     ${YEAR} > ${SEASON}/scores.report
ruby ${ROCKWEILER_RUBY}/aggregate_scores.rb  ${YEAR} > ${SEASON}/scores.json
ruby ${ROCKWEILER_RUBY}/hitting_report.rb    ${YEAR} > ${SEASON}/hitting.report
ruby ${ROCKWEILER_RUBY}/season_spreadsheet.rb ${YEAR}

ruby ${ROCKWEILER_RUBY}/update_positions.rb ${YEAR}  > ${SEASON}/positions.json

${ROCKWEILER_BIN}/identityUpdate.sh ${YEAR}

find ${SEASON} -type f -ls | sort -n -k 7
echo
ls -lrt ${SEASON}