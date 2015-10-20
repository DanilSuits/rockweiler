require 'json'
require 'net/http'

class UpdateBbrefOverview
  def schedule(year)
    [:AL, :NL].each do |league|
      [:pitching, :batting].each do |pos|
        local_file = File.expand_path("../data/#{year}/bbref/league.#{league}.pos.#{pos}.html")
        url = "http://www.baseball-reference.com/leagues/#{league}/#{year}-standard-#{pos}.shtml"

        fetch(url,local_file)

      end

      local_file = File.expand_path("../data/#{year}/bbref/league.#{league}.fielding.html")
      url = "http://www.baseball-reference.com/leagues/#{league}/#{year}-appearances-fielding.shtml"
      fetch(url,local_file)

    end
  end

  def fetch(url, local_file)
    update = {:remoteUri => url, :localDestination => local_file}

    puts JSON.pretty_generate(update)

    req = Net::HTTP::Post.new('/updates', initheader = {'Content-Type' => 'application/json'})
    req.body = update.to_json

    response = Net::HTTP.new('localhost', 8080).start { |http| http.request(req) }
  end
end

overview = UpdateBbrefOverview.new

Array(2015).each do |year|
  overview.schedule year
end
