package de.daug.semanticchess.Annotation;


//TODO fox entities to object

import org.aksw.qa.annotation.index.IndexDBO_classes;
import org.aksw.qa.annotation.index.IndexDBO_properties;
import org.aksw.qa.annotation.spotter.ASpotter;
import org.aksw.qa.annotation.spotter.Fox;
//import org.aksw.qa.annotation.spotter.Spotlight;
//import org.aksw.qa.annotation.util.NifEverything;
import org.aksw.qa.commons.datastructure.Entity;

import org.apache.jena.rdf.model.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.Iterator;
import java.util.List;


import org.aksw.hawk.datastructures.HAWKQuestion;

/**
 * @desc Analyzes the input question of the user and finds entities, classes and
 *       properties
 * @author jorn-henningdaug
 *
 */
@Deprecated
public class Annotations {

	// attributes
	private String query = "";
	private String language = "";
	public HashMap<String, List<String>> queryClasses = null;
	public HashMap<String, List<String>> queryProperties = null;
	private	List<FoxEntity> foxEntity = new ArrayList<FoxEntity>();
	private HAWKQuestion hawkQuestion = null;
	private ASpotter fox = null;
	private IndexDBO_classes classes = null; 
	private IndexDBO_properties properties = null;

	/**
	 * constructor
	 * 
	 * @param query:
	 *            input question
	 * @param language:
	 *            choosen query language
	 */
	public Annotations(String query, String language) {
		this.query = query;
		this.language = language;
		this.hawkQuestion = new HAWKQuestion();
		this.fox = new Fox();
		this.classes = new IndexDBO_classes();
		this.properties = new IndexDBO_properties();
		// init();
	}

	/**
	 * gets entities with the help of fox.aksw.org like 'Magnus Carlsen' and ' Viswanathan Anand'
	 */
	public void initFox(HAWKQuestion q, ASpotter fox) {	
		
		q.getLanguageToQuestion().put(this.language, this.query);		
		q.setLanguageToNamedEntites(fox.getEntities(q.getLanguageToQuestion().get(this.language)));
		
		
		for (String key : q.getLanguageToNamedEntites().keySet()) {
			
			for (Entity entity : q.getLanguageToNamedEntites().get(key)) {
				String label = "";
				String type = "";
				List<String> posTypesAndCategories = new ArrayList<String>();
				List<String> uris = new ArrayList<String>();
				
				label = entity.getLabel();
				type = entity.getType();
				
				//System.out.println("\t" + entity.getLabel() + " ->" + entity.getType() + entity.getPosTypesAndCategories());
				for (Resource r : entity.getPosTypesAndCategories()) {
					posTypesAndCategories.add(r.getURI());
					//System.out.println("===");
					//System.out.println("\t\tpos: " + r.getLocalName());
				}
				for (Resource r : entity.getUris()) {
					uris.add(r.getURI());
					//System.out.println("\t\turi: " + r);
				}
			
				foxEntity.add(new FoxEntity(label,type,posTypesAndCategories,uris));
			}
		}
	}

	/**
	 * gets classes like 'art' and 'dog'
	 */
	public void initIndexDBO_classes(IndexDBO_classes classes, String query) {
		try{
			queryClasses.clear();
		}catch(NullPointerException e){
			
		}
		queryClasses = new HashMap<String, List<String>>();
		
		String[] words = query.split("\\W+");

		for (String word : words) {
			List<String> wordClasses = classes.search(word);
			queryClasses.put(word, wordClasses);
		}

		Iterator it = queryClasses.entrySet().iterator();
		
		while (it.hasNext()) { HashMap.Entry pair = (HashMap.Entry)
		it.next(); System.out.println(pair.getKey() + " = " +
		pair.getValue()); it.remove(); // ConcurrentModificationException
		}

		// https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap

	}

	/**
	 * gets properties like 'win', 'come' and 'can'
	 */
	public void initIndexDBO_properties(IndexDBO_properties properties, String query) {
		try{
			queryProperties.clear();
		}catch(NullPointerException e){
			
		}
		queryProperties = new HashMap<String, List<String>>();
		
		String[] words = query.split("\\W+");

		for (String word : words) {
			List<String> wordProperties = properties.search(word);
			queryProperties.put(word, wordProperties);
		}

		
		 Iterator it = queryProperties.entrySet().iterator();
		 
		 while (it.hasNext()) { HashMap.Entry pair = (HashMap.Entry)
		 it.next(); System.out.println(pair.getKey() + " = " +
		 pair.getValue()); it.remove(); // avoids a
		 }
		 
		// https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
	}

	/**
	 * @return user query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @return choosen language for analysis
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * @return hawkQuestion from constructor
	 */
	public HAWKQuestion getHAWKquestion(){
		return this.hawkQuestion;
	}
	
	public ASpotter getFox(){
		return this.fox;
	}
	
	public IndexDBO_classes getClasses(){
		return this.classes;
	}
	
	public IndexDBO_properties getProperties(){
		return this.properties;
	}

	/**
	 * @param query:
	 *            sets a new query
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @param language:
	 *            seta a new language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * main method for testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		//1. Question
		Annotations annotate = new Annotations("Show me wins by Carlsen.",
				"en");
		//Entities
		annotate.initFox(annotate.getHAWKquestion(), annotate.getFox());
		for(FoxEntity fe : annotate.foxEntity){
			System.out.println(fe.toString());
			System.out.println("==============");
		}
		//classes
		System.out.println("Classes: \n");
		
		annotate.initIndexDBO_classes(annotate.getClasses(), "Show me wins Carlsen.");
		//properties
		System.out.println("\n");
		System.out.println("Properties: ");
		annotate.initIndexDBO_properties(annotate.getProperties(), "Show me wins by Carlsen.");
		
		System.out.println("\n\n");
		
		//2. Question
		annotate = new Annotations("What is the capital of Germany? ",
				"en");
		//annotate.initIndexDBO_classes();
		// annotate.initIndexDBO_properties();
		annotate.initFox(annotate.getHAWKquestion(), annotate.getFox());
		for(FoxEntity fe : annotate.foxEntity){
			System.out.println(fe.toString());
			System.out.println("==============");
		}
		//classes
		System.out.println("Classes: \n");
		annotate.initIndexDBO_classes(annotate.getClasses(), "Which buildings in art dog style did Shreve, Lamb and Harmon design?");
		//properties
		System.out.println("\n");
		System.out.println("Properties: ");
		annotate.initIndexDBO_properties(annotate.getProperties(),"can win come get");

	}
	
	//=========================================================================================
	/**
	 * class for an entity
	 */
	private class FoxEntity{
		private String label = "";
		private String type = "";
		private List<String> posTypesAndCategories = new ArrayList<String>();
		private List<String> uris = new ArrayList<String>();
		
		/**
		 * constructor
		 * @param label: name of the entity
		 * @param type
		 * @param posTypesAndCategories 
		 * @param uris: links to entity
		 */
		public FoxEntity(String label, String type, List<String> posTypesAndCategories, List<String> uris){
			this.label = label;
			this.type = type;
			this.posTypesAndCategories = posTypesAndCategories;
			this.uris = uris;
		}
		
		/**
		 * gets name of the entity
		 * @return label
		 */
		public String getLabel(){
			return this.label;
		}
		
		/**
		 * gets type of the entity
		 * @return type
		 */
		public String getType(){
			return this.type;
		}
		
		/**
		 * gets Pos types and categories of the entity
		 * @return posTypesAndCategories
		 */
		public List<String> getPosTypesAndCategories(){
			return this.posTypesAndCategories;
		}
		
		/**
		 * gets uris of the entity
		 * @return uris
		 */
		public List<String> getUris(){
			return this.uris;
		}
		
		/**
		 * converts Entity to String
		 */
		public String toString(){		
			StringBuilder posAndCat = new StringBuilder();
			for (String s : this.posTypesAndCategories){
				posAndCat.append("\n\t");
				posAndCat.append(s);
			}
			
			StringBuilder uris = new StringBuilder();
			for (String s : this.uris){
				uris.append("\n\t");
				uris.append(s);
			}
			
			return "Name: " + label + "\nType: " + type + "\nPosTypesAndCategories: " + posAndCat + "\nUris: " + uris;
		}
		
	}
	

}


