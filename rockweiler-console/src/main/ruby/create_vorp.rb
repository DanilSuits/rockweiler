require 'json'

class CreateVorp
  SEASON = '/Users/Danil/Dropbox/OOOL/data/2014/season'
  DRAFT = '/Users/Danil/Dropbox/OOOL/data/2015/draft'
end

source = File.expand_path("fielding.json", CreateVorp::DRAFT)
json = File.read(source)
season = JSON.parse(json)

catchers = []

season["C"].each do |lahman|
  catchers << lahman
end

replacement = {"H" => 40, "S" => 100, "R" => 140}

source = File.expand_path("season.scores.json", CreateVorp::SEASON)
json = File.read(source)
season = JSON.parse(json)

comment = {}
vorp = {}

season.each do |lahman, scores|
  scores.sort_by {|t, total| total}.reverse.first(1).each do |t, total|
    value = total - replacement[t]
    hint = "hint: %s %s:%d - %d = %d" % [lahman, t, total, replacement[t], value]

    if "S".eql? t
      value = (total  - replacement[t]) * 8 / 10
      hint = "hint: %s %s:.8(%d - %d) = %d" % [lahman, t, total, replacement[t], value]
    end

    if catchers.include? lahman
      value += 25
      hint = "hint: %s %s:%d - %d + 25 (C) = %d" % [lahman, t, total, replacement[t], value]
    end
    comment[lahman] = hint
    vorp[lahman] = value
  end
end

vorp.sort_by {|lahman, total| total}.reverse.each do |lahman, total|
  puts comment[lahman]
  puts "p %s" % [lahman]
end
