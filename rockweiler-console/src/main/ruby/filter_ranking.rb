require 'json'

class FilterRanking
  ROOT = '/Users/Danil/Dropbox/OOOL/data/2015/draft'
end

source = File.expand_path("oool.vorp.json", FilterRanking::ROOT)
json = File.read(source)
report = JSON.parse(json)

scores = {}

rank = 0;
report.each do |player|
  rank += 1
  player['id'].each do |k, v|
    scores[k] ||= {}
    scores[k][v] = rank
  end
end

source = File.expand_path("oool.keeper.json", FilterRanking::ROOT)
json = File.read(source)
report = JSON.parse(json)

sort_order = {}

report.each do |player|
  crnt = 999
  player['id'].each do |k, v|
    if scores.has_key? k
      if scores[k].has_key? v
        crnt = [crnt, scores[k][v]].min
      end
    end
  end
  sort_order[player] = crnt
end

sort_order.sort_by { |player, s| s }.each do |player, s|
  puts "p %s" % player['bio']['name']
end

