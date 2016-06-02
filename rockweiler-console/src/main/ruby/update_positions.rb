require 'json'

class UpdatePositions
  THISYEAR = ARGV[0].to_i
  LASTYEAR = THISYEAR - 1

  def initialize
    @positions = {}

    %w(1B 2B 3B SS C).each do |p|
      @positions[p] = p
    end

    %w(LF CF RF).each do |p|
      @positions[p] = 'OF'
    end

    @eligible = {}
  end

  def add(position, player)
    @eligible[position] ||= {}

    bbref = player['id']['bbref']
    bio = player['bio']

    @eligible[position][bbref] = { :bio => bio }
  end

  def readGameLog(year)

    json = File.read("/Users/Danil/Dropbox/OOOL/data/#{year}/season/gamelogs.json")
    JSON.parse(json).each do |player|

      bbref = player['id']['bbref']

      fielding = {}
      player['games'].each do |game|
        if "H".eql? game['score']['type']
          position_hint = game['hints']['pos']
          position_hint.split(" ").each do |p|
            if @positions.key? p
              pos = @positions[p]
              fielding[pos] ||=0
              fielding[pos] = fielding[pos] + 1
            end

          end
        end
      end

      fielding.each do |pos, count|
        if (count > 19)
          add(pos,player)
        end
      end
    end

  end

  def eligibles
    return @eligible
  end
end

update = UpdatePositions.new
update.readGameLog(UpdatePositions::LASTYEAR)
update.readGameLog(UpdatePositions::THISYEAR)

puts JSON.pretty_generate(update.eligibles)
