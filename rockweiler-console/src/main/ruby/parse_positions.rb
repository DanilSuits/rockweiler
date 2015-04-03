require 'csv'
require 'json'

class ParsePositions
  POSITIONS = %w[C 1B 2B 3B SS OF]
  def initialize(year_id)
    @root = '/Users/Danil/Dropbox/OOOL/data/Lahman'
    @year_id = year_id
  end

  def read_logs
    csv = File.expand_path("Fielding.csv", @root)

    players = {}

    CSV.read(csv).each do |row|
      year = row[1].to_i

      if year >= ( @year_id - 1 )
        id = row[0]
        pos = row[5]
        if /.F/.match(pos)
          pos = "OF"
        end

        games = row[6].to_i

        players[pos] ||= {}
        players[pos][id] ||= {}

        players[pos][id][year] ||= 0
        players[pos][id][year] += games
      end
    end

    fielding = {}
    POSITIONS.each do |pos|
      fielding[pos] = []

      players[pos].each do |id, data|
        eligible = false
        data.each do |year, games|
          if games > 19
            eligible = true
          end
        end

        if eligible
          fielding[pos] << id
        end
      end
      break
    end
    fielding
  end
end

parse = ParsePositions.new(2014)
fielding = parse.read_logs
puts JSON.pretty_generate(fielding)
