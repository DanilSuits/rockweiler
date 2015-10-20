require 'json'
require 'date'
require 'csv'

require 'axlsx'

class PositionReport
  YEAR = ARGV[0]
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/season"
  DRAFT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/draft"

  source = File.expand_path('scores.json', PositionReport::ROOT)
  json = File.read(source)
  db = JSON.parse(json)

  reports = {}

  db.each do |id, report|
    reports[id] = report
  end

  Axlsx::Package.new do |p|
    p.workbook do |wb|

      Dir.glob("#{DRAFT}/*.json").each do |file|
        puts File.basename(file, '.json')

        players = JSON.parse(File.read(file))
        if players.is_a?(Array)

          wb.add_worksheet do |sheet|
            sheet.name = File.basename(file, '.json')
            sheet.add_row ["rank", "id:bbref", "Name", "SP", "RP", "XX"]


            rank = 0
            players.each do |player|
              rank += 1

              name = player["bio"]["name"]
              id = player['id']['bbref']

              sp = ""
              rp = ""
              h = ""

              unless id.nil?

                report = reports[id]
                unless report.nil?
                  sp = report["totals"]["S"]
                  rp = report["totals"]["R"]
                  h = report["totals"]["H"]
                end
              end

              sheet.add_row [rank, id, name, sp, rp, h]


            end
          end
        end

      end
    end
    p.serialize File.expand_path("positions.xlsx", PositionReport::ROOT)
  end


  def skip
    wb.add_worksheet do |sheet|
      sheet.add_row ["id:bbref", "Name", "Owner", "SP", "RP", "XX"]

      db.each do |id, report|
        name = report["bio"]["name"]
        sp = report["totals"]["S"]
        rp = report["totals"]["R"]
        h = report["totals"]["H"]

        sheet.add_row [id, name, sp, rp, h]
      end

    end

  end

end
