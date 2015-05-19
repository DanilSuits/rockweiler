require 'json'
require 'net/http'
require 'mechanize'
require 'set'

class UpdateBbrefGamelogs
  YEAR = ARGV[0]
  mechanize = Mechanize.new

  players = {}

  [:pitching, :batting].each do |pos|
    players[pos] = Set.new

    [:AL, :NL].each do |league|
      local_file = File.expand_path("../data/#{YEAR}/bbref/league.#{league}.pos.#{pos}.html")

      uri = "file://#{local_file}"
      page = mechanize.get(uri)

      table = page.search("//table[@id='players_standard_#{pos}']/tbody")
      table.search("tr").each do |r|
        if r.search("td")[1].nil?
          next
        end

        r.search("td")[1].search("a").each do |link|
          /\/([^\/]+).shtml/.match(link['href']) do |m|
            players[pos].add m[1]
          end
        end

      end
    end
  end

  players.each do |pos, players|
    players.each do |id|

      uri = "http://www.baseball-reference.com/players/gl.cgi?id=%s&t=%s&year=%s" % [id, pos[0, 1], YEAR]
      local_file = File.expand_path("%s.gamelog.%s.html" % [pos, id], "../data/#{YEAR}/bbref/")

      update = {:remoteUri => uri, :localDestination => local_file}

      req = Net::HTTP::Post.new("/updates", initheader = {'Content-Type' => 'application/json'})
      req.body = update.to_json

      response = Net::HTTP.new("localhost", 8080).start { |http| http.request(req) }
    end
  end
end