package cakes.nlp.core;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Token extends Span {

	public Token(int begin, int end) {
		
		super(begin, end);
		lemmas = new ArrayList<Lemma>();
	}


	public  static final String XML_NAMESPACE = "http://example.org/nlp/parse";

	private String surface;
	private List<Lemma> lemmas;

	@Override
	public String toString() {
		
		StringBuffer text = new StringBuffer();
		text.append("[" + getBegin() + "," + getEnd() + "] ");
		text.append(surface);
		
		for (Lemma lemma : getLemmas() ) {
			
			text.append("\n  ");
			text.append(lemma.getLemmaForm());
			text.append(" (POS=" + lemma.getType() + ", pennTag=" + lemma.getPennTag() + ")");
		}
		
		return text.toString();
	}
	
	
	public List<Lemma> getLemmas() {
		return lemmas;
	}
	

	public void setLemmas(List<Lemma> lemmas) {
		this.lemmas = lemmas;
	}


	public String getSurface() {
		return surface;
	}


	public void setSurface(String surface) {
		this.surface = surface;
	}


	public void serializeToken(ContentHandler ch, int offset, int tokenIndex) throws SAXException {
		
    	AttributesImpl attr = new AttributesImpl();

		attr.addAttribute("", "begin",  "begin",  "CDATA", String.valueOf(getBegin() + offset));
		attr.addAttribute("", "end",    "end",    "CDATA", String.valueOf(getEnd() + offset));
		attr.addAttribute("", "start",  "start",  "CDATA", String.valueOf(tokenIndex));
		attr.addAttribute("", "finish", "finish", "CDATA", String.valueOf(tokenIndex));
   	
		ch.startElement(Token.XML_NAMESPACE, "token", "token", attr);
		
		ch.startElement(Token.XML_NAMESPACE, "surface", "surface", new AttributesImpl());
		char[] chars = surface.toCharArray();
		ch.characters(chars, 0, chars.length);
		ch.endElement(Token.XML_NAMESPACE, "surface", "surface");	

		for ( Lemma lemma: lemmas ) {
			
			lemma.serializeToXML(ch);
		}
		
		ch.endElement(Token.XML_NAMESPACE, "token", "token");	
	}

	
}
