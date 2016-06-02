require 'json'
require 'date'
require 'csv'

require 'axlsx'

class SeasonReport
  YEAR = ARGV[0]
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/season"

  to_owner = Hash.new('AVAILABLE')

  owners = %w(duncan chris treiber redquarx eric jacob john hagen michael phil greenwell steve subrata tim tom)
  owners.each do |o|
    roster = File.expand_path("#{o}.ids", SeasonReport::ROOT)
    if File.exists? roster
      File.readlines(roster).each do |id|
        to_owner[id.chomp] = o
      end
    end
  end

  source = File.expand_path('scores.json', SeasonReport::ROOT)
  json = File.read(source)
  db = JSON.parse(json)

  labels = {"S" => "SP", "R" => "RP", "H" => "XX"}
  sheets = {}

  labels.each do |category, label|
    sheets[category] = {}
  end

  db.each do |id, report|
    totals = report["totals"]
    totals["season"].each do |category, score|
      sheets[category][id] = report
    end
  end



  Axlsx::Package.new do |p|
    p.workbook do |wb|
      sheets.each do |category, reports|

        label = labels[category]

        wb.add_worksheet do |view|
          view.name = label
          view.add_row ["id:bbref", "Name", "Owner", labels[category]]

          reports.sort_by { |id, report| report["totals"]["season"][category]}.reverse.each do |id, report|

            owner = to_owner[id]
            name = report["bio"]["name"]
            score = report["totals"]["season"][category]
            view.add_row [id, name, owner, score]

          end
        end
      end
    end

    p.serialize File.expand_path("old.season.xlsx", SeasonReport::ROOT)

  end
end
