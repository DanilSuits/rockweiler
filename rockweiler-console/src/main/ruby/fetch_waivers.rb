require 'net/http'
require 'uri'
require 'json'

class FetchWaivers
  def open(url)
    Net::HTTP.get(URI.parse(url))
  end

  # http://www.rotoworld.com/player/MLB/5955/Matt-Davidson
  def fetch(ranks = {})
    ranks.each do |k, v|

      players = []

      page = open(v)
      rank = 0
      page.lines.each do |line|
        if /NL ONLY/.match(line)

          line.scan(/<br \/><a href="http:\/\/www.rotoworld.com\/player\/MLB\/([[:digit:]]+)\/\S+"/).each do |rotoworld|
            rank += 1
            id = {"pos" => k, "rank" => rank, "player" => {"id" => {"rotoworld" => rotoworld[0]}}}
            players.push id
          end
        end

      end

      players.each do |x|
        puts x.to_json
      end

    end

  end
end


ranks = {}
ranks['Waivers'] = 'http://www.rotoworld.com/articles/mlb/47777/9/waiver-wired-claim-clay'
FetchWaivers.new.fetch(ranks)