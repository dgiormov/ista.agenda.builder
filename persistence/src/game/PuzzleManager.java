package game;

import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

public class PuzzleManager {

  public static final int PUZZLE_COUNT = 5;
  public static final int PIECE_COUNT = 6;
  
  private int puzzle_counter = 1;
  private int piece_counter = 1;
  private BonusCodeGenerator bcg;
  private Vector<Piece> pieces = new Vector<Piece>(); 

  public static void main(String[] args) {
    PuzzleManager pd = new PuzzleManager();
    for (int i = 0; i < 625; i++) {
      pd.pieces.add(pd.dealNextPiece(i));
    }
    
    System.out.println(pd.checkPuzzleCode("aa.bb.cc.dd.ee.ff"));
    
  }

  public PuzzleManager() {
    bcg = new BonusCodeGenerator();
  }
  
  public Piece dealNextPiece(int id) {
    Piece p = new Piece();
    p.id = id;
    p.puzzle = puzzle_counter++;
    p.piece = piece_counter;
     if (puzzle_counter == PUZZLE_COUNT + 1) {
       puzzle_counter = 1;
       piece_counter++;
     }     
     if (piece_counter == PIECE_COUNT + 1) {
       piece_counter = 1;
     }
     p.code = bcg.getUnique2LettersCode();    
     return p;
  }
  
  public boolean checkPuzzleCode(String code) {    
    HashSet<Integer> positions = new HashSet<Integer>();
    positions.add(1);
    positions.add(2);
    positions.add(3);
    positions.add(4);
    positions.add(5);
    positions.add(6);    
    StringTokenizer st = new StringTokenizer(code, ".");
    String codeFromPiece = st.nextToken();
    Piece p = getByCode(codeFromPiece);
    if (p == null) return false; // no piece with that code
    int thePuzzle = p.puzzle;
    while (st.hasMoreTokens()) {
      codeFromPiece = st.nextToken();
      p = getByCode(codeFromPiece);      
      
      if ((p == null) ||
         (p.used) ||
         (p.puzzle != thePuzzle) ||
         (!positions.contains(p.piece))) {
        return false;
      } else {
        positions.remove(p.piece);
        p.used = true;
      }
    }
    
    return true;
  }
 
  private Piece getByCode(String codeFromPiece) {
    for (Piece p : pieces) {
      if (p.code.equalsIgnoreCase(codeFromPiece)) {
        return p;
      }
    }      
    return null;
  }

  class Piece {    
    
    int id = 0;
    int puzzle = 0;
    int piece = 0;
    String code = "";
    boolean used = false;
    
    public String toString() {      
      return "" + "User: " + id + "|Puzzle:" + puzzle + "|Piece:" + piece + "|Code: " + code + "|Used: " + used;      
    }
    
  }
  
}