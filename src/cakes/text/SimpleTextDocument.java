package cakes.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleTextDocument implements TextDocument {

	private String text = null;
	private String uri = null;
	private Map<String, Set<String>> dictionaryMap = null;

	@Override
	public String getTextContent() {
		return text;
	}

	@Override
	public String getUri() {
		return uri;
	}

	public SimpleTextDocument(String text, String uri) {
		
		this(text, uri, new HashMap<String, Set<String>>());
	}

	public SimpleTextDocument(String text, String uri, Map<String, Set<String>> dictionaryMap) {
		
		this.text = text;
		this.uri = uri;
		this.dictionaryMap = dictionaryMap;
	}
	
	@Override
	public Map<String, Set<String>> getDictionaryMap() {
		return dictionaryMap;
	}
	
}
