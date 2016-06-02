require 'json'
require 'date'

class AggregateScores
  YEAR = "#{ARGV[0]}"
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/season"


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

source = File.expand_path('gamelogs.json', AggregateScores::ROOT)
json = File.read(source)
db = JSON.parse(json)

report = {}
season = {}

scorers = Hash.new { |hash, key| hash[key] = MockScorer.new}
scorers['R'] = ReliefScorer.new
scorers['S'] = StartScorer.new
scorers['H'] = HittingScorer.new


db.each do |player|

  lahman = player['id']['bbref']

  season[lahman] ||= { :scores => {'H' => {}, 'S' => {}, 'R' => {}}, :totals => {:season => {}, :weekly => {}}, :id => { :bbref => lahman}, :bio => player['bio']}

  scores = season[lahman][:scores]

  games = player['games']
  games.each do |game|
    id = game['gameId']
    week = DateTime.parse(id.split('.')[0]).strftime('%Y-W%V-1')

    # Convert the week key to a more human readable form
    # specifically, the date of the monday that starts the week
    gameWeek = DateTime.parse(week).strftime('%Y-%m-%d')

    type = game['score']['type']
    points = game['score']['points']

    scores[type][gameWeek] ||= []

    scores[type][gameWeek] << points
  end

  scores.each do |k , v|
    count = 0

    weekly = {}

    v.each do |week, score_list|
      count += score_list.length
      weekly[week] = scorers[k].score score_list
    end
    if count > 0
      total = 0
      weekly.each do |w , s|
        total += s
      end
      season[lahman][:totals][:season][k] = total
      season[lahman][:totals][:weekly][k] = weekly
    end
  end
end

puts JSON.pretty_generate(season)

