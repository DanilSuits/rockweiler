require 'json'
require 'uuidtools'
require_relative 'player_pools'

class GenerateGamelogEvents
  GAMELOG = ARGV[0]

  def initialize
    @pool_bbref = IdGenerator.new(PlayerPools::ids().get('bbref'))
  end

  def players
    json = File.read(GAMELOG)
    db = JSON.parse(json)


    db.each do |player|
      yield player
    end

  end

  def events
    players do |player|
      yield playerReferenceObserved(player)
      games(player) do |event|
        yield event
      end
    end
  end

  def playerReferenceObserved(player)

    data = {}
    data[:id] = player['id']
    data[:bio] = player['bio']
    data[:referenceId] = @pool_bbref.get(data[:id]['bbref'])

    event = {:event_type => 'PlayerReferenceObserved', :data => data, :event_id => UUIDTools::UUID.random_create}
  end

  def games(player)
    player["games"].each do |game|

      data = {}
      data[:id] = player['id']
      data[:bio] = player['bio']
      data[:referenceId] = @pool_bbref.get(data[:id]['bbref'])

      data[:team] = game["hints"]["team"]

      event = {:event_type => 'GamePlayed', :data => data, :event_id => UUIDTools::UUID.random_create}
      yield event
    end
  end

end

app = GenerateGamelogEvents.new
app.events do |event|
  puts JSON.generate(event)
end