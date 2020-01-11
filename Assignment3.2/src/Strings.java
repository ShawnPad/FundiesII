import tester.Tester;

class ExamplesStrings {
  ILoString e1 = new ConsLoString("Lol",
        new ConsLoString("You", new ConsLoString("Cool", new MtLoString())));
  ILoString e2 = new ConsLoString("Race",
        new ConsLoString("car", new ConsLoString("Race", new MtLoString())));
  ILoString e3 = new ConsLoString("a",
        new ConsLoString("b", new ConsLoString("c", new MtLoString())));
  ILoString e4 = new ConsLoString("ab", new ConsLoString("abab", new MtLoString()));
  ILoString e5 = new ConsLoString("All", new ConsLoString("any",
        new ConsLoString("before", new ConsLoString("Broken", new MtLoString()))));
  ILoString e6 = new ConsLoString("aa", new ConsLoString("aa", new MtLoString()));
  ILoString e7 = new ConsLoString("z",
        new ConsLoString("y", new ConsLoString("x", new MtLoString())));
  ILoString e8 = new ConsLoString("a", new ConsLoString("a",
        new ConsLoString("b", new ConsLoString("b", new MtLoString()))));
  ILoString e9 = new ConsLoString("a", new ConsLoString("a",
        new ConsLoString("a", new MtLoString())));
  ILoString mT = new MtLoString();

  //test interleave
  boolean testInterleave(Tester t) {
    return t.checkExpect(e1.interleave(e2),new ConsLoString("Lol", new ConsLoString("Race",
          new ConsLoString("You", new ConsLoString("car",
                new ConsLoString("Cool", new ConsLoString("Race", new MtLoString())))))));
  }

  //test reverse
  boolean testReverse(Tester t) {
    return t.checkExpect(e3.reverse(), new ConsLoString("c",
          new ConsLoString("b", new ConsLoString("a", new MtLoString()))))
          &&  t.checkExpect(mT.reverse(), mT);
  }

  //test isSorted
  boolean testIsSorted(Tester t) {
    return t.checkExpect(e3.isSorted(), true)
          && t.checkExpect(e2.isSorted(), false)
          && t.checkExpect(e4.isSorted(), true)
          && t.checkExpect(e5.isSorted(), true)
          &&  t.checkExpect(mT.isSorted(), true);
  }

  //test sort
  boolean testSort(Tester t) {
    return t.checkExpect(e3.sort(), e3)
          && t.checkExpect(e2.sort(), new ConsLoString("car",
          new ConsLoString("race", new ConsLoString("race", new MtLoString()))))
          && t.checkExpect(e7.sort(), new ConsLoString("x",
          new ConsLoString("y", new ConsLoString("z", new MtLoString()))))
          &&  t.checkExpect(mT.sort(), mT);
  }

  //test isPalindromeList
  boolean testIsPalindromeList(Tester t) {
    return t.checkExpect(e2.isPalindromeList(),true)
          &&  t.checkExpect(mT.isPalindromeList(), true);
  }

  //test isDoubledList
  boolean testIsDoubledList(Tester t) {
    return t.checkExpect(e2.isDoubledList(),false)
          && t.checkExpect(e6.isDoubledList(),true)
          && t.checkExpect(e8.isDoubledList(),true)
          && t.checkExpect(e9.isDoubledList(),false)
          &&  t.checkExpect(mT.isDoubledList(), true);
  }
}

//Interface to represent list of strings
interface ILoString {
  //Sorts list of string
  ILoString sort();

  //inserts string into list helper for merge and sort
  ILoString insert(String str);

  //is the list sorted alphabetically?
  boolean isSorted();

  //helper for isSorted
  boolean isSortedacc(String prev);

  //interleaves two string list
  ILoString interleave(ILoString that);

  //merges two sorted list
  ILoString merge(ILoString that);

  //reverses string list
  ILoString reverse();

  //reverse helper
  ILoString reverseAcc(ILoString acc);

  //does the list have string pairs?
  boolean isDoubledList();

  //isDoubledList helper
  boolean isDoubledList(String prev);

  //checks if the list is a palindrome
  boolean isPalindromeList();
}

//class to hold first string and rest of list
class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  public ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  /* fields:
   *  this.first ... String
   *  this.rest ... ILoString
   * methods:
   *  this.sort() ... ILoString
   *  this.insert(String) ... ILoString
   *  this.isSorted() ... boolean
   *  this.interleave(ILoString) ... ILoString
   *  this.merge(ILoString) ... ILoString
   *  this.reverse() ... ILoString
   *  this.isDoubledList() ... boolean
   *  this.isPalindromeList() ... boolean
   */

  @Override
  //sorts list of string
  public ILoString sort() {
    return this.rest.sort().insert(this.first);
  }

  @Override
  //helps sort list of string by comparing and inserting
  public ILoString insert(String str) {
    if (this.first.compareToIgnoreCase(str) < 0) {
      return new ConsLoString(this.first.toLowerCase(), this.rest.insert(str));
    }
    else {
      return new ConsLoString(str.toLowerCase(), this);
    }
  }

  @Override
  //checks if sorted
  public boolean isSorted() {
    return rest.isSortedacc(first);
  }

  @Override
  //helper for isSorted by checking the prev and comparing to the current
  public boolean isSortedacc(String prev) {
    return first.compareToIgnoreCase(prev) >= 0 && rest.isSortedacc(first);
  }

  @Override
  //interleaves two string lists together
  public ILoString interleave(ILoString that) {
    return new ConsLoString(first, that.interleave(this.rest));
  }

  @Override
  //merges sorted lists together
  public ILoString merge(ILoString that) {
    return rest.merge(that.insert(first));
  }

  @Override
  //reverses a list
  public ILoString reverse() {
    return reverseAcc(new MtLoString());
  }

  @Override
  //helps reverse
  public ILoString reverseAcc(ILoString acc) {
    return rest.reverseAcc(new ConsLoString(first,acc));
  }

  @Override
  //checks if the list if made of pairs
  public boolean isDoubledList() {
    return rest.isDoubledList(first);
  }

  @Override
  //helps isDoubledList by checking prev with current
  public boolean isDoubledList(String prev) {
    if (prev.equals(" ")) {
      return rest.isDoubledList(first);
    }
    if (prev.equals(first)) {
      return rest.isDoubledList(" ");
    } else {
      return false;
    }
  }

  @Override
  //checks if list reads same forward and back
  public boolean isPalindromeList() {
    return this.reverse().interleave(this).isDoubledList();
  }
}

//class to represent an empty list
class MtLoString implements ILoString {

  /* fields:
   *  this.first ... String
   *  this.rest ... ILoString
   * methods:
   *  this.sort() ... ILoString
   *  this.insert(String) ... ILoString
   *  this.isSorted() ... boolean
   *  this.interleave(ILoString) ... ILoString
   *  this.merge(ILoString) ... ILoString
   *  this.reverse() ... ILoString
   *  this.isDoubledList() ... boolean
   *  this.isPalindromeList() ... boolean
   */

  @Override
  //base case sort
  public ILoString sort() {
    return new MtLoString();
  }

  @Override
  //appends end of list to current
  public ILoString insert(String str) {
    return new ConsLoString(str.toLowerCase(), this);
  }

  @Override
  //base case isSorted
  public boolean isSorted() {
    return true;
  }

  @Override
  //isSorted helper
  public boolean isSortedacc(String prev) {
    return true;
  }

  @Override
  //returns the rest of list
  public ILoString interleave(ILoString that) {
    return that;
  }

  @Override
  //returns rest of list
  public ILoString merge(ILoString that) {
    return that;
  }

  @Override
  //base case rebuilding list
  public ILoString reverse() {
    return new MtLoString();
  }

  @Override
  //reverse helper
  public ILoString reverseAcc(ILoString acc) {
    return acc;
  }

  @Override
  //base case isDoubledList
  public boolean isDoubledList() {
    return true;
  }

  @Override
  //checks if no matching pair
  public boolean isDoubledList(String prev) {
    return prev.equals(" ");
  }

  @Override
  //base case palindrome
  public boolean isPalindromeList() {
    return true;
  }
}
