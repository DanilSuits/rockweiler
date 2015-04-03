require 'mechanize'
require 'logger'
require 'set'

class SearchRotoworld
  def initialize(mechanize)
    @mechanize = mechanize
  end

  def run(range = [])
    [:American , :National].each do |league|
      yield @mechanize.get("http://www.rotoworld.com/teams/depth-charts/mlb.aspx?league=#{league}")
    end

    page = @mechanize.get("http://www.rotoworld.com/content/playersearch.aspx?searchname=a&sport=mlb")

    range.each do |last_name|
      page.forms.first['ctl00$cp1$tbLastNameSearch'] = last_name
      page.forms.first.radiobuttons[1].check

      button = page.forms.first.button_with(:value => 'Search')
      results = page.forms.first.submit(button)

      yield results

    end
  end
end

mechanize = Mechanize.new
mechanize.log = Logger.new(STDOUT)
mechanize.user_agent_alias = 'Mac Mozilla'

search = SearchRotoworld.new(mechanize)

db = []
known_players = Set.new

NAMES=("A".."Z")
search.run(NAMES) do |results|
  results.links.each do |l|
    /\/player\/mlb\/([0-9]+)/.match(l.href) do |m|
      id = m[1]
      if !known_players.include? id
        player_url = results.uri.merge l.uri
        name = l.text.strip.gsub(/\s+/, ' ')

        db << {:name => name, :url => player_url}

        known_players.add(id)
      end
    end
  end

end

File.write("../data/2015/rotoworld/rotoworld.search.json", JSON.pretty_generate(db))
