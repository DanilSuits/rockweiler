#!/usr/bin/env bash

. $(dirname ${0})/env.sh

convertDraft () {
    ruby ${ROCKWEILER_RUBY}/parse_draft_report.rb
}


readGameLogs () {
    ruby ${ROCKWEILER_RUBY}/find_aliases.rb ${SEASON}/gamelogs.json

}

publishEvents () {
    convertDraft
    readGameLogs
    cat ${OOOL}/alias.overrides
}

publishEvents |
tee ${EVENTS}/current.draft.events |
ruby ${ROCKWEILER_RUBY}/report_draft_picks.rb |
tee ${SEASON}/oool.draft.json
