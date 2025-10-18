package cakes.text;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Edit {	
	
	public static void removeObjectsFromContext(Map<String, Set<String>> context, Collection<String> toRemove) {
		
		for ( String object: toRemove ) {
			
			context.remove(object);
		}
	}
}
