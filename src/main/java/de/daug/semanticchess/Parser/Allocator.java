package de.daug.semanticchess.Parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QueryParseException;

import de.daug.semanticchess.Database.StringSimilarity;
import de.daug.semanticchess.Parser.Helper.Classes;
import de.daug.semanticchess.Parser.Helper.Entity;
import de.daug.semanticchess.Parser.Helper.Filters;
import de.daug.semanticchess.Parser.Helper.Values;

/**
 * assigns a sequence code to a sparql query
 */
public class Allocator {

	private String sequenceCode;
	private String sparqlQuery;

	private List<Entity> entities = new ArrayList<Entity>();
	private List<Classes> classes = new ArrayList<Classes>();
	private String topics;
	private String options;
	private Filters filters;
	private StringSimilarity similar;
	

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
		
		
		//TopicFinder topicFinder = new TopicFinder();
		this.topics = parser.getTopicStr();
		
		//System.out.println(this.topics.toString());
		
		//System.out.println(parser.getOptions().getLimitStr());
		if(parser.getOptions().getLimitStr().isEmpty()){
			parser.getOptions().setLimitStr(2000);
			parser.getOptions().setOffsetStr(0);
		}
		this.options = parser.getOptions().toString();
		this.filters = parser.getFilters();
		this.similar = parser.getSimilar();
		// allocateSequence();
	}

	/**
	 * replace all placeholders in the sparql query with found entities and
	 * properties
	 * 
	 * @param query
	 * @return
	 */
	public String replaceOnly(String sparql) {

		for (Entity e : entities) {
			if (!e.getEntityName().isEmpty()) {
			sparql = sparql.replaceAll(e.getEntityId(), e.getEntityName());
			} else {
				sparql = sparql.replaceAll(e.getEntityId(), "");
			}
			
			sparql = sparql.replaceAll(e.getPropertyId(), e.getPropertyName());
			sparql = sparql.replaceAll(e.getResourceId(), e.getResourceName());
		}
		for (Classes c : classes) {
			sparql = sparql.replaceAll(c.getClassesId(), c.getClassesName());
			sparql = sparql.replaceAll(c.getPropertyId(), c.getPropertyName());
			sparql = sparql.replaceAll(c.getResourceId(), c.getResourceName());
		}

		sparql = sparql.replace("FILTER", this.filters.getFilterStr());

		return sparql;
	}

	public String regexEntities(String sparql) {
		int counter = 1;

		for (Entity e : entities) {
			if (!e.getEntityName().isEmpty()) {
			
			String value = "?value" + counter;
			sparql = sparql.replaceAll(e.getEntityId(), value);
			filters.addRegex(value, e.getEntityName().replaceAll("'", ""), true);
			counter += 1;
			} else {
				sparql = sparql.replaceAll(e.getEntityId(), "");
			}
			
			sparql = sparql.replaceAll(e.getPropertyId(), e.getPropertyName());
			sparql = sparql.replaceAll(e.getResourceId(), e.getResourceName());
			//System.out.println(sparql);
		}

		for (Classes c : classes) {
			sparql = sparql.replaceAll(c.getClassesId(), c.getClassesName());
			sparql = sparql.replaceAll(c.getPropertyId(), c.getPropertyName());
			sparql = sparql.replaceAll(c.getResourceId(), c.getResourceName());
		}

		sparql = sparql.replace("FILTER", this.filters.getFilterStr());

		return sparql;
	}

	public String subStringEntities(String sparql) {

		Values values = new Values();

		sparql = sparql.replaceFirst("\\{", "\\{VALUE_PLACEHOLDER ");
		int counter = 1;

		for (Entity e : entities) {

			similar.setQuery(e.getPropertyName());

			ArrayList<String> subStrEntities = new ArrayList<String>();

			if (!e.getEntityName().isEmpty()) {

				try {
					subStrEntities = similar.subStringMatch(e.getEntityName().replaceAll("'", ""));
				} catch (QueryParseException err) {

				}

				if (subStrEntities.size() > 0) {
					values.setValueVars("?value" + counter);
					sparql = sparql.replaceAll(e.getEntityId(), "?value" + counter);
					values.addResult(subStrEntities);
					counter++;
				}

				else {
					sparql = sparql.replaceAll(e.getEntityId(), e.getEntityName());
				}
			} else {
				sparql = sparql.replaceAll(e.getEntityId(), "");
			}

			sparql = sparql.replaceAll(e.getPropertyId(), e.getPropertyName());
			sparql = sparql.replaceAll(e.getResourceId(), e.getResourceName());
		}

		String tempStr = "";
		
		values.generatePermutations(values.getResults(), values.getPermutation(), 0, tempStr);
		
		//System.out.println(values.toString());

		sparql = sparql.replace("VALUE_PLACEHOLDER", values.toString());
		
		for (

		Classes c : classes) {
			sparql = sparql.replaceAll(c.getClassesId(), c.getClassesName());
			sparql = sparql.replaceAll(c.getPropertyId(), c.getPropertyName());
			sparql = sparql.replaceAll(c.getResourceId(), c.getResourceName());
		}

		sparql = sparql.replace("FILTER", this.filters.getFilterStr());

		return sparql;
	}

	public String distanceEntities(String sparql) {

		for (Entity e : entities) {

			similar.setQuery(e.getPropertyName());

			if (!e.getEntityName().isEmpty()) {
				try {
					String subStrEntity = similar
							.distanceMatch(e.getEntityName().substring(1, e.getEntityName().length() - 1));
					sparql = sparql.replaceAll(e.getEntityId(), subStrEntity);
				} catch (QueryParseException err) {

					sparql = sparql.replaceAll(e.getEntityId(), e.getEntityName());
				}

				sparql = sparql.replaceAll(e.getPropertyId(), e.getPropertyName());
				sparql = sparql.replaceAll(e.getResourceId(), e.getResourceName());
			} else {
				sparql = sparql.replaceAll(e.getEntityId(), "");
				sparql = sparql.replaceAll(e.getPropertyId(), e.getPropertyName());
				sparql = sparql.replaceAll(e.getResourceId(), e.getResourceName());
			}

		}

		for (Classes c : classes) {
			sparql = sparql.replaceAll(c.getClassesId(), c.getClassesName());
			sparql = sparql.replaceAll(c.getPropertyId(), c.getPropertyName());
			sparql = sparql.replaceAll(c.getResourceId(), c.getResourceName());
		}

		sparql = sparql.replace("FILTER", this.filters.getFilterStr());

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
			sparqlQuery = Sequences._010;
			break;
		case "_020":
			sparqlQuery = Sequences._020;
			break;
		case "_030":
			sparqlQuery = Sequences._030;
			break;
		case "_040":
			sparqlQuery = Sequences._040;
			break;
		case "_050":
			sparqlQuery = Sequences._050;
			break;
		case "_060":
			sparqlQuery = Sequences._060;
			break;
		case "_070":
			sparqlQuery = Sequences._070;
			break;
		case "_100":
			sparqlQuery = Sequences._100;
			break;
		case "_110":
			sparqlQuery = Sequences._110;
			break;
		case "_120":
			sparqlQuery = Sequences._120;
			break;
		case "_130":
			sparqlQuery = Sequences._130;
			break;
		case "_140":
			sparqlQuery = Sequences._140;
			break;
		case "_150":
			sparqlQuery = Sequences._150;
			break;
		case "_160":
			sparqlQuery = Sequences._160;
			break;
		case "_170":
			sparqlQuery = Sequences._170;
			break;
		case "_200":
			sparqlQuery = Sequences._200;
			break;
		case "_210":
			sparqlQuery = Sequences._210;
			break;
		case "_220":
			sparqlQuery = Sequences._220;
			break;
		case "_230":
			sparqlQuery = Sequences._230;
			break;
		case "_240":
			sparqlQuery = Sequences._240;
			break;
		case "_250":
			sparqlQuery = Sequences._250;
			break;
		case "_260":
			sparqlQuery = Sequences._260;
			break;
		case "_270":
			sparqlQuery = Sequences._270;
			break;
		case "_300":
			sparqlQuery = Sequences._300;
			break;
		case "_310":
			sparqlQuery = Sequences._310;
			break;
		case "_320":
			sparqlQuery = Sequences._320;
			break;
		case "_330":
			sparqlQuery = Sequences._330;
			break;
		case "_340":
			sparqlQuery = Sequences._340;
			break;
		case "_350":
			sparqlQuery = Sequences._350;
			break;
		case "_360":
			sparqlQuery = Sequences._360;
			break;
		case "_370":
			sparqlQuery = Sequences._370;
			break;
		case "_400":
			sparqlQuery = Sequences._400;
			break;
		case "_410":
			sparqlQuery = Sequences._410;
			break;
		case "_420":
			sparqlQuery = Sequences._420;
			break;
		case "_430":
			sparqlQuery = Sequences._430;
			break;
		case "_440":
			sparqlQuery = Sequences._440;
			break;
		case "_450":
			sparqlQuery = Sequences._450;
			break;
		case "_460":
			sparqlQuery = Sequences._460;
			break;
		case "_470":
			sparqlQuery = Sequences._470;
			break;
		case "_021":
			sparqlQuery = Sequences._021;
			break;
		case "_041":
			sparqlQuery = Sequences._041;
			break;
		case "_061":
			sparqlQuery = Sequences._061;
			break;
		case "_081":
			sparqlQuery = Sequences._081;
			break;
		case "_0101":
			sparqlQuery = Sequences._0101;
			break;
		case "_201":
			sparqlQuery = Sequences._201;
			break;
		case "_221":
			sparqlQuery = Sequences._221;
			break;
		case "_241":
			sparqlQuery = Sequences._241;
			break;
		case "_261":
			sparqlQuery = Sequences._261;
			break;
		case "_281":
			sparqlQuery = Sequences._281;
			break;
		case "_2101":
			sparqlQuery = Sequences._2101;
			break;
		case "_401":
			sparqlQuery = Sequences._401;
			break;
		case "_421":
			sparqlQuery = Sequences._421;
			break;
		case "_441":
			sparqlQuery = Sequences._441;
			break;
		case "_461":
			sparqlQuery = Sequences._461;
			break;
		case "_481":
			sparqlQuery = Sequences._481;
			break;
		case "_4101":
			sparqlQuery = Sequences._4101;
			break;
		case "_601":
			sparqlQuery = Sequences._601;
			break;
		case "_621":
			sparqlQuery = Sequences._621;
			break;
		case "_641":
			sparqlQuery = Sequences._641;
			break;
		case "_661":
			sparqlQuery = Sequences._661;
			break;
		case "_681":
			sparqlQuery = Sequences._681;
			break;
		case "_6101":
			sparqlQuery = Sequences._6101;
			break;
		default:
			
			break;
		}
		
		//System.out.println(sparqlQuery.indexOf("*"));
		if(!this.topics.isEmpty()){
			sparqlQuery = sparqlQuery.replace("*", this.topics);
		} 
		
		this.sparqlQuery = sparqlQuery + " " + options;

	}

	/**
	 * @return sparql query
	 */
	public String getSparqlQuery() {
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
		Allocator alloc = new Allocator("Show me games by Wilhelm Steinitz.");

		alloc.allocateSequence();
		
		System.out.println(alloc.regexEntities(alloc.getSparqlQuery()));

	}

}