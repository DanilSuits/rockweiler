require 'json'
require 'i18n'
require 'set'
require 'uuidtools'

class FindAliases

  class TeamHints
    def initialize
      @teams = {}
    end

    def add(*hints)
      names = Set.new(hints)

      names.each do |hint|
          @teams[hint] = names
      end
    end

    def hints(team)
      @teams[team] ||= Set.new([team])

      @teams[team].each do |hint|
        yield hint
      end
    end
  end

  class NameBook
    def initialize
      @name = Set.new()
    end

    def add(first, last)
      name = makeName(first,last)

      @name.add(name)
      @name.add(I18n.transliterate(name).tr('.-', ''))
    end

    def clear
      @name.clear
    end

    def makeName(first, last)
      "#{last}_#{first}"
    end

    def all
      @name
    end
  end


  def initialize(team_hints)
    @team_hints = team_hints
  end

  def load(source)
    json = File.read(source)
    db = JSON.parse(json)

    db.each do |player|

      bbref = player['id']['bbref']

      name = player['bio']['name']

      teams = Set.new()

      player['games'].each do |game|
        if game['hints'].has_key?('team')
          teams.add game['hints']['team']
        end
      end

      playerHints(name).each do |name|
        teams.each do |team|
          @team_hints.hints(team) do |team|
            hint = "#{name}/#{team}"
            yield bbref, player['bio'], hint
          end
        end
      end
    end
  end

  def playerHints(name)
    parts = name.split(' ')

    names = NameBook.new

    names.add(parts[0], parts[-1])

    # TODO: consider if all of these edge cases should
    # just be skipped, and added in by hand.
    if parts.length > 2
      last = parts[-1]
      first = parts[0..-2].join
      names.add(first, last)

      ["De", "La", "Van", "den"].each do |prefix|
        if prefix.eql? parts[1]
          first = parts[0]
          last = parts[1..-1].join

          names.clear
          names.add(first, last)
        end
      end

    end


    names.all()
  end

end

teams = FindAliases::TeamHints.new
teams.add("ARZ", "ARI")
teams.add("CHC", "CHN")
teams.add("CHW", "CHA", "CWA")
teams.add("KCR", "KCA", "KC")
teams.add("LAD", "LAN")
teams.add("NYM", "NYN")
teams.add("NYY", "NYA")
teams.add("PHL", "PHI")
teams.add("SDP", "SDN")
teams.add("SFG", "SFN")
teams.add("STL", "SLN")
teams.add("TBR", "TBA", "TB")
teams.add("WSN", "WAS")

I18n.config.available_locales = :en

find_aliases = FindAliases.new(teams)

find_aliases.load(ARGV[0]) do |bbref, bio, hint|

  data = {:bbref => bbref, :bio => bio, :alias => hint}
  event = {'event_type' => 'AliasDiscovered', 'data' => data, 'event_id' => UUIDTools::UUID.random_create.to_s}
  puts JSON.generate(event)

end