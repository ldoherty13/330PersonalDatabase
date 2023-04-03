import java.util.*;

// Luke Doherty 

public class MusicVirtDAO {

   // Attributes
   private int songId;
   private String title;
   private String artistName;
   private String year;
   
   // Constructors
   public MusicVirtDAO() { }
   public MusicVirtDAO(int songId) {
      this.songId = songId;
   }
   public MusicVirtDAO(int songId, String title, String artistName, String year) {
      this.songId = songId;
      this.title = title;
      this.artistName = artistName;
      this.year = year;
    }

   // Operations 
   
   // -- Read
   public Boolean read() throws Exception {
      Database db = null;
      String sql = null;
      List<String> params = new ArrayList<String>();
      
      try {
         db = new Database();
         sql = "SELECT title, artistName, year FROM songs INNER JOIN artists USING(artistId) WHERE songId = ?";
         params.add(""+this.songId);
         List<String> results = db.getRow(sql, params);                
         if(results.size()>0) {
            this.title = results.get(0);
            this.artistName = results.get(1);
            this.year = results.get(2);
         }
         db.close();
      }
      catch (DLException ex) { 
         throw new Exception("Oops");
      }
      
      return true;
   }
      
   // -- Delete
   public int delete() throws Exception {
      Database db = null;
      String sql = null;
      int rc = -1;
      List<String> params = new ArrayList<String>();
      
      try {
         db = new Database();
         sql = "DELETE FROM songs WHERE songId = ?";
         params.add(""+this.songId);
         rc = db.execute(sql, params);
         db.close();
      }
      catch (DLException ex) { 
         throw new Exception("Oops");
      }
      return rc;
   }
   
   // -- Update
   public int update() throws Exception {
      Database db = null;
      String sql = null;
      int rc = -1;
      List<String> params = new ArrayList<String>();
      
      // - Build SQL string
      // - Run the statement
      try {
         db = new Database();
         sql = "UPDATE songs SET "
         + "title = ?, "
         + "year = ? "
         + "WHERE songId = ?";
         params.add(this.title);
         params.add(this.year);
         params.add(""+this.songId);
         rc = db.execute(sql, params);
         db.close();
      }
      catch (DLException ex) { 
         throw new Exception("Oops");
      }
      return rc;
   }
   
   // -- Insert
   public Boolean insert() {
      Database db = null;
      int rc = -1;
      String sql = null;
      List<String> params = new ArrayList<String>();
      
      try {
         db = new Database();
         db.startTransaction();
         sql = "SELECT artistId, artistName "
            + "FROM ARTISTS "
            + "WHERE artistName = ?";
         
         params.add(this.artistName);
         
         List<String> results = db.getRow(sql, params);
         //System.out.println(results);
         String artistId = null;
         //String artistName = null;
         //String songId = null;
         
         //System.out.println("Results size = " + results.size());
         if(results.size() >= 1) {
            this.songId = Integer.parseInt(db.getNewId("SONGS", "songId"));
            artistId = results.get(0);
         }
         else if(results.size() == 0) {
               System.out.println("Rolling back");
               db.rollbackTransaction();
               return false;
         }
         
         // Update in Insert
         try {
            
            sql = "UPDATE songs SET "
            + "title = ?, "
            + "artistId = ?, "
            + "year = ? "
            + "WHERE songId = ?";
            params.add(this.title);
            params.add(artistId);
            params.add(this.year);
            params.add(""+this.songId);
            rc = db.execute(sql, params);
            
         }
         catch (DLException ex) { 
            throw new Exception("Oops");
         }
      
         db.commitTransaction();
         db.close();
      }
      catch(Exception e) {
         try {
            db.rollbackTransaction();
         }
         catch(Exception ex) {
            {}
         }
      }
      
      return true;
   }
      
   // Accessors and Mutators
   public int getSongId() { return this.songId; }
   public String getTitle() { return this.title; }
   public String getArtistName() { return this.artistName; }
   public String getYear() { return this.year; }
   
   public void setSongId(int songId) {this.songId = songId; }
   public void setTitle(String title) { this.title = title; }
   public void setArtistName(String artistName) { this.artistName = artistName; }
   public void setYear(String year) { this.year = year; }

   
   // Main
   public static void main(String[] args) throws Exception {
      Boolean rc = false;
      int numRecords = 0;
      MusicVirtDAO dao = new MusicVirtDAO(2);
      
      // check read operation
      try {
         rc = dao.read();
      } 
      catch (DLException ex) { 
         throw new Exception("Oops");
      }
      System.out.println(dao.songId);
      System.out.println(dao.title);
      System.out.println(dao.artistName);
      System.out.println(dao.year);
      System.out.println("*******************");
      
      // check update operation
      try {
         dao.setTitle("Testing Update");
         numRecords = dao.update();
         rc = dao.read();
      }
      catch (Exception ex) {
      }
      System.out.println(dao.songId);
      System.out.println(dao.title);
      System.out.println(dao.artistName);
      System.out.println(dao.year);
      System.out.println("*******************");
      
      // check insert operation
      try {
         dao.setTitle("Testing Insert");
         rc = dao.insert();
         rc = dao.read();
      }
      catch (Exception ex) {
      }
      System.out.println(dao.songId);
      System.out.println(dao.title);
      System.out.println(dao.artistName);
      System.out.println(dao.year);
      System.out.println("*******************");
      
      // check delete operation
      try {
         numRecords = dao.delete();
         rc = dao.read();
         if (!rc) System.out.println("Info not found");
      }
      catch (Exception ex) {
      }
      System.out.println(dao.songId);
      System.out.println(dao.title);
      System.out.println(dao.artistName);
      System.out.println(dao.year);
      System.out.println("*******************");

   }
}