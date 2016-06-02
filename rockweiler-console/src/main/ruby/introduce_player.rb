require 'json'
require 'uuidtools'

class IntroducePlayer
end

known = {}

json = File.read('/Users/Danil/Dropbox/OOOL/data/2016/database/introduce.players.json')
JSON.parse(json).each do |player|
  id = player['id']['uuid']
  known[id] = player
end

player = { 'id' => {} }
uuid = UUIDTools::UUID.random_create
player['id']['uuid'] = uuid
player['hint'] = ARGV.join(" ")
known[uuid] = player

puts JSON.pretty_generate(known.values)
