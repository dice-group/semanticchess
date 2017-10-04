package de.daug.semanticchess.Converter;

import de.uni_leipzig.informatik.swp13_sc.converter.ECOToRDFConverter;

import de.daug.semanticchess.Configurations;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class EcoToRdf {
	
	public static void main(String[] args){

//		String[] inputEco = new String[1];
//		inputEco[0] = "src/main/resources/static/openings/chessopenings.txt";
//		
//		ECOToRDFConverter.main(inputEco);
//		
//		byte[] buffer = new byte[1024];
//
//	    try{
//
//	        GZIPInputStream gzis = new GZIPInputStream(new FileInputStream("src/main/resources/static/openings/chessopenings.txt.ttl.gz"));
//	        FileOutputStream out = new FileOutputStream("src/main/resources/static/openings/chessopenings.txt.ttl");
//
//	        int len;
//	        while ((len = gzis.read(buffer)) > 0) {
//	            out.write(buffer, 0, len);
//	        }
//
//	        gzis.close();
//	        out.close();
//
//	        
//
//	    } catch(IOException ex){
//	        ex.printStackTrace();
//	    }
		
		String[] inputLinker = new String[4];
		inputLinker[0] = Configurations.DB_GRAPH; //falscher Graph?
		inputLinker[1] = "localhost:1111";
		inputLinker[2] = Configurations.DB_USER;
		inputLinker[3] = Configurations.DB_PASSWORD;
		

		

		
		ECOLinker.main(inputLinker);
	}
	
}