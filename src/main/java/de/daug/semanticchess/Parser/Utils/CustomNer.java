package de.daug.semanticchess.Parser.Utils;

import java.util.List;

import de.daug.semanticchess.Annotation.Token;
import de.daug.semanticchess.Parser.ChessVocabulary;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.Morphology;

/**
 * This class adds or rewrites a NER tag to a word.
 * At first the Stanford coreNLP tags the words with NER.
 * After that this class completes the process.
 */
public class CustomNer {
	
	private ChessVocabulary  vocabulary = new ChessVocabulary();
	
	/**
	 * empty constructor
	 */
	public CustomNer (){		
	}
	
	/**
	 * does a stemming for all words to their basic forms
	 * @param tokens: List of words, ner and pos
	 * @return list of Token
	 */
	public List<Token> stemming(List<Token> tokens){
		for (Token token : tokens) {
			WordTag lemma = Morphology.stemStatic(token.getWord(), token.getPos());
			token.setWord(lemma.value());
		}	
		return tokens;
	}
	
	/**
	 * check the chess vocabulary to find consistencies with the tokens
	 * @param tokens: List of words, ner and pos
	 * @return list of Token
	 */
	public List<Token> checkChessVocabulary(List<Token> tokens){
		for (Token token : tokens) {
			if (token.getNe().equals("O")) {
				String word = token.getWord().toLowerCase();
				if (vocabulary.INVERSED_PROPERTIES.get(word) != null) {
					token.setNe(vocabulary.INVERSED_PROPERTIES.get(word));
				} else if (word.matches("[a-e][0-9]{2}")) {
					token.setNe("eco");
				} else if (word.matches("[kqrbn]*.*[a-h][1-8]{1}")){
					token.setNe("move");
				} else if (word.matches("[kqrbn]*[a-h][1-8]{1}[\\-x][a-h][1-8]{1}")){
					token.setNe("move");
				}
			}
		}
		
		return tokens;
	}
	
	/**
	 * check all dates, especially years whether they are ELO numbers or not
	 * @param tokens: List of words, NER and POS
	 * @return list of Token
	 */
	public List<Token> checkElo(List<Token> tokens){
		for (int i = 0; i < tokens.size(); i++) {
			String word = tokens.get(i).getWord().toLowerCase();
			if (tokens.get(i).getNe().equals("DATE")) {
				try {
					int elo = Integer.parseInt(word);
					boolean isElo = false;

					for (int j = 2; j >= -2; j--) {
						if ((i - j) < tokens.size()) {
							if (tokens.get(i - j).getNe().equals("elo")) {
								isElo = true;
								break;
							}
						}
					}
					if (isElo || (elo > 2025 && elo < 3000)) {
						tokens.get(i).setNe("elo");
					}
				} catch (Exception e) {}
			} else if(tokens.get(i).getNe().equals("ORGANIZATION")){
				if(word.equals("elo")){
					tokens.get(i).setNe("elo");
				}
			}
		}
		
		return tokens;
	}
	
	/**
	 * checks if found entities are openings
	 * @param tokens
	 * @return token with maybe an opening NER
	 */
	public List<Token> checkOpening(List<Token> tokens){
		for(int i = 0; i < tokens.size(); i++){
			String word = tokens.get(i).getWord().toLowerCase();
			String value = vocabulary.INVERSED_PROPERTIES.get(word);
			if(value != null && value.equals("OPENING") && tokens.get(i).getPos().equals("NNP")){
				value = "OPENING";
			} else if (value != null && value.equals("piece") && tokens.get(i).getPos().equals("NNP")){
				value = "OPENING";		
			}
			if (value != null && value.equals("OPENING")) {
				tokens.get(i).setNe(value);
				int j = i-1;
				while(tokens.get(j) != null && ((!tokens.get(j).getNe().equals("O") && !(tokens.get(j).getNe().indexOf("jj") > -1)) || tokens.get(j).getPos().equals("POS") || tokens.get(j).getPos().equals("NNP"))){
					tokens.get(j).setNe(value);
					j--;
				}
				int k = i+1;
				while(tokens.get(k) != null && ((!tokens.get(j).getNe().equals("O") && !(tokens.get(j).getNe().indexOf("jj") > -1)) || tokens.get(k).getPos().equals("POS") || tokens.get(k).getPos().equals("NNP"))){
					tokens.get(k).setNe(value);
					k++;
				}				
			}			
		}		
		return tokens;
	}
}