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
		strQuery = alloc.getSparqlQuery();
		
		JSONObject jsonArray = null;
		try {
			jsonArray = new JSONObject(virtuosoQuery.getCustomResult(strQuery));
			int MAX = 5;
			while(jsonArray.getJSONObject("results").getJSONArray("bindings").length() == 0 && MAX < 100){
				alloc.allocateSequence();
				strQuery = alloc.getSparqlQuery();
				MAX += 1;
			}
		} catch (JSONException e) {

		} 
//		try {
//			System.out.println(jsonArray.toString());
//			System.out.println(jsonArray.getJSONObject("results").getJSONArray("bindings").length());
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return virtuosoQuery.getCustomResult(strQuery);
	}
	
}