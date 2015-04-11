require 'json'

class CreateProspectList
  ROOT = '/Users/Danil/Dropbox/OOOL/data/2015/mlb'
end

source = File.expand_path("playerProspects.json", CreateProspectList::ROOT)
json = File.read(source)
report = JSON.parse(json)

ranked_prospects = report['prospect_players']['prospects']
ranked_prospects.each do |player|
  puts "# %s %s" % [player['player_first_name'], player['player_last_name']]
  puts "p %d" % [player['player_id'].to_i]
end