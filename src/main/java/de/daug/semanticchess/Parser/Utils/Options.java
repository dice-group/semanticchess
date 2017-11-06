package de.daug.semanticchess.Parser.Utils;

/**
 * This class adds modifiers to the SPARQL query.
 */
public class Options {

	private static final String LIMIT = "LIMIT ";
	private static final String OFFSET = "OFFSET ";
	private static final String ORDERBY = "ORDER BY ";
	private static final String GROUPBY = "GROUP BY ";
	private static final String HAVING = "HAVING ";

	private String limitStr ="";
	private String offsetStr = "";
	private String orderStr = "";
	private String groupStr = "";
	private String havingStr ="";

	/**
	 * empty constructor
	 */
	public Options() {
	}

	/**
	 * get the LIMIT modifier
	 * @return limitStr
	 */
	public String getLimitStr() {
		return limitStr;
	}

	/**
	 * set limit string
	 * @param limit: integer
	 */
	public void setLimitStr(int limit) {
		if(limitStr.isEmpty()){
			this.limitStr = LIMIT + limit;
		}
	}

	/**
	 * get the OFFSET modifier
	 * @return offsetStr
	 */
	public String getOffsetStr() {
		return offsetStr;
	}

	/**
	 * set offset string
	 * @param offset: integer
	 */
	public void setOffsetStr(int offset) {
		this.offsetStr = OFFSET + offset;
	}

	/**
	 * get the ORDER BY modifier
	 * @return orderStr
	 */
	public String getOrderStr() {
		return orderStr;
	}

	/**
	 * set order String
	 * @param orderDirection: ASC or DESC
	 * @param order: class that should be ordered
	 */
	public void setOrderStr(String orderDirection, String order) {
		if (orderStr.equals("")) {
			this.orderStr = ORDERBY + orderDirection + "(" + order + ") ";
		} else {
			this.orderStr += orderDirection + "(" + order + ") ";
		}
	}
	
	/**
	 * get the GROUB BY modifier
	 * @return groupStr
	 */
	public String getGroupStr() {
		return groupStr;
	}

	/**
	 * set GROUP BY String
	 * @param group
	 */
	public void setGroupStr(String group) {
		if (groupStr.equals("")) {
			this.groupStr = GROUPBY + group + " ";
		} else {
			this.groupStr += group + " ";
		}
	}
	
	/**
	 * get the HAVING modifier
	 * @return havingStr
	 */
	public String getHavingStr() {
		return havingStr;
	}

	/**
	 * set HAVING String
	 * @param havingStr
	 */
	public void setHavingStr(String having) {
		if (havingStr.equals("")) {
			this.havingStr = HAVING + having + " ";
		} else {
			this.havingStr += having + " ";
		}
	}

	/**
	 * returns a String with modifiers
	 */
	@Override
	public String toString() {

		if (limitStr == null && offsetStr == null && orderStr == null && groupStr == null) {
			return "";
		} else if (limitStr != null && offsetStr != null && orderStr == null && groupStr == null) {
			return limitStr + " " + offsetStr;
		} else if (limitStr != null && offsetStr != null && orderStr != null && groupStr == null) {
			return orderStr + " " + limitStr + " " + offsetStr;
		} else if (limitStr != null && offsetStr != null && orderStr != null && groupStr != null && havingStr == null) {
			return groupStr+ " " +orderStr + " " + limitStr + " " + offsetStr;
		} else if (limitStr != null && offsetStr != null && orderStr != null && groupStr != null && havingStr != null) {
			return groupStr+ " " + havingStr + " " +orderStr + " " + limitStr + " " + offsetStr;
		} else if (limitStr == null && offsetStr == null && orderStr != null && groupStr == null) {
			return orderStr;
		} else if (limitStr == null && offsetStr == null && orderStr != null && groupStr != null && havingStr == null) {
			return groupStr + " " + orderStr;
		} else if (limitStr == null && offsetStr == null && orderStr != null && groupStr != null && havingStr != null) {
			return groupStr + " " + havingStr + " " + orderStr;
		} else if (limitStr == null && offsetStr == null && orderStr == null && groupStr != null && havingStr == null) {
			return groupStr;
		} else if (limitStr == null && offsetStr == null && orderStr == null && groupStr != null && havingStr != null) {
			return groupStr + " " + havingStr;
		}

		return limitStr + " " + offsetStr;
	}

}