package cakes.geospatial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import xslt.Pipeline;

public class Geometry {

	public static double EPSILON = 1e-2;

	private Geometry() {
	}

	public static void addGeometry(Map<String, List<RealVector>> currentGeo, Map<String, List<RealVector>> newGeo) {
		
		int prevSize = currentGeo.size();
		
		Set<String> names = new HashSet<String>();
		names.addAll(currentGeo.keySet());
		names.retainAll(newGeo.keySet());
		System.out.println("No. of KML entries in common: " + names.size());
		
		for ( String name: names ) {
			
			List<RealVector> loc1 = currentGeo.get(name);
			List<RealVector> loc2 = newGeo.get(name);
			
			if ( loc1.size() != loc2.size() ) {
				
				System.err.println("Two different geometries for: " + name);
			}
			
			if ( loc1.size() == 1 && loc2.size() == 1) {
				
				RealVector v1 = loc1.iterator().next();
				RealVector v2 = loc2.iterator().next();
				
				if ( v1.getDistance(v2) > EPSILON ) {
					
					System.err.println("Two different points for: " + name + " - " + Kml.distance(v1, v2));
				}		
			}
		}
		
		currentGeo.putAll(newGeo);
		System.out.println("ADDED: " + (currentGeo.size() - prevSize) );
	}

	public static void serializeGeometry(Map<String, List<RealVector>> geo, Map<String, Set<String>> alt, ContentHandler ch) throws SAXException {
		
        ch.startDocument();
        ch.startElement("", "geo", "geo", new AttributesImpl());
        
        for ( String placeName: geo.keySet() ) {
        	
        	Set<String> altnames = alt != null ? alt.get(placeName) : null;
        	
            ch.startElement("", "place", "place", new AttributesImpl());
            
            ch.startElement("", "name", "name", new AttributesImpl());
            ch.characters(placeName.toCharArray(), 0, placeName.length());  		
            ch.endElement("", "name", "name");
            
            List<RealVector> coordinates = geo.get(placeName);
            
            for (RealVector coordinate: coordinates ) {
            	
            	ch.startElement("", "coordinates", "coordinates", new AttributesImpl());
                
                StringBuffer coordinateText = new StringBuffer();
                coordinateText.append(String.valueOf(coordinate.getEntry(0)));
                coordinateText.append(",");
                coordinateText.append(String.valueOf(coordinate.getEntry(1)));
                coordinateText.append(",0");
               
                ch.characters(coordinateText.toString().toCharArray(), 0, coordinateText.length());           
                ch.endElement("", "coordinates", "coordinates");
            }
    		
            if ( altnames != null) {
            	
            	for ( String altName : altnames ) {
            		
            		if ( !altName.equals(placeName) ) {
            			
                    	ch.startElement("", "altLabel", "altLabel", new AttributesImpl());
                    	ch.characters(altName.toCharArray(), 0, altName.length());
                    	ch.endElement("", "altLabel", "altLabel");            			
            		}
            	}
            	
            }
            
            ch.endElement("", "place", "place");

        }
		
        ch.endElement("", "geo", "geo");
		ch.endDocument();
	}

	
	public static void serializeGeometryAsKml(Map<String, List<RealVector>> geo, Map<String, Set<String>> alt, File output) throws TransformerConfigurationException, FileNotFoundException, SAXException  {
		
		Pipeline pipeline = new Pipeline();
		pipeline.addStep(new StreamSource(Geometry.class.getResourceAsStream("serializeToKml.xsl")));
		pipeline.setOutput(new FileOutputStream(output));
		ContentHandler ch = pipeline.getContentHandler();
		serializeGeometry(geo, alt, ch);
	}
	
	public static void serializeGeometry(Map<String, List<RealVector>> geo, Map<String, Set<String>> alt, File output) throws TransformerConfigurationException, FileNotFoundException, SAXException  {
		
		Pipeline pipeline = new Pipeline();
		pipeline.setOutput(new FileOutputStream(output));
		ContentHandler ch = pipeline.getContentHandler();
		serializeGeometry(geo, alt, ch);
	}

	
	public static RealVector getCentroid(Collection<RealVector> points) {
		
		if ( points.size() == 1 ) {
			
			return points.iterator().next();
		}
		else {
			
			RealVector centroid = new ArrayRealVector(new double[] {0.0, 0.0});	
			for ( RealVector point: points )  centroid = centroid.add(point);
			centroid.mapDivideToSelf(points.size());
			return centroid;
		}		
	}

}
