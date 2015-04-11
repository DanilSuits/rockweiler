require 'json'

class ReplacementLevel
  SEASON = '/Users/Danil/Dropbox/OOOL/data/2014/season'
  DRAFT = '/Users/Danil/Dropbox/OOOL/data/2015/draft'

  attr_reader :ranks

  def initialize(depth, hitting)
    @depth = depth
    @hitting = hitting
    @ranks = {}

    @depth.each do |pos, size|
      @ranks[pos] = []
    end
  end

  def submit entry, positions
    lahman = entry[0]
    oool = entry[1]

    ["C", "SS", "2B", "OF", "3B", "1B", "DH"].each do |pos|
      if !positions.include? pos
        next
      end

      if @ranks[pos].size < @depth[pos]
        @ranks[pos] << lahman
        break
      end

      sort(pos)

      if oool > @hitting[@ranks[pos][0]]
        yield @ranks[pos].shift

        @ranks[pos].unshift lahman
        break
      end
    end
  end

  def sort pos
    @ranks[pos].sort! { |lhs, rhs| @hitting[lhs] <=> @hitting[rhs] }

  end

  def report
    ["C", "SS", "2B", "3B", "OF", "1B", "DH"].each do |pos|
      sort(pos)
      lahman = @ranks[pos][0]
      yield pos, @hitting[lahman]


    end
  end

end

source = File.expand_path("season.scores.json", ReplacementLevel::SEASON)
json = File.read(source)
season = JSON.parse(json)

depth = {"DH" => 20, "C" => 30, "1B" => 20, "2B" => 20, "3B" => 20, "SS" => 20, "OF" => 80}

hitting = {}
fielding = {}

season.each do |lahman, scores|
  if scores.key? "H"
    hitting[lahman] = scores["H"]
    fielding[lahman] = ["DH"]
  end
end

db = ReplacementLevel.new(depth, hitting)


source = File.expand_path("fielding.json", ReplacementLevel::DRAFT)
json = File.read(source)
season = JSON.parse(json)

season.each do |pos, players|
  players.each do |lahman|
    if fielding.has_key? lahman
      fielding[lahman] << pos
    end
  end
end

hitters = hitting.sort_by { |p, s| s }

while hitters.any?
  entry = hitters.shift
  lahman = entry[0]
  if fielding.has_key? lahman
    db.submit(entry, fielding[lahman]) do |id|
      hitters.unshift [id, hitting[id]]
    end
  end
end

db.report do |pos, score|
  puts "%2s %d" % [pos, score]
end

