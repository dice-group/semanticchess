package de.daug.semanticchess.Parser.Helper;

/**
 * Object for classes: unnamed entities
 * e.g. "Show me tournaments with Magnus Carlsen"
 * "Magnus Carlsen" -> entity 
 * "tournament" -> unspecified, so it should be a class
 */
public class Classes {

	private final static String classesLabel = "C_";
	private final static String propertyLabel = "D_";
	private final static String resourceLabel = "S_";
	
	private String propertyPrefix;
	private String classesId;
	private String propertyId;
	private String resourceId;

	private String classesName;
	private String propertyName;
	private String resourceName;

	private int position;
	
	/**
	 * constructor
	 * @param counter: counts how often a class was find in the query
	 * @param classesName: name of the class
	 * @param propertyName: property of the class
	 * @param position: postion in the query
	 * @param resourceName: name of the resource (e.g. game, moves,...)
	 */
	public Classes(int counter, String classesName, String propertyPrefix, String propertyName, int position, String resourceName) {
		this.classesId = classesLabel + counter;
		this.propertyId = propertyLabel + counter;
		this.resourceId = resourceLabel + counter;

		this.propertyPrefix = propertyPrefix;
		this.classesName = classesName;
		this.propertyName = propertyPrefix + propertyName;
		this.resourceName = resourceName;

		this.position = position;
	}
	
	/**
	 * get the class id
	 * @return classesId
	 */
	public String getClassesId() {
		return classesId;
	}

	/**
	 * set the class id
	 * @param classesId
	 */
	public void setClassesId(String classesId) {
		this.classesId = classesId;
	}
	
	/**
	 * get the property id
	 * @return propertyId
	 */
	public String getPropertyId() {
		return propertyId;
	}
	
	/**
	 * set property id
	 * @param propertyId
	 */
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	/**
	 * get resource id
	 * @return resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * set resource id
	 * @param resourceId
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * get class name
	 * @return classesName
	 */
	public String getClassesName() {
		return classesName;
	}
	
	/**
	 * set class name
	 * @param classesName
	 */
	public void setClassesName(String classesName) {
		this.classesName = classesName;
	}
	
	/**
	 * get property name
	 * @return propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * set property name
	 * @param propertyName
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * get resource name
	 * @return resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}
	
	/**
	 * set resource name
	 * @param resourceName
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * get position
	 * @return position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * set position
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	public String getPropertyPrefix() {
		return propertyPrefix;
	}

	public void setPropertyPrefix(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
	}

	/**
	 * toString method
	 */
	@Override
	public String toString() {
		return "Classes [classesId=" + classesId + ", propertyId=" + propertyId + ", resourceId=" + resourceId
				+ ", classesName=" + classesName + ", propertyName=" + propertyName + ", resourceName=" + resourceName
				+ ", position=" + position + "]";
	}

}