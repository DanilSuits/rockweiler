require 'json'
require 'date'

class HittingReport

  YEAR = ARGV[0]
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{YEAR}/season"

  source = File.expand_path('scores.json', HittingReport::ROOT)
  json = File.read(source)
  db = JSON.parse(json)

  db.sort_by{ |bbref, report| bbref}.each do |bbref, report|
    name = report["bio"]["name"]

    report["scores"].each do |category, weeks|
      weeks.each do |monday, scores|
        total = report["totals"]["weekly"][category][monday]
        scores.sort!
        line = ":%s %s %-30s bbref:%-9s total: %3d scores:#{scores.map {" %3d"}.join}"
        puts line % [category, monday, name , bbref, total, *scores.reverse]

      end
    end
  end

end