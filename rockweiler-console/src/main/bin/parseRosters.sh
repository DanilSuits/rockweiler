#!/usr/bin/env bash

. $(dirname ${0})/env.sh

assignedPlayers () {
    ruby ${ROCKWEILER_RUBY}/parse_oool_roster.rb ${OOOL}/current.rosters
}


readGameLogs () {
    ruby ${ROCKWEILER_RUBY}/find_aliases.rb ${SEASON}/gamelogs.json

}

publishEvents () {
    assignedPlayers
    cat ${OOOL}/current.overrides
    readGameLogs
    cat ${OOOL}/alias.overrides
}

publishEvents |
tee ${EVENTS}/current.roster.events |
ruby ${ROCKWEILER_RUBY}/report_player_assignments.rb |
tee ${SEASON}/oool.rosters.json