require 'json'
require 'i18n'

class RebuildMaster
  YEAR = 2016
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/database"
end

I18n.config.available_locales = :en

source = File.expand_path('2015.master.players.json', RebuildMaster::ROOT)
json = File.read(source)
db = JSON.parse(json)

known = {}
db.each do |player|
  ids = player['id']
  if ids.key? 'bbref'
    id = ids['bbref']
    known[id] = player
  end
end

json = File.read("/Users/Danil/Dropbox/OOOL/data/2015/season/gamelogs.json")

gamelog = JSON.parse(json)

gamelog.each do |player|
  id = player['id']['bbref']

  if not known.key? id
    n = {}
    n['id'] = player['id']
    n['bio'] = player['bio'].clone

    n['bio']['name'] = I18n.transliterate(n['bio']['name'])

    known[id] = n
    db << n
  end
end

puts JSON.pretty_generate(db)
