package de.daug.semanticchess.Annotation;	
	/**
	 * class for a token (word, POS, NER)
	 */
	public class Token{
		private String word = "";
		private String pos = "";
		private String ne = "";
		
		/**
		 * constructor
		 * @param word: word in the query
		 * @param pos: tag of the current word
		 * @param ne: NER of the cuurent word
		 */
		public Token(String word, String pos, String ne){
			this.word = word;
			this.pos = pos;
			this.ne = ne;
		}
		
		/**
		 * get word from token
		 * @return word
		 */
		public String getWord(){
			return word;
		}
		
		/**
		 * get POS-tag of current word
		 * @return POS
		 */
		public String getPos(){
			return pos;
		}
		
		/**
		 * get NER of the current word
		 * @return NER
		 */
		public String getNe(){
			return ne;
		}
		
		/**
		 * set word
		 * @param word
		 */
		public void setWord(String word){
			this.word = word;
		}
		
		/**
		 * set POS of the current word
		 * @param pos
		 */
		public void setPos(String pos){
			this.pos = pos;
		}
		
		/**
		 * set NER of the current word
		 * @param ne
		 */
		public void setNe(String ne){
			this.ne = ne;
		}
		
		/**
		 * combine word POS and NER
		 */
		public String toString(){
			String taggedWord = word + "\t//" + pos + "\t>>" + ne; 			
			return taggedWord;
		}
		
	}