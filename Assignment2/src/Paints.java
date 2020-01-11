import tester.Tester;

import java.awt.Color;

class ExamplesPaint {
  IPaint red = new Solid("red", new Color(255, 0, 0));
  IPaint green = new Solid("green", new Color(0, 255, 0));
  IPaint blue = new Solid("blue", new Color(0, 0, 255));
  IPaint purple = new Combo("purple", new Blend(red, blue));
  IPaint darkPurple = new Combo("dark purple", new Darken(purple));
  IPaint khaki = new Combo("khaki", new Blend(red,green));
  IPaint yellow = new Combo("yellow", new Brighten(khaki));
  IPaint mauve = new Combo("mauve", new Blend(purple,khaki));
  IPaint pink = new Combo("pink", new Brighten(mauve));
  IPaint coral = new Combo("coral", new Blend(pink,khaki));
  IPaint nasty1 = new Combo("nasty1", new Blend(khaki,darkPurple));
  IPaint nasty2 = new Combo("nasty2", new Blend(coral,nasty1));
  IPaint nasty3 = new Combo("nasty3", new Darken(nasty2));
  IPaint nasty4 = new Combo("nasty4", new Brighten(nasty1));

  boolean testGetFinalColor(Tester t) {
    return t.checkExpect(this.red.getFinalColor(), new Color(255, 0, 0))
          && t.checkExpect(this.purple.getFinalColor(), new Color(127, 0, 127))
          && t.checkExpect(this.darkPurple.getFinalColor(), new Color(88, 0, 88))
          && t.checkExpect(this.yellow.getFinalColor(), new Color(181, 181, 0))
          && t.checkExpect(this.coral.getFinalColor(), new Color(153, 108, 45))
          && t.checkExpect(this.nasty3.getFinalColor(), new Color(90, 59, 30))
          && t.checkExpect(this.nasty4.getFinalColor(), new Color(152, 90, 62));
  }

  boolean testCountPaints(Tester t) {
    return t.checkExpect(this.red.countPaints(), 1)
          && t.checkExpect(this.purple.countPaints(), 2)
          && t.checkExpect(this.darkPurple.countPaints(), 3)
          && t.checkExpect(this.yellow.countPaints(), 3)
          && t.checkExpect(this.pink.countPaints(), 5)
          && t.checkExpect(this.khaki.countPaints(), 2)
          && t.checkExpect(this.coral.countPaints(), 7);
  }

  boolean testCountMixes(Tester t) {
    return t.checkExpect(this.red.countMixes(), 0)
          && t.checkExpect(this.purple.countMixes(), 1)
          && t.checkExpect(this.darkPurple.countMixes(), 2)
          && t.checkExpect(this.pink.countMixes(), 4)
          && t.checkExpect(this.khaki.countMixes(), 1)
          && t.checkExpect(this.coral.countMixes(), 6);
  }

  boolean testFormulaDepth(Tester t) {
    return t.checkExpect(this.red.formulaDepth(), 0)
          && t.checkExpect(this.purple.formulaDepth(), 1)
          && t.checkExpect(this.darkPurple.formulaDepth(), 2)
          && t.checkExpect(this.pink.formulaDepth(), 3)
          && t.checkExpect(this.khaki.formulaDepth(), 1)
          && t.checkExpect(this.coral.formulaDepth(), 4);
  }

  boolean testInvert(Tester t) {
    return t.checkExpect(this.red.invert(), this.red)
          && t.checkExpect(this.darkPurple.invert(),
          new Combo("dark purple", new Brighten(purple)));
  }

  boolean testMixingFormula(Tester t) {
    return t.checkExpect(this.red.mixingFormula(0), "red")
          && t.checkExpect(this.pink.mixingFormula(0), "pink")
          && t.checkExpect(this.pink.mixingFormula(2), "brighten(blend(purple, khaki))")
          && t.checkExpect(this.pink.mixingFormula(3),
          "brighten(blend(blend(red, blue), blend(red, green)))");
  }
}

//An interface for representing regular and operated paints
interface IPaint {
  //returns the rgb value in a Color object after all operations
  Color getFinalColor();

  //returns the count of how many paints are used to make a formula
  int countPaints();

  //returns the count of how many times a paint is mixed
  int countMixes();

  //returns the count of the largest depth to mix the paint
  int formulaDepth();

  //returns a paint that inverts the darken to brighten and brighten to darken operations
  IPaint invert();

  //returns a string of how a paint is mixed up to depth
  String mixingFormula(int depth);
}

//A class that represents a base paint color without any operations
class Solid implements IPaint {
  String name;
  Color color;

  Solid(String name, Color color) {
    this.name = name;
    this.color = color;
  }

  /* fields:
   *  this.name ... String
   *  this.color ... Color
   * methods:
   *  this.getFinalColor() ... Color
   *  this.countPaints() ... int
   *  this.countMixes() ... int
   *  this.formulaDepth() ... int
   *  this.invert() ... IPaint
   *  this.mixingFormula(int) ... String
   * methods for fields: none
   */

  public Color getFinalColor() {
    return this.color;
  }

  public int countPaints() {
    return 1;
  }

  public int countMixes() {
    return 0;
  }

  public int formulaDepth() {
    return 0;
  }

  public IPaint invert() {
    return this;
  }

  public String mixingFormula(int depth) {
    return name;
  }
}

//A class that represents a paint that undergoes an operation
class Combo implements IPaint {
  String name;
  IMixture operation;

  Combo(String name, IMixture operation) {
    this.name = name;
    this.operation = operation;
  }

  /* fields:
   *  this.name ... String
   *  this.operation ... IMixture
   * methods:
   *  this.getFinalColor() ... Color
   *  this.countPaints() ... int
   *  this.countMixes() ... int
   *  this.formulaDepth() ... int
   *  this.invert() ... IPaint
   *  this.mixingFormula(int) ... String
   * methods for fields:
   *  this.operation.getColor() ... Color
   *  this.operation.countPaints() ... int
   *  this.operation.countMixes() ... int
   *  this.operation.formulaDepth() ... int
   *  this.operation.invert() ... IPaint
   *  this.operation.mixingFormula(int) ... String
   */

  public Color getFinalColor() {
    return this.operation.getColor();
  }

  public int countPaints() {
    return this.operation.countPaints();
  }

  public int countMixes() {
    return this.operation.countMixes();
  }

  public int formulaDepth() {
    return this.operation.formulaDepth();
  }

  public IPaint invert() {
    return new Combo(this.name, this.operation.invert());
  }

  public String mixingFormula(int depth) {
    if (depth == 0) {
      return this.name;
    } else {
      return this.operation.mixingFormula(depth);
    }
  }
}

//An interface for objects that undergo operations
interface IMixture {
  //returns the color after the operation
  Color getColor();

  //returns the count of how many paints are used to make a formula
  int countPaints();

  //returns the count of how many times a paint is mixed
  int countMixes();

  //returns the count of the largest depth to mix the paint
  int formulaDepth();

  //returns a paint that inverts the darken to brighten and brighten to darken operations
  IMixture invert();

  //returns a string of how a paint is mixed up to depth
  String mixingFormula(int depth);

}

//A class for an object that darkens a paint
class Darken implements IMixture {
  IPaint paint;

  Darken(IPaint paint1) {
    this.paint = paint1;
  }

  /* fields:
   *  this.paint ... IPaint
   * methods:
   *  this.getColor() ... Color
   *  this.countPaints() ... int
   *  this.countMixes() ... int
   *  this.formulaDepth() ... int
   *  this.invert() ... IPaint
   *  this.mixingFormula(int) ... String
   * methods for fields: none
   */

  public Color getColor() {
    return this.paint.getFinalColor().darker();
  }

  public int countPaints() {
    return 1 + paint.countPaints();
  }

  public int countMixes() {
    return 1 + paint.countMixes();
  }

  public int formulaDepth() {
    return 1 + paint.formulaDepth();
  }

  public IMixture invert() {
    return new Brighten(this.paint.invert());
  }

  public String mixingFormula(int depth) {
    if (depth == 0) {
      return "";
    } else {
      return "darken(" + paint.mixingFormula(depth - 1) + ")";
    }
  }
}

//A class for an object that brightens a paint
class Brighten implements IMixture {
  IPaint paint;

  Brighten(IPaint paint1) {
    this.paint = paint1;
  }

  /* fields:
   *  this.paint ... IPaint
   * methods:
   *  this.getColor() ... Color
   *  this.countPaints() ... int
   *  this.countMixes() ... int
   *  this.formulaDepth() ... int
   *  this.invert() ... IPaint
   *  this.mixingFormula(int) ... String
   * methods for fields: none
   */

  public Color getColor() {
    return this.paint.getFinalColor().brighter();
  }

  public int countPaints() {
    return 1 + paint.countPaints();
  }

  public int countMixes() {
    return 1 + paint.countMixes();
  }

  public int formulaDepth() {
    return 1 + paint.formulaDepth();
  }

  public IMixture invert() {
    return new Darken(this.paint.invert());
  }

  public String mixingFormula(int depth) {
    if (depth == 0) {
      return "";
    } else {
      return "brighten(" + paint.mixingFormula(depth - 1) + ")";
    }
  }
}

//A class for an object that blends two paints together
class Blend implements IMixture {
  IPaint paint1;
  IPaint paint2;

  Blend(IPaint paint1, IPaint paint2) {
    this.paint1 = paint1;
    this.paint2 = paint2;
  }

  /* fields:
   *  this.paint1 ... IPaint
   *  this.paint2 ... IPaint
   * methods:
   *  this.getColor() ... Color
   *  this.countPaints() ... int
   *  this.countMixes() ... int
   *  this.formulaDepth() ... int
   *  this.invert() ... IPaint
   *  this.mixingFormula(int) ... String
   * methods for fields: none
   */

  public Color getColor() {
    int r1 = this.paint1.getFinalColor().getRed();
    int g1 = this.paint1.getFinalColor().getGreen();
    int b1 = this.paint1.getFinalColor().getBlue();
    int r2 = this.paint2.getFinalColor().getRed();
    int g2 = this.paint2.getFinalColor().getGreen();
    int b2 = this.paint2.getFinalColor().getBlue();
    return new Color(r1 / 2 + r2 / 2, g1 / 2 + g2 / 2, b1 / 2 + b2 / 2);
  }

  public int countPaints() {
    return paint1.countPaints() + paint2.countPaints();
  }

  public int countMixes() {
    return 1 + paint1.countMixes() + paint2.countMixes();
  }

  public int formulaDepth() {
    if (paint1.formulaDepth() > paint2.formulaDepth()) {
      return 1 + paint1.formulaDepth();
    } else {
      return 1 + paint2.formulaDepth();
    }
  }

  public IMixture invert() {
    return new Blend(paint1.invert(), paint2.invert());
  }

  public String mixingFormula(int depth) {
    if (depth == 0) {
      return "";
    } else {
      return "blend(" + paint1.mixingFormula(depth - 1) + ", "
            + paint2.mixingFormula(depth - 1) + ")";
    }
  }
}