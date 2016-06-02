require 'json'
require 'securerandom'

class CreateDraft
  def initialize(finish, keepers)
    @finish = finish
    @keepers = keepers
  end

  def id(reference_type)
    "/rockweiler/#{reference_type}/#{SecureRandom.uuid}"
  end

  def create()
    draft_order = [8,9,10,11,12,13,14,15,7,6,5,4,3,2,1].map { |x| x-1 }

    pick_owner = @finish.values_at *draft_order

    draft = {}
    draft[:meta] = {:year => 2014}
    draft[:league] = {:teams => [] , :rounds => []}

    pick_owner.sort.each do |owner|
      t = { :id => id(:teams), :owner => owner, :players => []}
      @keepers[owner].each do |p|
        player = { :id => id(:players), :name => p}
        t[:players] << player
      end

      draft[:league][:teams] << t
    end

    for r in 1 .. 35
      round = { :round => r, :id => id(:rounds), :picks => []}
      pick_owner.each do |owner|
        pick = { :id => id(:picks), :owner => owner, :state => :skipped, :comments => [] , :player => nil}
        round[:picks] << pick
      end

      draft[:league][:rounds] << round
    end

    draft
  end
end

class League
  def initialize(league = {})
    @league = league
  end
  def owner(o)
    @owner = o;
    if @league[@owner].nil?
      @league[@owner] = []
    end
    self
  end

  def player(p)
    @league[@owner] << p
    self
  end

  def inactive
    @league[@owner] = []
  end

  def build
    @league
  end
end

class TradeRepository

  def initialize(owners, rounds)
    @trades = {}
    owners.each do |owner|
      @trades[owner] = {}
      (1..rounds).each do |round|
        @trades[owner][round] = owner
      end
    end

    def trade(from, round, to)
      @trades[from][round] = to
    end

    def get(from, round)
      @trades[from][round]
    end
  end
end

builder = League.new

#2016
builder.
    owner("Tim").inactive()

builder.
    owner("Chris").
    player("Posey_Buster/SFN").
    player("Arrieta_Jake/CHN").
    player("Cespedes_Yoenis/NYN").
    player("Nola_Aaron/PHI").
    player("Norris_Daniel/DET").
    player("Velasquez_Vincent/PHI").
    player("Conforto_Michael/NYN").
    player("Winker_Jesse/CIN").
    player("Berrios_Jose/MIN").
    player("Blair_Aaron/ATL")

builder.
    owner("Duncan").
    player("Encarnacion_Edwin/TOR").
    player("Wong_Kolten/SLN").
    player("Gray_Sonny/OAK").
    player("Teheran_Julio/ATL")

builder.
    owner("Daniel").
    player("Dozier_Brian/MIN").
    player("Harper_Bruce/WAS").
    player("Puig_Yasiel/LAN").
    player("Gallo_Joey/TEX").
    player("Correa_Carlos/HOU").
    player("Bryant_Kris/CHN").
    player("Wacha_Michael/SLN").
    player("Archer_Chris/TBA").
    player("Franco_Maikel/PHI").
    player("Frazier_Todd/CIN").
    player("Richards_Garrett/LAA").
    player("Realmuto_Jacob/MIA").
    player("Duvall_Adam/CIN")

builder.
    owner("Danil").
    player("Kershaw_Clayton/LAN").
    player("Sale_Chris/CHA").
    player("Pollock_AJ/ARI").
    player("Ventura_Yordano/KCA").
    player("Walker_Taijuan/SEA").
    player("McCullers_Lance/HOU").
    player("Soler_Jorge/CHN").
    player("Glasnow_Tyler/PIT").
    player("Turner_Trea/WAS")


builder.
    owner("Michael").
    player("Betts_Mookie/BOS").
    player("Lindor_Francisco/CLE").
    player("Donaldson_Josh/TOR").
    player("Harvey_Matt/NYN").
    player("Matz_Steven/NYN").
    player("Urias_Julio/LAN").
    player("Devers_Rafael/BOS").
    player("Drury_Brandon/ARI").
    player("Marte_Ketel/SEA").
    player("Iglesias_Raisel/CIN").
    player("Conley_Adam/MIA").
    player("Kang_JungHo/PIT").
    player("Hahn_Jesse/OAK")

builder.
    owner("Phil").
    player("Abreu_Jose/CHA").
    player("Rendon_Anthony/WAS").
    player("Arenado_Nolan/COL").
    player("Seager_Corey/LAN").
    player("Tanaka_Masahiro/NYA").
    player("Fernandez_Jose/MIA").
    player("Price_David/BOS").
    player("Gray_Jonathan/COL").
    player("Dahl_David/COL").
    player("Mazara_Nomar/TEX").
    player("Judge_Aaron/NYA")


builder.
    owner("Greenwell").
    player("Vogt_Stephen/OAK").
    player("Bogaerts_Xander/BOS").
    player("OBrien_Peter/ARI").
    player("Solarte_Yangervis/SDN").
    player("Springer_George/HOU").
    player("DeGrom_Jacob/NYN").
    player("Rodon_Carlos/CHA").
    player("Rodgers_Brandan/COL").
    player("Buxton_Byron/MIN").
    player("Ross_Joe/WAS").
    player("Tomas_Yasmany/ARI")

builder.
    owner("Subrata").
    player("Belt_Brandon/SFN").
    player("Altuve_Jose/HOU").
    player("Quintana_Jose/CHA").
    player("Carpenter_Matt/SLN").
    player("Bauer_Trevor/CLE").
    player("Odorizzi_Jake/TBA").
    player("Crawford_JP/PHI").
    player("Bradley_Archie/ARI")

builder.
    owner("Tom").
    player("Pederson_Joc/LAN").
    player("Schwarber_Kyle/CHN").
    player("Stanton_Giancarlo/FLA").
    player("Odor_Rougned/TEX").
    player("Rodriguez_Eduardo/BOS").
    player("Salazar_Danny/CLE").
    player("d'Arnaud_Travis/NYN").
    player("Moncada_Yoan/BOS").
    player("Ray_Robbie/ARI").
    player("Piscotty_Stephen/SLN").
    player("Maeda_Kenta/LAN")

builder.
    owner("Eric").
    player("Cabrera_Miguel/DET").
    player("Trout_Mike/LAA").
    player("Herrera_Odubel/PHI").
    player("Keuchel_Dallas/HOU").
    player("McHugh_Collin/HOU").
    player("McCann_James/DET")

#2014
builder.
    owner("John").
    player("Miller_Shelby/ARI").
    player("Jungmann_Taylor/MIL").
    player("Eickhoff_Jared/PHI").
    player("Nicolina_Justin/MIA").
    player("Karns_Nate/SEA").
    player("Sanchez_Aaron/TOR").
    player("Martinez_JD/DET").
    player("Grichuk_Randall/SLN").
    player("Canha_Mark/OAK").
    player("Freeman_Freddie/ATL")

builder.
    owner("Hagen").
    player("Swihart_Blake/BOS").
    player("Goldschmidt_Paul/ARI").
    player("Machado_Manny/BAL").
    player("Olivera_Hector/ATL").
    player("Kluber_Corey/CLE").
    player("Corbin_Patrick/ARI").
    player("Martinez_Carlos/SLN").
    player("Stroman_Marcus/TOR").
    player("Heaney_Andrew/LAA").
    player("Giolito_Lucas/WAS")

builder.
    owner("Jacob/Don").
    player("Rizzo_Anthony/CHN").
    player("Hernandez_Felix/SEA").
    player("Marte_Starling/PIT").
    player("Wheeler_Zack/NYN").
    player("Syndergaard_Noah/NYN").
    player("Severino_Luis/NYA")

builder.
    owner("Steve").
    player("Bautista_Jose/TOR").
    player("Darvish_Yu/TEX").
    player("Gausman_Kevin/BAL").
    player("Schoop_Jonathan/BAL")



#2016
finish = ["Daniel", "Hagen", "Chris", "Danil", "Subrata", "Phil", "Eric", "Tim", "John", "Michael", "Jacob/Don", "Greenwell", "Steve", "Tom", "Duncan"]

inactive_owners = ["Tim"]


keepers = builder.build

#2015
trade_repo = TradeRepository.new(finish, 35)
trade_repo.trade("Daniel", 7, "Duncan") #OK
trade_repo.trade("Daniel", 8, "Duncan") #OK
trade_repo.trade("Duncan", 7, "Danil") #OK
trade_repo.trade("Greenwell", 6, "Daniel") #OK
trade_repo.trade("Daniel", 3, "Steve") #OK
trade_repo.trade("Daniel", 12, "Steve") #OK
trade_repo.trade("Subrata", 15, "Danil") #OK
trade_repo.trade("Eric", 1, "Tom") #OK
trade_repo.trade("Eric", 3, "Greenwell") #OK
trade_repo.trade("Eric", 4, "Tom") #OK
trade_repo.trade("John", 13, "Tom") #OK
trade_repo.trade("Hagen", 8, "Steve") #OK
trade_repo.trade("Phil", 10, "Tom") #OK
trade_repo.trade("Phil", 11, "Tom") #OK
trade_repo.trade("Tom", 11, "Phil") #OK


tool = CreateDraft.new(finish, keepers)
draft = tool.create

roster_slots = {}

roundId = 0
pickId = 0
# First, write out the keepers
draft[:league][:teams].each do | team |
  owner = team[:owner]
  roster_slots[owner] = 35

  team[:players].each do |player|
    puts "%d,%d,%s,%s,%s" % [roundId,pickId,owner,player[:name],:Keeper]
    roster_slots[owner] -= 1
  end
end

draft[:league][:rounds].each do |round|
  roundId = roundId + 1
  round[:picks].each do |pick|
    comments = []
    owner = trade_repo.get(pick[:owner], roundId)
    if ! owner.eql? pick[:owner]
      comments << "[from %s]" % pick[:owner]
    end

    if ! inactive_owners.include? owner
      if ! roster_slots[owner].eql? 0

        if roster_slots[owner].eql? 1
          comments << "[Last Pick]"
        end
        pickId = pickId + 1
        puts "%d,%d,%s,%s,%s" % [roundId,pickId,owner,"",comments.join(" ")]
        roster_slots[owner] -= 1
      end
    end
  end
end
