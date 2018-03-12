package de.daug.semanticchess.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.daug.semanticchess.Database.QueryVirtuoso;
import de.daug.semanticchess.Parser.Allocator;

/**
 * This class connects to the database and starts the allocator
 * to convert the user query to a sparql query.
 */
@Service
public class QueryService{
	
	@Autowired
	private QueryVirtuoso virtuosoQuery;
	
	/**
	 * Takes the user query and returns a JSON
	 * with the result data.
	 * There are three method to choose entities from the database, 
	 * if an entity is found in the user query:
	 * 1. Distance (distanceEntities): 
	 * 		- uses the Jaccard Distance to get the most similar entity from the database
	 * 		- if the entity is a substring to an entity from the database the method takes this instead
	 * 		- only returns the most similar entity
	 * 2.  Substring (subStringEntities):
	 * 		- collects all entities from the database if the entity from the query is a substring
	 * 		- in the SPARQL query they will be stored in a VALUES clause
	 * 3.	Regex (regexEntities):
	 * 		- just creates regex filters for all entities
	 * 4.  combination of 1. and 2. 
	 * @param strQuery: user query from the interface
	 * @return JSON: with result data
	 */
	public String getCustomResult(String strQuery){
		//init allocator 
		Allocator alloc = new Allocator(strQuery);
		//choose the SPARQL query
		alloc.allocateSequence();
		
		//methods for choosing entities from the database
		//===============================================
		//strQuery = alloc.distanceEntities(alloc.getSparqlQuery());
		//strQuery = alloc.subStringEntities(alloc.getSparqlQuery());
		//strQuery = alloc.regexEntities(alloc.getSparqlQuery());
		
		strQuery = alloc.substringWithDistanceFallback(alloc.getSparqlQuery());
		//===============================================
		
		return virtuosoQuery.getCustomResult(strQuery);
		
	}
}