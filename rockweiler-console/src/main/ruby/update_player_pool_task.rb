require 'optparse'
require 'json'
require_relative 'player_pools'


class UpdatePlayerPoolTask
  def self.publish(json)
    puts json
  end
end

class Projection
  def initialize
    @discovered_references = {}
  end

  def known_reference(key)
    @discovered_references.key?key
  end

  def replay(event)
    data = event['data']

    case event['eventType']
      when 'ReferenceDiscovered'
        if data['namespace'].eql? 'bbref'
          key = data['identifier']
          id = data['referenceId']

          @discovered_references[key] = id
        end
      else
    end
  end
end

options = {:year => 2016, :gamelog_year => 2015}

optparse = OptionParser.new do |opts|
  opts.on('--year year') do |env|
    options[:year] = env
  end

  opts.on('--master.year master') do |master|
    options[:master] = master
  end

  opts.on('--gamelog.year year') do |year|
    options[:gamelog_year] = year
  end
end

optparse.parse!

reference_ids = IdGenerator.new(PlayerPools.ids().get("bbref"))

projection = Projection.new

options[:master] ||= "/Users/Danil/Dropbox/OOOL/data/#{options[:year]}/database/events.master"
options[:gamelog_year] ||= options[:year]
options[:gamelog] ||= "/Users/Danil/Dropbox/OOOL/data/#{options[:gamelog_year]}/season/gamelogs.json"
File.open(options[:master]).each do |json|
  UpdatePlayerPoolTask.publish(json)
  projection.replay(JSON.parse(json))
end

json = File.read(options[:gamelog])
gamelog = JSON.parse(json)

gamelog.each do |player|
  bbref = player['id']['bbref']

  # ReferenceDiscovered
  # ReferenceMatched
  # ReferenceIdentified
  # PlayerIdentified(bio)

  # If this is the first time we have seen the bbref Id, then we need to add
  # a ReferenceDiscovered event to the history.

  events = []

  if not projection.known_reference(bbref)

    reference_id = reference_ids.get(bbref)

    reference_discovered = {:eventType => :ReferenceDiscovered }
    reference_discovered[:data] = {:referenceId => reference_id, :namespace => 'bbref', :identifier => bbref }

    events << reference_discovered

  end

  events.each do |e|
    projection.replay(e)
    UpdatePlayerPoolTask.publish(e.to_json)
  end

  if events.any?
    break
  end

end


