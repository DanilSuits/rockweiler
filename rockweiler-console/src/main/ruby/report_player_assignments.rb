require 'json'
require 'set'

class ReportPlayerAssignments
  def initialize
    @teams = {}
    @bio = {}
    @players = {}
  end

  def onPlayerAssigned(data)
    owner = data["owner"]
    hint = data["player"]

    @teams[owner] ||= Set.new
    @teams[owner].add hint
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

      if 'PlayerAssigned'.eql? event["event_type"]
        onPlayerAssigned(event["data"])
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
    @teams.each do |owner, hints|
      hints.each do |hint|
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

        yield owner, hint, id, bio
      end

    end
  end
end

roster = {}
report = ReportPlayerAssignments.new
report.load(STDIN)
report.createReport do |owner, hint, id, bio|
  if id.nil?
    id = "UNKNOWN"
  end

  roster[id] = owner
end

puts JSON.pretty_generate(roster)
