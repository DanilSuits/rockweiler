require 'json'
require 'set'

class ReportDraftPicks
  def initialize
    @keepers = []
    @picks = []
    @bio = {}
    @players = {}
  end

  def onPickSkipped(data)

  end

  def onPlayerKept(data)
    @keepers << data
  end

  def onPlayerDrafted(data)
    @picks << data
  end

  def onAliasDiscovered(data)
    bbref = data["bbref"]
    hint = data["alias"]

    @bio[bbref] = data["bio"]

    addHint(bbref, hint)
  end

  def addHint(bbref, hint)
    lower_hint = hint.downcase
    @players[lower_hint] ||= Set.new
    @players[lower_hint].add(bbref)
  end

  def onAliasSpecified(data)
    bbref = data["bbref"]
    hint = data["alias"]

    addHint(bbref, hint)
  end

  def load(file)
    file.each do |line|
      event = JSON.parse(line)

      if 'PickSkipped'.eql? event["event_type"]
        onPickSkipped(event["data"])
      end

      if 'PlayerKept'.eql? event["event_type"]
        onPlayerKept(event["data"])
      end

      if 'PlayerDrafted'.eql? event["event_type"]
        onPlayerDrafted(event["data"])
      end

      if 'AliasDiscovered'.eql? event["event_type"]
        onAliasDiscovered(event["data"])
      end

      if 'AliasSpecified'.eql? event["event_type"]
        onAliasSpecified(event["data"])
      end
    end
  end

  def createReport
    @keepers.each do |data|
      owner = data['owner']
      hint = data['hint']

      id = nil
      bio = {}

      data = @players[hint.downcase]
      if !data.nil?
        if 1 == data.length
          data.each do |bbref|
            id = bbref
            bio = @bio[id]
          end
        end
      end

      draftPosition = 0
      yield draftPosition, owner, hint, id, bio

    end

    @picks.each do |data|
      owner = data['owner']
      hint = data['hint']

      id = nil
      bio = {}

      candidates = @players[hint.downcase]
      if !candidates.nil?
        if 1 == candidates.length
          candidates.each do |bbref|
            id = bbref
            bio = @bio[id]
          end
        end
      end

      draftPosition = data['draftPosition']
      yield draftPosition, owner, hint, id, bio

    end
  end
end

roster = []
report = ReportDraftPicks.new
report.load(STDIN)
report.createReport do |draftPosition, owner, hint, id, bio|
  if id.nil?
    id = "UNKNOWN"
  end

  entry = { :id => id, :draftPosition => draftPosition, :owner => owner, :hint => hint, :bio => bio}

  roster << entry
end

puts JSON.pretty_generate(roster)
