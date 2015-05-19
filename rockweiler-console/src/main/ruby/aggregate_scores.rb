require 'json'
require 'date'

class AggregateScores
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{ARGV[0]}/season"


end

class MockScorer
  def score(scores)
    total = 0
    scores.each do | score |
        total += score
    end
    total
  end
end

class ReliefScorer
  def score(scores)
    total = 0
    scores.each do | score |
      total += [score, -1].max
    end
    total
  end
end

class StartScorer
  def score(scores)
    local = scores.dup
    local << 0
    return local.max
  end
end

class HittingScorer
  def score(scores)
    local = scores.dup
    while local.length < 7
      local << 0
    end
    
    local.sort!
    local.reverse!

    total = 0
    local.slice(0,5).each do |score|
      total += score
    end
    return total
  end
end

source = File.expand_path("gamelogs.json", AggregateScores::ROOT)
json = File.read(source)
db = JSON.parse(json)

report = {}
season = {}

scorers = Hash.new { |hash, key| hash[key] = MockScorer.new}
scorers["R"] = ReliefScorer.new
scorers["S"] = StartScorer.new
scorers["H"] = HittingScorer.new


db.each do |player|

  lahman = player["id"]["bbref"]

  season[lahman] ||= { :scores => {"H" => {}, "S" => {}, "R" => {}}, :totals => {}, :id => { :bbref => lahman}, :bio => player["bio"]}

  scores = season[lahman][:scores]

  games = player["games"]
  games.each do |game|
    id = game["gameId"]
    week = DateTime.parse(id.split(".")[0]).strftime("%V")

    type = game["score"]["type"]
    points = game["score"]["points"]

    scores[type][week] ||= []

    scores[type][week] << points
  end

  scores.each do |k , v|
    total = 0
    count = 0
    v.each do |week, score_list|
      count += score_list.length
      total += scorers[k].score score_list
    end
    if count > 0
      season[lahman][:totals][k] = total
    end
  end
end

puts JSON.pretty_generate(season)

