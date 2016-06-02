require 'uuidtools'
require_relative "id_generator"

module PlayerPools

  UUID=UUIDTools::UUID.parse("7d139d10-7c63-4f4e-988d-9a3f30c211f8")

  def self.ids ()
    return IdGenerator.new(UUID)
  end


end