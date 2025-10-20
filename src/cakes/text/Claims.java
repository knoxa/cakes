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
	 *  There is no formal CE - just try and make the best of simple sentences...
	 */
	
	private static final Pattern be = Pattern.compile("\\s+(is|are)\\s+?");

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
	

	public static Map<String, Set<String>> placeIn(Collection<String> phrases) {

		/*
		 * Parse phrases that link places to a containing region.
		 * Return phrase |-> region
		 * 
		 * Matches: "a place in ...", "places in ..." and "in ..."
		 * 
		 */
    	
		Map<String, Set<String>> results = new HashMap<String, Set<String>>();
		Pattern pattern = Pattern.compile("(?i)^((a\\s+place|places)\\s+)?in\\s+");

		for ( String phrase: phrases ) {
			
			Matcher matcher = pattern.matcher(phrase);
			
			if (matcher.find() ) {
				
				Maps.addMapValue(results, phrase, phrase.substring(matcher.end()).trim());
			}
		}
		
		return results;
	}
	

	public static Map<String, Set<String>> thingsThatAre(Collection<String> claims) {

		/*
		 * Parse sentences that contain "is a" or "are" and assume that one or more entities precede it.
		 * Return claim |-> set of entities
		 * 
		 * Claims should be single sentences.
		 * 
		 */
    	
		Map<String, Set<String>> results = new HashMap<String, Set<String>>();

		for ( String claim: claims ) {
			
			Matcher matcher = be.matcher(claim);
			
			if (matcher.find() ) {
				
				Set<String> entities = new HashSet<String>();
				entities.addAll(getEntitiesFromListPhrase(claim.substring(0, matcher.start())));					
				Maps.addMapValues(results,claim, entities);
			}
		}
		
		return results;
	}
	

	public static Map<String, Set<String>> whatThingsAre(Collection<String> claims) {

		/*
		 * Parse sentences that contain "is a" or "are" and assume that what follows it is a description.
		 * Return claim |-> description
		 * 
		 * Claims should be single sentences.
		 * 
		 */
    	
		Map<String, Set<String>> results = new HashMap<String, Set<String>>();

		for ( String claim: claims ) {
			
			Matcher matcher = be.matcher(claim);
			
			if (matcher.find() ) {
				
				Set<String> description = new HashSet<String>();
				description.add(claim.substring(matcher.end()).replaceFirst(".$", ""));					
				Maps.addMapValues(results,claim, description);
			}
		}
		
		return results;
	}
	
	
	public static Set<String> getEntitiesFromListPhrase(String text) {
		
		/*
		 * Split a text list into the listed items.
		 */

		//ignore leading determiner
		String list = text.replaceAll("(?i)^(the|an|a|there|they)\\b", "").trim();
		Set<String> results = new HashSet<String>();
		String[] items = list.split(",\\s*(and\\s+)?|\\s+and\\s+");
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
