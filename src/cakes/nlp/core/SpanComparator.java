package cakes.nlp.core;

import java.util.Comparator;

public class SpanComparator implements Comparator<Span> {

	@Override
	public int compare(Span object1, Span object2) {

		// sort by BEGIN ascending, END descending
		
		if  (object1.getBegin() != object2.getBegin()) return object1.getBegin() - object2.getBegin();
		else return object2.getEnd() - object1.getEnd();
	}

}
