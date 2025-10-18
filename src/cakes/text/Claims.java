package cakes.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cakes.category.Maps;
import cakes.nlp.core.Token;
import cakes.nlp.parse.Tokenizer;

public class Claims {
	
	/*
	 *  Exploit the controlled vocabulary of claims to do some light-weight NLP.
	 */

	public static Map<String, Set<String>> equivalentNames(Collection<String> claims) {
		
		// Returns a map claim |-> set of entities, where entities are quoted names in the claim
		
		Map<String, Set<String>> results = new HashMap<String, Set<String>>();
		
		Pattern pattern = Pattern.compile("\\\"(.*?)\\\"");

		for ( String claim: claims ) {
			
			Matcher matcher = pattern.matcher(claim);
			Set<String> names = new HashSet<String>();
			
			while (matcher.find() ) {
				
				names.add(matcher.group(1));
			}
			
			if ( names.size() > 0 ) results.put(claim, names);
		}
		
		return results;
	}
	

	public static Map<String, Set<String>> placeInRegion(Collection<String> claims) {

		/*
		 * Reads facts expressed in "controlled" English (CE). No formal rules for expression of CE - just simple sentence that should be easy
		 * to parse given some context.
		 * 
		 * Parse sentences that link a smaller place to a containing region. Creates a context where the objects are regions and the attributes
		 * are places they contain.
		 * 
		 * Claims should be single sentences.
		 * 
		 */
    	
		Map<String, Set<String>> results = new HashMap<String, Set<String>>();
		Pattern pattern = Pattern.compile("\\s+(is a place in|are places in)\\s+([^\\.]*)\\.");

		for ( String claim: claims ) {
			
			Matcher matcher = pattern.matcher(claim);
			
			if (matcher.find() ) {
				
				Set<String> placeNames = new HashSet<String>();
				placeNames.addAll(getEntitiesFromListPhrase(claim.substring(0, matcher.start())));					
				Maps.addMapValues(results, matcher.group(2), placeNames);
			}
		}
		
		return results;
	}
	
	
	public static Set<String> getEntitiesFromListPhrase(String text) {
		
		/*
		 * Split a text list into the listed items.
		 */
		
		Set<String> results = new HashSet<String>();
		String[] items = text.split(",\\s*|\\sand\\s");
		results.addAll(Arrays.asList(items));
		
		return results;
	}
	
    public static List<String> splitIntoSentences(String text) {
    	
    	/*
    	 * Split text into sentences.
    	 */
    	
    	List<String> results = new ArrayList<String>();    	
		List<List<Token>> parsed = Tokenizer.tokenize(text, new Locale("en_GB"));
    	
		for ( List<Token> sentence : parsed ) {
			
			String sentenceText = text.substring(sentence.get(0).getBegin(), sentence.get(sentence.size() - 1).getEnd());
			results.add(sentenceText);		
		}

    	return results;
    }
	
	
}
