/**
 * ECOLinker.java
 * SOURCE: http://pcai042.informatik.uni-leipzig.de/swp/SWP-13/swp13-sc/
 */

package de.daug.semanticchess.Converter.Utils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import de.daug.semanticchess.Configurations;
import de.uni_leipzig.informatik.swp13_sc.converter.ChessDataModelToRDFConverter.OutputFormats;
import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessOpening;
import de.uni_leipzig.informatik.swp13_sc.datamodel.rdf.ChessRDFVocabulary;
import de.uni_leipzig.informatik.swp13_sc.util.FileUtils;

/**
 * Links ECO resources to ChessGame resources.
 * 
 * @author Erik
 * 
 * modified by @author Joern
 * - mainly: getGamesForOpenings(ChessOpening co, String coURI)
 * - SPARQL query considers transpositions (sequence of moves)
 */
public class ECOLinker
{
    
    /**
     * Graph (Connection to Virtuoso triple store) to query from
     */
    protected VirtGraph virtuosoGraph;
    /**
     * cached mappings of ECOs and chess game URIs
     */
    protected Map<String, List<String>> ecoGameMapping;
    /**
     * List of all ECOs which causes errors and therefore returned invalid
     * results.
     */
    protected List<String> ecoWithQueryError;
    /**
     * if cached mappings are available
     */
    private boolean hasMappings;
    /**
     * whether all games should be processed or only those left
     */
    private boolean queryOnlyGamesWithOutECOResources;
    /**
     * How many sub query should be created before the moves left will only be
     * appended onto the rest.
     */
    private int subqueryDepth;
    /**
     * Class to retrieve chess openings (ECOs) from a triple store.
     */
    protected ChessOpeningDataRetriever codr;
    
    /**
     * Default timeout for a SPARQL query. (for efficiency) 
     */
    public final static int QUERY_TIMEOUT = 10;
    /**
     * Says that it will only query for games where no chess opening resource
     * is set. So it 'can' reduce the amount of games to search and process.
     */
    public final static boolean QUERY_ONLY_GAMES_WO_ECO_RES = true;
    /**
     * maximum depth to create subqueries
     */
    public final static int MAX_SUBQUERY_DEPTH = 18;
    /**
     * Name of file with error openings.
     */
    //public final static String FILE_ERROR_ECO_URI = "error_opening_list.txt";
    //public final static String FILE_ERROR_ECO_URI = "C:\\Users\\Erik\\Documents\\GitHub\\swp13-sc\\error_opening_list.txt";
    /**
     * Output file for mappings.
     */
    
    public String ecoLetter;
    
    public final static String FILE_MAPPING = Configurations.FILE_MAPPING;
    
    // ------------------------------------------------------------------------
    
    /**
     * variable name of ecos
     */
    protected final static String SPARQL_ECOS_VAR = "?ECO_URI";
    /**
     * graph name of ecos (testing)
     */
    protected final static String SPARQL_ECOS_GRAPH = "eco";
    /**
     * graph from clausel for sparql (testing)
     */
    protected final static String SPARQL_ECOS_FROM = ""; //"FROM <" + SPARQL_ECOS_GRAPH + ">\n";
    /**
     * sparql query to get all ECOs of the current database
     */
    protected final static String SPARQL_ECOS = "PREFIX cont:<http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#>  SELECT DISTINCT "
            + SPARQL_ECOS_VAR + "\n" + SPARQL_ECOS_FROM + "WHERE\n{\n  "
            + SPARQL_ECOS_VAR + " a cont:ChessOpening .\n}\nORDER BY " + SPARQL_ECOS_VAR;
    
    private String SPARQL_ECOS2;
    
    // ------------------------------------------------------------------------
    
    /**
     * variable name of a chess game (for sparql query, mapping)
     */
    protected final static String SPARQL_GAME_VAR = "?game";
    /**
     * variable prefix name of a chess move (for builder)
     */
    protected final static String SPARQL_GAME_MOVE_VAR_PREFIX = "?move_";
    
    // ------------------------------------------------------------------------
    
    /**
     * Creates a new ECOLinker object. Requires a VirtGraph to query a Virtuoso
     * triple store. Will set its query time out to 90 seconds to allow
     * efficient work.
     * 
     * @param   virtuosoGraph   Graph (connection) to triple store
     */
    public ECOLinker(VirtGraph virtuosoGraph)
    {
        this(virtuosoGraph, QUERY_TIMEOUT, QUERY_ONLY_GAMES_WO_ECO_RES);
    }
    
    /**
     * Creates a new ECOLinker object. Takes a new VirtGraph connection to a
     * Virtuoso triple store and sets it to the specified timeout. Creates a
     * new ChessOpeningDataRetriever with the same connection.
     * 
     * @param   virtuosoGraph   connection to Virtuoso triple store
     * @param   timeout     timeout in seconds
     */
    public ECOLinker(VirtGraph virtuosoGraph, int timeout, boolean onlyWOECORes)
    {
    	
    	
        this.virtuosoGraph = virtuosoGraph;
        this.virtuosoGraph.setQueryTimeout(timeout); // sec.
        
        this.hasMappings = false;
        
        this.queryOnlyGamesWithOutECOResources = onlyWOECORes;
        this.subqueryDepth = (MAX_SUBQUERY_DEPTH < 1)? 1 : MAX_SUBQUERY_DEPTH;
        
        try
        {
            this.codr = new ChessOpeningDataRetriever(this.virtuosoGraph);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    // ------------------------------------------------------------------------
    
    public void setEcoLetter(String letter){
    	this.ecoLetter = letter;
    }
    
    
    public void setSparqlEcos(){
        this.SPARQL_ECOS2 = "PREFIX cont:<http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#>  SELECT DISTINCT "
                + SPARQL_ECOS_VAR + "\n" + SPARQL_ECOS_FROM + "WHERE\n{\n  "
                + SPARQL_ECOS_VAR + " a cont:ChessOpening . \n"
                + SPARQL_ECOS_VAR + " cont:openingCode ?code . \n"
                +"FILTER regex(?code, "+this.ecoLetter+") } \nORDER BY " + SPARQL_ECOS_VAR;
    }
    
    public void setFileName(){
    	 //this.FILE_MAPPING = "src/main/resources/static/games/rdf/Mapping_ECO_GAME_"+this.ecoLetter+".ttl";
    }
    
    /**
     * Set to true if you want to only query chess games which do not contain a
     * mapping with the chess opening yet.
     * 
     * @param   ecoless false if all chess games
     */
    public void setOnlyGamesWithOutECOResources(boolean ecoless)
    {
        this.queryOnlyGamesWithOutECOResources = ecoless;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Queries the triple store the retrieve all the opening (ECO) URIs.
     * 
     * @return  List<String> with chess opening (ECO) URIs or null on error
     */
    public List<String> getOpeningURIs()
    {
        List<String> list = new ArrayList<String>();

        System.out.println("Get Openings: " + SPARQL_ECOS2);
        VirtuosoQueryExecution vqeS = VirtuosoQueryExecutionFactory.create(
                SPARQL_ECOS2, this.virtuosoGraph);

        try
        {
            org.apache.jena.query.ResultSet results = vqeS.execSelect();

            while (results.hasNext())
            {
                QuerySolution result = (QuerySolution) results.next();

                RDFNode n = result.get(SPARQL_ECOS_VAR);
                list.add(n.toString());
            }

            vqeS.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return list;
    }
    
    /**
     * Will query the triple store for games with equal opening moves like
     * those stored in the ChessOpening object.
     * 
     * @param   co  ChessOpening object (contains the opening moves)
     * @return  List<String> with mapped games for those opening moves,
     *              returns null on error
     */
    public List<String> getGamesForOpenings(ChessOpening co, String coURI)
    {
        if ((co == null) || (co.getMoves() == null) || (co.getMoves().isEmpty()))
        {
            return new ArrayList<String>();
        }
        
        //construct query
        StringBuilder sb = new StringBuilder();
        
        
        // more efficient to move later moves to the front and common to the
        // back, reduce the result set early (?)
        //for (int nr = 1; nr <= co.getMoves().size(); nr ++)
 
        for (int nr = co.getMoves().size(); nr > 0; nr --)
        {
            if (nr <= this.subqueryDepth)
            {
                // create subqueries
                String temp = sb.toString();
                
                sb = new StringBuilder()
                    .append("SELECT DISTINCT ")
                    .append(SPARQL_GAME_VAR)
                    .append(" WHERE\n{\n  ");
                if (temp.length() > 0)
                {
                    sb.append("{\n    ")
                    .append(temp.replaceAll("\n", "\n    "))
                    .append("\n  }\n  ");
                }
                
                
            }
            else if (nr == this.subqueryDepth + 1)
            {
                sb = new StringBuilder(sb.capacity())
                    .append(sb.toString().replaceAll("\n", "\n    "))
                    .append("\n  }\n  ");
            }
            else
            {
                // append normal
                sb.append("  ");
            }
            
            String moveVar = SPARQL_GAME_MOVE_VAR_PREFIX + nr;
            
            if (nr == 1)
            {
//                sb.append("{\n    SELECT ")
//                    .append(SPARQL_GAME_VAR)
//                    .append(" WHERE\n    {\n      ")
//                    .append(SPARQL_GAME_VAR)
//                    .append(" a ")
//                    .append(ChessRDFVocabulary.getOntologyPrefixName())
//                    .append(":")
//                    .append(ChessRDFVocabulary.ChessGame.getLocalName())
//                    .append(" .\n  ");
//                
//                if (this.queryOnlyGamesWithOutECOResources)
//                {
//                    sb.append("    OPTIONAL\n      {\n        ")
//                        .append(SPARQL_GAME_VAR)
//                        .append(' ')
//                        .append(ChessRDFVocabulary.getOntologyPrefixName())
//                        .append(":")
//                        .append(ChessRDFVocabulary.eco.getLocalName())
//                        .append(" ?eco .\n        ?eco a ")
//                        .append(ChessRDFVocabulary.getOntologyPrefixName())
//                        .append(":")
//                        .append(ChessRDFVocabulary.ChessOpening.getLocalName())
//                        .append(" .\n      }\n      FILTER (! BOUND ( ?eco ) ) .\n  ");
//                }
//                sb.append("  }\n  }\n  ");
                
                if (this.queryOnlyGamesWithOutECOResources)
                {
                    sb.append("{\n    SELECT ")
                        .append(SPARQL_GAME_VAR)
                        .append(" WHERE\n    {\n      ")
                        .append(SPARQL_GAME_VAR)
                        .append(" a ")
                        .append("prop")
                        .append(":")
                        .append("ChessGame")
                        .append(" .\n  ")
                        .append("    OPTIONAL\n      {\n        ")
                        .append(SPARQL_GAME_VAR)
                        .append(' ')
                        .append("prop")
                        .append(":")
                        .append("eco")
                        .append(" ?eco .\n        ?eco a ")
                        .append(ChessRDFVocabulary.getOntologyPrefixName())
                        .append(":")
                        .append(ChessRDFVocabulary.ChessOpening.getLocalName())
                        .append(" .\n      }\n      FILTER (! BOUND ( ?eco ) ) .\n  ")
                        .append("  }\n  }\n  ");
                }
                else
                {
                    sb.append(SPARQL_GAME_VAR)
                        .append(" a ")
                        .append("prop")
                        .append(":")
                        .append("ChessGame")
                        .append(" .\n  ");
                }
            }
            sb.append(SPARQL_GAME_VAR)
                .append(" ")
                .append("prop")
                .append(":")
                .append("moves")
                .append(" ")
                .append(moveVar)
                .append(" .\n  ")
                
//                .append(moveVar)
//                .append(" a ")
//                .append("prop")
//                .append(":")
//                .append("move")
//                .append(" .\n  ")
                
                .append(moveVar)
                .append(" ")
                .append("prop")
                .append(":")
                .append("moveNr")
                .append(" ")
                .append("?moveNr")
                .append(" .\n  ")
                
              .append(SPARQL_GAME_VAR)
              .append(" ")
              .append("prop")
              .append(":")
              .append("eco")
              .append(" ")
              .append("?eco")
              .append(" .\n  ")
                
                .append(moveVar)
                .append(" ")
                .append("prop")
                .append(":")
                .append("move")
                .append(" ")
                .append("'"+co.getMoves().get(nr - 1).getMove()+"'")
                .append(" .\n")
//            	.append("FILTER (regex(")
//            	.append(moveVarAbbr )
//            	.append(",")
//            	.append("'")
//            	.append("^"+co.getMoves().get(nr - 1).getMove().substring(0,1) + ".*" + co.getMoves().get(nr - 1).getMove().substring(1) + "$")
//            	.append("') && ")
                .append("FILTER (")
            	.append("regex(?eco,'" + co.getCode() +"') && ")
            	.append("?moveNr <= " + co.getMoves().size() + ")");
            
            if (nr <= this.subqueryDepth)
            {
                sb.append('}');
            }
            // FEN ?
        }
        
        if (co.getMoves().size() > this.subqueryDepth)
        {
            sb.insert(0, " WHERE\n{\n  {\n    ").insert(0, SPARQL_GAME_VAR).insert(0, "SELECT ").append('}');
        }
        
        
        //System.out.println("Get Games: " + sb.toString());
        
        List<String> list = new ArrayList<String>();
        
        VirtuosoQueryExecution vqeS = VirtuosoQueryExecutionFactory.create("PREFIX prop:<http://example.com/prop/> " + sb.toString(), this.virtuosoGraph);

        try
        {
            org.apache.jena.query.ResultSet results = vqeS.execSelect();
            
            while (results.hasNext())
            {
                QuerySolution result = (QuerySolution) results.next();

                RDFNode n = result.get(SPARQL_GAME_VAR);
                list.add(n.toString());
            }
            
            vqeS.close();
        }
        catch (Exception e)
        {
            System.out.println("Error with <" + coURI + "> : " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        
        return list;
    }
    
    /**
     * <p>
     * For the given URIs it will query the VirtGraph to get chess openings
     * (ECOs). It will then take the moves to get chess games with equal
     * openings and returns them for the opening URI.
     * </p>
     * <p>
     * It will remove openings from the URIs list if they were successful
     * processed. If there occurred an error the URI will remain in this list
     * waiting to be processed later on. -> So, if you want the list of URIs to
     * remain unchanged it would be best to create a work copy for this method.
     * It would also be possible to create a read-only list with
     * 
     * <pre>
     * &lt;Object&gt; List&lt;Object&gt; java.util.Collections.unmodifiableList(List<? extends Object> list)
     * </pre>
     * 
     * but this is discouraged.
     * </p>
     * 
     * @param uris
     *            URIs of chess openings
     * @return Map with URI of chess opening (ECO) and mapped games (URIs)
     */
    public Map<String, List<String>> getMappings(List<String> uris)
    {   
        if (this.codr == null)
        {
            System.err.println("Has no valid ChessOpeningDataRetriever!");
            return new HashMap<String, List<String>>();
        }
        
        if ((uris == null) || (uris.size() == 0))
        {
            System.err.println("No URIs to get ChessOntologies (ECOs) from!");
            return new HashMap<String, List<String>>();
        }
        
        Map<String, List<String>> mappings = new HashMap<String, List<String>>();
        List<String> errorURIs = new ArrayList<String>();
        
        // get Openings / ECOs objects
        for (String uri : uris)
        {
            ChessOpening co = this.codr.getOpening(uri);
            if (co == null)
            {
                System.err.println("<" + uri + "> is NULL ?");
                errorURIs.add(uri);
                continue;
            }
            
            long start = System.currentTimeMillis();
            List<String> games = getGamesForOpenings(co, uri);
            if (games == null)
            {
                //System.err.println("Error with Opening <" + uri + ">");
                errorURIs.add(uri);
                continue;
            }
            
            mappings.put(uri, games);
            
            // %f --> #.######
            // %s -- Float.toString(((System.currentTimeMillis() - startFile) / 1000.0f))
            System.out.format("Took %s sec. for %d mappings with %s.%n",
                    Float.toString(((System.currentTimeMillis() - start) / 1000.f)),
                    ((games == null) ? 0 : games.size()), co);
        }
        
        try
        {
            uris.clear();
            uris.addAll(errorURIs);
        }
        catch (Exception e)
        {
            // UnsupportedOperationException
            // ClassCastException
            // NullPointerException
            // IllegalArgumentException
            e.printStackTrace();
        }
        
        return mappings;
    }
    
    /**
     * Computes the reverse mapping for a mapping (Map<String, List<String>>).
     * E.g. ECO,URIs -> URI,ECOs
     * 
     * @param   map a Map< String, List< String >>
     * @return  Map< String, List< String >>
     */
    public Map<String, List<String>> getReverseMapping(Map<String, List<String>> map)
    {
        Map<String, List<String>> revMap = new HashMap<String, List<String>>();
        
        if (map != null)
        {
            for (String key : map.keySet())
            {
                for (String entry : map.get(key))
                {
                    if (revMap.containsKey(entry))
                    {
                        revMap.get(entry).add(key);
                    }
                    else
                    {
                        List<String> newList = new ArrayList<String>();
                        newList.add(key);
                        revMap.put(entry, newList);
                    }
                }
            }
        }
        
        return revMap;
    }
    
    // ------------------------------------------------------------------------   
    
    /**
     * Will retrieve all chess openings (ECOs) stored in the triple store and
     * tries to get all chess games to which the opening moves can be mapped.
     * It will store the results in the class.
     * 
     * @return  cached map with all the mapped chess games and their openings
     */
    public Map<String, List<String>> getMappings()
    {
        if ((this.hasMappings) && (this.ecoGameMapping != null)
                && (this.ecoGameMapping.size() > 0))
        {
            return this.ecoGameMapping;
        }
        
        this.ecoGameMapping = new HashMap<String, List<String>>();
        
        long start = System.currentTimeMillis();
        
        List<String> uris;
        if (this.ecoWithQueryError != null && this.ecoWithQueryError.size() > 0)
        {
            System.out.println("Using previously left ECO URIs ...");
            uris = this.ecoWithQueryError;
        }
        else
        {
            System.out.println("Using queried ECO URIs ...");
            uris = this.getOpeningURIs();
        }
        System.out.format("Took %s sec. for %d openings.%n",
                Float.toString(((System.currentTimeMillis() - start) / 1000.f)),
                uris.size());
        
        this.ecoGameMapping = this.getMappings(uris);
        this.ecoWithQueryError = uris;
        
        if (uris.size() > 0)
        {
            System.out.println(uris.size() + " URIs caused an error and were therefore skipped:");
            for (String uri : uris)
            {
                System.out.println("<" + uri + ">");
            }
        }
        
        this.hasMappings = true;
        
        // get URIs of openings / ECOs
        return this.ecoGameMapping;
    }
    
    /**
     * Returns the list of URIs of ECOs/Openings which caused errors while
     * querying. Used best after call to {@link #getMappings()}
     * 
     * @return  List<String> with URIs of openings which caused errors
     */
    public List<String> getECOsWithErrors()
    {
        return this.ecoWithQueryError;
    }
    
    /**
     * Set the list of URIs to process.
     * 
     * @param   uris    List<String>
     */
    public void setECOProcessingList(List<String> uris)
    {
        this.ecoWithQueryError = uris;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Creates a default Model with prefixes for chess rdf data.
     * 
     * @return  a new Model (Jena)
     */
    protected Model createDefaultModel()
    {
        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix(ChessRDFVocabulary.getOntologyPrefixName(),
                ChessRDFVocabulary.getURI());
        m.setNsPrefix(ChessRDFVocabulary.getResourcePrefixName(),
                ChessRDFVocabulary.getResourceURI());
        return m;
    }
    
    /**
     * Creates a new Mapping-Model and fills it with the given mappings.
     * 
     * @param   mappings    Map with ECO URI and mapped chess game URIs
     * @return  a new Model (Jena) with mappings (it may be empty if the given
     *          mappings are empty!)
     */
    public Model createMappingModel(Map<String, List<String>> mappings)
    {
        Model m = createDefaultModel();
        if (mappings == null)
        {
            System.err.println("No mappings for model!");
            return m;
        }
        
        for (String eco : this.ecoGameMapping.keySet())
        {
            RDFNode r_eco = m.createResource(eco);
            Property prop = m.createProperty(ChessRDFVocabulary.eco.toString());
            
            for (String game : this.ecoGameMapping.get(eco))
            {
                Resource r_game = m.createResource(game);
                
                m.add(r_game, prop, r_eco);
            }
        }
        
        return m;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Creates a new Mapping-Model, fills it with queried mappings and writes
     * it to the specified file.
     * 
     * @param   file    File to write into (GZIP-Stream)
     * @return  true if successful
     */
    public boolean writeMappingModel(String file)
    {

    	
        Model m = this.createMappingModel(this.getMappings());
        
        try
        {
            OutputStream os = FileUtils.openGZIPOutputStream(file);
            
            long start = System.currentTimeMillis();
            m.write(os, OutputFormats.TURTLE.getFormat());
            System.out.format("Took %s sec. for writing %d mappings.%n",
                    Float.toString(((System.currentTimeMillis() - start) / 1000.f)),
                    m.size());
            
            os.flush();
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Main method. Start from command line.
     * Will create a default mapping and stores it in the file.
     * 
     * @param   args    <ol>
     *                      <li> graph to query</li>
     *                      <li> link to database</li>
     *                      <li> username</li>
     *                      <li> password</li>
     *                      <li> [ecoless  (default: false)] - query games with eco
     *                               resources or only those without</li>
     *                      <li> [outputfile  (default: Mapping_ECO_GAME.ttl)]</li>
     *                  </ol>
     */
    public static void main(String[] args)
    {
        if ((args.length < 4) || (args.length > 6))
        {
            System.out.println("Usage: java -jar Programm.jar <graph> <link to db>"
                    + " <username> <password> [<Only games w/o eco::=yes|no> [<outputfilename::="
                    +  "file name>]]");
            System.exit(1);
        }
        
        String graph = args[0];
        String link = args[1];
        String user = args[2];
        String pass = args[3];
        boolean ecoless = (args.length >= 5) ? "yes".equalsIgnoreCase(args[4]) : false;
        String file = (args.length == 6) ? args[5] : FILE_MAPPING + "_" + args[4] + ".ttl";
        
        
        
        ECOLinker ecol = new ECOLinker(new VirtGraph(graph, "jdbc:virtuoso://"
                + link, user, pass));
        ecol.setOnlyGamesWithOutECOResources(ecoless);
        //String file = (args.length == 6) ? args[5] : ecol.FILE_MAPPING;
//        try
//        {
//            BufferedReader br = FileUtils.openReader(FileUtils.openInputStream(FILE_ERROR_ECO_URI));
//            List<String> uris = new ArrayList<String>();
//            
//            if (br != null && br.ready())
//            {
//                String uri;
//                while((uri = br.readLine()) != null && ! "".equals(uri))
//                {
//                    uris.add(uri);
//                }
//            }
//            
//            ecol.setECOProcessingList(uris);
//            br.close();
//            
//        }
//        catch (Exception e)
//        {
//        }
        ecol.setEcoLetter("'" + args[4] + "'");
        ecol.setSparqlEcos();
        ecol.writeMappingModel(file);
//        ecol.setEcoLetter("'B'");
//        ecol.setSparqlEcos();
//        ecol.writeMappingModel(file);
        
//        try
//        {
//            BufferedWriter bw = FileUtils.openWriter(FileUtils.openOutputStream(FILE_ERROR_ECO_URI));
//            
//            for (String uri : ecol.getECOsWithErrors())
//            {
//                bw.write(uri);
//                bw.newLine();
//            }
//            
//            bw.close();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
    }
}
