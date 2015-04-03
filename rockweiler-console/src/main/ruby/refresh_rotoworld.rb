require 'json'

class RefreshRotoworld

  database = []

  Dir.glob("/Users/Danil/Dropbox/OOOL/data/2015/rotoworld/*.roster.html").each do |roster|
    IO::foreach(roster) do |line|
      /<td>([0-9]+)\/([0-9]+)\/([0-9]+)<\/td>/.match(line) do |dob|
        dateId = "%#04d%#02d%#02d" % [dob[3], dob[1], dob[2]]

        /href='(\/player\/mlb\/([0-9]+)\/[^']+)'>([^<+]+)/.match(line) do |m|
          database << { :bio => { :name => m[3], :dob => dateId}, :id => { :rotoworld => m[2]}}
        end
      end
    end
  end

  File.write("../data/2015/database/rotoworld.players.json", JSON.pretty_generate(database))
end

