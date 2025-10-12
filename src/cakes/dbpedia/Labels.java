package cakes.dbpedia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;


public class Labels {

	public static void main(String[] args) throws IOException {

		//File rdf = new File("models/measures.xml");		
		//File rdf = new File("models/place-concept.xml");
		String name = "data-science";
		//String name = "dtic";
		//String name = "place-concept";
		
		//	File aka = new File("models/aka.xml");		
		//	Model akaModel = getModel(aka); 
		File aka = new File("models/synonyms.xml");		
		Model akaModel = getModel(aka, Lang.RDFXML); 
		
		File rdf = new File("models/" + name + ".xml");		
//		File rdf = new File("/D:/Data/RDF/aircraft.rdf");		
		Model descriptions = getModel(rdf, Lang.RDFXML);
		descriptions.add(akaModel);
		
		List<String> labels = queryLabels(descriptions);
						
		PrintWriter out = new PrintWriter("lists/" + name + ".txt", "UTF-8");
		for (String label: labels) out.println(label);
		out.close();
		
/*
		Map<String, String> labelMap = queryLabelMap(descriptions);
		
		for (String label: labelMap.keySet() ) {
			
			System.out.println("\"" + label + "\"," + labelMap.get(label));
			
		}
*/
//		Set<String> labelSet = queryLabelSet(descriptions);
		
//		for (String label: labelSet ) {
			
		//	System.out.println("\"" + label + "\"," + label);
			
//		}
		
	}

	
	public static List<String> queryLabels(Model m) throws IOException {
		
		// returns a list of labels - redundant?
		
		String queryString = IOUtils.toString(Labels.class.getResourceAsStream("sparql/labels.txt"), "UTF-8");

		Query qry = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(qry, m);
        ResultSet rs = qe.execSelect();
        
        List<QuerySolution> results = ResultSetFormatter.toList(rs);
        
        List<String> entityLabels = results.stream().map(r -> r.getLiteral("label").getString()).collect(Collectors.toList());
        
        return entityLabels;
	}

	public static Map<String, Set<String>> queryLabelMap(Model m) throws IOException {
		
		// Returns a Map of labels (both preferred and alternate) to IRI's. Labels may be ambiguous, so a lookup returns a set of IRI's
		
		String queryString = IOUtils.toString(Labels.class.getResourceAsStream("sparql/labels.txt"), "UTF-8");

		Query qry = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(qry, m);
        ResultSet rs = qe.execSelect();
        
        List<QuerySolution> results = ResultSetFormatter.toList(rs);
        
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        
        for (QuerySolution result: results) {
        	
        	String key = result.getLiteral("label").getString();
        	Resource resource = result.getResource("entity");
			String value = resource.isURIResource() ? resource.getURI() : resource.getId().getLabelString();				
        	
        	Set<String> values = map.get(key);
        	
        	if (values == null) {
        		
        		values = new HashSet<String>();
        	}

        	values.add(value);
        	map.put(key, values);
        }
        
        return map;
	}

	public static Map<String, String> queryTypeMap(Model m) throws IOException {
		
		// Returns a Map of IRI's to type. A type is the preferred label of any broader concept.
		
		String queryString = IOUtils.toString(Labels.class.getResourceAsStream("sparql/types.txt"), "UTF-8");

		Query qry = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(qry, m);
        ResultSet rs = qe.execSelect();
        
        List<QuerySolution> results = ResultSetFormatter.toList(rs);
        
        Map<String, String> map = new HashMap<String, String>();
        
        for (QuerySolution result: results) {
        	
        	String value = result.getLiteral("label").getString();
        	Resource resource = result.getResource("entity");
			String key = resource.isURIResource() ? resource.getURI() : resource.getId().getLabelString();				
        	map.put(key, value);
        }
        
        return map;
	}
	

	public static Map<String, String> queryPreferredNameMap(Model m) throws IOException {
		
		// returns a Map of IRI's to preferred labels.
		
		String queryString = IOUtils.toString(Labels.class.getResourceAsStream("preferredNames.txt"), "UTF-8");

		Query qry = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(qry, m);
        ResultSet rs = qe.execSelect();
        
        List<QuerySolution> results = ResultSetFormatter.toList(rs);
        
/*        for ( QuerySolution x: results) {
        	
        	System.out.println(x.getResource("entity").toString() + " --> " + x.getLiteral("label").getString());
        }       
        System.out.println("-----------------");
*/        
        Map<String, String> entityLabelMap = results.stream().collect(
        		Collectors.toMap(
        		k -> k.getResource("entity").toString(),
        		v -> v.getLiteral("label").getString()
        		)
        );
        
        return entityLabelMap;
	}

	public static Set<String> queryLabelSet(Model m) throws IOException {
		
		// returns a Set of labels
		
		String queryString = IOUtils.toString(Labels.class.getResourceAsStream("labels-only.txt"), "UTF-8");

		Query qry = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(qry, m);
        ResultSet rs = qe.execSelect();
        
        List<QuerySolution> results = ResultSetFormatter.toList(rs);
        
        Set<String> entityLabelSet = results.stream().map(k -> k.getLiteral("label").getString()).collect(Collectors.toSet());
        
        return entityLabelSet;
	}

	public static Model getModel(File rdf, Lang lang) throws IOException {

		Model m = ModelFactory.createDefaultModel(); 
        RDFDataMgr.read(m, rdf.toURI().toString(), lang);

        return m;
	}
}
