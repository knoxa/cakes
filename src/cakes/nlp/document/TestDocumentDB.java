package cakes.nlp.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import cakes.nlp.core.Annotation;
import cakes.nlp.core.Span;
import cakes.nlp.core.Token;
import cakes.nlp.dictionary.DefaultTokenizer;
import cakes.nlp.dictionary.DictionaryUtils;
import cakes.nlp.dictionary.DictionaryWrapper;
import cakes.nlp.dictionary.MatchReport;
import cakes.nlp.document.Database;
import cakes.nlp.document.DocumentModel;
import cakes.nlp.document.DocumentUtils;
import cakes.nlp.document.CollectionIterator;
//import cakes.nlp.document.test.GraphTable;
import cakes.nlp.parse.POSTagger;
import cakes.nlp.parse.Tokenizer;
import xslt.Pipeline;
import xslt.PipelineBuilder;

public class TestDocumentDB {

	public static Pattern dates = Pattern.compile("(\\d{1,2}(st|nd|rd|th)?\\s+)?(jan|january|feb|february|mar|march|apr|april|may|jun|june|jul|july|aug|august|sep|september|oct|october|nov|november|dec|december)(,?\\s+(\\d{2,4}))?\\b", Pattern.CASE_INSENSITIVE);

	public static void main(String[] args) throws URISyntaxException, SAXException, IllegalArgumentException, IOException, SQLException, TransformerException {

		String text = DocumentUtils.readIntoString(new FileInputStream("input/input.txt"));
		
		Database database = Database.getInstance("TESTING");
		
		int doc_id = DocumentModel.insertDocument(database.getConnection(), text, new URI(""));
		System.out.println("doc id: " + doc_id);			

		// Parse text
    	POSTagger tagger = POSTagger.getInstance();
    	List<Span> sentenceSpans = new ArrayList<Span>();
    	
		List<List<Token>> parsedDoc = Tokenizer.tokenize(text, new Locale("en_US"));
				
		for ( List<Token> sentence : parsedDoc ) {
			
			sentenceSpans.add(new Span(sentence.get(0).getBegin(), sentence.get(sentence.size() - 1).getEnd()));
	    	tagger.tag(sentence);
			DocumentModel.insertTokens(database.getConnection(), doc_id, sentence);		
		}
		
		DocumentModel.insertSentences(database.getConnection(), doc_id, sentenceSpans);
		
		///
		serializeDoc(database.getConnection(), doc_id, new FileOutputStream("out.xml"));
        
		///
		dictionary(database.getConnection(), new File("D:\\GitHub\\muc3\\lists\\person.txt"), "*person");
		dictionary(database.getConnection(), new File("D:\\GitHub\\muc3\\lists\\name-person.txt"), "*name-person");
		dictionary(database.getConnection(), new File("D:\\GitHub\\muc3\\lists\\place.txt"), "*place");
		dictionary(database.getConnection(), new File("D:\\GitHub\\muc3\\lists\\name-place.txt"), "*name-place");
		
		///
		sql(database.getConnection(), "clean.sql");
		sql(database.getConnection(), "graph-schema.sql");
		sql(database.getConnection(), "graph-insert.sql");
//		sql(database.getConnection(), "graph-select.sql");
        
		///
		graph(database.getConnection());
		
		///
		pipeline(database.getConnection(), doc_id, new File("../XSLT-Parsers/pipelines/time-phrases.xml"), new FileOutputStream("outx.xml"));
	}
	
	
	public static void serializeDoc(Connection connection, int docid, OutputStream out) throws TransformerConfigurationException, IllegalArgumentException, SAXException {
		
        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler serializer;
        serializer = tf.newTransformerHandler();
        serializer.setResult(new StreamResult(out));
        
        List<Token> list = DocumentModel.getTokens(connection, docid);
        System.out.println(list.size());
        
        serializer.startDocument();
        DocumentModel.serializeDocument(connection, docid, serializer);
        serializer.endDocument();
	}
	
	
	public static void pipeline(Connection connection, int docid, File pipelineFile, OutputStream out) throws TransformerConfigurationException, SAXException {
		
		PipelineBuilder builder = new PipelineBuilder();				  
		Pipeline pipeline = builder.getPipeline(pipelineFile);
		pipeline.setOutput(out);
		
		ContentHandler ch = pipeline.getContentHandler();
		
		ch.startDocument();
		DocumentModel.serializeDocument(connection, docid, ch);
		ch.endDocument();
	}
	

	public static void dictionary(Connection connection, File file, String name) throws IOException {
		
		List<String> terms = FileUtils.readLines(file, "windows-1252");
		
		cakes.nlp.dictionary.Tokenizer defaultTokenizer = new DefaultTokenizer();
		DictionaryWrapper wrapper = DictionaryUtils.getWrappedDictionary(terms, defaultTokenizer);

		CollectionIterator iterator = new CollectionIterator(connection, name);
		
		DictionaryUtils.applyDictionary(iterator, wrapper, (MatchReport) iterator);
		
		List<Annotation> annotations = iterator.getAnnotations();
		DocumentModel.insertAnnotations(connection, annotations);

	}
	

	public static void dates(Connection connection, String name) throws IOException {
		
		int n = DocumentModel.getDocumentCount(connection);

		for ( int i = 1; i <= n; i++ ) {
			
			List<Annotation> annotations = applyPattern(dates, connection, i, name);
			DocumentModel.insertAnnotations(connection, annotations);
		}
	}

	
	public static void sql(Connection connection, String sqlFile) {
		
		String text = DocumentUtils.readIntoString(TestDocumentDB.class.getResourceAsStream(sqlFile));

        String[] statements = text.trim().split(";");
        
        try{
            Statement jdbc = connection.createStatement();
             
            for ( int i = 0; i < statements.length && !( jdbc.execute(statements[i]) ); i++) {
                System.out.println("statement "+ i + " completed");
                connection.commit();        
           }
        }
        catch (java.sql.SQLException e) {
        	
        	System.err.println(e.getCause().getMessage());
            System.err.println("SQL Exception " + e.toString());
            e.printStackTrace();
            
        }
	}

	
	public static void sql(Connection connection, InputStream sqlStream) {
		
		String text = DocumentUtils.readIntoString(sqlStream);

        String[] statements = text.trim().split(";");
        
        try{
            Statement jdbc = connection.createStatement();
             
            for ( int i = 0; i < statements.length && !( jdbc.execute(statements[i]) ); i++) {
                System.out.println("statement "+ i + " completed");
                connection.commit();        
           }
        }
        catch (java.sql.SQLException e) {
        	
        	System.err.println(e.getCause().getMessage());
            System.err.println("SQL Exception " + e.toString());
            e.printStackTrace();
            
        }
	}
	

	public static void graph(Connection connection) throws SQLException, TransformerException, IOException, SAXException {
		
		GraphTable data = new GraphTable();
		data.getAllGraphs(connection);
	}
	
	public static void adjustSentences(Connection connection) {
		
		sql(connection, "sentence-adjust.sql");
	}
	
	
	public static List<Annotation> applyPattern(Pattern pattern, Connection connection, int docid, String tag) {
		
		List<Annotation> annotations = new ArrayList<Annotation>();

		String text = DocumentModel.getText(connection, docid);		
		Matcher matcher = pattern.matcher(text);

		while ( matcher.find() ) {
			
			if ( matcher.group(0).matches(".*?\\d.*") ) {
				
				Annotation a = new Annotation();
				a.docid = docid;
				a.actor = 321;
				a.begin = matcher.start(0);
				a.end = matcher.end(0);
				//a.surface = DocumentModel.getText(connection, a.docid).substring(a.begin, a.end);
				a.surface = matcher.group(0);
				a.lemma = normalizeDate(a.surface); // !!!!!!!!!!!
				a.type = tag;
				a.confidence = (float) 0.5;
				annotations.add(a);
			}

		}
		
		return annotations;
	}

	public static String normalizeDate(String inputDate) {

		String outputDate = "";
		if (inputDate == null)  return outputDate;
		String txt = inputDate.trim();
		if (txt.length() == 0) return outputDate;

		txt = txt.replaceAll("(\\.|,)\\s*", "\\/");

		SimpleDateFormat f1 = new SimpleDateFormat("d/M/y");
		SimpleDateFormat f2 = new SimpleDateFormat("d MMM y");
		SimpleDateFormat f3 = new SimpleDateFormat("MMM-y");
		SimpleDateFormat f4 = new SimpleDateFormat("y");
		SimpleDateFormat f5 = new SimpleDateFormat("M/y");
		SimpleDateFormat f6 = new SimpleDateFormat("MMM y");

		SimpleDateFormat o1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat o2 = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat o3 = new SimpleDateFormat("yyyy");

		try {

			if (txt.matches("^\\d{4}\\-\\d{2}\\-\\d{2}$")) { // normal form anyway

				outputDate = o1.format(o1.parse(txt));
			}
			else if (txt.matches("^\\w{3,4}\\-\\d+$")) {

				outputDate = o2.format(f3.parse(txt));
			}
			else if (txt.matches("^\\w+\\s\\d+$")) {

				outputDate = o2.format(f6.parse(txt));
			}
			else if (txt.matches("^\\d+\\/\\d+\\/\\d{4}$")) {

				outputDate = o1.format(f1.parse(txt));
			}
			else if (txt.matches("^\\d+\\/\\d+\\/\\d{2}$")) {

				outputDate = o1.format(f1.parse(txt));
			}
			else if (txt.matches("^\\d+\\s\\w+\\s\\d{4}$")) {

				outputDate = o1.format(f2.parse(txt));
			}
			else if (txt.matches("^\\d+\\/\\d{4}$")) {

				outputDate = o2.format(f5.parse(txt));
			}
			else if (txt.matches("^\\d{4}$")) {

				outputDate = o3.format(f4.parse(txt));
			}
			else {
				System.err.println("Can't parse: " + inputDate + "<" + txt + ">");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return outputDate;
	}

}
