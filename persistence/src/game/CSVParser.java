package game;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;

public class CSVParser {

  static float total1MB;
  static float count1MB30s;
  static float count1MB60s;
  static float count1MB2m;
  static float count1MB5m;
  static float count1MB10m;

  static float total5MB;
  static float count5MB30s;
  static float count5MB60s;
  static float count5MB2m;
  static float count5MB5m;
  static float count5MB10m;

  static float total10MB;
  static float count10MB30s;
  static float count10MB60s;
  static float count10MB2m;
  static float count10MB5m;
  static float count10MB10m;

  static float total50MB;
  static float count50MB30s;
  static float count50MB60s;
  static float count50MB2m;
  static float count50MB5m;
  static float count50MB10m;

  static float c_count1MB60s;
  static float c_count1MB2m;
  static float c_count1MB5m;
  static float c_count1MB10m;
  static float c_total1MB;

  static float c_count5MB60s;
  static float c_count5MB2m;
  static float c_count5MB5m;
  static float c_count5MB10m;
  static float c_total5MB;
  
  static float c_count10MB60s;
  static float c_count10MB2m;
  static float c_count10MB5m;
  static float c_count10MB10m;
  static float c_total10MB;

  static float c_count50MB60s;
  static float c_count50MB2m;
  static float c_count50MB5m;
  static float c_count50MB10m;
  static float c_total50MB;  
  
  
  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    File[] files = new File("c:/temp/audit_csv_FACTORY/").listFiles();
    for (int i = 0; i < files.length; i++) {
      if (!files[i].getName().contains("2014-02")) { // skip everything else
        continue;
      }
      System.out.println("File: "  + files[i].getName());
      RandomAccessFile f = new RandomAccessFile(files[i], "rw");
      try {

        f.readLine(); // header line
        StringTokenizer st = new StringTokenizer(f.readLine(), ";");
        st.nextToken(); // All
        st.nextToken(); // empty
        total1MB += Integer.parseInt(st.nextToken().trim());
        total5MB += Integer.parseInt(st.nextToken().trim());
        total10MB += Integer.parseInt(st.nextToken().trim());
        total50MB += Integer.parseInt(st.nextToken().trim());

        st = new StringTokenizer(f.readLine(), ";"); // success 0-30 line
        st.nextToken(); // Success
        st.nextToken(); // time
        count1MB30s += Integer.parseInt(st.nextToken().trim());
        count5MB30s += Integer.parseInt(st.nextToken().trim());
        count10MB30s += Integer.parseInt(st.nextToken().trim());
        count50MB30s += Integer.parseInt(st.nextToken().trim());

        st = new StringTokenizer(f.readLine(), ";"); // success 30-60 line
        st.nextToken(); // Success
        st.nextToken(); // time
        count1MB60s += Integer.parseInt(st.nextToken().trim());
        count5MB60s += Integer.parseInt(st.nextToken().trim());
        count10MB60s += Integer.parseInt(st.nextToken().trim());
        count50MB60s += Integer.parseInt(st.nextToken().trim());

        st = new StringTokenizer(f.readLine(), ";"); // success 1-2 line
        st.nextToken(); // Success
        st.nextToken(); // time
        count1MB2m += Integer.parseInt(st.nextToken().trim());
        count5MB2m += Integer.parseInt(st.nextToken().trim());
        count10MB2m += Integer.parseInt(st.nextToken().trim());
        count50MB2m += Integer.parseInt(st.nextToken().trim());

        st = new StringTokenizer(f.readLine(), ";"); // success 2-5 line
        st.nextToken(); // Success
        st.nextToken(); // time
        count1MB5m += Integer.parseInt(st.nextToken().trim());
        count5MB5m += Integer.parseInt(st.nextToken().trim());
        count10MB5m += Integer.parseInt(st.nextToken().trim());
        count50MB5m += Integer.parseInt(st.nextToken().trim());

        st = new StringTokenizer(f.readLine(), ";"); // success 5-10 line
        st.nextToken(); // Success
        st.nextToken(); // time
        count1MB10m += Integer.parseInt(st.nextToken().trim());
        count5MB10m += Integer.parseInt(st.nextToken().trim());
        count10MB10m += Integer.parseInt(st.nextToken().trim());
        count50MB10m += Integer.parseInt(st.nextToken().trim());
      } finally {
        f.close();
      }
    }

    System.out.println("------------------------");
    System.out.println("TOTAL 1MB : " + total1MB);
    System.out.println("count 0s-30: " + count1MB30s);
    System.out.println("count 30-60: " + count1MB60s);
    System.out.println("count 1m-2m: " + count1MB2m);
    System.out.println("count 2m-5m: " + count1MB5m);
    System.out.println("count 5m-10: " + count1MB10m);
    System.out.println("------------------------");

    System.out.println("TOTAL 5MB : " + total5MB);
    System.out.println("count 0s-30: " + count5MB30s);
    System.out.println("count 30-60: " + count5MB60s);
    System.out.println("count 1m-2m: " + count5MB2m);
    System.out.println("count 2m-5m: " + count5MB5m);
    System.out.println("count 5m-10: " + count5MB10m);
    System.out.println("------------------------");

    System.out.println("TOTAL 10MB : " + total10MB);
    System.out.println("count 0s-30: " + count10MB30s);
    System.out.println("count 30-60: " + count10MB60s);
    System.out.println("count 1m-2m: " + count10MB2m);
    System.out.println("count 2m-5m: " + count10MB5m);
    System.out.println("count 5m-10: " + count10MB10m);
    System.out.println("------------------------");

    System.out.println("TOTAL 50MB : " + total50MB);
    System.out.println("count 0s-30: " + count50MB30s);
    System.out.println("count 30-60: " + count50MB60s);
    System.out.println("count 1m-2m: " + count50MB2m);
    System.out.println("count 2m-5m: " + count50MB5m);
    System.out.println("count 5m-10: " + count50MB10m);
    System.out.println("------------------------");

    
    c_count1MB60s = 1*100*count1MB60s/total1MB;
    c_count1MB2m =  2*100*count1MB2m/total1MB;
    c_count1MB5m =  3*100*count1MB5m/total1MB;
    c_count1MB10m = 4*100*count1MB10m/total1MB;
    c_total1MB = c_count1MB60s + c_count1MB2m + c_count1MB5m + c_count1MB10m;

    c_count5MB60s = 1*100*count5MB60s/total5MB;
    c_count5MB2m =  2*100*count5MB2m/total5MB;
    c_count5MB5m =  3*100*count5MB5m/total5MB;
    c_count5MB10m = 4*100*count5MB10m/total5MB;
    c_total5MB = c_count5MB60s + c_count5MB2m + c_count5MB5m + c_count5MB10m;
    
    c_count10MB60s = 1*100*count10MB60s/total10MB;
    c_count10MB2m =  2*100*count10MB2m/total10MB;
    c_count10MB5m =  3*100*count10MB5m/total10MB;
    c_count10MB10m = 4*100*count10MB10m/total10MB;
    c_total10MB = c_count10MB60s + c_count10MB2m + c_count10MB5m + c_count10MB10m;

    c_count50MB60s = 1*100*count50MB60s/total50MB;
    c_count50MB2m =  2*100*count50MB2m/total50MB;
    c_count50MB5m =  3*100*count50MB5m/total50MB;
    c_count50MB10m = 4*100*count50MB10m/total50MB;
    c_total50MB = c_count50MB60s + c_count50MB2m + c_count50MB5m + c_count50MB10m;
    
    System.out.println("TOTAL coef: " + c_total1MB + " " + c_total5MB + " " + c_total10MB + " " + c_total50MB);
    
    System.out.println("DETENTION: " + (int) (4*c_total1MB + 3*c_total5MB + 2*c_total10MB + 1*c_total50MB));
    
  }

}
