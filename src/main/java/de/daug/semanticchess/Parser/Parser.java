package de.daug.semanticchess.Parser;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import java.util.Map.Entry;

import java.util.Stack;

import de.daug.semanticchess.Annotation.PosTagger;
import de.daug.semanticchess.Annotation.TimeTagger;
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

import de.daug.semanticchess.Parser.Helper.TopicFinder;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * parses the user query to a sequence to find a suitable sparql query
 */
public class Parser {
	private List<Token> tokens = new ArrayList<Token>();
	private String query;

	private List<Entity> entities = new ArrayList<Entity>();
	private List<Classes> classes = new ArrayList<Classes>();
	private Options options = new Options();
	private FenRegex fenReg = new FenRegex();
	private Filters filters = new Filters();
	private TopicFinder topics = new TopicFinder();
	private StringSimilarity similar = new StringSimilarity();

	private ChessVocabulary vocabulary = new ChessVocabulary();

	private int index;
	private int moveCounter = 0;

	private boolean isBlack = false;
	private boolean isWhite = false;
	private boolean isDecisive = false;
	private boolean isUnion = false;
	private boolean isElo = false;
	private boolean isFen = false;
	private boolean isCount = false;
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
		tokens = cNer.checkOpening(tokens);

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

		topics.collectTopics(this.entities, this.classes);
		if (isCount) {

			int firstEntityPosition = 999;
			int firstClassesPosition = 999;
			try {
				firstEntityPosition = entities.get(0).getStartPosition();
			} catch (Exception err) {

			}
			try {
				firstClassesPosition = classes.get(0).getPosition();
			} catch (Exception err) {

			}

			if (firstEntityPosition < firstClassesPosition) {
				topics.addCount(entities.get(0).getResourceName());
				this.options.setOrderStr("DESC", "?nr");

			} else {
				topics.addCount(classes.get(0).getClassesName());
				this.options.setOrderStr("DESC", "?nr");
				this.options.setGroupStr(classes.get(0).getClassesName());
			}
		}

		if (topics.isAlgebra()) {
			for (String topic : topics.getTopics()) {

				if (this.options.getGroupStr().indexOf(topic) == -1 && topic.indexOf("(") == -1) {

					this.options.setGroupStr(topic);
				}
			}
		}

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
			String tempNumber = "";
			try {
				int i = index;
				while (nextFoundNe.equals("O") || nextFoundNe.equals("NUMBER")) {
					if (nextFoundNe.equals("NUMBER")) {
						tempNumber = nextFoundWord;
					}

					nextFoundNe = tokens.get(i + 1).getNe();
					nextFoundWord = tokens.get(i + 1).getWord();
					nextFoundIndex = i + 1;
					i++;
				}
			} catch (Exception err) {
				nextFoundNe = "O";
				nextFoundWord = "";
			}

			String preFoundNe = "O";
			@SuppressWarnings("unused")
			String preFoundWord = "";
			try {
				int i = index;
				while (preFoundNe.equals("O")) {
					preFoundNe = tokens.get(i - 1).getNe();
					preFoundWord = tokens.get(i - 1).getWord();
					i--;
				}
			} catch (Exception err) {
				preFoundNe = "O";
				preFoundWord = "";
			}

			String pos = tokens.get(index).getPos();

			switch (ne) {
			case "PERSON":
				addEntityOrClass(word, ne, "prop:", "white|prop:black", "?game");
				break;
			case "MISC":
				addEntityOrClass(word, ne, "prop:", "", "?game");
				break;
			case "LOCATION":
				addEntityOrClass(word, ne, "prop:", "site", "?game");
				break;
			case "ORGANIZATION":
				addEntityOrClass(word, ne, "prop:", "event", "?game");
				break;
			case "DATE":
				addEntityOrClass(word, ne, "prop:", "date", "?game");
				break;
			case "ORDINAL":
				if (nextNe.equals("round")) {

					index += 1;
					if (word.equals("last")) {
						addEntityOrClass("round", ne, "prop:", "round", "?game");
						options.setLimitStr(1);
						options.setOffsetStr(0);
						options.setOrderStr("DESC", "?round");
					} else {
						addEntityOrClass(word.replaceAll("\\D+", ""), ne, "prop:", "round", "?game");
					}

				} else if (nextNe.equals("event") && !word.equals("last")) {

					similar.setOffset(Integer.parseInt(word.replaceAll("\\D+", "")));
				} else if (nextNe.equals("MISC") && !word.equals("last")) {
					similar.setOffset(Integer.parseInt(word.replaceAll("\\D+", "")));

				} else if (nextNe.equals("site") && !word.equals("last")) {
					similar.setOffset(Integer.parseInt(word.replaceAll("\\D+", "")));
				} else {
					if (word.equals("last")) {
						options.setLimitStr(1);
						options.setOffsetStr(0);
						options.setOrderStr("DESC", "?date");
					} else {
						options.setLimitStr(1);
						options.setOffsetStr((Integer.valueOf(word.replaceAll("\\D+", ""))) - 1);
						if (!nextFoundNe.equals("jjs_pos") && !nextFoundNe.equals("jjs_neg")) {
							options.setOrderStr("ASC", "?date");
						}

					}
					if (getClassByName("?date") == null) {
						classes.add(new Classes(classes.size() + 1, "?date", "prop:", "date", 999, "?game"));
					}

				}

				break;
			case "NUMBER":
				if (preNe.equals("round")) {

					index += 1;
					addEntityOrClass(word.replaceAll("\\D+", ""), ne, "prop:", "round", "?game");
				} else if (nextNe.equals("piece")) {
					// will be used in case piece
					break;
				} else if (nextNe.equals("moves")) {
					break;
				} else {
					options.setLimitStr(Integer.valueOf(word));
					options.setOffsetStr(0);

				}
				break;
			case "game":

				addEntityOrClass("", ne, "a prop:ChessGame", "", "?game");
				break;
			case "eco":
				addEntityOrClass(word.substring(0, 1).toUpperCase() + word.substring(1), ne, "cont:", "openingCode",
						"?contEco");
				if (getClassByName("?contEco") == null) {
					classes.add(new Classes(classes.size() + 1, "?contEco", "cont:", "eco", 999, "?game"));
				}
				break;
			case "elo":
				isElo = true;
				if (word.equals(ne)) {
					addEntityOrClass(word, ne, "prop:", "whiteelo|prop:blackelo", "?game");
				}

				else if (!preFoundNe.equals("jjr_pos") && !preFoundNe.equals("jjr_neg") && !nextNe.equals("O")) {
					addEntityOrClass(word, ne, "prop:", "whiteelo|prop:blackelo", "?game");
					// TODO

				} else if (preFoundNe.equals("jjr_pos")) {
					this.filters.addGreaterThan("?elo", word);
				} else if (preFoundNe.equals("jjr_neg")) {
					this.filters.addLowerThan("?elo", word);
				}

				break;
			case "black":
				isBlack = true;
				break;
			case "white":
				isWhite = true;
				break;
			case "1-0":
				isDecisive = true;
				addEntityOrClass(ne, ne, "prop:", "result", "?game");
				break;
			case "0-1":
				isDecisive = true;
				addEntityOrClass(ne, ne, "prop:", "result", "?game");
				break;
			case "1/2-1/2":
				addEntityOrClass(ne, ne, "prop:", "result", "?game");
				break;
			case "event":
				addEntityOrClass(word, ne, "prop:", "event", "?game");
				// if (!this.tokens.get(index).getWord().equals("tournament")
				// && !this.tokens.get(index).getWord().equals("event")) {
				// filters.addRegex("?" + ne, word, true);
				// }
				break;
			case "eventEntity":
				entities.add(
						new Entity(entities.size() + 1, "'" + word + "'", "prop:", "event", index, index, "?game"));
				break;
			case "round":
				addEntityOrClass(word, ne, "prop:", "round", "?game");
				break;
			case "OPENING":
				if (word.equals("opening") && !pos.equals("NNP")) {
					addEntityOrClass(word, ne, "cont:", "openingName", "?contEco");
					if (getClassByName("?contEco") == null) {
						classes.add(new Classes(classes.size() + 1, "?contEco", "cont:", "eco", 999, "?game"));
					}
				} else {
					addEntityOrClass(word, ne, "cont:", "openingName", "?contEco");
					if (getClassByName("?contEco") == null) {
						classes.add(new Classes(classes.size() + 1, "?contEco", "cont:", "eco", 999, "?game"));
					}

				}
				break;
			case "moves":

				addEntityOrClass(word, ne, "prop:", "moves", "?game");
				break;
			case "move":
				moveCounter++;
				if (getClassByName("?moves") == null) {
					classes.add(new Classes(classes.size() + 1, "?move" + moveCounter, "prop:", "moves", 999, "?game"));
				}
				if (word.matches("[kqrbn]+[a-h][1-8]{1}[\\-x][a-h][1-8]{1}")) {
					addEntityOrClass(word.substring(0, 1).toUpperCase() + word.substring(1), ne, "prop:", "move",
							"?move" + moveCounter);
				} else {
					addEntityOrClass(word, ne, "prop:", "move", "?move" + moveCounter);
				}

				entities.add(new Entity(entities.size() + 1, "'" + moveCounter + "' " + "^^xsd:nonNegativeInteger",
						"prop:", "moveNr", 999, 999, "?move" + moveCounter));

				break;
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

				if (getClassByName("?moves") == null) {
					classes.add(new Classes(classes.size() + 1, "?moves", "prop:", "moves", 999, "?game"));
				}
				if (getClassByName("?fen") == null) {
					classes.add(new Classes(classes.size() + 1, "?fen", "prop:", "fen", 999, "?moves"));
				}

				break;
			case "average":
				if (!nextFoundNe.isEmpty()) {

					this.topics.addAvg("?" + nextFoundNe);
				} else {
					this.topics.addAvg("?" + preFoundNe);
				}

				break;
			case "jjr_pos":
				isFilter = true;
				if (tempNumber.isEmpty()) {
					isElo = true;
					if (getClassByName("?elo") == null) {
						classes.add(new Classes(classes.size() + 1, "?elo", "prop:", "whiteelo|prop:blackelo", index,
								"?game"));

					}

				} else {
					if (nextFoundNe.equals("moves")) {
						if (getClassByName("?moveNr") == null) {
							classes.add(new Classes(classes.size() + 1, "?moveNr", "prop:", "moveNr", nextFoundIndex,
									"?moves"));
						}
						filters.addGreaterThan("?moveNr", tempNumber);
					} else if (nextFoundNe.equals("round")) {
						if (getClassByName("?round") == null) {
							classes.add(new Classes(classes.size() + 1, "?round", "prop:", "round", nextFoundIndex,
									"?game"));
						}
						filters.addGreaterThan("?round", tempNumber);
					}

				}

				// index = nextFoundIndex + 1;
				break;
			case "jjr_neg":
				isFilter = true;
				if (tempNumber.isEmpty()) {

					if (getClassByName("?elo") == null) {
						classes.add(new Classes(classes.size() + 1, "?elo", "prop:", "whiteelo|prop:blackelo", index,
								"?game"));
						isElo = true;
					}
					this.options.setOrderStr("ASC", "?elo");
				} else {
					if (nextFoundNe.equals("moves")) {
						if (getClassByName("?moveNr") == null) {
							classes.add(new Classes(classes.size() + 1, "?moveNr", "prop:", "moveNr", nextFoundIndex,
									"?moves"));
						}
						filters.addLowerThan("?moveNr", tempNumber);
					} else if (nextFoundNe.equals("round")) {
						if (getClassByName("?round") == null) {
							classes.add(new Classes(classes.size() + 1, "?round", "prop:", "round", nextFoundIndex,
									"?game"));
						}
						filters.addLowerThan("?round", tempNumber);
					}

				}
				break;
			case "jjs_pos":
				if (word.equals("longest") && nextFoundNe.equals("game")) {
					if (getClassByName("?moveNr") == null) {
						classes.add(new Classes(classes.size() + 1, "?moveNr", "prop:", "moveNr", nextFoundIndex,
								"?moves"));
					}
					if (getClassByName("?moves") == null) {
						classes.add(
								new Classes(classes.size() + 1, "?moves", "prop:", "moves", nextFoundIndex, "?game"));
					}
					this.topics.addMax("?moveNr");
					this.options.setOrderStr("DESC", "?nr");
					if (this.options.getLimitStr().isEmpty()) {
						this.options.setLimitStr(1);
						this.options.setOffsetStr(0);
					}

				} else if (!word.equals("longest")) {
					isElo = true;
					if (getClassByName("?elo") == null) {
						classes.add(new Classes(classes.size() + 1, "?elo", "prop:", "whiteelo|prop:blackelo", index,
								"?game"));
					}
					if (nextFoundNe.equals("PERSON")) {
						this.topics.addMax("?elo");
					} else {
						this.topics.addAvg("?elo");
					}

					this.topics.addToBlacklist("?elo");
					this.options.setOrderStr("DESC", "?nr");
					if (this.options.getLimitStr().isEmpty()) {
						this.options.setLimitStr(1);
						this.options.setOffsetStr(0);
					}
				}

				break;
			case "jjs_neg":
				if (word.equals("shortest") && nextFoundNe.equals("game")) {
					if (getClassByName("?moveNr") == null) {
						classes.add(new Classes(classes.size() + 1, "?moveNr", "prop:", "moveNr", nextFoundIndex,
								"?moves"));
					}
					if (getClassByName("?moves") == null) {
						classes.add(
								new Classes(classes.size() + 1, "?moves", "prop:", "moves", nextFoundIndex, "?game"));
					}
					this.topics.addMin("?moveNr");
					this.options.setOrderStr("ASC", "?nr");
					if (this.options.getLimitStr().isEmpty()) {
						this.options.setLimitStr(1);
						this.options.setOffsetStr(0);
					}

				} else if (!word.equals("shortest")) {
					isElo = true;
					if (getClassByName("?elo") == null) {
						classes.add(new Classes(classes.size() + 1, "?elo", "prop:", "whiteelo|prop:blackelo", index,
								"?game"));
					}
					if (nextFoundNe.equals("PERSON")) {
						this.topics.addMin("?elo");
					} else {
						this.topics.addAvg("?elo");
					}

					this.topics.addToBlacklist("?elo");
					this.options.setOrderStr("ASC", "?nr");
					if (this.options.getLimitStr().isEmpty()) {
						this.options.setLimitStr(1);
						this.options.setOffsetStr(0);
					}
				}
				break;
			case "count":
				isCount = true;
				break;

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

					// Flipper f = new Flipper();
					// if(getEntityByName("'1-0'") != null){
					// System.out.println(f.toFlip("'1-0'"));
					// getEntityByName("'1-0'").setEntityName(f.toFlip("'1-0'"));
					// }
				} else if (e.getEndPosition() != personHasColor[0]
						&& e.getPropertyName().equals("prop:white|prop:black")) {
					e.setPropertyName("prop:white");
				}
			}
			for (Classes c : classes) {

				if (c.getPosition() == personHasColor[0]) {
					c.setPropertyName("prop:black");

					Flipper f = new Flipper();
					if (getEntityByName("'1-0'") != null) {

						getEntityByName("'1-0'").setEntityName(f.toFlip("'1-0'"));
					}
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
				// System.out.println(tempPerson);
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
						// System.out.println("3. " +
						// getEntityByEndPosition(pair.getValue()).getPropertyName());
						try {
							getEntityByEndPosition(pair.getValue()).setPropertyName("prop:white");
						} catch (Exception err) {
							getClassByPosition(pair.getValue()).setPropertyName("prop:white");
						}

						isWhite = true;

					} catch (NullPointerException err) {

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
			} catch (Exception err) {

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
						
						if(getEntityByName("'1-0'") != null){
							isWhite = true;
						} else if (getEntityByName("'0-1'") != null){
							isBlack = true;
						}
						
					}
					isUnion = true;
				}
			} catch (Exception err) {

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

				for (Classes c : classes) {
					if (c.getPropertyName().equals("prop:white|prop:black") && isFirst) {
						c.setPropertyName("prop:white");
						isFirst = false;
					} else if (c.getPropertyName().equals("prop:white|prop:black") && !isFirst) {
						c.setPropertyName("prop:black");
					}
				}
			}

		}

	}

	public void addEntityOrClass(String word, String ne, String propertyPrefix, String property, String resource) {
		int startPosition = index;

		if (Character.isUpperCase(ne.charAt(0))) {
			while ((index + 1) < tokens.size() && tokens.get(index + 1).getNe().equals(ne)) {
				word += " " + tokens.get(index + 1).getWord();
				word = word.replace(" '", "'");
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

		} else if (ne.equals("DATE")) {
			TimeTagger tt = new TimeTagger();
			word = tt.getDate(word);
		}

		int endPosition = index;

		String entity = vocabulary.INVERSED_PROPERTIES.get(word);

		if (entity != null && entity != "1-0" && entity != "0-1" && entity != "1/2-1/2") {

			if (getClassByName("?" + ne.toLowerCase()) == null) {
				classes.add(new Classes(classes.size() + 1, "?" + ne.toLowerCase(), propertyPrefix, property,
						endPosition, resource));
			}

		} else if (ne.equals("DATE")) {
			if (getClassByName("?date") == null) {
				classes.add(new Classes(classes.size() + 1, "?date", propertyPrefix, property, endPosition, resource));
			}
			filters.addRegex("?date", word, false);
		} else {

			if (!word.isEmpty()) {
				entities.add(new Entity(entities.size() + 1, "'" + word + "'", propertyPrefix, property, startPosition,
						endPosition, resource));
			} else {
				entities.add(new Entity(entities.size() + 1, word, propertyPrefix, property, startPosition, endPosition,
						resource));
			}

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

			tempEntities.add(new Entity(tempEntities.size() + 1, newEntityName, e.getPropertyPrefix(),
					newPropertyName.replace(e.getPropertyPrefix(), ""), e.getStartPosition(), e.getEndPosition(),
					e.getResourceName()));
		}

		for (Entity t : tempEntities) {
			entities.add(new Entity(entities.size() + 1, t.getEntityName(), t.getPropertyPrefix(),
					t.getPropertyName().replace(t.getPropertyPrefix(), ""), t.getStartPosition(), t.getEndPosition(),
					t.getResourceName()));
		}

		// classes
		List<Classes> tempClasses = new ArrayList<Classes>();

		for (Classes c : classes) {

			newPropertyName = flipper.toFlip(c.getPropertyName());
			newEntityName = flipper.toFlip(c.getClassesName());

			tempClasses.add(new Classes(tempClasses.size() + 1, newEntityName, c.getPropertyPrefix(),
					newPropertyName.replace(c.getPropertyPrefix(), ""), c.getPosition(), c.getResourceName()));
		}

		for (Classes t : tempClasses) {
			classes.add(new Classes(classes.size() + 1, t.getClassesName(), t.getPropertyPrefix(),
					t.getPropertyName().replace(t.getPropertyPrefix(), ""), t.getPosition(), t.getResourceName()));
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

	public String getTopicStr() {
		return topics.getString();
	}

	public StringSimilarity getSimilar() {
		return similar;
	}

	public void setSimilar(StringSimilarity similar) {
		this.similar = similar;
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

	public Classes getClassByName(String name) {
		for (Classes c : classes) {
			if (c.getClassesName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public Entity getEntityByName(String name) {
		for (Entity e : entities) {
			if (e.getEntityName().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		String query = "Show me positions with bishop and bishop against rook.";

		Parser p = new Parser(query);

		System.out.println(p.getTokens().toString());
		System.out.println(p.getEntities().toString());
		System.out.println(p.getClasses().toString());

	}

}