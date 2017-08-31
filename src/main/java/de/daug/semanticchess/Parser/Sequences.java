package de.daug.semanticchess.Parser;

public class Sequences {

	// What is the capital of Germany? => (Annotation) Property_1 (P_1) Entity_1
	// (E_1) => (SPARQL template file) SELECT ?s {?s P_1 E_1.}
	
	
	//1. Example: Show me games by Magnus Carlsen.
	static String _1 = "SELECT DISTINCT ?game " + "WHERE { " + " ?game P_1 'E_1'. " + "}";

	//2a. Example: Show me games played at world championships.
	static String _2 = "SELECT DISTINCT ?game " + "WHERE { " + " ?game P_1 'E_1'. " + " ?game P_2 'E_2'. " + "}";
	
	//3.
	static String _3 = "SELECT DISTINCT ?game " + "WHERE { " + " ?game P_1 'E_1'. " + " ?game P_2 'E_2'. " + " ?game P_3 'E_3'. " + "}";

	
	// Show me all won games of Carlsen against Anand
	// private String gamePersonResultPerson = "SELECT DISTINCT ?game " +
	// "WHERE {{ " +
	// " ?game P_1 E_1 " +
	// " ?game P_2 E_2 " +
	// " ?game P_3 C_1 } " +
	// "UNION {" +
	// " ?game P_2 E_1 " +
	// " ?game P_1 E_2 " +
	// " ?game P_3 C_2 } " +
	// " }";
	//
	//
	//
	
	
}