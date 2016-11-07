require 'mechanize'
require 'json'
require 'uuidtools'


class ParseDraftReport
  def initialize ()
    @mechanize = Mechanize.new
  end

  def load(html)

    if not File.exists?(html)
      raise "Where is the file #{html}"
    end

    uri = "file://#{html}"
    page = @mechanize.get(uri)
    page.encoding = 'utf-8'
    yield page

  end

end

process = ParseDraftReport.new

process.load("/Users/Danil/Dropbox/OOOL/data/2016/oool/draft.report.html") do |page|
  rows = page.search("//table[@width=1001]//tr")
  rows.shift

  draftId = UUIDTools::UUID.random_create.to_s
  rows.each do |r|
    columns = r.search("td")
    pick = columns[1].text
    owner = columns[2].text
    player = columns[3].text

    data = { :owner => owner, :hint => player, :draftId => draftId}
    if "0".eql? pick
      event = {'event_type' => 'PlayerKept', 'data' => data, 'event_id' => UUIDTools::UUID.random_create.to_s}
    elsif "PASS".eql? player
      data = { :owner => owner, :draftPosition => pick }
      event = {'event_type' => 'PickSkipped', 'data' => data, 'event_id' => UUIDTools::UUID.random_create.to_s}
    else
      data[:draftPosition] = pick
      event = {'event_type' => 'PlayerDrafted', 'data' => data, 'event_id' => UUIDTools::UUID.random_create.to_s}
    end
    puts JSON.generate(event)


  end



end

