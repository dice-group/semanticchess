package de.daug.semanticchess.Parser.Utils;

import java.util.ArrayList;

/**
 * This class takes the entities the subStringEntities method (in Allocator.java) have found
 * and permutates them so that every combination will be considered in the final string.
 * The class return return a VALUES clause for SPARQL as String.
 * 
 * For an example run the main method in this class.
 */
public class Values {

	//variable names
	private String valueVars = "";

	//needed for permutation
	private ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
	private ArrayList<String> permutation = new ArrayList<String>();
	
	/**
	 * empty constructor
	 */
	public Values(){	
	}
	
	/**
	 * String with all variable names
	 * if there are two entities, it will produce e.g ?value1 and ?value2
	 * @param valueVar: variable name
	 * @return String with variable names
	 */
	public String setValueVarsStr(String valueVar){
		return this.valueVars += valueVar + " ";
	}
	
	/**
	 * adds entity array to the result array
	 * @param result: array of found entities
	 */
	public void addResult(ArrayList<String> result){
		this.results.add(result);
	}
	
	/**
	 * Source of this method: https://stackoverflow.com/a/17193002
	 * does a permutation over the arrays (result) in the results array
	 * @param Lists: starting list
	 * @param result: final list
	 * @param depth: in this case zero
	 * @param current: at first empty, builds up
	 */
	public void generatePermutations(ArrayList<ArrayList<String>> Lists, ArrayList<String> result, int depth, String current)
	{	
	    if(depth == Lists.size())
	    {       
	       result.add(current);
	       this.permutation = result;
	       return;
	     }
	    for(int i = 0; i < Lists.get(depth).size(); ++i)
	    {
	        generatePermutations(Lists, result, depth + 1, current + Lists.get(depth).get(i));
	    }
	}
	
	/**
	 * return the VALUES clause
	 */
	@Override
	public String toString(){
		if(this.getValueVars().equals("")){
			return "";
		}
		
		String values = "VALUES ("+ this.getValueVars() + ") {";
		for (String p : permutation){
			values += "(" + p + ")";
		}
		values += "}";
		
		return values;
	}
	
	/**
	 * get all variable names
	 * @return String: variable names
	 */
	public String getValueVars() {
		return valueVars;
	}
	
	/**
	 * set variable names
	 * @param valueVars: variable name
	 */
	public void setValueVars(String valueVars) {
		this.valueVars += valueVars + " ";
	}

	/**
	 * the collection of found entities
	 * @return: array of entity names
	 */
	public ArrayList<ArrayList<String>> getResults() {
		return results;
	}

	/**
	 * set the collection of entities
	 * @param results: array with arrays of entity names
	 */
	public void setResults(ArrayList<ArrayList<String>> results) {
		this.results = results;
	}
	
	/**
	 * get the result of the permutation
	 * @return: permutation result
	 */
	public ArrayList<String> getPermutation() {
		return this.permutation;
	}

	/**
	 * set the result of the permutation
	 * @param result: resulting ArrayList 
	 */
	public void setPermutation(ArrayList<String> result) {
		this.permutation = result;
	}

	/**
	 * example:
	 * The user asks: 'Show me games between Steinitz and Lasker'.
	 * It is not clear (for the engine) if the user means V Steinitz or Wilhelm Steinitz
	 * respectively Emanuel Lasker or Edward Lasker.
	 * So the engine will return two arrays: 
	 * ["V Steinitz","Wilhelm Steinitz"] and ["Emanuel Lasker","Edward Lasker"]
	 * Now the class constructs a VALUES clause, so that every math will be considered:
	 * - Wilhelm Steinitz vs Emanuel Lasker
	 * - Wilhelm Steinitz vs Edward Lasker
	 * - V Steinitz vs Emanuel Lasker
	 * - V Steinitz vs Edward Lasker
	 * @param args
	 */
	public static void main (String[] args){
		Values v = new Values();
		ArrayList<String> a = new ArrayList<String>();
		ArrayList<String> b = new ArrayList<String>();
		
		a.add("'Wilhelm Steinitz'");
		b.add("'Emanuel Lasker'");
		
		a.add("'V Steinitz'");
		b.add("'Edward Lasker'");

		v.addResult(a);
		v.addResult(b);

		//v.setResultStr();
		
		v.setValueVars("?value1");
		v.setValueVars("?value2");
		
		System.out.println(v.results);
		
		String tempStr = "";
		v.generatePermutations(v.results, v.getPermutation(), 0, tempStr);
		System.out.println("Result: " + v.toString());
		
	}
	
}