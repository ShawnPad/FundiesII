import tester.*;

//class to represent a book
class Book {
  String title;
  String author;
  int price;

  public Book(String title, String author, int price) {
    this.title = title;
    this.author = author;
    this.price = price;
  }
}

//interface to compare objects
interface IComparator<T> {
  // Returns a negative number if t1 comes before t2 in this ordering
  // Returns zero              if t1 is the same as t2 in this ordering
  // Returns a positive number if t1 comes after t2 in this ordering
  int compare(T t1, T t2);
}

//compare books by title
class BooksByTitle implements IComparator<Book> {

  // Returns a negative number if b1 title comes before b2 by alpha order
  // Returns zero              if b1 title is the same as b2 by alpha order
  // Returns a positive number if b1 title comes after b2 by alpha order
  public int compare(Book b1, Book b2) {
    return b1.title.compareToIgnoreCase(b2.title);
  }
}

//compare books by author
class BooksByAuthor implements IComparator<Book> {

  // Returns a negative number if b1 author comes before b2 by alpha order
  // Returns zero              if b1 author is the same as b2 by alpha order
  // Returns a positive number if b1 author comes after b2 by alpha order
  public int compare(Book b1, Book b2) {
    return b1.author.compareToIgnoreCase(b2.author);
  }
}

//compare books by price
class BooksByPrice implements IComparator<Book> {

  // Returns a negative number if b1 price is cheaper than b2
  // Returns zero              if b1 price is the same as b2
  // Returns a positive number if b1 price is more expensive that b2
  public int compare(Book b1, Book b2) {
    return b1.price - b2.price;
  }
}

//class to represent a binary tree
abstract class ABST<T> {
  IComparator<T> order;

  public ABST(IComparator<T> order) {
    this.order = order;
  }

  //inserts that object in the correct place in the tree by comparator
  abstract ABST<T> insert(T that);

  //gets the left most object in the tree exception if leaf
  abstract T getLeftmost();

  //helps getLeftmost by accumulating the current left
  abstract T getLeftmostAcc(T acc);

  //returns the tree without the leftmost object exception if leaf
  abstract ABST<T> getRight();

  //helps getRight to find empty case without exception
  abstract ABST<T> getRightHelp();

  //is this the same tree as that by structure and data
  abstract boolean sameTree(ABST<T> that);

  //helps sameTree with the left side of tree to check sameness
  abstract boolean sameTreeHelpL(ABST<T> that);

  //helps sameTree with the right side of tree to check sameness
  abstract boolean sameTreeHelpR(ABST<T> that);

  //compares this data with that data with comparator
  abstract boolean compare(T that);

  //is the tree the same by data using comparator
  abstract boolean sameData(ABST<T> that);

  //helps sameData with testing both lists this and that for sameness
  abstract boolean sameDataHelp(ABST<T> that);

  //checks if this tree has that
  abstract boolean contains(T that);

  //is this tree have the same data as that list
  abstract boolean sameAsList(IList<T> that);

  //builds a list from the given tree and list
  abstract IList<T> buildList(IList<T> acc);
}

//class to represent the end of a branch
class Leaf<T> extends ABST<T> {
  public Leaf(IComparator<T> order) {
    super(order);
  }

  //if reach leaf build a new node with that as data with same leaves
  ABST insert(T that) {
    return new Node<>(that, this, this, order);
  }

  //No leftmost item of an empty tree
  T getLeftmost() {
    throw new RuntimeException("No leftmost item of an empty tree");
  }

  //reached most left return data
  T getLeftmostAcc(T acc) {
    return acc;
  }

  //No right of an empty tree
  ABST<T> getRight() {
    throw new RuntimeException("No right of an empty tree");
  }

  //build with this leaf base case
  ABST<T> getRightHelp() {
    return this;
  }

  //andmap base case
  boolean sameTree(ABST<T> that) {
    return true;
  }

  //andmap base case
  boolean sameTreeHelpL(ABST<T> that) {
    return true;
  }

  //andmap base case
  boolean sameTreeHelpR(ABST<T> that) {
    return true;
  }

  //comparing leaf with object not same
  boolean compare(T that) {
    return false;
  }

  //andmap base case
  boolean sameData(ABST<T> that) {
    return true;
  }

  //andmap base case
  boolean sameDataHelp(ABST<T> that) {
    return true;
  }

  //ormap base case
  boolean contains(T that) {
    return false;
  }

  //andmap base case
  boolean sameAsList(IList<T> that) {
    return true;
  }

  //reached end return accumulator
  IList<T> buildList(IList<T> acc) {
    return acc;
  }
}

//class to represent a node of tree with data
class Node<T> extends ABST<T> {
  T data;
  ABST<T> left;
  ABST<T> right;

  public Node(IComparator<T> order, T data, ABST<T> left, ABST<T> right) {
    super(order);
    this.data = data;
    this.left = left;
    this.right = right;
  }

  public Node(T data, ABST left, ABST right, IComparator order) {
    super(order);
    this.data = data;
    this.left = left;
    this.right = right;
  }

  //inserts object in correct order
  ABST insert(T that) {
    if (order.compare(this.data, that) == 0) {
      return new Node<>(data, left, new Node<>(that,new Leaf<>(order), right, order), order);
    } else if (order.compare(this.data, that) < 1) {
      return new Node<>(data, left, right.insert(that), order);
    } else {
      return new Node<>(data, left.insert(that), right, order);
    }
  }

  //get the left most object in tree
  T getLeftmost() {
    return this.left.getLeftmostAcc(data);
  }

  //help getLeftmost by accumulating the left most data
  T getLeftmostAcc(T acc) {
    return this.left.getLeftmostAcc(data);
  }

  //there is a right for a node so call helper
  ABST<T> getRight() {
    return getRightHelp();
  }

  //remove the leftmost item from tree
  ABST<T> getRightHelp() {
    if (order.compare(data,this.getLeftmost()) == 0) {
      return right;
    } else {
      return new Node<>(data, left.getRightHelp(), right, order);
    }
  }

  //compare both trees if they have the same data and structure
  boolean sameTree(ABST<T> that) {
    return that.compare(data) && that.sameTreeHelpL(left) && that.sameTreeHelpR(right);
  }

  //help check left side
  boolean sameTreeHelpL(ABST<T> that) {
    return that.sameTree(left);
  }

  //help check right side
  boolean sameTreeHelpR(ABST<T> that) {
    return that.sameTree(right);
  }

  //compare data of this with that data
  boolean compare(T that) {
    return order.compare(data,that) == 0;
  }

  //check both this and that if they have the same data as eachother
  boolean sameData(ABST<T> that) {
    return this.sameDataHelp(that) && that.sameDataHelp(this);
  }

  //help check left and right side if they have the same data
  boolean sameDataHelp(ABST<T> that) {
    return that.contains(this.data) && left.sameDataHelp(that) && right.sameDataHelp(that);
  }

  //check entire tree if it contains that data
  boolean contains(T that) {
    return this.order.compare(that,this.data) == 0 || left.contains(that) || right.contains(that);
  }

  //checks if the built list is the same as the given list
  boolean sameAsList(IList<T> that) {
    return that.sameList(this.buildList(new MtList<>()), order);
  }

  //build a list from this tree with the list given
  IList<T> buildList(IList<T> acc) {
    return right.buildList(new MtList<>()).append(new ConsList<>(data, left.buildList(acc)));
  }
}

//interface to represent a list of objects
interface IList<T> {
  //append this list with that list
  IList<T> append(IList<T> acc);

  //check if this list is the same as that list with comparator
  boolean sameList(IList<T> that, IComparator<T> order);

  //help sameList by moving second list
  boolean sameListHelp(IList<T> that, IComparator<T> order);

  //builds a tree in the correct order onto the acc with this list
  ABST<T> buildTree(ABST<T> acc);

  //compares data of list with that object
  boolean compareThat(T that, IComparator<T> order);
}

//class to represent an empty list
class MtList<T> implements IList<T> {
  //append this list with that list
  public IList<T> append(IList<T> acc) {
    return acc;
  }

  //and base case
  public boolean sameList(IList<T> that, IComparator<T> order) {
    return true;
  }

  //and base case helper
  public boolean sameListHelp(IList<T> that, IComparator<T> order) {
    return true;
  }

  //comparing and empty to an object false
  public boolean compareThat(T that, IComparator<T> order) {
    return false;
  }

  //reach end of list return accumulator
  public ABST<T> buildTree(ABST<T> acc) {
    return acc;
  }
}

//class to represent element of list
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  //append this list to the acc list
  public IList<T> append(IList<T> acc) {
    return new ConsList<>(this.first, this.rest.append(acc));
  }

  //compare each element of this list if same as that list
  public boolean sameList(IList<T> that, IComparator<T> order) {
    return that.compareThat(first, order) && that.sameListHelp(this.rest, order);
  }

  //help sameList by moving second list
  public boolean sameListHelp(IList<T> that, IComparator<T> order) {
    return this.rest.sameList(that, order);
  }

  //is the first of this list equal to that object
  public boolean compareThat(T that, IComparator<T> order) {
    return order.compare(this.first, that) == 0;
  }

  //build tree by inserting each element onto the acc
  public ABST<T> buildTree(ABST<T> acc) {
    return this.rest.buildTree(acc.insert(this.first));
  }
}


class Examples {
  Book b1 = new Book("A book 1", "An Author 1", 1);
  Book b2 = new Book("A book 2", "An Author 2", 2);
  Book b3 = new Book("A book 3", "An Author 3", 3);
  Book b4 = new Book("A book 4", "An Author 4", 4);
  Book b5 = new Book("A book 5", "An Author 5", 5);
  IComparator<Book> price = new BooksByPrice();
  IComparator<Book> title = new BooksByTitle();
  IComparator<Book> author = new BooksByAuthor();
  Leaf<Book> leaf = new Leaf<>(price);
  Leaf<Book> leaf2 = new Leaf<>(title);
  ABST<Book> bTB = new Node<>(b2, new Node<>(b1, leaf ,leaf, price),
        new Node<>(b3, leaf, leaf, price), price);
  ABST<Book> bstA = new Node<>(b3,
        new Node<>(b2, leaf, new Node<>(b1,leaf,leaf,price), price),
        new Node<>(b4, leaf ,leaf, price), price);
  ABST<Book> bstB = new Node<>(b3,
        new Node<>(b2, leaf, new Node<>(b1,leaf,leaf,price), price),
        new Node<>(b4, leaf ,leaf, price), price);
  ABST<Book> bstC = new Node<>(b2,
        new Node<>(b1, leaf, leaf, price),
        new Node<>(b4, new Node<>(b3,leaf,leaf,price),
              leaf, price), price);
  ABST<Book> bstD = new Node<>(b3,
        new Node<>(b1, leaf, leaf, price),
        new Node<>(b4, new Node<>(b5,leaf,leaf,price),
              leaf, price), price);
  IList<Book> blist = new ConsList<>(b1, new ConsList<>(b2, new ConsList<>(b3, new MtList<>())));
  IList<Book> blist2 = new ConsList<>(b4, new ConsList<>(b3,
        new ConsList<>(b1, new ConsList<>(b2, new MtList<>()))));
  IList<Book> blistRev = new ConsList<>(b3, new ConsList<>(b2, new ConsList<>(b1, new MtList<>())));
  IList<Book> elist = new ConsList<>(b1, new ConsList<>(b3,
        new ConsList<>(b4, new ConsList<>(b5, new MtList<>()))));
  IList<Book> elistRev = new ConsList<>(b5, new ConsList<>(b4,
        new ConsList<>(b3, new ConsList<>(b1, new MtList<>()))));
  IList<Book> listD = new ConsList<>(b3, new ConsList<>(b1,
        new ConsList<>(b4, new MtList<>())));
  IList<Book> listB = new ConsList<>(b2, new ConsList<>(b1,
        new ConsList<>(b5, new MtList<>())));
  IList<Book> listC = new ConsList<>(b1, new ConsList<>(b3,
        new ConsList<>(b5, new MtList<>())));

  //Test comparators
  //test BooksByPrice
  boolean testBooksByPrice(Tester t) {
    return t.checkExpect(price.compare(b1,b2), -1)
          && t.checkExpect(price.compare(b2,b1), 1)
          && t.checkExpect(price.compare(b3,b3), 0);
  }

  //test BooksByTitle
  boolean testBooksByTitle(Tester t) {
    return t.checkExpect(title.compare(b1,b2), -1)
          && t.checkExpect(title.compare(b2,b1), 1)
          && t.checkExpect(title.compare(b3,b3), 0);
  }

  //test BooksByAuthor
  boolean testBooksByAuthor(Tester t) {
    return t.checkExpect(author.compare(b3,b4), -1)
          && t.checkExpect(author.compare(b4,b3), 1)
          && t.checkExpect(author.compare(b5,b5), 0);
  }

  //Test Binary Tree
  //test insert
  boolean testInsert(Tester t) {
    return t.checkExpect(leaf.insert(b1), new Node<>(b1,leaf,leaf,price))
          && t.checkExpect(bTB.insert(b4),
          new Node<>(b2, new Node<>(b1, leaf ,leaf, price),
                new Node<>(b3, leaf,
                      new Node<>(b4,leaf,leaf,price),
                      price), price));
  }

  //test getLeftmost
  boolean testGetLeftmost(Tester t) {
    return t.checkExpect(bTB.getLeftmost(), b1)
          && t.checkExpect(bstD.getLeftmost(), b1)
          && t.checkException(new RuntimeException("No leftmost item of an empty tree"),
          leaf , "getLeftmost");
  }

  //test getLeft accumulator
  boolean testGetLeftmostAcc(Tester t) {
    return t.checkExpect(bTB.getLeftmostAcc(b2), b1)
          && t.checkExpect(bstD.getLeftmostAcc(b2), b1);
  }

  //test getRight
  boolean testGetRight(Tester t) {
    return t.checkExpect(bTB.getRight(), new Node<>(b2, leaf,
          new Node<>(b3, leaf ,leaf, price), price))
          && t.checkExpect(bstD.getRight(), new Node<>(b3, leaf,
          new Node<>(b4, new Node<>(b5, leaf, leaf, price) , leaf , price), price))
          && t.checkException(new RuntimeException("No right of an empty tree"),
          leaf , "getRight");
  }

  //test getRight helper
  boolean testGetRightHelp(Tester t) {
    return t.checkExpect(bTB.getRight(), new Node<>(b2, leaf,
          new Node<>(b3, leaf ,leaf, price), price))
          && t.checkExpect(bstD.getRight(), new Node<>(b3, leaf,
          new Node<>(b4, new Node<>(b5, leaf, leaf, price) , leaf , price), price));
  }

  //test sameTree
  boolean testSameTree(Tester t) {
    return t.checkExpect(bTB.sameTree(bTB), true)
          && t.checkExpect(bTB.sameTree(leaf), false)
          && t.checkExpect(bstA.sameTree(bstB), true)
          && t.checkExpect(bstA.sameTree(bstD), false)
          && t.checkExpect(leaf.sameTree(leaf), true);
  }

  //test sameTree helper for left side
  boolean testSameTreeHelpL(Tester t) {
    return t.checkExpect(new Node<Book>(b1, bTB, leaf, price).sameTreeHelpL(bTB), true)
          && t.checkExpect(leaf.sameTreeHelpL(leaf), true);
  }

  //test sameTree helper for right side
  boolean testSameTreeHelpR(Tester t) {
    return t.checkExpect(new Node<Book>(b1, leaf, bTB, price).sameTreeHelpR(bTB), true)
          && t.checkExpect(leaf.sameTreeHelpR(leaf), true);
  }

  //test sameData
  boolean testSameData(Tester t) {
    return t.checkExpect(bstA.sameData(bstC),true)
          && t.checkExpect(bstA.sameData(bstB),true)
          && t.checkExpect(bstA.sameData(bstD),false)
          && t.checkExpect(leaf.sameData(leaf),true);
  }

  //test sameData helper
  boolean testSameDataHelp(Tester t) {
    return t.checkExpect(bstA.sameDataHelp(bstC),true)
          && t.checkExpect(bstA.sameDataHelp(bstB),true)
          && t.checkExpect(bstA.sameDataHelp(bstD),false)
          && t.checkExpect(leaf.sameDataHelp(leaf),true);
  }

  //test compare
  boolean testCompare(Tester t) {
    return t.checkExpect(bstA.compare(b1), false)
          && t.checkExpect(bstA.compare(b3), true)
          && t.checkExpect(bstC.compare(b2), true);
  }

  //test buildList
  boolean testBuildList(Tester t) {
    return t.checkExpect(bstD.buildList(new MtList<>()),
          new ConsList<>(b4, new ConsList<>(b5,
                new ConsList<>(b3, new ConsList<>(b1, new MtList<>())))))
          && t.checkExpect(bstA.buildList(new MtList<>()),
          new ConsList<>(b4, new ConsList<>(b3,
                new ConsList<>(b1, new ConsList<>(b2, new MtList<>())))));
  }

  //test sameAsList
  boolean testSameAsList(Tester t) {
    return t.checkExpect(bstD.sameAsList(new ConsList<>(b5, new ConsList<>(b4,
          new ConsList<>(b1, new ConsList<>(b3, new MtList<>()))))), false)
          && t.checkExpect(bstB.sameAsList(blist2), true);
  }

  //test contains
  boolean contains(Tester t) {
    return t.checkExpect(bstD.contains(b1), true)
          && t.checkExpect(bstD.contains(b5), false)
          && t.checkExpect(leaf.contains(b5), false);
  }

  //Test Lists
  //test builds with given
  boolean testBuilds(Tester t) {
    return t.checkExpect(blist.buildTree(leaf).buildList(new MtList<>()), blistRev)
          && t.checkExpect(elist.buildTree(leaf2).buildList(new MtList<>()), elistRev);
  }

  //test buildTree
  boolean testBuildTree(Tester t) {
    return t.checkExpect(listD.buildTree(leaf), new Node<Book>(b3,
          new Node<Book>(b1, leaf, leaf, price), new Node<Book>(b4, leaf, leaf, price), price))
          && t.checkExpect(listB.buildTree(leaf), new Node<Book>(b2,
          new Node<Book>(b1, leaf, leaf, price), new Node<Book>(b5, leaf, leaf, price), price))
          && t.checkExpect(listB.buildTree(leaf2), new Node<Book>(b2,
          new Node<Book>(b1, leaf2, leaf2, title), new Node<Book>(b5, leaf2, leaf2, title), title));
  }

  //test append
  boolean testAppend(Tester t) {
    return t.checkExpect(new ConsList<>(b1,
                new MtList<>()).append(new ConsList<>(b2,new MtList<>())),
          new ConsList<>(b1,new ConsList<>(b2, new MtList<>())))
          && t.checkExpect(new ConsList<>(b5,
                new MtList<>()).append(new ConsList<>(b4,new MtList<>())),
          new ConsList<>(b5,new ConsList<>(b4, new MtList<>())))
          && t.checkExpect(new ConsList<>(b1,new MtList<>()).append(new MtList<>()),
          new ConsList<>(b1, new MtList<>()));
  }

  //test sameList
  boolean testSameList(Tester t) {
    return t.checkExpect(listB.sameList(listB, price), true)
          && t.checkExpect(listC.sameList(listD, price), false);
  }

  //test sameList helper
  boolean testSameListHelp(Tester t) {
    return t.checkExpect(new ConsList<>(b1, listB).sameListHelp(listB, price), true)
          && t.checkExpect(new ConsList<>(b1, listC).sameList(listD, price), false);
  }

  //test compareThat
  boolean testCompareThat(Tester t) {
    return t.checkExpect(blist.compareThat(b1,price),true)
          && t.checkExpect(blist.compareThat(b2,price),false)
          && t.checkExpect(new MtList<Book>().compareThat(b2,price),false);
  }
}
