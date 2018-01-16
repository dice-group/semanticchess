package de.daug.semanticchess.Controller;

import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.daug.semanticchess.Service.SparqlRDFService;

/**
 * Sets up a REST API
 * The page /sparql delivers a result JSON for SPARQL queries.
 */
@RestController
@RequestMapping("/sparqlxml")
public class SparqlRDFController{
	
	/**
	 * service that connects to the database
	 */
	@Autowired
	private SparqlRDFService sparqlRDFService;
	
	/**
	 * connects to SparqlService.java 
	 * @param strQuery: SPARQL query
	 * @return a JSON with the results from the database
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String getCustomResult(@RequestBody String strQuery){
		System.out.printf("%-16s %s\n", "Controller: ",  strQuery);
		String jObj = null;
		jObj ="{\"XML\": \""+ JSONValue.escape(sparqlRDFService.getRDFResult(strQuery)) + "\"}";
		
		return jObj.toString();
	}
	
}