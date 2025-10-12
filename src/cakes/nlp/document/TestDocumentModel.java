package cakes.nlp.document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;

import cakes.nlp.core.Span;
import cakes.nlp.core.Token;
import cakes.nlp.parse.POSTagger;
import cakes.nlp.parse.Tokenizer;

public class TestDocumentModel {

	static {
		
		try {   
			Class.forName ("org.apache.derby.jdbc.EmbeddedDriver").newInstance ();
			
		}
		catch (Exception e) {   
			System.err.println ("\nError loading Derby Driver...\n" + e);
		}

	}
	
	public static void main(String[] args) throws URISyntaxException, TransformerConfigurationException, SAXException, IllegalArgumentException, FileNotFoundException {

		String text = DocumentUtils.readIntoString(new FileInputStream("input/input.txt"));
		
		Connection connection = getConnection();
		
		int doc_id = DocumentModel.insertDocument(connection, text, new URI(""));
		System.out.println("doc id: " + doc_id);			

		// Parse text
    	POSTagger tagger = POSTagger.getInstance();
    	List<Span> sentenceSpans = new ArrayList<Span>();
    	
		List<List<Token>> parsedDoc = Tokenizer.tokenize(text, new Locale("en_US"));
				
		for ( List<Token> sentence : parsedDoc ) {
			
			sentenceSpans.add(new Span(sentence.get(0).getBegin(), sentence.get(sentence.size() - 1).getEnd()));
	    	tagger.tag(sentence);
			DocumentModel.insertTokens(connection, doc_id, sentence);		
		}
		
		DocumentModel.insertSentences(connection, doc_id, sentenceSpans);
		
        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler serializer;
        serializer = tf.newTransformerHandler();
        serializer.setResult(new StreamResult(new FileOutputStream("outdb.xml")));
        
 
        List<Token> list = DocumentModel.getTokens(connection, doc_id);
        System.out.println(list.size());
        
        serializer.startDocument();
        DocumentModel.serializeDocument(connection, doc_id, serializer);
        serializer.endDocument();
        
        try {
			connection.close();
		}
        catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static Connection getConnection() {
		
		Connection connection = null;
		
 		try {
			String dburl = "jdbc:derby:C:/temp/db/TESTING";
			connection = DriverManager.getConnection(dburl, "user", "password");
		}
 		catch (SQLException e) {

			e.printStackTrace();
		}
 		
 		return connection;
	}

}
