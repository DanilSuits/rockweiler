require 'json'
require 'mechanize'

class ParseFieldingLogs
  def initialize(year)
    @year = year
  end

  def run
    mechanize = Mechanize.new

    positions = {}

      [:AL, :NL].each do |league|
        local_file = File.expand_path("../data/#{@year}/bbref/league.#{league}.fielding.html")
        puts local_file
        puts File.exists?(local_file)

        uri = "file://#{local_file}"
        page = mechanize.get(uri)


      end
  end
end

year = ARGV[0]

task = ParseFieldingLogs.new(year)

task.run