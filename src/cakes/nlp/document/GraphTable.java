package cakes.nlp.document;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import cakes.nlp.document.DatabaseUtils;
import uk.ac.kent.dover.fastGraph.EdgeStructure;
import uk.ac.kent.dover.fastGraph.FastGraph;
import uk.ac.kent.dover.fastGraph.NodeStructure;

public class GraphTable {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws TransformerException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	
	private int nodeid = 0, edgeid = 0;	
	private Map<String, NodeStructure> nodeMap = new HashMap<String, NodeStructure>();
	private List<NodeStructure> nodes = new ArrayList<NodeStructure>();
	private List<EdgeStructure> edges = new ArrayList<EdgeStructure>();
	
	private int nodetypeid = 0, edgetypeid = 0;
	private Map<String, Integer> nodeTypeMap = new HashMap<String, Integer>();
	private Map<String, Integer> edgeTypeMap = new HashMap<String, Integer>();
	
	
	public GraphTable() {
		super();
	}

	public FastGraph getAllGraphs(Connection connection) throws SQLException, TransformerException, IOException, SAXException {

		getNodeTypesFromDatabase(connection);

		String sql = "select distinct subject, stype, predicate, object, otype from GRAPH";

		ResultSet results = DatabaseUtils.processQuery(sql, connection);
				
		return makeGraphFromResults(results);
	}


	public FastGraph getGraph(Connection connection, String id) throws SQLException, TransformerException, IOException, SAXException {

		getNodeTypesFromDatabase(connection);

		String sql = "select distinct subject, stype, predicate, object, otype from GRAPH where GRAPH_ID = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, id);
		ResultSet results = stmt.executeQuery();
				
		return makeGraphFromResults(results);
	}
	
	
	private FastGraph makeGraphFromResults(ResultSet results) throws SQLException {
		
		while ( results.next() ) {
        	
			String subject = results.getString("subject");			
			String stype = results.getString("stype");			
			String predicate = results.getString("predicate");			
			String object = results.getString("object");			
			String otype = results.getString("otype");
			
			//	public EdgeStructure(int id, String label, int weight, byte type, byte age, int node1, int node2) {
			
			NodeStructure f = getNode(subject, stype);
			NodeStructure t = getNode(object,  otype);

			EdgeStructure edge = new EdgeStructure(edgeid++, "", 0, (byte) getEdgeType(predicate), (byte) 0, f.getId(), t.getId());
			edges.add(edge);
		}
		
		return FastGraph.structureFactory(null, (byte) 0, nodes, edges, true);
	}

	
	public NodeStructure getNode(String label, String type) {
		
		NodeStructure node = nodeMap.get(type+label);
		
		if ( node == null ) {
			
			node = new NodeStructure(nodeid++, label, 0, (byte) getNodeType(type), (byte) 0);
			nodeMap.put(type+label, node);
			nodes.add(node);
		}
		
		return node;
	}
	
	public int getNodeType(String type) {
		
		Integer value = nodeTypeMap.get(type);
		
		if ( value == null ) {
			
			value = nodetypeid++;
			nodeTypeMap.put(type, value);		
			System.out.println(type + " = " +value);
		}
		
		return value;
	}
	
	public int getEdgeType(String type) {
		
		Integer value = edgeTypeMap.get(type);
		
		if ( value == null ) {
			
			value = edgetypeid++;
			edgeTypeMap.put(type, value);
			//System.out.println(type + " = " +value);
		}
		
		return value;
	}

	
	public void getNodeTypesFromDatabase(Connection connection) throws SQLException, TransformerException, IOException, SAXException {

		String sql = "select distinct STYPE as TYPE from GRAPH union select distinct OTYPE as TYPE from GRAPH";
		ResultSet results = DatabaseUtils.processQuery(sql, connection);

		while ( results.next() ) {
        	
			String type = results.getString("TYPE");			
			nodeTypeMap.put(type, nodetypeid++);		
		}
	}
	
	public void getEdgeTypesFromDatabase(Connection connection) throws SQLException, TransformerException, IOException, SAXException {

		String sql = "select distinct PREDICATE from GRAPH";
		ResultSet results = DatabaseUtils.processQuery(sql, connection);

		while ( results.next() ) {
        	
			String type = results.getString("PREDICATE");			
			edgeTypeMap.put(type, edgetypeid++);		
		}
	}

	public static Connection getConnection() {
		
		Connection connection = null;
		//String dburl = "jdbc:derby:D:/Data/db/MUC";
		String dburl = "jdbc:derby:D:/Data/db/TACITUS";
		
 		try {
			connection = DriverManager.getConnection(dburl, "user", "password");
		}
 		catch (SQLException e) {

			e.printStackTrace();
		}
 		
 		return connection;
	}
	
	public static String getDocUri(Connection connection, int doc_id) {
		
		String sql = "select URI from DOC_TEXT where DOC_ID = ?";
		String uri = null;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, doc_id);
			ResultSet results = stmt.executeQuery();
			
			if ( results.next() ) {
				
				uri = results.getString("URI");
			}
			
			stmt.close();
		}
		catch (SQLException e) {
				e.printStackTrace();
			}

		return uri;
	}

	
	public static void main (String[] args) throws SQLException, TransformerException, IOException, SAXException {
		
		GraphTable d = new GraphTable();
		d.getAllGraphs(getConnection());
	}
}
