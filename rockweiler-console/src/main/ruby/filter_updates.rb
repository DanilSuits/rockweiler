require 'json'

class FilterUpdates
  def lastUpdated(file)
    if File.exists?(file)
      File.mtime(file)
    else
      Time.at(0)
    end
  end

  def event_order(json)
    update = JSON.parse(json)
    localDestination = update["localDestination"]
    lastUpdated(localDestination)
  end
end

filter = FilterUpdates.new

STDIN.sort_by { |json| filter.event_order(json)}.each do |json|
  puts json
end
