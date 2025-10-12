package cakes.dbpedia;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;


public class EntityGetter {

	private static final String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";

	private ParameterizedSparqlString query;
	
	public EntityGetter() {

		try {
			String construct = IOUtils.toString(EntityGetter.class.getResourceAsStream("entity.txt"), "UTF-8");
    		query = new ParameterizedSparqlString();
    		query.setCommandText(construct);
		}
		catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void fetch(String iri, Model results) {

		query.setIri("entity", iri);
		System.out.println("fetching: " + iri);
		
 		QueryEngineHTTP engine = new QueryEngineHTTP(DBPEDIA_SPARQL_ENDPOINT, query.toString());
 		engine.setModelContentType("application/rdf+xml");
 		engine.execConstruct(results);
 		engine.close();
	}
	

	public static void main(String[] args) throws IOException {

		Model model = ModelFactory.createDefaultModel();

		EntityGetter e = new EntityGetter();
		e.fetch("http://dbpedia.org/resource/Kilometre", model);
		//e.fetch("http://dbpedia.org/resource/Movimiento_Armado_Quintin_Lame", model);
		
		model.write(System.out);
	}

}
