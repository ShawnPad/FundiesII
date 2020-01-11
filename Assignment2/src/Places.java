import tester.Tester;

class ExamplesPlaces {
  //Map of London
  //Venue: National Theatre
  //Restaurants: Tandoor Chop House, Terroirs
  //Place: South Bank, Westminster, Covent Garden
  //Shuttle: N87, N38, 24, Magic Bus

  ILoFeature westminsterList = new MtLoFeature();
  Place westminster = new Place("South Bank", westminsterList);
  ILoFeature southBankList = new ConsLoFeature(new Venue("National Theatre", "theatre", 1160),
        new ConsLoFeature(new ShuttleBus("N38", westminster),new MtLoFeature()));
  Place southBank = new Place("South Bank", southBankList);
  ILoFeature coventGardenList = new ConsLoFeature(
        new Restaurant("Tandoor Chop House", "Indian", 4.5),
        new ConsLoFeature(new Restaurant("Terroirs", "French", 4.0),
              new ConsLoFeature(new ShuttleBus("N87", southBank),
                    new ConsLoFeature(new ShuttleBus("24", westminster), new MtLoFeature()))));
  Place coventGarden = new Place("South Bank", coventGardenList);
  Place boston = new Place("Boston", new ConsLoFeature(
        new ShuttleBus("Magic Bus", coventGarden), new MtLoFeature()));


  ILoFeature northEndList = new ConsLoFeature(new Venue("TD Garden","stadium",19580),
        new ConsLoFeature(
              new Restaurant("The Daily Catch", "Sicilian", 4.4), new MtLoFeature()));
  Place northEnd = new Place("North End", northEndList);
  ILoFeature harvardList = new ConsLoFeature(new ShuttleBus("Freshmen-15", northEnd),
        new ConsLoFeature(new Restaurant("Border Cafe", "Tex-Mex", 4.5),
              new ConsLoFeature(
                    new Venue("Harvard Stadium", "football", 30323), new MtLoFeature())));
  Place harvard = new Place("Harvard", harvardList);
  ILoFeature southStationList = new ConsLoFeature(new ShuttleBus("Little Italy Express", northEnd),
        new ConsLoFeature(new Restaurant("Regina's Pizza","pizza",4.0),
              new ConsLoFeature(new ShuttleBus("Crimson Cruiser", harvard),
                    new ConsLoFeature(
                          new Venue("Boston Common", "public", 150000), new MtLoFeature()))));
  Place southStation = new Place("South Station", southStationList);
  ILoFeature cambridgeSideList = new ConsLoFeature(
        new Restaurant("Sarku Japan","teriyaki",3.9),
        new ConsLoFeature(new Restaurant("Starbucks", "coffee", 4.1),
              new ConsLoFeature(
                    new ShuttleBus("bridge shuttle", southStation), new MtLoFeature())));
  Place cambridgeSide = new Place("CambridgeSide Galleria", cambridgeSideList);

  boolean testFoodinessRating(Tester t) {
    return t.checkInexact(this.cambridgeSide.foodinessRating(),4.22, 0.01)
          && t.checkInexact(this.southStation.foodinessRating(),4.33, 0.01)
          && t.checkInexact(this.harvard.foodinessRating(),4.45, 0.01)
          && t.checkInexact(this.northEnd.foodinessRating(),4.4, 0.01);
  }

  boolean testRestaurantInfo(Tester t) {
    return t.checkExpect(this.cambridgeSide.restaurantInfo(),
          "Sarku Japan (teriyaki), Starbucks (coffee), The Daily Catch (Sicilian), "
                + "Regina's Pizza (pizza), The Daily Catch (Sicilian), Border Cafe (Tex-Mex)")
          && t.checkExpect(this.northEnd.restaurantInfo(),
          "The Daily Catch (Sicilian)")
          && t.checkExpect(this.harvard.restaurantInfo(),
          "The Daily Catch (Sicilian), Border Cafe (Tex-Mex)")
          && t.checkExpect(this.boston.restaurantInfo(),
          "Tandoor Chop House (Indian), Terroirs (French)");

  }
}

//A class to represent a place on a map
class Place {
  String name;
  ILoFeature features;

  Place(String name, ILoFeature features) {
    this.name = name;
    this.features = features;
  }

  /* fields:
   *  this.name ... String
   *  this.features ... ILoFeature
   * methods:
   *  this.getFeatures() ... ILoFeature
   * methods for fields:
   *  this.features.restaurantInfo() ... String
   *  this.features.foodinessRating() ... double
   */

  public ILoFeature getFeatures() {
    return this.features;
  }

  public String restaurantInfo() {
    return this.features.restaurantInfo();
  }

  public double foodinessRating() {
    return this.features.foodinessRating();
  }
}

//An interface to represent a list of places
interface ILoFeature {
  //returns a list of restaurants reachable from this location
  ILoFeature getRestaurants();

  //returns string of all restaurants and their type reachable from this location
  String restaurantInfo();

  //helper of restaurantInfo
  String restaurantInfoA();

  //helper of foodinessRating to apply accumulator
  double foodinessRating();

  //returns the average rating of all restaurants reachable from this location
  double foodinessRatingAcc(int acc, double acc2);

  //returns appended list of this to acc
  ILoFeature append(ILoFeature acc);
}

//A class to hold the feature and a list of the rest of features
class ConsLoFeature implements ILoFeature {
  IFeature first;
  ILoFeature rest;

  ConsLoFeature(IFeature first, ILoFeature rest) {
    this.first = first;
    this.rest = rest;
  }

  /* fields:
   *  this.first ... IFeature
   *  this.rest ... ILoFeature
   * methods:
   *  this.append(ILoFeature) ... ILoFeature
   *  this.getRestaurants() ... ILoFeature
   * methods for fields:
   *  this.features.restaurantInfo() ... String
   *  this.features.foodinessRating() ... double
   *  this.features.append(ILoFeature) ... ILoFeature
   */

  public ILoFeature append(ILoFeature acc) {
    return new ConsLoFeature(this.first, this.rest.append(acc));
  }

  public ILoFeature getRestaurants() {
    if (this.first.isShuttle()) {
      return first.getDestination().getFeatures().getRestaurants().append(rest.getRestaurants());
    } else if (this.first.isRestaurant()) {
      return new ConsLoFeature(this.first, this.rest.getRestaurants());
    } else {
      return this.rest.getRestaurants();
    }

  }

  public double foodinessRating() {
    return this.getRestaurants().foodinessRatingAcc(0,0);
  }

  public double foodinessRatingAcc(int acc, double acc2) {
    if (this.first.isRestaurant()) {
      return this.rest.foodinessRatingAcc(acc + 1, this.first.getRating() + acc2);
    } else {
      return this.rest.foodinessRatingAcc(acc, acc2);
    }
  }

  public String restaurantInfo() {
    return this.getRestaurants().restaurantInfoA();
  }

  public String restaurantInfoA() {
    if (this.first.isRestaurant()) {
      if (this.rest.foodinessRating() == 0) {
        return this.first.getName() + " (" + this.first.getType() + this.rest.restaurantInfoA();
      } else {
        return this.first.getName() + " (" + this.first.getType() + "), "
              + this.rest.restaurantInfoA();
      }
    } else {
      return this.rest.restaurantInfoA();
    }
  }
}

class MtLoFeature implements ILoFeature {

  /* fields: none
   * methods:
   *  this.append(ILoFeature) ... ILoFeature
   *  this.getRestaurants() ... ILoFeature
   * methods for fields:
   *  this.features.restaurantInfo() ... String
   *  this.features.foodinessRating() ... double
   *  this.features.append(ILoFeature) ... ILoFeature
   */

  public ILoFeature getRestaurants() {
    return this;
  }

  public double foodinessRating() {
    return 0;
  }

  public double foodinessRatingAcc(int acc, double acc2) {
    return acc2 / acc;
  }

  public String restaurantInfo() {
    return "";
  }

  public String restaurantInfoA() {
    return ")";
  }

  public ILoFeature append(ILoFeature acc) {
    return acc;
  }
}

interface IFeature {
  //check if a feature is a restaurant
  boolean isRestaurant();

  //check if a feature is a shuttle
  boolean isShuttle();

  //accessor for averageRating
  double getRating();

  //accessor for name
  String getName();

  //accessor for type
  String getType();

  //accessor for destination
  Place getDestination();
}

class Restaurant implements IFeature {
  String name;
  String type;
  double averageRating;

  Restaurant(String name, String type, double averageRating) {
    this.name = name;
    this.type = type;
    this.averageRating = averageRating;
  }

  /* fields:
   *  this.name ... String
   *  this.type ... String
   *  this.averageRating ... double
   * methods:
   *  this.isRestaurant() ... boolean
   *  this.getRating() ... double
   *  this.getName() ... String
   *  this.getType() ... String
   *  this.isShuttle() ... boolean
   *  this.getDestination() ... Place
   * methods for fields: none
   */

  public boolean isRestaurant() {
    return true;
  }

  public double getRating() {
    return this.averageRating;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public boolean isShuttle() {
    return false;
  }

  public Place getDestination() {
    return null;
  }
}

class Venue implements IFeature {
  String name;
  String type;
  int capacity;

  Venue(String name, String type, int capacity) {
    this.name = name;
    this.type = type;
    this.capacity = capacity;
  }

  /* fields:
   *  this.name ... String
   *  this.type ... String
   *  this.capacity ... int
   * methods:
   *  this.isRestaurant() ... boolean
   *  this.getRating() ... double
   *  this.getName() ... String
   *  this.getType() ... String
   *  this.isShuttle() ... boolean
   *  this.getDestination() ... Place
   * methods for fields: none
   */

  public boolean isRestaurant() {
    return false;
  }

  public double getRating() {
    return 0;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public boolean isShuttle() {
    return false;
  }

  public Place getDestination() {
    return null;
  }
}

class ShuttleBus implements IFeature {
  String name;
  Place destination;

  ShuttleBus(String name, Place destination) {
    this.name = name;
    this.destination = destination;
  }

  /* fields:
   *  this.name ... String
   *  this.destination ... Place
   * methods:
   *  this.isRestaurant() ... boolean
   *  this.getRating() ... double
   *  this.getName() ... String
   *  this.getType() ... String
   *  this.isShuttle() ... boolean
   *  this.getDestination() ... Place
   * methods for fields:
   *  this.destination.getFeatures() ... ILoFeature
   */

  public boolean isRestaurant() {
    return false;
  }

  public double getRating() {
    return 0;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return "none";
  }

  public boolean isShuttle() {
    return true;
  }

  public Place getDestination() {
    return this.destination;
  }
}
