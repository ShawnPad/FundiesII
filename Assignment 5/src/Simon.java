import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import javalib.worldcanvas.*;
import java.awt.*;
import java.util.Random;

//World Class for big bangr
class SimonWorld extends World {
  ILoInt seq;
  ILoInt input;
  ILoBalls b;
  int state;
  int fade;
  boolean theGameIsOver;
  int current;
  Random rand;

  public SimonWorld(boolean gg, Random rand) {
    this.b = ILoBalls.makeList(new Balls(0),
          new Balls(1),
          new Balls(2),
          new Balls(3));
    this.theGameIsOver = gg;
    this.rand = rand;
  }

  public SimonWorld(int current, ILoInt seq, ILoInt input, Random rand) {
    this.b = ILoBalls.makeList(new Balls(0),
          new Balls(1),
          new Balls(2),
          new Balls(3));
    this.seq = seq;
    this.input = input;
    this.state = 3;
    this.fade = 0;
    this.theGameIsOver = false;
    this.current = current;
    this.rand = rand;
  }

  public SimonWorld(ILoInt seq, ILoInt input, Random rand) {
    this.b = ILoBalls.makeList(new Balls(0),
          new Balls(1),
          new Balls(2),
          new Balls(3));
    this.seq = seq;
    this.input = input;
    this.state = 2;
    this.fade = 0;
    this.theGameIsOver = false;
    this.rand = rand;
  }

  public SimonWorld(ILoInt seq, int fade, Random rand) {
    this.b = ILoBalls.makeList(new Balls(0),
          new Balls(1),
          new Balls(2),
          new Balls(3));
    this.seq = seq;
    this.input = new MtLoInt();
    this.state = 1;
    this.fade = fade;
    this.theGameIsOver = false;
    this.rand = rand;
  }

  public SimonWorld() {
    this(new Random());
  }

  public SimonWorld(Random rand) {
    this.b = ILoBalls.makeList(new Balls(0),
          new Balls(1),
          new Balls(2),
          new Balls(3));
    this.seq = new MtLoInt();
    this.input = new MtLoInt();
    this.state = 0;
    this.fade = 0;
    this.theGameIsOver = false;
    this.rand = rand;
  }

  //draws the current world
  public WorldScene makeScene() {
    if (state == 0) {
      return b.draw(new WorldScene(1000, 1000));
    } else if (state == 1) {
      return seq.draw(b.draw(new WorldScene(1000, 1000)), fade);
    } else if (state == 3) {
      return new Balls(current, true).draw(
            b.draw(new WorldScene(1000, 1000)));
    } else {
      return b.draw(new WorldScene(1000, 1000));
    }
  }

  //when moused pressed check if on ball and darken
  public World onMousePressed(Posn mouse) {
    System.out.println("*");
    int result = b.insideWhat(mouse);
    if (result == -1) {
      return new SimonWorld(seq, input, rand);
    } else {
      return new SimonWorld(b.insideWhat(mouse), seq, input, rand);
    }
  }

  //when mouse released check if on ball and add to input list
  public World onMouseReleased(Posn mouse) {
    int result = b.insideWhat(mouse);
    if (result == -1) {
      return new SimonWorld(seq, input, rand);
    } else {
      return new SimonWorld(seq, new ConsLoInt(result,input), rand);
    }
  }

  //check if game over and display end screen if true
  public WorldEnd worldEnds() {
    if (theGameIsOver) {
      return new WorldEnd(true, this.makeAFinalScene());
    } else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  //display end screen
  public WorldScene makeAFinalScene() {
    return b.draw(new WorldScene(1000, 1000)).placeImageXY(
          new TextImage("You Lose",20, Color.black),500,500);

  }

  //game logic ran every tick
  public World onTick() {
    if (state == 0) {
      return new SimonWorld(new ConsLoInt(rand.nextInt(4), seq), 0, rand);
    } else if (state == 1) {
      int temp = (fade + 1) % 10;
      if (temp == seq.length()) {
        return new SimonWorld(seq, input, rand);
      } else {
        return new SimonWorld(seq, temp, rand);
      }
    } else if (state == 2 && !input.compare(seq)) {
      return new SimonWorld(true, rand);
    } else {
      if (input.length() == seq.length()) {
        return new SimonWorld(new ConsLoInt(rand.nextInt(4), seq), 0, rand);
      }
      return new SimonWorld(seq, input, rand);
    }
  }
}



//interface to hold list of integer to store sequence and input
interface ILoInt {

  //personal convenience method to make lists
  static ILoInt makeList(int... b) {
    if (b.length == 1) {
      return new ConsLoInt(b[0], new MtLoInt());
    } else if (b.length == 0) {
      return new MtLoInt();
    }
    int[] a = new int[b.length - 1];
    for (int i = 0; i < a.length; i++) {
      a[i] = b[i + 1];
    }
    if (a.length == 0) {
      return new MtLoInt();
    } else {
      return new ConsLoInt(b[0], makeList(a));
    }
  }

  //draw the sequence on screen by fade which is current ball to darken
  WorldScene draw(WorldScene acc, int fade);

  //returns length of list
  int length();

  //gets element of list
  int getElement(int acc);

  //compares this list with second list if corresponding elements are equal
  boolean compare(ILoInt second);

  //helps compare by comparing the reversed lists
  boolean compareHelp(ILoInt second);

  //helps comparehelp by moving second list
  boolean compareHelp2(ILoInt second);

  //reverses this list
  ILoInt reverse();

  //reverse accumulator
  ILoInt reverse(ILoInt acc);

  //compare this list with a int
  boolean compareInt(int a);

}

//class to represent the int first and ILoInt rest
class ConsLoInt implements ILoInt {
  int first;
  ILoInt rest;

  public ConsLoInt(int first, ILoInt rest) {
    this.first = first;
    this.rest = rest;
  }

  //draws the darkened element given by fade
  public WorldScene draw(WorldScene acc, int fade) {
    return new Balls(this.getElement(this.length() - fade - 1),true).draw(acc);
  }

  //gets the acc element in list
  public int getElement(int acc) {
    if (acc == 0) {
      return first;
    } else {
      return rest.getElement(acc - 1);
    }
  }

  //compares this list with second list
  public boolean compare(ILoInt second) {
    return this.reverse().compareHelp(second.reverse());
  }

  //helper to compare lists in reverse order
  public boolean compareHelp(ILoInt second) {
    return second.compareInt(first) && second.compareHelp2(rest);
  }

  //helper to move second list
  public boolean compareHelp2(ILoInt second) {
    return rest.compareHelp(second);
  }

  //reverse list accumulator
  public ILoInt reverse(ILoInt acc) {
    return rest.reverse(new ConsLoInt(first, acc));
  }

  //reverse accumulator starter
  public ILoInt reverse() {
    return this.reverse(new MtLoInt());
  }

  //is the first of this list equal to int a
  public boolean compareInt(int a) {
    return a == first;
  }

  //returns the length of this list
  public int length() {
    return 1 + rest.length();
  }
}

//class to represent the empty list
class MtLoInt implements ILoInt {

  //base case return accumulator of drawn scene
  public WorldScene draw(WorldScene acc, int fade) {
    return acc;
  }

  //base case to add to mt list is 0 length
  public int length() {
    return 0;
  }

  //element not found base case
  public int getElement(int acc) {
    return -1;
  }

  //base case andmap
  public boolean compare(ILoInt second) {
    return true;
  }

  //base case andmap
  public boolean compareHelp(ILoInt second) {
    return true;
  }

  //base case andmap
  public boolean compareHelp2(ILoInt second) {
    return true;
  }

  //return the reversed list which is the accumulator
  public ILoInt reverse(ILoInt acc) {
    return acc;
  }

  //the reverse of an mt is mt
  public ILoInt reverse() {
    return this;
  }

  //if comparing an int with mt return true because the input
  // list is not complete but not game over
  public boolean compareInt(int a) {
    return true;
  }
}

//interface to represent a list of balls
interface ILoBalls {

  //personal convenience method to make list
  static ILoBalls makeList(Balls... b) {
    if (b.length == 1) {
      return new ConsLoBalls(b[0], new MtLoBalls());
    } else if (b.length == 0) {
      return new MtLoBalls();
    }
    Balls[] a = new Balls[b.length - 1];
    for (int i = 0; i < a.length; i++) {
      a[i] = b[i + 1];
    }
    if (a.length == 0) {
      return new MtLoBalls();
    } else {
      return new ConsLoBalls(b[0], makeList(a));
    }
  }

  //draw balls on to the acc
  WorldScene draw(WorldScene acc);

  //what ball is this posn in? returns -1 if outside
  int insideWhat(Posn m);

}

//class to represent first ball and rest of list
class ConsLoBalls implements ILoBalls {
  Balls first;
  ILoBalls rest;

  public ConsLoBalls(Balls first, ILoBalls rest) {
    this.first = first;
    this.rest = rest;
  }

  //draw first ball on accumulator
  public WorldScene draw(WorldScene acc) {
    return rest.draw(first.draw(acc));
  }

  //check what ball mouse is inside
  public int insideWhat(Posn m) {
    if (first.inside(m)) {
      return first.getId();
    } else {
      return rest.insideWhat(m);
    }
  }

}

//class to represent empty list
class MtLoBalls implements ILoBalls {

  //return the accumulated scene
  public WorldScene draw(WorldScene acc) {
    return acc;
  }

  //if outside return -1
  public int insideWhat(Posn m) {
    return -1;
  }
}

//class to represent balls on screen
class Balls {
  int rad;
  Posn pos;
  int c;

  public Balls(int ball) {
    this(ball, false);
  }

  public Balls(int ball, boolean dark) {
    switch (ball) {
      case 0:
        this.pos = new Posn(250,250);
        break;
      case 1:
        this.pos = new Posn(750,250);
        break;
      case 2:
        this.pos = new Posn(250,750);
        break;
      case 3:
        this.pos = new Posn(750,750);
        break;
      default:
    }
    if (dark) {
      this.c = ball + 4;
    } else {
      this.c = ball;
    }
    this.rad = 100;
  }

  //returns the id of this ball
  public int getId() {
    return c;
  }

  //draw the ball on the accumulator
  public WorldScene draw(WorldScene acc) {
    return acc.placeImageXY(new CircleImage(rad,OutlineMode.SOLID,getColor()),pos.x,pos.y);
  }

  //this id represents what color
  public Color getColor() {
    switch (c) {
      case 0:
        return Color.yellow;
      case 1:
        return Color.blue;
      case 2:
        return Color.pink;
      case 3:
        return Color.green;
      case 4:
        return Color.yellow.darker();
      case 5:
        return Color.blue.darker();
      case 6:
        return Color.pink.darker();
      case 7:
        return Color.green.darker();
      default:
        return null;
    }
  }

  //check is posn m is inside this ball
  boolean inside(Posn m) {
    return Math.sqrt((m.x - pos.x) * (m.x - pos.x) + (m.y - pos.y) * (m.y - pos.y)) < rad;
  }

}

//Class to test game
class ExamplesSimon {
  SimonWorld START = new SimonWorld();
  //State 0 starting
  SimonWorld STARTTester0 = new SimonWorld(new Random(1));
  //State 1 draw sequence
  //With seed 1 random sequence is: 2,0,1...
  SimonWorld STARTTester1 = new SimonWorld(ILoInt.makeList(2,0,1), 0, new Random(1));
  //State 2 wait user input
  SimonWorld STARTTester2 = new SimonWorld(ILoInt.makeList(2,0,1),
        ILoInt.makeList(1), new Random(1));
  SimonWorld STARTTester2Fail = new SimonWorld(ILoInt.makeList(2,0,1),
        ILoInt.makeList(0), new Random(1));
  //State 3 draw user input
  SimonWorld STARTTester3 = new SimonWorld(1, ILoInt.makeList(2,0,1),
        ILoInt.makeList(1), new Random(1));
  SimonWorld GameOver = new SimonWorld(true,  new Random(1));
  WorldScene EmptyWorld = new WorldScene(1000, 1000);
  ILoBalls Buttons = ILoBalls.makeList(new Balls(0),
        new Balls(1),
        new Balls(2),
        new Balls(3));
  WorldScene defaultWorld = Buttons.draw(EmptyWorld);
  WorldScene darken1World = ILoInt.makeList(2,0,1).draw(defaultWorld, 0);

  //test SimonWorld
  //test makeScene
  boolean testMakeScene(Tester t) {
    return t.checkExpect(STARTTester0.makeScene(), defaultWorld)
          && t.checkExpect(STARTTester1.makeScene(), darken1World)
          && t.checkExpect(STARTTester2.makeScene(), defaultWorld)
          && t.checkExpect(STARTTester3.makeScene(), darken1World);
  }

  //test onMousePressed
  boolean testOnMousePressed(Tester t) {
    return t.checkExpect(STARTTester2.onMousePressed(new Posn(250,250)),
          new SimonWorld(0, ILoInt.makeList(2,0,1), ILoInt.makeList(1), new Random(1)))
          && t.checkExpect(STARTTester2.onMousePressed(new Posn(0,0)),
          STARTTester2);
  }

  //test onMouseReleased
  boolean testOnMouseReleased(Tester t) {
    return t.checkExpect(STARTTester3.onMouseReleased(new Posn(250,250)),
          new SimonWorld(ILoInt.makeList(2,0,1), ILoInt.makeList(0,1), new Random(1)))
          && t.checkExpect(STARTTester2.onMouseReleased(new Posn(0,0)),
          STARTTester2);
  }

  //test worldEnds
  boolean testWorldEnds(Tester t) {
    return t.checkExpect(GameOver.worldEnds(),
          new WorldEnd(true, defaultWorld.placeImageXY(
                new TextImage("You Lose",20, Color.black),500,500)))
          && t.checkExpect(STARTTester0.worldEnds(),
          new WorldEnd(false, defaultWorld));
  }

  //test makeAFinalScene
  boolean testMakeAFinalScene(Tester t) {
    return t.checkExpect(GameOver.makeAFinalScene(), defaultWorld.placeImageXY(
          new TextImage("You Lose",20, Color.black),500,500));
  }

  //test onTick
  boolean testOnTick(Tester t) {
    return t.checkExpect(STARTTester0.onTick(),
          new SimonWorld(ILoInt.makeList(2), 0 ,new Random(1)))
          && t.checkExpect(STARTTester1.onTick(),
          new SimonWorld(ILoInt.makeList(2,0,1),1, new Random(1)))
          && t.checkExpect(STARTTester2Fail.onTick(),
          new SimonWorld(true, new Random(1)))
          && t.checkExpect(STARTTester3.onTick(),
          new SimonWorld(ILoInt.makeList(2,0,1), ILoInt.makeList(1), new Random(1)));
  }

  //Test ILoInt
  //Test draw
  boolean testDraw(Tester t) {
    return t.checkExpect(ILoInt.makeList(1).draw(defaultWorld, 0), darken1World);
  }

  //test length
  boolean testLength(Tester t) {
    return t.checkExpect(ILoInt.makeList(1,2,3,4,5).length(),5)
          && t.checkExpect(ILoInt.makeList().length(),0);
  }

  //test getElement
  boolean testGetElement(Tester t) {
    return t.checkExpect(ILoInt.makeList(1,2,3,4,5).getElement(1),2)
          && t.checkExpect(ILoInt.makeList().getElement(1),-1);
  }

  //test Compare
  boolean testCompare(Tester t) {
    return t.checkExpect(ILoInt.makeList(1,2).compare(ILoInt.makeList(2)), true)
          && t.checkExpect(ILoInt.makeList(1,2).compare(ILoInt.makeList(1,2)), true)
          && t.checkExpect(ILoInt.makeList(1,2).compare(ILoInt.makeList(0)), false)
          && t.checkExpect(ILoInt.makeList(1,2).compare(ILoInt.makeList()), true)
          && t.checkExpect(ILoInt.makeList().compare(ILoInt.makeList()), true)
          && t.checkExpect(ILoInt.makeList(1,2).compare(ILoInt.makeList(1,5)), false);
  }

  //test CompareHelp
  boolean testCompareHelp(Tester t) {
    return t.checkExpect(ILoInt.makeList(2,1).compareHelp(ILoInt.makeList(2)), true)
          && t.checkExpect(ILoInt.makeList(2,1).compareHelp(ILoInt.makeList(2,1)), true)
          && t.checkExpect(ILoInt.makeList(2,1).compareHelp(ILoInt.makeList(0)), false)
          && t.checkExpect(ILoInt.makeList(2,1).compareHelp(ILoInt.makeList()), true)
          && t.checkExpect(ILoInt.makeList().compareHelp(ILoInt.makeList()), true)
          && t.checkExpect(ILoInt.makeList(2,1).compareHelp(ILoInt.makeList(5,1)), false);
  }

  //test Reverse
  boolean testReverse(Tester t) {
    return t.checkExpect(ILoInt.makeList(1,2,3).reverse(),ILoInt.makeList(3,2,1))
          && t.checkExpect(ILoInt.makeList(3).reverse(),ILoInt.makeList(3))
          && t.checkExpect(ILoInt.makeList().reverse(),ILoInt.makeList());
  }

  //test Reverse accumulator
  boolean testReverseA(Tester t) {
    return t.checkExpect(ILoInt.makeList(1,2,3).reverse(new MtLoInt()),ILoInt.makeList(3,2,1))
          && t.checkExpect(ILoInt.makeList(3).reverse(new MtLoInt()),ILoInt.makeList(3))
          && t.checkExpect(ILoInt.makeList().reverse(new MtLoInt()),ILoInt.makeList());
  }

  //test compareInt
  boolean testCompareInt(Tester t) {
    return t.checkExpect(ILoInt.makeList(1,2,3).compareInt(1), true)
          && t.checkExpect(ILoInt.makeList(1,2,3).compareInt(2), false)
          && t.checkExpect(ILoInt.makeList().compareInt(1), true);
  }

  //Test ILoBalls
  //test draw in balls
  boolean testDrawB(Tester t) {
    return t.checkExpect(Buttons.draw(EmptyWorld), defaultWorld);
  }

  //test insideWhat
  boolean testInsideWhat(Tester t) {
    return t.checkExpect(Buttons.insideWhat(new Posn(250,250)), 0)
          && t.checkExpect(Buttons.insideWhat(new Posn(750,250)), 1)
          && t.checkExpect(Buttons.insideWhat(new Posn(250,750)), 2)
          && t.checkExpect(Buttons.insideWhat(new Posn(750,750)), 3)
          && t.checkExpect(Buttons.insideWhat(new Posn(0,0)), -1);
  }

  //Test Balls
  //test getID
  boolean testGetId(Tester t) {
    return t.checkExpect(new Balls(1).getId(), 1)
          && t.checkExpect(new Balls(2).getId(), 2);
  }

  //test draw in ball
  boolean testDrawBall(Tester t) {
    return t.checkExpect(new Balls(1).draw(EmptyWorld),
          EmptyWorld.placeImageXY(new CircleImage(100,OutlineMode.SOLID, Color.blue),750, 250));
  }

  //test getColor
  boolean testGetColor(Tester t) {
    return t.checkExpect(new Balls(1).getColor(), Color.blue)
          && t.checkExpect(new Balls(2).getColor(), Color.pink)
          && t.checkExpect(new Balls(5).getColor(), Color.blue.darker())
          && t.checkExpect(new Balls(6).getColor(), Color.pink.darker());
  }

  //test inside
  boolean testInside(Tester t) {
    return t.checkExpect(new Balls(1).inside(new Posn(750,250)), true)
          && t.checkExpect(new Balls(1).inside(new Posn(750,0)), false);
  }

  //Play game full random
  boolean testSimon(Tester t) {
    WorldCanvas c = new WorldCanvas(1000, 1000);
    return START.bigBang(1000,1000,0.5);
  }
}