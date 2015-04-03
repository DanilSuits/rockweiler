require 'csv'
require 'json'
require 'net/http'

class FetchGameLogs

  def initialize(year_id)
    @root = '/Users/Danil/Dropbox/OOOL/data/Lahman'
    @year_id = year_id
  end

  def update_repository(database, position, gamelog_type)

    csv = File.expand_path(database, @root)

    CSV.read(csv).each do |row|
      # TODO: there are duplicate rows in these files for players who get traded
      # from one team to another.  Need to add code to
      if row[1] == @year_id
        # http://www.baseball-reference.com/players/gl.cgi?id=kershcl01&t=p&year=2014
        player_id = row[0]

        uri = "http://www.baseball-reference.com/players/gl.cgi?id=%s&t=%s&year=%s" % [player_id, gamelog_type, @year_id]
        local_file = File.expand_path("%s.gamelog.%s.html" % [position, player_id], "../data/%s/bbref/" % [@year_id])

        if !File.exists?(local_file)
          puts local_file
          update = {:remoteUri => uri, :localDestination => local_file}

          req = Net::HTTP::Post.new("/updates", initheader = {'Content-Type' => 'application/json'})
          req.body = update.to_json

          response = Net::HTTP.new("localhost", 8080).start { |http| http.request(req) }

        end
      end
    end
  end

end

client = FetchGameLogs.new("2014")
client.update_repository('Pitching.csv', 'pitcher', 'p')
client.update_repository('Batting.csv', 'hitter', 'b')