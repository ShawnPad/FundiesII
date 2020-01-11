import java.util.ArrayList;
import java.util.ArrayList;

// an adjacency list representation of a graph
class Graph {
  ArrayList<Cell> allCells;
  
  Graph() {
    this.allCells = new ArrayList<Cell>();
  }

  //EFFECT: adds cell to this graph
  void addCell(Cell v) {
    this.allCells.add(v);
  }

  //size of graph
  int size() {
    return allCells.size();
  }
  
  boolean hasPathBetween(Cell from, Cell to) {
    ArrayList<Cell> alreadySeen = new ArrayList<Cell>();
    ArrayList<Cell> worklist = new ArrayList<Cell>();
   
    // Initialize the worklist with the from Cell
    worklist.add(from);
    // As long as the worklist isn't empty...
    while (!worklist.isEmpty()) {
      Cell next = worklist.remove(0);
      if (next.equals(to)) {
        return true; // Success!
      }
      else if (alreadySeen.contains(next)) {
        // do nothing: we've already seen this one
      }
      else {
        // add all the neighbors of next to the worklist for further processing
        for (Edge e : next.outEdges) {
          worklist.add(e.to);
        }
        // add next to alreadySeen, since we're done with it
        alreadySeen.add(next);
      }
    }
    // We haven't found the to Cell, and there are no more to try
    return false;
  }
}