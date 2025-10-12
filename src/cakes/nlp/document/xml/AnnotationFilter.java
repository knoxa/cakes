package cakes.nlp.document.xml;

import java.util.HashSet;
import java.util.Set;

import cakes.nlp.core.Annotation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;


public class AnnotationFilter extends XMLFilterImpl {
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	private boolean outputAll = true;
	private int docid = -1;
	
	@Override
	public void startDocument() throws SAXException {

		annotations = new HashSet<Annotation>();
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qname, Attributes attr) throws SAXException {

		if ( localName.equals("annotation") ) {

			String type = attr.getValue("type");
			String lemma = attr.getValue("lemma");
			String surface = attr.getValue("surface");
			String actorAttr = attr.getValue("actor");
			
			String docIdAttr = attr.getValue("docid");
			int docid = (docIdAttr != null) ? Integer.parseInt(docIdAttr) : this.docid;
			int begin = Integer.parseInt(attr.getValue("begin"));
			int end   = Integer.parseInt(attr.getValue("end"));
			
			String confidenceAttr = attr.getValue("confidence");
			float confidence = (confidenceAttr != null) ? Float.parseFloat(attr.getValue("confidence")) : (float) 0.26;
			
			Annotation a = new Annotation();
			a.docid = docid;
			a.begin = begin; a.end = end; a.type = type; a.lemma = lemma; 
			a.docid = docid; a.surface = surface;
			a.confidence = confidence;
			
			if ( actorAttr != null ) {
				
				a.actor = Integer.valueOf(actorAttr);
			}
			
			annotations.add(a);
			
			super.startElement(uri, localName, qname, attr);

		}
		else if ( localName.equals("document") ) {
			
			String annotationDocid = attr.getValue("doc");
			if (annotationDocid != null) docid = Integer.parseInt(annotationDocid);
			
			super.startElement(uri, localName, qname, attr);
		}
		else if (outputAll) {
						
			super.startElement(uri, localName, qname, attr);
		}
	}
	
	
	
	@Override
	public void endElement(String uri, String localName, String qname) throws SAXException {

		if ( localName.equals("annotation") || outputAll ) {
		
			super.endElement(uri, localName, qname);
		}
	}

	public Set<Annotation> getAnnotations() {
		
		return annotations;
	}
	
	public void reset() {
		
		annotations = new HashSet<Annotation>();
	}
}
