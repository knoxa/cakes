package cakes.nlp.core;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Lemma {

	private String lemmaForm, partOfSpeech, pennTag;

	public String getLemmaForm() {
		return lemmaForm;
	}

	public void setLemmaForm(String lemmaForm) {
		this.lemmaForm = lemmaForm;
	}

	public String getType() {
		return partOfSpeech;
	}

	public void setType(String type) {
		this.partOfSpeech = type;
	}

	public String getPennTag() {
		return pennTag;
	}

	public void setPennTag(String pennTag) {
		this.pennTag = pennTag;
	}
	

	public void serializeToXML(ContentHandler ch) throws SAXException {
		
    	AttributesImpl attr = new AttributesImpl();

		if ( partOfSpeech != null ) attr.addAttribute("", "type",  "type",  "CDATA", partOfSpeech);
		if ( pennTag != null ) attr.addAttribute("", "penn", "penn", "CDATA", pennTag);
   	
		ch.startElement(Token.XML_NAMESPACE, "lemma", "lemma", attr);
		
		if ( lemmaForm != null ) {
			
			char[] chars = lemmaForm.toCharArray();
			ch.characters(chars, 0, chars.length);
		}
		
		ch.endElement(Token.XML_NAMESPACE, "lemma", "lemma");
	}

}
