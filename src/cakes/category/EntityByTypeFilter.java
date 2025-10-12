package cakes.category;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import xslt.BaseFilter;

public class EntityByTypeFilter extends BaseFilter {
	
	private Map<String, Set<String>> map;
	private Map<String, Set<String>> altNames;
	private String currentType, surface;

	public EntityByTypeFilter() {
		super();
		//map = new HashMap<String, Set<String>>();
		altNames = new HashMap<String, Set<String>>();
	}
	
	public Map<String, Set<String>> getMap() {
		return map;
	}
	
	
	public Map<String, Set<String>> getAltNames() {
		return altNames;
	}

	
	public void setMap(Map<String, Set<String>> map) {
		this.map = map;
	}

	@Override
	public void startElement(String uri, String localName, String qname, Attributes attr) throws SAXException {
				
		if ( qname.equals("entity") ) {
			
			currentType = attr.getValue("type");
			surface = attr.getValue("surface");
		}

		super.startElement(uri, localName, qname, attr);
	}

	@Override
	public void endElement(String uri, String localName, String qname) throws SAXException {
				
		if ( qname.equals("entity") ) {
			
			String type = currentType != null ? currentType : "unknown";		
			Maps.addMapValue(map, type, getText());
			
			if ( surface != null ) {
			
				Maps.addMapValue(altNames, getText(), surface);
			}
		}

		super.endElement(uri, localName, qname);
	}
	
}
