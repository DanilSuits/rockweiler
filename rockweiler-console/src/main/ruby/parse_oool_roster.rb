require 'json'
require 'uuidtools'

class ParseOoolRoster
  def self.load(source)
    currentOwner = ""

    state = :scanning

    File.open(source).each do |line|
      if state.eql? :scanning
        /^ ---/.match(line) do |m|
          state = :team
        end
      end

      if state.eql? :team
        /^ (\S+ (\S+))/.match(line) do |m|
          currentOwner = m[1]
          state = :players
        end
      end

      if state.eql? :players
        /^ ---/.match(line) do |m|
          state = :team
        end

        /^.\w+\W+(\w+( [A-Z][a-z]+)?\/[A-Z]..)\b/.match(line) do |m|
          currentPlayer = m[1]
          yield(currentOwner, currentPlayer)
        end
      end
    end
  end
end

ParseOoolRoster::load(ARGV[0]) do |owner, player|
  data = { :owner => owner, :player => player}
  event = {'event_type' => 'PlayerAssigned', 'data' => data, 'event_id' => UUIDTools::UUID.random_create.to_s}
  puts JSON.generate(event)
end
