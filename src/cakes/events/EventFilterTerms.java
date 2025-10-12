package cakes.events;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import cakes.nlp.dictionary.DictionaryUtils;
import cakes.nlp.dictionary.DictionaryWrapper;
import cakes.nlp.dictionary.Match;
import xslt.BaseFilter;

/**
 * Scan text elements in Events XML for mentions of terms in the supplied list. Add entity elements if found.
 *
 */

public class EventFilterTerms extends BaseFilter {
	
	private String text, type;
    private DictionaryWrapper wrapper;

	public EventFilterTerms() {
		super();
	}

	public void setTerms(Collection<String> terms, String type) throws IOException {
		
		this.type = type;
		
		// make a dictionary from supplied terms
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
        		
				AttributesImpl attr = new AttributesImpl();
				attr.addAttribute(uri, "type", "type", "String", type);
				//attr.addAttribute(uri, "surface", "surface", "String", text.substring(match.getBegin(), match.getEnd()));
				super.startElement(uri, "entity", "entity", attr);
				super.characters(dictionaryEntry.toCharArray(), 0, dictionaryEntry.length());
				super.endElement(uri, "entity", "entity");
			}
		}

		super.endElement(uri, localName, qname);
	}

}
