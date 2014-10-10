package gamification;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;

public class Score {

  // ------------------ App ussage ---------------------
  // login in the app
  public static final float LOGIN = 5;
  // Plan the day
  public static final float AGENDA = 5;
//  // Comment a session
//  public static final float COMMENT = 5;
//  // Like a comment
//  public static final float LIKE = 1;
  // Liked your comment
  public static final float LIKED = (float) 1;
  
  //5 users liked your comment 
  public static final float LIKED5 = (float) 5;

  // PUZZLE
  public static final float PUZZLE = (float) 35;  
  
  // ---------------- Codes ---------------
  // Early Bird
  public static final float EARLY_BIRD = (float) 15;
  public static final String EARLY_BIRD_NAME = "Early Bird";

  // Session Q&A
  public static final float SESSION_QA = (float) 20;
  public static final String SESSION_QA_NAME = "Breakout Session";

  // Session special question
  public static final float SESSION_SPECIAL = (float) 10;
  public static final String GIFT_NAME = "Gift";
  public static final float GIFT = (float) 10;

  // others ?

  public static Hashtable<String, Float> codeValues = new Hashtable<String, Float>();
  public static Hashtable<String, String> codeNames = new Hashtable<String, String>();

  public static void loadCodesFromFile(String fileName) throws IOException {    
    RandomAccessFile f = new RandomAccessFile(fileName, "rw");
    Log.text("Loading codes from file: " + fileName);
    try {
      String line = f.readLine();
      while (line != null) {
        int index = line.indexOf(",");
        String code = line.substring(0, index);
        String name = line.substring(index + 1);
        Log.text("|" + code + "|" + name + "|");
        switch (name) {
          case EARLY_BIRD_NAME:{
            codeValues.put(code, EARLY_BIRD);
            codeNames.put(code, EARLY_BIRD_NAME);
            break;
          }
          case SESSION_QA_NAME:{
            codeValues.put(code, SESSION_QA);
            codeNames.put(code, SESSION_QA_NAME);
            break;
          }          
        }
        line = f.readLine();
      }
    } finally {
      f.close();
    }
  }

}
