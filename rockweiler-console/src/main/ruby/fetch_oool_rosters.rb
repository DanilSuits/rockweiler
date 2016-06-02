require 'json'
require 'net/http'
require 'mechanize'

require 'nokogiri'

class FetchOoolRosters
end

uri = ARGV[0]

auth = {"Y2User-10309" => "voiceofunreason", "Y2Pass-10309"=>"DkpUJQGiEextbG0h5TMlAQ", "Y2Sess-10309" => "qTXw+U9st+0zjkFXKZDfGw"}

mechanize = Mechanize.new

auth.each do |k,v|
   cookie = Mechanize::Cookie.new(k,v)
   cookie.domain = ".rotowatch.com"
   cookie.path ="/"
   mechanize.cookie_jar.add(cookie)
end

page = mechanize.get(uri)

page.search("//div[@class='message']/tt/text()").each do |message|

  puts message.text
end