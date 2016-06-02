require 'json'
require 'uuidtools'

class SpecifyAlias
end

data = {:bbref => ARGV[0], :alias => ARGV[1]}
event = {'event_type' => 'AliasSpecified', 'data' => data, 'event_id' => UUIDTools::UUID.random_create.to_s}
puts JSON.generate(event)
