package de.daug.semanticchess.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import de.daug.semanticchess.Database.SparqlVirtuoso;

/**
 * connects to the database
 */
@Service
public class SparqlService{
	
	@Autowired
	private SparqlVirtuoso virtuosoQuery;
	
	public String getCustomResult(String strQuery){
		return virtuosoQuery.getCustomResult(strQuery);
	}
	
}