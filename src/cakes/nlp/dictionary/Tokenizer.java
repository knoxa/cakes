package cakes.nlp.dictionary;

import cakes.nlp.core.Span;

public interface Tokenizer {

	Span[] tokenize(String text);
	String[] getSpanText(Span[] spans, String text);
}
