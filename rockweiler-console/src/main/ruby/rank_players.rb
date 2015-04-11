require 'json'

class RankPlayers
  ROOT = '/Users/Danil/Dropbox/OOOL/data/2014/season'
end

source = File.expand_path("season.scores.json", RankPlayers::ROOT)
json = File.read(source)
season = JSON.parse(json)

report = {}
comment = {}

season.each do |lahman, scores|
 scores.sort_by {|t, total| total}.reverse.first(1).each do |t, total|
   comment[lahman] = "%s: %3d %s" % [t, total, lahman]
   report[lahman] = total
 end
end

report.sort_by {|id, total| total}.reverse.each do |id, total|
  puts "# %s" % comment[id]
  puts "%s %s" % [:p,id]
end