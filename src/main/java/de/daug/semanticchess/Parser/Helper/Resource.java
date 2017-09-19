package de.daug.semanticchess.Parser.Helper;

@Deprecated
public class Resource{
	
	private final static String resourceLabel = "R_";
	
	private final static String resource = "a prop:";
	
	private String resourceId;

	private String resourceName;
	private String propertyName;
	
	private int position;
	
	public Resource(int counter, String resourceName, String propertyName, int position){
		this.resourceId = resourceLabel + counter;
		

		this.resourceName = resourceName;
		this.propertyName = resource + propertyName;
		
		this.position = position;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "Resource [resourceId=" + resourceId + ", resourceName=" + resourceName + ", propertyName="
				+ propertyName + ", position=" + position + "]";
	}
	
	
	
}