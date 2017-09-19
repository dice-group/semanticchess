package de.daug.semanticchess.Parser.Helper;

import java.util.ArrayList;
import java.util.List;

import de.daug.semanticchess.Annotation.Token;

@Deprecated
public class ResultChecker {

	String newProperty;
	String reverseProperty;
	
	List<Integer> personPositions = new ArrayList<Integer>();
	int colorPosition;
	int[] personHasColor = new int[2];

	public ResultChecker(List<Token> tokens) {
		PropertyAllocator ca = new PropertyAllocator(tokens);
		this.personPositions = ca.getPersonPositions();
		this.colorPosition = ca.getFirstColorPosition();
		this.personHasColor = ca.allocateColor();
	}
	
	

}