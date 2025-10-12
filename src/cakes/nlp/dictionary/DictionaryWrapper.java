package cakes.nlp.dictionary;

import java.util.Map;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.StringList;

public class DictionaryWrapper {

	Dictionary dictionary;
    Map<StringList, String> lookup;
    Tokenizer tokenizer;
    
	public Dictionary getDictionary() {
		return dictionary;
	}
	
	public String getDictionaryEntry(StringList match) {
		return lookup.get(match);
	}

	public Tokenizer getTokenizer() {
		return tokenizer;
	}

}
