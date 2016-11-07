require 'json'
require 'date'
require 'csv'
require 'set'
require 'axlsx'

class SeasonSpreadsheet
  YEAR = ARGV[0]
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/season"

  to_owner = Hash.new('AVAILABLE')

  source = File.expand_path('oool.rosters.json', SeasonSpreadsheet::ROOT)
  json = File.read(source)
  JSON.parse(json).each do |player, owner|
    to_owner[player] = owner
  end

  source = File.expand_path('scores.json', SeasonSpreadsheet::ROOT)
  json = File.read(source)
  scores = JSON.parse(json)

  labels = {"S" => "SP", "R" => "RP", "H" => "XX"}
  sheets = {}

  labels.each do |category, label|
    sheets[category] = {}
  end

  mondays = Set.new

  scores.each do |id, report|
    totals = report["totals"]
    totals["season"].each do |category, score|
      sheets[category][id] = report
    end

    totals["weekly"].each do |category, weeks|
      weeks.each do |monday, score|
        mondays.add(monday)
      end
    end
  end

  source = File.expand_path('positions.json', SeasonSpreadsheet::ROOT)
  json = File.read(source)
  positions = JSON.parse(json)

  lookup = {}
  positions.each do |position, players|
    players.each do |id, report|
      if scores.has_key? id
        lookup[id] ||= []
        lookup[id] << position
      end
    end
  end
  players = lookup

  hitting = {}
  scores.each do |id, report|
    if report["totals"]["season"].has_key? "H"
      hitting[id] = report
    end
  end

  class Worksheet
    def initialize(view)
      @view = view
      @attention = { 'AVAILABLE' => view.styles.add_style(:bg_color => "EF0920", :fg_color => "FFFFFF")}
      @attention['Danil Suits'] = view.styles.add_style(:bg_color => "D0D0D0", :fg_color => "2009EF", :b => true)
      @currentWeek = view.styles.add_style(
          :fg_color => "808080", :b => false)
    end

    def addRow(id,name,owner,score,*weekly)
      row = @view.add_row [id,name,owner,score, *weekly]
      row.cells[4].style = @currentWeek
      if @attention.has_key? owner
        row.cells[2].style = @attention[owner]
      end
    end

  end

  Axlsx::Package.new do |p|
    p.workbook do |wb|

      views = {}

      sheets.each do |category, reports|


        label = labels[category]
        wb.add_worksheet do |view|



          view.name = label
          view.add_row ["id:bbref", "Name", "Owner", label] + mondays.each.sort.reverse

          worksheet = Worksheet.new(view)

          reports.sort_by { |id, report| report["totals"]["season"][category] }.reverse.each do |id, report|

            owner = to_owner[id]
            name = report["bio"]["name"]
            score = report["totals"]["season"][category]

            weekly_scores = Hash.new("")
            mondays.each{ |week| weekly_scores[week] = report["totals"]["weekly"][category][week]}
            scores_by_week = mondays.each.sort.reverse.map{ |week| weekly_scores[week]}
            worksheet.addRow(id, name, owner, score, *scores_by_week)
          end
        end
      end

      positions.keys.each do |position|

        wb.add_worksheet do |view|
          view.name = position
          view.add_row ["id:bbref", "Name", "Owner", position] + mondays.each.sort.reverse

          views[position] = Worksheet.new(view)
        end
      end

      category = "H"

      hitting.sort_by { |id, report| report["totals"]["season"][category] }.reverse.each do |id, report|
        if players.has_key? id

          owner = to_owner[id]
          name = report["bio"]["name"]
          score = report["totals"]["season"][category]

          players[id].each do |position|
            ws = views[position]

            weekly_scores = Hash.new("")
            mondays.each{ |week| weekly_scores[week] = report["totals"]["weekly"][category][week]}
            scores_by_week = mondays.each.sort.reverse.map{ |week| weekly_scores[week]}
            ws.addRow(id, name, owner, score, *scores_by_week)
          end

        end

      end
    end

    p.serialize File.expand_path("season.xlsx", SeasonSpreadsheet::ROOT)

  end
end
