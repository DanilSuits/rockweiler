require 'json'
require 'mechanize'

class ParseDraft
  def initialize(mechanize, year)
    @mechanize = mechanize
    @year = year
    @root = "/Users/Danil/Dropbox/OOOL/data/#{@year}/season"

  end

  def run
    events = []
    load_page('subrata.draft.report.html') do |page|
      table = page.at("td:contains('Rnd')").parent.parent
      table.search('tr').each do |r|
        round, pick_order, team_hint, player_hint = r.search('td').map { |c| c.text }

        slot = {:round => round.to_i, :scheduledAt => pick_order.to_i}
        team = {:hint => team_hint}
        player = {:hint => player_hint}


        if round.eql? '0'
          events << event('PLAYER_KEPT', :team => team, :player => player)
        elsif player_hint.include? 'SKIPPED'
          events << event('PICK_SKIPPED', :pick => slot, :team => team)
        else
          events << event('PICK_SUBMITTED', :pick => slot, :team => team, :player => player)
        end

      end
    end

    puts JSON.pretty_generate(events)
  end

  def event(event_type, properties)
    {:eventType => event_type}.merge(properties)
  end

  def load_page(file)
    html = File.expand_path(file, @root)

    if not File.exists?(html)
      raise "Where is the file #{html}"
    end

    uri = "file://#{html}"
    page = @mechanize.get(uri)
    page.encoding = 'utf-8'
    yield page

  end
end

year = ARGV[0]
mechanize = Mechanize.new

task = ParseDraft.new(mechanize, year)

task.run