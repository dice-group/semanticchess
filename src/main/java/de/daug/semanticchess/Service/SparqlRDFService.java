package de.daug.semanticchess.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.daug.semanticchess.Database.SparqlRDFVirtuoso;

/**
 * This class connects to the database
 * and returns the result for the SPARQL query.
 */
@Service
public class SparqlRDFService{
	
	@Autowired
	private SparqlRDFVirtuoso virtuosoQuery;
	
	/**
	 * Takes the SPARQL query and returns
	 * a JSON with the result data.
	 * @param strQuery: SPARQL query
	 * @return JSON: with result data
	 */
	public String getRDFResult(String strQuery){
		return virtuosoQuery.getRDFResult(strQuery);
	}
	
}