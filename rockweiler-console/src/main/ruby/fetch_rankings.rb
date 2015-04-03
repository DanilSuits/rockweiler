require 'net/http'
require 'uri'
require 'json'

class FetchRankings
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
        if /<\/table>/.match(line)
          break
        end

        if match = /http:\/\/www.rotoworld.com\/player\/MLB\/([[:digit:]]+)/.match(line)
          rank += 1
          id = { "pos" => k, "rank" => rank, "player" => { "id" => { "rotoworld" => match.captures[0]} } }
          players.push id
        end
      end

      players.each do |x|
        puts x.to_json
      end

    end

  end
end

ranks = {}
ranks['SP'] = 'http://www.rotoworld.com/articles/mlb/47618/2/july-starter-rankings'
ranks['RP'] = 'http://www.rotoworld.com/articles/mlb/47614/2/july-reliever-rankings'
ranks['OF'] = 'http://www.rotoworld.com/articles/mlb/47616/2/july-outfielder-rankings'
ranks['1B'] = 'http://www.rotoworld.com/articles/mlb/47611/2/july-first-baseman-rankings'
ranks['2B'] = 'http://www.rotoworld.com/articles/mlb/47612/2/july-second-baseman-rankings'
ranks['SS'] = 'http://www.rotoworld.com/articles/mlb/47615/2/july-shortstop-rankings'
ranks['C'] = 'http://www.rotoworld.com/articles/mlb/47610/2/july-catcher-rankings'
ranks['3B'] = 'http://www.rotoworld.com/articles/mlb/47613/2/july-third-baseman-rankings'
ranks['Top'] = 'http://www.rotoworld.com/articles/mlb/47739/2/2015-top-300-overall'
FetchRankings.new.fetch(ranks)