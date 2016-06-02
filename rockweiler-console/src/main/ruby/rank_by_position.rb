require 'json'

class RankByPosition
end

json = File.read('/Users/Danil/Dropbox/OOOL/data/2016/database/2016.bbref.players.json')
db = {}
JSON.parse(json).each do |player|
  bbref = player['id']['bbref']
  db[bbref] = player
end

positions = {}

ranks = {'OF' => {}, 'SP' => {}, 'RP' => {}, 'XX' => {}, 'multi' => {}}

%w(1B 2B 3B SS C).each do |p|
  positions[p] = p
  ranks[p] = {}
end

%w(LF CF RF).each do |p|
  positions[p] = 'OF'
end

hitters = {}

json = File.read('/Users/Danil/Dropbox/OOOL/data/2015/season/gamelogs.json')
JSON.parse(json).each do |player|

  bbref = player['id']['bbref']
  hitters[bbref] ||= []

  fielding = {}
  player['games'].each do |game|
    if "H".eql? game['score']['type']
      position_hint = game['hints']['pos']
      position_hint.split(" ").each do |p|
        if positions.key? p
          pos = positions[p]
          fielding[pos] ||=0
          fielding[pos] = fielding[pos] + 1
        end

      end
    end
  end

  fielding.each do |pos, count|
    if (count > 19 )
      hitters[bbref] << pos
    end
  end
end



json = File.read('/Users/Danil/Dropbox/OOOL/data/2015/season/scores.json')
JSON.parse(json).each do |id, report|
  player = db[id]

  totals = report['totals']
  if totals.key? 'H'
    score = totals['H']

    ranks['XX'][id] = score

    if hitters[id].length > 1
      ranks['multi'][id] = score

      hitters[id].each do |pos|
        k = "multi.#{pos}"
        ranks[k] ||= {}
        ranks[k][id] = score
      end
    end

    hitters[id].each do |pos|
      ranks[pos][id] = score
    end

  end

  %w(SP RP).each do |pos|
    k = pos[0]
    if totals.key? k
      score = totals[k]
      ranks[pos][id] = score
    end
  end
end

ranks.each do |pos, scores|
  ranked = []
  scores.sort_by {|k,v| v}.reverse.each do |id, score|
    ranked << db[id]
  end

  report = "/Users/Danil/Dropbox/OOOL/data/2016/draft/position.#{pos}.oool.json"
  File.open(report, 'w') do |file|
    file.write(JSON.pretty_generate(ranked))
  end
end