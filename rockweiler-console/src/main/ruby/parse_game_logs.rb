require 'mechanize'
require 'logger'

class ParseGameLogs
  YEAR = ARGV[0]
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/bbref"

  def initialize(mechanize)
    @mechanize = mechanize

    @decision_scores = {"W" => 5, "BW" => 5, "L" => -3, "BL" => -3, "S" => 2, "BS" => 0}
  end

  def pitchers
    Dir.glob("#{ROOT}/pitching.gamelog.*.html").each do |file|
      /pitching.gamelog.([^\\.]+).html/.match(file) do |m|
        yield m[1], m[0]
      end
    end
  end

  def hitters
    Dir.glob("#{ROOT}/batting.gamelog.*.html").each do |file|
      /batting.gamelog.([^\\.]+).html/.match(file) do |m|
        yield m[1], m[0]
      end
    end
  end


  def parse_hitting(file)
    load_page(file) do |page|
      gamelog = {:games => [], :source => page.uri, }

      rows = page.search("//table[@id='batting_gamelogs']//tr[starts-with(@id,'batting_gamelogs')]")
      rows.each do |r|
        game = {:hints => {}, :score => {:points => 0}, :stats => {}}

        columns = r.search("td")
        game[:gameId] = columns[3].attribute("csk")

        game[:stats][:AB] = columns[10].text.to_i
        game[:stats][:R] = columns[11].text.to_i
        game[:stats][:H] = columns[12].text.to_i
        game[:stats]["2B"] = columns[13].text.to_i
        game[:stats]["3B"] = columns[14].text.to_i
        game[:stats][:HR] = columns[15].text.to_i
        game[:stats][:RBI] = columns[16].text.to_i
        game[:stats][:BB] = columns[17].text.to_i

        game[:stats][:SB] = columns[25].text.to_i
        game[:stats][:CS] = columns[26].text.to_i

        # Batting Points = ToB + TB + (SB-CS) + (R+RBI) - OUTS
        # = 2*H + W + TB + R + RBI + SB - CS - AB

        points = 2 * game[:stats][:H]
        points += game[:stats][:BB]
        points += game[:stats][:H] + game[:stats]["2B"] + 2 * game[:stats]["3B"] + 3 * game[:stats][:HR]
        points += game[:stats][:R] + game[:stats][:RBI]
        points += game[:stats][:SB] - game[:stats][:CS]
        points -= game[:stats][:AB]

        game[:score][:type] = "H"
        game[:score][:points] = points


        gamelog[:games] << game
      end

      gamelog
    end
  end

  def load_page(file)
    html = File.expand_path(file, ROOT)

    if not File.exists?(html)
      raise "Where is the file #{html}"
    end

    uri = "file://#{html}"
    yield @mechanize.get(uri)

  end

  def parse_pitching(file)
    html = File.expand_path(file, ROOT)

    if not File.exists?(html)
      raise "Where is the file #{html}"
    end

    uri = "file://#{html}"
    page = @mechanize.get(uri)
    rows = page.search("//table[@id='pitching_gamelogs']//tr[starts-with(@id,'pitching_gamelogs')]")

    gamelog = {:games => [], :source => uri, }

    rows.each do |r|
      game = {:hints => {}, :score => {:points => 0}, :stats => {}}

      columns = r.search("td")
      game[:gameId] = columns[3].attribute("csk")

      game[:hints][:innings] = columns[8].text
      game[:hints][:decision] = columns[9].text
      game[:hints][:totalIP] = columns[11].text
      game[:hints][:gameScore] = columns[30].text

      game[:stats][:H] = columns[12].text.to_i
      game[:stats][:R] = columns[13].text.to_i
      game[:stats][:ER] = columns[14].text.to_i
      game[:stats][:BB] = columns[15].text.to_i
      game[:stats][:SO] = columns[16].text.to_i

      outs = game[:hints][:totalIP].split(".")
      game[:stats][:outs] = 3 * outs[0].to_i + outs[1].to_i

      game[:score][:type] = "R"
      ["GS", "CG", "SHO"].each do |h|
        if game[:hints][:innings].start_with?(h)
          game[:score][:type] = "S"
        end
      end

      points = game[:stats][:outs]

      points -= 2 * (game[:stats][:H] + game[:stats][:R] + game[:stats][:ER])
      points += game[:stats][:SO] - game[:stats][:BB]

      bonus = 2 * [outs[0].to_i - 4, 0].max
      points += bonus

      /^([^(]+)/.match(game[:hints][:decision]) do |m|
        if @decision_scores.key?(m[1])
          points += @decision_scores[m[1]]
        end
      end
      game[:score][:points] = points

      gamelog[:games] << game
    end

    gamelog

  end

  def parse_bio(file)
    html = File.expand_path(file, ROOT)

    if not File.exists?(html)
      raise "Where is the file #{html}"
    end

    uri = "file://#{html}"
    page = @mechanize.get(uri)

    bio = {}
    bio["name"] = page.search("span[@id=player_name]").text
    bio["dob"] = page.search("span[@id=necro-birth]").attribute("data-birth").to_s.gsub("-", "")
    bio
  end

end

mechanize = Mechanize.new

db = []

gamelogs = ParseGameLogs.new(mechanize)
gamelogs.pitchers do |id, html|
  player = {:id => {:bbref => id}}
  player[:bio] = gamelogs.parse_bio(html)
  player.merge! gamelogs.parse_pitching(html)
  db << player
end

gamelogs.hitters do |id, html|
  player = {:id => {:bbref => id}}
  player[:bio] = gamelogs.parse_bio(html)
  player.merge! gamelogs.parse_hitting(html)
  db << player
end

puts JSON.pretty_generate(db)
