class ExamplesP {

}

abstract class Person {
  String name;
  IList<Course> courses;

  public Person(String name) {
    this.name = name;
  }
}

class Instructor extends Person {

  public Instructor(String name) {
    super(name);
  }
}

class Student extends Person {
  int id;

  public Student(String name, int id) {
    super(name);
    this.id = id;
  }

  void enroll(Course c) {
    c.addStudent(this);
  }

  boolean classmates(Student c) {
    return c.classmates(courses);
  }

  boolean classmates(IList c) {
    return c.accept(new ContainsVisitor<>(courses,new CompareClass());
  }

}

class Course {
  String name;
  Instructor prof;
  IList<Student> students;

  public Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.students = new MtList<>();
  }

  void addStudent(Student s) {
    students = new ConsList<>(s,students);
  }

  boolean compare(Course c) {
    return this.name.equals(c.name) && this.prof.equals(c.prof);
  }
}

interface IPred<T> {
  boolean apply(T t);
}

interface IFunc<X, Y> {
  //applies an operation to x
  Y apply(X x);
}

interface IListVisitor<T, R> {
  //applies an operation to a ConsList
  R visit(ConsList<T> cons);
  //applies an operation to an MtList
  R visit(MtList<T> mt);
}


// design a containsVisitor
class ContainsVisitor<T> implements IListVisitor<T, Boolean> {
  T c;
  T s;
  IPred pred;

  ContainsVisitor(T t, T s, IPred pred) {
    this.c = t;
    this.s = s;
    this.pred = pred;
  }

  public Boolean visit(ConsList<T> cons) {
    return pred.apply(cons.first) && cons.rest.accept(this);
  }

  public Boolean visit(MtList<T> mt) {
    return false;
  }
}

// design a orMapVisitor
class OrMapVisitor<T> implements IListVisitor<T, Boolean> {
  IFunc<T, Boolean> fun;

  OrMapVisitor(IFunc<T, Boolean> fun) {
    this.fun = fun;
  }

  //checks the items in the list to determine if they pass the test
  public Boolean visit(ConsList<T> cons) {
    return fun.apply(cons.first) || cons.rest.accept(this);
  }

  //no more items to check, returns the mt list
  public Boolean visit(MtList<T> mt) {
    return false;
  }
}

// design a FilterVisitor
class FilterVisitor<T> implements IListVisitor<T, IList<T>> {
  IFunc<T, Boolean> fun;

  FilterVisitor(IFunc<T, Boolean> fun) {
    this.fun = fun;
  }
  //checks the items in the list to determine if they pass the test
  public IList<T> visit(ConsList<T> cons) {
    if (fun.apply(cons.first)) {
      return new ConsList<T>(cons.first, cons.rest.accept(this));
    }
    else {
      return cons.rest.accept(this);
    }
  }

  //no more items to check, returns the mt list
  public IList<T> visit(MtList<T> mt) {
    return mt;
  }
}

class CompareClass implements IPred<Course> {
  Course c;

  public CompareClass(Course c) {
    this.c = c;
  }

  public boolean apply(Course course) {
    return this.c.compare(course);
  }
}

interface IList<T> {
  //accepts a visitor for this IList
  <R> R accept(IListVisitor<T, R> ilv);
}

class MtList<T> implements IList<T> {

  //accepts a visitor for this empty list
  public <R> R accept(IListVisitor<T, R> ilv) {
    return ilv.visit(this);
  }

}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  //accepts a visitor for this cons list
  public <R> R accept(IListVisitor<T, R> ilv) {
    return ilv.visit(this);
  }

}