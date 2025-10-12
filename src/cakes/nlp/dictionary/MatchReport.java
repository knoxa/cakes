package cakes.nlp.dictionary;

public interface MatchReport {

	public void match(String text, int begin, int end, String dictionaryEntry);
}
