package de.daug.semanticchess.Parser;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

/**
 * simple chess vocabulary assign a term to a topic
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
	 * a list the terms assigned to a topic
	 */
	public void fillProperties() {
		ArrayList<String> blackProperty = new ArrayList<String>();
		blackProperty.add("black");
		// ==============================
		PROPERTIES.put("black", blackProperty);
				
		ArrayList<String> dateProperty = new ArrayList<String>();
		dateProperty.add("when");
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
		// ==============================
		PROPERTIES.put("elo", eloProperty);
		
		ArrayList<String> eventProperty = new ArrayList<String>();
		eventProperty.add("blitz");
		eventProperty.add("candidate");
		eventProperty.add("championship");
		eventProperty.add("chess960");
		eventProperty.add("classics");
		eventProperty.add("congress");
		eventProperty.add("event");
		eventProperty.add("events");
		eventProperty.add("festival");
		eventProperty.add("kongress");
		eventProperty.add("masters");
		eventProperty.add("meeting");
		eventProperty.add("open");
		eventProperty.add("random chess");
		eventProperty.add("rapid");
		eventProperty.add("section");
		eventProperty.add("simul");
		eventProperty.add("simultan");
		eventProperty.add("simultaneous");
		eventProperty.add("speed");
		eventProperty.add("tournament");
		// ==============================
		PROPERTIES.put("event", eventProperty);
		
		ArrayList<String> fenProperty = new ArrayList<String>();
		fenProperty.add("fen");
		fenProperty.add("position");
		fenProperty.add("rank");
		fenProperty.add("file");
		// ==============================
		PROPERTIES.put("fen", fenProperty);
		
		ArrayList<String> greaterThanProperty = new ArrayList<String>();
		greaterThanProperty.add("above");
		greaterThanProperty.add("highest");
		//===============================
		PROPERTIES.put("greater", greaterThanProperty);
		
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
		// ==============================
		PROPERTIES.put("game", gameProperty);

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
		openingProperty.add("opening");
		openingProperty.add("symmetrical");
		openingProperty.add("line");
		openingProperty.add("indian");
		// ==============================
		PROPERTIES.put("opening", openingProperty);
		
		ArrayList<String> ordinalProperty = new ArrayList<String>();
		ordinalProperty.add("last");
		// ==============================
		PROPERTIES.put("ORDINAL", ordinalProperty);
		
		ArrayList<String> optionProperty = new ArrayList<String>();
		optionProperty.add("average");
		//===============================
		PROPERTIES.put("option", optionProperty);
		
		ArrayList<String> personProperty = new ArrayList<String>();
		personProperty.add("player");
		personProperty.add("opponent");
		personProperty.add("who");
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
		PROPERTIES.put("site", siteProperty);

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
