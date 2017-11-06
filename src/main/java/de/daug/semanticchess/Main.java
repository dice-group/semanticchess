package de.daug.semanticchess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.daug.semanticchess.Converter.EcoToRdf;
import de.daug.semanticchess.Converter.PgnToRdf;
import de.daug.semanticchess.Database.LoadData;


@SpringBootApplication
public class Main 
{
	
	/**
	 * This class starts the applications. Consider using the params.
	 * @param args
	 * pgn -> start pgn to rdf converter
     * eco -> start mapping openings to games and eco to rdf converter
     * load -> load .ttl files to database
	 */
    public static void main( String[] args )
    {
    	if(args.length == 1){
        	if(args[0].equals("pgn")){
        		PgnToRdf.main(null);
        	} else if(args[0].equals("eco")){
        		EcoToRdf.main(null);
        	} else if(args[0].equals("load")){
        		LoadData.main(null);
        	}
    	} else {
        	//Application starts
    		SpringApplication.run(Main.class, args);
    	}

    	

    	
 

    }
}
