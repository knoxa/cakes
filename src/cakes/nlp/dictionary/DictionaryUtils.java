package cakes.nlp.dictionary;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
	
}
