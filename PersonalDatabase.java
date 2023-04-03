import java.util.*;
import java.sql.*;
import java.io.*;

public class PersonalDatabase {

   // Luke Doherty ISTE 330
   // Attributes
   
   String protocol;
   String driver;
   String host; 
   String userId;
   String password;
   String db;
   Connection conn;
   int transactionCount;
   
   // Constructors 
   public PersonalDatabase(String protocol, String host, String userId, String password, String db) throws DLException { 
      
      try {
      
         this.protocol = protocol;
         this.driver = driver;
         this.host = host;
         this.userId = userId;
         this.password = password;
         this.db = db;
         
         connect();
         
      } catch(SQLException e) {
         throw new DLException("Constructor1", e);
      }
   }
   
   public PersonalDatabase() throws FileNotFoundException, DLException {
   
         File config = new File("dbconfig.properties");
         Scanner s = new Scanner(config);
         ArrayList<String> fields = new ArrayList<String>();
         s.nextLine();
         
         while(s.hasNextLine()) {
            String line = s.nextLine();
            String edit = line.substring(line.indexOf("=") + 1);
            edit.trim();
            fields.add(edit);
         }
         
         this.protocol = fields.get(0);
         this.driver = fields.get(1);
         this.host = fields.get(2);
         this.userId = fields.get(3);
         this.password = fields.get(4);
         this.db = fields.get(5);
         
         try { connect(); }
         
         catch(SQLException e) {
            throw new DLException("Constructor2", e);
         
      }
   }
   
   public PersonalDatabase(String fileName) throws DLException {
      try {
         FileReader reader = new FileReader(fileName);
         Properties properties = new Properties();
         
         properties.load(reader);
         this.protocol = properties.getProperty("protocol");
         this.driver = properties.getProperty("driver");
         this.host = properties.getProperty("host");
         this.userId = properties.getProperty("user");
         this.password = properties.getProperty("password");
         this.db = properties.getProperty("db");
         
         connect();
         
      }catch(Exception ex) {
         throw new DLException("Constructor3", ex);
      }
   }
   
   public Connection getConnection() {
      return this.conn;
   }
   
   // Closes connection to database
   public Boolean closeConnection() {
      boolean rc = false;
      try {
         this.conn.close();
         rc = true;
      }  catch(Exception ex) {
         return rc;
      }
      return rc;
   }
   
   // Handles prepared statements
   private PreparedStatement prepareAndBind(String sql, List<String> params) throws DLException {
      
      try {
         PreparedStatement pstmt = this.conn.prepareStatement(sql);    
         if(params != null) {
            for(int i=0; i<params.size(); i++) {
               pstmt.setString(i+1, params.get(i));
            } 
         }
         return pstmt;
         
      } catch(Exception ex) {
         throw new DLException("Prepare and Bind", ex);
      }
   }
   
   // Connects to database
   public void connect() throws SQLException {
      String uri = protocol
                     + "://"
                     + host
                     + "/" + db;
                     
      //System.out.println(uri);
      conn = DriverManager.getConnection(uri, userId, password);
   }
   
   // Handles & runs SQL commands
   public int execute(String sql, List<String> params) throws DLException {
      int numRows = -1;   
      try {
         PreparedStatement pstmt = prepareAndBind(sql, params);
         numRows = pstmt.executeUpdate();
      } catch(Exception ex) {
         throw new DLException("Execute", ex);
      }
      return numRows;
   }
   
   // Creates a new Id row using INSERT INTO, populates based on fKeys
   public int getNewId(String tName, String pkName, List<List<String>> fKeys) throws DLException {
      
      int numRows = -1;
      int newPk = 0;
      try {
         this.startTransaction();
         List<String> params = new ArrayList<String>();
         String sql = ("SELECT "+pkName+" FROM "+tName+" ORDER BY "+pkName+" DESC LIMIT 1");
         //String sql = ("SELECT MAX("+pkName+") FROM "+tName+";");
         //newPk = Integer.parseInt(sql); newPk++;
         
         PreparedStatement pstmt = prepareAndBind(sql, params);
         ResultSet rs = pstmt.executeQuery();
         
         int currentRow = 0;
         while(rs.next()) {
            newPk = rs.getInt(1);
         }
         newPk++;
         
         //String exVal = "14624"; <FIND A WAY TO ITERATE THROUGH THIS>              
         //sql = "INSERT INTO "+tName+" ("+pkName+", Zip) VALUES ("+newPk+", "+exVal+");";
         
         String sql2 = "";
         sql = "INSERT INTO "+tName+" ("+pkName;
         for(int i=0; i<fKeys.size(); i++) {
            params.add(fKeys.get(i).get(1));
            sql+= ", "+fKeys.get(i).get(0);
            sql2+= ", ?";
         }
         sql+= ") VALUES (" + newPk;
         sql+=sql2+")";
         pstmt = prepareAndBind(sql, params);
         numRows = pstmt.executeUpdate();
         
         this.commitTransaction();
      }
      catch(SQLException sqlex) {
         this.rollbackTransaction();
         throw new DLException("SQL Ex. NewID", sqlex);
      }
      catch(Exception e){
         this.rollbackTransaction();
         throw new DLException("NewID error", e);
      }
      return newPk;
   }
   
   // Handles transactions
   public void startTransaction() throws DLException {
      transactionCount++;
      try {
         conn.setAutoCommit(false);
      }
      catch(Exception e) {
         throw new DLException("start transaction", e);
      }
   }
   
   public void commitTransaction() throws DLException {
      transactionCount--;
      try {
         conn.commit();
         conn.setAutoCommit(true);
      }
      catch(Exception e) {
         throw new DLException("commit transaction", e);
      }
   }
   
   public void rollbackTransaction() throws DLException {
      transactionCount--;
      try {
         conn.rollback();
         conn.setAutoCommit(true);
      }
      catch(Exception e) {
         throw new DLException("rollback transaction", e);
      }
   }
   
   // start transaction -> conn.setAutoCommit(false);
   // if (everythingWorked) { conn.commit(); } else { conn.rollback(); }
   // end transaction -> conn.setAutoCommit(true);
   
   // Handles stored procedures
   public List<List<String>> run(String procName, List<String> params) throws DLException {
      try {
         List<List<String>> data = new ArrayList<List<String>>();
         String sql = "(call "+ procName + "(";
         
         if(params != null) {
            sql += "?";
         }
         for(int i=1; i<params.size(); i++) {
            sql += ", ?";
         }
         sql += ") }";
         
         CallableStatement cstmt = this.conn.prepareCall(sql);
         
         for(int i=0; i<params.size(); i++) {
            cstmt.setString(i+1, params.get(1));
         }
         
         ResultSet rs = cstmt.executeQuery();
         ResultSetMetaData rsmd = rs.getMetaData();
         
         while(rs.next()) {
            List<String> row = new ArrayList<String>();
            
            for(int i=1; i<rsmd.getColumnCount(); i++) {
               row.add(rs.getString(i));
            }
            data.add(row);
         }
         return data;
      }
      catch(Exception ex) {
         throw new DLException("", ex);
      }
      
   }
   
   // Runs SQL statement to load results into 2D arraylist (table)
   public List<List<String>> getTable(String sql, List<String> params) throws DLException {
      try {
         return getTable(sql, params, false);
      } 
      catch(Exception e){
         throw new DLException("Table Getter", e);
      }
   }
   
   // Runs SQL statement to load results into 2D arraylist (table), overloaded with boolean parameter
   public List<List<String>> getTable(String sql, List<String> params, Boolean includeNames) throws DLException {
      
      List<List<String>> data = new ArrayList<List<String>>();  
      try
      {
         PreparedStatement pstmt = prepareAndBind(sql, params);
         ResultSet rs = pstmt.executeQuery();
         ResultSetMetaData rsmd = rs.getMetaData();
         
         if(includeNames == false) {
            while(rs.next())
            {
               List<String> row = new ArrayList<String>();
               for(int i=1; i<=rsmd.getColumnCount(); i++)
               {
                  row.add(rs.getString(i));
               }
               data.add(row);
            }
         }
         else {
            List<String> row0 = new ArrayList<String>();
            for(int i=1; i<=rsmd.getColumnCount(); i++) {
                  row0.add(rsmd.getColumnName(i));
            }
            data.add(row0);
            
            while(rs.next())
            {
               List<String> row = new ArrayList<String>();
               for(int i=1; i<=rsmd.getColumnCount(); i++)
               {
                  row.add(rs.getString(i));
               }
               data.add(row);
            }
         }
      }
      catch(SQLException sqlex) {
         throw new DLException("SQL Ex. OverloadTable", sqlex);
      }
      catch(Exception e){
         throw new DLException("Overloaded Table Getter", e);
      }
      return data;
   }
   
   // Runs SQL statements to load results into a 1D arraylist (row)
   public List<String> getRow(String sql, List<String> params) throws DLException {
      
      List<String> data = new ArrayList<String>();   
      try
      {
         PreparedStatement pstmt = this.prepareAndBind(sql, params);
         ResultSet rs = pstmt.executeQuery();
         ResultSetMetaData rsmd = rs.getMetaData();                
         rs.next();
         for(int i=1; i<=rsmd.getColumnCount(); i++){
            data.add(rs.getString(i));
         }       
      }
      catch(SQLException sqlex) {
         throw new DLException("SQL Ex. Row", sqlex);
      }
      catch(Exception e){
         throw new DLException("Row Getter", e);
      }
      return data;
   }
   
   // Runs SQL statement to load results into a single stirng variable (value)
   public String getValue(String sql, List<String> params) throws DLException
   {
   
      String data = null;
      try {
         PreparedStatement pstmt = prepareAndBind(sql, params);
         ResultSet rs = pstmt.executeQuery(); 
         while(rs.next()) {
            data = rs.getString(1);
         }    
         // INTENTIONAL ERROR TO TEST EXCEPTION CATCHING VVV
         //data =  rs.getString(100);       
         // INTENTIONAL ERROR TO TEST EXCEPTION CATCHING ^^^  
      }
      catch(SQLException sqlex) {
         throw new DLException("SQL Ex. Value", sqlex);
      }
      catch(Exception e){
         throw new DLException("Value Getter", e);
      }
      return data;
   }
   
   // Main method
   public static void main(String[] args) throws DLException {
      String sql = null;
      int numRows = 0;
      List<String> params = new ArrayList<String>();
      
      try {
         PersonalDatabase db1 = new PersonalDatabase();
         System.out.println("Database = "+db1.getConnection().getCatalog());
         sql = "Update Passenger SET fname = 'Luke' WHERE PassengerID = ?";
         params.add(""+9);
         numRows = db1.execute(sql,params);
         System.out.println("Number of rows affected in database = " + numRows + "\n");
         
         params = new ArrayList<String>();
         sql = "Select PassengerID, fname, lname, street, zip FROM passenger WHERE PassengerID < ?";
         params.add(""+3);
         System.out.println("Overloaded Method -> Passengers with PassengerID less than 3: "+db1.getTable(sql,params,true) + "\n");
         
         params = new ArrayList<String>();
         sql = "Select PassengerID, fname, lname, street, zip FROM passenger WHERE PassengerID < ?";
         params.add(""+5);
         System.out.println("Table Method -> Passengers with PassengerID less than 5: "+db1.getTable(sql,params) + "\n"); 
         
         params = new ArrayList<String>();
         sql = "Select PassengerID, fname, lname, street, zip FROM passenger WHERE PassengerID = ?";
         params.add(""+5);
         System.out.println("Row Method -> Passengers with PassengerID = 5: "+db1.getRow(sql,params) + "\n"); 
         
         params = new ArrayList<String>();
         sql = "Select fname FROM passenger WHERE PassengerID = ?";
         params.add(""+5);
         System.out.println("Value Method -> First name of Passenger with PassengerID = 5: "+db1.getValue(sql,params) + "\n"); 
         
         String tName = "passenger";
         String pkName = "PassengerID";
         String fkName = "zip";
         String fkName2 = "FName"; 
         
         List<List<String>> fKeys = new ArrayList<List<String>>();
         ArrayList<String> row = new ArrayList<String>();
         ArrayList<String> row1 = new ArrayList<String>();
         ArrayList<String> row2 = new ArrayList<String>();
      
         row.add("zip");
         row.add("14624");
         fKeys.add(row);
         
         row1.add("Fname");
         row1.add("Luke");
         fKeys.add(row1);
         
         System.out.println("New ID: " + db1.getNewId(tName, pkName, fKeys));
         
         db1.closeConnection();          
      }
      catch(Exception ex) {
         throw new DLException("Main", ex);
      }
   }
}