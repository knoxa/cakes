package cakes.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cakes.category.Maps;
import cakes.nlp.dictionary.DictionaryUtils;
import cakes.nlp.dictionary.DictionaryWrapper;
import cakes.nlp.dictionary.MatchReport;

public class Edit {

	public static Map<String,String> removeWords(Set<String> texts, List<String> toRemove) {
		
		// Takes a set of strings as input. Finds any occurrences of tokens in the 'toRemove' list and
		// deletes them. Returns a map of unmodified |-> modified texts
		
		Map<String,String> changes = new HashMap<String,String>();

        DictionaryWrapper wrapper = DictionaryUtils.getWrappedDictionary(toRemove);
        
        Map<String,Set<String>> work = new HashMap<String,Set<String>>();

        DictionaryUtils.applyDictionary(texts.iterator(), wrapper, new MatchReport() {

			@Override
			public void match(String text, int begin, int end, String dictionaryEntry) {
				
				Maps.addMapValue(work, text, text.substring(begin, end));
			}
		});
        
        for ( String text: work.keySet() ) {
        	
        	String editedText = text;
        	
        	for ( String token: work.get(text) ) {
        		
        		editedText = editedText.replaceAll(token, "");
        	}
        	
        	changes.put(text, editedText.replaceAll("\\s+", " ").trim());
        }
        
		return changes;
	}
}
