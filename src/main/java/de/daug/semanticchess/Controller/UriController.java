package de.daug.semanticchess.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import de.daug.semanticchess.Service.UriService;

/**
 * Sets up a REST API.
 * The page /uri delivers a result JSON for queries with a game URI.
 */
@RestController
@RequestMapping("/uri")
public class UriController{
	
	/**
	 * service that connects to the database
	 */
	@Autowired
	private UriService uriService;
	
	/**
	 * connects to UriService
	 * @param gameUri: chess game URI
	 * @return a JSON with the results from the database
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String getGame(@RequestBody String gameUri){
		System.out.printf("%-16s %s\n", "Controller: ",  gameUri);
		return uriService.getGame(gameUri);
	}
	
}