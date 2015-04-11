require 'mechanize'

class ParseRotoworldDepth
  ROOT = '/Users/Danil/Dropbox/OOOL/data/2015/rotoworld'

end

mechanize = Mechanize.new

html = File.expand_path('american.depth.html', ParseRotoworldDepth::ROOT)
uri = "file://#{html}"
page = mechanize.get(uri)

rank = 0
pos = "?"

table = page.search("//table[@id='cp1_tblDepthCharts']/tr/td/table")
table.search("tr").each do |r|
  r.search("td/b").each do |p|
    pos = p.text
    rank = 0
  end
  r.search("td")[1].search("a").each do |link|
    rank += 1
    puts "%s %d %s" % [pos, rank, link['href']]
  end

end

