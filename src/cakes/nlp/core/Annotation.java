package cakes.nlp.core;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Annotation {

	public int begin, end;
	public String type, surface, lemma, uri;
	public int docid;
	public float confidence = 0;
	public int actor = -1;

	
	@Override
	public String toString() {

		String retval = new String(docid + ": (" + begin + "," + end +  ") type=" + type + ", conf=" + confidence + ", lemma=" + lemma + ", surface=" + surface);
		return retval;
	}
	
	
	public static void serializeAnnotation(ContentHandler ch, Annotation annotation, int offset) throws SAXException {
		
    	AttributesImpl attr = new AttributesImpl();

		attr.addAttribute("", "begin",  "begin",  "CDATA", String.valueOf(annotation.begin + offset));
		attr.addAttribute("", "end",    "end",    "CDATA", String.valueOf(annotation.end + offset));
		attr.addAttribute("", "type",  "type",  "CDATA", annotation.type);
		if (annotation.lemma != null) attr.addAttribute("", "lemma", "lemma", "CDATA", annotation.lemma);
		attr.addAttribute("", "surface", "surface", "CDATA", annotation.surface);
		attr.addAttribute("", "conf", "conf", "CDATA", String.valueOf(annotation.confidence));
		if (annotation.uri != null) attr.addAttribute("", "about", "about", "CDATA", annotation.uri);
   	
		ch.startElement(Token.XML_NAMESPACE, "annotation", "annotation", attr);
		ch.endElement(Token.XML_NAMESPACE, "annotation", "annotation");	
	}

	
}
