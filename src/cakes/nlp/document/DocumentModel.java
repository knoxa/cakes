package cakes.nlp.document;

import java.net.URI;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import cakes.nlp.core.Annotation;
import cakes.nlp.core.Lemma;
import cakes.nlp.core.Span;
import cakes.nlp.core.Token;

public class DocumentModel {

	public  static final String XML_NAMESPACE = "http://uk.gov.dstl/baleen/parse";
	
	public static Annotation getSentenceContainingOffset(Connection connection, int doc_id, int char_pos) {
		
		String sql = "SELECT S.DOC_ID, S.SENTENCE_NO, S.\"BEGIN\", S.\"END\", substr(D.CONTENT, S.\"BEGIN\"+1, S.\"END\"-S.\"BEGIN\") as \"TEXT\" FROM SENTENCES as S, DOC_TEXT as D where S.DOC_ID = D.DOC_ID and S.DOC_ID = ? and ? BETWEEN S.\"BEGIN\" and S.\"END\" order by S.\"BEGIN\"";
		
		Annotation sentence = null;
		
		try {
				
			PreparedStatement sentenceStmt = connection.prepareStatement(sql);
			sentenceStmt.setInt(1, doc_id);
			sentenceStmt.setInt(2, char_pos);
			
			ResultSet results = sentenceStmt.executeQuery();
			
			if ( results.next() ) {
				
				Clob clob = results.getClob("TEXT");
				sentence = new Annotation();
				sentence.surface =  new String (clob.getSubString(1, (int) clob.length()));
				sentence.docid = results.getInt("DOC_ID");
				sentence.begin = results.getInt("BEGIN");
				sentence.end = results.getInt("END");
				sentence.type = "sentence";
			}
			
			sentenceStmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		if  ( sentence == null )  System.err.println("No sentence covers: doc_d = " + doc_id + ", char_pos=" + char_pos);
				
		return sentence;
	}
	
	public static List<Annotation> getSentencesCoveringSpan(Connection connection, int doc_id, Span span) {
		
		String sql = "SELECT S.DOC_ID, S.SENTENCE_NO, S.\"BEGIN\", S.\"END\", substr(D.CONTENT, S.\"BEGIN\"+1, S.\"END\"-S.\"BEGIN\") as \"TEXT\" FROM SENTENCES as S, DOC_TEXT as D where S.DOC_ID = D.DOC_ID and S.DOC_ID = ? and S.\"BEGIN\" >= ? and S.\"END\" <= ? order by S.\"BEGIN\"";
		
		Annotation firstSentence = getSentenceContainingOffset(connection, doc_id, span.getBegin());
		Annotation lastSentence  = getSentenceContainingOffset(connection, doc_id, span.getEnd());

		List<Annotation> sentences = new ArrayList<Annotation>();
		
		try {
				
			PreparedStatement sentenceStmt = connection.prepareStatement(sql);
			sentenceStmt.setInt(1, doc_id);
			sentenceStmt.setInt(2, firstSentence.begin);
			sentenceStmt.setInt(3, lastSentence.end);
			
			ResultSet results = sentenceStmt.executeQuery();
			
			while ( results.next() ) {
				
				Clob clob = results.getClob("TEXT");
				Annotation sentence = new Annotation();
				sentence.surface =  new String (clob.getSubString(1, (int) clob.length()));
				sentence.docid = results.getInt("DOC_ID");
				sentence.begin = results.getInt("BEGIN");
				sentence.end = results.getInt("END");
				sentence.type = "sentence";
				sentences.add(sentence);
			}
			
			sentenceStmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
				
		return sentences;
	}
	
	public static List<Annotation> getSentencesCoveredBySpan(Connection connection, int doc_id, Span span) {
		
		String sql = "SELECT D.URI, S.DOC_ID, S.SENTENCE_NO, S.\"BEGIN\", S.\"END\", substr(D.CONTENT, S.\"BEGIN\"+1, S.\"END\"-S.\"BEGIN\") as \"TEXT\" FROM SENTENCES as S, DOC_TEXT as D where S.DOC_ID = D.DOC_ID and S.DOC_ID = ? and (S.\"BEGIN\" between ? and ? or S.\"END\" between ? and ?) order by S.\"BEGIN\"";
		
		List<Annotation> sentences = new ArrayList<Annotation>();
		
		try {
				
			PreparedStatement sentenceStmt = connection.prepareStatement(sql);
			sentenceStmt.setInt(1, doc_id);
			sentenceStmt.setInt(2, span.getBegin());
			sentenceStmt.setInt(3, span.getEnd());
			sentenceStmt.setInt(4, span.getBegin());
			sentenceStmt.setInt(5, span.getEnd());
			
			ResultSet results = sentenceStmt.executeQuery();
			
			while ( results.next() ) {
				
				Clob clob = results.getClob("TEXT");
				Annotation sentence = new Annotation();
				sentence.surface =  new String (clob.getSubString(1, (int) clob.length()));
				sentence.docid = results.getInt("DOC_ID");
				sentence.begin = results.getInt("BEGIN");
				sentence.end = results.getInt("END");
				sentence.uri = results.getString("URI");
				sentence.type = "sentence";
				
				sentences.add(sentence);
			}
			
			sentenceStmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
				
		return sentences;
	}

	
	public static List<Annotation> getDocumentSentences(Connection connection, int doc_id) {
		
		String sql = "SELECT S.DOC_ID, S.SENTENCE_NO, S.\"BEGIN\", S.\"END\", substr(D.CONTENT, S.\"BEGIN\"+1, S.\"END\"-S.\"BEGIN\") as \"TEXT\" FROM SENTENCES as S, DOC_TEXT as D where S.DOC_ID = D.DOC_ID and S.DOC_ID = ?";
		
		List<Annotation> sentences = new ArrayList<Annotation>();
		
		try {
				
			PreparedStatement docSentenceStmt = connection.prepareStatement(sql);
			
			docSentenceStmt.setInt(1, doc_id);
			
			ResultSet results = docSentenceStmt.executeQuery();
			
			while ( results.next() ) {
				
				Annotation sentence = new Annotation();
				
				Clob clob = results.getClob("TEXT");
				sentence = new Annotation();
				sentence.surface =  new String (clob.getSubString(1, (int) clob.length()));
				sentence.docid = results.getInt("DOC_ID");
				sentence.begin = results.getInt("BEGIN");
				sentence.end = results.getInt("END");
				sentence.type = "sentence";
				
				sentences.add(sentence);
			}
			
			docSentenceStmt.close();			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
				
		return sentences;
	}
	
	
	public static int updateAnnotation(Connection connection, Annotation annotation) {
		
		String sql = "update ANNOTATIONS set lemma=?, confidence = ? where DOC_ID = ? and \"BEGIN\" = ? and \"END\" = ? and TYPE = ?";

		int rows = 0;
		
		try {
			
			PreparedStatement updateAnnotationStmt = connection.prepareStatement(sql);
			
			updateAnnotationStmt.setString(1, annotation.lemma);
			updateAnnotationStmt.setFloat(2, annotation.confidence);
			updateAnnotationStmt.setInt(3, annotation.docid);
			updateAnnotationStmt.setInt(4, annotation.begin);
			updateAnnotationStmt.setInt(5, annotation.end);
			updateAnnotationStmt.setString(6, annotation.type);
			
			rows = updateAnnotationStmt.executeUpdate();
			updateAnnotationStmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
				
		return rows;
	}
	
	
	
	
	public static int insertAnnotations(Connection connection, Collection<Annotation> annotations) {
		
		String sql = "insert into ANNOTATIONS (SURFACE, TYPE, LEMMA, DOC_ID, \"BEGIN\", \"END\", CONFIDENCE, ACTOR) values(?, ?, ?, ?, ?, ?, ?, ?)";

		int rows = 0;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			for (Annotation annotation: annotations ) {

	        	try {				
					stmt.setString(1, annotation.surface);
					stmt.setString(2, annotation.type);
					stmt.setString(3, annotation.lemma);
					stmt.setInt(4, annotation.docid);
					stmt.setInt(5, annotation.begin);
					stmt.setInt(6, annotation.end);
					stmt.setFloat(7, annotation.confidence);
					stmt.setInt(8, annotation.actor);

					rows += stmt.executeUpdate();
				}
				catch (SQLException e) {
					
					if ( e.getSQLState().equals("23505") ) {
						
						System.err.println("a duplicate annotation: " + annotation);
					}
					else {
						
						System.err.println("message: " + e.getMessage());
					}
				}
			}
		}
		catch (SQLException e1) {
			System.err.println("message: " + e1.getMessage());
		}

				
		return rows;
	}

	
	public static int insertIdentities(Connection connection, Collection<Annotation> annotations) {
		
		String sql = "insert into \"IDENTITY\" (DOC_ID, \"BEGIN\", \"END\", URI) values(?, ?, ?, ?)";

		int rows = 0;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			for (Annotation annotation: annotations ) {

				if ( annotation.uri != null ) {
					
		        	try {				
						stmt.setInt(1, annotation.docid);
						stmt.setInt(2, annotation.begin);
						stmt.setInt(3, annotation.end);
						stmt.setString(4, annotation.uri);

						rows += stmt.executeUpdate();
					}
					catch (SQLException e) {
						
						if ( e.getSQLState().equals("23505") ) {
							
							System.err.println("a duplicate annotation: " + annotation);
						}
						else {
							
							System.err.println("message: " + e.getMessage());
						}
					}
				}
			}
		}
		catch (SQLException e1) {
			System.err.println("message: " + e1.getMessage());
		}

				
		return rows;
	}

	
	public static List<Annotation> getAnnotations(Connection connection, String surface) {
		
		String sql = "select DOC_ID, \"BEGIN\", \"END\", SURFACE from ANNOTATIONS where SURFACE = ?";
		
		List<Annotation> annotations = new ArrayList<Annotation>();

		try {
			
			PreparedStatement getAnnotationStmt = connection.prepareStatement(sql);
			
			getAnnotationStmt.setString(1, surface);
			ResultSet results = getAnnotationStmt.executeQuery();
			
			while ( results.next() ) {
				
				Annotation annotation = new Annotation();
				annotation.surface =  results.getString("SURFACE");
				annotation.docid = results.getInt("DOC_ID");
				annotation.begin = results.getInt("BEGIN");
				annotation.end = results.getInt("END");
				
				annotations.add(annotation);
			}
			
			getAnnotationStmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return annotations;
	}
	
	public static List<Annotation> getAnnotationsWithin(Connection connection, int doc_id, int begin, int end) {
		
		String sql = "select DOC_ID, \"BEGIN\", \"END\", TYPE, LEMMA, SURFACE, CONFIDENCE from ANNOTATIONS where DOC_ID = ? and \"BEGIN\" >= ? and \"END\" <= ? order by \"BEGIN\" asc, \"END\" desc";
		
		List<Annotation> annotations = new ArrayList<Annotation>();

		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, doc_id);
			stmt.setInt(2, begin);
			stmt.setInt(3, end);
			ResultSet results = stmt.executeQuery();
			
			while ( results.next() ) {
				
				Annotation annotation = new Annotation();
				annotation.surface =  results.getString("SURFACE");
				annotation.docid = results.getInt("DOC_ID");
				annotation.begin = results.getInt("BEGIN");
				annotation.end = results.getInt("END");
				annotation.type = results.getString("TYPE");
				annotation.lemma = results.getString("LEMMA");
				annotation.confidence = results.getFloat("CONFIDENCE");
				
				annotations.add(annotation);
			}
			
			stmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return annotations;
	}
	
	public static List<Annotation> getDocumentAnnotations(Connection connection, int doc_id, double confidence) {
		
		String sql = "select DOC_ID, \"BEGIN\", \"END\", TYPE, LEMMA, SURFACE, CONFIDENCE from ANNOTATIONS where DOC_ID = ? and CONFIDENCE > ? order by \"BEGIN\" asc, \"END\" desc";
		
		List<Annotation> annotations = new ArrayList<Annotation>();

		try {
			
			PreparedStatement getDocAnnotationTypeStmt = connection.prepareStatement(sql);
			
			getDocAnnotationTypeStmt.setInt(1, doc_id);
			getDocAnnotationTypeStmt.setDouble(2, confidence);
			ResultSet results = getDocAnnotationTypeStmt.executeQuery();
			
			while ( results.next() ) {
				
				Annotation annotation = new Annotation();
				annotation.surface =  results.getString("SURFACE");
				annotation.docid = results.getInt("DOC_ID");
				annotation.begin = results.getInt("BEGIN");
				annotation.end = results.getInt("END");
				annotation.type = results.getString("TYPE");
				annotation.lemma = results.getString("LEMMA");
				annotation.confidence = results.getFloat("CONFIDENCE");
				
				annotations.add(annotation);
			}
			
			getDocAnnotationTypeStmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return annotations;
	}
	
	public static List<Annotation> getDocumentAnnotationsOfType(Connection connection, int doc_id, String type) {
		
		String sql = "select DOC_ID, \"BEGIN\", \"END\", TYPE, LEMMA, SURFACE, CONFIDENCE from ANNOTATIONS where DOC_ID = ? and TYPE = ?";
		
		List<Annotation> annotations = new ArrayList<Annotation>();

		try {
			
			PreparedStatement getDocAnnotationTypeStmt = connection.prepareStatement(sql);
			
			getDocAnnotationTypeStmt.setInt(1, doc_id);
			getDocAnnotationTypeStmt.setString(2, type);
			ResultSet results = getDocAnnotationTypeStmt.executeQuery();
			
			while ( results.next() ) {
				
				Annotation annotation = new Annotation();
				annotation.surface =  results.getString("SURFACE");
				annotation.docid = results.getInt("DOC_ID");
				annotation.begin = results.getInt("BEGIN");
				annotation.end = results.getInt("END");
				annotation.type = results.getString("TYPE");
				annotation.lemma = results.getString("LEMMA");
				annotation.confidence = results.getFloat("CONFIDENCE");
				
				annotations.add(annotation);
			}
			
			getDocAnnotationTypeStmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return annotations;
	}
	
	public static List<Annotation> getAnnotationsLike(Connection connection, String surface) {
		
		String sql = "select DOC_ID, \"BEGIN\", \"END\", SURFACE, TYPE from ANNOTATIONS where SURFACE like ?";
		
		List<Annotation> annotations = new ArrayList<Annotation>();

		try {
			
			PreparedStatement getAnnotationLikeStmt = connection.prepareStatement(sql);
			
			getAnnotationLikeStmt.setString(1, surface);
			ResultSet results = getAnnotationLikeStmt.executeQuery();
			
			while ( results.next() ) {
				
				Annotation annotation = new Annotation();
				annotation.surface =  results.getString("SURFACE");
				annotation.docid = results.getInt("DOC_ID");
				annotation.begin = results.getInt("BEGIN");
				annotation.end = results.getInt("END");
				annotation.type = results.getString("TYPE");
				
				annotations.add(annotation);
			}
			
			getAnnotationLikeStmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return annotations;
	}

	
	public static List<Annotation> getAnnotationsWithLemma(Connection connection, String lemma) {
		
		String sql = "select DOC_ID, \"BEGIN\", \"END\", SURFACE, TYPE from ANNOTATIONS where LEMMA = ?";
		
		List<Annotation> annotations = new ArrayList<Annotation>();

		try {
			
			PreparedStatement getAnnotationLemmaStmt = connection.prepareStatement(sql);
			
			//java.sql.Types.
			getAnnotationLemmaStmt.setString(1, lemma);
			ResultSet results = getAnnotationLemmaStmt.executeQuery();
			
			while ( results.next() ) {
				
				Annotation annotation = new Annotation();
				annotation.surface =  results.getString("SURFACE");
				annotation.docid = results.getInt("DOC_ID");
				annotation.begin = results.getInt("BEGIN");
				annotation.end = results.getInt("END");
				annotation.type = results.getString("TYPE");
				
				annotations.add(annotation);
			}
			
			getAnnotationLemmaStmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return annotations;
	}

	
	public static Annotation getWindowContaining(Connection connection, int doc_id, int begin, int end, int paddingLeft, int paddingRight) {
		
		String sql = "with Y (DOC_ID, SENTENCE_NO, \"BEGIN\", \"END\") as (select T1.DOC_ID, T1.SENTENCE_NO, MIN(T2.\"BEGIN\"), MAX(T2.\"END\") from TOKEN_POS as T1, TOKEN_POS as T2 where T1.DOC_ID = ? and (T1.\"BEGIN\" = ? or T1.\"END\" = ?) and T1.DOC_ID = T2.DOC_ID and T1.SENTENCE_NO=T2.SENTENCE_NO and T2.TOKEN_NO  between (T1.TOKEN_NO - ?) and (T1.TOKEN_NO + ?) group by T1.DOC_ID, T1.SENTENCE_NO) select Y.*, substr(D.CONTENT, Y.\"BEGIN\"+1, Y.\"END\"-Y.\"BEGIN\") as SURFACE from DOCUMENT_FACT as D, Y where D.ID = Y.DOC_ID";
		
		Annotation window = null;

		try {
				
			PreparedStatement windowStmt = connection.prepareStatement(sql);
			
			windowStmt.setInt(1, doc_id);
			windowStmt.setInt(2, begin);
			windowStmt.setInt(3, end);
			windowStmt.setInt(4, paddingLeft);
			windowStmt.setInt(5, paddingRight);
			
			ResultSet results = windowStmt.executeQuery();
			
			if ( results.next() ) {
				
				Clob clob = results.getClob("SURFACE");
				window = new Annotation();
				window.surface =  new String (clob.getSubString(1, (int) clob.length()));
				window.docid = results.getInt("DOC_ID");
				window.begin = results.getInt("BEGIN");
				window.end = results.getInt("END");
			}
			
			windowStmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
				
		return window;
	}
	

	public static Annotation getSpanWithNextAnnotation(Connection connection, int doc_id, int begin, int end) {
		
		String sql = "select Y.*, substr(D.CONTENT, ?, Y.\"END\"-?) as SURFACE from DOC_TEXT as D, (SELECT DOC_ID, MIN(\"END\") as \"END\" FROM ANNOTATIONS where DOC_ID = ? and \"BEGIN\" > ? group by DOC_ID) as Y";
		
		Annotation window = null;
		
		try {
				
			PreparedStatement toNextAnnotationStmt = connection.prepareStatement(sql);
			
			toNextAnnotationStmt.setInt(3, doc_id);
			toNextAnnotationStmt.setInt(4, end);
			toNextAnnotationStmt.setInt(1, begin+1);
			toNextAnnotationStmt.setInt(2, begin);
			
			ResultSet results = toNextAnnotationStmt.executeQuery();
			
			if ( results.next() ) {
				
				Clob clob = results.getClob("SURFACE");
				window = new Annotation();
				window.surface =  new String (clob.getSubString(1, (int) clob.length()));
				window.docid = results.getInt("DOC_ID");
				window.begin = begin;
				window.end = results.getInt("END");
			}
			
			toNextAnnotationStmt.close();		
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
				
		return window;
	}
	
	public static Annotation getNextAnnotation(Connection connection, int doc_id, int offset) {
		
		// return a list?
		
		String sql = "select A.* from ANNOTATIONS as A, (SELECT DOC_ID, MIN(\"BEGIN\") as \"BEGIN\", MIN(\"END\") as \"END\" FROM ANNOTATIONS where DOC_ID = ? and \"BEGIN\" > ? GROUP BY DOC_ID) as Y where A.DOC_ID = Y.DOC_ID and A.\"BEGIN\" = Y.\"BEGIN\" and A.\"END\" = Y.\"END\"";
		
		Annotation window = null;
		
		try {
				
			PreparedStatement toNextAnnotationStmt = connection.prepareStatement(sql);
			
			toNextAnnotationStmt.setInt(1, doc_id);
			toNextAnnotationStmt.setInt(2, offset);
			
			ResultSet results = toNextAnnotationStmt.executeQuery();
			
			if ( results.next() ) {
				
				window = new Annotation();
				window.surface = results.getString("SURFACE");
				window.docid = results.getInt("DOC_ID");
				window.begin = results.getInt("BEGIN");
				window.end = results.getInt("END");
				window.type = results.getString("TYPE");
				window.lemma = results.getString("LEMMA");
				window.confidence = results.getFloat("CONFIDENCE");
			}
			
			toNextAnnotationStmt.close();		
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
				
		return window;
	}

	
	public static int insertTokens(Connection connection, int docid, List<Token> tokens) {
		
		return insertTokens(connection, docid, tokens, 0);
	}
	

	public static int insertTokens(Connection connection, int docid, List<Token> tokens, int offset) {
		
		String sql = "insert into PARSED_TEXT (DOC_ID, SURFACE, LEMMA, \"BEGIN\", \"END\", POS, PENN_TAG) values(?, ?, ?, ?, ?, ?, ?)";

		int rows = 0;
		
		try {
			
			PreparedStatement insertTokenStmt = connection.prepareStatement(sql);
			
			for (Token token: tokens) {
				
				insertTokenStmt.setInt(1, docid);
				insertTokenStmt.setString(2, token.getSurface());
				insertTokenStmt.setString(3, token.getLemmas().isEmpty() ? null : token.getLemmas().get(0).getLemmaForm());
				insertTokenStmt.setInt(4, token.getBegin() + offset);
				insertTokenStmt.setInt(5, token.getEnd() + offset);
				insertTokenStmt.setString(6, token.getLemmas().get(0).getType());
				insertTokenStmt.setString(7, token.getLemmas().get(0).getPennTag());
				
				rows = insertTokenStmt.executeUpdate();
			}
			
			insertTokenStmt.close();
		}
		catch (SQLException e) {
			
			System.err.println("message: " + e.getMessage());
			
			for (Token token: tokens) {
				
				System.err.println(token);
			}
		}
				
		return rows;
	}
	

	public static int deleteAllTokens(Connection connection, int docid) {
		
		String sql = "delete from PARSED_TEXT where DOC_ID = ?";

		int rows = 0;
		
		try {
			
			PreparedStatement deleteTokensStmt = connection.prepareStatement(sql);
			
			deleteTokensStmt.setInt(1, docid);
			
			rows = deleteTokensStmt.executeUpdate();
			deleteTokensStmt.close();
		}
		catch (SQLException e) {
			
			System.err.println("message: " + e.getMessage());
		}
				
		return rows;
	}


	public static int deleteAllSentences(Connection connection, int docid) {
		
		String sql = "delete from SENTENCES where DOC_ID = ?";

		int rows = 0;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, docid);
			
			rows = stmt.executeUpdate();
			stmt.close();
			
		}
		catch (SQLException e) {
			
			System.err.println("message: " + e.getMessage());
		}
				
		return rows;
	}


	public static int deleteAllAnnotations(Connection connection, int docid) {
		
		String sql = "delete from ANNOTATIONS where DOC_ID = ?";

		int rows = 0;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, docid);
			
			rows = stmt.executeUpdate();
			stmt.close();
			
		}
		catch (SQLException e) {
			
			System.err.println("message: " + e.getMessage());
		}
				
		return rows;
	}


	public static int insertDocument(Connection connection, String text, URI uri) {
		
		int doc_id = -1;
		
		String sql = "insert into DOC_TEXT (CONTENT, URI) values(?, ?)";
		
		try {
			
			PreparedStatement insertDocumentStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			insertDocumentStmt.setString(1, text);
			insertDocumentStmt.setString(2, uri.toString());
			
            if (insertDocumentStmt.executeUpdate() != 1) {
                System.err.println("Document text not inserted.");
            }
            else {
            	ResultSet results = insertDocumentStmt.getGeneratedKeys();

        		if ( results.next() ) {
        			doc_id = results.getInt(1);
        		}
            }
            
            insertDocumentStmt.close();
		}
		catch (SQLException e) {
			
			System.err.println("message: " + e.getMessage());
		}
				
		return doc_id;
	}

	
	public static int updateDocument(Connection connection, int doc_id, String text) {
		
		String sql = "update DOC_TEXT set CONTENT = ? where DOC_ID = ?";
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setString(1, text);
			stmt.setInt(2, doc_id);
			
			stmt.executeUpdate();
			stmt.close();
		}
		catch (SQLException e) {
			
			System.err.println("message: " + e.getMessage());
		}
				
		return doc_id;
	}

	
	public static int deleteCoveredTokens(Connection connection, int docid) {
		
		String sql = "delete from PARSED_TEXT as A where exists (select B.* from PARSED_TEXT as B where A.DOC_ID = B.DOC_ID and A.DOC_ID = ? and ((A.BEGIN >= B.BEGIN and A.\"END\" < B.\"END\") or (A.BEGIN > B.BEGIN and A.\"END\" <= B.\"END\") ))";

		int rows = 0;
		
		try {
			
			PreparedStatement deleteCoveredTokensStmt = connection.prepareStatement(sql);
			deleteCoveredTokensStmt.setInt(1, docid);
			rows = deleteCoveredTokensStmt.executeUpdate();
			deleteCoveredTokensStmt.close();
		}
		catch (SQLException e) {
			
			System.err.println("message: " + e.getMessage());
		}
				
		return rows;
	}
	
	public static int insertSentences(Connection connection, int doc_id, List<Span> sentences) {
		
		String sql = "insert into SENTENCES (DOC_ID, \"BEGIN\", \"END\", SENTENCE_NO) values(?, ?, ?, ?)";

		int rows = 0; int sentenceNum = 0;
		
		try {
			
			PreparedStatement insertSentenceStmt = connection.prepareStatement(sql);
			
			for (Span sentence: sentences) {
				
				insertSentenceStmt.setInt(1, Integer.valueOf(doc_id));
				insertSentenceStmt.setInt(2, sentence.getBegin());
				insertSentenceStmt.setInt(3, sentence.getEnd());
				insertSentenceStmt.setInt(4, ++sentenceNum);
				
				rows = insertSentenceStmt.executeUpdate();
			}
			
			insertSentenceStmt.close();
		}
		catch (SQLException e) {
			
			System.err.println("message: " + e.getMessage());
		}
				
		return rows;
	}
	
	
	public static List<Token> getTokens(Connection connection, int doc_id, int begin, int end) {
		
		String sql = "select \"BEGIN\", \"END\", SURFACE, LEMMA, POS, PENN_TAG from PARSED_TEXT where DOC_ID = ? and \"BEGIN\" >= ? and \"END\" <= ? order by \"BEGIN\" asc, \"END\" desc";
		
		List<Token> tokens = new ArrayList<Token>();

		try {
			
			PreparedStatement getTokensStmt = connection.prepareStatement(sql);
			
			getTokensStmt.setInt(1, doc_id);
			getTokensStmt.setInt(2, begin);
			getTokensStmt.setInt(3, end);
			ResultSet results = getTokensStmt.executeQuery();
			
			while ( results.next() ) {
				
				Token token = new Token(results.getInt("BEGIN"), results.getInt("END"));
				token.setSurface(results.getString("SURFACE"));
				
				Lemma lemma = new Lemma();
				lemma.setLemmaForm(results.getString("LEMMA"));
				lemma.setPennTag(results.getString("PENN_TAG"));
				lemma.setType(results.getString("POS"));
				
				token.getLemmas().add(lemma);
				
				tokens.add(token);
			}
			
			getTokensStmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return tokens;
	}


	public static List<Token> getTokens(Connection connection, int doc_id) {
		
		String sql = "select \"BEGIN\", \"END\", SURFACE, LEMMA, POS, PENN_TAG from PARSED_TEXT where DOC_ID = ? order by \"BEGIN\" asc, \"END\" desc";
		
		List<Token> tokens = new ArrayList<Token>();

		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, doc_id);
			ResultSet results = stmt.executeQuery();
			
			while ( results.next() ) {
				
				Token token = new Token(results.getInt("BEGIN"), results.getInt("END"));
				token.setSurface(results.getString("SURFACE"));
				
				Lemma lemma = new Lemma();
				lemma.setLemmaForm(results.getString("LEMMA"));
				lemma.setPennTag(results.getString("PENN_TAG"));
				lemma.setType(results.getString("POS"));

				token.getLemmas().add(lemma);
				tokens.add(token);
			}
			
			stmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return tokens;
	}

	
	public static String getText(Connection connection, int doc_id) {
		
		String sql = "select CONTENT from DOC_TEXT where DOC_ID = ?";
		String docText = null;
		
		try {
			
			PreparedStatement getDocTextStmt = connection.prepareStatement(sql);
			
			getDocTextStmt.setInt(1, doc_id);
			ResultSet results = getDocTextStmt.executeQuery();
			
			if ( results.next() ) {
				
				Clob clob = results.getClob("CONTENT");
				docText =  new String (clob.getSubString(1, (int) clob.length()));
			}
			
			getDocTextStmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return docText;
	}

	
	public static String getIdentity(Connection connection, int doc_id, int begin, int end) {
		
		String sql = "select URI from \"IDENTITY\" where DOC_ID = ? and \"BEGIN\" = ? and \"END\" = ?";
		String result = null;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, doc_id);
			stmt.setInt(2, begin);
			stmt.setInt(3, end);
			ResultSet results = stmt.executeQuery();
			
			if ( results.next() ) {
				
				result = results.getString("URI");
			}
			
			stmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return result;
	}

	
	public static int getDocID(Connection connection, URI uri) {
		
		String sql = "select DOC_ID from DOC_TEXT where URI = ?";
		int doc_id = -1;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setString(1, uri.toString());
			ResultSet results = stmt.executeQuery();
			
			if ( results.next() ) {
				
				doc_id = results.getInt("DOC_ID");
			}
			
			stmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return doc_id;
	}

	
	public static String getDocURI(Connection connection, int docid) {
		
		String sql = "select URI from DOC_TEXT where DOC_ID = ?";
		String uri = null;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, docid);
			ResultSet results = stmt.executeQuery();
			
			if ( results.next() ) {
				
				uri = results.getString("URI");
			}
			
			stmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return uri;
	}

	
	public static int getDocumentCount(Connection connection) {
		
		String sql = "select count(DOC_ID) from DOC_TEXT";
		int count = -1;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			ResultSet results = stmt.executeQuery();
			
			if ( results.next() ) {
				
				count = results.getInt(1);
			}
			
			stmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return count;
	}

	
	public static int updateTokens(Connection connection, List<Token> tokens) {
		
		String sql = "update PARSED_TEXT set POS = ?, PENN_TAG=?, LEMMA=? where DOC_ID = ? and \"BEGIN\" = ? and \"END\" = ?";

		try {
			
			//if (updateTokensStmt == null)  updateTokensStmt = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			PreparedStatement stmt = connection.prepareStatement(sql);
			connection.setAutoCommit(false);
			
			for (Token token: tokens) {
				
				stmt.setString(1, token.getLemmas().get(0).getType());
				stmt.setString(2, token.getLemmas().get(0).getPennTag());
				stmt.setString(3, token.getLemmas().isEmpty() ? null : token.getLemmas().get(0).getLemmaForm());
				stmt.setInt(5, token.getBegin());
				stmt.setInt(6, token.getEnd());
				
				stmt.addBatch();
			}
			stmt.executeBatch();
			
			connection.commit();
			connection.setAutoCommit(true);
			
			stmt.close();
		}
		catch (SQLException e) {

			e.printStackTrace();	
			
			if (connection != null) {
				try { connection.rollback(); } catch (SQLException e1) { e1.printStackTrace(); } 
			}
		}
				
		return 0;
	}

	
	/**
	 * 
	 * @param connection
	 * @param doc_id
	 * @param ch
	 * @throws SAXException
	 */
	public static void serializeDocument(Connection connection, int doc_id, ContentHandler ch) throws SAXException {
		
		AttributesImpl docAttr = new AttributesImpl();
		//docAttr.addAttribute(NamespaceSupport.XMLNS, "xmlns",  "xmlns",  "CDATA", XML_NAMESPACE);
		String uri = getDocURI(connection, doc_id);
//		docAttr.addAttribute("", "uri",  "uri", "CDATA", String.valueOf(doc_id));
		docAttr.addAttribute("", "uri",  "uri", "CDATA", uri);

		String text = DocumentModel.getText(connection, doc_id);
		
		ch.startPrefixMapping("", XML_NAMESPACE);
		ch.startElement(XML_NAMESPACE, "document", "document", docAttr);
		
        ch.startElement("", "text", "text", new AttributesImpl());
        if (text != null) ch.characters(text.toCharArray(), 0, text.length());
        ch.endElement("", "text", "text");
		
		List<Annotation> sentences = DocumentModel.getDocumentSentences(connection, doc_id);
		
		for (Annotation sentence: sentences ) {
			
			AttributesImpl sentenceAttr = new AttributesImpl();
			sentenceAttr.addAttribute("", "begin",  "begin",  "CDATA", String.valueOf(sentence.begin));
			sentenceAttr.addAttribute("", "end",  "end",  "CDATA", String.valueOf(sentence.end));

	        ch.startElement("", "sentence", "sentence", sentenceAttr);

			List<Token> tokens = DocumentModel.getTokens(connection, doc_id, sentence.begin, sentence.end);
			
	    	int tokenIndex = 0;

	    	for (Token token : tokens ) {
				
				token.serializeToken(ch, 0, tokenIndex++);
			}
	    	
	    	List<Annotation> annotations = DocumentModel.getAnnotationsWithin(connection, doc_id, sentence.begin, sentence.end);
			//annotations.sort(new AnnotationComparator()); // sorted in SQL that gets annotations from database
			
	    	for (Annotation annotation : annotations ) {

	    		String ident = DocumentModel.getIdentity(connection, doc_id, annotation.begin, annotation.end);
	    		if ( ident != null ) annotation.uri = ident;
				Annotation.serializeAnnotation(ch, annotation, 0);
			}
	    	
	        ch.endElement("", "sentence", "sentence");
		}
		
		ch.endPrefixMapping("");
		ch.endElement("", "document", "document");
	}

	
	public static void serializeSentenceAsDocument(Connection connection, Span span, int doc_id, String uri, ContentHandler ch) throws SAXException {
		
		AttributesImpl docAttr = new AttributesImpl();
		docAttr.addAttribute("", "docid",  "docid",  "Integer", String.valueOf(doc_id));
		docAttr.addAttribute("", "uri",  "uri",  "String", uri + "#" + span.getBegin() + "-" + span.getEnd());

		String text = DocumentModel.getText(connection, doc_id);
		String excerptText = text.substring(span.getBegin(), span.getEnd());
		
		ch.startPrefixMapping("", XML_NAMESPACE);
		ch.startElement(Token.XML_NAMESPACE, "document", "document", docAttr);
        ch.startElement("", "text", "text", new AttributesImpl());
        if (excerptText != null) ch.characters(excerptText.toCharArray(), 0, excerptText.length());
        ch.endElement("", "text", "text");
		
		AttributesImpl sentenceAttr = new AttributesImpl();
		sentenceAttr.addAttribute("", "begin", "begin", "Integer", String.valueOf(0));
		sentenceAttr.addAttribute("", "end", "end", "Integer", String.valueOf(span.getEnd() - span.getBegin()));

        ch.startElement("", "sentence", "sentence", sentenceAttr);

		List<Token> tokens = DocumentModel.getTokens(connection, doc_id, span.getBegin(), span.getEnd());
		
    	int tokenIndex = 0;

    	for ( Token token : tokens ) {
			
			token.serializeToken(ch, -span.getBegin(), tokenIndex++);
			//Token.serializeToken(ch, token, 0, tokenIndex++);
		}
    	
    	List<Annotation> annotations = DocumentModel.getAnnotationsWithin(connection, doc_id, span.getBegin(), span.getEnd());
		
    	for (Annotation annotation : annotations ) {
			
			Annotation.serializeAnnotation(ch, annotation, -span.getBegin());
		}

        ch.endElement("", "sentence", "sentence");
		
		ch.endPrefixMapping("");
		ch.endElement(Token.XML_NAMESPACE, "document", "document");
	}

	
	public static void serializeCoveringSentencesAsDocument(Connection connection, Span span, int doc_id, String uri, ContentHandler ch) throws SAXException {
		
		List<Annotation> sentences = getSentencesCoveringSpan(connection, doc_id, span);
		span.setBegin(sentences.get(0).begin);
		span.setEnd(sentences.get(sentences.size()-1).end);
		
		
		AttributesImpl docAttr = new AttributesImpl();
		docAttr.addAttribute("", "docid",  "docid",  "Integer", String.valueOf(doc_id));
		docAttr.addAttribute("", "begin",  "begin",  "Integer", String.valueOf(span.getBegin()));
		docAttr.addAttribute("", "end",  "end",  "Integer", String.valueOf(span.getEnd()));
		docAttr.addAttribute("", "uri",  "uri",  "String", uri + "#" + span.getBegin() + "-" + span.getEnd());

		String text = DocumentModel.getText(connection, doc_id);
		String excerptText = text.substring(span.getBegin(), span.getEnd());
		
		ch.startPrefixMapping("", XML_NAMESPACE);
		ch.startElement(Token.XML_NAMESPACE, "document", "document", docAttr);
        ch.startElement("", "text", "text", new AttributesImpl());
        if (excerptText != null) ch.characters(excerptText.toCharArray(), 0, excerptText.length());
        ch.endElement("", "text", "text");

		for ( Annotation sentence: sentences ) {
			
			AttributesImpl sentenceAttr = new AttributesImpl();
			sentenceAttr.addAttribute("", "begin", "begin", "Integer", String.valueOf(sentence.begin - span.getBegin()));
			sentenceAttr.addAttribute("", "end", "end", "Integer", String.valueOf(sentence.end - span.getBegin()));
			
	        ch.startElement("", "sentence", "sentence", sentenceAttr);

			List<Token> tokens = DocumentModel.getTokens(connection, doc_id, sentence.begin, sentence.end);
			
	    	int tokenIndex = 0;

	    	for ( Token token : tokens ) {
				
				token.serializeToken(ch, -span.getBegin(), tokenIndex++);
				//Token.serializeToken(ch, token, 0, tokenIndex++);
			}
	    	
	    	List<Annotation> annotations = DocumentModel.getAnnotationsWithin(connection, doc_id, sentence.begin, sentence.end);
			
	    	for (Annotation annotation : annotations ) {
				
				Annotation.serializeAnnotation(ch, annotation, -span.getBegin());
			}

	        ch.endElement("", "sentence", "sentence");
		}
		
		ch.endPrefixMapping("");
		ch.endElement(Token.XML_NAMESPACE, "document", "document");
	}

	
	public static Hits getXYZ(Connection connection, String lemma) {
		
		String sql = "select DOC_ID, count(*) as NUM from PARSED_TEXT where LEMMA = ? group by DOC_ID";
		
		Set<Integer> matches = new HashSet<Integer>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		Hits hits   = new Hits();
		hits.idset  = matches;
		hits.counts = map;

		try {
			
			PreparedStatement scratchStmt = connection.prepareStatement(sql);
			
			scratchStmt.setString(1, lemma);
			ResultSet results = scratchStmt.executeQuery();
			
			while ( results.next() ) {
				
				Integer docid = new Integer(results.getInt("DOC_ID"));
				matches.add(docid);
				map.put(docid, results.getInt("NUM"));
			}
			
			scratchStmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return hits;
	}

	
	
	
	public static Hits getdocSizes(Connection connection) {
		
		String sql = "select DOC_ID, count(*) as NUM from PARSED_TEXT group by DOC_ID";
		
		Set<Integer> matches = new HashSet<Integer>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		Hits hits   = new Hits();
		hits.idset  = matches;
		hits.counts = map;

		try {
			
			PreparedStatement scratchStmt = connection.prepareStatement(sql);
			
			ResultSet results = scratchStmt.executeQuery();
			
			while ( results.next() ) {
				
				Integer docid = new Integer(results.getInt("DOC_ID"));
				matches.add(docid);
				map.put(docid, results.getInt("NUM"));
			}
			
			scratchStmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return hits;
	}

}
