import tester.Tester;

//Class to represent a bagel recipe
class BagelRecipe {
  double flour;
  double water;
  double yeast;
  double salt;
  double malt;

  /* fields:
   *  this.flour ... double
   *  this.water ... double
   *  this.yeast ... double
   *  this.salt ... double
   *  this.malt ... double
   * methods:
   *  this.sameRecipe(BagelRecipe other) ... boolean
   * methods for fields: none
   */

  public BagelRecipe(double flour, double water, double yeast, double salt, double malt) {
    if (flour < 0 || water < 0 || yeast < 0 || salt < 0 || malt < 0) {
      throw new IllegalArgumentException("Negative ingredient");
    }
    if (flour == water) {
      this.flour = flour;
      this.water = water;
    }
    else {
      throw new IllegalArgumentException("Invalid flour: " + flour
            + " compared to water: " + water);
    }
    if (yeast == malt) {
      this.yeast = yeast;
      this.malt = malt;
    }
    else {
      throw new IllegalArgumentException("Invalid yeast: " + yeast
            + " compared to malt: " + malt);
    }
    if (salt + yeast == flour / 20) {
      this.salt = salt;
    }
    else {
      throw new IllegalArgumentException("Invalid salt: " + salt);
    }
  }

  public BagelRecipe(double flour, double yeast) {
    this(flour, flour, yeast, flour / 20 - yeast, yeast);
  }

  public BagelRecipe(double flour, double yeast, double salt) {
    this(flour * 4.25,flour * 4.25, yeast * 5 / 48,
          salt * 10 / 48, yeast * 5 / 48);
  }

  //checks if a recipe has the same ingredients as other to 0.001 tolerance
  boolean sameRecipe(BagelRecipe other) {
    return Math.abs(this.flour - other.flour) < 0.001
          && Math.abs(this.yeast - other.yeast) < 0.001
          && Math.abs(this.salt - other.salt) < 0.001;
  }
}

class ExamplesBagels {
  BagelRecipe bagel1 = new BagelRecipe(500,500, 5, 20, 5);
  BagelRecipe bagel2 = new BagelRecipe(20, 20, 1, 0, 1);
  BagelRecipe bagel3 = new BagelRecipe(100, 1);
  BagelRecipe bagel4 = new BagelRecipe(500, 5);
  BagelRecipe bagel5 = new BagelRecipe(25, 17, 17);

  //Test if same recipe works with examples
  boolean testSameRecipe(Tester t) {
    return t.checkExpect(bagel1.sameRecipe(bagel4), true)
          && t.checkExpect(bagel2.sameRecipe(bagel5), false)
          && t.checkExpect(bagel3.sameRecipe(bagel3), true);
  }

  //check if the class throws exceptions for invalid recipes
  boolean testException(Tester t) {
    return t.checkConstructorException(
          new IllegalArgumentException("Invalid yeast: 4.0 compared to malt: 3.0"),
          "BagelRecipe", new BagelRecipe(100.0,100.0,4.0, 1.0, 3.0))
          && t.checkConstructorException(new IllegalArgumentException("Invalid salt: 0.5"),
          "BagelRecipe", new BagelRecipe(10,1))
          && t.checkConstructorException(
          new IllegalArgumentException("Invalid flour: 100.0 compared to water: 99.0"),
          "BagelRecipe", new BagelRecipe(100.0,99.0,4.0, 1.0, 4.0))
          && t.checkConstructorException(
          new IllegalArgumentException("Negative ingredient"),
          "BagelRecipe", new BagelRecipe(500, -5));
  }
}
