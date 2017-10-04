package de.daug.semanticchess.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.daug.semanticchess.Database.QueryVirtuoso;
import de.daug.semanticchess.Parser.Allocator;

/**
 * connects to the database and starts the allocator
 * to convert the user query to a sparql query
 */
@Service
public class QueryService{
	
	@Autowired
	private QueryVirtuoso virtuosoQuery;
	
	public String getCustomResult(String strQuery){
		Allocator alloc = new Allocator(strQuery);
		alloc.allocateSequence();
		strQuery = alloc.distanceEntities(alloc.getSparqlQuery());
		//strQuery = alloc.subStringEntities(alloc.getSparqlQuery());
		//strQuery = alloc.regexEntities(alloc.getSparqlQuery());
		
		
		
		
		return virtuosoQuery.getCustomResult(strQuery);
	}
}