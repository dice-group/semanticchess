package de.daug.semanticchess.Parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.daug.semanticchess.Annotation.PosTagger;
import de.daug.semanticchess.Annotation.PosTagger.Token;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;

/**
 * parses the user query to a sequence to find a suitable sparql query
 */
public class Parser {
	
	private List<Token> tokens;
	private HashMap<Integer, String[]> sequence = new HashMap<Integer, String[]>();
	private String sequenceCode = "";
	private ChessVocabulary vocabs;

	//counter to track the found entities and properties
	private int allCounter = 0;
	private int entityCounter = 0;
	private int propertyCounter = 0;
	private int miscCounter = 0;
	private int locationCounter = 0;
	private int dateCounter = 0;
	private int numberCounter = 0;
	private int ordinalCounter = 0;
	private int organizationCounter = 0;
	private int resultCounter = 0;
	private int colorCounter = 0;

	//labels for entities and properties
	private String entity = "E_";
	private String misc = "M_";
	private String property = "P_";
	private String location = "L_";
	private String date = "D_";
	private String number = "N_";
	private String ordinal = "O_";
	private String organization = "Z_";
	private String result = "R_";
	private String color = "C_";

	//labels by the Stanford NER
	private String nerEntity = "PERSON";
	private String nerMisc = "MISC";
	private String nerLocation = "LOCATION";
	private String nerDate = "DATE";
	private String nerNumber = "NUMBER";
	private String nerOrdinal = "ORDINAL";
	private String nerOrganization = "ORGANIZATION";

	/**
	 * constructor: takes a query and returns a sequence code 
	 * @param query: user question
	 */
	public Parser(String query) {
		this.tokens = initTokens(query);
		ChessVocabulary cv = new ChessVocabulary();
		this.vocabs = cv;
		getStanfordEntities(getTokens());

		setSequenceCode(getSequence());
		System.out.println(getSequenceCode());
	}
	
	/**
	 * main to test the parsing process
	 * @param args
	 */
	public static void main(String[] args) {

		Parser p = new Parser(
				"Show me games where Magnus Carlsen won.");
		
		//Iterator to loop through the HashMap and print out the sequence codes
		Iterator<Entry<Integer, String[]>> it = p.getSequence().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, String[]> pair = (Map.Entry<Integer, String[]>) it.next();
			int key = (int) pair.getKey();
			String[] seq = (String[]) pair.getValue();
			System.out.println(key + " = " + seq[0] + " " + seq[1]);
			it.remove();
		}
	}
	
	/**
	 * initialize the POS tagger
	 * @param query: user question
	 * @return a list of tokens (word, ner, pos)
	 */
	private List<Token> initTokens(String query) {
		PosTagger tagger = new PosTagger();
		StanfordCoreNLP pipeline = tagger.getPipeline();
		tagger.setQuery(query);
		tagger.setDocument(tagger.setAnnotator(pipeline, tagger.getQuery()));
		tagger.initAnnotations();

		List<Token> tokens = tagger.getTokens();

		//basic morphology, for example changes "defeated" to "defeat"
		for (Token token : tokens) {
			WordTag lemma = Morphology.stemStatic(token.getWord(), token.getPos());
			token.setWord(lemma.value());
		}

		return tokens;
	}

	/**
	 * loop through the list of tokens
	 * check the ner
	 * check the custom chess vocabulary
	 * and fill the Hashmap for sequences
	 * @param tokens
	 */
	private void getStanfordEntities(List<Token> tokens) {
		// TODO: implement FOX-java

		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).getNe().equals(nerEntity)) {
				entityCounter += 1;
				i = checkTypes(i, entityCounter, nerEntity, entity);
			} else if (tokens.get(i).getNe().equals(nerMisc)) {
				miscCounter += 1;
				i = checkTypes(i, miscCounter, nerMisc, misc);
			} else if (tokens.get(i).getNe().equals(nerLocation)) {
				locationCounter += 1;
				i = checkTypes(i, locationCounter, nerLocation, location);
			} else if (tokens.get(i).getNe().equals(nerDate)) {
				dateCounter += 1;
				i = checkTypes(i, dateCounter, nerDate, date);
			} else if (tokens.get(i).getNe().equals(nerNumber)) {
				numberCounter += 1;
				i = checkTypes(i, numberCounter, nerNumber, number);
			} else if (tokens.get(i).getNe().equals(nerOrdinal)) {
				ordinalCounter += 1;
				i = checkTypes(i, ordinalCounter, nerOrdinal, ordinal);
			} else if (tokens.get(i).getNe().equals(nerOrganization)) {
				organizationCounter += 1;
				i = checkTypes(i, organizationCounter, nerOrganization, organization);
			} else {
				checkProperty(tokens.get(i).getWord());				
			}
		}
		//switch the standard result if black wins or loses
		switchResult();
	}
	
	/**
	 * switch the standard result ("1-0" for winning and "0-1" for loosing) if black wins or loses
	 */
	private void switchResult() {
		Iterator<Entry<Integer, String[]>> iter = getSequence().entrySet().iterator();
		//conditions that have to be true for switching the result
		boolean colorFlag = false;
		boolean resultFlag = false;
		int keyNr = 0;
		
		//loop through the HashMap of sequences
		while (iter.hasNext()) {
			Map.Entry<Integer, String[]> pair = (Map.Entry<Integer, String[]>) iter.next();
			String[] seq = (String[]) pair.getValue();
			
			if(seq[1].equals("black")) {
				colorFlag = true;
			}
			if(seq[0].contains(result)) {
				resultFlag = true;
				keyNr = pair.getKey();
			}
			
			if (colorFlag && resultFlag) {
				seq = getSequence().get(keyNr);
				String[] result = seq[1].split("-");
				String newResult = result[1]+"-"+result[0];
				seq[1] = newResult;
				getSequence().put(keyNr, seq);
				break;
			}
		}
	}
	
	/**
	 * check if the NER is followed by the same NER
	 * iff: combine the words and put the sequence together
	 * @param i: current index
	 * @param counter: current entity or porperty counter
	 * @param nerLabel: provided by Stanford NET
	 * @param codeLabel: custom label to distinguish the entity or property
	 * @return
	 */
	private int checkTypes(int i, int counter, String nerLabel, String codeLabel) {

		String type = tokens.get(i).getWord();
		String code = codeLabel + counter;

		while ((i + 1) < tokens.size() && tokens.get(i + 1).getNe().equals(nerLabel)) {
			type += " " + tokens.get(i + 1).getWord();
			i += 1;
		}
		checkEntity(type);
		allCounter += 1;
		putSequence(allCounter, code, type);

		return i;
	}
	
	/**
	 * check properties for exceptions
	 * @param prop
	 */
	private void checkProperty(String prop) {
		try {
			if (this.vocabs.INVERSED_PROPERTIES.get(prop.toLowerCase()) != null) {

				allCounter += 1;
				String propertyCode = "";
				String resultCode = "";
				String colorCode = "";
				switch (this.vocabs.INVERSED_PROPERTIES.get(prop.toLowerCase())) {
				case "white":
				case "black":
					colorCounter += 1;
					colorCode = color + colorCounter;
					putSequence(allCounter, colorCode, this.vocabs.INVERSED_PROPERTIES.get(prop.toLowerCase()));
					break;

				case "1-0":
				case "0-1":
				case "1/2-1/2":
					propertyCounter += 1;
					propertyCode = property + propertyCounter;
					putSequence(allCounter, propertyCode, "result");
					resultCounter += 1;
					resultCode = result + resultCounter;
					allCounter += 1;
					putSequence(allCounter, resultCode, this.vocabs.INVERSED_PROPERTIES.get(prop.toLowerCase()));
					break;
				default:
					putSequence(allCounter, propertyCode, this.vocabs.INVERSED_PROPERTIES.get(prop.toLowerCase()));
					break;

				}
			}
		} catch (NullPointerException err) {
		}
	}

	/**
	 * check if entity is known in the chess vocabulary.
	 * iff: assign the entity a property
	 * @param entity
	 */
	private void checkEntity(String entity) {
		String[] entityChecker = entity.split(" ", -1);
		for (String e : entityChecker) {
			try {
				if (this.vocabs.INVERSED_PROPERTIES.get(e.toLowerCase()) != null) {
					propertyCounter += 1;
					String propertyCode = property + propertyCounter;
					allCounter += 1;
					putSequence(allCounter, propertyCode, this.vocabs.INVERSED_PROPERTIES.get(e.toLowerCase()));
				}
			} catch (NullPointerException err) {

			}
		}
	}
	
	/**
	 * put the sequence to the HashMap
	 * @param index
	 * @param code
	 * @param word
	 */
	private void putSequence(int index, String code, String word) {
		String[] seq = new String[] { code, word };
		this.sequence.put(index, seq);
	}
	
	/**
	 * set the sequence code 
	 * with this code a sparql query will be chosen
	 * @param sequence
	 */
	private void setSequenceCode(HashMap<Integer, String[]> sequence) {
		Iterator<Entry<Integer, String[]>> iter = getSequence().entrySet().iterator();
		String code = "";
		while (iter.hasNext()) {
			Map.Entry<Integer, String[]> pair = (Map.Entry<Integer, String[]>) iter.next();
			String[] seq = (String[]) pair.getValue();
			code += seq[0].substring(0, 1);
		}
		
		//https://stackoverflow.com/a/605901
		char chars[] = code.toCharArray();
		Arrays.sort(chars);
		code = new String(chars);

		this.sequenceCode = code;
	}
	
	/**
	 * @return sequence code
	 */
	public String getSequenceCode() {
		return this.sequenceCode;
	}

	/**
	 * set the list of tokens
	 * @param tokens
	 */
	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * @return list of tokens
	 */
	public List<Token> getTokens() {
		return this.tokens;
	}

	/**
	 * @return HashMap of sequences
	 */
	public HashMap<Integer, String[]> getSequence() {
		return sequence;
	}
	
	/**
	 * set the HashMap of sequences
	 * @param sequence
	 */
	public void setSequence(HashMap<Integer, String[]> sequence) {
		this.sequence = sequence;
	}

}