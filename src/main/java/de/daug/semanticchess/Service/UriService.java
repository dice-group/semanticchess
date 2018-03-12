package de.daug.semanticchess.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import de.daug.semanticchess.Database.GameVirtuoso;

/**
 * This class connects to the database
 * and collects all information about a chess game
 * into a PGN string. 
 */
@Service
public class UriService{
	
	@Autowired
	private GameVirtuoso gameQuery;
	
	/**
	 * Takes the URI of chess game and 
	 * returns the PGN information.
	 * @param gameUri: URI of chess game in the Virtuoso database
	 * @return JSON: with a PGN information
	 */
	public String getGame(String gameUri){		
		return gameQuery.getPGN(gameUri);
	}
}