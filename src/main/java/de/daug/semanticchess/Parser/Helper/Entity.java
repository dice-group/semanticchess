package de.daug.semanticchess.Parser.Helper;

/**
 * found entity in the user query
 *
 */
public class Entity {

	private final static String entityLabel = "E_";
	private final static String propertyLabel = "P_";
	private final static String resourceLabel = "R_";
	
	private String propertyPrefix;
	private String entityId;
	private String propertyId;
	private String resourceId;

	private String entityName;
	private String propertyName;
	private String resourceName;

	private int startPosition;
	private int endPosition;
	
	/**
	 * constructor
	 * @param counter: counts how often a class was find in the query
	 * @param entityName: name of the entity
	 * @param propertyName: property of the entity
	 * @param startPosition: start position in the query
	 * @param endPosition: end position in the query
	 * @param resourceName: name of the resource (e.g. game, moves,...)
	 */
	public Entity(int counter, String entityName, String propertyPrefix, String propertyName, int startPosition, int endPosition,
			String resourceName) {
		this.entityId = entityLabel + counter;
		this.propertyId = propertyLabel + counter;
		this.resourceId = resourceLabel + counter;

		this.entityName = entityName;
		this.propertyPrefix = propertyPrefix;
		this.propertyName = propertyPrefix + propertyName;
		this.resourceName = resourceName;

		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}
	
	/**
	 * get entity id
	 * @return entityId
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * set entity id
	 * @param entityId
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * get property id
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
	 * set resource Id
	 * @param resourceId
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * get entity name
	 * @return entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * set entity Name
	 * @param entityName
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * get property name
	 * @return
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
	 * get start position
	 * @return startPosition
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * set start position
	 * @param startPosition
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * get end position
	 * @return endPosition
	 */
	public int getEndPosition() {
		return endPosition;
	}
	
	/**
	 * set end position
	 * @param endPosition
	 */
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
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
		return "Entity [entityId=" + entityId + ", propertyId=" + propertyId + ", resourceId=" + resourceId
				+ ", entityName=" + entityName + ", propertyName=" + propertyName + ", resourceName=" + resourceName
				+ ", startPosition=" + startPosition + ", endPosition=" + endPosition + "]";
	}

}