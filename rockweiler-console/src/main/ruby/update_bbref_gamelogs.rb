require 'json'
require 'net/http'
require 'mechanize'
require 'set'

class UpdateBbrefGamelogs
  def initialize(year)
    @year = year
  end

  def run
    mechanize = Mechanize.new

    positions = {}

    [:pitching, :batting].each do |pos|
      positions[pos] = Set.new

      [:AL, :NL].each do |league|
        local_file = File.expand_path("../data/#{@year}/bbref/league.#{league}.pos.#{pos}.html")

        uri = "file://#{local_file}"
        page = mechanize.get(uri)

        table = page.search("//table[@id='players_standard_#{pos}']/tbody")
        table.search('tr').each do |r|
          if r.search('td')[1].nil?
            next
          end

          r.search('td')[1].search('a').each do |link|
            /\/([^\/]+).shtml/.match(link['href']) do |m|
              positions[pos].add m[1]
            end
          end

        end
      end
    end

    positions.each do |pos, players|
      filter(players).each do |id|

        uri = 'http://www.baseball-reference.com/players/gl.cgi?id=%s&t=%s&year=%s' % [id, pos[0, 1], @year]
        local_file = File.expand_path('%s.gamelog.%s.html' % [pos, id], "../data/#{@year}/bbref/")

        update = {:remoteUri => uri, :localDestination => local_file}

        req = Net::HTTP::Post.new('/updates', initheader = {'Content-Type' => 'application/json'})
        req.body = update.to_json

        response = Net::HTTP.new('localhost', 8080).start { |http| http.request(req) }
      end
    end

  end

  def filter(players)
    players
  end

end

class Redquarx < UpdateBbrefGamelogs
  def initialize(year, team)
    super(year)
    @team = team
  end

  def filter(players)
    players.select { |id| @team.include? id}
  end
end

year = ARGV[0]


roster = File.expand_path('redquarx.ids', "../data/#{year}/season/")

team = []

Dir["../data/#{year}/season/*.ids"].each do |roster|
  team << IO.readlines(roster).map(&:chomp)
end

task = UpdateBbrefGamelogs.new(year)
# task = Redquarx.new(year, team.flatten!)

task.run
