package cakes.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import xslt.Pipeline;

public class ModelMaker {

	public static Model newModel() {
		
        Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
		model.setNsPrefix("time", "http://www.w3.org/2006/time#");
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		model.setNsPrefix("aif", "http://www.arg.dundee.ac.uk/aif#");
		model.setNsPrefix("ies", "http://ies.data.gov.uk/ies4#");
		model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		
        return model;	
	}
	
	
	public static Model merge(Model... models) throws IOException {
		
        // The same blank node label in different models map to the same mode when the model are simply merged. This is wrong.
        // We need to serialize, then deserialize, to get rid of labels and ensure blank nodes in different models are distinct.

		Model result = ModelFactory.createDefaultModel();
		
		PipedInputStream min = new PipedInputStream();
		PipedOutputStream mout = new PipedOutputStream(min);
		
		Runnable blanker = new Runnable() {

			@Override
			public void run() {
				
				try {
					
					for ( Model model: models ) {
						
						model.write(mout, "TURTLE");
					}
					
					mout.close();
				}
				catch (IOException e) {
					System.err.println("Error merging models...");
					e.printStackTrace();
				}
			}
		
		};
		
		new Thread(blanker).start();
		RDFDataMgr.read(result, min, null, Lang.TURTLE);

		return result;
	}

	
	public static Model getModelFromXML(File input, Pipeline pipeline) throws TransformerException, IOException {
		
	/* Push the XML through the pipeline and construct a model from the output.
	 * The pipeline must produce RDF NTRIPLES format.
	 */
		
		PipedInputStream pin = new PipedInputStream();
		PipedOutputStream pout = new PipedOutputStream(pin);
		
		Runnable loader = new Runnable() {

			@Override
			public void run() {
				
				try {
					
					Source s = new StreamSource(new FileInputStream(input));
					pipeline.setOutput(pout);					
					pipeline.transform(s);
					pout.close();
				}
				catch (TransformerException | IOException e) {
					System.err.println("Error creating NTRIPLES from XSLT pipeline.");
					e.printStackTrace();
				}
			}
		
		};
		
		new Thread(loader).start();
		Model model = newModel();
		RDFDataMgr.read(model, pin, null, Lang.NTRIPLES);

		return model;
	}

}
