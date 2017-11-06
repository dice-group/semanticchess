package de.daug.semanticchess.Database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.commons.text.similarity.JaccardDistance;

/**
 * This class has three different ways to check for string similarity.
 * - exact match
 * - substring match
 * - distance match
 */
public class StringSimilarity {

	private String variable;
	public String query;
	private ResultSet resultSet;
	private int offset = 0;

	/**
	 * empty constructor
	 */
	public StringSimilarity() {
	}
	
	/**
	 * prescribed SPARQL queries to manipulate the returning result set from Virtuoso.
	 * For example: player
	 * Orders by ELO so that the strongest player is at the top of the set.
	 * If the user asks "Get games by Steinitz", the method ranks Wilhelm Steinitz higher
	 * than V Steinitz. 
	 * 
	 * @param property
	 * @return SPARQL query
	 */
	public String setQuery(String property) {
		String query = "";

		switch (property) {
		case "prop:white":
		case "prop:black":
		case "prop:white|prop:black":
			this.variable = "player";
			query = "SELECT DISTINCT ?player WHERE {?game prop:white|prop:black ?player. ?game prop:whiteelo|prop:blackelo ?elo.} ORDER BY DESC(?elo)";
			break;
		case "prop:event":
			this.variable = "event";
			query = "SELECT DISTINCT ?event WHERE {?game prop:event ?event. ?game prop:date ?date.} ORDER BY ASC(?date)";
			break;
		case "cont:openingName":
			this.variable = "openingName";
			query = "SELECT DISTINCT ?openingName (COUNT(?game) AS ?nr) WHERE {?game cont:eco ?eco. ?eco cont:openingName ?openingName} GROUP BY ?openingName ORDER BY DESC(?nr)";
			break;
		case "prop:site":
			this.variable = "site";
			query = "SELECT DISTINCT ?site WHERE {?game prop:site ?site. ?game prop:date ?date.} ORDER BY ASC(?date)";
			break;
		case "cont:eco":
			this.variable = "eco";
			query = "SELECT DISTINCT ?eco WHERE {?game cont:openingCode ?eco. game prop:date ?date.} ORDER BY ASC(?date)";
			break;
		}

		this.query = query;
		return this.query;
	}
	
	/**
	 * exact string match
	 * @param entity to check
	 * @return List with found entities
	 */
	public List<String> exactMatch(String entity) {
		List<String> foundEntities = new ArrayList<String>();

		SparqlVirtuoso sQuery = new SparqlVirtuoso();
		this.resultSet = sQuery.getResultSet(this.query);

		for (; this.resultSet.hasNext();) {
			QuerySolution soln = this.resultSet.next();
			if (soln.getLiteral(this.variable).getString().equals(entity)) {
				foundEntities.add(entity.replaceAll("\'", "\\\\'"));
			}
		}

		return foundEntities;
	}

	/**
	 * substring match
	 * @param entity to check
	 * @return List with found entities
	 */
	public ArrayList<String> subStringMatch(String entity) {
		ArrayList<String> foundEntities = new ArrayList<String>();

		SparqlVirtuoso sQuery = new SparqlVirtuoso();

		this.resultSet = sQuery.getResultSet(this.query);
		int i = 0;
		for (; this.resultSet.hasNext();) {
			QuerySolution soln = this.resultSet.next();
			if (offset > 0) {
				if (soln.getLiteral(this.variable).getString().toLowerCase().indexOf(entity.toLowerCase()) > -1) {
					if (i == offset - 1) {
						foundEntities
								.add("'" + soln.getLiteral(this.variable).getString().replaceAll("\'", "\\\\'") + "'");
					}
					i++;
				}
			} else {
				if (soln.getLiteral(this.variable).getString().toLowerCase().indexOf(entity.toLowerCase()) > -1) {
					foundEntities.add("'" + soln.getLiteral(this.variable).getString().replaceAll("\'", "\\\\'") + "'");
				}
			}		
		}
		
		return foundEntities;
	}
	
	/**
	 * distance String match (Jaccard Distance)
	 * @param entity to check
	 * @return best match
	 */
	public String distanceMatch(String entity) {
		String foundEntity = "";
		SparqlVirtuoso sQuery = new SparqlVirtuoso();

		this.resultSet = sQuery.getResultSet(this.query);
		double distance = 999;
		JaccardDistance jd = new JaccardDistance();
		LinkedHashMap<String, Double> candidatesList = new LinkedHashMap<String, Double>();

		for (; this.resultSet.hasNext();) {

			QuerySolution soln = this.resultSet.next();
			String candidateEntity = soln.getLiteral(this.variable).getString();

			double candidateDistance = jd.apply(entity, candidateEntity);

			if (candidateEntity.toLowerCase().indexOf(entity.toLowerCase()) > -1) {
				candidatesList.put(candidateEntity, candidateDistance);
			}

			if (distance > candidateDistance) {
				distance = candidateDistance;
				foundEntity = candidateEntity;
			}
		}

		if (offset > 0 && !candidatesList.isEmpty()) {
			int i = 0;
			for (String key : candidatesList.keySet()) {
				if (i == offset - 1) {
					return "'" + key + "'";
				}
				i++;
			}
		}

		else if (offset == 0 && !candidatesList.isEmpty()) {
			Entry<String, Double> min = null;
			for (Entry<String, Double> entry : candidatesList.entrySet()) {
				if (min == null || min.getValue() > entry.getValue()) {
					min = entry;
				}
			}

			return "'" + min.getKey().replaceAll("\'", "\\\\\\\\\\\\'") + "'";
		}

		return "'" + foundEntity.replaceAll("\'", "\\\\\\\\\\'") + "'";
	}
	
	/**
	 * gets query
	 * @return query
	 */
	public String getQuery() {
		return this.query;
	}
	
	/**
	 * gets the offset
	 * @return offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * sets the offset
	 * @param offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * main method for testing
	 * @param args
	 */
	public static void main(String[] args) {
		StringSimilarity similar = new StringSimilarity();
		similar.setQuery("cont:openingName");
		//similar.setOffset(3);
		System.out.println(similar.subStringMatch("Queen's Gambit"));
	}
}