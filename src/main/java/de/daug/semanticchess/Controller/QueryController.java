package de.daug.semanticchess.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.daug.semanticchess.Service.QueryService;

/**
 * sets up a REST API
 * the page /query delivers a result json for user queries
 */
@RestController
@RequestMapping("/query")
public class QueryController{
	
	/**
	 * service that connects to the database
	 */
	@Autowired
	private QueryService queryService;
	
	/**
	 * connects to QueryService.java 
	 * @param strQuery: user query
	 * @return a json with the results from the database
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String getCustomResult(@RequestBody String strQuery){
		System.out.println("Controller: " + strQuery);
		return queryService.getCustomResult(strQuery);
	}
	
}