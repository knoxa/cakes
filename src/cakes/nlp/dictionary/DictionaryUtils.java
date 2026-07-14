package cakes.nlp.dictionary;

import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cakes.category.Maps;
import cakes.nlp.core.Span;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.util.StringList;

public class DictionaryUtils {
	
	public static DictionaryWrapper getWrappedDictionary(Collection<String> terms) {
		
	    return getWrappedDictionary(terms, new DefaultTokenizer());
	}

	
	public static DictionaryWrapper getWrappedDictionary(Collection<String> terms, Tokenizer tokenizer) {
		
	    DictionaryWrapper wrapper = new DictionaryWrapper();
	    
	    wrapper.dictionary = new Dictionary(true);	    
	    wrapper.lookup = new HashMap<StringList, String>();
	    wrapper.tokenizer = tokenizer;

        for ( String term: terms ) {
        	
        	Span[] span = wrapper.tokenizer.tokenize(term);
        	StringList entry = new StringList(tokenizer.getSpanText(span, term.toLowerCase()));
        	wrapper.dictionary.put(entry);
        	wrapper.lookup.put(entry, term);        	
        }
        
	    return wrapper;
	}
	
	public static DictionaryWrapper getWrappedDictionary(Map<String, Set<String>> termsMap) {
		
    	Set<String> terms = new HashSet<String>();
    	terms.addAll(termsMap.keySet());
    	for ( String key: termsMap.keySet() )  terms.addAll(termsMap.get(key));
    	return getWrappedDictionary(terms, new DefaultTokenizer());
	}

	
	public static Dictionary getDictionary(Collection<String> terms, Tokenizer tokenizer) {
		
	    Dictionary dict = new Dictionary();    
        
        for ( String term: terms ) {
        	
        	Span[] span = tokenizer.tokenize(term);     	
        	StringList entry = new StringList(Span.getSpanText(span, term));
        	dict.put(entry);
        }

	    return dict;
	}	

	
	public static void applyDictionary(Iterator<String> textIterator, DictionaryWrapper wrapper, MatchReport report) {
		
        TokenNameFinder nameFinder = new DictionaryNameFinder(wrapper.getDictionary());

        while ( textIterator.hasNext() ) {
			
        	String text = textIterator.next();
        	
        	Span[] spans = wrapper.getTokenizer().tokenize(text);
        	String[] tokens = Span.getSpanText(spans, text.toLowerCase());
        	
            opennlp.tools.util.Span[] nameSpans = nameFinder.find(tokens);
                        
            for (opennlp.tools.util.Span hit: nameSpans) {
            	
            	report.match(text, spans[hit.getStart()].getBegin(), spans[hit.getEnd()-1].getEnd(), wrapper.getDictionaryEntry(new StringList(Arrays.copyOfRange(tokens, hit.getStart(), hit.getEnd()))));
            }
            
		}
	}

	
	public static Set<Match> applyDictionary(String text, DictionaryWrapper wrapper) {
		
        Set<Match> results = new HashSet<Match>();
		TokenNameFinder nameFinder = new DictionaryNameFinder(wrapper.getDictionary());

    	Span[] spans = wrapper.getTokenizer().tokenize(text);
    	String[] tokens = Span.getSpanText(spans, text.toLowerCase());
    	
        opennlp.tools.util.Span[] nameSpans = nameFinder.find(tokens);
                    
        for (opennlp.tools.util.Span hit: nameSpans) {
        	
        	Match match = new Match();
        	match.setBegin(spans[hit.getStart()].getBegin()); match.setEnd(spans[hit.getEnd()-1].getEnd());
        	match.setDictionaryEntry(wrapper.getDictionaryEntry(new StringList(Arrays.copyOfRange(tokens, hit.getStart(), hit.getEnd()))));;
        	results.add(match);
        }
        
        return results;
	}


	public static void applyTermsToSelf(List<String> terms, Map<String, Set<String>> map) {
		
		// sort the terms in order of increasing string length
		
		Collections.sort(terms, new Comparator<String>() {
	
			public int compare(String a, String b) {
				return ( a.length() - b.length() );
		}});
		
		// take each term and look for it as a substring of longer terms (after it in the list)
		
		for (int i = 0; i < terms.size(); i++ ) {
			
			String term = terms.get(i);
	    	String normalTerm = java.text.Normalizer.normalize(term, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

			// we want to find the term between word boundaries, ignore case, and allow for a closing bracket as part of the match
			
			String regex = "(?i).*?\\b" + normalTerm.replaceAll("\\.", "\\\\.") + "(\\)|\\s|\\b).*";
			
			for ( int j = i + 1; j < terms.size(); j++ ) {
				
				String text = terms.get(j);
		    	String normalText = java.text.Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
				
				if ( normalText.matches(regex) ) {
					
					// a term points to the set of smaller terms it contains
					Maps.addMapValue(map, text, term);
				}
			}
			
			// a term that doesn't contain a smaller term maps to itself
			
			if ( !map.containsKey(term) ) {
				
				Maps.addMapValue(map, term, term);
			}
		}
	}
	
}
