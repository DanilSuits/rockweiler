require 'json'
require 'net/http'


class RequestPlayersRotoworld

  db = JSON.parse(File.read("../data/2015/rotoworld/rotoworld.search.json"))
  db.each do |p|
    /player\/mlb\/([0-9]+)/.match(p["url"]) do |m|
      local_file = File.expand_path("../data/2015/rotoworld/player.#{m[1]}.html")

      if ! File.file?(local_file)
        update = {:remoteUri => p["url"], :localDestination => local_file}

        req = Net::HTTP::Post.new("/updates", initheader = {'Content-Type' =>'application/json'})
        req.body = update.to_json

        response = Net::HTTP.new("localhost", 8080).start {|http| http.request(req) }
      end
    end
  end
end