require 'json'
require 'uuidtools'

class UpdateBBRefPool
  class History
    def initialize(pool)
      @pool = pool
      @events = []
    end

    def add(e)
      @pool.apply(e)
      @events << e
    end

    def replay
      @events.each do |e|
        yield e
      end
    end
  end

  def initialize
    @ids = {}
  end

  def on(e)
    history = History.new(self)
    if e['event_type'].eql? 'PlayerReferenceObserved'
      id = e['data']['id']
      bbref = id['bbref']

      if not @ids.has_key? bbref


        bio = e['data']['bio']

        data = {'id' => id, 'bio' => bio}
        discovered = {'event_type' => 'PlayerReferenceDiscovered', 'data' => data, 'event_id' => UUIDTools::UUID.random_create.to_s}

        history.add(discovered)
      end
    end

    history
  end

  def apply(e)
    if e['event_type'].eql? 'PlayerReferenceDiscovered'
      id = e['data']['id']
      bbref = id['bbref']
      bio = e['data']['bio']

      @ids[bbref] = bio.clone
    end

  end
end

pool = UpdateBBRefPool.new

File.readlines(ARGV[0]).each do |json|
  event = JSON.parse(json)
  pool.apply(event)
  puts JSON.generate(event)
end

STDIN.each do |json|
  event = JSON.parse(json)

  pool.on(event).replay do |e|
    puts JSON.generate(e)
  end
end