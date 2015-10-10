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

  Axlsx::Package.new do |p|
    p.workbook do |wb|
      wb.add_worksheet do |sheet|
        sheet.add_row ["id:bbref", "Name", "Owner", "SP", "RP", "XX"]

        db.each do |id, report|
          owner = to_owner[id]
          name = report["bio"]["name"]
          sp = report["totals"]["S"]
          rp = report["totals"]["R"]
          h = report["totals"]["H"]

          sheet.add_row [id, name, owner, sp, rp, h]
        end

      end

    end

    p.serialize File.expand_path("season.xlsx", SeasonReport::ROOT)

  end
end
