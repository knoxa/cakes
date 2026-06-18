package cakes.rdf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.transform.TransformerException;

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
import org.xml.sax.SAXException;

import cakes.category.Maps;


public class Labels {
	
	public static List<String> queryLabels(Model m) throws IOException {
		
		// returns a list of labels - redundant?
		
		String queryString = IOUtils.toString(Labels.class.getResourceAsStream("labels.txt"), "UTF-8");

		Query qry = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(qry, m);
        ResultSet rs = qe.execSelect();
        
        List<QuerySolution> results = ResultSetFormatter.toList(rs);
        
        List<String> entityLabels = results.stream().map(r -> r.getLiteral("label").getString()).collect(Collectors.toList());
        
        return entityLabels;
	}

	public static Map<String, Set<String>> queryLabelMap(Model m) throws IOException {
		
		// Returns a Map of labels (both preferred and alternate) to IRI's. Labels may be ambiguous, so a lookup returns a set of IRI's
		
		String queryString = IOUtils.toString(Labels.class.getResourceAsStream("labels.txt"), "UTF-8");
        Map<String, Set<String>> map = queryLabelMap(m, queryString);       
        return map;
	}

	private static Map<String, Set<String>> queryLabelMap(Model m, String queryString) throws IOException {
		
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

	public static Map<String, Set<String>> queryTypeMap(Model m) throws IOException {
		
		// Returns a Map of IRI's to type. A type is the preferred label of any broader concept.
		
		String queryString = IOUtils.toString(Labels.class.getResourceAsStream("types.txt"), "UTF-8");
        Map<String, Set<String>> map = queryLabelMap(m, queryString);       
        return map;
	}
	

	public static Map<String, Set<String>> queryPreferredNameMap(Model m) throws IOException {
		
		// returns a Map of preferred labels to IRI's.
		
		String queryString = IOUtils.toString(Labels.class.getResourceAsStream("preferredNames.txt"), "UTF-8");
        Map<String, Set<String>> map = queryLabelMap(m, queryString);       
        return map;
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

	
	public static  Map<String, Set<String>> extractLabels(Model model) throws TransformerException, IOException, SAXException {
	    
	    // Construct map of label to preferred label. A preferred label maps to itself.
		// A label with no corresponding preferred label maps to itself.

		Map<String, Set<String>> labels = queryLabelMap(model);
	    Map<String, Set<String>> prefs = Maps.invertMap(queryPreferredNameMap(model));
	
	    // "labels" maps label to IRI, inverted preferred name map gives preferred name(s) for IRI, bridge between the two ...
	      
	    Map<String, Set<String>> labelmap = new HashMap<>();
	
		for ( String key: labels.keySet() ) {
			
			for ( String uri: labels.get(key) ) {
				
				Set<String> prefset = prefs.get(uri);
				
				if ( prefset != null ) {
					
	    			for ( String pref: prefset ) {
	    				
	    				Maps.addMapValue(labelmap, key, pref);
	    			}
				}
				else {
					
					Maps.addMapValue(labelmap, key, key);
				}
			}
		}
		
	    return labelmap;
	}
}
