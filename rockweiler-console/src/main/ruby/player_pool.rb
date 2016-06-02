require 'uuidtools'

class PlayerPool
  def initialize(rawId)
    @rootId = UUIDTools::UUID.parse(rawId)
  end

  def create_player_id(player)
    UUIDTools::UUID.md5_create(@rootId, player)
  end
end