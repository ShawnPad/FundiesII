import tester.Tester;

// a piece of media
interface IMedia {

  // is this media really old?
  boolean isReallyOld();

  // are captions available in this language?
  boolean isCaptionAvailable(String language);

  // a string showing the proper display of the media
  String format();
}

//abstraction of media formats
abstract class AMedia implements IMedia {
  String title;
  ILoString captionOptions; // available captions

  /* fields:
   *  this.title ... String
   *  this.captionOptions ... ILoString
   * methods:
   *  this.isReallyOld() ... boolean
   *  this.isCaptionAvailable(String language) ... boolean
   * methods for fields:
   *  this.captionOptions.orMap(String str) ... boolean
   */

  AMedia(String title, ILoString captionOptions) {
    this.title = title;
    this.captionOptions = captionOptions;
  }

  //default value for really old is false
  public boolean isReallyOld() {
    return false;
  }

  //checks if caption is available
  public boolean isCaptionAvailable(String language) {
    return captionOptions.orMap(language);
  }
}

// represents a movie
class Movie extends AMedia {
  int year;

  /* fields:
   *  this.title ... String
   *  this.captionOptions ... ILoString
   *  this.year ... int
   * methods:
   *  this.isReallyOld() ... boolean
   *  this.isCaptionAvailable(String language) ... boolean
   *  this.format() ... String
   * methods for fields:
   *  this.captionOptions.orMap(String str) ... boolean
   */

  Movie(String title, int year, ILoString captionOptions) {
    super(title, captionOptions);
    this.year = year;
  }

  //formats title as described
  public String format() {
    return title + " (" + year + ")";
  }

  //checks if the move is old if it is older than 1930
  public boolean isReallyOld() {
    return year < 1930;
  }
}

// represents a TV episode
class TVEpisode extends AMedia {
  String showName;
  int seasonNumber;
  int episodeOfSeason;

  /* fields:
   *  this.title ... String
   *  this.captionOptions ... ILoString
   *  this.showName ... String
   *  this.seasonNumber ... int
   *  this.episodeOfSeason ... int
   * methods:
   *  this.isReallyOld() ... boolean
   *  this.isCaptionAvailable(String language) ... boolean
   *  this.format() ... String
   * methods for fields:
   *  this.captionOptions.orMap(String str) ... boolean
   */

  TVEpisode(String title, String showName, int seasonNumber, int episodeOfSeason,
            ILoString captionOptions) {
    super(title, captionOptions);
    this.showName = showName;
    this.seasonNumber = seasonNumber;
    this.episodeOfSeason = episodeOfSeason;
  }

  //formats title as described
  public String format() {
    return showName + " " + seasonNumber + "." + episodeOfSeason + " - " + title;
  }
}

// represents a YouTube video
class YTVideo extends AMedia {
  String channelName;

  /* fields:
   *  this.title ... String
   *  this.captionOptions ... ILoString
   *  this.channelName ... String
   * methods:
   *  this.isReallyOld() ... boolean
   *  this.isCaptionAvailable(String language) ... boolean
   *  this.format() ... String
   * methods for fields:
   *  this.captionOptions.orMap(String str) ... boolean
   */

  public YTVideo(String title, String channelName, ILoString captionOptions) {
    super(title, captionOptions);
    this.channelName = channelName;
  }

  //formats title as described
  public String format() {
    return title + " by " + channelName;
  }

}

// lists of strings
interface ILoString {
  static ILoString makeList(String... b) {
    if (b.length == 1) {
      return new ConsLoString(b[0], new MtLoString());
    }
    String[] a = new String[b.length - 1];
    for (int i = 0; i < a.length; i++) {
      a[i] = b[i + 1];
    }
    if (a.length == 0) {
      return new MtLoString();
    } else {
      return new ConsLoString(b[0], makeList(a));
    }
  }

  boolean orMap(String str);

}

// an empty list of strings
class MtLoString implements ILoString {

  public boolean orMap(String str) {
    return true;
  }

}

// a non-empty list of strings
class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  public boolean orMap(String str) {
    return first.equals(str) && rest.orMap(str);
  }
}

class ExamplesMedia {
  IMedia movie1 = new Movie("A Movie" ,1900, ILoString.makeList("English"));
  IMedia movie2 = new Movie("A Movie two" ,2030, ILoString.makeList("English", "Spanish"));
  IMedia tv1 = new TVEpisode("A Show", "With Bob",
        10, 4, ILoString.makeList("English"));
  IMedia tv2 = new TVEpisode("Not A Show", "With Joe",
        1, 4, ILoString.makeList("French"));
  IMedia YT1 = new YTVideo("I poo","JoeDoes", ILoString.makeList("English"));
  IMedia YT2 = new YTVideo("I sat","JoeDoes", ILoString.makeList("English"));

  //test isCaptionAvailable
  boolean testIsCaptionAvailable(Tester t) {
    return t.checkExpect(movie1.isCaptionAvailable("English"),true)
          && t.checkExpect(movie1.isCaptionAvailable("Spanish"),false);
  }

  //test isReallyOld
  boolean testIsReallyOld(Tester t) {
    return t.checkExpect(movie1.isReallyOld(),true)
          && t.checkExpect(movie2.isReallyOld(),false)
          && t.checkExpect(tv1.isReallyOld(),false)
          && t.checkExpect(YT1.isReallyOld(),false);
  }

  //test format
  boolean testFormat(Tester t) {
    return t.checkExpect(movie1.format(),"A Movie (1900)")
          && t.checkExpect(tv2.format(),"With Joe 1.4 - Not A Show")
          && t.checkExpect(YT2.format(),"I sat by JoeDoes");
  }
}