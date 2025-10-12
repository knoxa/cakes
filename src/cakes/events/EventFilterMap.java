package cakes.events;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import cakes.nlp.dictionary.DictionaryUtils;
import cakes.nlp.dictionary.DictionaryWrapper;
import cakes.nlp.dictionary.Match;
import xslt.BaseFilter;

/**
 * Scan text elements in Events XML for mentions of terms in the supplied dictionary. Add entity elements if found.
 * The dictionary is taken from a Map<String, Set<String>>. Each entry key in the map is a label to add to dictionary.
 * The entry value is expected to be a set of size 1 containing the corresponding preferred label. More than one
 * preferred label suggests ambiguity has crept in.
 *
 */

public class EventFilterMap extends BaseFilter {
	
	private String text, type;
	private Map<String, Set<String>> entityMap;
    private DictionaryWrapper wrapper;

	public EventFilterMap() {
		super();
	}

	public void setMap(Map<String, Set<String>> entityMap, String type) throws IOException {
		
		this.entityMap = entityMap;
		this.type = type;
		
		// make a dictionary from all the labels
    	Set<String> terms = new HashSet<String>();
    	terms.addAll(entityMap.keySet());
    	
    	for ( String key: entityMap.keySet() ) {
    		
    		Set<String> prefLabels = entityMap.get(key);
    		terms.addAll(prefLabels);
    		// there should only be one prefLabel though ...
    		if ( prefLabels.size() > 1 ) System.err.println("Warning: The term \"" + key + "\" is ambigous: " + prefLabels );
    	}

    	wrapper = DictionaryUtils.getWrappedDictionary(terms);
	}

	public void setWrapper(DictionaryWrapper wrapper) {		
		this.wrapper = wrapper;
	}

	@Override
	public void endElement(String uri, String localName, String qname) throws SAXException {

		if ( localName.equals("text") )  {
			
			text = this.getText();
		}
		else if ( qname.equals("event") ) {
			
			Set<Match> matches = DictionaryUtils.applyDictionary(text, wrapper);
			
			for ( Match match: matches ) {
				
	        	String dictionaryEntry = match.getDictionaryEntry();
        		String name =  entityMap.get(dictionaryEntry) == null ? dictionaryEntry : entityMap.get(dictionaryEntry).iterator().next();
        		
				AttributesImpl attr = new AttributesImpl();
				attr.addAttribute(uri, "type", "type", "String", type);
				attr.addAttribute(uri, "surface", "surface", "String", text.substring(match.getBegin(), match.getEnd()));
				super.startElement(uri, "entity", "entity", attr);
				super.characters(name.toCharArray(), 0, name.length());
				super.endElement(uri, "entity", "entity");
			}
		}

		super.endElement(uri, localName, qname);
	}

}
