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
        t[:players] << p
      end

      draft[:league][:teams] << t
    end

    for r in 1 .. 35
      round = { :round => r, :id => id(:rounds), :picks => []}
      pick_owner.each do |owner|
        pick = { :id => id(:picks), :state => :skipped, :comments => [] , :player => nil}
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
    self
  end

  def player(p)
    if @league[@owner].nil?
      @league[@owner] = []
    end
    @league[@owner] << p
    self
  end

  def build
    @league
  end
end

builder = League.new
builder.
    owner("Tim").
    player("Stephen Strasburg").
    player("Gerrit Cole").
    player("Wil Meyers").
    player("Andrew McCutchen").
    player("Miguel Sano").
    player("Javier Baez").
    player("Addison Russell").
    player("Kyle Zimmer").
    player("Robert Stephenson").
    player("Eric Hosmer").
    player("Jackie Bradley").
    player("Aaron Sanchez").
    player("Jedd Gyorko").
    player("Martin Perez")

builder.
    owner("Subrata").
    player("Adam Wainwright").
    player("Prince Fielder").
    player("Jose Altuva").
    player("Wei-Yin Chen").
    player("Albert Almora").
    player("Archie Bradley").
    player("Jake Odorizzi")

builder.
  owner("Danil").
  player("Michael Pineda").
  player("Matt Adams").
  player("Michael Zunino").
  player("Yordano Ventura").
  player("Kolten Wong").
  player("Jorge Soler").
  player("Clayton Kershaw").
  player("Max Scherzer").
  player("Chris Sale").
  player("Taijuan Walker")

builder.
  owner("Chris").
  player("Troy Tulowitzki").
  player("Jason Castro").
  player("Yoenis Cespedes").
  player("Yu Darvish").
  player("Yan Gomes").
  player("Evan Gattis").
  player("Alex Wood").
  player("Tanner Roark").
  player("Robbie Erlin").
  player("Jose Dominguez")

builder.
  owner("Daniel").
  player("Chris Archer").
  player("Jay Bruce").
  player("Dylan Bundy").
  player("Carlos Correa").
  player("Sonny Gray").
  player("Desmond Jennings").
  player("Shelby Miller").
  player("Mike Minor").
  player("Salvador Perez").
  player("Yasiel Puig").
  player("Andrelton Simmons").
  player("Michael Wacha")

builder.
  owner("Dave").
  player("Everth Cabrera").
  player("Ben Revere").
  player("Carlos Gomez").
  player("Jordan Zimmerman").
  player("Julio Teheran").
  player("Wily Peralta").
  player("Addison Reed")

builder.
  owner("Eric").
  player("Jonathan Lucroy").
  player("Avisail Garcia").
  player("Mike Trout").
  player("Cody Asche").
  player("Josmil Pinto").
  player("Joe Kelly").
  player("Carlos Gonzalez").
  player("Ausitn Meadows")

builder.
  owner("Michael").
  player("Joey Votto").
  player("Josh Donaldson").
  player("Gio Gonzales").
  player("Danny Salazar").
  player("Matt Moore").
  player("Jose Bautista").
  player("Matt Harvey").
  player("Francisco Lindor")

builder.
  owner("Tom").
  player("Miguel Cabrera").
  player("Giancarlo Stanton").
  player("Jason Kipnis").
  player("Matt Carpenter").
  player("Jurrickson Profar").
  player("Brad Miller").
  player("Christian Yelich").
  player("Travis d'Arnaud").
  player("Trevor Rosenthal").
  player("Allen Craig")

builder.
  owner("Jacob/Don").
  player("Anthony Rizzo").
  player("Matt Cain").
  player("Nick Castellanos").
  player("Alex Cobb").
  player("AJ Cole").
  player("Starling Marte").
  player("Jameson Taillon").
  player("Zack Wheeler").
  player("Noah Syndergaard").
  player("Michael Olt")

builder.
  owner("Steve").
  player("Mark Trumbo").
  player("Adam Jones").
  player("Yadier Molina").
  player("Hanley Ramirez").
  player("Kevin Gausman")

builder.
  owner("Phil").
  player("Ryan Braun").
  player("Justin Upton").
  player("David Price").
  player("Wilin Rosario").
  player("Nolan Arenado").
  player("Anthony Rendon").
  player("Lance Lynn").
  player("Jose Fernandez").
  player("Masahiro Tanaka").
  player("Jonathan Gray")

builder.
  owner("Greenwell").
  player("Mark Appel").
  player("Byron Buxton").
  player("Evan Longoria").
  player("Dustin Pedroia").
  player("Carlos Santana").
  player("AJ Griffin").
  player("Kole Calhoun").
  player("Chris Owings").
  player("James Paxton").
  player("George Springer")

builder.
  owner("Hagen").
  player("Madison Bumgarner").
  player("Buster Posey").
  player("Paul Goldschmidt").
  player("Manny Machado").
  player("Tyler Skaggs").
  player("Gary Sanchez").
  player("Ian Desmond").
  player("Tony Cingrani").
  player("Tanner Scheppers")

builder.
  owner("Corey").
  player("Bryce Harper").
  player("Xander Bogaerts").
  player("Carlos Martinez").
  player("Oscar Taveras").
  player("Ian Kinsler").
  player("Jacoby Ellsbury").
  player("Jonathon Singleton").
  player("Austin Hedges")

builder.
  owner("John").
  player("Robinson Cano").
  player("Joe Mauer").
  player("Freddie Freeman").
  player("Dillon Gee").
  player("HyunJin Ryu")


finish = ["Tom", "Michael", "Chris", "Greenwell", "Steve", "Subrata", "Hagen", "Phil", "Eric", "John", "Daniel", "Dave", "Danil", "Jacob/Don", "Tim", "Corey"]

tool = CreateDraft.new(finish, builder.build)

puts JSON.pretty_generate(tool.create)

