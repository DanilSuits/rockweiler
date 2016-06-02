#!/usr/bin/env bash

URL=${1}
shift

. $(dirname ${0})/env.sh

ROSTER_DATA=${OOOL}/rosters.$(date +%Y%m%d)
LINK=${OOOL}/current.rosters

ruby ${ROCKWEILER_RUBY}/fetch_oool_rosters.rb ${URL} > ${ROSTER_DATA}

(cd ${OOOL} ; ln -nsf $(basename ${ROSTER_DATA}) ${LINK})