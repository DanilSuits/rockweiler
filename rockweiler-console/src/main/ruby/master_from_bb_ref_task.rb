require 'json'
require 'i18n'
require 'uuidtools'

class MasterFromBBRefTask
end

I18n.config.available_locales = :en

known = {}

json = File.read('/Users/Danil/Dropbox/OOOL/data/2016/database/2016.bbref.players.json')
JSON.parse(json).each do |player|
  id = player['id']['bbref']
  known[id] = player
end

json = File.read("/Users/Danil/Dropbox/OOOL/data/2014/season/gamelogs.json")

gamelog = JSON.parse(json)

gamelog.each do |player|
  id = player['id']['bbref']

  if not known.key? id
    n = {}
    n['id'] = player['id'].clone
    n['id']['uuid'] = UUIDTools::UUID.random_create
    n['bio'] = player['bio'].clone

    n['bio']['name'] = I18n.transliterate(n['bio']['name'])

    known[id] = n
  end
end

puts JSON.pretty_generate(known.values)