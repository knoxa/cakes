package cakes.nlp.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Database {

	private static String JDBC_URL_PREFIX = "jdbc:derby:memory:";
	private String jdbcURL;
	private String name;
	private Connection connection;
	
	private static Map<String, Database> databases;
	

	static
	{   
		databases = new HashMap<String, Database>();
		
		try {   
			Class.forName ("org.apache.derby.jdbc.EmbeddedDriver").newInstance ();
			
		}
		catch (Exception e) {   
			System.err.println ("\n  Error loading Derby Embedded Driver...\n" + e);
		}
	}

	
	private Database() {
		
	}
	
	public static Database createDatabase(String dburl) {
		
		Database database = new Database();
		database.jdbcURL = dburl;
		database.create();
		return database;
	}

	public static Database getInstance(String name) {
		
		Database database = databases.get(name);
		
		if ( database == null ) {
			
			database = new Database();
			database.jdbcURL = JDBC_URL_PREFIX + name;
			database.name = name;
			database.create();
			
			databases.put(name, database);
		}

		return database;
	}

	private void create() {
	
		try {			
	        InputStreamReader in = new InputStreamReader(this.getClass().getResourceAsStream("schema.sql"));
			BufferedReader reader = new BufferedReader(in);

			StringBuffer buffer = new StringBuffer();        
	        String line = null;
	        
	        while ( (line = reader.readLine()) != null ) {
	        	
	        	buffer.append(line);
	        	buffer.append(System.getProperty("line.separator"));
	        }

	        in.close();
	        reader.close();
	        
//	        System.out.println(buffer.toString());
			
	        String[] statements = buffer.toString().trim().split(";");
	        
	        try{
	            Statement jdbc = getConnection().createStatement();
	             

	            for ( int i = 0; i < statements.length && !( jdbc.execute(statements[i]) ); i++) {
	                //System.out.println("statement "+ i + " completed");
	           }
	        }
	        catch (java.sql.SQLException e) {
	        	
	        	System.err.println(e.getCause().getMessage());
	            System.err.println("SQL Exception " + e.toString());
	            e.printStackTrace();
	            
	        }

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void destroy() {
		
		try {
			databases.remove(name);
			DriverManager.getConnection(jdbcURL + ";drop=true");
		}
		catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {

		try {
			if (connection == null || connection.isClosed() ) {
				
				connection = DriverManager.getConnection(jdbcURL + ";create=true", "user", null);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return connection;
	}
	
	
	public static Set<String> getNames() {
		
		return databases.keySet();
	} 

}
