require 'json'

class CheckRotoworld
  db = {}
  db[:database] = JSON.parse(File.read("/Users/Danil/Dropbox/OOOL/data/2015/database/rotoworld.database.json"))
  db[:players] = JSON.parse(File.read("/Users/Danil/Dropbox/OOOL/data/2015/database/rotoworld.players.json"))

  id_store = {}
  db[:database].each do |player|
    id_store[player["id"]["rotoworld"]] = player
  end

  db[:players].each do |known|
    player = id_store[known["id"]["rotoworld"]]
    if ! player.nil?
      puts known
      puts player
    end
  end
end