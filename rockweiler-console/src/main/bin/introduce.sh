#/bin/bash

# ruby rockweiler-console/src/main/ruby/introduce_player.rb Byung Ho Park > ../data/2016/database/introduce.players.json.$(date +%s)
ruby rockweiler-console/src/main/ruby/introduce_player.rb "$@" | tee ../data/2016/database/introduce.players.json.$(date +%s)

