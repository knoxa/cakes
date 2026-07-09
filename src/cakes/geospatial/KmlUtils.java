package cakes.geospatial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.RealVector;

import cakes.category.Maps;

public class KmlUtils {

	public static Map<String, Set<String>> getGeoDictionary(Kml kml) {
	
	/*
	 * Make a dictionary of places names. Keys of the returned map are place names that are terms in the dictionary.
	 * Each term maps to a set of preferred names. This set should of size 1, otherwise preferred names are ambiguous.
	 */
		Map<String, List<RealVector>> geometry = kml.getGeometry();
		Map<String, String> prefNames = kml.getPrefNames();
		
		Map<String, Set<String>> dictionary = new HashMap<String, Set<String>>();
	
		for ( String place: geometry.keySet() ) {
	
			Maps.addMapValue(dictionary, place, place); // the place name is the preferred label
		}
		
		// Also add alternate labels as dictionary terms, with preferred label as value
		for ( String place: prefNames.keySet() ) {
			
			Maps.addMapValue(dictionary, place, prefNames.get(place));
		}
	
		return dictionary;
	}

}
