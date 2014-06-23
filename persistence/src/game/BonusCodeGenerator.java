package game;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Random;

public class BonusCodeGenerator {

  public final String CRLF = "\r\n";
  public final String SEP = ",";
  public Random r = new Random(System.currentTimeMillis());
  private HashSet<String> hs = new HashSet<String>(100);

  public static void main(String[] args) throws IOException {
    BonusCodeGenerator bcg = new BonusCodeGenerator();
    bcg.generateCodesInFile(50, Score.EARLY_BIRD_NAME); // arrive before 9:15
    bcg.generateCodesInFile(39, Score.SESSION_QA_NAME); // 3 rewards per session (13 sessions)
  }

  public void generateCodesInFile(int number, String text) throws IOException {
    RandomAccessFile f = new RandomAccessFile("c:/temp/codes.txt", "rw");
    f.seek(f.length());
    for (int i = 0; i < number; i++) {
      String line = getUnique4LettersCode() + SEP + text + CRLF;
      f.write(line.getBytes());
    }
    f.close();
  }

  public String getUnique4LettersCode() { 
    String str = getSingle4LettersCode();
    while (hs.contains(str)) {
      str = getSingle4LettersCode();
    }
    
    hs.add(str);
    return str;
  }

  public String getSingle4LettersCode() {
    char c1 = (char) (97 + r.nextInt(25));
    char c2 = (char) (97 + r.nextInt(25));
    char c3 = (char) (97 + r.nextInt(25));
    char c4 = (char) (97 + r.nextInt(25));    
    return "" + c1+c2+c3+c4;
  }

  public String getUnique2LettersCode() { 
    String str = getSingle2LettersCode();
    while (hs.contains(str)) {
      str = getSingle2LettersCode();
    }    
    hs.add(str);
    return str;
  }  
  
  public String getSingle2LettersCode() {
    char c1 = (char) (97 + r.nextInt(25));
    char c2 = (char) (97 + r.nextInt(25));
    return "" + c1+c2;
  }  
    
  
  public String getSingle6NumbersCode() {
    String str = "" + Math.abs((new Random()).nextInt());
    while ((str.length() != 6) || hs.contains(str)) {
      str = Math.abs(r.nextInt()) + "";
    }
    return str;
  }  
  

}