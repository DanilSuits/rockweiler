require 'json'

class CreateRankSpreadsheet
  ROOT = '/Users/Danil/Dropbox/OOOL/data/2014/season'
end

source = File.expand_path("season.scores.json", CreateRankSpreadsheet::ROOT)
json = File.read(source)
season = JSON.parse(json)

report = []
comment = {}

ranks = { "H" => {}, "S" => {}, "R" => {} }
season.each do |lahman, scores|
  scores.sort_by { |t, total| total }.reverse.first(1).each do |t, total|
    ranks[t][lahman] = total
  end
end

ranks.each do |pos, players|
  rank = 0
  players.sort_by { |id, score| score }.reverse.each do |id, score|
    report[rank] ||= {}
    report[rank][pos] = [id, score]
    rank += 1
  end
end

report.each do |row|
  ["H", "S", "R"].each do |pos|
    if row.has_key? pos
      print "%s,%s,%d," % [ pos, row[pos][0] , row[pos][1] ]
    else
      print ",,,"
    end
  end
  puts
end






