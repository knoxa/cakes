package cakes.text;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.language.Soundex;

import cakes.category.Maps;

public class Names {

	static Soundex soundex = new Soundex();

	public static Map<String, Set<String>> getNameTokensContext(Collection<String> names) {
		
		// Convert the set of names to a map of tokens to names that contain them.
		// Returned tokens are lower case.
		// Apostrophes, diacritical marks or funny characters are elided.
		// Full stops, dashes, commas, and underscores are replaced with whitespace to make them token boundaries.
		
		HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
		
		for ( String name: names ) {
			
			String work = name.toLowerCase().replaceAll("'|`","").replaceAll("-|\\.|,|_", " ").replaceAll("æ", "ae").replaceAll("œ", "oe");
			
			//work = work.replaceAll("(\\w)\\w+\\s+", "$1 ");
			//work = work.replaceAll("s|f", "*");
			
			String normalized = Normalizer.normalize(work, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			String[] tokens = normalized.trim().split("\\s+");
			
			for ( String token: tokens ) {
				
				Maps.addMapValue(results, token, name);
			}
		}
		
		return results;
	}

	public static Map<String, Set<String>> modifyTokens(Map<String, Set<String>> tokenMap) {

		HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
		
		for ( String token: tokenMap.keySet() ) {
			
			String tokenNew = token.replaceAll("[aeiou]", "").replaceAll("([a-z])\\1+", "$1");
			Maps.addMapValues(results, tokenNew, tokenMap.get(token));
		}
		
		return results;
	}

	public static Map<String, Set<String>> convertToSoundexTokens(Map<String, Set<String>> tokenMap) {

		HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
		
		for ( String token: tokenMap.keySet() ) {
			
			try {
				
				String sound = soundex.encode(token);	
				Maps.addMapValues(results, sound, tokenMap.get(token));
			}
			catch (IllegalArgumentException e) {
				
				System.err.println("Soundex: " + e.getMessage());
			}

		}
		
		return results;
	}

}
