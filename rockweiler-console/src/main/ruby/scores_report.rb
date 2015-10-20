require 'json'
require 'date'

class ScoresReport
  YEAR = ARGV[0]
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/season"

  source = File.expand_path('gamelogs.json', ScoresReport::ROOT)
  json = File.read(source)
  db = JSON.parse(json)

  report = {}
  db.each do |player|

    bbref = player['id']['bbref']

    name = player['bio']['name']

    report[bbref] ||= {:bbref => bbref, :name => name, :scores => {}}
    scores = report[bbref][:scores]

    player['games'].each do |game|
      gameId = game['gameId']
      gameDate = gameId.split(".")[0]

      week = DateTime.parse(gameDate).strftime('%Y-W%V-1')
      gameWeek = DateTime.parse(week).strftime('%Y-%m-%d')

      type = game['score']['type']
      points = game['score']['points']

      if game['hints'].has_key?('pos')
        if game['hints']['pos'].eql?('P')
          next
        end
      end

      scores[gameWeek] ||={}
      scores[gameWeek][gameDate] ||={}
      scores[gameWeek][gameDate][gameId] ||= {}
      scores[gameWeek][gameDate][gameId][type] = points
    end
  end

  ['S', 'R', 'H'].each do |type|
    report.keys.sort.each do |bbref|
      name = report[bbref][:name]
      scores = report[bbref][:scores]

      scores.keys.sort.each do |gameWeek|
        scores[gameWeek].keys.sort.each do |gameDate|
          scores[gameWeek][gameDate].keys.sort.each do |gameId|
            if scores[gameWeek][gameDate][gameId].has_key? type
              points = scores[gameWeek][gameDate][gameId][type]
              puts '%3d:%s %s %-30s bbref:%s week:%s' % [points, type, gameDate, name, bbref, gameWeek]
            end
          end
        end
      end
    end

  end
end
