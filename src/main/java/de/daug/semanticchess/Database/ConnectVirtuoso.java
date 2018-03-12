package de.daug.semanticchess.Database;

import de.daug.semanticchess.Configurations;
import virtuoso.jena.driver.VirtModel;

/**
 * This class connects to the Virtuoso database.
 */
public class ConnectVirtuoso {
	
	private String url;
	private String user;
	private String pwd;
	private String graph;
	
	/**
	 * empty constructor
	 */
	public ConnectVirtuoso(){	
		Configurations config = new Configurations();
		this.url = config.getDB();
		this.user = config.getDBUser();
		this.pwd = config.getDBPassword();
		this.graph = config.getDBGraph();
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