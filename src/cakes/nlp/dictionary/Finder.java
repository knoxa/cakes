package cakes.nlp.dictionary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import cakes.nlp.dictionary.EntityReporter;
import cakes.nlp.core.Span;
import cakes.nlp.dictionary.DictionaryUtils;
import cakes.nlp.dictionary.DictionaryWrapper;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.namefind.TokenNameFinder;

public class Finder {

	private DictionaryWrapper wrapper;
    private TokenNameFinder nameFinder;

    
	public Finder(DictionaryWrapper wrapper) {
		
		this.wrapper = wrapper;
        nameFinder = new DictionaryNameFinder(this.wrapper.getDictionary());
	}

	
	public List<String> scan(String text) {
		
		List<String> results = new ArrayList<String>();
		
    	Span[] spans = wrapper.getTokenizer().tokenize(text);
    	String[] tokens = Span.getSpanText(spans, text.toLowerCase());
    	
        opennlp.tools.util.Span[] nameSpans = nameFinder.find(tokens);
                    
        for (opennlp.tools.util.Span hit: nameSpans) {
        	
        	//StringList match = new StringList(Arrays.copyOfRange(tokens, hit.getStart(), hit.getEnd()));
        	int begin = spans[hit.getStart()].getBegin();
        	int end = spans[hit.getEnd()-1].getEnd();
        	results.add(text.substring(begin, end));
        }
		
		return results;
	}

	public static Map<String, Set<String>> applyDictionaryToEntitySet(Set<String> labels, Map<String, Set<String>> lookup, Set<String> found) throws XPathExpressionException, FileNotFoundException, TransformerException, ParserConfigurationException, SAXException, IOException {

        DictionaryWrapper wrapper = DictionaryUtils.getWrappedDictionary(lookup.keySet());
        
        EntityReporter reporter = new EntityReporter();
        reporter.setPrefLabels(lookup);
				
        DictionaryUtils.applyDictionary(labels.iterator(), wrapper, reporter);
        
        Map<String, Set<String>> refs = reporter.getRefers();
        
        for ( String key: reporter.getFound() ) {
        	refs.remove(key);
        }
        System.out.println("refs: " + refs);
        found.addAll(reporter.getFound());
        
        return reporter.getMatches();
	}
	
}
