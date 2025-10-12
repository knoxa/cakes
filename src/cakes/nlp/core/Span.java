package cakes.nlp.core;


public class Span {

	private int begin, end;
	

	public Span(int begin, int end) {
		
		this.begin = begin;
		this.end   = end;
	}


	@Override
	public String toString() {
		return "Span [begin=" + begin + ", end=" + end + "]";
	}
	
	
	public int getBegin() {
		return begin;
	}


	public void setBegin(int begin) {
		this.begin = begin;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}


	public static String[] getSpanText(Span[] spans, String text) {
		
		String[] results = new String[spans.length];
		
		for (int i = 0; i < spans.length; i++ ) {
			
			results[i] = text.substring(spans[i].begin, spans[i].end);
		}
		
		return results;
	}
}
