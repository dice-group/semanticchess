package de.daug.semanticchess.Parser.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class KeywordVocabulary{
	
	// Map for "synonyms"
	public Map<String, ArrayList<String>> PROPERTIES = new HashMap<String, ArrayList<String>>();
	// inversed Map
	public Map<String, String> INVERSED_PROPERTIES = new HashMap<String, String>();

	/**
	 * constructor fill the maps
	 */
	public KeywordVocabulary() {
		fillProperties();
		inverseProperties();
	}
	
	public void fillProperties(){
		
	}
	
	
	
	/**
	 * inverse the properties
	 */
	public void inverseProperties() {
		for (HashMap.Entry<String, ArrayList<String>> entry : PROPERTIES.entrySet()) {
			for (String prop : entry.getValue()) {
				// System.out.println(prop + " " + entry.getKey());
				INVERSED_PROPERTIES.put(prop, entry.getKey());
			}
		}
	}
	
	
}