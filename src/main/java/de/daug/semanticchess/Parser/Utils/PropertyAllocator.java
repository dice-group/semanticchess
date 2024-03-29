package de.daug.semanticchess.Parser.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.daug.semanticchess.Annotation.Token;

/**
 * This class allocates a property to a entity.
 * For example:
 *  - a color to a player
 *  - a ELO rating to a player
 */
public class PropertyAllocator {

	private List<Token> tokens = new ArrayList<Token>();
	private List<Integer> personPositions = new ArrayList<Integer>();
	private int colorPosition;

	public PropertyAllocator(List<Token> tokens) {
		this.tokens = tokens;
		this.personPositions = getPersonPositions();
		this.colorPosition = getFirstColorPosition();
	}

	/**
	 * Finds all persons and save their positions in the query. It is assumed
	 * that for the color allocation only two person exists in the query
	 * @param tokens: List of words, ner and pos
	 * @return List<Integer>: list of person positions
	 */
	public List<Integer> getPersonPositions() {
		List<Integer> personPositions = new ArrayList<Integer>();

		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).getNe().equals("PERSON")) {
				while ((i + 1) < tokens.size() && tokens.get(i + 1).getNe().equals("PERSON")) {
					i += 1;
				}
				personPositions.add(i);
			}
		}
		return personPositions;
	}

	/**
	 * saves the first position of one color
	 * @return integer: color position
	 */
	public int getFirstColorPosition() {
		int colorPosition = 0;

		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).getNe().equals("black") || tokens.get(i).getNe().equals("white")) {
				colorPosition = i;
				break;
			}
		}
		return colorPosition;
	}
	
	/**
	 * saves the first position of an ELO rating
	 * @return integer: ELO position
	 */
	public List<Integer> getEloPositions(){
		List<Integer> eloPosition = new ArrayList<Integer>();
		
		for(int i = 0; i < tokens.size(); i++){
			if(tokens.get(i).getNe().equals("elo")){
				eloPosition.add(i);				
			}
			if(tokens.get(i).getNe().equals("jjs_pos") || tokens.get(i).getNe().equals("jjs_neg") || tokens.get(i).getNe().equals("jjr_pos") || tokens.get(i).getNe().equals("jjr_neg") ){
				if(!tokens.get(i).getWord().equals("longest") && !tokens.get(i).getWord().equals("shortest")){
					eloPosition.add(i);
				}							
			}
		}

		return eloPosition;
	}

	/**
	 * simple distance function: the color is allocated to the person who has the
	 * smallest distance to the color
	 * 
	 * @param personPositions: list of person positions
	 * @param colorPosition: color position
	 * @return integer[]: personPosition,colorPosition
	 */
	public int[] allocateColor() {
		int[] personHasColor = new int[2];
		int bestPosition = personPositions.get(0);
		
		personHasColor[0] = bestPosition;
		personHasColor[1] = colorPosition;

		for (int i = 1; i < personPositions.size(); i++) {

			if (Math.abs(personPositions.get(i) - colorPosition) < 
					Math.abs(personPositions.get(personPositions.indexOf(bestPosition)) - colorPosition)) {
				bestPosition = personPositions.get(i);
				personHasColor[0] = bestPosition;
				personHasColor[1] = colorPosition;
			}
		}
		return personHasColor;
	}
	
	/**
	 * simple distance function: the property is allocated to the person who has the
	 * smallest distance to the property
	 * @param properties to check 
	 * @return array propertyToPerson: property that is closest to a person 
	 */
	public HashMap<Integer,Integer> allocateProperty(List<Integer> properties) {
		HashMap<Integer,Integer> propertyToPerson = new HashMap<Integer,Integer>();
		
		int distance = 9999;
		int bestPerson = 9999;
		for(int i = 0; i < properties.size(); i++){
			int tempProperty = properties.get(i);
			
			for (int j = 0; j < personPositions.size(); j++){
				int tempPerson = personPositions.get(j);
				int tempDistance = Math.abs(tempPerson-tempProperty);
				if(tempDistance < distance){
					distance = tempDistance;
					bestPerson = tempPerson;
				}
			}
			propertyToPerson.put(tempProperty, bestPerson);
			distance = 9999;
		}

		return propertyToPerson;
	}

}