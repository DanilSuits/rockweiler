#!/usr/bin/env bash

YEAR=${1:-$(date +%Y)}

DB=$(cd ../data/${YEAR}; pwd)
SEASON=${DB}/season
EVENTS=${DB}/events

mkdir -p ${EVENTS}

UPDATE=${EVENTS}/pool.crnt.events

# http://stackoverflow.com/questions/1055671/how-can-i-get-the-behavior-of-gnus-readlink-f-on-a-mac
# greadlink -f pool.crnt.events
SOURCE=${UPDATE}

TARGET=${EVENTS}/pool.$(date +%s).events


# TODO: ???
BIN=$(cd $(dirname ${0}) ; pwd)
RUBY=$(cd ${BIN}/../ruby ; pwd)

GAMELOG=${SEASON}/gamelogs.json
ruby ${RUBY}/generate_gamelog_events.rb ${GAMELOG} |
ruby ${RUBY}/update_bb_ref_pool.rb ${SOURCE} > ${TARGET}

awk 'FNR==NR{a[$0]++;next}(!($0 in a))' ${SOURCE} ${TARGET}

(cd ${EVENTS} ; ln -nsf $(basename ${TARGET}) ${UPDATE})


