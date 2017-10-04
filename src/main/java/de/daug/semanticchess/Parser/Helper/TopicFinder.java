package de.daug.semanticchess.Parser.Helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicFinder {

	private Set<String> topics = new HashSet<String>();

	private Set<String> blacklist = new HashSet<String>();
	boolean isAlgebra = true;
	private String max = "";
	private String min = "";
	private String avg = "";
	private String count = "";

	public TopicFinder() {

	}

	public Set<String> collectTopics(List<Entity> entities, List<Classes> classes) {
		int firstEntityPosition = 999;
		int secondEntityPosition = 999;
		int firstClassesPosition = 999;

		try {
			firstEntityPosition = entities.get(0).getStartPosition();

		} catch (Exception err) {

		}

		try {
			secondEntityPosition = entities.get(1).getStartPosition();
		} catch (Exception err) {

		}

		try {
			firstClassesPosition = classes.get(0).getPosition();
		} catch (Exception err) {

		}

		boolean entityIsEmpty = false;
		try {

			try {
				if (entities.get(0).getEntityName().isEmpty()) {
					topics.add(entities.get(0).getResourceName());
					entityIsEmpty = true;
				}
			} catch (Exception err) {
				entityIsEmpty = true;
			}
			System.out.println(classes.toString());
			for (Classes c : classes) {

				if (!entityIsEmpty) {
					if (c.getPosition() < firstEntityPosition) {
						if (!blacklist.contains(c.getClassesName())) {
							topics.add(c.getClassesName());

						}
						break;
					}
				} else {

					if (c.getPosition() < secondEntityPosition) {
						if (!blacklist.contains(c.getClassesName())) {
							topics.add(c.getClassesName());

						}
					}
				}

			}

			if (topics.isEmpty()) {
				for (Entity e : entities) {
					if (e.getStartPosition() < firstClassesPosition && e.getEntityName().isEmpty()) {
						topics.add(e.getResourceName());
					}
				}

			}
			
//			if(topics.isEmpty()){
//				for (Entity e : entities) {
//					topics.add(e.getResourceName());
//					break;
//				}
//				
//			}
//			if(topics.isEmpty()){
//				for (Classes c : classes) {
//					topics.add(c.getResourceName());
//					break;
//				}
//				
//			} 

		} catch (Exception err) {

		}

		return this.topics;
	}

	public void addCount(String countThis) {
		this.isAlgebra = true;
		this.count = countThis;

	}

	public void addMax(String maxThis) {
		this.isAlgebra = true;
		this.max = maxThis;

	}

	public void addMin(String minThis) {
		this.isAlgebra = true;
		this.min = minThis;

	}

	public void addAvg(String avgThis) {
		this.isAlgebra = true;
		this.avg = avgThis;

	}

	public Set<String> getTopics() {
		return topics;
	}

	public void setTopics(Set<String> topics) {
		this.topics = topics;
	}

	public boolean isAlgebra() {
		return isAlgebra;
	}

	public void setAlgebra(boolean isAlgebra) {
		this.isAlgebra = isAlgebra;
	}

	public void addToBlacklist(String topic) {
		this.blacklist.add(topic);
	}

	public String getString() {
		String topicStr = "";
		String[] topicArray = topics.toArray(new String[topics.size()]);
		for (int i = topicArray.length - 1; i >= 0; i--) {

			topicStr += topicArray[i] + " ";
		}

		if (!avg.isEmpty() && !max.isEmpty() && min.isEmpty()) {
			topicStr += "(AVG(" + avg + ") AS ?nr) ";
		} else if (!avg.isEmpty() && max.isEmpty() && !min.isEmpty()) {
			topicStr += "(AVG(" + avg + ") AS ?nr) ";
		} else if (!avg.isEmpty() && max.isEmpty() && min.isEmpty()) {
			topicStr += "(AVG(" + avg + ") AS ?nr) ";
		} else if (!max.isEmpty()) {
			topicStr += "(MAX(" + max + ") AS ?nr) ";
		} else if (!min.isEmpty()) {
			topicStr += "(MIN(" + min + ") AS ?nr) ";
		} else if (!count.isEmpty()) {
			topicStr += "(COUNT(" + count + ") AS ?nr) ";
		}

		return topicStr;
	}

}