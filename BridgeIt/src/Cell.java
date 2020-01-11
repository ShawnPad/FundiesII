import java.util.ArrayList;
import java.awt.Color;
import javalib.worldimages.*;

// a cell in the board
class Cell {
  static int SIDE_LENGTH = 50;
  int x;
  int y;
  Color color;
  ArrayList<Edge> outEdges;
  
  Cell(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.outEdges = new ArrayList<Edge>();
  }
  
  // EFFECT: adds an edge to this Cell's out-edges
  void addOutEdge(Edge e) {
    this.outEdges.add(e);
  }

  // EFFECT: links two cells together by adding an edge
  //         to each cell's list of outEdges
  void linkTo(Cell other) {
    this.addOutEdge(new Edge(this, other));
    other.addOutEdge(new Edge(other, this));
  }

  //draw this cell
  WorldImage draw() {
    return new RectangleImage(
        Cell.SIDE_LENGTH, 
        Cell.SIDE_LENGTH, 
        OutlineMode.SOLID, 
        this.color)
        .movePinholeTo(new Posn(
            (Cell.SIDE_LENGTH / 2 * -1),
            (Cell.SIDE_LENGTH / 2 * -1)));
  }
}