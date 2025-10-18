package cakes.text;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cakes.category.Maps;

public class Names {

	public static Map<String, Set<String>> getNameTokensContext(Collection<String> names) {
		
		// Convert the set of names to a set of tokens.
		// Returned tokens are lower case, without diacritical marks or funny characters.
		
		HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
		
		for ( String name: names ) {
			
			String work = name.toLowerCase().replaceAll("-|\\.|,", "").replaceAll("æ", "ae").replaceAll("œ", "oe");
			String normalized = Normalizer.normalize(work, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			String[] tokens = normalized.trim().split("\\s+");
			
			for ( String token: tokens ) {
				
				Maps.addMapValue(results, token, name);
			}
		}
		
		return results;
	}

}
