package de.daug.semanticchess.Database;

import de.daug.semanticchess.Configurations;
import virtuoso.jena.driver.VirtModel;

/**
 * connect to the Virtuoso database
 *
 */
public class ConnectVirtuoso {
	private String url = Configurations.DB;
	private String user = Configurations.DB_USER;
	private String pwd = Configurations.DB_PASSWORD;
	private String graph = Configurations.DB_GRAPH;
	
	/**
	 * constructor
	 */
	public ConnectVirtuoso(){
		
	}
	
	/**
	 * connect to database
	 * @return vModel: database
	 */
	public VirtModel connect(){
		VirtModel vModel = VirtModel.openDatabaseModel(graph, url, user, pwd);
				
		return vModel;
	}
}