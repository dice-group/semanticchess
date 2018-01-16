package de.daug.semanticchess.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.daug.semanticchess.Service.QueryService;

/**
 * Sets up a REST API.
 * The page /query delivers a result JSON for user queries.
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
	 * @return a JSON with the results from the database
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String getCustomResult(@RequestBody String strQuery){

		SimpleDateFormat sdf = new SimpleDateFormat("DD.MM.yy HH:mm:ss:SS");
		String uhrzeit = sdf.format(new Date());
		System.out.println(uhrzeit + " Started backend process");
		System.out.printf("%-16s %s\n", "Controller: ",  strQuery);

		return queryService.getCustomResult(strQuery);
	}
	
}