/**
 * PGNToChessDataModelConverter.java
 * SOURCE: http://pcai042.informatik.uni-leipzig.de/swp/SWP-13/swp13-sc/
 */

package de.daug.semanticchess.Converter.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.Move;

import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessGame;
import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessMove;
import de.uni_leipzig.informatik.swp13_sc.datamodel.ChessPlayer;
import de.uni_leipzig.informatik.swp13_sc.datamodel.pgn.ChessPGNVocabulary;
import de.uni_leipzig.informatik.swp13_sc.util.FileUtils;

/**
 * Converter class.<br />
 * Converts PGN Files (text input streams) into the internal chess data format
 * and then into RDF.<br />
 * 
 *
 * @author Erik
 */
public class PGNToChessDataModelConverter
{
    
    /**
     * The inputFilename.
     */
    private String inputFilename;
    /**
     * The reader used for the input file.
     */
    private BufferedReader reader;
    /**
     * isParsing
     */
    private volatile boolean isParsing;
    /**
     * finishedParsing
     */
    private volatile boolean finishedParsing;
    /**
     * finishedInput
     */
    private volatile boolean finishedInput;
    /**
     * List of all converted chess games. Internal chess data format.
     */
    private List<ChessGame> games;
    /**
     * complete numberOfGames
     */
    private AtomicInteger numberOfGames;
    /**
     * total number of invalid (unconverted) games
     */
    private AtomicInteger numberOfInvalidGames;
    /**
     * temporary file for storing invalid PGNs
     */
    private File invalidPGNsOutputFile;
    /**
     * Writer for invalid PGN chess games.
     */
    private BufferedWriter invalidPGNWriter;
    
    /**
     * Used to check that the last input file is only opened once.
     * There occurred an infinite loop if the last line of a PGN-File
     * was empty (an additional last line!) and the file was reopened.
     */
    private String lastInputFilename;
    
    // ------------------------------------------------------------------------
    
    /**
     * Internal. Empty String. Not null.
     */
    private final static String EMPTY = "";
    
    /**
     * ALL_GAMES indicates that all available games should be processed.
     */
    public final static int ALL_GAMES = -1;
    
    // ------------------------------------------------------------------------
    
    /**
     * Regex Pattern for a single chess move in algebraic notation.
     */
    private static Pattern pattern_single_move = Pattern.compile(ChessPGNVocabulary.regex_move_single);
        
    /**
     * Regex Pattern for the start of a meta data entry line.
     */
    @SuppressWarnings("unused")
    private static Pattern pattern_meta_start_line =
            Pattern.compile(ChessPGNVocabulary.regex_meta_start_line);//, Pattern.MULTILINE);
    
    /**
     * Regex Pattern for a single meta data entry line.
     */
    private static Pattern pattern_meta = Pattern.compile(ChessPGNVocabulary.regex_meta);
    
    /**
     * Regex pattern to search for commas.
     */
    private static Pattern pattern_name_comma = Pattern.compile(
            ChessPGNVocabulary.regex_player_name_comma);
    /**
     * Regex pattern to search for braces.
     */
    private static Pattern pattern_name_braces = Pattern.compile(
            ChessPGNVocabulary.regex_player_name_braces);
    private static Pattern pattern_name_spaces = Pattern.compile(
            ChessPGNVocabulary.regex_player_name_spaces);
    
    // ------------------------------------------------------------------------
    
    /**
     * Creates a new Converter. Nothing more.
     * 
     * @param   inputFilename   Input filename used later while parsing.
     */
    public PGNToChessDataModelConverter(String inputFilename)
    {
        this();
        
        // no validation!
        this.inputFilename = inputFilename;
    }
    
    /**
     * Standard Constructor.
     */
    public PGNToChessDataModelConverter()
    {
        this.isParsing = false;
        this.finishedParsing = false;
        this.finishedInput = false;
        
        this.games = new ArrayList<ChessGame>();
        this.numberOfGames = new AtomicInteger();
        
        this.numberOfInvalidGames = new AtomicInteger();
    }
    
    /**
     * Returns true if the converter parsed the complete input.
     * 
     * @return  true if finished else false
     */
    public boolean finishedInputFile()
    {
        return this.finishedInput;
    }
    
    /**
     * Returns true if the converter finished parsing its given part.
     * The input file may be longer and not completely finished - only
     * the parsing method!
     * 
     * @return  true if finished.
     */
    public boolean finishedParsing()
    {
        return this.finishedParsing;
    }
    
    /**
     * Return the complete number of actual parsed games.
     * 
     * @return  int
     */
    public int numberOfParsedGames()
    {
        return numberOfGames.get();
    }
    
    /**
     * Return the complete number of actual invalid chess games.
     * 
     * @return  int
     */
    public int numberOfInvalidGames()
    {
        return numberOfInvalidGames.get();
    }
    
    /**
     * Returns the number of unconverted games still in memory.
     * 
     * @return  int
     */
    public int numberUnconvertedGames()
    {
        if (this.games != null)
        {
            return this.numberOfParsedGames() - this.games.size();
        }
        return numberOfParsedGames();
    }
    
    /**
     * Returns the absolute path name of the output file for invalid PGN games.
     * Will return null if unknown pathname.
     * 
     * @return  String with absolute pathname of invalid PGN games or null on
     *          error
     */
    public String getFileOfInvalidGames()
    {
        if (this.invalidPGNsOutputFile == null)
        {
            return null;
        }
        
        return this.invalidPGNsOutputFile.getAbsolutePath();
    }
    
    
    /**
     * Sets the input filename.
     * 
     * @param   inputFilename   Filename of input file
     */
    public void setInputFilename(String inputFilename)
    {
        this.inputFilename = inputFilename;
    }
    
    /**
     * Sets the input reader for this parser. It takes the inputStream and
     * creates a new reader from it.
     * 
     * @param   inputStream     InputStream to read from
     */
    public void setInputStream(InputStream inputStream)
    {
        this.setInputReader(FileUtils.openReader(inputStream));
    }
    
    /**
     * Sets the input reader for this parser.
     * 
     * @param   reader  BufferedReader to read from.
     * @return  true if successful else false (can't read, no outputfilename
     *          or is already working)
     */
    public boolean setInputReader(BufferedReader reader)
    {
        // check if working
        if (this.isParsing)
        {
            return false;
        }
        
        // check if readable
        try
        {
            if (! reader.ready())
            {
                this.reader = null;
                return false;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        
        this.reader = reader;
        return true;
    }
    
    /**
     * Sets the output stream for invalid PGN files to capture. It will automatically
     * create a new writer from it.
     * 
     * @param   outputStream    OutputStream to write into
     */
    public void setInvalidPGNOutputStream(OutputStream outputStream)
    {
        this.setInvalidPGNOutputWriter(FileUtils.openWriter(outputStream));
    }
    
    /**
     * Sets the invalid PGN game output writer for this parser.
     * 
     * @param   writer  BufferedWriter to write with
     * @return  true if successful assigned
     */
    public boolean setInvalidPGNOutputWriter(BufferedWriter writer)
    {
        // is working
        //if (this.isParsing)
        //{
        //    return false;
        //}
        
        this.invalidPGNWriter = writer;
        
        return true;
    }
    
    /**
     * Creates a temporary (!) file for storing invalid chess games. Default.
     * 
     * @param   filename    Filename of input file / reference file
     * @return  false on error
     */
    public boolean createTemporaryOutputWriterForInvalidPGNs(String filename)
    {
        if (this.invalidPGNsOutputFile != null && this.invalidPGNsOutputFile.exists())
        {
            if (this.invalidPGNWriter == null)
            {
                try
                {
                    this.setInvalidPGNOutputStream(new FileOutputStream(this.invalidPGNsOutputFile));
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                    return false;
                }
                //return true;
            }
            //else
            // has a writer already
        }
        else
        {
            // TODO: use for directory?
            File tf;
            
            // create filename
            if (filename == null)
            {
                filename = "InvalidGames_";
            }
            else
            {
                tf = new File(this.inputFilename);
                filename = tf.getName();
                
                if (filename.indexOf(".pgn") != -1)
                {
                    filename = filename.substring(0, filename.indexOf(".pgn"));
                }
                filename += "_";
            }
            
            // create new temp file
            try
            {
                this.invalidPGNsOutputFile = File.createTempFile(filename, ".pgn", null);
                this.invalidPGNsOutputFile.deleteOnExit();
                this.setInvalidPGNOutputStream(new FileOutputStream(this.invalidPGNsOutputFile));
                
                //System.out.format("  Created new temp file \"%s\" for invalid PGN games.%n",
                //        this.invalidPGNsOutputFile.getPath());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Parses the previously given input file to the internal chess data format.
     * Stops after count games. Can continue an old session if reader still open.
     * 
     * @param   count   Count chess games to parse or all if set to -1
     * @return  true if successful, false for error
     */
    public boolean parse(int count)
    {
        this.finishedParsing = false;
        this.isParsing = true;
        
        // determine if continuing file or new
        try
        {
            if (reader == null || ! reader.ready())
            {
                // new file if not opened before
                //System.out.println("[DEBUG] Opened new input file.");
                if ((this.lastInputFilename == null) ||
                        (! this.lastInputFilename.equalsIgnoreCase(this.inputFilename)))
                {
                    this.lastInputFilename = this.inputFilename;
                    reader = FileUtils.openReader(FileUtils.openInputStream(this.inputFilename));
                }
            }
            
            // create file for invalid games if not existing
            createTemporaryOutputWriterForInvalidPGNs(this.inputFilename);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // check if valid reader
        if (reader == null)
        {
            return false;
        }
        
        String line = null;
        boolean inMoves = false;
        boolean outside = true;
        int gameCount = 0;
        List<String> metaLines = new ArrayList<String>();
        List<String> moveLines = new ArrayList<String>();
        
        // start parse
        while (true)
        {
            // get next line
            try
            {
                line = reader.readLine();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                // break;
                return false;
            }
            // check if end
            if (line == null)
            {
                this.finishedInput = true;
                
                // needed for last game
                if (inMoves) {
                    ChessGame cg = parseGame(metaLines, moveLines);
                    if (cg != null)
                    {
                        this.games.add(cg);
                        this.numberOfGames.incrementAndGet();
                        gameCount ++;
                    }
                    else
                    {
                        this.numberOfInvalidGames.incrementAndGet();
                        
                        // output game
                        writePGN(metaLines, moveLines);
                    }
                    metaLines = new ArrayList<String>();
                    moveLines = new ArrayList<String>();
                }
                
                break;
            }
            
            // sort lines and separate games
            if (EMPTY.equals(line))
            {
                // -- separator lines --
                if (outside)
                {
                    continue;
                }
                else
                {
                    outside = true;
                    
                    // start actual converting/parsing of meta, moves
                    if (inMoves)
                    {
                        inMoves = false;
                        
                        // start thread?
                        ChessGame cg = parseGame(metaLines, moveLines);
                        if (cg != null)
                        {
                            this.games.add(cg);
                            this.numberOfGames.incrementAndGet();
                            gameCount ++;
                        }
                        else
                        {
                            this.numberOfInvalidGames.incrementAndGet();
                            
                            // output invalid game
                            writePGN(metaLines, moveLines);
                        }
                        
                        // check for end
                        if ((count != ALL_GAMES) && (gameCount >= count))
                        {
                            break;
                        }
                        
                        // fresh new line lists
                        metaLines = new ArrayList<String>();
                        moveLines = new ArrayList<String>();
                    }
                }
            }
            else if (line.charAt(0) == '[')
            //else if (pattern_meta_start_line.matcher(line).lookingAt())
            {
                // add meta data lines to list
                metaLines.add(line);
            }
            else
            {
                // add moves to list
                if (outside)
                {
                    outside = false;
                    inMoves = true;
                }
                moveLines.add(line);
            }
            
        }
        
        // end of parsing
        if (this.finishedInput)
        {
            try
            {
                reader.close();
                reader = null;
                
                if (this.invalidPGNWriter != null)
                {
                    invalidPGNWriter.close();
                    invalidPGNWriter = null;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
        
        this.isParsing = false;
        this.finishedParsing = true;
        return true;
    }
    
    /**
     * Parses the whole input stream.
     * 
     * @return  true if successful
     */
    public boolean parse()
    {
        return this.parse(ALL_GAMES);
    }
    
    /**
     * Parses the given lines into a {@link ChessGame}.<br />
     * Called for each game.
     * 
     * @param   metaLines   meta data lines
     * @param   moveLines   move list lines
     * @return  ChessGame if successful else null.
     */
    protected ChessGame parseGame(List<String> metaLines, List<String> moveLines)
    {
        // input validation
        if (metaLines == null || metaLines.size() < 7)
        {
            return null;
        }
        if (moveLines == null || moveLines.size() == 0)
        {
            return null;
        }
        
        // new chess game
        ChessGame.Builder cgb = new ChessGame.Builder();
        
        // --------------------------------------------------------------------
        
        // parse meta data entries
        for (String line : metaLines)
        {
            Matcher meta = pattern_meta.matcher(line);
            
            // get values
            if (! meta.find())
            {
                // log line
                continue;
            }
            try
            {
                String key = meta.group(1);
                String value = meta.group(2);
                
                // remove empty values
                if (EMPTY.equalsIgnoreCase(value.trim()))
                {
                    //System.out.println("WARN: Empty value for key " + key +
                    //        " in Game " + (numberOfGames.get() + 1));
                    continue;
                }
                
                // switch(key) only with JRE 1.7 !
                if (ChessPGNVocabulary.Meta_Key_White.equals(key))
                {
                    //System.out.println("Name Normalization: \"" + value + "\" -> \""
                    //        + normalizeName(value) + "\"");
                    cgb.setWhitePlayer(new ChessPlayer.Builder().setName(normalizeName(value)).build());
                }
                else if (ChessPGNVocabulary.Meta_Key_Black.equals(key))
                {
                    //System.out.println("Name Normalization: \"" + value + "\" -> \""
                    //        + normalizeName(value) + "\"");
                    cgb.setBlackPlayer(new ChessPlayer.Builder().setName(normalizeName(value)).build());
                }
                else if (ChessPGNVocabulary.Meta_Key_Date.equals(key))
                {
                    cgb.setDate(value);
                }
                else if (ChessPGNVocabulary.Meta_Key_Round.equals(key))
                {
                    cgb.setRound(value);
                }
                else if (ChessPGNVocabulary.Meta_Key_Result.equals(key))
                {
                    cgb.setResult(value);
                }
                else if (ChessPGNVocabulary.Meta_Key_Site.equals(key))
                {
                    cgb.setSite(value);
                }
                else if (ChessPGNVocabulary.Meta_Key_Event.equals(key))
                {
                    cgb.setEvent(value);
                }
                else
                {
                    cgb.addMetaData(key, value);
                }         
            }
            catch (IllegalStateException e)
            {
                e.printStackTrace();
            }
            catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }
        
        // --------------------------------------------------------------------
        
        //ChessBoard my_cb = new ChessBoard();
        //boolean first = true;
        Board cb = new Board();
        cb.startPosition();
        
        // parse moves
        int nr = 0;
        for (String line : moveLines)
        {
            Matcher move = pattern_single_move.matcher(line);
            while (move.find())
            {
                nr ++;
                ChessMove.Builder cmb = new ChessMove.Builder();
                
                String m = move.group(1);
                String comment = move.group(10);
                
                cmb.setNr(nr).setMove(m).setComment(comment);
                
                try
                {
                    int moveInt = Move.getFromString(cb, m, true);
                    //if (! cb.move(m))
                    if (! cb.doMove(moveInt, true))
                    {
                        System.err.println("WARN: move \"" + m + "\" -> \""
                                + Move.toStringExt(moveInt) +
                                "\" in <Game " + (numberOfGames.get() + 1)
                                + ">");
                        
                        return null;
                    }
                    
                    String fen = cb.getFen();
                    fen = fen.substring(0, fen.indexOf(' '));
                    cmb.setFEN(fen);
                    
                    // Compare FEN generation
                    //if (first)
                    //{
                    //    my_cb.move(m);
                    //    if (! fen.equals(my_cb.computeFEN()))
                    //    {
                    //        System.out.println("WARN: Different FEN in game " +
                    //                (numberOfGames.get() + 1) + ", move " + nr);
                    //        first = false;
                    //    }
                    //}
                }
                catch (Exception e)
                {
                    // IllegalStateException
                    // IndexOutOfBoundsException
                    System.out.println("ERROR in <Game " +
                            (numberOfGames.get() + 1) + ">.");
                    e.printStackTrace();
                    return null;
                }
                
                cgb.addMove(cmb.build());
            }
        }
        
        return cgb.build();
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Returns count parsed games from the internal list and optionally
     * removes them after returning (to delete all references to them).
     * 
     * @param   count   Number of games to return.
     * @param   remove  true if they should be removed.
     * @return  List<{@link
     *          de.uni_leipzig.informatik.swp13_sc.datamodel.ChessGame}>
     */
    public List<ChessGame> getGames(int count, boolean remove)
    {
        if (this.games == null || this.games.isEmpty())
        {
            return null;
        }
        
        // get count list elements
        List<ChessGame> gms = new ArrayList<ChessGame>();
        if (count != ALL_GAMES)
        {
            if (this.games.size() < count)
            {
                count = this.games.size();
            }
            try
            {
                List<ChessGame> part = this.games.subList(0, count);
                gms.addAll(part);
                if (remove)
                {
                    part.clear();
                }
            }
            catch (Exception e)
            {
                // IndexOutOfBoundsException
                // IllegalArgumentException
                // NullPointerException
                // ClassCastException
                // UnsupportedOperationException
                e.printStackTrace();
            }
        }
        else
        {
            gms.addAll(this.games);
            if (remove)
            {
                this.games.clear();
            }
        }
        
        return gms;
    }
    
    /**
     * Returns all the parsed games and removes them from the internal list.
     * 
     * @return  List<{@link
     *          de.uni_leipzig.informatik.swp13_sc.datamodel.ChessGame}>
     */
    public List<ChessGame> getGames()
    {
        return this.getGames(ALL_GAMES, true);
    }
    
    /**
     * Returns all the parsed games and optionally removes them form the list.
     * 
     * @param   remove  If true all the internally saved games are cleared.
     * @return  List<{@link
     *          de.uni_leipzig.informatik.swp13_sc.datamodel.ChessGame}>
     */
    public List<ChessGame> getGames(boolean remove)
    {
        return this.getGames(ALL_GAMES, remove);
    }
    
    
    // ------------------------------------------------------------------------
    
    /**
     * Will switch surname and first. Removes spaces and braces.
     * 
     * @param   name    Name to process
     * @return  normalized name
     */
    private final static String normalizeName(String name)
    {
        // remove braces
        name = pattern_name_braces.matcher(name).replaceAll("");
        
        Matcher n = pattern_name_comma.matcher(name);
        
        // if too much commas
        if (! n.find())
        {
            return name.trim();
        }
        
        // concat name parts
        name = n.group(3);
        name = (name == null) ? "" : name;        
        name += " " + n.group(1);
        
        // remove spaces
        return pattern_name_spaces.matcher(name).replaceAll(" ").trim();
    }
    
    /**
     * Internal. Writes the parsed lines (...) into the invalidPGNWriter
     * object.
     * 
     * @param   meta    PGN meta data
     * @param   moves   PGN moves
     * @return  true if no error
     */
    private boolean writePGN(List<String> meta, List<String> moves)
    {
        if (meta == null || moves == null)
        {
            return false;
        }
        
        if (invalidPGNWriter == null)
        {
            return false;
        }
        
        try
        {
            for (String line : meta)
            {
                invalidPGNWriter.write(line);
                invalidPGNWriter.newLine();
            }
            invalidPGNWriter.newLine();
            for (String line : moves)
            {
                invalidPGNWriter.write(line);
                invalidPGNWriter.newLine();
            }
            invalidPGNWriter.newLine();
            
            invalidPGNWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
        
}
