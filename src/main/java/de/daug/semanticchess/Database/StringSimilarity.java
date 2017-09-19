package de.daug.semanticchess.Database;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

public class StringSimilarity{
	
	private String property;
	private String entity;
	private String variable;
	public String query;
	private ResultSet resultSet;
	
	public StringSimilarity(){

	}
	
	
	public String setQuery(String property){
		switch(property){
			case "white":
			case "black":
			case "white|prop:black":
				this.variable = "player";
				return this.query = "SELECT DISTINCT ?player WHERE {?game prop:white|prop:black ?player. ?game prop:whiteelo|prop:blackelo ?elo.} ORDER BY DESC(?elo)";
			case "event":
				this.variable = "event";
				return this.query = "SELECT DISTINCT ?event WHERE {?game prop:event ?event.}";
			case "opening":
				this.variable = "opening";
				return this.query = "SELECT DISTINCT ?opening WHERE {?game prop:opening ?opening.}";
			case "site":				
				this.variable = "site";
				return this.query = "SELECT DISTINCT ?site WHERE {?game prop:site ?site.}";
			case "eco":
				this.variable = "eco";
				return this.query = "SELECT DISTINCT ?site WHERE {?game prop:eco ?eco.}";
			case "date":
				this.variable = "date";
				return this.query = "SELECT DISTINCT ?date WHERE {?game prop:date ?date.}";
		}
		
		
		return "";
	}
	
	public List<String> exactMatch(String entity){
		List<String> foundEntities = new ArrayList<String>();
		
		SparqlVirtuoso sQuery = new SparqlVirtuoso();
		this.resultSet = sQuery.getResultSet(this.query);
		
		for ( ; this.resultSet.hasNext() ; )
	    {
	        QuerySolution soln = this.resultSet.next();
	        if(soln.getLiteral(this.variable).getString().equals(entity)){
	        	
	        	foundEntities.add(entity);
	        }
	       
	    }
		
		return foundEntities;
	}
	
	public List<String> subStringMatch(String entity){
		List<String> foundEntities = new ArrayList<String>();
		
		SparqlVirtuoso sQuery = new SparqlVirtuoso();
		this.resultSet = sQuery.getResultSet(this.query);
		
		for ( ; this.resultSet.hasNext() ; )
	    {
	        QuerySolution soln = this.resultSet.next();
	        if(soln.getLiteral(this.variable).getString().indexOf(entity) > -1){
	        	
	        	foundEntities.add(soln.getLiteral(this.variable).getString());
	        }
	       
	    }
		
		return foundEntities;
	}
	
	public void distanceMatch(){
		
	}
	
	public String getQuery(){
		return this.query;
	}
	
	public static void main (String[] args){
		StringSimilarity similar = new StringSimilarity();
		similar.setQuery("white");
		System.out.println(similar.query);
		System.out.println(similar.subStringMatch("Steinitz"));
		
	}
	
}