package cakes.nlp.document;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseUtils {

    public static ResultSet processQuery(String sql, Connection conn) throws IOException {

        ResultSet rs = null;
        String[] statements = sql.split(";");
        
        try{
        	
            Statement jdbc = conn.createStatement();
           
            for ( int i = 0; i < statements.length && !(jdbc.execute(statements[i]) ); i++) {
            	
               // System.out.println("statement "+ i + " completed");
            }
            
            rs = jdbc.getResultSet();
        }
        catch (java.sql.SQLException e) {
        	
        	System.err.println(e.getCause().getMessage());
            System.err.println("SQL Exception " + e.toString());
            e.printStackTrace();
        }
        
        return rs;
    }

}
