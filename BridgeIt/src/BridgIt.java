import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import javalib.worldcanvas.WorldCanvas;

// utilities for the BridgIt game
class BridgItUtil {
  Color posToColor(int x, int y) {
    if (x % 2 != 0 && y % 2 == 0) {
      return BridgIt.PLAYER_ONE_COLOR;
    } else if (x % 2 == 0 && y % 2 != 0) {
      return BridgIt.PLAYER_TWO_COLOR;
    } else {
      return BridgIt.BOARD_COLOR;
    }
  }
}

// the BridgIt game
class BridgIt extends World {
  static int BOARD_SIZE = 11;
  static Color PLAYER_ONE_COLOR = Color.MAGENTA;
  static Color PLAYER_TWO_COLOR = Color.PINK;
  static Color BOARD_COLOR = Color.WHITE;
  Graph board;
  Color playerTurn;

  BridgIt() {

//    //Board must be larger than 3
//    if (BridgIt.BOARD_SIZE < 5) {
//      BridgIt.BOARD_SIZE = 5;
//    }
//
//    //Board must be odd sized
//    if (BridgIt.BOARD_SIZE % 2 == 0) {
//      BridgIt.BOARD_SIZE++;
//    }

    this.board = new Graph();
    this.playerTurn = BridgIt.PLAYER_ONE_COLOR;
    
    // create all cells
    for (int i = 0; i < BridgIt.BOARD_SIZE; i++) {
      for (int j = 0; j < BridgIt.BOARD_SIZE; j++) {
        this.board.addCell(new Cell(j, i, new BridgItUtil().posToColor(j, i)));
      }
    }
    
    // link edge cells
    for (int k = 1; k <= BridgIt.BOARD_SIZE - 3; k += 2) {
      this.getCellAtIndex(k, 0).linkTo(this.getCellAtIndex(k + 2, 0));
      this.getCellAtIndex(k, BridgIt.BOARD_SIZE - 1).linkTo(this.getCellAtIndex(k + 2, BridgIt.BOARD_SIZE - 1));
      this.getCellAtIndex(0, k).linkTo(this.getCellAtIndex(0, k + 2));
      this.getCellAtIndex(BridgIt.BOARD_SIZE - 1, k).linkTo(this.getCellAtIndex(BridgIt.BOARD_SIZE - 1, k + 2));
    }
  }
  
  

  // draws the scene
  public WorldScene makeScene() {
    WorldScene s = new WorldScene(this.board.size() * Cell.SIDE_LENGTH,
          this.board.size() * Cell.SIDE_LENGTH);
    for (Cell c : this.board.allCells) {
      s.placeImageXY(c.draw(), c.x * Cell.SIDE_LENGTH, c.y * Cell.SIDE_LENGTH);
    }

    return s;
  }
  
  Cell getCellAt(Posn pos) {
    return this.board.allCells.get(pos.y / Cell.SIDE_LENGTH * BridgIt.BOARD_SIZE + pos.x / Cell.SIDE_LENGTH);
  }
  
  Cell getCellAtIndex(int x, int y) {
    return this.board.allCells.get(y * BridgIt.BOARD_SIZE + x);
  }

//  //EFFECT: Game play logic turn based clicks and switch turns check win link cells
//  public void onMouseReleased(Posn pos) {
//    Cell tempCell = this.getCellAt(pos);
//    if (tempCell.color.equals(BridgIt.BOARD_COLOR) &&
//          !this.outside(tempCell)) {
//      tempCell.color = this.playerTurn;
//      Cell tempAboveCell = this.getCellAtIndex(tempCell.x, tempCell.y - 1);
//      if (tempAboveCell.color.equals(this.playerTurn)) {
//        tempAboveCell.linkTo(this.getCellAtIndex(tempCell.x, tempCell.y + 1));
//      } else {
//        this.getCellAtIndex(tempCell.x - 1, tempCell.y)
//        .linkTo(this.getCellAtIndex(tempCell.x + 1, tempCell.y));
//      }
//      this.swapTurns();
//    }
//
//
//    if (this.board.hasPathBetween(
//        this.getCellAtIndex(1, 0),
//        this.getCellAtIndex(1, BridgIt.BOARD_SIZE - 1))
//        || this.board.hasPathBetween(
//            this.getCellAtIndex(0, 1),
//            this.getCellAtIndex(BridgIt.BOARD_SIZE - 1, 1))) {
//      System.out.println("win!");
//
//    }
//
//  }
//
//  //is cell is inside play area?
//  Boolean outside(Cell tempCell) {
//    return tempCell.x == 0 || tempCell.x == BridgIt.BOARD_SIZE - 1 ||
//          tempCell.y == 0 || tempCell.y == BridgIt.BOARD_SIZE - 1;
//  }
//
//  //EFFECT: switch current player turn
//  void swapTurns() {
//    if (this.playerTurn.equals(BridgIt.PLAYER_ONE_COLOR)) {
//      this.playerTurn = BridgIt.PLAYER_TWO_COLOR;
//    } else {
//      this.playerTurn = BridgIt.PLAYER_ONE_COLOR;
//    }
//  }
}

// tests and examples for BridgIt
class ExamplesBridgIt {

  //draw the game
  void Draw(Tester t) {
    BridgIt temp = new BridgIt();
    WorldCanvas c = 
        new WorldCanvas(
            BridgIt.BOARD_SIZE * Cell.SIDE_LENGTH,
              BridgIt.BOARD_SIZE * Cell.SIDE_LENGTH);

    c.drawScene(temp.makeScene());
    c.show();
  }
//
//  void testOnMouseReleased(Tester t) {
//    BridgIt temp = new BridgIt();
//
//    //test outside clicks no change
//    t.checkExpect(temp.board.allCells.get(0).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//    temp.onMouseReleased(new Posn(0,0));
//    t.checkExpect(temp.board.allCells.get(0).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//
//    t.checkExpect(temp.board.allCells.get(BridgIt.BOARD_SIZE - 1).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//    temp.onMouseReleased(new Posn(BridgIt.BOARD_SIZE * Cell.SIDE_LENGTH,0));
//    t.checkExpect(temp.board.allCells.get(BridgIt.BOARD_SIZE - 1).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//
//    t.checkExpect(temp.getCellAtIndex(0, BridgIt.BOARD_SIZE - 1).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//    temp.onMouseReleased(new Posn(0, Cell.SIDE_LENGTH * (BridgIt.BOARD_SIZE - 1)));
//    t.checkExpect(temp.getCellAtIndex(0, BridgIt.BOARD_SIZE - 1).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//
//    t.checkExpect(temp.getCellAtIndex(BridgIt.BOARD_SIZE - 1, 0).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//    temp.onMouseReleased(new Posn(Cell.SIDE_LENGTH * (BridgIt.BOARD_SIZE - 1),
//          Cell.SIDE_LENGTH * (BridgIt.BOARD_SIZE - 1)));
//    t.checkExpect(temp.getCellAtIndex(BridgIt.BOARD_SIZE - 1, 0).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//
//    //no change if click on color
//    t.checkExpect(temp.getCellAtIndex(2,1).color, Color.PINK);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//    temp.onMouseReleased(new Posn(2 * Cell.SIDE_LENGTH, Cell.SIDE_LENGTH));
//    t.checkExpect(temp.getCellAtIndex(2,1).color, Color.PINK);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//
//    t.checkExpect(temp.getCellAtIndex(1,2).color, Color.MAGENTA);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//    temp.onMouseReleased(new Posn(Cell.SIDE_LENGTH, 2 * Cell.SIDE_LENGTH));
//    t.checkExpect(temp.getCellAtIndex(1,2).color, Color.MAGENTA);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//
//    //change if click in play zone and white box check links
//    t.checkExpect(temp.board.hasPathBetween(temp.getCellAtIndex(1,0),
//          temp.getCellAtIndex(1,2)), false);
//    t.checkExpect(temp.getCellAtIndex(1,1).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//    temp.onMouseReleased(new Posn(Cell.SIDE_LENGTH, Cell.SIDE_LENGTH));
//    t.checkExpect(temp.getCellAtIndex(1,1).color, Color.MAGENTA);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_TWO_COLOR);
//    t.checkExpect(temp.board.hasPathBetween(temp.getCellAtIndex(1,0),
//          temp.getCellAtIndex(1,2)), true);
//
//
//    t.checkExpect(temp.board.hasPathBetween(temp.getCellAtIndex(0,3),
//          temp.getCellAtIndex(2,3)), false);
//    t.checkExpect(temp.getCellAtIndex(1,3).color, Color.WHITE);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_TWO_COLOR);
//    temp.onMouseReleased(new Posn(Cell.SIDE_LENGTH, Cell.SIDE_LENGTH * 3));
//    t.checkExpect(temp.getCellAtIndex(1,3).color, Color.PINK);
//    t.checkExpect(temp.playerTurn, BridgIt.PLAYER_ONE_COLOR);
//    t.checkExpect(temp.board.hasPathBetween(temp.getCellAtIndex(0,3),
//          temp.getCellAtIndex(2,3)), true);
//  }
//
//  //test turn swap
//  void testSwapTurns(Tester t) {
//    BridgIt temp = new BridgIt();
//    t.checkExpect(temp.playerTurn ,BridgIt.PLAYER_ONE_COLOR);
//    temp.swapTurns();
//    t.checkExpect(temp.playerTurn ,BridgIt.PLAYER_TWO_COLOR);
//    temp.swapTurns();
//    t.checkExpect(temp.playerTurn ,BridgIt.PLAYER_ONE_COLOR);
//  }
//
//  //test mouse outside play area
//  boolean testOutside(Tester t) {
//    BridgIt temp = new BridgIt();
//    return t.checkExpect(temp.outside(temp.board.allCells.get(0)), true)
//          && t.checkExpect(
//                temp.outside(temp.board.allCells.get(BridgIt.BOARD_SIZE - 1)), true)
//          && t.checkExpect(temp.outside(
//                temp.getCellAtIndex(BridgIt.BOARD_SIZE - 1, 0)), true)
//          && t.checkExpect(temp.outside(
//          temp.getCellAtIndex(0,BridgIt.BOARD_SIZE - 1)), true)
//          && t.checkExpect(
//                temp.outside(temp.board.allCells.get(BridgIt.BOARD_SIZE + 1)), false);
//  }
  
  void testBigBang(Tester t) {
    BridgIt temp = new BridgIt();
    temp.bigBang(900, 900, 0.1);
  }

  //test linkTo in Cell
  void testLinkTo(Tester t) {
    BridgIt temp = new BridgIt();
    t.checkExpect(temp.board.allCells.get(6).outEdges.size(), 0);
    t.checkExpect(temp.board.allCells.get(4).outEdges.size(), 0);
    temp.board.allCells.get(6).linkTo(temp.board.allCells.get(4));
    t.checkExpect(temp.board.allCells.get(6).outEdges.size(), 1);
    t.checkExpect(temp.board.allCells.get(4).outEdges.size(), 1);
    t.checkExpect(temp.board.allCells.get(6).outEdges.get(0).to, temp.board.allCells.get(4));
    t.checkExpect(temp.board.allCells.get(4).outEdges.get(0).to, temp.board.allCells.get(6));
  }

  //test addOutEdge in Cell
  void testAddOutEdge(Tester t) {
    BridgIt temp = new BridgIt();
    t.checkExpect(temp.board.allCells.get(6).outEdges.size(), 0);
    t.checkExpect(temp.board.allCells.get(4).outEdges.size(), 0);
    //link link top middle with middle cell
    temp.board.allCells.get(6).addOutEdge(new Edge(temp.board.allCells.get(6),
          temp.board.allCells.get(4)));
    t.checkExpect(temp.board.allCells.get(6).outEdges.size(), 1);
    t.checkExpect(temp.board.allCells.get(4).outEdges.size(), 0);
    //link link middle with top middle cell
    temp.board.allCells.get(4).addOutEdge(new Edge(temp.board.allCells.get(4),
          temp.board.allCells.get(6)));
    t.checkExpect(temp.board.allCells.get(6).outEdges.size(), 1);
    t.checkExpect(temp.board.allCells.get(4).outEdges.size(), 1);
    t.checkExpect(temp.board.allCells.get(6).outEdges.get(0).to, temp.board.allCells.get(4));
    t.checkExpect(temp.board.allCells.get(4).outEdges.get(0).to, temp.board.allCells.get(6)); 
  }

  //test draw in Cell
  void testCellDraw(Tester t) {
    BridgIt temp = new BridgIt();
    t.checkExpect(temp.board.allCells.get(0).draw(),
          new RectangleImage(
                Cell.SIDE_LENGTH,
                Cell.SIDE_LENGTH,
                OutlineMode.SOLID,
                Color.WHITE)
                .movePinholeTo(new Posn(
                      (Cell.SIDE_LENGTH / 2 * -1),
                      (Cell.SIDE_LENGTH / 2 * -1))));
    t.checkExpect(temp.board.allCells.get(1).draw(),
          new RectangleImage(
                Cell.SIDE_LENGTH,
                Cell.SIDE_LENGTH,
                OutlineMode.SOLID,
                Color.MAGENTA)
                .movePinholeTo(new Posn(
                      (Cell.SIDE_LENGTH / 2 * -1),
                      (Cell.SIDE_LENGTH / 2 * -1))));
    t.checkExpect(temp.board.allCells.get(BridgIt.BOARD_SIZE).draw(),
          new RectangleImage(
                Cell.SIDE_LENGTH,
                Cell.SIDE_LENGTH,
                OutlineMode.SOLID,
                Color.PINK)
                .movePinholeTo(new Posn(
                      (Cell.SIDE_LENGTH / 2 * -1),
                      (Cell.SIDE_LENGTH / 2 * -1))));
  }

  //test addCell in Graph
  void testAddCell(Tester t) {
    Graph temp = new Graph();
    t.checkExpect(temp.size(), 0);
    temp.addCell(new Cell(1,2, Color.pink));
    t.checkExpect(temp.size(), 1);
    t.checkExpect(temp.allCells.get(0),new Cell(1,2, Color.pink));

    temp.addCell(new Cell(2,2, Color.white));
    t.checkExpect(temp.size(), 2);
    t.checkExpect(temp.allCells.get(1),new Cell(2,2, Color.white));
  }

  //test size in Graph
  void testSize(Tester t) {
    t.checkExpect(new Graph().size(), 0);
    BridgIt temp = new BridgIt();
    t.checkExpect(temp.board.size(), BridgIt.BOARD_SIZE*BridgIt.BOARD_SIZE);
  }
}
