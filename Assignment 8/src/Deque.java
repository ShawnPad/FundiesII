import tester.*;

// Represents a boolean-valued question over values of type T
interface IPred<T> {
  //apply a question to given data
  boolean apply(T t);
}

// Predicate to check sameness of any data type
class FindObject<T> implements IPred<T> {
  T ob;

  FindObject(T ob) {
    this.ob = ob;
  }

  //checks if target data is equal to pred data
  public boolean apply(T t) {
    return t.equals(ob);
  }
}

// abstract class to represent node and sentinels having a next and previous value
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }

  //count number of nodes
  abstract int count(int acc);

  //remove a node given previous node
  abstract T remove(ANode<T> p);

  //find first node that passes true for the pred
  abstract ANode<T> find(IPred<T> pred);

  //remove node from list
  abstract void removeNodeHelp(ANode<T> n);

}

// class to represent a node with data
class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    super(null,null);
    this.data = data;
  }

  Node(T data, ANode<T> next, ANode<T> prev) {
    super(next, prev);
    if (next == null || prev == null) {
      throw new IllegalArgumentException();
    }
    this.data = data;
    next.prev = this;
    prev.next = this;
  }

  Node(ANode<T> next, ANode<T> prev, T data) {
    super(next, prev);
    if (next == null || prev == null) {
      throw new IllegalArgumentException();
    }
    this.data = data;
    next.prev = this;
    prev.next = this;
  }

  //adds to the accumulator for every node visited
  int count(int acc) {
    return next.count(acc + 1);
  }

  //removes this node by connecting the previous to the next
  T remove(ANode<T> p) {
    p.next = next;
    next.prev = p;
    return data;
  }

  //checks if this node passes the pred
  ANode<T> find(IPred<T> pred) {
    if (pred.apply(data)) {
      return this;
    } else {
      return next.find(pred);
    }
  }

  //removes this node if it matches the target node
  void removeNodeHelp(ANode<T> n) {
    if (n.equals(this)) {
      this.remove(this.prev);
    } else {
      next.removeNodeHelp(n);
    }
  }
}

// class to represent the head and tail of the list
class Sentinel<T> extends ANode<T> {

  Sentinel() {
    super(null,null);
    this.next = this;
    this.prev = this;
  }

  //starts the accumulator to count nodes
  int count() {
    return next.count(0);
  }

  //reached end return accumulator
  int count(int acc) {
    return acc;
  }

  //add node to next of sentinel
  void addAtHead(T insert) {
    new Node<T>(next, this, insert);
  }

  //add node to prev of sentinel
  void addAtTail(T insert) {
    new Node<T>(this, prev, insert);
  }

  //remove node next to sentinel
  T removeFromHead() {
    return next.remove(this);
  }

  //remove node prev to sentinel
  T removeFromTail() {
    return prev.remove(prev.prev);
  }

  //can't remove sentinel throw exception
  T remove(ANode<T> p) {
    throw new RuntimeException();
  }

  //if node matching pred not found return sentinel
  ANode<T> find(IPred<T> pred) {
    return this;
  }

  //find node matching pred
  ANode<T> findHelp(IPred<T> pred) {
    return next.find(pred);
  }

  //remove node that matches target node
  void removeNode(ANode<T> n) {
    next.removeNodeHelp(n);
  }

  //if node not found don't change list
  void removeNodeHelp(ANode<T> n) {
    //not found don't do anything
  }
}

//class to represent a circular list
class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  //count how many node in this list
  int size() {
    return header.count();
  }

  //add node to head of list
  void addAtHead(T insert) {
    header.addAtHead(insert);
  }

  //add node to tail of list
  void addAtTail(T insert) {
    header.addAtTail(insert);
  }

  //remove node from head of list
  T removeFromHead() {
    return header.removeFromHead();
  }

  //remove node from tail of list
  T removeFromTail() {
    return header.removeFromTail();
  }

  //find first node matching the pred
  ANode<T> find(IPred<T> pred) {
    return header.findHelp(pred);
  }

  //remove matching node from list
  void removeNode(ANode<T> n) {
    header.removeNode(n);
  }
}

class Examples {

  //test size and helpers
  void testSize(Tester t) {
    Sentinel<String> sentinel = new Sentinel<String>();
    ANode<String> abc = new Node<String>(sentinel,sentinel,"abc");
    ANode<String> bcd = new Node<String>(sentinel,abc,"bcd");
    ANode<String> cde = new Node<String>(sentinel,bcd,"cde");
    ANode<String> def = new Node<String>(sentinel,cde,"def");
    Deque<String> e1 = new Deque<String>(sentinel);
    t.checkExpect(e1.size(), 4);

    //no nodes return 0
    Sentinel<String> emptySent = new Sentinel<String>();
    Deque<String> empty = new Deque<String>(emptySent);
    t.checkExpect(empty.size(), 0);

    //test helpers
    t.checkExpect(sentinel.count(),4);
    t.checkExpect(sentinel.next.count(0),4);
    t.checkExpect(emptySent.count(),0);
    t.checkExpect(emptySent.next.count(0),0);
  }

  //test addAtHead and helper
  void testAddAtHead(Tester t) {
    Sentinel<String> sentinel = new Sentinel<String>();
    ANode<String> abc = new Node<String>(sentinel,sentinel,"abc");
    ANode<String> bcd = new Node<String>(sentinel,abc,"bcd");
    ANode<String> cde = new Node<String>(sentinel,bcd,"cde");
    ANode<String> def = new Node<String>(sentinel,cde,"def");
    Deque<String> e1 = new Deque<String>(sentinel);
    t.checkExpect(sentinel.next, abc);
    e1.addAtHead("Wow");
    t.checkExpect(sentinel.next, new Node<String>(abc,sentinel,"Wow"));

    //add wow to list
    Sentinel<String> emptySent = new Sentinel<String>();
    Deque<String> empty = new Deque<String>(emptySent);
    empty.addAtHead("Wow");
    t.checkExpect(emptySent.next, new Node<String>(emptySent,emptySent,"Wow"));

    //test helper
    t.checkExpect(sentinel.next, new Node<String>(abc,sentinel,"Wow"));
    sentinel.addAtHead("Wow2");
    t.checkExpect(sentinel.next,
          new Node<String>(
                new Node<String>(abc,sentinel,"Wow"),sentinel,"Wow2"));
  }

  //test addAtTail and helper
  void testAddAtTail(Tester t) {
    Sentinel<String> sentinel = new Sentinel<String>();
    ANode<String> abc = new Node<String>(sentinel,sentinel,"abc");
    ANode<String> bcd = new Node<String>(sentinel,abc,"bcd");
    ANode<String> cde = new Node<String>(sentinel,bcd,"cde");
    ANode<String> def = new Node<String>(sentinel,cde,"def");
    Deque<String> e1 = new Deque<String>(sentinel);
    t.checkExpect(sentinel.prev, def);
    e1.addAtTail("Wow");
    t.checkExpect(sentinel.prev, new Node<String>(sentinel,def,"Wow"));

    //add wow to list
    Sentinel<String> emptySent = new Sentinel<String>();
    Deque<String> empty = new Deque<String>(emptySent);
    empty.addAtTail("Wow");
    t.checkExpect(emptySent.prev, new Node<String>(emptySent,emptySent,"Wow"));

    //test helper
    t.checkExpect(sentinel.prev, new Node<String>(sentinel,def,"Wow"));
    sentinel.addAtTail("Wow2");
    t.checkExpect(sentinel.prev,
          new Node<String>(
                sentinel, new Node<String>(sentinel,def,"Wow"),"Wow2"));
  }

  //test removeFromHead and helper
  void testRemoveFromHead(Tester t) {
    Sentinel<String> sentinel = new Sentinel<String>();
    ANode<String> abc = new Node<String>(sentinel,sentinel,"abc");
    ANode<String> bcd = new Node<String>(sentinel,abc,"bcd");
    ANode<String> cde = new Node<String>(sentinel,bcd,"cde");
    ANode<String> def = new Node<String>(sentinel,cde,"def");
    Deque<String> e1 = new Deque<String>(sentinel);
    t.checkExpect(sentinel.next, abc);
    e1.removeFromHead();
    t.checkExpect(sentinel.next, bcd);

    //runtime exception if remove from empty
    Sentinel<String> emptySent = new Sentinel<String>();
    Deque<String> empty = new Deque<String>(emptySent);
    t.checkException(new RuntimeException(), empty, "removeFromHead");

    //test helper
    sentinel.removeFromHead();
    t.checkExpect(sentinel.next, cde);
    t.checkException(new RuntimeException(), emptySent, "removeFromHead");
  }

  //test removeFromTail
  void testRemoveFromTail(Tester t) {
    Sentinel<String> sentinel = new Sentinel<String>();
    ANode<String> abc = new Node<String>(sentinel,sentinel,"abc");
    ANode<String> bcd = new Node<String>(sentinel,abc,"bcd");
    ANode<String> cde = new Node<String>(sentinel,bcd,"cde");
    ANode<String> def = new Node<String>(sentinel,cde,"def");
    Deque<String> e1 = new Deque<String>(sentinel);
    t.checkExpect(sentinel.prev, def);
    e1.removeFromTail();
    t.checkExpect(sentinel.prev, cde);

    //runtime exception if remove from empty
    Sentinel<String> emptySent = new Sentinel<String>();
    Deque<String> empty = new Deque<String>(emptySent);
    t.checkException(new RuntimeException(), empty, "removeFromTail");
  }

  //test remove
  void testRemove(Tester t) {
    Sentinel<String> sentinel = new Sentinel<String>();
    ANode<String> abc = new Node<String>(sentinel,sentinel,"abc");
    ANode<String> bcd = new Node<String>(sentinel,abc,"bcd");
    ANode<String> cde = new Node<String>(sentinel,bcd,"cde");
    ANode<String> def = new Node<String>(sentinel,cde,"def");
    Deque<String> e1 = new Deque<String>(sentinel);
    t.checkExpect(abc.next, bcd);
    bcd.remove(bcd.prev);
    t.checkExpect(abc.next, cde);

    //runtime exception if try to remove sentinel
    Sentinel<String> emptySent = new Sentinel<String>();
    Deque<String> empty = new Deque<String>(emptySent);
    t.checkException(new RuntimeException(), emptySent, "remove", emptySent);
  }

  //test find and helpers
  void testFind(Tester t) {
    Sentinel<String> sentinel = new Sentinel<String>();
    Node<String> abc = new Node<String>(sentinel,sentinel,"abc");
    ANode<String> bcd = new Node<String>(sentinel,abc,"bcd");
    ANode<String> cde = new Node<String>(sentinel,bcd,"cde");
    ANode<String> def = new Node<String>(sentinel,cde,"def");
    Deque<String> e1 = new Deque<String>(sentinel);
    t.checkExpect(e1.find(new FindObject<String>("abc")), abc);
    t.checkExpect(e1.find(new FindObject<String>("def")), def);

    //return sentinel if not found
    Sentinel<String> emptySent = new Sentinel<String>();
    Deque<String> empty = new Deque<String>(emptySent);
    t.checkExpect(empty.find(new FindObject<String>("def")), emptySent);

    //test helpers
    t.checkExpect(sentinel.find(new FindObject<String>("def")),sentinel);
    t.checkExpect(sentinel.findHelp(new FindObject<String>("def")),def);
    t.checkExpect(emptySent.find(new FindObject<String>("def")),emptySent);
    t.checkExpect(emptySent.findHelp(new FindObject<String>("def")),emptySent);
  }

  //test removeNode and helper
  void testRemoveNode(Tester t) {
    Sentinel<String> sentinel = new Sentinel<String>();
    ANode<String> abc = new Node<String>(sentinel,sentinel,"abc");
    ANode<String> bcd = new Node<String>(sentinel,abc,"bcd");
    ANode<String> cde = new Node<String>(sentinel,bcd,"cde");
    ANode<String> def = new Node<String>(sentinel,cde,"def");
    Deque<String> e1 = new Deque<String>(sentinel);
    t.checkExpect(sentinel.next.next, bcd);
    e1.removeNode(bcd);
    t.checkExpect(sentinel.next.next, cde);

    //nothing happens empty case
    Sentinel<String> emptySent = new Sentinel<String>();
    Deque<String> empty = new Deque<String>(emptySent);
    Sentinel<String> emptySent2 = new Sentinel<String>();
    Deque<String> empty2 = new Deque<String>(emptySent2);
    empty.removeNode(abc);
    t.checkExpect(empty, empty2);

    //test helper
    t.checkExpect(sentinel.next, abc);
    abc.removeNodeHelp(abc);
    t.checkExpect(sentinel.next, cde);
  }

}