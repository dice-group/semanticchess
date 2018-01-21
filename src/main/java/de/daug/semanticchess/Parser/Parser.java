package de.daug.semanticchess.Parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.List;

import java.util.Map.Entry;

import java.util.Stack;

import de.daug.semanticchess.Annotation.PosTagger;
import de.daug.semanticchess.Annotation.TimeTagger;
import de.daug.semanticchess.Annotation.Token;
import de.daug.semanticchess.Annotation.WebPosTagger;
import de.daug.semanticchess.Database.StringSimilarity;
import de.daug.semanticchess.Parser.Utils.Classes;
import de.daug.semanticchess.Parser.Utils.CustomNer;
import de.daug.semanticchess.Parser.Utils.Entity;
import de.daug.semanticchess.Parser.Utils.FenRegex;
import de.daug.semanticchess.Parser.Utils.Filters;
import de.daug.semanticchess.Parser.Utils.Flipper;
import de.daug.semanticchess.Parser.Utils.Options;
import de.daug.semanticchess.Parser.Utils.PropertyAllocator;
import de.daug.semanticchess.Parser.Utils.TopicFinder;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;

/**
 * This class parses the user query to a sequence to find a suitable SPARQL query.
 * Therefore it performs the following step:
 * 1. analyze the query with the Stanford coreNLP
 * 2. expand NER from the coreNLP with the CustomNer.java
 * 3. check every Token and check their NER
 * 4. save entities and classes
 * 5. check if colors are used in the query and if necessary change properties that are color specific
 * 6. check if ELO is used in the query and if necessary change properties that are ELO and color specific
 * 7. check the result and if necessary produce a UNION clause and change properties that are result specific
 * 8. check if the program has to add a regex with a FEN
 * 9. collect the topics of the query (for the SELECT clause)
 * 10. check if there are aggregates in the SELECT clause and group variables if necessary
 * 11. add variables to the SELECT clause if only ?game is in  the SELECT clause
 * 12. build a sequence code for the Allocator
 */
public class Parser {
	private List<Token> tokens = new ArrayList<Token>();
	private String query;

	//Tagger
	PosTagger tagger = null;
	WebPosTagger webTagger = null;
	TimeTagger timeTagger = null;
	
	//parts of the SPARQL query
	private List<Entity> entities = new ArrayList<Entity>();
	private List<Classes> classes = new ArrayList<Classes>();
	private Options options = new Options();
	private FenRegex fenReg = new FenRegex();
	private Filters filters = new Filters();
	private TopicFinder topics = new TopicFinder();
	private StringSimilarity similar = new StringSimilarity();
	
	//dictionary
	private ChessVocabulary vocabulary = new ChessVocabulary();
	
	//counter
	private int index;
	private int moveCounter = 0;

	//flags
	private boolean isBlack = false;
	private boolean isWhite = false;
	private boolean isDecisive = false;
	private boolean isUnion = false;
	private boolean isElo = false;
	private boolean isFen = false;
	private boolean isCount = false;
	private boolean isFilter = false;

	//sequence code
	private String sequence;
	
	/**
	 * constructor: performs multiple steps to collect all 
	 * entities, classes, options (modifiers), filters and aggregates.
	 * at last it returns a sequence code for the Allocator.
	 * @param query: user input
	 */
	public Parser(String query) {
		this.query = query;
		
		//Stanford coreNLP
		List<Token> tokens = null;
		
		try {
			this.webTagger = new WebPosTagger();
			StanfordCoreNLPClient pipeline = webTagger.getPipeline();
			webTagger.setQuery(query);
			webTagger.setDocument(webTagger.setAnnotator(pipeline, webTagger.getQuery()));
			webTagger.initAnnotations();
			System.out.printf("%-16s %s\n", "Parser: ", "Webclient Stanford coreNLP complete.");		
			tokens = webTagger.getTokens();
			
		} catch (Exception e){
			this.tagger = new PosTagger();
			StanfordCoreNLP pipeline = tagger.getPipeline();
			tagger.setQuery(query);
			tagger.setDocument(tagger.setAnnotator(pipeline, tagger.getQuery()));
			tagger.initAnnotations();
			System.out.printf("%-16s %s\n", "Parser: ","Stanford coreNLP complete.");		
			tokens = tagger.getTokens();
		}
		
		

		
		//Custom NER
		CustomNer cNer = new CustomNer();
		tokens = cNer.stemming(tokens);
		tokens = cNer.checkChessVocabulary(tokens);
		tokens = cNer.checkElo(tokens);
		tokens = cNer.checkOpening(tokens);		
		this.tokens = tokens;
		System.out.printf("%-16s %s\n", "Parser: ","Custom NER complete.");
		System.out.printf("%-16s %s\n", "Result: ", this.tokens.toString());	

		//run through every token and analyze its meaning
		collectEntities(tokens);
		System.out.printf("%-16s %s\n", "Parser: ","Entities and classes collected.");
		System.out.printf("%-16s %s\n", "Entities: ", entities.toString());
		System.out.printf("%-16s %s\n", "Classes: ", classes.toString());
		
		//if flags isBlack or isWhite are set change properties that are color specific 
		if (isBlack || isWhite) {
			try {
				injectColor();
			} catch (Exception err) {
			}
		}
		System.out.printf("%-16s %s\n", "Parser: ","Color checker complete.");

		//if flag isElo is set change properties that are elo specific
		if (isElo) {
			try {
				injectElo();
			} catch (Exception err) {
			}
		}
		System.out.printf("%-16s %s\n", "Parser: ","ELO checker complete.");
		
		//check the result and if necessary change properties that are result specific
		resultChecker();
		System.out.printf("%-16s %s\n", "Parser: ","Result checker complete.");
		
		//if flag isUnion is set add an UNION clause
		if (isUnion()) {
			makeUnion();
			System.out.printf("%-16s %s\n", "Parser: ","UNION is added.");
		}


		//if flag isFilter and if Fen is set create regex with a FEN
		if (isFilter) {
			if (isFen) {
				fenReg.createFen();
				filters.addRegex("?fen", fenReg.getFen(), false);
				System.out.printf("%-16s %s\n", "Parser: ","FEN regex is added.");
			}
		}
		
		//collect topics for the SELECT clause
		topics.collectTopics(this.entities, this.classes);

		
		System.out.printf("%-16s %s\n", "Parser: ","Topics collected.");
		
		/* if the query implies there should be something to count, e.g. 'most often' or 'how often'
		 * add an aggregate to the SELECT clause
		 */ 
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
				topics.addCount("DISTINCT " + entities.get(0).getResourceName());
				this.options.setOrderStr("DESC", "?nr");

			} else {
				topics.addCount(classes.get(0).getClassesName());
				this.options.setOrderStr("DESC", "?nr");
				this.options.setGroupStr(classes.get(0).getClassesName());
			}
			System.out.printf("%-16s %s\n", "Parser: ","COUNT added");
		}
		
		//check if an aggregate is used in the SELECT clause
		if (topics.isAlgebra()) {
			for (String topic : topics.getTopics()) {
				
				if (this.options.getGroupStr().indexOf(topic) == -1 && topic.indexOf("(") == -1) {
					this.options.setGroupStr(topic);
				}
			}
			System.out.printf("%-16s %s\n", "Parser: ","Topics grouped.");
		}
		
		
		//add some information to the result if only games should be returned
		if(topics.onlyGames() && !topics.isAlgebra() && !isUnion){
			if (getClassByName("?white") == null) {
				classes.add(new Classes(classes.size() + 1, "?white", "prop:", "white", 999, "?game"));
				topics.add("?white");
			}
			if (getClassByName("?black") == null) {
				classes.add(new Classes(classes.size() + 1, "?black", "prop:", "black", 999, "?game"));
				topics.add("?black");
			}
			if (getClassByName("?date") == null) {
				classes.add(new Classes(classes.size() + 1, "?date", "prop:", "date", 999, "?game"));
				topics.add("?date");
			}
			System.out.printf("%-16s %s\n", "Parser: ","Information added to the SELECT clause.");
		}
		
		//get the sequence code
		if (!isUnion) {
			this.sequence = "_" + classes.size() + "" + entities.size() + "0";
		} else {
			this.sequence = "_" + classes.size() + "" + entities.size() + "1";
		}
		System.out.printf("%-16s %s\n", "SEQUENCE CODE: ", this.sequence);	
		SimpleDateFormat sdf = new SimpleDateFormat("DD.MM.yy HH:mm:ss:SS");
		String uhrzeit = sdf.format(new Date());
		System.out.println(uhrzeit + " Sequence Code is found.");
	}
	
	/**
	 * runs through all tokens and analyzes their meanings
	 * @param tokens: word with POS and NER
	 */
	public void collectEntities(List<Token> tokens) {
		boolean isBlackPieces = false;
		
		for (index = 0; index < tokens.size(); index++) {
			//save properties of the token
			String word = tokens.get(index).getWord();
			String ne = tokens.get(index).getNe();
			String pos = tokens.get(index).getPos();
			
			//get the properties of the previous token
			String preNe = "O";
			String preWord = "";
			try {
				preNe = tokens.get(index - 1).getNe();
				preWord = tokens.get(index - 1).getWord();
			} catch (Exception e) {
				preNe = "O";
			}
			
			//get the properties of the next token
			String nextNe = "O";
			String nextWord = "";
			try {
				nextNe = tokens.get(index + 1).getNe();
				nextWord = tokens.get(index + 1).getWord();
			} catch (Exception e) {
				nextNe = "O";
			}

			//get the properties of the next token where the NER is not O
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

			//get the properties of the previous token where the NER is not O
			String preFoundNe = "O";
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

			//Switch through NER of a token
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
				
				//check the next token and his properties and set if necessary LIMIT and OFFSET
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
						if (getClassByName("?date") == null) {
							classes.add(new Classes(classes.size() + 1, "?date", "prop:", "date", 999, "?game"));
						}
					} else {
						options.setLimitStr(1);
						options.setOffsetStr((Integer.valueOf(word.replaceAll("\\D+", ""))) - 1);
						if (!nextFoundNe.equals("jjs_pos") && !nextFoundNe.equals("jjs_neg") && !nextFoundNe.equals("count")) {
							options.setOrderStr("ASC", "?date");
							if (getClassByName("?date") == null) {
								classes.add(new Classes(classes.size() + 1, "?date", "prop:", "date", 999, "?game"));
							}
						}

					}


				}

				break;
			case "NUMBER":
				
				//check the properties of the tokens with NER not O around this token 
				if (preNe.equals("round")) {

					index += 1;
					addEntityOrClass(word.replaceAll("\\D+", ""), ne, "prop:", "round", "?game");
				} else if (nextNe.equals("piece")) {
					// will be used in case piece
					break;
				} else if (nextNe.equals("moves") && !preWord.equals("than")) {
					if (getClassByName("?moveNr") == null) {
						classes.add(new Classes(classes.size() + 1, "?moveNr", "prop:", "moveNr", 999, "?moves"));
					}
					filters.addEqual("?moveNr","'"+String.valueOf(Integer.valueOf(word)*2)+"'^^xsd:nonNegativeInteger");
				} else if (nextNe.equals("round") && !preWord.equals("than")){
					filters.addEqual("?round","'"+word+"'^^xsd:nonNegativeInteger");
				} else if(!preWord.equals("than")) {
					options.setLimitStr(Integer.valueOf(word));
					options.setOffsetStr(0);

				}
				break;
			//CUSTOM NER
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
				break;
			case "eventEntity":
				entities.add(new Entity(entities.size() + 1, "'" + word + "'", "prop:", "event", index, index, "?game"));
				break;
			case "round":
				addEntityOrClass(word, ne, "prop:", "round", "?game");
				break;
			case "site":
				if(!pos.equals("WRB")){
					addEntityOrClass(word, ne, "prop:", "site", "?game");
				}
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
				if (word.matches("[kqrbn]*.*[a-h][1-8]{1}")) {
					if(word.startsWith("k") || word.startsWith("q") || word.startsWith("r") || word.startsWith("b") || word.startsWith("n")){
						word = word.substring(0,1).toUpperCase() + word.substring(1);
					}
					addEntityOrClass(word, ne, "prop:", "move",
							"?move" + moveCounter);
				}

				entities.add(new Entity(entities.size() + 1, "'" + moveCounter + "' " + "^^xsd:nonNegativeInteger",
						"prop:", "moveNr", 999, 999, "?move" + moveCounter));
				break;
			case "piece":
				if (preFoundNe.equals("fen") || nextFoundNe.equals("fen")) {
					break;
				}
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
			case "fen":
				if (getClassByName("?moves") == null) {
					classes.add(new Classes(classes.size() + 1, "?moves", "prop:", "moves", 999, "?game"));
				}
				if (getClassByName("?move") == null) {
					classes.add(new Classes(classes.size() + 1, "?move", "prop:", "move", 999, "?moves"));
				}
				if (word.equals("castle") || word.equals("castling")) {

					if (preWord.equals("long") || nextWord.equals("long") || preWord.equals("queenside") ||nextWord.equals("queenside")) {
						filters.addRegex("?move", fenReg.createMove("castling", "long"), true);
					} else {
						filters.addRegex("?move", fenReg.createMove("castling", "short"), true);
					}
				} else if (word.indexOf("promot") > -1) {
					if (word.indexOf("underpromot") > -1) {
						String piece = "";
						if (nextFoundNe.equals("piece")) {
							piece = nextFoundWord;
						} else if (preFoundNe.equals("piece")) {
							piece = preFoundWord;
						}

						if (piece.isEmpty()) {
							filters.addRegex("?move", fenReg.createMove("underpromotion", ""), true);
						} else {
							filters.addRegex("?move", fenReg.createMove("promotion", piece), true);
						}

					}
				} else if (word.indexOf("captur") > -1 || word.indexOf("exchange") > -1) {
					// exception: the exchange -> not implemented
					filters.addRegex("?move", fenReg.createMove("capture", ""), true);
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
						filters.addGreaterThan("?moveNr",String.valueOf(Integer.valueOf(tempNumber)*2));
					} else if (nextFoundNe.equals("round")) {
						if (getClassByName("?round") == null) {
							classes.add(new Classes(classes.size() + 1, "?round", "prop:", "round", nextFoundIndex,
									"?game"));
						}
						filters.addGreaterThan("?round", String.valueOf(Integer.valueOf(tempNumber)*2));
					}
				}
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
						if (getClassByName("?move") == null) {
							classes.add(new Classes(classes.size() + 1, "?move", "prop:", "move", nextFoundIndex,
									"?moves"));
						}
						
						topics.addCount("distinct ?move");
						options.setHavingStr("(COUNT(distinct ?move) < '" + String.valueOf(Integer.valueOf(tempNumber)*2) + "'^^xsd:nonNegativeInteger)");
						//filters.addLowerThan("?nr", String.valueOf(Integer.valueOf(tempNumber)*2));
					} else if (nextFoundNe.equals("round")) {
						if (getClassByName("?round") == null) {
							classes.add(new Classes(classes.size() + 1, "?round", "prop:", "round", nextFoundIndex,
									"?game"));
						}
						filters.addLowerThan("?round", "'" + String.valueOf(Integer.valueOf(tempNumber)*2)  + "'^^xsd:nonNegativeInteger)");
					}
				}
				break;
			case "jjs_pos":
				if (word.equals("longest") && nextFoundNe.equals("game")) {
					if (getClassByName("?moveNr") == null) {
						classes.add(new Classes(classes.size() + 1, "?moveNr", "prop:", "moveNr", 999,
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

				} else if (!word.equals("longest") && !word.equals("main")) {
					isElo = true;
					if (getClassByName("?elo") == null) {
						classes.add(new Classes(classes.size() + 1, "?elo", "prop:", "whiteelo|prop:blackelo", index,
								"?game"));
					}
					if (nextFoundNe.equals("PERSON")) {
						this.topics.addMax("?elo");
					} else if(nextFoundNe.equals("elo")){
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
				} else if(word.equals("main")) {
					this.topics.addCount("?game");
					this.options.setOrderStr("DESC", "?nr");
					this.options.setLimitStr(1);
					
				}
				break;
			case "jjs_neg":
				if (word.equals("shortest") && nextFoundNe.equals("game")) {
					if (getClassByName("?move") == null) {
						classes.add(new Classes(classes.size() + 1, "?move", "prop:", "move", nextFoundIndex,
								"?moves"));
					}
					if (getClassByName("?moves") == null) {
						classes.add(
								new Classes(classes.size() + 1, "?moves", "prop:", "moves", nextFoundIndex, "?game"));
					}
					this.topics.addCount("?move");
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
					} else if(nextFoundNe.equals("elo")){
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
			case "temporal":
				if (nextFoundNe.equals("fen") || nextFoundNe.equals("move")) {
					if (getClassByName("?moves") == null) {
						classes.add(new Classes(classes.size() + 1, "?moves", "prop:", "moves", 999, "?game"));
					}
					if (getClassByName("?move") == null) {
						classes.add(new Classes(classes.size() + 1, "?move", "prop:", "move", 999, "?moves"));
					}
					if (getClassByName("?moveNr") == null) {
						classes.add(new Classes(classes.size() + 1, "?moveNr", "prop:", "moveNr", 999, "?moves"));
					}
					if (word.equals("earliest")) {
						this.options.setOrderStr("ASC", "?moveNr");
					} else if(word.equals("latest")){
						this.options.setOrderStr("DESC", "?moveNr");
					}
					this.options.setLimitStr(1);
					this.options.setOffsetStr(0);
				} else {
					if (getClassByName("?date") == null) {
						classes.add(new Classes(classes.size() + 1, "?date", "prop:", "date", 999, "?game"));
					}
					if (word.equals("earliest")) {
						this.options.setOrderStr("ASC", "?date");
					} else if(word.equals("latest")){
						this.options.setOrderStr("DESC", "?date");
					}
					this.options.setLimitStr(1);
					this.options.setOffsetStr(0);
				}
				break;
			case "count":
				isCount = true;
				this.topics.setCount(true);
				if(this.options.getLimitStr().isEmpty()){
					this.options.setLimitStr(1);
					this.options.setOffsetStr(0);
				}
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
	
	/**
	 * changes properties of entities and classes that are color specific
	 */
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

	/**
	 * changes properties of entities and classes that are elo specific
	 */
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
						if (getEntityByName("'1-0'") != null) {
							isWhite = true;
						} else if (getEntityByName("'0-1'") != null) {
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

	/**
	 * checks the result and flips color specific properties if necessary 
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
	
	/**
	 * adds an entity or a class to an arraylist
	 * @param word: current word
	 * @param ne: named entity of the word
	 * @param propertyPrefix: property prefix that should be used in the SPARQL query
	 * @param property: property of the word
	 * @param resource: resource of the word
	 */
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
			this.timeTagger = new TimeTagger();
			word = timeTagger.getDate(word);
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
	/**
	 * prepares the entities and class for an UNION clause 
	 */
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
	
	/**
	 * get the list of tokens
	 * @return tokens
	 */
	List<Token> getTokens() {
		return tokens;
	}

	/**
	 * set the list of tokens
	 * @param tokens
	 */
	void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * get query
	 * @return query
	 */
	String getQuery() {
		return query;
	}

	/**
	 * set query
	 * @param query
	 */
	void setQuery(String query) {
		this.query = query;
	}

	/**
	 * get the list of entities
	 * @return entities
	 */
	List<Entity> getEntities() {
		return entities;
	}

	/**
	 * set the list of entities
	 * @param entities
	 */
	void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	/**
	 * get the list of classes
	 * @return classes
	 */
	List<Classes> getClasses() {
		return classes;
	}

	/**
	 * set the list of classes
	 * @param classes
	 */
	void setClasses(List<Classes> classes) {
		this.classes = classes;
	}

	/**
	 * get the isUnion flag
	 * @return boolean
	 */
	boolean isUnion() {
		return isUnion;
	}

	/**
	 * set the isUnion flag
	 * @param isUnion
	 */
	void setUnion(boolean isUnion) {
		this.isUnion = isUnion;
	}

	/**
	 * get the sequence code
	 * @return sequence code
	 */
	String getSequence() {
		return sequence;
	}

	/**
	 * set the sequence code
	 * @param sequence code
	 */
	void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * get options (modifiers)
	 * @return options
	 */
	Options getOptions() {
		return options;
	}

	/**
	 * get filters
	 * @return filters
	 */
	public Filters getFilters() {
		return filters;
	}

	/**
	 * set filers
	 * @param filters
	 */
	public void setFilters(Filters filters) {
		this.filters = filters;
	}

	/**
	 * get the topic String for  the SELECT clause
	 * @return topic String
	 */
	public String getTopicStr() {
		return topics.getString();
	}

	/**
	 * get the StringSimilarity class
	 * @return similar
	 */
	public StringSimilarity getSimilar() {
		return similar;
	}

	/**
	 * get the end position of an entity
	 * @param position in the arraylist
	 * @return end position in the user query
	 */
	public Entity getEntityByEndPosition(int position) {
		for (Entity e : entities) {
			if (e.getEndPosition() == position) {
				return e;
			}
		}
		return null;
	}

	/**
	 * get the start position of an entity
	 * @param position in the arraylist
	 * @return start position in the user query
	 */
	public Entity getEntityByStartPosition(int position) {
		for (Entity e : entities) {
			if (e.getStartPosition() == position) {
				return e;
			}
		}
		return null;
	}

	/**
	 * get the position of a class
	 * @param position in the arraylist
	 * @return position in the user query
	 */
	public Classes getClassByPosition(int position) {
		for (Classes c : classes) {
			if (c.getPosition() == position) {
				return c;
			}
		}
		return null;
	}

	/**
	 * get a class by name
	 * @param name of the class
	 * @return class
	 */
	public Classes getClassByName(String name) {
		for (Classes c : classes) {
			if (c.getClassesName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * get an entity by name
	 * @param name of the entity
	 * @return entity
	 */
	public Entity getEntityByName(String name) {
		for (Entity e : entities) {
			if (e.getEntityName().equals(name)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * get a class by property
	 * @param name of the property
	 * @return class
	 */
	public Classes getClassByProperty(String property) {
		for (Classes c : classes) {
			if (c.getPropertyName().equals(property)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * get an entity by property
	 * @param name of the property
	 * @return entity
	 */
	public Entity getEntityByProperty(String property) {
		for (Entity e : entities) {
			if (e.getPropertyName().equals(property)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * main method for testing
	 * @param args
	 */
	public static void main(String[] args) {
		String query = "Which player with black defeated Howard Staunton in December 1843?";

		Parser p = new Parser(query);

		System.out.println(p.getTokens().toString());
		System.out.println(p.getEntities().toString());
		System.out.println(p.getClasses().toString());

	}

}