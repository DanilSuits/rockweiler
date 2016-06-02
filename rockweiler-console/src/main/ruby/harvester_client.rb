require 'json'
require 'net/http'

class HarvesterClient
  def initialize()
    @host = ENV['HARVESTER_HOST'] || 'localhost'
    @port = ENV['HARVESTER_PORT'] || 8080
  end

  def schedule(update)

    req = Net::HTTP::Post.new('/updates', initheader = {'Content-Type' => 'application/json'})
    req.body = update

    response = Net::HTTP.new(@host, @port).start { |http| http.request(req) }
  end
end


client = HarvesterClient.new

STDIN.each do |update|
  client.schedule(update)
end