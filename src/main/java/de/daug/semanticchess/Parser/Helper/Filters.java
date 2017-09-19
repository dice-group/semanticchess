package de.daug.semanticchess.Parser.Helper;

/**
 * filter for the sparql query
 */
public class Filters{
	
	public String filter = "";
	private boolean isFirstFilter = true;
	
	/**
	 * constructor
	 */
	public Filters(){
		
	}
	
	/**
	 * filter for phrases with 'greater than'
	 */
	public void addGreaterThan(String classesName, String filterValue){
		if(isFirstFilter){
			this.filter = "FILTER "+ classesName +" > " + filterValue;
		} else {
			this.filter += " && "+ classesName +" > " + filterValue;
		}
	}
	
	/**
	 * filter for phrases with 'lower than'
	 */
	public void addLowerThan(String classesName, String filterValue){
		if(isFirstFilter){
			this.filter = "FILTER "+ classesName +" < " + filterValue;
		} else {
			this.filter += " && "+ classesName +" < " + filterValue;
		}
	}
	
	/**
	 * filter with regex function
	 * @param classesName: name of the class that should be filtered
	 * @param regex: regular expression
	 * @param caseSensitiv: acitvate case sensitivity
	 */
	public void addRegex(String classesName, String regex, boolean caseSensitiv){
		if(isFirstFilter){
			if(!caseSensitiv){
				this.filter = "FILTER regex( " + classesName + ", '" + regex + "')";
			} else {
				this.filter = "FILTER regex( " + classesName + ", '" + regex + "' "+ ",'i' " + ")";
			}
		}else {
			if(!caseSensitiv){
				this.filter += " && regex( " + classesName + ", '" + regex + "')";
			} else {
				this.filter += " && regex( " + classesName + ", '" + regex + "' "+ ",'i' " + ")";
			}
		}
		
	}

	/**
	 * get filter
	 * @return filter
	 */
	public String getFilterStr() {
		return filter;
	}

	/**
	 * set filter
	 * @param filter
	 */
	public void setFilterStr(String filter) {
		this.filter = filter;
	}
	
	
	
}