package cakes.category;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import xslt.Pipeline;
import xslt.PipelineBuilder;

public class Maps {

	private Maps() {
		
	}

	public static Set<String> checkMap(Map<String, Set<String>> map) {
		
		Set<String> ambiguous = new HashSet<String>();
		for ( String key: map.keySet() ) {
			
			Set<String> values = map.get(key);
			
			if ( values.size() != 1 ) {
				
				System.err.println("Mulitple instances: key=" + key + ", values=" + values);
				ambiguous.add(key);
			}
		}
		
		return ambiguous;
	}

	
	public static void addSimpleLookupToMap(Map<String, Set<String>> map, Map<String, String> lookup) {
		
		for ( String key: lookup.keySet() ) {
			
			Set<String> values = map.get(key);			
			if ( values == null )  values = new HashSet<String>();
			values.add(lookup.get(key));
			map.put(key, values);
		}
	}

	
	public static Map<String, Set<String>> invertMap(Map<String, Set<String>> lookup) {
		
		Map<String, Set<String>> inverted = new HashMap<String, Set<String>>();
		
		for (String key: lookup.keySet() ) {
			
			Set<String> values = lookup.get(key);
			
			for ( String value: values ) {
				
				Set<String> newValues = inverted.get(value);
				if ( newValues == null )  newValues = new HashSet<String>();
				newValues.add(key);
				inverted.put(value, newValues);
			}
		}
		
		return inverted;
	}

	public static void addFunctionOutput(Map<String, Set<String>> map, String input, String output) {
		
		// Consider the map as a function from input to output. Add the output to the set of values keyed by the input.
		// For a function, there should only be one output. Allow more, but output an "ambiguity" warning message.
		
    	addMapValue(map, input, output);
		Set<String> values = map.get(input);	
    	
    	if ( values.size() > 1 ) {
    		
    		System.err.println("Warning: Mapping is ambiguous - " + input +" -> " + values);
    	}
	}

	public static void addMapValue(Map<String, Set<String>> map, String key, String value) {

		// Add a string to the set of values for given key, creating the set if necessary
		
		Set<String> values = map.get(key);
    	if ( values == null ) values = new HashSet<String>();
    	values.add(value);
    	map.put(key, values);
	}

	public static void addMapValues(Map<String, Set<String>> map, String key, Set<String> values) {

		// Add a set to the set of values for given key, creating the set if necessary
		
		Set<String> storedValues = map.get(key);
    	if ( storedValues == null ) storedValues = new HashSet<String>();
    	storedValues.addAll(values);
    	map.put(key, storedValues);
	}
	
	public static void addXmlToMap(FileInputStream input, Map<String, Set<String>> map) throws TransformerException, XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		MapFilter filter = new MapFilter();
		filter.setMap(map);
		reader.setContentHandler(filter);
		reader.parse(new InputSource(input));
	}
	
	public static Map<String, Set<String>> loadXmlMap(FileInputStream input) throws TransformerException, XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		addXmlToMap(input, map);
		return map;
	}
	
	public static void entityMap(FileInputStream input, Map<String, Set<String>> map) throws TransformerException, XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		EntityByTypeFilter filter = new EntityByTypeFilter();
		filter.setMap(map);
		reader.setContentHandler(filter);
		reader.parse(new InputSource(input));
	}
	
	
	public static void serializeXmlMap(FileOutputStream out, Map<String, Set<String>> map) throws TransformerConfigurationException, SAXException {
		
		Pipeline pipeline = PipelineBuilder.getEmptyPipeline();
		pipeline.setOutput(out);
		
		ContentHandler ch = pipeline.getContentHandler();
		
		ch.startDocument();
		ch.startElement("", "map", "map", new AttributesImpl());
		
		for ( String key: map.keySet() ) {
			
			Set<String> values = map.get(key);
			
			for (String value: values) {
				
				AttributesImpl attr = new AttributesImpl();
				attr.addAttribute("", "key", "key", "String", key);
				attr.addAttribute("", "value", "value", "String", value);
				ch.startElement("", "entry", "entry", attr);
				ch.endElement("", "entry", "entry");
			};
		}
		
		ch.endElement("", "map", "map");
		ch.endDocument();
	}
}
