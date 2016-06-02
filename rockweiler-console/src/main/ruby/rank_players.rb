require 'json'

json = File.read('/Users/Danil/Dropbox/OOOL/data/2016/database/2016.bbref.players.json')
db = {}
JSON.parse(json).each do |player|
  bbref = player['id']['bbref']
  db[bbref] = player
end

replacements = {"S" => 90, "R" => 130, "H" => 90}
scores = {}

json = File.read('/Users/Danil/Dropbox/OOOL/data/2015/season/scores.json')
JSON.parse(json).each do |id, report|
  score = 0
  report['totals'].each do |k,v|
    score = [ score, v - replacements[k]].max
  end

  scores[id] = score

end

ranked = []
scores.sort_by {|k,v| v}.reverse.each do |id, score|
  ranked << db[id]
end

puts JSON.pretty_generate(ranked)