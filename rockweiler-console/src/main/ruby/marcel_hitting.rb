require 'csv'

class MarcelHitting
  ROOT = '/Users/Danil/Dropbox/OOOL/data/2015/draft'
end

CSV.foreach(File.expand_path("marcel.hitters.oool.csv", MarcelHitting::ROOT), :headers => true) do |row|
  puts "# %s %s" % [ row['First'], row['Last']]
  puts "p %s" % [ row['bdbID'] ]
end