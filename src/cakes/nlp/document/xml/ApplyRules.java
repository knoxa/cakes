package cakes.nlp.document.xml;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import cakes.nlp.core.Annotation;
import cakes.nlp.document.DocumentModel;
import xslt.Pipeline;

public class ApplyRules {

	public static void pipeline(Connection connection, Pipeline pipeline, int doc_id, AnnotationFilter filter) throws ParserConfigurationException, SAXException, IOException, TransformerException, SQLException {
		
		ContentHandler ch = pipeline.getContentHandler();

		ch.startDocument();
		DocumentModel.serializeDocument(connection, doc_id, ch);
		ch.endDocument();
		
		if ( filter == null ) return;
		
		Set<Annotation> annotations = filter.getAnnotations();
		
		for (Annotation annotation: annotations) {
			
			System.out.println("*#*** " + annotation);
		}
		
		DocumentModel.insertAnnotations(connection, annotations);	
	}

	
}
