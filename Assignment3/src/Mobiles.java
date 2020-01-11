import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import javalib.worldcanvas.*;
import java.awt.*;

class Examples {
  IMobile exampleSimple = new Simple(2,20,Color.blue);
  IMobile exampleComplex = new Complex(1,9, 3,
        new Simple(1,36,Color.blue),
        new Complex(2,8,1,
              new Simple(1,12,Color.red),
              new Complex(2,5,3,
                    new Simple(2,36, Color.red),
                    new Simple(1,60,Color.green))));
  IMobile test1 = new Complex(2, 2,3, exampleSimple, exampleSimple);
  IMobile test2 = new Complex(3,9, 3,
        new Simple(2,36,Color.blue),
        new Complex(3,8,1,
              new Simple(3,12,Color.red),
              new Complex(2,5,3,
                    new Simple(2,36, Color.red),
                    new Simple(3,60,Color.green))));

  //test totalHeight
  boolean testTotalHeight(Tester t) {
    return t.checkExpect(exampleSimple.totalHeight(),4)
          && t.checkExpect(exampleComplex.totalHeight(),12);
  }

  //test isBalanced
  boolean testIsBalanced(Tester t) {
    return t.checkExpect(exampleSimple.isBalanced(),true)
          && t.checkExpect(exampleComplex.isBalanced(),true);
  }

  //test buildMobile
  boolean testBuildMobile(Tester t) {
    return t.checkExpect(exampleSimple.buildMobile(exampleSimple,1,4),
          new Complex(1,2,2, exampleSimple, exampleSimple))
          && t.checkExpect(exampleComplex.buildMobile(exampleSimple,1,30),
          new Complex(1,3,27,exampleComplex,exampleSimple));
  }

  //test curWidth
  boolean testCurWidth(Tester t) {
    return t.checkExpect(exampleSimple.curWidth(), 2)
          && t.checkExpect(test1.curWidth(), 7)
          && t.checkExpect(exampleComplex.curWidth(), 21);
  }

  //test drawMobile
  boolean testDrawMobile(Tester t) {
    WorldCanvas c = new WorldCanvas(500, 500);
    WorldScene s = new WorldScene(500, 500);
    return c.drawScene(s.placeImageXY(new ScaleImage(test2.buildMobile(test2,3,20).drawMobile(), 2) , 250, 100))
          && c.show();
  }
}

//interface to group simple and complex mobiles
interface IMobile {
  //gets total weight of a mobile
  int totalWeight();

  //gets total height of a mobile
  int totalHeight();

  //is the torque on the left equal to the right?
  boolean isBalanced();

  //builds a new mobile from two mobiles
  IMobile buildMobile(IMobile that, int length, int totalStrut);

  //gets the total width of a mobile stationary on a surface
  int curWidth();

  //helper for curWidth
  int leftWidth(int acc);

  //helper for curWidth
  int rightWidth(int acc);

  //draws the mobile on a canvas
  WorldImage drawMobile();

}

//a class to represent simple mobile
class Simple implements IMobile {
  int length;
  int weight;
  Color color;

  public Simple(int length, int weight, Color color) {
    this.length = length;
    this.weight = weight;
    this.color = color;
  }

  /* fields:
   *  this.length ... int
   *  this.weight ... int
   *  this.color ... Color
   * methods:
   *  this.totalWeight() ... int
   *  this.totalHeight() ... int
   *  this.isBalanced() ... boolean
   *  this.buildMobile() ... IMobile
   *  this.curWidth() ... int
   *  this.drawMobile() ... WorldImage
   * methods for fields: none
   */

  @Override
  //getter for weight
  public int totalWeight() {
    return this.weight;
  }

  @Override
  //applies algo to get height of simple
  public int totalHeight() {
    return this.length + this.weight / 10;
  }

  @Override
  //a simple is always balanced
  public boolean isBalanced() {
    return true;
  }

  @Override
  //builds a complex from given mobiles
  public IMobile buildMobile(IMobile that, int nLength, int totalStrut) {
    int center = totalStrut * that.totalWeight() / (this.totalWeight() + that.totalWeight());
    return new Complex(nLength, center,totalStrut - center, this, that);
  }

  @Override
  //returns width of a mobile
  public int curWidth() {
    return ((int) Math.ceil(weight / 10.0));
  }

  @Override
  //base case left side helper
  public int leftWidth(int acc) {
    return ((int) Math.ceil(curWidth() / 2.0)) + acc;
  }

  @Override
  //base case right side helper
  public int rightWidth(int acc) {
    return ((int) Math.ceil(curWidth() / 2.0)) + acc;
  }

  @Override
  //draws a simple mobile
  public WorldImage drawMobile() {
    int scale = 10;
    WorldImage w = new OverlayOffsetImage(
          new RectangleImage(curWidth() * scale,
                ((int) Math.ceil(weight / 10.0)) * scale, OutlineMode.SOLID, color),
          0, -((weight / 20.0 + length / 2.0) * scale),
          new LineImage(new Posn(0, length * scale), Color.black));
    return w.movePinhole(0,-w.getHeight() / 2.0);
  }
}

//a class to represent complex mobiles
class Complex implements IMobile {
  int length;
  int leftside;
  int rightside;
  IMobile left;
  IMobile right;

  public Complex(int length, int leftside, int rightside, IMobile left, IMobile right) {
    this.length = length;
    this.leftside = leftside;
    this.rightside = rightside;
    this.left = left;
    this.right = right;
  }

  /* fields:
   *  this.length ... int
   *  this.leftside ... int
   *  this.rightside ... int
   *  this.left ... IMobile
   *  this.right ... IMobile
   * methods:
   *  this.totalWeight() ... int
   *  this.totalHeight() ... int
   *  this.isBalanced() ... boolean
   *  this.buildMobile() ... IMobile
   *  this.curWidth() ... int
   *  this.drawMobile() ... WorldImage
   * methods for fields:
   *  this.left.totalWeight() ... int
   *  this.left.totalHeight() ... int
   *  this.left.isBalanced() ... boolean
   *  this.left.buildMobile() ... IMobile
   *  this.left.curWidth() ... int
   *  this.left.drawMobile() ... WorldImage
   *  this.right.totalWeight() ... int
   *  this.right.totalHeight() ... int
   *  this.right.isBalanced() ... boolean
   *  this.right.buildMobile() ... IMobile
   *  this.right.curWidth() ... int
   *  this.right.drawMobile() ... WorldImage
   */

  @Override
  //adds the total weight of both sides of mobile
  public int totalWeight() {
    return left.totalWeight() + right.totalWeight();
  }

  @Override
  //finds the maximum height of the mobile
  public int totalHeight() {
    return this.length + Math.max(left.totalHeight(),right.totalHeight());
  }

  @Override
  //compares torque of sides if they are equal
  public boolean isBalanced() {
    return leftside * left.totalWeight() == rightside * right.totalWeight();
  }

  @Override
  //combines mobiles
  public IMobile buildMobile(IMobile that, int nLength, int totalStrut) {
    int center = totalStrut * that.totalWeight() / (this.totalWeight() + that.totalWeight());
    return new Complex(nLength, center,totalStrut - center, this, that);
  }

  @Override
  //finds the total width of the mobile
  public int curWidth() {
    return leftWidth(0) + rightWidth(0);
  }

  @Override
  //helps find largest width left
  public int leftWidth(int acc) {
    return Math.max(this.left.leftWidth(acc + this.leftside),
          this.right.leftWidth(acc + (this.rightside * -1)));
  }

  @Override
  //helps find largest width right
  public int rightWidth(int acc) {
    return Math.max(this.right.rightWidth(acc + this.rightside),
          this.left.rightWidth(acc + (this.leftside * -1)));
  }

  @Override
  //draws complex mobiles
  public WorldImage drawMobile() {
    int scale = 10;
    return new OverlayImage(new OverlayImage(new OverlayImage(new OverlayImage(
          new LineImage(new Posn(0, length * scale),
                Color.black).movePinhole(0,-length / 2.0 * scale),
          new LineImage(new Posn(leftside * scale,0),
                Color.black).movePinhole(leftside / 2.0 * scale, - length * scale)),
          right.drawMobile().movePinhole(- rightside * scale, - length * scale)),
          left.drawMobile().movePinhole(leftside * scale, - length * scale)),
          new LineImage(new Posn(-rightside * scale,0),
                Color.black).movePinhole(-rightside / 2.0 * scale, - length * scale));
  }
}