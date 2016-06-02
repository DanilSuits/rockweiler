#!/usr/bin/env bash

YEAR=${1:-$(date +%Y)}

ROCKWEILER_BIN=$(dirname ${0})
ROCKWEILER_RUBY=$( cd ${ROCKWEILER_BIN}/../ruby ; pwd)


ruby ${ROCKWEILER_RUBY}/update_bbref_overview.rb |
ruby ${ROCKWEILER_RUBY}/harvester_client.rb
sleep 15

ruby ${ROCKWEILER_RUBY}/update_bbref_gamelogs.rb ${YEAR} |
ruby ${ROCKWEILER_RUBY}/filter_updates.rb 2> /dev/null |
ruby ${ROCKWEILER_RUBY}/harvester_client.rb
