package cakes.dbpedia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;


public class ModelExaminer {

	public static void main(String[] args) throws IOException {
//		File rdf = new File("models/place-concept.xml");		
	//	File rdf = new File("models/org.xml");		
	//	File rdf = new File("models/people.xml");		
		File rdf = new File("models/muc.xml");		
//		File rdf = new File("models/virus.xml");		
		//File rdf = new File("models/ability.xml");		
//		File rdf = new File("models/events.xml");		
//		File rdf = new File("models/ai.xml");		
	//	File rdf = new File("models/ai-science.xml");		
//		File rdf = new File("models/roman.xml");		
	//	File rdf = new File("models/tacitus-people.xml");		
	//	File rdf = new File("models/data-science.xml");		
		//File rdf = new File("models/tigers.xml");		
//		File rdf = new File("models/aircraft.xml");		
	//	File rdf = new File("models/infrastructure.xml");		
	//	File rdf = new File("models/places.xml");		
		Model descriptions = getModel(rdf); 
		
		List<String> work = queryWithoutDescription(descriptions);
		EntityGetter getter = new EntityGetter();
		
		for ( String item: work ) {
			
			System.out.println("to do:" + item);
			getter.fetch(item, descriptions);
		}
		
		List<String> workx = queryWithoutDescription(descriptions);
		System.out.println(workx.size());
		
		descriptions.write(new PrintWriter("models/next.xml", "UTF-8"));
	}

	
	public static List<String> queryWithoutDescription(Model m) throws IOException {
		
		// returns a list of the resources in the input model that don't have a skos:description
		
		String queryString = IOUtils.toString(ModelExaminer.class.getResourceAsStream("undescribed.txt"), "UTF-8");

		Query qry = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(qry, m);
        ResultSet rs = qe.execSelect();
        
        List<QuerySolution> results = ResultSetFormatter.toList(rs);
        List<String> iriList = results.stream().map(r -> r.getResource("entity").toString()).collect(Collectors.toList());
        
        return iriList;
	}

	public static Model getModel(File rdf) throws IOException {

		Model m = ModelFactory.createDefaultModel(); 
        RDFDataMgr.read(m, rdf.toURI().toString());

        return m;
	}
}
