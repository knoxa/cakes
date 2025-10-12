package cakes.nlp.parse;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;

import cakes.nlp.core.Span;
import cakes.nlp.core.Token;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Tokenizer {
	
	private static final int NEW_SENTENCE  =  1;
	private static final int IN_SENTENCE   =  2;
	private static final int END_SENTENCE  =  4;
	private static final int ABBREVIATION  =  8;
	private static final int FULL_STOP     = 16;

	public static List<Token> getTokens(String text, Locale locale) {
		
		List<Token> tokens = new ArrayList<Token>();

		BreakIterator tokenIterator = BreakIterator.getWordInstance(locale);

		Span[] spans = split(text, tokenIterator);
		String[] surface = Span.getSpanText(spans, text);
		
    	for (int i = 0; i < spans.length; i++ ) {
    		
    		Token token = new Token(spans[i].getBegin(), spans[i].getEnd());
    		
    		token.setSurface(surface[i]);
    		
    	//	Lemma lemma = new Lemma();
    	//	lemma.setLemmaForm(surface[i].toLowerCase(locale));
    	//	token.getLemmas().add(lemma);
    		
    		tokens.add(token);
    	}

		return tokens;	
	}
	
	
	public static Span[] getSentences(String text, Locale locale) {
		
		BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(locale);
		return split(text, sentenceIterator);	
	}
	
	
	public static Span[] split(String text, BreakIterator iterator) {
		
		List<Span> spans = new ArrayList<Span>();
		
	    iterator.setText(text);
	    int start = iterator.first();
	    int end   = iterator.next();

	    while ( end != BreakIterator.DONE ) {

            if ( !Character.isWhitespace(text.charAt(start)) ) {
            	
            	if ( end - start > 2 && Character.compare(text.charAt(end - 2), '\'') == 0 ) { // split on possessive: 's
                	
                	spans.add(new Span(start, end - 2));
                	spans.add(new Span(end - 2, end));
               }
            	else {
            		
                   	spans.add(new Span(start, end));
           	}
             }

	        start = end;
	        end = iterator.next();
	    }
	    
	    return spans.toArray(new Span[spans.size()]);
	}

	
	public static List<List<Token>> tokenize(String text, Locale locale) {
		
		Set<String> abbreviations = new HashSet<String>();
		String[] abbrev = {"dr", "mr", "sr", "jr", "prof", "hon", "st", "u.s", "u.k", "co", "no", "a.m", "p.m", "a.k.a", "inf", "va", "viz", "ss", "rev", "ult", "inst"};
		abbreviations.addAll(Arrays.asList(abbrev));
		String[] months = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
		abbreviations.addAll(Arrays.asList(months));
		String[] mil = {"sergt", "corpl", "acting-corpl", "act-corpl", "corpls", "temp", "pte", "l-cpl", "l-corpl", "lance-corpl", "lance-corp", "lance-sergt", "l-sergt", "l-sgt", "gen", "cpl", "sgt", "lieut", "col", "maj", "capt", "lt", "lieut", "second-lieut", "second-lieuts", "lieut-col", "regt", "mcl"};
		abbreviations.addAll(Arrays.asList(mil));
		String[] names = {"wm", "geo"};
		abbreviations.addAll(Arrays.asList(names));

		List<List<Token>> sentences = new ArrayList<List<Token>>();
		
		List<Token> tokens = Tokenizer.getTokens(text, locale);

		List<Token> sentence = null; Token t = null; Token next = null; int quoteCount = 0;
		int state = NEW_SENTENCE;
		
		ListIterator<Token> iterator = tokens.listIterator();
		
		if ( iterator.hasNext() ) {
			
			while ( iterator.hasNext() ) {
										
				switch (state) {
				
				case NEW_SENTENCE:
					
					sentence = new ArrayList<Token>();
					state = IN_SENTENCE;
					t = iterator.next();
					break;
					
				case IN_SENTENCE:
					
					if ( t.getSurface().equals("\"") )  quoteCount++;
					
					if ( t.getSurface().matches("^\\-+$") ) {
						
						sentence.add(t);
						t = iterator.next();
					}

					else if ( t.getSurface().length() == 1 && Character.isUpperCase(t.getSurface().charAt(0)) ) state = ABBREVIATION;
					
					else if ( t.getSurface().matches("^(.+)?\\.\\w$") ) state = ABBREVIATION;

					else if ( abbreviations.contains(t.getSurface().toLowerCase()) ) state = ABBREVIATION;

					else if ( t.getSurface().matches("^\\.+$|^\\?+$") )  {
						
						state = FULL_STOP;
						sentence.add(t);
					}
					
					else {
						
						sentence.add(t);
						t = iterator.next();
					}
					
					break;
					
				case ABBREVIATION:
					
					state = IN_SENTENCE;
					
					next = iterator.next();
					
					if ( next.getSurface().matches("^\\.$|^\\?$") ) {
						
						t.setEnd(next.getEnd());
						t.setSurface(t.getSurface() + next.getSurface());
						//l = new Lemma(); l.setLemmaForm(t.getSurface().toLowerCase(locale));	t.getLemmas().add(l);					

						if (iterator.hasNext()) {							
							sentence.add(t);
							t = iterator.next();
						}
					}
					else {
						
						sentence.add(t);
						t = next;
					}
					break;
					
				case FULL_STOP:
					
					next = iterator.next();
					
					if ( (next.getSurface().equals("\"") && quoteCount % 2 == 1) || (next.getSurface().equals(")")) ) {
						// A close quote or close bracket may be after the full stop in a sentence
						sentence.add(next);
						t = null;
					}
					else if ( next.getSurface().equals(",") ||  next.getSurface().equals(";") || next.getSurface().equals(":") || next.getSurface().equals("-") || next.getSurface().equals("’") ) {
						// punctuation might immediately follow some (unrecognised) abbreviation
						sentence.add(next);
						t = null;						
					}
					else {
						iterator.previous();
					}
					
					state = END_SENTENCE;
					break;
					
				case END_SENTENCE:
					
					sentences.add(sentence);
					state = NEW_SENTENCE;
					break;
				}
			}
			
			if ( t != null ) sentence.add(t);
			if ( sentence != null && sentence.size() > 0 ) sentences.add(sentence);
		}
		
		return sentences;
	}

	
	public static void serializeSentences(ContentHandler ch, List<List<Token>> sentences, int offset) throws SAXException {

		for ( List<Token> sentence : sentences ) {
			
			int begin = sentence.get(0).getBegin() + offset;
			int end   = sentence.get(sentence.size() - 1).getEnd() + offset;
			
			AttributesImpl sentenceAttr = new AttributesImpl();
			sentenceAttr.addAttribute("", "begin", "begin", "Integer", String.valueOf(begin));
			sentenceAttr.addAttribute("", "end", "end", "Integer", String.valueOf(end));

	        ch.startElement(Token.XML_NAMESPACE, "sentence", "sentence", sentenceAttr);
	        int tokenIndex = 0;
	        
			for ( Token token : sentence ) {
				
				token.serializeToken(ch, offset, ++tokenIndex);
			}
	        
	        ch.endElement(Token.XML_NAMESPACE, "sentence", "sentence");
		}
	}
}
