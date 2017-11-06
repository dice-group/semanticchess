/**
 * ChessDataModelToRDFConverter.java
 * SOURCE: http://pcai042.informatik.uni-leipzig.de/swp/SWP-13/swp13-sc/
 */

package de.daug.semanticchess.Converter.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessGame;
import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessMove;
import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessOpening;
import de.uni_leipzig.informatik.swp13_sc.datamodel.rdf.ChessRDFVocabulary;
import de.uni_leipzig.informatik.swp13_sc.util.FileUtils;

/**
 * Converter class.<br />
 * Converts the Chess Datamodel classes to their RDF representation.
 *
 * @author Erik
 */
public class ChessDataModelToRDFConverter
{
    
    /**
     * Possible Output-Formats for Converter {@link
     * ChessDataModelToRDFConverter}
     *
     * @author Erik
     */
    public static enum OutputFormats
    {
        /**
         * TURTLE
         */
        TURTLE("TURTLE", "ttl"),
        /**
         * NTRIPEL
         */
        NTRIPEL("N-TRIPLE", "nt"),
        /**
         * RDF_XML
         */
        RDF_XML("RDF/XML", "rdf"),
        /**
         * RDF_XML_PRETTY
         */
        RDF_XML_PRETTY("RDF/XML-ABBREV", "rdf");
        
        /**
         * Internal Jena Format String.
         */
        private String format;        
        /**
         * Internal Jena File Extension String.
         */
        private String extension;
        
        /**
         * Internal. Constructor.
         * 
         * @param   format      Format in Jena
         * @param   extension   File extension
         */
        private OutputFormats(String format, String extension)
        {
            this.format = format;
            this.extension = extension;
        }
        
        /**
         * Returns the File-Extension.
         * 
         * @return  String
         */
        public String getExtension()
        {
            return this.extension;
        }
        
        /**
         * Returns the Output-Format.
         * 
         * @return  String
         */
        public String getFormat()
        {
            return this.format;
        }        
    }
    
    
    /**
     * Internal model for storing statements/tripel.
     */
    private Model model;
    /**
     * Internal. List of all game names.
     */
    //private Set<String> convertedGames;
    private Map<String, Integer> convertedGames;
    /**
     * Internal. List of all opening resource names.
     */
    private Map<String, Integer> convertedOpenings;
    /**
     * Internal. Seperator char for URI names.
     */
    private static char separator = '_';
    /**
     * Internal. Empty String. Not null.
     */
    private final static String EMPTY = "";
    
    /**
     * Basic Constructor
     */
    public ChessDataModelToRDFConverter()
    {
        // create store
        model = createModel();
        
        convertedGames = new HashMap<String, Integer>();
        convertedOpenings = new HashMap<String, Integer>();
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Returns a Set of all converted game names.
     * 
     * @return  List<String>
     */
    public List<String> getConvertedGameNames()
    {
        List<String> list = new ArrayList<String>();
        
        for (String key : this.convertedGames.keySet())
        {
            int n = this.convertedGames.get(key);
            for (int i = 1; i <= n; i ++)
            {
                String gameName = key;
                if (i > 1)
                {
                    gameName += separator + "1";
                }
                list.add(gameName);
            }
        }
        
        return list;
    }
    
    /**
     * Returns a Set of all converted opening names.
     * 
     * @return  List<String>
     */
    public List<String> getConvertedOpeningNames()
    {
        List<String> list = new ArrayList<String>();
        
        for (String key : this.convertedOpenings.keySet())
        {
            int n = this.convertedOpenings.get(key);
            for (int i = 1; i <= n; i ++)
            {
                String openingName = key;
                if (i > 1)
                {
                    openingName += separator + "1";
                }
                list.add(openingName);
            }
        }
        
        return list;
    }
    
    /**
     * Gets the number of created Statements (or Triples - internal).
     * 
     * @return  long
     */
    public long getStatementCount()
    {
        if (! this.model.isClosed())
        {
            return this.model.size();
        }
        return 0;
    }
    
    /**
     * Returns the internal Graph for the used Model. A new Model will be
     * generated, so no changes can result from working with the Graph.
     * 
     * @return  Graph (Jena)
     */
    public Graph getTripelGraph()
    {
        Graph g = null;
        if (! this.model.isClosed())
        {
            g = this.model.getGraph();
            //this.model.close();
            // TODO: create new Model necessary?
            this.model = createModel();
        }
        
        return g;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Creates a default Model for storing the RDF Statements.
     * 
     * @return  Model
     */
    protected static Model createModel()
    {
        // create store
        Model model = ModelFactory.createDefaultModel();
        // add namespace prefixes
        model.setNsPrefix(ChessRDFVocabulary.getOntologyPrefixName(),
                ChessRDFVocabulary.getURI());
        model.setNsPrefix(ChessRDFVocabulary.getResourcePrefixName(),
                ChessRDFVocabulary.getResourceURI());
        model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        
        return model;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Takes a {@link ChessGame} and converts it (and its members) to RDF data.<br />
     * Returns if values are null!
     * 
     * @param   game    {@link ChessGame} to convert
     * @return  true if successful else false
     */
    public boolean convert(ChessGame game)
    {
        // check for close?
        
        // check values
        if (game == null)
        {
            return false;
        }
        if (game.getBlackPlayer() == null)
        {
            return false;
        }
        if (game.getWhitePlayer() == null)
        {
            return false;
        }
        if (game.getMoves() == null)
        {
            return false;
        }
        
        // generate normalized player names
        String whitePlayer = getNormalizedString(game.getWhitePlayer().getName());
        String blackPlayer = getNormalizedString(game.getBlackPlayer().getName());
        
        // generate player resources
        Resource r_wPlayer = model.createResource(ChessRDFVocabulary
                .getResourceURI() + whitePlayer, ChessRDFVocabulary.ChessPlayer);
        Resource r_bPlayer = model.createResource(ChessRDFVocabulary
                .getResourceURI() + blackPlayer, ChessRDFVocabulary.ChessPlayer);
        
        // add data to players
        r_bPlayer.addProperty(ChessRDFVocabulary.name, game.getBlackPlayer().getName());
        r_wPlayer.addProperty(ChessRDFVocabulary.name, game.getWhitePlayer().getName());
        
        // --------------------------------------------------------------------
        // get date
        String date = game.getDate();
        if (date == null)
        {
            date = "_default_date_";
        }
        
        // generate game
        String gameName = getUniqueGameName(blackPlayer, whitePlayer, date);
        Resource r_game = model.createResource(ChessRDFVocabulary
                .getResourceURI() + gameName, ChessRDFVocabulary.ChessGame);
       
        // add both players to game
        model.add(r_game, ChessRDFVocabulary.blackPlayer, r_bPlayer)
            .add(r_game, ChessRDFVocabulary.whitePlayer, r_wPlayer);
        
        // add metadata to game
        for (String metaKey : game.getMetaData().keySet())
        {
            String metaValue = game.getMetaValue(metaKey);
            
            // skip invalid or empty values to reduce size of output
            if (metaValue == null || EMPTY.equals(metaValue))
            {
                continue;
            }
            // TODO: Think of a better way!
            r_game.addProperty(model.createProperty(ChessRDFVocabulary.getURI()
                    + metaKey), metaValue);
        }
        
        // --------------------------------------------------------------------
        // add moves to game
        int nr = 0;
        for (ChessMove m : game.getMoves())
        {
            // convert & add move
            nr ++;
            Resource r_move = model.createResource(ChessRDFVocabulary.ChessMove)
                    .addProperty(ChessRDFVocabulary.move, m.getMove())
                    .addProperty(ChessRDFVocabulary.moveNr, "" + nr,
                            XSDDatatype.XSDnonNegativeInteger);
            model.add(r_game, ChessRDFVocabulary.moves, r_move);
            if (m.hasComment() && m.getComment() != null)
            {
                r_move.addProperty(ChessRDFVocabulary.comment, m.getComment());
            }
            if (m.getFEN() != null)
            {
                r_move.addProperty(ChessRDFVocabulary.fen, m.getFEN());
            }
            // ignore to reduce size ... interferes with querying??
            //else
            //{
            //	r_move.addProperty(ChessRDFVocabulary.fen, ""); // add empty?
            //}
        }
        // finished!
        return true;
    }
    
    /**
     * Converts the whole list of {@link ChessGame}s.
     * 
     * @param   games   List<{@link ChessGame}>
     * @return  true if successful else false
     */
    public boolean convert(List<ChessGame> games)
    {
        if (games == null)
        {
            return false;
        }
        if (this.model.isClosed())
        {
            return false;
        }
        
        for (ChessGame g : games)
        {
            // to add all games fail-safe
            try
            {
                convert(g);
            }
            // TODO: differentiate exceptions
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return true;
    }
    
    /**
     * Converts a subset of the list games.
     * 
     * @param   games   List to convert.
     * @param   count   Number of items to convert before stopping
     * @return  true if successful else false
     */
    public boolean convert(List<ChessGame> games, int count)
    {
        if (games == null)
        {
            return false;
        }
        if (this.model.isClosed())
        {
            return false;
        }
        
        for (int i = 0; (i < count) && (! games.isEmpty()); i ++)
        {
            try
            {
                convert(games.remove(0));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return true;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Takes a {@link ChessOpening} and converts it (and its members) to RDF data.<br />
     * Returns if values are null!
     * 
     * @param   opening    {@link ChessOpening} to convert
     * @return  true if successful else false
     */
    public boolean convert(ChessOpening opening)
    {
        // check for close?
        
        // check values
        if (opening == null)
        {
            return false;
        }
        if (opening.getCode() == null || EMPTY.equals(opening.getCode()))
        {
            return false;
        }
        if (opening.getName() == null)
        {
            return false;
        }
        if (opening.getMoves() == null)
        {
            return false;
        }
        
        // --------------------------------------------------------------------
        
        // generate game for opening
        String openingName = getUniqueOpeningName(opening.getCode(), opening.getName());
        //System.out.println(openingName);
        // create resource object in model
        Resource r_opening = model.createResource(ChessRDFVocabulary
                .getResourceURI() + openingName, ChessRDFVocabulary.ChessOpening);
        
        // add code name
        r_opening.addProperty(ChessRDFVocabulary.openingCode, opening.getCode());
        
        // add normal name if not ""
        if (! EMPTY.equals(opening.getName()))
        {
            r_opening.addProperty(ChessRDFVocabulary.openingName, opening.getName());
        }
        
        // --------------------------------------------------------------------
        // add moves to opening
        int nr = 0;
        for (ChessMove m : opening.getMoves())
        {
            // convert & add move
            nr ++;
            Resource r_move = model.createResource(ChessRDFVocabulary.ChessMove)
                    .addProperty(ChessRDFVocabulary.move, m.getMove())
                    .addProperty(ChessRDFVocabulary.moveNr, "" + nr,
                            XSDDatatype.XSDnonNegativeInteger);
            model.add(r_opening, ChessRDFVocabulary.moves, r_move);
            if (m.hasComment() && m.getComment() != null)
            {
                r_move.addProperty(ChessRDFVocabulary.comment, m.getComment());
            }
            if (m.getFEN() != null)
            {
                r_move.addProperty(ChessRDFVocabulary.fen, m.getFEN());
            }
            // ignore to reduce size ... interferes with querying??
            //else
            //{
            //  r_move.addProperty(ChessRDFVocabulary.fen, ""); // add empty?
            //}
        }
        // finished!
        return true;
    }
    
    /**
     * Converts the whole list of {@link ChessOpening}s.
     * 
     * @param   openings   List<{@link ChessOpening}>
     * @return  true if successful else false
     */
    public boolean convertOpenings(List<ChessOpening> openings)
    {
        if (openings == null)
        {
            return false;
        }
        if (this.model.isClosed())
        {
            return false;
        }
        
        for (ChessOpening co : openings)
        {
            // to add all openings fail-safe
            try
            {
                convert(co);
            }
            // TODO: differentiate exceptions
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return true;
    }
    
    /**
     * Converts a subset of the list openings.
     * 
     * @param   openings   List to convert.
     * @param   count   Number of items to convert before stopping
     * @return  true if successful else false
     */
    public boolean convertOpenings(List<ChessOpening> openings, int count)
    {
        if (openings == null)
        {
            return false;
        }
        if (this.model.isClosed())
        {
            return false;
        }
        
        for (int i = 0; (i < count) && (! openings.isEmpty()); i ++)
        {
            try
            {
                convert(openings.remove(0));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return true;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Internal method.<br />
     * Flushes the model into the OutputStream output with the format.
     * 
     * @param   output  OutputStream used.
     * @param   format  Format of Output
     * @return  true if successful else false
     */
    protected boolean flushToStream(OutputStream output, OutputFormats format)
    {
        if (this.model.isClosed())
        {
            return false;
        }
        
        if (output == null)
        {
            return false;
        }
        
        String f = null;
        if (format != null)
        {
            f = format.getFormat();
        }
        
        model.write(output, f, null).close();
        
        // reuse this class -> need fresh model
        
        //System.gc();
        
        model = ChessDataModelToRDFConverter.createModel();
        
        return true;
    }
    
    /**
     * Writes the converted RDF data to the OutputStream os in the format.
     * 
     * @param   os      Stream to write into
     * @param   format  Outputformat
     * @return  true if successful, false on error
     */
    public boolean write(OutputStream os, OutputFormats format)
    {
        // check output stream
        if (os == null)
        {
            return false;
        }
        
        // flush all
        return this.flushToStream(os, format);
    }
    
    /**
     * Writes the RDF data to the specified OutputStream os.<br />
     * Uses the standard format {@link OutputFormats#TURTLE}
     * 
     * @param   os  Stream to write into
     * @return  true if successful
     */
    public boolean write(OutputStream os)
    {
        return this.write(os, OutputFormats.TURTLE);
    }
    
    /**
     * Writes the RDF data to the specified outputFilename. Old file data will
     * be overwritten and therefore lost. Stream is closed eventually.
     * 
     * @param   outputFilename  File to write into.
     * @return  true if successful else false
     */
    public boolean write(String outputFilename)
    {
        //OutputStream os = FileUtils.openOutputStream(outputFilename);
        OutputStream os = FileUtils.openGZIPOutputStream(outputFilename);
        
        // write data in default format
        this.write(os);
        
        try
        {
            os.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    
    /**
     * Writes all the converted chess game names into the stream.
     * 
     * @param   outputStream    OutputStream to write into
     * @return  true if successful else false
     */
    public boolean writeConvertedGameNames(OutputStream outputStream)
    {
        if (this.convertedGames.size() == 0)
        {
            return false;
        }
        
        BufferedWriter bw = FileUtils.openWriter(outputStream);
        if (bw == null)
        {
            return false;
        }
        
        for (String key : this.convertedGames.keySet())
        {
            int n = this.convertedGames.get(key);
            for (int i = 1; i <= n; i ++)
            {
                try
                {
                    String gameName = key;
                    if (i > 1)
                    {
                        gameName += separator + "" + i;
                    }
                    
                    bw.write(gameName);
                    bw.newLine();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        try
        {
            bw.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return true;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Normalizes the String str. For use as URI.<br />
     * All non-alpha-numeric characters are turned to '_'. The resulting String
     * contains only letters, numbers and the character '_'.
     * 
     * @param   str     String to normalize
     * @return  normalized String
     */
    protected static String getNormalizedString(String str)
    {
        if (str == null)
        {
            return "_null_";
        }

        // Use Regex Pattern?
        int specialCount = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i ++)
        {
            char c = chars[i];
            if (Character.isLetterOrDigit(c))
            {
                int number = c;
                if (number > 0x7a)
                {
                    chars[i] = separator;
                    specialCount ++;
                }
            }
            else
            {
                chars[i] = separator;
            }
        }
        return (new String(chars)) + ((specialCount > 0) ? separator + "" + specialCount : "");
    }
    
    /**
     * Generates a new unique game identifier.
     * 
     * @param   black   black player's name
     * @param   white   white player's name
     * @param   date    date of game
     * @return  String with unique ID
     */
    protected String getUniqueGameName(String black, String white, String date)
    {
        String key = getNormalizedString(black) + separator +
                getNormalizedString(white) + separator +
                getNormalizedString(date);
        String game;
        if (convertedGames.containsKey(key))
        {
            int nr = convertedGames.get(key);
            nr ++;
            game = key + separator + "" + nr;
            this.convertedGames.put(key, nr);
        }
        else
        {
            game = key;
            this.convertedGames.put(key, 1);
        }
        
        return game;
    }
    
    /**
     * Generates a new unique opening identifier.
     * 
     * @param   code  code name of opening
     * @param   name  name of opening (ignored)
     * @return  String with unique ID
     */
    protected String getUniqueOpeningName(String code, String name)
    {
        //String key = code + separator + getNormalizedString(name);
        String key = code;
        
        String opening;
        if (convertedOpenings.containsKey(key))
        {
            int nr = convertedOpenings.get(key);
            nr ++;
            opening = key + separator + "" + nr;
            this.convertedOpenings.put(key, nr);
        }
        else
        {
            opening = key;
            this.convertedOpenings.put(key, 1);
        }
        
        // DEBUG:
        //System.out.println(code + " -> " + opening);
        
        return opening;
    }
}