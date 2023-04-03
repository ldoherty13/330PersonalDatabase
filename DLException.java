import java.util.*;
import java.sql.*;
import java.io.*;
import java.time.*;

// Luke Doherty ISTE 330

public class DLException extends Exception {
   private String location = null;
   private Exception ex = null;
   
   public DLException(String loc, Exception e) {
      super();
      this.location = loc;
      this.ex = e;
      writeLog();
   }
   public DLException(Exception e) {
      super();
      this.ex = e;
      writeLog();
   }
   
   public String getLocation() {
      return this.location;
   }
   
   private void writeLog() {
      try {
         FileWriter fw = new FileWriter("dataerror.log", true);
         fw.write("Exception triggered: " + LocalDateTime.now().toString() + "\n");
         fw.write("  Location: " + this.getLocation() + "\n");
         fw.write("  Message: " + ex.getMessage() + "\n");
         fw.write("  Class: " + ex.getClass() + "\n");
         fw.close();
      }
      catch (IOException e) {}
   }
   
}