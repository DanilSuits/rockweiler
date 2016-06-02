require 'optparse'
require 'json'

class FilterRanks
end

options = {}

optparse = OptionParser.new do |opts|
  opts.on('--order path') do |path|
    options[:order] = path
  end

  opts.on('--filter path') do |path|
    options[:filter] = path
  end
end

optparse.parse!

show = {}

json = File.read(options[:filter])
JSON.parse(json).each do |player|
  uuid = player['id']['uuid']
  show[uuid] = player
end

ranked = []

json = File.read(options[:order])
JSON.parse(json).each do |player|
  uuid = player['id']['uuid']
  if show.key? uuid
    ranked << show[uuid]
  end
end


puts JSON.pretty_generate(ranked)
