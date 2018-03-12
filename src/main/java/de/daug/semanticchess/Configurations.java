package de.daug.semanticchess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Configurations
 */
public class Configurations {
	// Stanford Core NLP Web Server
	public static final String DEFAULT_WEB_SERVER = "http://139.18.2.39";

	// Database
	public static final String DEFAULT_DB = "jdbc:virtuoso://localhost:1111";
	public static final String DEFAULT_DB_SHORT = "localhost:1111";
	public static final String DEFAULT_DB_USER = "dba";
	public static final String DEFAULT_DB_PASSWORD = "dba";
	public static final String DEFAULT_DB_GRAPH = "http://www.example.com/";
	public static final String DEFAULT_DB_OPENING_GRAPH = "";

	public String TEST_TEST;

	// SPARQL
	public static final String SPARQL_PREFIXES = "PREFIX ex:<http://example.com> "
			+ "PREFIX res:<http://example.com/res/> " + "PREFIX prop:<http://example.com/prop/> "
			+ "PREFIX cres:<http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology/Resources/> "
			+ "PREFIX cont:<http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#> "
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";

	// FILE PATHS
	public static final String JSON_CHESSOPENINGS = "src/main/resources/static/openings/chessopenings.json";
	public static final String TXT_CHESSOPENINGS = "src/main/resources/static/openings/chessopenings.txt";
	public static final String FILE_MAPPING = "src/main/resources/static/rdf/Mapping_ECO_GAME";
	public static final String PGN = "src/main/resources/static/games/pgn/";
	public static final String RDF = "src/main/resources/static/rdf/";
	public static final String OPENINGS = "src/main/resources/static/openings/";
	public static final String GAMES_TEST_FILE = "1610-1899";
	public static final String OPENINGS_TXT = "src/main/resources/static/openings/chessopenings.txt";
	public static final String OPENINGS_TXT_GZ = "src/main/resources/static/openings/chessopenings.txt.ttl.gz";
	public static final String OPENINGS_TTL = "src/main/resources/static/rdf/chessopenings.txt.ttl";

	public Configurations() {

	}
	
	public String getWebserver(){
		String ws = System.getenv("CHESS_APP_WEBSERVER");
		if (ws.length() == 0 || ws == null) {
			System.out.println("Can not find environment variable for <<CHESS_APP_WEBSERVER>>. App takes default value.");
			ws = DEFAULT_WEB_SERVER;
		}
		return ws;
	}
	
	public String getDB(){
		String db = System.getenv("CHESS_APP_DB");
		if (db.length() == 0 || db == null) {
			System.out.println("Can not find environment variable for <<CHESS_APP_DB>>. App takes default value.");
			db = DEFAULT_DB;
		}
		return db;
	}
	
	public String getDBShort(){
		String dbs = System.getenv("CHESS_APP_DB_SHORT");
		if (dbs.length() == 0 || dbs == null) {
			System.out.println("Can not find environment variable for <<CHESS_APP_DB_SHORT>>. App takes default value.");
			dbs = DEFAULT_DB;
		}
		return dbs;
	}

	public String getDBUser() {
		String db_user = System.getenv("CHESS_APP_DB_USER");
		if (db_user.length() == 0 || db_user == null) {
			System.out.println("Can not find environment variable for <<CHESS_APP_DB_USER>>. App takes default value.");
			db_user = DEFAULT_DB_USER;
		}
		return db_user;
	}
	
	public String getDBPassword(){
		String db_pwd = System.getenv("CHESS_APP_DB_PASSWORD");
		if (db_pwd.length() == 0 || db_pwd == null) {
			System.out.println("Can not find environment variable for <<CHESS_APP_DB_PASSWORD>>. App takes default value.");
			db_pwd = DEFAULT_DB_PASSWORD;
		}
		return db_pwd;
	}
	
	public String getDBGraph(){
		String db_graph = System.getenv("CHESS_APP_DB_GRAPH");
		if (db_graph.length() == 0 || db_graph == null) {
			System.out.println("Can not find environment variable for <<CHESS_APP_DB_GRAPH>>. App takes default value.");
			db_graph = DEFAULT_DB_GRAPH;
		}
		return db_graph;
	}
	
	public String getDBOpeningGraph(){
		String db_graph = System.getenv("CHESS_APP_DB_OPENING_GRAPH");
		if (db_graph.length() == 0 || db_graph == null) {
			System.out.println("Can not find environment variable for <<CHESS_APP_DB_OPENING_GRAPH>>. App takes default value.");
			db_graph = DEFAULT_DB_OPENING_GRAPH;
		}
		return db_graph;
	}
	


}