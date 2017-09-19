package de.daug.semanticchess.Parser.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * creates a regex to find positions from fen
 */
public class FenRegex{
	
	List<Piece> piecesWhite = new ArrayList<Piece>();
	List<Piece> piecesBlack = new ArrayList<Piece>();
	
	private String fen = "^";
	
	private static String MIN = "(?=(.*X){Y})";
	private static String MAX = "(?!(.*X){Z})";
	private static String notOnBoard = "((?![QqRrBbNnPp]).)*$";
	
	/**
	 * constructor
	 */
	public FenRegex(){
	}
	
	/**
	 * add white pieces to the list
	 * @param counter: number of this piece
	 * @param piece: name of the piece
	 */
	public void addPieceWhite(int counter, String piece){
		Piece p = new Piece(counter, piece);
		piecesWhite.add(p);
	}
	
	/**
	 * add black pieces to the list
	 * @param counter: number of this piece
	 * @param piece: name of the piece
	 */
	public void addPieceBlack(int counter, String piece){
		Piece p = new Piece(counter, piece);
		piecesBlack.add(p);
	}
	
	/**
	 * create fen from the list of white and black pieces
	 */
	public void createFen(){
		boolean whitePawnIsCalled = false;
		boolean blackPawnIsCalled = false;
		
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
		
		// if there are specified numbers of pawns then remove pawns frome the list
		// of pieces that are not on the board
		if(!whitePawnIsCalled && !blackPawnIsCalled){
			FenRegex.notOnBoard = FenRegex.notOnBoard.replace("P", "");
			FenRegex.notOnBoard = FenRegex.notOnBoard.replace("p", "");	
		}
		
		if(piecesWhite.size() > 0){
			fen += FenRegex.notOnBoard;
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
		FenRegex.notOnBoard = FenRegex.notOnBoard.replace(piece, "");		
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
		reg.addPieceWhite(1, "queen");
		reg.addPieceBlack(1, "rook");
		reg.addPieceBlack(1, "pawn");
		
		reg.createFen();
		
		System.out.println(reg.getFen());
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