package cakes.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;

public class Homophone {

	static Metaphone encoder = new Metaphone();
	static Soundex soundex = new Soundex();

	public static Map<String, String> getSoundex(Set<String> tokens) {
		
		Map<String, String> soundexMap = new HashMap<String, String>();

		for ( String token: tokens ) {
			
			try {
				
				String sound = soundex.encode(token);	
				soundexMap.put(token, sound);
			}
			catch (IllegalArgumentException e) {
				
				System.err.println("Soundex: " + e.getMessage());
			}
		}
		
		return soundexMap;
	}
	
	
	public static Map<String, String> getMetaphones(Set<String> tokens) {
		
		Map<String, String> metaphoneMap = new HashMap<String, String>();
		
		for ( String token: tokens ) {
			
			String code = encoder.encode(token);
			
			if ( code.length() > 0 )  {
				
				metaphoneMap.put(token, code);
			}
		}
		
		return metaphoneMap;
	}
}
