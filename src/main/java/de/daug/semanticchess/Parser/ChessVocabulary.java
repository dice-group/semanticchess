package de.daug.semanticchess.Parser;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

/**
 * Simple chess vocabulary: assign a term to a topic
 */
public class ChessVocabulary {

	// Map for "synonyms"
	public Map<String, ArrayList<String>> PROPERTIES = new HashMap<String, ArrayList<String>>();
	// inversed Map
	public Map<String, String> INVERSED_PROPERTIES = new HashMap<String, String>();

	/**
	 * constructor fill the maps
	 */
	public ChessVocabulary() {
		fillProperties();
		inverseProperties();
	}

	/**
	 * a list: the terms assigned to a topic
	 */
	public void fillProperties() {
		ArrayList<String> blackProperty = new ArrayList<String>();
		blackProperty.add("black");
		// ==============================
		PROPERTIES.put("black", blackProperty);
		
		ArrayList<String> countProperty = new ArrayList<String>();
		countProperty.add("often");
		countProperty.add("many");
		countProperty.add("most");
		countProperty.add("main");
		// ==============================
		PROPERTIES.put("count", countProperty);
				
		ArrayList<String> dateProperty = new ArrayList<String>();
		dateProperty.add("when");
		dateProperty.add("date");
		// ==============================
		PROPERTIES.put("DATE", dateProperty);
				
		ArrayList<String> drawProperty = new ArrayList<String>();
		drawProperty.add("1/2");
		drawProperty.add("1/2-1/2");
		drawProperty.add("draw");
		drawProperty.add("remis");
		drawProperty.add("tie");
		drawProperty.add("stalemate");
		drawProperty.add("threfold");
		// ==============================
		PROPERTIES.put("1/2-1/2", drawProperty);
		
		ArrayList<String> eloProperty = new ArrayList<String>();
		eloProperty.add("elo");
		eloProperty.add("rating");
		eloProperty.add("rate");
		// ==============================
		PROPERTIES.put("elo", eloProperty);
		
		ArrayList<String> eventProperty = new ArrayList<String>();
		
		eventProperty.add("event");
		eventProperty.add("tournament");
		eventProperty.add("tourney");
		eventProperty.add("championship");
		// ==============================
		PROPERTIES.put("event", eventProperty);
		
		ArrayList<String> eventEntityProperty = new ArrayList<String>();
		
		eventEntityProperty.add("blitz");
		eventEntityProperty.add("candidate");
		eventEntityProperty.add("chess960");
		eventEntityProperty.add("classics");
		eventEntityProperty.add("congress");
		eventEntityProperty.add("festival");
		eventEntityProperty.add("kongress");
		eventEntityProperty.add("masters");
		eventEntityProperty.add("meeting");
		eventEntityProperty.add("open");
		eventEntityProperty.add("rapid");
		eventEntityProperty.add("section");
		eventEntityProperty.add("simul");
		eventEntityProperty.add("simultan");
		eventEntityProperty.add("simultaneous");
		eventEntityProperty.add("speed");
		eventEntityProperty.add("exhibition");
		eventEntityProperty.add("consultation");
		eventEntityProperty.add("correspondence");
		eventEntityProperty.add("challenge");
		eventEntityProperty.add("playoff");
		eventEntityProperty.add("final");
		eventEntityProperty.add("blind");
		eventEntityProperty.add("knockout");
		eventEntityProperty.add("blindfolded");
		eventEntityProperty.add("olympiad");
		// ==============================
		PROPERTIES.put("eventEntity", eventEntityProperty);
		
		ArrayList<String> fenProperty = new ArrayList<String>();
		fenProperty.add("capture");
		//fenProperty.add("check");
		//fenProperty.add("doubled");
		//fenProperty.add("enpassant");
		fenProperty.add("exchange");
		//fenProperty.add("fianchetto");
		//fenProperty.add("fen");
		//fenProperty.add("isolate");
		//fenProperty.add("position");
		//fenProperty.add("rank");
		//fenProperty.add("file");
		fenProperty.add("castling");
		fenProperty.add("castle");
		//fenProperty.add("passant");
		fenProperty.add("promote");
		fenProperty.add("promotion");
		fenProperty.add("underpromotion");
		// ==============================
		PROPERTIES.put("fen", fenProperty);
		
		ArrayList<String> comparativePosProperty = new ArrayList<String>();
		comparativePosProperty.add("above");
		comparativePosProperty.add("higher");
		comparativePosProperty.add("more");
		comparativePosProperty.add("larger");
		comparativePosProperty.add("over");
		comparativePosProperty.add("beyond");
		comparativePosProperty.add("longer");
		comparativePosProperty.add("stronger");
		comparativePosProperty.add("greater");
		//===============================
		PROPERTIES.put("jjr_pos", comparativePosProperty);
		
		ArrayList<String> superlativePosProperty = new ArrayList<String>();
		superlativePosProperty.add("longest");
		superlativePosProperty.add("strongest");
		superlativePosProperty.add("toughest");
		superlativePosProperty.add("hardest");
		superlativePosProperty.add("highest");
		superlativePosProperty.add("main");
		//===============================
		PROPERTIES.put("jjs_pos", superlativePosProperty);
		
		ArrayList<String> gameProperty = new ArrayList<String>();
		gameProperty.add("game");
		gameProperty.add("games");
		gameProperty.add("plays");
		gameProperty.add("match");
		gameProperty.add("matchs");
		gameProperty.add("matchup");
		gameProperty.add("matchups");
		gameProperty.add("pairing");
		gameProperty.add("pairings");
		gameProperty.add("encounter");
		gameProperty.add("encounters");
		gameProperty.add("endgame");
		gameProperty.add("position");
		// ==============================
		PROPERTIES.put("game", gameProperty);
		
		ArrayList<String> comparativeNegProperty = new ArrayList<String>();
		comparativeNegProperty.add("less");
		comparativeNegProperty.add("lower");
		comparativeNegProperty.add("smaller");
		comparativeNegProperty.add("weaker");
		comparativeNegProperty.add("below");
		comparativeNegProperty.add("under");		
		comparativeNegProperty.add("shorter");
		// ==============================
		PROPERTIES.put("jjr_neg", comparativeNegProperty);
		
		ArrayList<String> superlativeNegProperty = new ArrayList<String>();
		superlativeNegProperty.add("shortest");
		superlativeNegProperty.add("weakest");
		superlativeNegProperty.add("lowest");
		PROPERTIES.put("jjs_neg", superlativeNegProperty);

		ArrayList<String> loseProperty = new ArrayList<String>();
		loseProperty.add("lose");
		loseProperty.add("loss");
		// ==============================
		PROPERTIES.put("0-1", loseProperty);
		
		ArrayList<String> movesProperty = new ArrayList<String>();
		movesProperty.add("move");
		// ==============================
		PROPERTIES.put("moves", movesProperty);
		
		ArrayList<String> openingProperty = new ArrayList<String>();
		openingProperty.add("opening");
		openingProperty.add("system");
		openingProperty.add("defence");
		openingProperty.add("defense");
		openingProperty.add("variation");
		openingProperty.add("wall");
		openingProperty.add("gambit");
		openingProperty.add("attack");
		openingProperty.add("counterattack");
		openingProperty.add("countergambit");
		openingProperty.add("opening");
		openingProperty.add("symmetrical");
		openingProperty.add("line");
		openingProperty.add("indian");
		// ==============================
		PROPERTIES.put("OPENING", openingProperty);
		
		ArrayList<String> ordinalProperty = new ArrayList<String>();
		ordinalProperty.add("last");
		// ==============================
		PROPERTIES.put("ORDINAL", ordinalProperty);
		
		ArrayList<String> averageProperty = new ArrayList<String>();
		averageProperty.add("average");
		//===============================
		PROPERTIES.put("average", averageProperty);
		
		ArrayList<String> personProperty = new ArrayList<String>();
		personProperty.add("player");
		personProperty.add("opponent");
		personProperty.add("who");
		personProperty.add("whom");
		personProperty.add("person");
		// ==============================
		PROPERTIES.put("PERSON", personProperty);

		ArrayList<String> pieceProperty = new ArrayList<String>();
		pieceProperty.add("pawn");
		pieceProperty.add("bishop");
		pieceProperty.add("knight");
		pieceProperty.add("rook");
		pieceProperty.add("queen");
		pieceProperty.add("king");
		// ==============================
		PROPERTIES.put("piece", pieceProperty);

		ArrayList<String> roundProperty = new ArrayList<String>();
		roundProperty.add("round");
		// ==============================
		PROPERTIES.put("round", roundProperty);

		ArrayList<String> siteProperty = new ArrayList<String>();
		siteProperty.add("where");
		siteProperty.add("city");
		siteProperty.add("town");
		siteProperty.add("country");
		siteProperty.add("place");
		// ==============================
		PROPERTIES.put("site", siteProperty);
		
		ArrayList<String> temporalProperty = new ArrayList<String>();
		temporalProperty.add("earliest");
		temporalProperty.add("latest");
		// ==============================
		PROPERTIES.put("temporal", temporalProperty);

		ArrayList<String> whiteProperty = new ArrayList<String>();
		whiteProperty.add("white");
		// ==============================
		PROPERTIES.put("white", whiteProperty);

		ArrayList<String> winProperty = new ArrayList<String>();
		winProperty.add("1-0");
		winProperty.add("0-1");
		winProperty.add("beat");
		winProperty.add("win");
		winProperty.add("checkmate");
		winProperty.add("mate");
		winProperty.add("defeat");
		winProperty.add("victory");
		// ==============================
		PROPERTIES.put("1-0", winProperty);
	}

	/**
	 * inverse the properties
	 */
	public void inverseProperties() {
		for (HashMap.Entry<String, ArrayList<String>> entry : PROPERTIES.entrySet()) {
			for (String prop : entry.getValue()) {
				INVERSED_PROPERTIES.put(prop, entry.getKey());
			}
		}
	}
}
