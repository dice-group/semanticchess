package de.daug.semanticchess.Database;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.springframework.stereotype.Repository;

import de.daug.semanticchess.Configurations;
import virtuoso.jena.driver.VirtModel;

/**
 * This class collects all parts of a PGN and
 * returns a JSON.
 */
@Repository
public class GameVirtuoso {

	private ConnectVirtuoso conn;
	private VirtModel vModel;

	private static String PREFIX = Configurations.SPARQL_PREFIXES;

	/**
	 * constructor: 
	 * connects to Virtuoso and initialize a VirtModel
	 */
	public GameVirtuoso() {
		conn = new ConnectVirtuoso();
		vModel = conn.connectDefault();
	}

	/**
	 * gets moves from a chess game.
	 * @param uri: chess game URI
	 * @return: String with all moves of the specified game
	 */
	public String getMoves(String uri) {
		String moveList = "";
		int moveCounter = 0;

		String query = PREFIX + " SELECT DISTINCT ?moveNr ?move " + "WHERE {" + "<" + uri + "> "
				+ "prop:moves ?moves. ?moves prop:move ?move. ?moves prop:moveNr ?moveNr" + "} ORDER BY ?moveNr";

		// database connection
		System.out.println("============================");
		System.out.println("Exec: " + query);
		// create the sparql query from the String
		Query jquery = QueryFactory.create(query);

		QueryExecution qexec = QueryExecutionFactory.create(jquery, vModel);
		ResultSet results = qexec.execSelect();

		for (; results.hasNext();) {
			QuerySolution soln = results.nextSolution();
			int moveNr = soln.getLiteral("moveNr").getInt(); 
			String move = soln.getLiteral("move").getString();

			if (!move.equals("O-O") && !move.equals("O-O-O")) {
				move = move.replace("-", "");
			}

			if (moveNr % 2 != 0) {
				moveCounter++;
				moveList += moveCounter + ". " + move;
			} else {
				moveList += " " + move + " ";
			}
		}

		return moveList;
	}
	
	/**
	 * gets a property of a chess games
	 * @param uri: chess game URI
	 * @param property: event, date, site, white, black, whiteelo, blackelo, eco, result
	 * @return result
	 */
	public String getStringProperty(String uri, String property) {
		String query = PREFIX + "SELECT DISTINCT ?" + property + " WHERE {<" + uri + "> prop:" + property + " ?"
				+ property + "}";

		System.out.println("============================");
		System.out.println("Exec: " + query);
		// create the sparql query from the String
		Query jquery = QueryFactory.create(query);

		QueryExecution qexec = QueryExecutionFactory.create(jquery, vModel);
		ResultSet results = qexec.execSelect();
		String result = "";

		for (; results.hasNext();) {
			QuerySolution soln = results.nextSolution();
			try {
				result = soln.getLiteral(property).getString();
			} catch (Exception e) {
			}
		}

		if (result.isEmpty()) {
			result = "?";
		}

		if (property.equals("eco") && !result.equals("?")) {
			result = result.substring(0, 3);
		}

		return result;
	}

	/**
	 * gets property that is an integer
	 * @param uri: chess game URI
	 * @param property: round
	 * @return result
	 */
	public String getIntProperty(String uri, String property) {
		String query = PREFIX + "SELECT DISTINCT ?" + property + " WHERE {<" + uri + "> prop:" + property + " ?"
				+ property + "}";

		System.out.println("============================");
		System.out.println("Exec: " + query);
		// create the sparql query from the String
		Query jquery = QueryFactory.create(query);

		QueryExecution qexec = QueryExecutionFactory.create(jquery, vModel);
		ResultSet results = qexec.execSelect();
		String result = "0";

		for (; results.hasNext();) {
			QuerySolution soln = results.nextSolution();
			try {
				result = String.valueOf(soln.getLiteral(property).getInt());
			} catch (Exception e) {

			}
		}

		if (result.equals("0")) {
			result = "?";
		}

		return result;
	}
	
	/**
	 * bundles all needed information for a PGN into a String
	 * @param uri: chess game URI
	 * @return JSON with PGN
	 */
	public String getPGN(String uri){
		
		String results = "[Event '" + this.getStringProperty(uri, "event") + "']" + " " + "[Site '"
				+ this.getStringProperty(uri, "site") + "']" + " " + "[Date '" + this.getStringProperty(uri, "date")
				+ "']" + " " + "[Round '" + this.getIntProperty(uri, "round") + "']" + " " + "[White '"
				+ this.getStringProperty(uri, "white") + "']" + " " + "[Black '"
				+ this.getStringProperty(uri, "black") + "']" + " " + "[ECO '" + this.getStringProperty(uri, "eco")
				+ "']" + " " + "[Result '" + this.getStringProperty(uri, "result") + "']" + " "
				+ this.getMoves(uri);
				
		String json = "{\"head\": {\"vars\": [ \"answer\" ] } , \"results\": {\"bindings\": [{ \"answer\": { \"type\": \"pgn\" , \"value\": \""
				+ results + "\" }}]}}";
		
		return json;
	}
	
	/**
	 * main method for testing
	 * @param args
	 */
	public static void main(String[] args) {
		GameVirtuoso game = new GameVirtuoso();
		String uri = "http://example.com/res/Beniamino_Vergani_Kurt_Von_Bardeleben_1895______";

		System.out.println(game.getPGN(uri));
	}

}