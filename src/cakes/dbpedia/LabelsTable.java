package cakes.dbpedia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.apache.jena.vocabulary.RDFS;


public class LabelsTable {

	public static void main(String[] args) throws IOException {

		File modelFolder = new File("models");
		Model model = ModelFactory.createDefaultModel();
		
		for (File file: modelFolder.listFiles()) { 
			
			model.add(getModel(file));
		}
		
		queryLabels(model);
		queryLabelsCsv(model, new File("lists/labels.csv"));
	}

	public static void queryLabelsCsv(Model m, File output) throws IOException {
		
		String queryString = IOUtils.toString(LabelsTable.class.getResourceAsStream("labels.txt"), "UTF-8");

		Query qry = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(qry, m);
        ResultSet rs = qe.execSelect();
        
        ResultSetFormatter.outputAsCSV(new FileOutputStream(output), rs);
	}

	public static void queryLabels(Model m) throws IOException {
		
		String queryString = IOUtils.toString(LabelsTable.class.getResourceAsStream("labels.txt"), "UTF-8");

		Query qry = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(qry, m);
        ResultSet rs = qe.execSelect();
        
        while ( rs.hasNext() ) {
        	
        	QuerySolution soln = rs.next();
        	String label = soln.getLiteral("label").getString();
        	String normal = StringUtils.stripAccents(label);
        	
        	if ( !normal.equals(label) )  {
        		
        	 soln.getResource("entity").addLiteral(RDFS.label, normal);
        	}
        }
	}

	public static Model getModel(File rdf) throws IOException {

		Model m = ModelFactory.createDefaultModel(); 
        RDFDataMgr.read(m, rdf.toURI().toString());

        return m;
	}
	
	public static Model getModel(String url) throws IOException {

		Model m = ModelFactory.createDefaultModel(); 
        RDFDataMgr.read(m, url);

        return m;
	}

}
