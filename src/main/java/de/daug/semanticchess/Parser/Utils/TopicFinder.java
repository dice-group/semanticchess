package de.daug.semanticchess.Parser.Utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class tries to find the topic of the user query
 * respectively what the user wants to see as result.
 * Therefore the class looks at the first entity position and
 * collects all classes (e.g when, where, who) in front of it.
 * If there are no classes it takes all classes in front of the second entity position.
 * If it there are still no classes it takes a resource name (e.g ?game) as topic.
 */
public class TopicFinder {

	//Set of topics
	private Set<String> topics = new HashSet<String>();
	
	//blacklist, if you do not want that certain topics appear
	private Set<String> blacklist = new HashSet<String>();
	
	//algebraic function that could appear in the SELECT clause
	private boolean isAlgebra;
	private boolean isCount;
	private String max = "";
	private String min = "";
	private String avg = "";
	private String count = "";

	/**
	 * empty constructor
	 */
	public TopicFinder() {
	}

	/**
	 * collects all topics
	 * starting position are at 999, so if no entity is found, all classes are topics
	 * @param entities: list of found entities
	 * @param classes: list of found classes
	 * @return Set of topics
	 */
	public Set<String> collectTopics(List<Entity> entities, List<Classes> classes) {
		this.blacklist.add("?moveNr");
		this.blacklist.add("?moves");
		this.blacklist.add("?contEco");
		
		int firstEntityPosition = 999;
		@SuppressWarnings("unused")
		int secondEntityPosition = 999;
		int firstClassesPosition = 999;
		
		// get first entity position
		try {
			firstEntityPosition = entities.get(0).getStartPosition();
		} catch (Exception err) {
		}

		//get second entity position
		try {
			secondEntityPosition = entities.get(1).getStartPosition();
		} catch (Exception err) {
		}

		//get first class position
		try {
			firstClassesPosition = classes.get(0).getPosition();
		} catch (Exception err) {
		}
		
		boolean entityIsEmpty = false;
		try {
			try {
				if (entities.get(0).getEntityName().isEmpty() && !this.isCount) {

					topics.add(entities.get(0).getResourceName());
					entityIsEmpty = true;
				}
			} catch (Exception err) {
				//System.out.println(err);
				entityIsEmpty = true;
			}
			for (Classes c : classes) {
				//collect all classes in front of the first entity position
				if (!entityIsEmpty) {
					if (c.getPosition() < firstEntityPosition) {
						if (!blacklist.contains(c.getClassesName())) {
							topics.add(c.getClassesName());
						}
						break;
					}
				} 
//				//collect all classes in front of the second entity position
//				else {
//					if (c.getPosition() < secondEntityPosition) {
//						if (!blacklist.contains(c.getClassesName())) {
//							topics.add(c.getClassesName());
//						}
//					}
//				}
			}

			if (topics.isEmpty() && !this.isCount) {
				for (Entity e : entities) {
					if (e.getStartPosition() < firstClassesPosition && e.getEntityName().isEmpty()) {
						topics.add(e.getResourceName());
					}
				}

			}

			
			if (this.isAlgebra && topics.isEmpty() && !this.isCount) {
				try {
					topics.add(entities.get(0).getResourceName());
				}catch (Exception err){
					//System.out.println(err);
				}

			}
			
		} catch (Exception err) {
			//System.out.println(err);
		}

		return this.topics;
	}
	
	/**
	 * adds aggregate COUNT to the SELECT clause
	 * @param countThis: entities to count
	 */
	public void addCount(String countThis) {
		setAlgebra(true);
		this.count = countThis;
	}

	/**
	 * adds aggregate MAX to the the SELECT clause
	 * @param maxThis: gets maximum of something
	 */
	public void addMax(String maxThis) {
		setAlgebra(true);
		this.max = maxThis;
	}

	/**
	 * adds aggregate MIN to the the SELECT clause
	 * @param minThis: gets minimum of something
	 */
	public void addMin(String minThis) {
		setAlgebra(true);
		this.min = minThis;
	}
	
	/**
	 * adds aggregate AVG to the the SELECT clause
	 * @param avgThis: gets the average of something
	 */
	public void addAvg(String avgThis) {
		setAlgebra(true);
		this.avg = avgThis;
	}

	/**
	 * returns all topics
	 * @return Set of topics
	 */
	public Set<String> getTopics() {
		return topics;
	}
	
	/**
	 * set the topics
	 * @param topics
	 */
	public void setTopics(Set<String> topics) {
		this.topics = topics;
	}

	/**
	 * checks if algebra is used
	 * @return boolean
	 */
	public boolean isAlgebra() {
		return isAlgebra;
	}

	/**
	 * sets isAlgebra to true or false
	 * @param isAlgebra
	 */
	public void setAlgebra(boolean isAlgebra) {
		this.isAlgebra = isAlgebra;
	}
	
	
	/**
	 * checks if count is used
	 * @return boolean
	 */
	public boolean isCount() {
		return isCount;
	}
	
	/**
	 * sets isCount to true or false
	 * @param isCount
	 */
	public void setCount(boolean isCount) {
		this.isCount = isCount;
	}
	
	/**
	 * adds variables to blacklist
	 * @param topic
	 */
	public void addToBlacklist(String topic) {
		this.blacklist.add(topic);
	}
	
	/**
	 * add a topic
	 * @param topic
	 */
	public void add(String topic){
		this.topics.add(topic);
	}

	/**
	 * checks if ?game is the only topic
	 * @return
	 */
	public boolean onlyGames() {
		if (topics.size() == 1) {
			Iterator<String> iter = topics.iterator();

			if (iter.next().equals("?game")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get the topic String for the SELECT clause
	 * @return input for the SELECT clause
	 */
	public String getString() {
		String topicStr = "";
		String[] topicArray = topics.toArray(new String[topics.size()]);
		for (int i = topicArray.length - 1; i >= 0; i--) {
			topicStr += topicArray[i] + " ";
		}

		if (!avg.isEmpty() && !max.isEmpty() && min.isEmpty()) {
			topicStr += "(AVG(xsd:integer(" + avg + ")) AS ?nr) ";
		} else if (!avg.isEmpty() && max.isEmpty() && !min.isEmpty()) {
			topicStr += "(AVG(xsd:integer(" + avg + ")) AS ?nr) ";
		} else if (!avg.isEmpty() && max.isEmpty() && min.isEmpty()) {
			topicStr += "(AVG(xsd:integer(" + avg + ")) AS ?nr) ";
		} else if (!max.isEmpty()) {
			topicStr += "(MAX(xsd:integer(" + max + ")) AS ?nr) ";
		} else if (!min.isEmpty()) {
			topicStr += "(MIN(xsd:integer(" + min + ")) AS ?nr) ";
		} else if (!count.isEmpty()) {
			topicStr += "(COUNT(" + count + ") AS ?nr) ";
		}

		return topicStr;
	}
}