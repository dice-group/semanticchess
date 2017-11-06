package de.daug.semanticchess.Database;

import de.daug.semanticchess.Configurations;
import virtuoso.jena.driver.VirtModel;

/**
 * This class connects to the Virtuoso database.
 */
public class ConnectVirtuoso {
	private String url = Configurations.DB;
	private String user = Configurations.DB_USER;
	private String pwd = Configurations.DB_PASSWORD;
	private String graph = Configurations.DB_GRAPH;
	
	/**
	 * empty constructor
	 */
	public ConnectVirtuoso(){		
	}
	
	/**
	 * builds a VirtModel and connects to the database
	 * @return vModel: database connection
	 */
	public VirtModel connectDefault(){
		VirtModel vModel = VirtModel.openDatabaseModel(graph, url, user, pwd);
				
		return vModel;
	}
}