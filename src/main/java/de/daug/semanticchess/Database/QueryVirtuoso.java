package de.daug.semanticchess.Database;

import java.io.ByteArrayOutputStream;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.springframework.stereotype.Repository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.daug.semanticchess.Configurations;
import de.daug.semanticchess.Database.ConnectVirtuoso;
import virtuoso.jena.driver.VirtModel;

/**
 * The previously allocated SPARQL query from the user query (by Allocator.java) is now 
 * used to get an result by the database.
 */
@Repository
public class QueryVirtuoso {

	private static String PREFIX = 	Configurations.SPARQL_PREFIXES;
	
	/**
	 * empty constructor
	 */
	public QueryVirtuoso() {
	}

	/**
	 * the method connects to the database and returns a JSON
	 * with the wanted result
	 * @param strQuery: SPARQL Query
	 * @return JSON
	 */
	public String getCustomResult(String strQuery) {
		String query = PREFIX + " " + strQuery;
		
		//database connection
		ConnectVirtuoso conn = new ConnectVirtuoso();
		VirtModel vModel = conn.connectDefault();

		System.out.println("============================");
		System.out.println("Exec: " + query);
		//create the sparql query from the String
		Query jquery = QueryFactory.create(query);
		Gson gson = new GsonBuilder().create();
		String json = "";

		//SELECT
		if (jquery.isSelectType()) {
			QueryExecution qexec = QueryExecutionFactory.create(jquery, vModel);
			ResultSet results = qexec.execSelect();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(outputStream, results);
			json = new String(outputStream.toByteArray());
			System.out.println("============================\n");

			return json;
		}

		//ASK
		if (jquery.isAskType()) {
			QueryExecution qexec = QueryExecutionFactory.create(jquery, vModel);
			Boolean results = qexec.execAsk();

			json = "{\"head\": {\"vars\": [ \"answer\" ] } , \"results\": {\"bindings\": [{ \"answer\": { \"type\": \"boolean\" , \"value\": \""
					+ Boolean.valueOf(results) + "\" }}]}}";

			return json;
		}
		
		//UNKNOWN STATEMENT
		json = gson.toJson("Unkown statement.");

		return json;
	}

	/**
	 * main method for testing
	 * @param args
	 */
	public static void main(String[] args) {
		SparqlVirtuoso virtQuery = new SparqlVirtuoso();
		String json = virtQuery.getCustomResult(
				"select ?game ?black WHERE {?game prop:white 'Wilhelm Steinitz'. ?game prop:black ?black}");

		System.out.println(json);

	}
}