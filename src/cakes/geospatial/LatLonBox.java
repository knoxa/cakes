package cakes.geospatial;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class LatLonBox {

	private String name = "overlay";	
	private double north = 0.0, south = 0.0, east = 0.0, west = 0.0, rotation = 0.0;

	public double getNorth() {
		return north;
	}

	public void setNorth(double north) {
		this.north = north;
	}

	public double getSouth() {
		return south;
	}

	public void setSouth(double south) {
		this.south = south;
	}

	public double getEast() {
		return east;
	}

	public void setEast(double east) {
		this.east = east;
	}

	public double getWest() {
		return west;
	}

	public void setWest(double west) {
		this.west = west;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		
		return String.format("%s ... North: %f, South: %f, West: %f, East: %f, Rotation: %f\n", name, north, south, east, west, rotation);
	}
	
	
	public void serialize(ContentHandler ch) throws SAXException {
		
		String north = String.format("%.8f", this.north);
		String south = String.format("%.8f", this.south);
		String west  = String.format("%.8f", this.west);
		String east  = String.format("%.8f", this.east);
		String rotation = String.format("%.4f", this.rotation);
		
		ch.startElement("", "GroundOverlay", "GroundOverlay", new AttributesImpl());
		
		ch.startElement("", "name", "name", new AttributesImpl());
		ch.characters(name.toCharArray(), 0, name.length());
		ch.endElement("", "name", "name");

		ch.startElement("", "LatLonBox", "LatLonBox", new AttributesImpl());
		
		ch.startElement("", "north", "north", new AttributesImpl());
		ch.characters(north.toCharArray(), 0, north.length());
		ch.endElement("", "north", "north");
		
		ch.startElement("", "south", "south", new AttributesImpl());
		ch.characters(south.toCharArray(), 0, south.length());
		ch.endElement("", "south", "south");
		
		ch.startElement("", "west", "west", new AttributesImpl());
		ch.characters(west.toCharArray(), 0, west.length());
		ch.endElement("", "west", "west");
		
		ch.startElement("", "east", "east", new AttributesImpl());
		ch.characters(east.toCharArray(), 0, east.length());
		ch.endElement("", "east", "east");
		
		ch.startElement("", "rotation", "rotation", new AttributesImpl());
		ch.characters(rotation.toCharArray(), 0, rotation.length());
		ch.endElement("", "rotation", "rotation");
		
		ch.endElement("", "LatLonBox", "LatLonBox");
		ch.endElement("", "GroundOverlay", "GroundOverlay");
	}
	
}
