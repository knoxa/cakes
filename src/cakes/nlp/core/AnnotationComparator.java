package cakes.nlp.core;

import java.util.Comparator;

public class AnnotationComparator implements Comparator<Annotation> {

	@Override
	public int compare(Annotation object1, Annotation object2) {

		// sort by BEGIN ascending, END descending		
		if  (object1.begin != object2.begin) return object1.begin - object2.begin;
		else return object2.end - object1.end;
	}

}
