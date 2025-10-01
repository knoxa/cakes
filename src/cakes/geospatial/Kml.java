package cakes.geospatial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Kml {

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document doc;
	private String filename;

	private XPathFactory xpathFactory;
	private XPath xpath;
	private NamespaceContext namespaceContext;
	
	private XPathExpression placemarksExpr, nameExpr, coordsExpr, geomExpr, altLabelExpr;

	private Map<String, Element> places, geometry;
	private Map<String, String> prefNames;

	public Kml() {
		
		places = new HashMap<String, Element>();
		geometry = new HashMap<String, Element>();
		prefNames = new HashMap<String, String>();
		
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setExpandEntityReferences(false);
		
		try {
			builder = factory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		initializeXpath();
	}

	public void readKmlFile(File file) throws FileNotFoundException, SAXException, IOException, XPathExpressionException {
		
		filename = file.getName();
		
		doc = builder.parse(new FileInputStream(file));
				
		NodeList list = (NodeList) placemarksExpr.evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
		
		for ( int i = 0; i < list.getLength(); i++ ) {
			
			Element placemark = (Element) list.item(i);
			//String placemarkId = placemark.getAttribute("id"); // make use of id?
			
			Element name = (Element) nameExpr.evaluate(placemark, XPathConstants.NODE);
			String mapkey = name.getTextContent();
			
			if ( places.get(mapkey) != null ) {
				
				System.err.println(getFileName() + " - Duplicate place name: " + mapkey);
			}
			
			places.put(mapkey, placemark);
					
			Element geom = (Element) geomExpr.evaluate(placemark, XPathConstants.NODE);
			geometry.put(mapkey, geom);
			
			NodeList altlabelList = (NodeList) altLabelExpr.evaluate(placemark, XPathConstants.NODESET);
			
			for ( int j = 0; j < altlabelList.getLength(); j++ ) {
				
				String altLabel = altlabelList.item(j).getTextContent();
				String prefName = prefNames.get(altLabel);
				
				if ( prefName == null ) {
					
					prefNames.put(altLabel, mapkey);
				}
				else if ( prefName != mapkey ) {
					
					System.err.println("This alternative label is ambiguous: " + altLabel);
				}
			}
		}		
	}

	
	public List<RealVector> getGeometry(String place) {
		
		List<RealVector> points = new ArrayList<RealVector>();
		
		Element geom = geometry.get(place);
		
		if ( geom != null ) {
			
			NodeList coords = geom.getElementsByTagName("coordinates");
			
			String text = coords.item(0).getTextContent().trim();			
			String[] coordinateArray = text.split(" ");
			
			for ( int i = 0; i < coordinateArray.length; i++ ) {
				
				String[] items = coordinateArray[i].split(",");
				RealVector point = new ArrayRealVector(new double[] {Double.valueOf(items[0]), Double.valueOf(items[1])});
				points.add(point);
			}
		}
		
		return points;
	}

	
	public String getGeometryType(String place) {
		
		String result = null;
		Element geom = geometry.get(place);	
		if ( geom != null )  result = geom.getLocalName();
		return result;
	}

	
	public Map<String, List<RealVector>> getGeometry() {
		
		Map<String, List<RealVector>> results = new HashMap<String, List<RealVector>>();
		
		for (String key: geometry.keySet() ) {
			
			List<RealVector> points = new ArrayList<RealVector>();
			
			Element geom = geometry.get(key);

			try {
				Element coords = (Element) coordsExpr.evaluate(geom, XPathConstants.NODE);
				
				String text = coords.getTextContent().trim();			
				String[] coordinateArray = text.split(" ");
				
				for ( int i = 0; i < coordinateArray.length; i++ ) {
					
					String[] items = coordinateArray[i].split(",");
					RealVector point = new ArrayRealVector(new double[] {Double.valueOf(items[0]), Double.valueOf(items[1])});
					points.add(point);
				}
				
				results.put(key, points);
			}
			catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		
		return results;
	}
	
	public boolean isPoint(String place) {
		
		Element geom = geometry.get(place);		
		return nodeHasName(geom, "Point");
	}
	
	public boolean isLine(String place) {
		
		Element geom = geometry.get(place);		
		return nodeHasName(geom, "LineString");
	}
	
	public boolean isPolygon(String place) {
		
		Element geom = geometry.get(place);		
		return nodeHasName(geom, "Polygon");
	}
	
	private boolean nodeHasName(Element node, String name) {
		
		return node.getLocalName().equals(name);
	}
	
	public static double distance(RealVector a, RealVector b) {
		
		return a.getDistance(b);
	}
	
	public Set<String> getPlaceNames() {
		
		return geometry.keySet();
	}
	
	public Map<String, String> getPrefNames() {
		
		return prefNames;
	}
	
	public String getFileName() {
		return filename;
	}
	
	
	public static Map<String, List<RealVector>> getGeometryFromKmlMap(Map<String, Kml> kmlMap) {
		
		Map<String, List<RealVector>> geometry = new HashMap<String, List<RealVector>>();
		
		for (String key: kmlMap.keySet() ) {
			Geometry.addGeometry(geometry, kmlMap.get(key).getGeometry());
		}
		
		return geometry;
	}

	
	private void initializeXpath() {
		
		xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();
		
		namespaceContext = new NamespaceContext() {

			@Override
			public String getNamespaceURI(String prefix) {
				
		        if (prefix == null) throw new NullPointerException("Null prefix");
		        else if ("xhtml".equals(prefix)) return "http://www.w3.org/1999/xhtml";
		        else if ("kml".equals(prefix)) return "http://www.opengis.net/kml/2.2";
		        else if ("skos".equals(prefix)) return "http://www.w3.org/2004/02/skos/core#";
		        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
		        return XMLConstants.NULL_NS_URI;
			}

			@Override
			public String getPrefix(String uri) {

				throw new UnsupportedOperationException();
			}

			@Override
			public Iterator<String> getPrefixes(String uri) {

				throw new UnsupportedOperationException();
			}
			
		};
		
		xpath.setNamespaceContext(namespaceContext);
		
		try {
			placemarksExpr = xpath.compile(".//kml:Placemark");
			nameExpr = xpath.compile("./kml:name[1]");
			geomExpr = xpath.compile("./kml:Point|kml:LineString[1]|kml:Polygon[1]");
			altLabelExpr = xpath.compile(".//skos:altLabel");
			coordsExpr = xpath.compile(".//kml:coordinates");
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

}
