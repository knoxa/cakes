package cakes.nlp.dictionary;

import java.text.BreakIterator;
//import java.text.Normalizer.Form;
//import org.apache.commons.lang3.StringUtils;
import java.text.Normalizer.Form;

import cakes.nlp.core.Span;

public class DefaultTokenizer implements Tokenizer {

	public Span[] tokenize(String text) {
		
    	String normal = java.text.Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    	//String normal = StringUtils.stripAccents(text);
		//String normal = text;
		return cakes.nlp.parse.Tokenizer.split(normal, BreakIterator.getWordInstance());
	}

	public String[] getSpanText(Span[] spans, String text) {

		String[] tokens = Span.getSpanText(spans, text.toLowerCase());
		return tokens;
	}
}
