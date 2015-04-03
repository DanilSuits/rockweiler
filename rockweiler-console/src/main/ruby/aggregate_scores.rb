require 'json'
require 'date'

class AggregateScores
  ROOT = '/Users/Danil/Dropbox/OOOL/data/2014/season'


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

report = {"H" => {}, "S" => {}, "R" => {}}

scorers = Hash.new { |hash, key| hash[key] = MockScorer.new}
scorers["R"] = ReliefScorer.new
scorers["S"] = StartScorer.new


db.each do |player|

  lahman = player["id"]["lahman"]

  scores = { "H" => {}, "S" => {}, "R" => {} }

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
      report[k][lahman] = total
    end
  end
end

report.each do |type, players|
  rank = 0
  players.sort_by {|id, total| total}.reverse.each do |id, total|
    rank +=1
    puts "%s, %d, %s, %d" % [type, rank, id, total]
  end
end
