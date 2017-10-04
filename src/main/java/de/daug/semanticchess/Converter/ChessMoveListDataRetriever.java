/**
 * ChessMoveListDataRetriever.java
 */
package de.daug.semanticchess.Converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessMove;
import de.uni_leipzig.informatik.swp13_sc.datamodel.rdf.ChessRDFVocabulary;

/**
 * A retriever class for querying a triplestore over a VirtGraph (only
 * Virtuoso therefore). It returns for given URI/IRIs the found
 * resources and wraps it into {@link ChessMove} objects.
 * 
 * @author Erik
 */
public class ChessMoveListDataRetriever
{
    /**
     * The virtuosoGraph (Virtuoso triple store) which will be queried.
     */
    private final VirtGraph virtuosoGraph;
    
    /**
     * Internal. VARIABLE_MOVE_MOVENR
     */
    private final static String VARIABLE_MOVE_MOVENR = "?nr";
    /**
     * Internal. VARIABLE_MOVE
     */
    private final static String VARIABLE_MOVE = "?move";
    /**
     * Internal. VARIABLE_MOVE_MOVENAME
     */
    private final static String VARIABLE_MOVE_MOVENAME = "?movename";
    /**
     * Internal. VARIABLE_MOVE_FEN
     */
    private final static String VARIABLE_MOVE_FEN = "?fen";
    
    
    /**
     * Constructor. Requires the connection to the datastore (Virtuoso)
     * 
     * @param   virtuosoGraph   the triplestore which will be queried
     * @throws  Exception   if argument is null
     */
    public ChessMoveListDataRetriever(VirtGraph virtuosoGraph)
        throws Exception
    {
        if (virtuosoGraph == null)
        {
            throw new NullPointerException("virtuosoGraph is null");
        }
        
        this.virtuosoGraph = virtuosoGraph;
    }
    
    
    /**
     * Returns a list of {@link ChessMove}s for a given game URI/IRI. On error
     * it will return an empty list.
     * 
     * @param   uri URI/IRI of game or opening
     * @return  List<{@link ChessMove}>
     */
    public List<ChessMove> getMoves(String uri)
    {
        if (uri == null)
        {
            return new ArrayList<ChessMove>();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("PREFIX cont:<http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#> SELECT DISTINCT ")
            .append(VARIABLE_MOVE_MOVENAME)
            .append(' ')
            .append(VARIABLE_MOVE_MOVENR)
            .append(' ')
            .append(VARIABLE_MOVE_FEN)
            .append("\nWHERE\n{\n  <")
            .append(uri)
            .append("> <")
            .append(ChessRDFVocabulary.moves.toString())
            .append("> ")
            .append(VARIABLE_MOVE)
            .append(".\n  ")
            .append(VARIABLE_MOVE)
            .append(" <")
            .append(ChessRDFVocabulary.move.toString())
            .append("> ")
            .append(VARIABLE_MOVE_MOVENAME)
            .append(".\n  ")
            .append(VARIABLE_MOVE)
            .append(" <")
            .append(ChessRDFVocabulary.moveNr.toString())
            .append("> ")
            .append(VARIABLE_MOVE_MOVENR)
            .append(".\n  OPTIONAL\n  {\n    ")
            .append(VARIABLE_MOVE)
            .append(" <")
            .append(ChessRDFVocabulary.fen.toString())
            .append("> ")
            .append(VARIABLE_MOVE_FEN)
            .append(".\n  }\n")
            .append("}\nORDER BY ")
            .append(VARIABLE_MOVE_MOVENR);
        
        // DEBUG:
        //System.out.println(sb.toString());
        
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        
        VirtuosoQueryExecution vqeS = VirtuosoQueryExecutionFactory.create(sb.toString(), this.virtuosoGraph);
        
        try
        {
            ResultSet results = vqeS.execSelect();
            
            ChessMove lastMove = new ChessMove.Builder().setNr(-2).build();
            
            while (results.hasNext())
            {
                QuerySolution result = (QuerySolution) results.next();
                
                Literal l_name = result.getLiteral(VARIABLE_MOVE_MOVENAME);
                String name = l_name.toString();
                
                Literal l_nr = result.getLiteral(VARIABLE_MOVE_MOVENR);
                int nr = l_nr.getInt();
                
                Literal l_fen = result.getLiteral(VARIABLE_MOVE_FEN);
                String fen = (l_fen == null) ? null : l_fen.toString();
                fen = ("".equals(fen)) ? null : fen;
                
                // create ChessMove object and set data.
                // check if double - that means a move with the same nr but different data
                ChessMove move = new ChessMove.Builder().setMove(name).setNr(nr).setFEN(fen).build();
                if (move.getNr() == lastMove.getNr())
                {
                    // second move has FEN
                    if (lastMove.getFEN() == null && move.getFEN() != null)
                    {
                        moves.remove(moves.size() - 1);
                    }
                }
                moves.add(move);
                lastMove = move;
                
                // DEBUG:
                // System.out.println(lastMove);
            }
            
            vqeS.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<ChessMove>();
        }
        
        return moves;
    }
    
    
}
