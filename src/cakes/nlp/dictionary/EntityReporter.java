package cakes.nlp.dictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cakes.nlp.dictionary.MatchReport;

/**
 * Matching mentions of named entities. A mention may match a dictionary entry exactly, or may contain a dictionary entry as a substring.
 * The dictionary may contain both alternate and preferred names for the same entity. 
 *
 */

public class EntityReporter implements MatchReport {

	private Map<String, Set<String>> matches, refers, prefLabels;
	private Set<String> found;
	
	public EntityReporter() {

		prefLabels = new HashMap<String, Set<String>>();
		matches    = new HashMap<String, Set<String>>();
		refers     = new HashMap<String, Set<String>>();
		found      = new HashSet<String>();
	}

	@Override
	public void match(String text, int begin, int end, String dictionaryEntry) {

    	if ( text.length() == dictionaryEntry.length() ) {

    		// The label matches a dictionary entry exactly. Get the corresponding preferred label and 
    		// record it as matching the dictionary term.

    		Set<String> pl = prefLabels.get(dictionaryEntry);
    		String prefLabel = pl == null ? dictionaryEntry : pl.iterator().next();
	    	
	    	// There should be only one preferred name per label.
    		cakes.category.Maps.addFunctionOutput(matches, dictionaryEntry, prefLabel);
	    	
	    	found.add(text);
    	}
    	else {
    		
    		// There is a dictionary match within the label. The same label may hit more than one
    		// dictionary entry. Map the label to the set of hits.
    		
			cakes.category.Maps.addMapValue(refers, text, dictionaryEntry);
    	}
	}

	public Map<String, Set<String>> getMatches() {
		return matches;
	}

	public Map<String, Set<String>> getRefers() {
		return refers;
	}

	public void setPrefLabels(Map<String, Set<String>> prefLabels) {
		this.prefLabels = prefLabels;
	}

	public Set<String> getFound() {
		return found;
	}

}
