class RenameGameLogs
  ROOT = "/Users/Danil/Dropbox/OOOL/data/#{ARGV[0]}/bbref"


  def pitchers
    Dir.glob("#{ROOT}/pitcher.gamelog.*.html").each do |file|
      /pitcher.gamelog.([^\\.]+).html/.match(file) do |m|
        yield m[1], m[0]
      end

    end
  end

  def hitters
    Dir.glob("#{ROOT}/hitter.gamelog.*.html").each do |file|
      /hitter.gamelog.([^\\.]+).html/.match(file) do |m|
        yield m[1], m[0]
      end
    end
  end

  def move(source, destination)
    original = File.expand_path(source, ROOT)

    if not File.exists?(original)
      raise "Where is the file #{original}"
    end

    File.rename(original, File.expand_path(destination,ROOT))
  end

end

gamelogs = RenameGameLogs.new

gamelogs.pitchers do |id, html|
  gamelogs.move html, "pitching.gamelog.#{id}.html"
end

gamelogs.hitters do |id, html|
  gamelogs.move html, "batting.gamelog.#{id}.html"
end