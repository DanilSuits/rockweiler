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
    draft_order = [8,9,10,11,12,13,14,15,16,7,6,5,4,3,2,1].map { |x| x-1 }

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

#2014
#2014
#2015
#2014
#2014
#2014
#2014
#2014
#2014
#2014
#2014

#2014

#2015
builder.
    owner("Corey").inactive

# player("Bryce Harper")
# player("Xander Bogaerts").
# player("Carlos Martinez").
# player("Oscar Taveras").
# player("Ian Kinsler").
# player("Jacoby Ellsbury").
# player("Jonathon Singleton").
# player("Austin Hedges")

builder.
    owner("Chris").
    player("Tulowitzki_Troy/COL").
    player("Posey_Buster/SFN").
    player("Cespedes_Yoenis/DET").
    player("Roark_Tanner/WAS").
    player("Wood_Alex/ATL").
    player("Gomes_Yan/CLE").
    player("Norris_Daniel/TOR")

builder.
    owner("Duncan").
    player("Gomez_Carlos/MIL").
    player("Encarnacion_Edwin/TOR").
    player("Zimmermann_Jordan/WAS").
    player("Teheran_Julio/ATL")

builder.
    owner("Daniel").
    player("Ross_Tyson/SDN").
    player("Dozier_Brian/MIN").
    player("Perez_Salvador/KCA").
    player("Puig_Yasiel/LAN").
    player("Gray_Sonny/OAK").
    player("Wacha_Michael/SLN").
    player("Archer_Chris/TBA").
    player("Alcantara_Arismendy/CHN").
    player("Byrant_Kris/CHN").
    player("Gallo_Joey/TEX").
    player("Correa_Carlos/HOU").
    player("Bundy_Dylan/BAL")

builder.
    owner("Danil").
    player("Kershaw_Clayton/LAN").
    player("Sale_Chris/CHA").
    player("Pineda_Michael/NYA").
    player("Adams_Matt/SLN").
    player("Wong_Kolten/SLN").
    player("Ventura_Yordano/KCA").
    player("Walker_Taijuan/SEA").
    player("Soler_Jorge/CHN").
    player("Castillo_Rusney/BOS").
    player("Glasnow_Tyler/PIT")

builder.
    owner("Michael").
    player("Harvey_Matt/NYN").
    player("Hahn_Jesse/OAK").
    player("House_TJ/CLE").
    player("Votto_Joey/CIN").
    player("Donaldson_Josh/TOR").
    player("Semien_Marcus/OAK").
    player("Betts_Mookie/BOS").
    player("Lamb_Jake/ARI").
    player("Pompey_Dalton/TOR").
    player("Taylor_Michael/WAS").
    player("Lindor_Francisco/CLE").
    player("Urias_Julio/LAN").
    player("Devers_Rafael/BOS")

builder.
    owner("Phil").
    player("Price_David/DET").
    player("Carter_Chris/HOU").
    player("Smyly_Drew/TBA").
    player("Arenado_Nolan/COL").
    player("Fernandez_Jose/MIA").
    player("Rendon_Anthony/WAS").
    player("Abreu_Jose/CHA").
    player("Tanaka_Masahiro/NYA").
    player("Dahl_David/COL").
    player("Gray_Jonathan/COL").
    player("Seager_Corey/LAN")

builder.
    owner("Greenwell").
    player("Buxton_Byron/MIN").
    player("Rodon_Carlos/CHA").
    player("Calhoun_Kole/LAA").
    player("Springer_George/HOU").
    player("Owings_Chris/ARI").
    player("Santana_Carlos/CLE").
    player("Santana_Danny/CLE").
    player("DeGrom_Jacob/NYN").
    player("Cueto_Johnny/CIN").
    player("Paxton_James/SEA").
    player("Tropeano_Nick/HOU")

builder.
    owner("Subrata").
    player("Altuve_Jose/HOU").
    player("Moore_Matt/TBA").
    player("Chen_Wei-Yin/BAL").
    player("Iwakuma_Hisashi/SEA").
    player("Quintana_Jose/CHA").
    player("Bauer_Trevor/CLE").
    player("Holt_Brock/BOS").
    player("Odorizzi_Jake/TBA").
    player("Bradley_Archie/ARI").
    player("Almora_Albert/CHN").
    player("Barnes_Matt/BOS")

builder.
    owner("Tom").
    player("Cabrera_Miguel/DET").
    player("Stanton_Giancarlo/FLA").
    player("Dickerson_Corey/COL").
    player("Ozuna_Marcell/FLA").
    player("Yelich_Christian/FLA").
    player("d'Arnaud_Travis/NYN").
    player("Pederson_Joc/LAN")

builder.
    owner("Eric").
    player("Lucroy_Jonathan/MIL").
    player("Trout_Mike/LAA").
    player("Garcia_Avisail/CHA").
    player("Rua_Ryan/TEX").
    player("McHugh_Collin/HOU").
    player("Shoemaker_Matt/LAA").
    player("Scherzer_Max/DET").
    player("Keuchel_Dallas/HOU")

builder.
    owner("Tim").
    player("Hosmer_Eric/KCA").
    player("Strasburg_Stephen/WSH").
    player("Cole_Gerrit/PIT").
    player("McCutchen_Andrew/PIT").
    player("Baez_Javier/CHN").
    player("Sano_Miguel/MIN").
    player("Russell_Addison/CHN").
    player("Polanco_Gregory/PIT").
    player("Myers_Wil/TBA").
    player("Stephenson_Robert/CIN")

#2014
builder.
    owner("John").
    player("Cano_Robinson/SEA").
    player("Freeman_Freddie/ATL").
    player("Martinez_JD/DET").
    player("Flores_Wilmer/NYN").
    player("Ryu_HJ/LAN").
    player("Greene_Shane/DET").
    player("Anderson_Chase/ARI")

#2014
builder.
    owner("Hagen").
    player("Desmond_Ian/WAS").
    player("Bumgarner_Madison/SFN").
    player("Goldschmidt_Paul/ARI").
    player("Machado_Manny/BAL").
    player("Kluber_Corey/CLE").
    player("Gattis_Evan/HOU").
    player("Giolito_Lucas/WAS")

builder.
    owner("Jacob/Don").
    player("Rizzo_Anthony/CHN").
    player("Castellanos_Nick/DET").
    player("Marte_Starling/PIT").
    player("Hernandez_Felix/SEA").
    player("Cobb_Alex/TBA").
    player("Wheeler_Zack/NYN").
    player("Syndergaard_Noah/NYN")

builder.
    owner("Steve").
    player("Ramirez_Hanley/BOS").
    player("Jones_Adam/BAL").
    player("Harrison_Josh/PIT").
    player("Gausman_Kevin/BAL").
    player("Schoop_Jonathan/BAL").
    player("Despaigne_Odrisamer/SDN")



#2015
finish = ["Phil", "Eric", "Tom", "John", "Subrata", "Hagen", "Chris", "Greenwell", "Steve", "Daniel", "Jacob/Don", "Danil", "Duncan", "Michael", "Corey", "Tim"]

inactive_owners = ["Corey"]

keepers = builder.build

#2015
trade_repo = TradeRepository.new(finish, 35)
trade_repo.trade("Daniel", 7, "Corey") #OK
trade_repo.trade("Eric", 1, "Danil") #OK
trade_repo.trade("Eric", 5, "Danil") #OK
trade_repo.trade("Tom", 8 , "Danil") #OK
trade_repo.trade("Subrata", 15, "Danil") #OK
trade_repo.trade("Eric",4,"Michael") #OK
trade_repo.trade("Eric",9,"Michael") #OK
trade_repo.trade("Tom", 9,"Michael") #OK
trade_repo.trade("Tom",11,"Duncan")
trade_repo.trade("Phil", 10, "Chris")
trade_repo.trade("Tim",11,"Hagen")
trade_repo.trade("Daniel",12,"Steve")

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
