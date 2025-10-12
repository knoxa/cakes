package cakes.nlp.document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class DocumentUtils {
	
	
public static String readIntoString(InputStream input) {
    	
        StringBuffer buffer = new StringBuffer();
        InputStreamReader in; BufferedReader reader;
        
		try {
			in = new InputStreamReader(input, "UTF-8");
			reader = new BufferedReader(in);

	        String line = null;
	        
	        while ( (line = reader.readLine()) != null ) {
	        	
	        	buffer.append(line);
	        	buffer.append(System.getProperty("line.separator"));
	        }

	        reader.close();
		}
		catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		catch (IOException e) {

			e.printStackTrace();
		}

        return buffer.toString().trim();
    }


	public static ResultSet processQuery(String sql, Connection conn) throws IOException {
	
	    ResultSet rs = null;
	    String[] statements = sql.split(";");
	    
	    try{
	        Statement jdbc = conn.createStatement();
	        
	        for ( int i = 0; i < statements.length && !(jdbc.execute(statements[i]) ); i++) {
	            System.out.println("statement "+ i + " completed");
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

	public static void writeResultsToContentHandler(ResultSet results, ContentHandler contentHandler) {
	    
	    try {
	        
	        String digits ="0123456789";
	    	String[] columnNames;
	    	
	        ResultSetMetaData metadata = results.getMetaData();
	        int columnCount = metadata.getColumnCount();
	        
	        columnNames = new String[columnCount];
	        
//	        contentHandler.startDocument();      
			contentHandler.startElement("", "DATA", "DATA", new AttributesImpl());

			contentHandler.startElement("", "HEADINGS", "HEADINGS", new AttributesImpl());
	        
	        for (int i = 1; i <= columnCount; i++) {
	            
	    		contentHandler.startElement("", "HEADING", "HEADING", new AttributesImpl());
	            String colname = metadata.getColumnName(i);
	            colname.replace('\'',' ');
	            colname.trim();
	            
	            if ( digits.indexOf(colname.charAt(0)) > 0 ) {
	            	
	            	// we have a column number rather than name - prepend "COL_"
	            	colname = "COL_" + colname;
	            }
	            
	            columnNames[i-1] = colname;
	            
				char[] chars = colname.toCharArray();
				contentHandler.characters(chars, 0, chars.length);   

	    		contentHandler.endElement("", "HEADING", "HEADING");
	        }

			contentHandler.endElement("", "HEADINGS", "HEADINGS");
	        
	        while ( results.next() ) {
	        
	    		contentHandler.startElement("", "ROW", "ROW", new AttributesImpl());
	            
	            for (int i = 1; i <= columnCount; i++) {
	            	
	        		contentHandler.startElement("", columnNames[i-1], columnNames[i-1], new AttributesImpl());

	                 Object value = results.getObject(i);
	                 String text;
	                
	                if (value != null)  {
	                	
	            		if ( value instanceof Clob ) {
	                		
	            			Clob clob = (Clob) value;
	                    	text = new String (clob.getSubString(1, (int) clob.length()));
	                    }
	            		else if ( value instanceof SQLXML ) {
	            			
	            			text=new String("an XML column");
	            		}
	            		else if (columnNames[i-1].startsWith("XML") ) {
	            			
	            			//int xx = metadata.getColumnType(i);
	            			String zz = "type: " + java.sql.Types.BINARY + " = " + metadata.getColumnType(i) + "  -  " + value.getClass().getName();
	            			text=new String(zz);
	            		}
	                	else text = value.toString();
	            		
	        			char[] chars = text.toCharArray();
	        			contentHandler.characters(chars, 0, chars.length);   
	                }
	                
	        		contentHandler.endElement("", columnNames[i-1], columnNames[i-1]);
	            }
	            
	    		contentHandler.endElement("", "ROW", "ROW");
	        }
	        
			contentHandler.endElement("", "DATA", "DATA");
//	        contentHandler.endDocument();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    catch (SAXException e) {
			e.printStackTrace();
		}

	}
	
}
