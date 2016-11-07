require 'json'
require 'date'
require 'csv'
require 'set'
require 'axlsx'

class SeasonDraftReport
  YEAR = ARGV[0]
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/season"

  source = File.expand_path('oool.draft.json', SeasonDraftReport::ROOT)
  json = File.read(source)
  draftEvents = JSON.parse(json)

  source = File.expand_path('scores.json', SeasonDraftReport::ROOT)
  json = File.read(source)
  scores = JSON.parse(json)

  labels = {"S" => "SP", "R" => "RP", "H" => "XX"}

  scoreboard = {}

  owners = Set.new
  draftEvents.each do |e|
    owners.add(e['owner'])
  end

  scores.each do |id, report|
    scoreboard[id] = {}

    totals = report["totals"]
    totals["season"].each do |category, score|
      label = labels[category]
      scoreboard[id][label] = score
    end
  end

  draft_headers = ["id:bbref", "Name", "Owner", "Status", "draftPosition", "score"]
  scoring_categories = ["XX", "SP", "RP"]

  headers = draft_headers + scoring_categories

  def self.isKeeper(e)
    0.eql? e['draftPosition']
  end

  def self.isPick(e)
    0 < e['draftPosition'].to_i
  end

  def self.hasScore(e, scoreboard, scoring_category)
    id = e['id']
    if scoreboard.has_key? id
      max = scoreboard[id].max_by {|k,v| v}
      puts '%s %s' % [scoring_category , max]
      return scoring_category.eql? scoreboard[id].max_by {|k,v| v}[0]
    end
    false
  end

  def self.rowWithSingleScore(e, scoreboard, scoring_category)
    row = []
    row << e['id']
    row << e['hint']

    if e.has_key? 'bio'
      if !e['bio'].nil?
        if !e['bio'].empty?
          row[1] = e['bio']['name']
        end
      end
    end

    row << e['owner']
    status = 'Drafted'
    if SeasonDraftReport::isKeeper(e)
      status = 'Kept'
    end

    row << status
    row << e['draftPosition']
    row << scoreboard[e['id']][scoring_category]

    row.flatten
  end

  def self.rowForDraftEvent(e, scoreboard, scoring_categories)
    row = []
    row << e['id']
    row << e['hint']

    if e.has_key? 'bio'
      if !e['bio'].nil?
        if !e['bio'].empty?
          row[1] = e['bio']['name']
        end
      end
    end

    row << e['owner']
    status = 'Drafted'
    if SeasonDraftReport::isKeeper(e)
      status = 'Kept'
    end

    row << status
    row << e['draftPosition']

    if !'UNKNOWN'.eql? e['id']
      if scoreboard.has_key? e['id']

        scores = ['=MAX(OFFSET(INDIRECT(ADDRESS(ROW(),COLUMN())),0,1,1,3))']
        scoring_categories.each do |header|
          scores << scoreboard[e['id']][header]
        end

        row << scores

      end
    end

    row.flatten

  end

  Axlsx::Package.new do |p|
    p.workbook do |wb|


      wb.add_worksheet do |view|

        view.name = 'Overview'
        view.add_row ['Owner', 'Keeper Value', 'Draft Value']

        owners.each do |owner|
          row = [owner]
          row << '=SUMIF(Keepers!C:C, "=" & INDIRECT(ADDRESS(ROW(),COLUMN()-1)), Keepers!F:F)'
          row << '=SUMIF(Draft!C:C, "=" & INDIRECT(ADDRESS(ROW(),COLUMN()-2)), Draft!F:F)'

          view.add_row row.flatten

        end
      end

      wb.add_worksheet do |view|
        view.name = SeasonDraftReport::YEAR
        view.add_row headers

        attention = {}

        attention['Danil'] = view.styles.add_style(:bg_color => "D0D0D0", :fg_color => "2009EF", :b => true)

        draftEvents.each do |e|

          row = view.add_row SeasonDraftReport::rowForDraftEvent(e, scoreboard, scoring_categories)
          if attention.has_key? e['owner']
             row.cells[2].style = attention[ e['owner']]
          end
        end

      end

      wb.add_worksheet do |view|
        view.name = "Keepers"
        view.add_row headers

        draftEvents.each do |e|
          if SeasonDraftReport::isKeeper(e)
            view.add_row SeasonDraftReport::rowForDraftEvent(e, scoreboard, scoring_categories)
          end
        end


      end

      wb.add_worksheet do |view|
        view.name = "Draft"
        view.add_row headers

        draftEvents.each do |e|
          if SeasonDraftReport::isPick(e)
            view.add_row SeasonDraftReport::rowForDraftEvent(e, scoreboard, scoring_categories)
          end
        end

      end

      ['SP', 'RP'].each do |scoring_category|
        wb.add_worksheet do |view|
          view.name = scoring_category
          view.add_row draft_headers

          draftEvents.each do |e|
            if SeasonDraftReport::isPick(e)
              if SeasonDraftReport::hasScore(e, scoreboard, scoring_category)
                view.add_row SeasonDraftReport::rowWithSingleScore(e, scoreboard, scoring_category)
              end
            end
          end
        end
      end

    end

    p.serialize File.expand_path("draft.review.xlsx", SeasonDraftReport::ROOT)
  end

end