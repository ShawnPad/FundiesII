import tester.Tester;

class Examples {
  IArith tree1 = new Formula(new Multiply(), "mult", new Const(1), new Const(3));
  IArith tree2 = new Formula(new Divide(), "div", new Const(1), new Const(0));
  IArith tree3 = new Formula(new Divide(), "div", new Const(1),
        new Formula(new Multiply(), "div", new Const(0), new Const(10)));

  boolean testEval(Tester t) {
    return t.checkExpect(tree1.accept(new EvalVisitor()), 3.0);
  }

  boolean testPrint(Tester t) {
    return t.checkExpect(tree1.accept(new PrintVisitor()), "(mult 1.0 3.0)");
  }

  boolean testDouble(Tester t) {
    return t.checkExpect(tree1.accept(new DoublerVisitor()),
          new Formula(new Multiply(), "mult", new Const(2), new Const(6)));
  }

  boolean testAllSmall(Tester t) {
    return t.checkExpect(tree1.accept(new AllSmallVisitor()), true);
  }

  boolean testNoDivBy0(Tester t) {
    return t.checkExpect(tree1.accept(new NoDivBy0()), true)
          && t.checkExpect(tree2.accept(new NoDivBy0()), false)
          && t.checkExpect(tree3.accept(new NoDivBy0()), false);
  }
}

interface IFunc2<X, Y, Z> {
  X apply(Y a, Z b);
}

class Multiply implements IFunc2<Double,Double,Double> {
  public Double apply(Double a, Double b) {
    return a * b;
  }
}

class Divide implements IFunc2<Double,Double,Double> {
  public Double apply(Double a, Double b) {
    return a / b;
  }
}

interface IArithVisitor<R> {

  //applies an operation to a formula
  R visit(Formula f);

  //applies an operation to an const
  R visit(Const c);

  R apply(IArith a);

}

interface IArith {
  <R> R accept(IArithVisitor<R> visitor);
}

class EvalVisitor implements IArithVisitor<Double> {

  public Double visit(Formula f) {
    return f.fun.apply(f.left.accept(this), f.right.accept(this));
  }

  public Double visit(Const c) {
    return c.num;
  }

  public Double apply(IArith a) {
    return a.accept(this);
  }
}

class PrintVisitor implements IArithVisitor<String> {

  public String visit(Formula f) {
    return "(" + f.name + " " + f.left.accept(this) + " " + f.right.accept(this) + ")";
  }


  public String visit(Const c) {
    return Double.toString(c.num);
  }

  public String apply(IArith a) {
    return a.accept(this);
  }
}

class DoublerVisitor implements IArithVisitor<IArith> {

  public IArith visit(Formula f) {
    return new Formula(f.fun,f.name,f.left.accept(this),f.right.accept(this));
  }

  public IArith visit(Const c) {
    return new Const(c.num * 2);
  }

  public IArith apply(IArith a) {
    return a.accept(this);
  }
}

class AllSmallVisitor implements IArithVisitor<Boolean> {

  public Boolean visit(Formula f) {
    return f.left.accept(this) && f.right.accept(this);
  }

  public Boolean visit(Const c) {
    return c.num < 10;
  }

  public Boolean apply(IArith a) {
    return a.accept(this);
  }
}

class NoDivBy0 implements IArithVisitor<Boolean> {

  public Boolean visit(Formula f) {
    if (f.name.equals("div")) {
      return Math.abs(f.right.accept(new EvalVisitor())) > 0.0001 &&
            f.right.accept(this) && f.left.accept(this);
    } else {
      return f.left.accept(this) && f.right.accept(this);
    }
  }

  public Boolean visit(Const c) {
    return true;
  }

  public Boolean apply(IArith a) {
    return a.accept(this);
  }
}

class Formula implements IArith {
  IFunc2<Double, Double, Double> fun;
  String name;
  IArith left;
  IArith right;

  public Formula(IFunc2<Double, Double, Double> fun, String name, IArith left, IArith right) {
    this.fun = fun;
    this.name = name;
    this.left = left;
    this.right = right;
  }

  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visit(this);
  }
}

class Const implements IArith {
  double num;

  public Const(double num) {
    this.num = num;
  }

  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visit(this);
  }
}