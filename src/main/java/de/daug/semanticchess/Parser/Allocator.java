package de.daug.semanticchess.Parser;

import java.util.ArrayList;
import java.util.List;

import de.daug.semanticchess.Database.StringSimilarity;
import de.daug.semanticchess.Parser.Helper.Classes;
import de.daug.semanticchess.Parser.Helper.Entity;

/**
 * assigns a sequence code to a sparql query
 */
public class Allocator {

	private String sequenceCode;
	private String sparqlQuery;

	private List<Entity> entities = new ArrayList<Entity>();
	private List<Classes> classes = new ArrayList<Classes>();
	private String options;
	private String filters;
	public int counter = 0;
	private StringSimilarity sim = new StringSimilarity();

	/**
	 * constructor allocates the given sequence code to a sparql query
	 * 
	 * @param query:
	 *            user question
	 */
	public Allocator(String query) {
		Parser parser = new Parser(query);

		this.sequenceCode = parser.getSequence();
		this.entities = parser.getEntities();
		this.classes = parser.getClasses();
		this.options = parser.getOptions().toString();
		this.filters = parser.getFilters().getFilterStr();
		// allocateSequence();
	}

	/**
	 * replace all placeholders in the sparql query with found entities and
	 * properties
	 * 
	 * @param query
	 * @return
	 */
	private String replaceCodes(String sparql) {

		for (Entity e : entities) {

			String entity = "";
			try {
				this.sim.setQuery(e.getPropertyName().replace("prop:", ""));
				List<String> entityList = this.sim.subStringMatch(e.getEntityName().replaceAll("'", ""));
				if(entityList.size() > 1){
					entity = "'" + this.sim.subStringMatch(e.getEntityName().replaceAll("'", "")).get(counter) + "'";
				} else {
					entity = "'" + this.sim.subStringMatch(e.getEntityName().replaceAll("'", "")).get(0) + "'";
				}
				

			} catch (Exception err) {
				entity = e.getEntityName();
			}
			sparql = sparql.replaceAll(e.getEntityId(), entity);
			sparql = sparql.replaceAll(e.getPropertyId(), e.getPropertyName());
			sparql = sparql.replaceAll(e.getResourceId(), e.getResourceName());
		}
		for (Classes c : classes) {
			sparql = sparql.replaceAll(c.getClassesId(), c.getClassesName());
			sparql = sparql.replaceAll(c.getPropertyId(), c.getPropertyName());
			sparql = sparql.replaceAll(c.getResourceId(), c.getResourceName());
		}

		sparql = sparql.replace("FILTER", this.filters);

		return sparql;
	}

	/**
	 * allocate sequence code
	 */
	public void allocateSequence() {
		String sparqlQuery = "";
		System.out.println(this.sequenceCode);
		switch (this.sequenceCode) {
		case "_010":
			sparqlQuery = replaceCodes(Sequences._010);
			break;
		case "_020":
			sparqlQuery = replaceCodes(Sequences._020);
			break;
		case "_030":
			sparqlQuery = replaceCodes(Sequences._030);
			break;
		case "_040":
			sparqlQuery = replaceCodes(Sequences._040);
			break;
		case "_100":
			sparqlQuery = replaceCodes(Sequences._100);
			break;
		case "_110":
			sparqlQuery = replaceCodes(Sequences._110);
			break;
		case "_120":
			sparqlQuery = replaceCodes(Sequences._120);
			break;
		case "_130":
			sparqlQuery = replaceCodes(Sequences._130);
			break;
		case "_140":
			sparqlQuery = replaceCodes(Sequences._140);
			break;
		case "_200":
			sparqlQuery = replaceCodes(Sequences._200);
			break;
		case "_210":
			sparqlQuery = replaceCodes(Sequences._210);
			break;
		case "_220":
			sparqlQuery = replaceCodes(Sequences._220);
			break;
		case "_230":
			sparqlQuery = replaceCodes(Sequences._230);
			break;
		case "_240":
			sparqlQuery = replaceCodes(Sequences._240);
			break;
		case "_021":
			sparqlQuery = replaceCodes(Sequences._021);
			break;
		case "_041":
			sparqlQuery = replaceCodes(Sequences._041);
			break;
		case "_061":
			sparqlQuery = replaceCodes(Sequences._061);
			break;
		case "_201":
			sparqlQuery = replaceCodes(Sequences._201);
			break;
		case "_221":
			sparqlQuery = replaceCodes(Sequences._221);
			break;
		case "_241":
			sparqlQuery = replaceCodes(Sequences._241);
			break;
		case "_261":
			sparqlQuery = replaceCodes(Sequences._261);
			break;
		case "_401":
			sparqlQuery = replaceCodes(Sequences._401);
			break;
		case "_421":
			sparqlQuery = replaceCodes(Sequences._421);
			break;
		case "_441":
			sparqlQuery = replaceCodes(Sequences._441);
			break;
		case "_461":
			sparqlQuery = replaceCodes(Sequences._461);
			break;
		default:
			break;
		}
		this.sparqlQuery = sparqlQuery + " " + options;
		
	}

	/**
	 * @return sparql query
	 */
	public String getSparqlQuery() {
		this.counter += 1;
		return sparqlQuery;
	}

	/**
	 * set the sparql query
	 * 
	 * @param sparqlQuery
	 */
	public void setSparqlQuery(String sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
		
	}

	/**
	 * for testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Allocator alloc = new Allocator("All losing games of Steinitz from London.");
		
		

		int i = 0;
		while (i < 10) {
			alloc.allocateSequence();
			System.out.println(alloc.counter);
			System.out.println(alloc.getSparqlQuery());
			i++;
		}

	}

}