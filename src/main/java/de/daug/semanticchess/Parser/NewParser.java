package de.daug.semanticchess.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.daug.semanticchess.Annotation.PosTagger;
import de.daug.semanticchess.Annotation.Token;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;

@Deprecated
public class NewParser {

	private List<Token> tokens;
	private ChessVocabulary vocabs = new ChessVocabulary();
	private int sequence = 0;
	private int optionSequence = 0;
	private int classSequence = 0;
	private int unionSequence = 0;
	private int index = 0;

	private String propertyLabel = "P_";
	private String entityLabel = "E_";
	private String optionsLabel = "O_";
	private String optionsNameLabel = "N_";
	private String classLabel = "C_";
	private String classPropertiesLabel = "D_";

	private int counter = 0;
	private int optionCounter = 0;
	private int classCounter = 0;
	private int personCounter = 0;

	private List<Pair> properties = new ArrayList<Pair>();
	private List<Pair> entities = new ArrayList<Pair>();
	private List<Pair> options = new ArrayList<Pair>();
	private List<Pair> optionNames = new ArrayList<Pair>();
	private List<Pair> classProperties = new ArrayList<Pair>();
	private List<Pair> classes = new ArrayList<Pair>();

	private List<Flag> flags = new ArrayList<Flag>();

	// custom flags
	private boolean isDecisive = false;
	private String colorFlag = "";

	public NewParser(String query) {
		query = toOrdinal(query);
		this.tokens = initTokens(query);
		getSequences(this.tokens);

		System.out.println("=========================");
		System.out.println("Entities: " + entities.toString());
		System.out.println("Properties: " + properties.toString());
		System.out.println("Options: " + options.toString());
		System.out.println("Optionnames: " + optionNames.toString());
		System.out.println("ClassProperties: " + classProperties.toString());
		System.out.println("Classes: " + classes.toString());
		System.out.println("Flags: " + flags.toString());
		System.out.println("Sequence: " + getSequence());
		System.out.println("=========================");
	}

	public static void main(String[] args) {
		NewParser np = new NewParser("Show me all won games by Magnus Carlsen against an opponent with elo 2700");
	}

	private void getSequences(List<Token> tokens) {

		for (int i = 0; i < tokens.size(); i++) {
			String ner = tokens.get(i).getNe();

			String word = null;
			while (ner.equals("O")) {
				word = tokens.get(i).getWord();
				
				if (this.vocabs.INVERSED_PROPERTIES.get(word.toLowerCase()) != null) {
					ner = this.vocabs.INVERSED_PROPERTIES.get(word.toLowerCase());
					if (ner.equals("1-0") || ner.equals("0-1")) {
						isDecisive = true;
					}
				} else if (word.matches("[a-e][0-9]{2}")) {
					ner = "eco";

				} else {
					if ((i + 1) < tokens.size()) {
						i += 1;
						ner = tokens.get(i).getNe();
						word = tokens.get(i).getWord();
					} else {
						break;
					}
				}
			}

			
			switch (ner) {
			case "PERSON":
				counter += 1;

				if (!isDecisive) {
					putPair(i, "PERSON", "prop:white|prop:black");
				} else {
					if (personCounter == 0) {
						putPair(i, "PERSON", "prop:white");
						personCounter += 1;
					} else {
						putPair(i, "PERSON", "prop:black");
						personCounter += 1;
					}
				}

				i = index;
				break;
			case "MISC":
				counter += 1;

				putPair(i, "MISC", "");
				i = index;
				break;
			case "LOCATION":
				counter += 1;

				putPair(i, "LOCATION", "prop:site");
				i = index;
				break;
			case "ORGANIZATION":
				counter += 1;

				putPair(i, "ORGANIZATION", "prop:event");
				i = index;
				break;
			case "DATE":
				counter += 1;

				boolean isElo = false;

				for (int j = 2; j >= -2; j--) {
					if((i-j) < tokens.size()){
						if (tokens.get(i - j).getNe().equals("elo")) {
							isElo = true;
							break;
							
						}
					}

				}

				int dateNumber = 0;
				try {
					dateNumber = Integer.parseInt(tokens.get(i).getWord());
				} catch (Exception e) {
				}

				if (isElo || dateNumber > 2050) {
					putNumber(i, ner, tokens.get(i).getWord(), "prop:whiteelo|prop:blackelo");
				} else {
					putPair(i, "DATE", "prop:date");
					i = index;
				}

				break;
			case "eco":
				counter += 1;
				putPair(i, "eco", "prop:eco");
				i = index;
				break;
			case "ORDINAL":
				if (tokens.get(i + 1).getNe().equals("round")) {
					counter += 1;

					putNumber(i, ner, tokens.get(i).getWord().replace("\\D+", ""), "prop:round");

				} else {
					optionCounter += 1;
					putOptions(i, "ORDINAL", "OFFSET");
					i = index;
				}

				break;
			case "NUMBER":

				break;
			case "black":
				flags.add(new Flag(i, ner, counter));
				colorFlag = "prop:black";
				break;
			case "white":
				flags.add(new Flag(i, ner, counter));
				colorFlag = "prop:white";
				break;
			case "1-0":
				counter += 1;
				
				putResult(i, "1-0", "prop:result");
				i = index;
				break;
			case "0-1":
				counter += 1;
				
				putResult(i, "0-1", "prop:result");
				i = index;
				break;
			case "1/2-1/2":
				counter += 1;
				putResult(i, "1/2-1/2", "prop:result");
				i = index;
				break;
			case "event":
				classCounter += 1;
				putClass(i, "event", "prop:event");
				break;
			case "opening":
				classCounter += 1;
				putClass(i, "eco", "prop:eco");
			default:

				break;
			}

		}

		if (!colorFlag.equals("")) {
			String reverseColorFlag = "";
			if (colorFlag.equals("prop:white")) {
				reverseColorFlag = "prop:black";
			} else {
				reverseColorFlag = "prop:white";
			}

			boolean resultFlipper = false;
			List<Integer> personPositions = new ArrayList<Integer>();
			int colorPosition = 0;
			for (Flag flag : flags) {
				if (flag.getNerLabel().equals("PERSON")) {
					personPositions.add(flag.getPosition());
				}
				if (flag.getNerLabel().equals("black") || flag.getNerLabel().equals("white")) {
					colorPosition = flag.getPosition();
				}
			}

			int startPosition = personPositions.get(0);
			int temp = Math.abs(personPositions.get(0) - colorPosition);
			int bestPosition = personPositions.get(0);

			for (int pos : personPositions) {

				if (Math.abs(pos - colorPosition) < temp) {
					temp = Math.abs(pos - colorPosition);
					bestPosition = pos;
				}

			}

			int i = 0;
			for (Flag flag : flags) {
				if (flag.getPosition() == bestPosition) {
					i = flag.getCounter() - 1;
					break;
				}
			}

			if (startPosition == bestPosition && colorFlag.equals("prop:black")) {
				resultFlipper = true;
			}
			if (startPosition != bestPosition && colorFlag.equals("prop:white")) {
				resultFlipper = true;
			}

			properties.get(i).setValue(properties.get(i).getValue().replace("prop:white|prop:black", colorFlag));

			for (Pair property : properties) {

				if (property.getValue().equals("prop:white|prop:black")) {
					property.setValue(reverseColorFlag);
				}
			}

			for (Pair entity : entities) {
				if (resultFlipper) {
					if (entity.getValue().equals("1-0")) {
						entity.setValue("0-1");
					} else if (entity.getValue().equals("0-1")) {
						entity.setValue("1-0");
					}
				}
			}

		} else {
			
			unionSequence += 1;
			List<Pair> tempEntities = new ArrayList<Pair>();
			for (Pair entity : entities) {

				if (entity.getValue().equals("1-0")) {

					tempEntities.add(new Pair(entity.getLabel().replace("E", "EU"), "0-1"));
				} else if (entity.getValue().equals("0-1")) {
					Pair pair = new Pair(entity.getLabel().replace("E", "EU"), "1-0");
					tempEntities.add(pair);
				} else {
					Pair pair = new Pair(entity.getLabel().replace("E", "EU"), entity.getValue());
					tempEntities.add(pair);
				}
			}

			List<Pair> tempProperties = new ArrayList<Pair>();
			for (Pair property : properties) {
				if (property.getValue().equals("prop:white")) {
					Pair pair = new Pair(property.getLabel().replace("P", "PU"), "prop:black");
					tempProperties.add(pair);
				} else if (property.getValue().equals("prop:black")) {
					Pair pair = new Pair(property.getLabel().replace("P", "PU"), "prop:white");
					tempProperties.add(pair);
				} else {
					Pair pair = new Pair(property.getLabel().replaceAll("P", "PU"), property.getValue());
					tempProperties.add(pair);
				}
			}

			for (Pair temp : tempEntities) {
				entities.add(temp);
			}
			for (Pair temp : tempProperties) {
				properties.add(temp);
			}
		}

	}

	private String toOrdinal(String query) {
		if (query.matches(".*[0-9]+\\..*\\.{0,1}.*")) {
			int pos = query.indexOf(".");
			String subStr = query.substring(pos - 2, pos).trim();

			if (subStr.endsWith("1") && !subStr.equals("11")) {
				query = query.substring(0, pos) + "st" + query.substring(pos + 1);
			} else if (subStr.endsWith("2") && !subStr.equals("12")) {
				query = query.substring(0, pos) + "nd" + query.substring(pos + 1);
			} else if (subStr.endsWith("3") && !subStr.equals("13")) {
				query = query.substring(0, pos) + "rd" + query.substring(pos + 1);
			} else {
				query = query.substring(0, pos) + "th" + query.substring(pos + 1);
			}

		}

		query = query.replace("first", "1st");
		query = query.replace("second", "2nd");
		query = query.replace("third", "3rd");
		query = query.replace("fourth", "4th");
		query = query.replace("fifth", "5th");
		query = query.replace("sixth", "6th");
		query = query.replace("seventh", "7th");
		query = query.replace("eighth", "8th");
		query = query.replace("ninth", "9th");

		return query;
	}

	public void putPair(int i, String ner, String propValue) {
		flags.add(new Flag(i, ner, counter));

		String entLabel = entityLabel + counter;

		String entValue = tokens.get(i).getWord();
		while ((i + 1) < tokens.size() && tokens.get(i + 1).getNe().equals(ner)) {
			entValue += " " + tokens.get(i + 1).getWord();
			i += 1;
		}
		Pair pair = new Pair(entLabel, entValue);
		entities.add(pair);

		String[] propertyCheck = entValue.split(" ");

		String propLabel = propertyLabel + counter;

		if (ner.equals("MISC")) {
			for (String checks : propertyCheck) {
				if (this.vocabs.INVERSED_PROPERTIES.get(checks.toLowerCase()) != null) {
					propValue = "prop:" + this.vocabs.INVERSED_PROPERTIES.get(checks.toLowerCase());
				}
			}
		}

		pair = new Pair(propLabel, propValue);
		properties.add(pair);

		this.sequence += 1;

		index = i;
	}

	public void putResult(int i, String ner, String propValue) {
		flags.add(new Flag(i, ner, counter));

		String entLabel = entityLabel + counter;

		String entValue = ner;

		Pair pair = new Pair(entLabel, entValue);
		entities.add(pair);

		String propLabel = propertyLabel + counter;

		pair = new Pair(propLabel, propValue);
		properties.add(pair);

		this.sequence += 1;

		index = i;

	}

	public void putClass(int i, String ner, String classProperty) {
		flags.add(new Flag(i, ner, counter));

		String cLabel = classLabel + classCounter;

		String cValue = "?" + ner;

		Pair pair = new Pair(cLabel, cValue);
		classes.add(pair);

		String cPropLabel = classPropertiesLabel + classCounter;
		pair = new Pair(cPropLabel, classProperty);
		classProperties.add(pair);

		this.classSequence += 1;
	}

	public void putNumber(int i, String ner, String number, String numberProperty) {
		flags.add(new Flag(i, ner, counter));

		String entLabel = entityLabel + counter;
		String entValue = number;

		Pair pair = new Pair(entLabel, entValue);
		entities.add(pair);
		
		String propLabel = propertyLabel + counter;
		
		pair = new Pair(propLabel, numberProperty);
		properties.add(pair);
	}

	public void putOptions(int i, String ner, String optionLabelName) {
		try {
			flags.add(new Flag(i, ner, 0));
			String optLabel = optionsLabel + optionCounter;
			String optValue = tokens.get(i).getWord();
			optValue = optValue.substring(0, optValue.length() - 2);

			Pair pair = new Pair(optLabel, optValue);
			options.add(pair);

			String optNameLabel = optionsNameLabel + optionCounter;
			String optNameValue = optionLabelName;

			pair = new Pair(optNameLabel, optNameValue);
			optionNames.add(pair);

			this.optionSequence += 1;

			index = i;

		} catch (Exception e) {
			index = i;
			optionCounter -= 1;
		}

	}

	/**
	 * initialize the POS tagger
	 * 
	 * @param query:
	 *            user question
	 * @return a list of tokens (word, ner, pos)
	 */
	private List<Token> initTokens(String query) {
		PosTagger tagger = new PosTagger();
		StanfordCoreNLP pipeline = tagger.getPipeline();
		tagger.setQuery(query);
		tagger.setDocument(tagger.setAnnotator(pipeline, tagger.getQuery()));
		tagger.initAnnotations();

		List<Token> tokens = tagger.getTokens();

		// basic morphology, for example changes "defeated" to "defeat"
		for (Token token : tokens) {
			WordTag lemma = Morphology.stemStatic(token.getWord(), token.getPos());
			token.setWord(lemma.value());
		}

		return tokens;
	}

	@Override
	public String toString() {
		return "NewParser [tokens=" + tokens + ", vocabs=" + vocabs + ", sequence=" + sequence + ", propertyLabel="
				+ propertyLabel + ", entityLabel=" + entityLabel + ", counter=" + counter + ", optionCounter="
				+ optionCounter + ", properties=" + properties + ", entities=" + entities + ", options=" + options
				+ ", optionNames=" + optionNames + ", flags=" + flags + "]";
	}

	List<Token> getTokens() {
		return tokens;
	}

	void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	ChessVocabulary getVocabs() {
		return vocabs;
	}

	void setVocabs(ChessVocabulary vocabs) {
		this.vocabs = vocabs;
	}

	int getSequence() {
		return sequence;
	}

	void setSequence(int sequence) {
		this.sequence = sequence;
	}

	String getPropertyLabel() {
		return propertyLabel;
	}

	void setPropertyLabel(String propertyLabel) {
		this.propertyLabel = propertyLabel;
	}

	String getEntityLabel() {
		return entityLabel;
	}

	void setEntityLabel(String entityLabel) {
		this.entityLabel = entityLabel;
	}

	int getCounter() {
		return counter;
	}

	void setCounter(int counter) {
		this.counter = counter;
	}

	int getOptionCounter() {
		return optionCounter;
	}

	void setOptionCounter(int optionCounter) {
		this.optionCounter = optionCounter;
	}

	List<Pair> getProperties() {
		return properties;
	}

	void setProperties(List<Pair> properties) {
		this.properties = properties;
	}

	List<Pair> getEntities() {
		return entities;
	}

	void setEntities(List<Pair> entities) {
		this.entities = entities;
	}

	List<Pair> getOptions() {
		return options;
	}

	void setOptions(List<Pair> options) {
		this.options = options;
	}

	List<Pair> getOptionNames() {
		return optionNames;
	}

	void setOptionNames(List<Pair> optionNames) {
		this.optionNames = optionNames;
	}

	List<Flag> getFlags() {
		return flags;
	}

	void setFlags(List<Flag> flags) {
		this.flags = flags;
	}

	int getClassSequence() {
		return classSequence;
	}

	void setClassSequence(int classSequence) {
		this.classSequence = classSequence;
	}

	int getUnionSequence() {
		return unionSequence;
	}

	void setUnionSequence(int unionSequence) {
		this.unionSequence = unionSequence;
	}

	List<Pair> getClassProperties() {
		return classProperties;
	}

	void setClassProperties(List<Pair> classProperties) {
		this.classProperties = classProperties;
	}

	List<Pair> getClasses() {
		return classes;
	}

	void setClasses(List<Pair> classes) {
		this.classes = classes;
	}

	class Pair {
		private String label;
		private String value;

		public Pair(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "Pair [label=" + label + ", value=" + value + "]";
		}
	}

	class Flag {
		private int position;
		private String nerLabel;
		private int counter;

		public Flag(int position, String nerLabel, int counter) {
			this.position = position;
			this.nerLabel = nerLabel;
			this.counter = counter;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public String getNerLabel() {
			return nerLabel;
		}

		public void setNerLabel(String nerLabel) {
			this.nerLabel = nerLabel;
		}

		int getCounter() {
			return counter;
		}

		void setCounter(int counter) {
			this.counter = counter;
		}

		@Override
		public String toString() {
			return "Flag [position=" + position + ", nerLabel=" + nerLabel + ", counter=" + counter + "]";
		}

	}

}