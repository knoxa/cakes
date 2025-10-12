package cakes.category;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import xslt.BaseFilter;

public class MapFilter extends BaseFilter {
	
	private Map<String, Set<String>> map;

	public MapFilter() {
		super();
		map = new HashMap<String, Set<String>>();
	}
	
	public Map<String, Set<String>> getMap() {
		return map;
	}
	

	public void setMap(Map<String, Set<String>> map) {
		this.map = map;
	}

	@Override
	public void startElement(String uri, String localName, String qname, Attributes attr) throws SAXException {
				
		if ( qname.equals("entry") ) {
			
			String key = attr.getValue("key");
			String value = attr.getValue("value");
			Maps.addMapValue(map, key, value);
		}

		super.startElement(uri, localName, qname, attr);
	}
	
}
