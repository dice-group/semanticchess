package de.daug.semanticchess.Parser.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class a returns regular expressions for some positions.
 * It can build expressions for positions with certain material combinations.
 * For example: knight versus rook
 * Or for certain moves, for example underpromotion, exchange, castling etc.
 */
public class FenRegex{
	
	List<Piece> piecesWhite = new ArrayList<Piece>();
	List<Piece> piecesBlack = new ArrayList<Piece>();
	
	private String fen = "^";
	
	private String MIN = "(?=(.*X){Y})";
	private String MAX = "(?!(.*X){Z})";
	private String notOnBoard = "((?![QqRrBbNnPp]).)*$";
	
	private int whiteQueenCounter = 0;
	private int whiteRookCounter = 0;
	private int whiteBishopCounter = 0;
	private int whiteKnightCounter = 0;
	private int whitePawnCounter = 0;
	
	private int blackQueenCounter = 0;
	private int blackRookCounter = 0;
	private int blackBishopCounter = 0;
	private int blackKnightCounter = 0;
	private int blackPawnCounter = 0;
	
	
	private String castlingShort = "O-O";
	private String castlingLong = "O-O-O";
	
	private String promote = "=[QqRrBbNn]$";
	private String underpromote = "=[RrBbNn]$";
	private String promoteQueen = "=[Qq]$";
	private String promoteRook = "=[Rr]$";
	private String promoteBishop = "=[Bb]$";
	private String promoteKnight = "=[Nn]$";	
	
	/**
	 * empty constructor
	 */
	public FenRegex(){
	}
	
	/**
	 * creates a regex for a certain move
	 * @param move
	 * @param piece: is called near the move. E.g. Underpromotion to a knigt
	 * @return regex
	 */
	public String createMove(String move, String piece){
		switch(move){
			case "castle":
			case "castling":
				if(piece.equals("long")){
					return castlingLong;
				}else {
					return castlingShort;
				}
			case "underpromotion":	
			case "underpromote":
				return underpromote;
			case "promotion":
			case "promote":
				switch(piece){
					case "queen":
						return promoteQueen;
					case "rook":
						return promoteRook;
					case "bishop":
						return promoteBishop;
					case "knight":
						return promoteKnight;
					default:
						return promote;
				}
			case "exchange":
			case "capture":
				return "x";	
			default:
				return "";
		}
	}

// 	not complete, it is massive 
//	public String createExchangeFen(){
//		String RRrBbbNNnn = "(?=(.*R){2})(?!(.*R){3})(?=(.*r){1})(?!(.*r){2})(?=(.*B){1})(?!(.*B){2})(?=([^ ]*b){2})(?!([^ ]*b){3})(?=(.*N){2})(?!(.*N){3})(?=(.*n){2})(?!(.*n){3})";
//		String RRrBBbbNnn = "(?=(.*R){2})(?!(.*R){3})(?=(.*r){1})(?!(.*r){2})(?=(.*B){2})(?!(.*B){3})(?=([^ ]*b){2})(?!([^ ]*b){3})(?=(.*N){1})(?!(.*N){2})(?=(.*n){2})(?!(.*n){3})";
//		String RrrBBbNNnn = "(?=(.*R){1})(?!(.*R){2})(?=(.*r){2})(?!(.*r){3})(?=(.*B){2})(?!(.*B){3})(?=([^ ]*b){1})(?!([^ ]*b){2})(?=(.*N){2})(?!(.*N){3})(?=(.*n){2})(?!(.*n){3})";
//		String RrrBBbbNNn = "(?=(.*R){1})(?!(.*R){2})(?=(.*r){2})(?!(.*r){3})(?=(.*B){2})(?!(.*B){3})(?=([^ ]*b){2})(?!([^ ]*b){3})(?=(.*N){2})(?!(.*N){3})(?=(.*n){1})(?!(.*n){2})";
//		
//		String RBbbNNnn = "(?=(.*R){1})(?!(.*R){2})(?=(.*r){0})(?!(.*r){1})(?=(.*B){1})(?!(.*B){2})(?=([^ ]*b){2})(?!([^ ]*b){3})(?=(.*N){2})(?!(.*N){3})(?=(.*n){2})(?!(.*n){3})";
//		String RBBbbNnn = "(?=(.*R){1})(?!(.*R){2})(?=(.*r){0})(?!(.*r){1})(?=(.*B){2})(?!(.*B){3})(?=([^ ]*b){2})(?!([^ ]*b){3})(?=(.*N){1})(?!(.*N){2})(?=(.*n){2})(?!(.*n){3})";
//		String rBBbNNnn = "(?=(.*R){0})(?!(.*R){1})(?=(.*r){1})(?!(.*r){2})(?=(.*B){2})(?!(.*B){3})(?=([^ ]*b){1})(?!([^ ]*b){2})(?=(.*N){2})(?!(.*N){3})(?=(.*n){2})(?!(.*n){3})";
//		String rBBbbNNn = "(?=(.*R){0})(?!(.*R){1})(?=(.*r){1})(?!(.*r){2})(?=(.*B){2})(?!(.*B){3})(?=([^ ]*b){2})(?!([^ ]*b){3})(?=(.*N){2})(?!(.*N){3})(?=(.*n){1})(?!(.*n){2})";
//		
//		String exchangeFen = "^"
//				+ "(" + RRrBbbNNnn
//				+ "|" + RRrBBbbNnn
//				+ "|" + RrrBBbNNnn
//				+ "|" + RrrBBbbNNn
//				+ "|" + RBbbNNnn
//				+ "|" + RBBbbNnn
//				+ "|" + rBBbNNnn
//				+ "|" + rBBbbNNn
//				+ ")";
//		
//		
//		return exchangeFen;
//	}
	
	/**
	 * add white pieces to a list
	 * @param counter: number of this piece
	 * @param piece: name of the piece
	 */
	public void addPieceWhite(int counter, String piece){
		switch(piece){
		case "queen":
			this.whiteQueenCounter += counter;
			break;
		case "rook":
			this.whiteRookCounter += counter;
			break;
		case "bishop":
			this.whiteBishopCounter += counter;
			break;	
		case "knight":
			this.whiteKnightCounter += counter;
			break;
		case "pawn":
			this.whitePawnCounter += counter;			
			break;
		default:
			break;
		}	
	}
	
	/**
	 * add black pieces to a list
	 * @param counter: number of this piece
	 * @param piece: name of the piece
	 */
	public void addPieceBlack(int counter, String piece){
		switch(piece){
		case "queen":
			this.blackQueenCounter += counter;
			break;
		case "rook":
			this.blackRookCounter += counter;
			break;
		case "bishop":
			this.blackBishopCounter += counter;
			break;	
		case "knight":
			this.blackKnightCounter += counter;
			break;
		case "pawn":
			this.blackPawnCounter += counter;
			
			break;
		default:
			break;
		}
	}
	
	/**
	 * creates fen from the list of white and black pieces
	 */
	public void createFen(){
		boolean whitePawnIsCalled = false;
		boolean blackPawnIsCalled = false;
		
		if(this.whiteQueenCounter > 0){
			Piece Q = new Piece(this.whiteQueenCounter, "queen");
			piecesWhite.add(Q);
		}
		if(this.whiteRookCounter > 0){
			Piece R = new Piece(this.whiteRookCounter, "rook");
			piecesWhite.add(R);
		}
		if(this.whiteBishopCounter > 0){
			Piece B = new Piece(this.whiteBishopCounter, "bishop");
			piecesWhite.add(B);
		}
		if(this.whiteKnightCounter > 0){
			Piece N = new Piece(this.whiteKnightCounter, "knight");
			piecesWhite.add(N);
		}
		if(this.whitePawnCounter > 0){
			Piece P = new Piece(this.whitePawnCounter, "pawn");
			piecesWhite.add(P);
		}
		if(this.blackQueenCounter > 0){
			Piece q = new Piece(this.blackQueenCounter, "queen");
			piecesBlack.add(q);
		}
		if(this.blackRookCounter > 0){
			Piece r = new Piece(this.blackRookCounter, "rook");
			piecesBlack.add(r);
		}
		if(this.blackBishopCounter > 0){
			Piece b = new Piece(this.blackBishopCounter, "bishop");
			piecesBlack.add(b);
		}
		if(this.blackKnightCounter > 0){
			Piece n = new Piece(this.blackKnightCounter, "knight");
			piecesBlack.add(n);
		}
		if(this.blackPawnCounter > 0){
			Piece p = new Piece(this.blackPawnCounter, "pawn");
			piecesBlack.add(p);
		}
				
		//white pieces
		for(Piece piece : piecesWhite){	
			switch(piece.getName()){
				case "queen":
					setFen("Q", piece.getCounter() , piece.getCounter()+1);
					break;
				case "rook":
					setFen("R", piece.getCounter() , piece.getCounter()+1);
					break;
				case "bishop":
					setFen("B", piece.getCounter() , piece.getCounter()+1);
					break;	
				case "knight":
					setFen("N", piece.getCounter() , piece.getCounter()+1);
					break;
				case "pawn":
					setFen("P", piece.getCounter() , piece.getCounter()+1);
					whitePawnIsCalled = true;
					break;
				default:
					break;
			}
		}
		
		//black pieces
		for(Piece piece : piecesBlack){
			switch(piece.getName()){
				case "queen":
					setFen("q", piece.getCounter() , piece.getCounter()+1);
					break;
				case "rook":
					setFen("r", piece.getCounter() , piece.getCounter()+1);
					break;
				case "bishop":
					setFen("b", piece.getCounter() , piece.getCounter()+1);
					break;	
				case "knight":
					setFen("n", piece.getCounter() , piece.getCounter()+1);
					break;
				case "pawn":
					setFen("p", piece.getCounter() , piece.getCounter()+1);
					blackPawnIsCalled = true;
					break;
				default:
					break;
			}
		}
		
		// if there are specified numbers of pawns then remove pawns from the list
		// of pieces that are not on the board
		if(!whitePawnIsCalled && !blackPawnIsCalled){
			this.notOnBoard = this.notOnBoard.replace("P", "");
			this.notOnBoard = this.notOnBoard.replace("p", "");	
		}
		
		if(piecesWhite.size() > 0){
			fen += this.notOnBoard;
		}		
	}
	
	/**
	 * sets up the regex for the fen
	 * @param piece: name of the piece
	 * @param min: minimal number of the piece
	 * @param max: maximal number of the piece
	 */
	private void setFen(String piece, int min, int max){
		if(piece.equals("b")){
			String pieceTemp = "[^ ]b";
			this.fen += MIN.replace("X", pieceTemp).replace("Y", Integer.toString(min));
			this.fen += MAX.replace("X", pieceTemp).replace("Z", Integer.toString(max));
		}else {
			this.fen += MIN.replace("X", piece).replace("Y", Integer.toString(min));
			this.fen += MAX.replace("X", piece).replace("Z", Integer.toString(max));
		}
		this.notOnBoard = this.notOnBoard.replace(piece, "");	
	}
	
	/**
	 * get fen
	 * @return fen
	 */
	public String getFen() {
		return fen;
	}

	/**
	 * set fen
	 * @param fen
	 */
	public void setFen(String fen) {
		this.fen = fen;
	}
	
	/**
	 * get list of white pieces
	 * @return piecesWhite
	 */
	public List<Piece> getPiecesWhite() {
		return piecesWhite;
	}

	/**
	 * set list of white pieces
	 * @param piecesWhite
	 */
	public void setPiecesWhite(List<Piece> piecesWhite) {
		this.piecesWhite = piecesWhite;
	}
	
	/**
	 * get list of black pieces
	 * @return piecesBlack
	 */
	public List<Piece> getPiecesBlack() {
		return piecesBlack;
	}
	
	/**
	 * set list of white pieces
	 * @param piecesBlack
	 */
	public void setPiecesBlack(List<Piece> piecesBlack) {
		this.piecesBlack = piecesBlack;
	}
	
	/**
	 * main method for testing
	 * @param args
	 */
	public static void main (String[] args){
		
		FenRegex reg = new FenRegex();
		
		reg.addPieceBlack(1, "rook");
		reg.addPieceWhite(2, "bishop");
		
		reg.createFen();
	}
	
	/**
	 * class for a piece
	 * counter: number of the piece
	 * name: name of the piece
	 */
	public class Piece{
		
		private int counter;
		private String name;
		public Piece(int counter, String name) {
			this.counter = counter;
			this.name = name;
		}
		public int getCounter() {
			return counter;
		}
		public void setCounter(int counter) {
			this.counter = counter;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}		
	}	
}