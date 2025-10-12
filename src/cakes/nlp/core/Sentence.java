package cakes.nlp.core;

import java.util.ArrayList;
import java.util.List;

public class Sentence extends Span {

	private List<Token> tokens;
	
	public Sentence(int begin, int end) {
		
		super(begin, end);
		tokens = new ArrayList<Token>();
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

}
