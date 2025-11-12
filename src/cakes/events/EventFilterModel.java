package cakes.events;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import cakes.nlp.dictionary.DictionaryUtils;
import cakes.nlp.dictionary.DictionaryWrapper;
import cakes.nlp.dictionary.Match;
import cakes.rdf.Labels;
import xslt.BaseFilter;

/**
 * Scan text elements in Events XML for mentions of terms in the supplied dictionary. Add entity elements if found.
 * The dictionary is taken from an RDF model.
 *
 */

public class EventFilterModel extends BaseFilter {
	
	private String text;
	private Map<String, Set<String>> labelMap;
	private Map<String, String> typeMap;
	private Map<String, String> prefMap;
    private DictionaryWrapper wrapper;

	public EventFilterModel() {
		super();
	}

	public void setModel(Model model) throws IOException {
		
        // All labels (preferred and alternate) map to a set of IRI's (different entities may have the same name).
		labelMap = Labels.queryLabelMap(model);

        // Each IRI maps to a type (only one type per IRI)
		typeMap = Labels.queryTypeMap(model);
        
        // Each IRI maps to a preferred label, if specified (only one per IRI)
		prefMap = Labels.queryPreferredNameMap(model);
    	
		// make a dictionary from the labels
    	Set<String> terms = labelMap.keySet();
    	terms.removeAll(typeMap.values());
        wrapper = DictionaryUtils.getWrappedDictionary(labelMap.keySet());
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
	        	
	        	// get the set of entity IRI that correspond with this dictionary hit
	        	Set<String> matchingRdfIds = labelMap.get(dictionaryEntry);
	        	
	        	for ( String id: matchingRdfIds ) {
	        		
	        		String name = prefMap.get(id) == null ? dictionaryEntry : prefMap.get(id);
	        		String type = typeMap.get(id) == null ? "UNKNOWN" : typeMap.get(id);
	        		
					AttributesImpl attr = new AttributesImpl();
					attr.addAttribute(uri, "type", "type", "String", type);
					attr.addAttribute(uri, "surface", "surface", "String", text.substring(match.getBegin(), match.getEnd()));
					super.startElement(uri, "entity", "entity", attr);
					super.characters(name.toCharArray(), 0, name.length());
					super.endElement(uri, "entity", "entity");
	        	}
			}
		}

		super.endElement(uri, localName, qname);
	}

}
