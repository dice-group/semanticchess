/**
 * ChessOpeningDataRetriever.java
 * SOURCE: http://pcai042.informatik.uni-leipzig.de/swp/SWP-13/swp13-sc/
 */
package de.daug.semanticchess.Converter.Utils;

import java.util.ArrayList;
import java.util.List;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;

import de.daug.semanticchess.Converter.Utils.ChessMoveListDataRetriever;
import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessGame;
import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessOpening;
import de.uni_leipzig.informatik.swp13_sc.datamodel.rdf.ChessRDFVocabulary;

/**
 * A retriever class for querying a triplestore over a VirtGraph (only
 * Virtuoso therefore). It returns for given URI/IRIs the first found
 * resource and wraps it into a {@link ChessOpening} object.
 * 
 * @author Erik
 */
public class ChessOpeningDataRetriever
{
    /**
     * The virtuosoGraph (Virtuoso triple store) which will be queried.
     */
    private final VirtGraph virtuosoGraph;
    
    /**
     * Internal ChessMoveListDataRetriever to retrieve chess move data from
     * game URI/IRIs and assign the resources to the chess games.
     */
    private ChessMoveListDataRetriever cmdr;
    
    /**
     * Internal. VARIABLE_CODE
     */
    private final static String VARIABLE_CODE = "?code";
    /**
     * Internal. VARIABLE_NAME
     */
    private final static String VARIABLE_NAME = "?name";
    
    /**
     * Constructor. Requires the connection to the datastore (Virtuoso)
     * 
     * @param   virtuosoGraph   the triplestore which will be queried
     * @throws  Exception   if argument is null
     */
    public ChessOpeningDataRetriever(VirtGraph virtuosoGraph)
        throws Exception
    {
        if (virtuosoGraph == null)
        {
            throw new NullPointerException("virtuosoGraph is null");
        }
        
        this.virtuosoGraph = virtuosoGraph;
        
        this.createDefaultChessMoveListDataRetriever();
    }
    
    /**
     * Creates a default {@link ChessMoveListDataRetriever} object to retrieve
     * chess move resources.
     * 
     * @return  {@link ChessOpeningDataRetriever}
     */
    public ChessOpeningDataRetriever createDefaultChessMoveListDataRetriever()
    {
        try
        {
            this.cmdr = new ChessMoveListDataRetriever(this.virtuosoGraph);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }
    
    /**
     * Determines if the given URI/IRI is a chess opening resource.
     * 
     * @param   openingURI URI/IRI to check
     * @return  true if chess opening resource else false
     */
    public boolean isOpening(String openingURI)
    {
        if (openingURI == null)
        {
            return false;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT *\nWHERE\n{\n  <")
            .append(openingURI)
            .append("> a <")
            .append(ChessRDFVocabulary.ChessOpening)
            .append(">.\n}");
        
        // DEBUG:
        //System.out.println(sb.toString());
        
        VirtuosoQueryExecution vqeS = VirtuosoQueryExecutionFactory.create(sb.toString(), this.virtuosoGraph);
        
        boolean ret = false;
        
        try
        {
            ResultSet results = vqeS.execSelect();
            
            if (results.hasNext())
            {
                ret = true;
            }
            
            vqeS.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        
        return ret;
    }
    
    /**
     * Returns a {@link ChessOpening} for a given game URI/IRI.
     * 
     * @param   openingURI
     * @return  {@link ChessGame}
     */
    public ChessOpening getOpening(String openingURI)
    {
        if (openingURI == null)
        {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("PREFIX cont:<http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#> SELECT DISTINCT ")
            .append(VARIABLE_CODE)
            .append(" ")
            .append(VARIABLE_NAME)
            .append("\nWHERE\n{  <")
            .append(openingURI)
            .append("> <")
            .append(ChessRDFVocabulary.openingCode.toString())
            .append("> ")
            .append(VARIABLE_CODE)
            .append(" .\n  <")
            .append(openingURI)
            .append("> <")
            .append(ChessRDFVocabulary.openingName.toString())
            .append("> ")
            .append(VARIABLE_NAME)
            .append(" .\n}");
        
        // DEBUG:
        //System.out.println(sb.toString());
        
        VirtuosoQueryExecution vqeS = VirtuosoQueryExecutionFactory.create(sb.toString(), this.virtuosoGraph);
        
        try
        {
            ResultSet results = vqeS.execSelect();
            
            ChessOpening.Builder cob = new ChessOpening.Builder();
            
            if (results.hasNext())
            {
                QuerySolution result = (QuerySolution) results.next();
                // DEBUG:
                //System.out.println(result.toString());
                
                Literal l_code = result.getLiteral(VARIABLE_CODE);
                String code = l_code.toString();
                
                Literal l_name = result.getLiteral(VARIABLE_NAME);
                String name = (l_name == null) ? null : l_name.toString();
                name = ("".equals(name)) ? null : name;
                
                cob.setCode(code).setName(name);
            }
            
            cob.setMoves(this.cmdr.getMoves(openingURI));
            
            vqeS.close();
            
            return cob.build();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Returns a List with {@link ChessOpening} objects for each URI/IRI found
     * in the parameter playerURIs.
     * 
     * @param   openingURI  a List of Strings with the URI/IRIs of the chess
     *                      opening resources which are to be queried
     * @return  List<{@link ChessOpening}> with all the found resources - it
     *          won't be shown which URI/IRIs couldn't return data.
     */
    public List<ChessOpening> getPlayers(List<String> openingURI)
    {
        // if parameter null than return empty list
        if (openingURI == null)
        {
            return new ArrayList<ChessOpening>();
        }
        
        ArrayList<ChessOpening> list = new ArrayList<ChessOpening>();
        
        for (String playerURI : openingURI)
        {
            ChessOpening cp = this.getOpening(playerURI);
            if (cp != null)
            {
                list.add(cp);
            }
        }
        
        return list;
    }
    
    
    
}