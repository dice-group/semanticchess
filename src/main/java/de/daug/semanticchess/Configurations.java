package de.daug.semanticchess;

/**
 * Configurations
 */
public class Configurations{
	//Stanford Core NLP Web Server
	public static final String WEB_SERVER = "http://139.18.2.39";
		
	//Database
	public static final String DB = "jdbc:virtuoso://localhost:1111";
	public static final String DB_SHORT = "localhost:1111";
	public static final String DB_USER = "dba";
	public static final String DB_PASSWORD = "dba";
	public static final String DB_GRAPH = "http://www.example.com/";
	public static final String DB_OPENING_GRAPH = "";
	
	//SPARQL
	public static final String SPARQL_PREFIXES = "PREFIX ex:<http://example.com> " 
			+ "PREFIX res:<http://example.com/res/> "
			+ "PREFIX prop:<http://example.com/prop/> "
			+ "PREFIX cres:<http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology/Resources/> "
			+ "PREFIX cont:<http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#> "
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";

	//FILE PATHS
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
	
}