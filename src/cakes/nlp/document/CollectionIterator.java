package cakes.nlp.document;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cakes.nlp.core.Annotation;
import cakes.nlp.dictionary.MatchReport;

public class CollectionIterator implements MatchReport, Iterator<String> {

	public static final int ACTOR_ID = 13;
	private int index = 0;
	int numdocs;
	private Connection connection;

	private String type = "DICT_ENTRY";
	private List<Annotation> annotations;

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		
		this.connection = connection;
		numdocs = DocumentModel.getDocumentCount(connection);
	}
	
	public CollectionIterator() {

		annotations = new ArrayList<Annotation>();
	}

	public CollectionIterator(Connection connection, String type) {

		this();
		setConnection(connection);
		this.type = type;
	}

	@Override
	public boolean hasNext() {
		return index < numdocs ? true : false;
	}

	@Override
	public String next() {
		String text = DocumentModel.getText(connection, ++index);
		return text;
	}

	@Override
	public void match(String text, int begin, int end, String dictionaryEntry) {
		
    	Annotation annotation = new Annotation();
    	
    	annotation.docid = index;
    	annotation.begin = begin;
    	annotation.end   = end;
    	annotation.surface = text.substring(annotation.begin, annotation.end);
    	annotation.lemma = dictionaryEntry;
    	annotation.type = type;
    	annotation.confidence = (float) 0.288;
    	annotation.actor = ACTOR_ID;
    	
    	annotations.add(annotation);
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	
}
