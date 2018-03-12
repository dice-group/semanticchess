package de.daug.semanticchess.Converter;

import de.daug.semanticchess.Configurations;
import de.daug.semanticchess.Converter.Utils.ECOLinker;
import de.uni_leipzig.informatik.swp13_sc.converter.ECOToRDFConverter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * This class converts the chessopenings.txt to RDF data.
 * It seperates the classes of the ECO system (A,B,C,D,E) in different files.
 */
public class EcoToRdf {
	
	public static void main(String[] args){

		String[] inputEco = new String[1];
		inputEco[0] = Configurations.OPENINGS_TXT;
		
		ECOToRDFConverter.main(inputEco);
		
		byte[] buffer = new byte[1024];

	    try{
	        GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(Configurations.OPENINGS_TXT_GZ));
	        FileOutputStream out = new FileOutputStream(Configurations.OPENINGS_TTL);

	        int len;
	        while ((len = gzis.read(buffer)) > 0) {
	            out.write(buffer, 0, len);
	        }
	        gzis.close();
	        out.close();
	    } catch(IOException ex){
	        ex.printStackTrace();
	    }
		
	    Configurations config = new Configurations();
		String[] inputLinker = new String[5];
		inputLinker[0] = config.getDBGraph();
		inputLinker[1] = config.getDBShort();
		inputLinker[2] = config.getDBUser();
		inputLinker[3] = config.getDBPassword();
		
		
		if(args == null){
			inputLinker[4] = "A";
			ECOLinker.main(inputLinker);
			
			inputLinker[4] = "B";
			ECOLinker.main(inputLinker);
			
			inputLinker[4] = "C";
			ECOLinker.main(inputLinker);
			
			inputLinker[4] = "D";
			ECOLinker.main(inputLinker);
			
			inputLinker[4] = "E";
			ECOLinker.main(inputLinker);
		} else if (args.length == 2){
			inputLinker[4] = args[1];
			ECOLinker.main(inputLinker);
		}

		
	}
	
}