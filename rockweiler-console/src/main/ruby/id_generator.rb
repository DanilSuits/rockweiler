class IdGenerator
  def initialize(root_id)
    @root_id = root_id
  end

  def get(key)
    uuid = UUIDTools::UUID.md5_create(@root_id, key)
  end
end