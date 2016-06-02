require 'json'
require 'uuidtools'
require_relative 'player_pools'

class CreatePlayerPoolTask
end

legacy_database = ARGV[0]
json = File.read(legacy_database)
db = JSON.parse(json)

pool_ids = PlayerPools::ids()
pools = {}

db.each do |player|
  command = UUIDTools::UUID.random_create

  data = {}
  player_id = UUIDTools::UUID.md5_create(command, 'playerId')

  data[:playerId] = player_id
  data[:name] = player['bio']['name']
  data[:dateOfBirth] = player['bio']['dob']

  player_introduced = {:eventType => :PlayerIntroduced, :event_id => UUIDTools::UUID.random_create }
  player_introduced[:data] = data

  events = [player_introduced]

  player['id'].each do |k,v|

    if not pools.key? k
      pools[k] = IdGenerator.new(pool_ids.get(k))
    end
    reference_id = pools[k].get(v)

    reference_matched = {:eventType => :PlayerReferenceMatched, :event_id => UUIDTools::UUID.random_create }
    reference_matched[:data] = {:referenceId => reference_id, :id => { k => v }, :playerId => player_id}
    events << reference_matched
  end

  events.each do |e|
    puts e.to_json
  end
end

