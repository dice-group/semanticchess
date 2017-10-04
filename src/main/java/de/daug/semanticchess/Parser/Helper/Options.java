package de.daug.semanticchess.Parser.Helper;

/**
 * options for the sparql query
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
	 * constructor
	 */
	public Options() {
	}

	/**
	 * get limit
	 * 
	 * @return limitStr
	 */
	public String getLimitStr() {
		return limitStr;
	}

	/**
	 * set limit string
	 * 
	 * @param limit:
	 *            integer
	 */
	public void setLimitStr(int limit) {
		this.limitStr = LIMIT + limit;
	}

	/**
	 * get offset
	 * 
	 * @return offsetStr
	 */
	public String getOffsetStr() {
		return offsetStr;
	}

	/**
	 * set offset string
	 * 
	 * @param offset:
	 *            integer
	 */
	public void setOffsetStr(int offset) {
		this.offsetStr = OFFSET + offset;
	}

	/**
	 * get order string
	 * 
	 * @return
	 */
	public String getOrderStr() {
		return orderStr;
	}

	/**
	 * set order String
	 * 
	 * @param orderDirection:
	 *            ASC or DESC
	 * @param order:
	 *            class that should be ordered
	 */
	public void setOrderStr(String orderDirection, String order) {
		if (orderStr.equals("")) {
			this.orderStr = ORDERBY + orderDirection + "(" + order + ") ";
		} else {
			this.orderStr += orderDirection + "(" + order + ") ";
		}
	}

	public String getGroupStr() {
		return groupStr;
	}

	public void setGroupStr(String group) {
		if (groupStr.equals("")) {
			this.groupStr = GROUPBY + group + " ";
		} else {
			this.groupStr += group + " ";
		}
	}

	/**
	 * toString method
	 */
	@Override
	public String toString() {

		if (limitStr == null && offsetStr == null && orderStr == null && groupStr == null) {
			return "";
		} else if (limitStr != null && offsetStr != null && orderStr == null && groupStr == null) {
			return limitStr + " " + offsetStr;
		} else if (limitStr != null && offsetStr != null && orderStr != null && groupStr == null) {
			return orderStr + " " + limitStr + " " + offsetStr;
		} else if (limitStr != null && offsetStr != null && orderStr != null && groupStr != null) {
			return groupStr+ " " +orderStr + " " + limitStr + " " + offsetStr;
		} else if (limitStr == null && offsetStr == null && orderStr != null && groupStr == null) {
			return orderStr;
		} else if (limitStr == null && offsetStr == null && orderStr != null && groupStr != null) {
			return groupStr + " " + orderStr;
		}
		else if (limitStr == null && offsetStr == null && orderStr == null && groupStr != null) {
			return groupStr;
		}

		return limitStr + " " + offsetStr;
	}

}