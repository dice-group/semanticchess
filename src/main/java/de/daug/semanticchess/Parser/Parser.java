package de.daug.semanticchess.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.springframework.util.StringUtils;

import de.daug.semanticchess.Annotation.PosTagger;
import de.daug.semanticchess.Annotation.Token;
import de.daug.semanticchess.Database.StringSimilarity;
import de.daug.semanticchess.Parser.Helper.Classes;
import de.daug.semanticchess.Parser.Helper.PropertyAllocator;
import de.daug.semanticchess.Parser.Helper.CustomNer;
import de.daug.semanticchess.Parser.Helper.Entity;
import de.daug.semanticchess.Parser.Helper.FenRegex;
import de.daug.semanticchess.Parser.Helper.Filters;
import de.daug.semanticchess.Parser.Helper.Flipper;
import de.daug.semanticchess.Parser.Helper.Options;
import de.daug.semanticchess.Parser.Helper.Resource;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * parses the user query to a sequence to find a suitable sparql query
 */
public class Parser {
	private List<Token> tokens = new ArrayList<Token>();
	private String query;

	private List<Entity> entities = new ArrayList<Entity>();
	private List<Classes> classes = new ArrayList<Classes>();
	private List<Resource> resources = new ArrayList<Resource>();
	private Options options = new Options();
	private FenRegex fenReg = new FenRegex();
	private Filters filters = new Filters();

	private ChessVocabulary vocabulary = new ChessVocabulary();

	int index;

	private boolean isBlack = false;
	private boolean isWhite = false;
	private boolean isDecisive = false;
	private boolean isUnion = false;
	private boolean isElo = false;
	private boolean isFen = false;
	private boolean isFilter = false;

	private String sequence;

	public Parser(String query) {
		this.query = query;

		PosTagger tagger = new PosTagger();
		StanfordCoreNLP pipeline = tagger.getPipeline();
		tagger.setQuery(query);
		tagger.setDocument(tagger.setAnnotator(pipeline, tagger.getQuery()));
		tagger.initAnnotations();

		List<Token> tokens = tagger.getTokens();

		
		
		CustomNer cNer = new CustomNer();
		tokens = cNer.stemming(tokens);
		tokens = cNer.checkChessVocabulary(tokens);
		tokens = cNer.checkElo(tokens);

		this.tokens = tokens;
		
		System.out.println(this.tokens.toString());
		
		collectEntities(tokens);
		if (isBlack || isWhite) {
			try {
				injectColor();
			} catch (Exception err) {

			}

		}

		if (isElo) {
			try {
				injectElo();
			} catch (Exception err) {

			}

			// List erweitern? get element by position?
		}

		resultChecker();
		if (isUnion()) {
			makeUnion();
		}

		if (!isUnion) {
			this.sequence = "_" + classes.size() + "" + entities.size() + "0";
		} else {
			this.sequence = "_" + classes.size() + "" + entities.size() + "1";
		}

		if (isFilter) {
			if (isFen) {
				fenReg.createFen();
				filters.addRegex("?fen", fenReg.getFen(), false);
			}
		}

	}

	public static void main(String[] args) {
		String query = "Give me the games with rook and pawn against rook.";

		Parser p = new Parser(query);

		System.out.println(p.getTokens().toString());
		System.out.println(p.getEntities().toString());
		System.out.println(p.getClasses().toString());
		System.out.println(p.getResources().toString());
	}

	public void collectEntities(List<Token> tokens) {
		boolean isBlackPieces = false;
		for (index = 0; index < tokens.size(); index++) {
			String word = tokens.get(index).getWord();
			String ne = tokens.get(index).getNe();
			String preNe = "O";
			String preWord = "";
			try {
				preNe = tokens.get(index - 1).getNe();
				preWord = tokens.get(index - 1).getWord();
			} catch (Exception e) {
				preNe = "O";
			}
			String nextNe = "O";
			String nextWord = "";
			try {
				nextNe = tokens.get(index + 1).getNe();
				nextWord = tokens.get(index + 1).getWord();
			} catch (Exception e) {
				nextNe = "O";
			}

			String nextFoundNe = "O";
			String nextFoundWord = "";
			int nextFoundIndex = 0;
			try {
				int i = index;
				while (nextFoundNe.equals("O")) {
					nextFoundNe = tokens.get(i + 1).getNe();
					nextFoundWord = tokens.get(i + 1).getWord();
					nextFoundIndex = i + 1;
					i++;
				}
			} catch (Exception err) {
				nextFoundNe = "O";
				nextFoundWord = "";
			}

			String pos = tokens.get(index).getPos();

			switch (ne) {
			case "PERSON":
				addEntityOrClass(word, ne, "white|prop:black", "?game");
				break;
			case "MISC":
				addEntityOrClass(word, ne, "", "?game");
				break;
			case "LOCATION":
				addEntityOrClass(word, ne, "site", "?game");
				break;
			case "ORGANIZATION":
				addEntityOrClass(word, ne, "event", "?game");
				break;
			case "DATE":
				addEntityOrClass(word, ne, "date", "?game");
				break;
			case "ORDINAL":
				if (nextNe.equals("round")) {

					index += 1;
					if (word.equals("last")) {
						addEntityOrClass("round", ne, "round", "?game");
						options.setLimitStr(1);
						options.setOffsetStr(0);
						options.setOrderStr("DESC", "?round");
					} else {
						addEntityOrClass(word.replaceAll("\\D+", ""), ne, "round", "?game");
					}

				} else {
					if (word.equals("last")) {
						options.setLimitStr(1);
						options.setOffsetStr(0);
						options.setOrderStr("DESC", "?date");
					} else {
						options.setLimitStr(1);
						options.setOffsetStr((Integer.valueOf(word.replaceAll("\\D+", ""))) - 1);
						options.setOrderStr("ASC", "?date");
					}

					classes.add(new Classes(classes.size() + 1, "?date", "date", 999, "?game"));

				}

				// TODO count events, etc
				break;
			case "NUMBER":
				if (preNe.equals("round")) {

					index += 1;
					addEntityOrClass(word.replaceAll("\\D+", ""), ne, "round", "?game");
				} else if (nextNe.equals("piece")) {
					// will be used in case piece
					break;
				} else {
					options.setLimitStr(Integer.valueOf(word));
					options.setOffsetStr(0);

				}
				break;
			case "game":
				// TODO als Spezialfall, konkurrierend mit anderen res
				// TODO bei eco, opening, event,... flag für game ressource
				// resources.add(new Resource((resources.size() + 1), "?game",
				// "ChessGame", index));
				break;
			case "eco":
				addEntityOrClass(word.substring(0,1).toUpperCase() + word.substring(1), ne, "eco", "?game");
				break;
			case "elo":
				isElo = true;
				addEntityOrClass(word, ne, "whiteelo|prop:blackelo", "?game");
				break;
			case "black":
				isBlack = true;
				break;
			case "white":
				isWhite = true;
				break;
			case "1-0":
				isDecisive = true;
				addEntityOrClass(ne, ne, "result", "?game");
				break;
			case "0-1":
				isDecisive = true;
				addEntityOrClass(ne, ne, "result", "?game");
				break;
			case "1/2-1/2":
				addEntityOrClass(ne, ne, "result", "?game");
				break;
			case "event":
				addEntityOrClass(word, ne, "event", "?game");
				if(!this.tokens.get(index).getWord().equals("tournament") && !this.tokens.get(index).getWord().equals("event")){
					filters.addRegex("?" + ne, word, false);
				}
				
				break;
			case "opening":
				addEntityOrClass(word, ne, "eco", "?game");
				break;
			case "moves":
				addEntityOrClass(word, ne, "moves", "?game");
			case "move":
				addEntityOrClass(word, ne, "move", "?moves");
			case "piece":
				isFen = true;
				isFilter = true;
				int number = 1;

				if (nextWord.equals("pair")) {
					number = 2;
				}

				if (!isBlackPieces) {
					try {
						number = Integer.valueOf(preWord);
					} catch (Exception e) {

					}

					fenReg.addPieceWhite(number, word);
				} else {
					try {
						number = Integer.valueOf(preWord);
					} catch (Exception e) {

					}
					fenReg.addPieceBlack(number, word);

				}

				if (fenReg.getPiecesWhite().size() == 1 && fenReg.getPiecesBlack().size() == 0) {
					classes.add(new Classes(classes.size() + 1, "?moves", "moves", 999, "?game"));
					classes.add(new Classes(classes.size() + 1, "?fen", "fen", 999, "?moves"));
				}

				break;
			case "option":
				if (word.equals("average")) {
					options.setOrderStr("AVG", "?" + nextFoundNe);
				}

				break;
			case "greater":
				isFilter = true;
				filters.addGreaterThan("?" + nextFoundNe, nextFoundWord);
				index = nextFoundIndex + 1;
			default:
				if (word.equals("versus") || word.equals("vs") || word.equals("against")) {
					if (preNe.equals("piece")) {
						isBlackPieces = true;
					}
				}

				break;

			}

		}
	}

	public void injectColor() {
		PropertyAllocator ca = new PropertyAllocator(tokens);
		int[] personHasColor = ca.allocateColor();

		if (isWhite) {
			for (Entity e : entities) {
				if (e.getEndPosition() == personHasColor[0]) {
					e.setPropertyName("prop:white");
				} else if (e.getEndPosition() != personHasColor[0]
						&& e.getPropertyName().equals("prop:white|prop:black")) {
					e.setPropertyName("prop:black");
				}
			}
			for (Classes c : classes) {
				if (c.getPosition() == personHasColor[0]) {
					c.setPropertyName("prop:white");
				} else if (c.getPosition() != personHasColor[0]
						&& c.getPropertyName().equals("prop:white|prop:black")) {
					c.setPropertyName("prop:black");
				}
			}

		} else if (isBlack) {
			for (Entity e : entities) {
				if (e.getEndPosition() == personHasColor[0]) {
					e.setPropertyName("prop:black");
				} else if (e.getEndPosition() != personHasColor[0]
						&& e.getPropertyName().equals("prop:white|prop:black")) {
					e.setPropertyName("prop:white");
				}
			}
			for (Classes c : classes) {
				if (c.getPosition() == personHasColor[0]) {
					c.setPropertyName("prop:black");
				} else if (c.getPosition() != personHasColor[0]
						&& c.getPropertyName().equals("prop:white|prop:black")) {
					c.setPropertyName("prop:white");
				}
			}
		}
	}

	public void injectElo() {
		PropertyAllocator pa = new PropertyAllocator(tokens);
		HashMap<Integer, Integer> propertyToPerson = pa.allocateProperty(pa.getEloPositions());
		int tempPerson = 9999;

		for (Entry<Integer, Integer> pair : propertyToPerson.entrySet()) {
			if (pair.getValue() < tempPerson) {
				tempPerson = pair.getValue();
			}

			try {
				Entity e = getEntityByEndPosition(pair.getValue());

				if (e.getPropertyName().equals("prop:white")) {
					try {

						getEntityByEndPosition(pair.getKey()).setPropertyName("prop:whiteelo");
					} catch (NullPointerException err) {
						getClassByPosition(pair.getKey()).setPropertyName("prop:whiteelo");
					}
				} else if (e.getPropertyName().equals("prop:black")) {
					try {

						getEntityByEndPosition(pair.getKey()).setPropertyName("prop:blackelo");
					} catch (NullPointerException err) {
						getClassByPosition(pair.getKey()).setPropertyName("prop:blackelo");
					}

				} else {
					try {

						getEntityByEndPosition(pair.getKey()).setPropertyName("prop:whiteelo");
						System.out.println("3. " + getEntityByEndPosition(pair.getValue()).getPropertyName());
						try {
							getEntityByEndPosition(pair.getValue()).setPropertyName("prop:white");
						} catch (Exception err) {
							getClassByPosition(pair.getValue()).setPropertyName("prop:white");
						}

						isWhite = true;

					} catch (NullPointerException err) {
						System.out.println(pair.getKey() + "-> " + pair.getValue());
						getClassByPosition(pair.getKey()).setPropertyName("prop:whiteelo");
						try {
							getEntityByEndPosition(pair.getValue()).setPropertyName("prop:white");
						} catch (Exception error) {
							getClassByPosition(pair.getValue()).setPropertyName("prop:white");
						}

						isWhite = true;
					}

					isUnion = true;

				}
			} catch (NullPointerException err) {

			}
			try {
				Classes c = getClassByPosition(pair.getValue());

				if (c.getPropertyName().equals("prop:white")) {
					try {

						getEntityByEndPosition(pair.getKey()).setPropertyName("prop:whiteelo");
					} catch (NullPointerException err) {
						getClassByPosition(pair.getKey()).setPropertyName("prop:whiteelo");
					}
				} else if (c.getPropertyName().equals("prop:black")) {
					try {

						getEntityByEndPosition(pair.getKey()).setPropertyName("prop:blackelo");
					} catch (NullPointerException err) {
						getClassByPosition(pair.getKey()).setPropertyName("prop:blackelo");
					}
				} else {

					try {

						getEntityByEndPosition(pair.getKey()).setPropertyName("prop:whiteelo");
						try {
							getEntityByEndPosition(pair.getValue()).setPropertyName("prop:white");
						} catch (Exception err) {
							getClassByPosition(pair.getValue()).setPropertyName("prop:white");

						}

						isBlack = true;

					} catch (NullPointerException err) {
						getClassByPosition(pair.getKey()).setPropertyName("prop:whiteelo");
						try {
							getClassByPosition(pair.getValue()).setPropertyName("prop:white");
						} catch (Exception error) {
							getEntityByEndPosition(pair.getValue()).setPropertyName("prop:white");
						}

						isWhite = true;
					}
					isUnion = true;
				}
			} catch (NullPointerException err) {

			}

		}
		injectColor();
	}

	// public void injectElo(){

	/*
	 * 1. entity/class finden mit prop:black prop:white 2. nehme position und
	 * gehe zu HashMap 3. aus Hashmap ermittle position von elo 4. ändere elo
	 * property ab
	 * 
	 */

	public void resultChecker() {
		Stack<String> colors = new Stack<String>();
		String result = "";
		boolean isFlipped = false;

		if (isDecisive && (isBlack || isWhite)) {
			for (Entity e : entities) {
				if (e.getPropertyName().equals("prop:white") || e.getPropertyName().equals("prop:black")) {
					colors.push(e.getPropertyName());
				}
				if (e.getPropertyName().equals("prop:result")) {
					result = e.getEntityName();
				}
			}
			for (Classes c : classes) {
				if (c.getPropertyName().equals("prop:white") || c.getPropertyName().equals("prop:black")) {
					colors.push(c.getPropertyName());
				}
			}

			if (colors.size() > 1) {
				if (colors.peek().equals("prop:white")) {
					if (result.equals("'0-1'")) {
						result = "'1-0'";
						isFlipped = true;
					} else if (result.equals("'1-0'")) {
						result = "'0-1'";
						isFlipped = true;
					}
				}
			} else if (colors.size() == 1) {
				if (colors.peek().equals("prop:black")) {
					if (result.equals("'0-1'")) {
						result = "'1-0'";
						isFlipped = true;
					} else if (result.equals("'1-0'")) {
						result = "'0-1'";
						isFlipped = true;
					}
				}
			}

			if (isFlipped) {
				for (Entity e : entities) {
					if (e.getPropertyName().equals("prop:result")) {
						e.setEntityName(result);
					}
				}
			}
		} else if (isDecisive && (!isBlack || !isWhite)) {
			for (Entity e : entities) {
				if (e.getPropertyName().equals("prop:result")) {
					result = e.getEntityName();
				}
			}
			if (result.equals("'1-0'") || result.equals("'0-1'")) {
				isUnion = true;
				boolean isFirst = true;
				for (Entity e : entities) {
					if (e.getPropertyName().equals("prop:white|prop:black") && isFirst) {
						e.setPropertyName("prop:white");
						isFirst = false;
					} else if (e.getPropertyName().equals("prop:white|prop:black") && !isFirst) {
						e.setPropertyName("prop:black");
					}
				}
			}
		}
	}

	public void addEntityOrClass(String word, String ne, String property, String resource) {
		int startPosition = index;

		if (Character.isUpperCase(ne.charAt(0))) {
			while ((index + 1) < tokens.size() && tokens.get(index + 1).getNe().equals(ne)) {
				word += " " + tokens.get(index + 1).getWord();
				index += 1;
			}
		}

		if (ne.equals("MISC")) {
			String[] words = word.split(" ");

			for (String w : words) {
				if (vocabulary.INVERSED_PROPERTIES.get(w.toLowerCase()) != null) {
					property = vocabulary.INVERSED_PROPERTIES.get(w.toLowerCase());
				}
			}
		}

		int endPosition = index;

		String entity = vocabulary.INVERSED_PROPERTIES.get(word);

		if (entity != null && entity != "1-0" && entity != "0-1" && entity != "1/2-1/2") {
			classes.add(new Classes(classes.size() + 1, "?" + ne, property, endPosition, resource));
		} else {
//			System.out.println(property + " " + word);
//			try {
//				StringSimilarity sim = new StringSimilarity(property,word);
//				word = sim.subStringMatch();			
//			} catch(Exception e){
//				
//			}
			
			
			entities.add(
					new Entity(entities.size() + 1, "'" + word + "'", property, startPosition, endPosition, resource));
		}
	}

	public void makeUnion() {
		Flipper flipper = new Flipper();
		String newPropertyName = "";
		String newEntityName = "";

		List<Entity> tempEntities = new ArrayList<Entity>();

		for (Entity e : entities) {

			newPropertyName = flipper.toFlip(e.getPropertyName());
			newEntityName = flipper.toFlip(e.getEntityName());

			tempEntities.add(new Entity(tempEntities.size() + 1, newEntityName, newPropertyName.replace("prop:", ""),
					e.getStartPosition(), e.getEndPosition(), e.getResourceName()));
		}

		for (Entity t : tempEntities) {
			entities.add(new Entity(entities.size() + 1, t.getEntityName(), t.getPropertyName().replace("prop:", ""),
					t.getStartPosition(), t.getEndPosition(), t.getResourceName()));
		}

		// classes
		List<Classes> tempClasses = new ArrayList<Classes>();

		for (Classes c : classes) {

			newPropertyName = flipper.toFlip(c.getPropertyName());
			newEntityName = flipper.toFlip(c.getClassesName());

			tempClasses.add(new Classes(tempClasses.size() + 1, newEntityName, newPropertyName.replace("prop:", ""),
					c.getPosition(), c.getResourceName()));
		}

		for (Classes t : tempClasses) {
			classes.add(new Classes(classes.size() + 1, t.getClassesName(), t.getPropertyName().replace("prop:", ""),
					t.getPosition(), t.getResourceName()));
		}
	}

	List<Token> getTokens() {
		return tokens;
	}

	void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	String getQuery() {
		return query;
	}

	void setQuery(String query) {
		this.query = query;
	}

	List<Entity> getEntities() {
		return entities;
	}

	void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	List<Classes> getClasses() {
		return classes;
	}

	void setClasses(List<Classes> classes) {
		this.classes = classes;
	}

	List<Resource> getResources() {
		return resources;
	}

	void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	boolean isUnion() {
		return isUnion;
	}

	void setUnion(boolean isUnion) {
		this.isUnion = isUnion;
	}

	String getSequence() {
		return sequence;
	}

	void setSequence(String sequence) {
		this.sequence = sequence;
	}

	Options getOptions() {
		return options;
	}

	public Filters getFilters() {
		return filters;
	}

	public void setFilters(Filters filters) {
		this.filters = filters;
	}

	public Entity getEntityByEndPosition(int position) {
		for (Entity e : entities) {
			if (e.getEndPosition() == position) {
				return e;
			}
		}
		return null;
	}

	public Entity getEntityByStartPosition(int position) {
		for (Entity e : entities) {
			if (e.getStartPosition() == position) {
				return e;
			}
		}
		return null;
	}

	public Classes getClassByPosition(int position) {
		for (Classes c : classes) {
			if (c.getPosition() == position) {
				return c;
			}
		}
		return null;
	}

}