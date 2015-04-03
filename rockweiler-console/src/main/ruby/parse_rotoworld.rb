require 'json'

class ParseRotoworld
  root = '/Users/Danil/Dropbox/OOOL/data/2015/rotoworld'

  id_store = {}
  database = []

  Dir.glob("#{root}/player.*.html").each do |file|

    player = {:bio => {}, :id => {}}

    /player.([0-9]+).html/.match(file) do |m|
      player[:id][:rotoworld] = m[1]
    end

    IO::foreach(file) do |line|

      /<div class="playername"><h1>(.*) \|/.match(line) do |m|
        player[:bio][:name] = m[1]
      end
      /DOB: <\/b>&nbsp;<\/td><td align="left">\(<b>[0-9]+<\/b>\) \/ ([0-9]+)\/([0-9]+)\/([0-9]+)<\/td>/.match(line) do |dob|
        player[:bio][:dob] = "%#04d%#02d%#02d" % [dob[3], dob[1], dob[2]]
        break
      end
    end

    id_store[player[:id][:rotoworld]] = player
    database << player
  end
  File.write("/Users/Danil/Dropbox/OOOL/data/2015/database/rotoworld.database.json", JSON.pretty_generate(database))
end