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
			this.filter = "FILTER ("+ classesName +" > " + filterValue;
			this.isFirstFilter = false;
		} else {
			this.filter += " && "+ classesName +" > " + filterValue;
		}
	}
	
	/**
	 * filter for phrases with 'lower than'
	 */
	public void addLowerThan(String classesName, String filterValue){
		if(isFirstFilter){
			this.filter = "FILTER ("+ classesName +" < " + filterValue;
			this.isFirstFilter = false;
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
				this.filter = "FILTER ( regex( " + classesName + ", '" + regex + "')";
				this.isFirstFilter = false;
			} else {
				this.filter = "FILTER ( regex( " + classesName + ", '" + regex + "' "+ ",'i' " + ")";
				this.isFirstFilter = false;
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
		if(filter.equals("")){
			return "";
		}
		else {
			return filter + ")";
		}
		
	}

	/**
	 * set filter
	 * @param filter
	 */
	public void setFilterStr(String filter) {
		this.filter = filter;
	}

	public boolean isFirstFilter() {
		return isFirstFilter;
	}

	public void setFirstFilter(boolean isFirstFilter) {
		this.isFirstFilter = isFirstFilter;
	}
	
	
	
}