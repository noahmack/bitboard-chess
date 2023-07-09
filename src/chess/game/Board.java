package chess.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import chess.graphics.PrintGraphics;

public class Board {

//		-------------------------------------------------------------------------------------------------------
//	  	|										USEFUL FIGURES:												  |
//	 	-------------------------------------------------------------------------------------------------------
	 
	/*
	 * 		Board as indices of bitboard:
	 * 
	 * 		8.	63	62	61	60	59	58	57	56
	 * 		7.	55	54	53	52	51	50	49	48
	 * 		6.	47	46	45	44	43	42	41	40
	 * 		5.	39	38	37	36	35	34	33	32
	 * 		4.	31	30	29	28	27	26	25	24
	 * 		3.	23	22	21	20	19	18	17	16
	 * 		2.	15	14	13	12	11	10	9	8
	 * 		1.	7	6	5	4	3	2	1	0
	 * 			a	b	c	d	e	f	g	h
	 * 
	 */
	
	/*
	 * 	AtomicLong vs. Long vs. long
	 * 	----------------------------
	 * 	I wanted a way to hold a long value in an Object and be able to update the values
	 * 	as moves are made in the game. long doesn't allow this because it is a primitive type.
	 * 	While Long (java.util.Long) is an Object that holds a long value, the long value is
	 * 	immutable and can't be updated. This is why I chose to use the AtomicLong class, which
	 * 	holds a long value as a field which can be modified and updated within the same object
	 * 	reference.
	 */
	private AtomicLong whitePawns;
	private AtomicLong whiteRooks;
	private AtomicLong whiteKnights;
	private AtomicLong whiteBishops;
	private AtomicLong whiteQueens;
	private AtomicLong whiteKing;
	
	private AtomicLong blackPawns;
	private AtomicLong blackRooks;
	private AtomicLong blackKnights;
	private AtomicLong blackBishops;
	private AtomicLong blackQueens;
	private AtomicLong blackKing;
	
	private AtomicLong whitePieces;
	private AtomicLong blackPieces;
	private AtomicLong allPieces;
	
	private List<AtomicLong> bitboards;
	
	// default constructor: generates a board with standard starting position
	public Board() {
		whitePawns = new AtomicLong(0x000000000000FF00L);
		whiteRooks = new AtomicLong(0x0000000000000081L);
		whiteKnights = new AtomicLong(0x0000000000000042L);
		whiteBishops = new AtomicLong(0x0000000000000024L);
		whiteQueens = new AtomicLong(0x0000000000000010L);
		whiteKing = new AtomicLong(0x0000000000000008L);
		
		blackPawns = new AtomicLong(0x00FF000000000000L);
		blackRooks = new AtomicLong(0x8100000000000000L);
		blackKnights = new AtomicLong(0x4200000000000000L);
		blackBishops = new AtomicLong(0x2400000000000000L);
		blackQueens = new AtomicLong(0x1000000000000000L);
		blackKing = new AtomicLong(0x0800000000000000L);
		
		whitePieces = new AtomicLong(whitePawns.get() | whiteRooks.get() | whiteKnights.get() | whiteBishops.get() | whiteQueens.get() | whiteKing.get());
		blackPieces = new AtomicLong(blackPawns.get() | blackRooks.get() | blackKnights.get() | blackBishops.get() | blackQueens.get() | blackKing.get());
		allPieces = new AtomicLong(whitePieces.get() | blackPieces.get());
		
		bitboards = new ArrayList<>();
		bitboards.add(whitePawns);
		bitboards.add(whiteRooks);
		bitboards.add(whiteKnights);
		bitboards.add(whiteBishops);
		bitboards.add(whiteQueens);
		bitboards.add(whiteKing);

		bitboards.add(blackPawns);
		bitboards.add(blackRooks);
		bitboards.add(blackKnights);
		bitboards.add(blackBishops);
		bitboards.add(blackQueens);
		bitboards.add(blackKing);

		bitboards.add(whitePieces);
		bitboards.add(blackPieces);
		bitboards.add(allPieces);
	}
	
	public void move(String move, List<AtomicLong> bitboards) {
		
		/*
		 * NOTES ABOUT MOVEMENT:
		 * 
		 * 		- moving up a rank = 8 bit shift to the left, down a rank = 8 bit shift to the right
		 * 		- moving files is a simple shift left/right
		 * 		- To move then, we can create a bitboard of the piece to be moved, OR it with
		 * 		  itself shifted to the corresponding move, then XOR it with the corresponding piece bitboard
		 */
		
		// let's start with pawns, and let's not worry about parsing a move from a string. For now, we will just
		// assume that our input string will be in the correct format.
		
		// example move: e2e4 aka kings pawn
		// the first square's file (e) is the same as the second square's file (e) so we know this is a rankwise move
		// to determine the number of ranks, we can subtract the second square's rank from the first square's rank
		// when a move changes files as well as ranks, we can shift by 1 for each file instead of by 8
		
			// another method will make sure that the move is legal before it is passed to this method, so we
			// just have to manipulate the bitboards with the given move
			
			// first we need to find out which piece type is moving
			
			// so, lets start by finding the index of the starting square. We can do this by multiplying the letter
			// index alphabet value by the rank number:
			
			int startIndex = Integer.parseInt("" + move.charAt(1)) * 8 - (move.charAt(0) - 97) - 1;
			
			// now that we have our starting index, let's create a long with a 1 at that index
			
			long startingPiece = 1L << startIndex;
			
			// now to find the piece type we can loop through the bitboards and see which type it is:
			
			AtomicLong startingPieceBitboard = null;
			String binaryStarting = Long.toBinaryString(startingPiece);
			
			for(AtomicLong bitboard : bitboards) {
				if((bitboard.get() & startingPiece) != 0) {
					startingPieceBitboard = bitboard;
					break;
				}
			}
			// now, we can create an ending piece long by shifting the right amount of bits for the given move
			
			int deltaRank = move.charAt(3) - move.charAt(1);
			int deltaFile = move.charAt(2) - move.charAt(0);
			
			// '>>>' vs '>>': '>>' is a signed shift, so if the most significant bit of the long is
			// 1, each bit that gets shifted in will be an additional 1. We want 0s to be shifted in,
			// so we use '>>>' when shifting right. Shifting left does not matter, in fact '<<<' is 
			// not valid syntax, because 0s are always concatenated when shifting left
			
			long endingPiece = deltaRank >= 0 ? startingPiece << (8 * deltaRank) : startingPiece >>> (-8 * deltaRank);
			endingPiece = deltaFile >= 0 ? endingPiece >>> deltaFile : endingPiece << (-1 * deltaFile);
			
			// next, we should make a move bitboard that can be used to execute the move:
			
			long moveBitboard = startingPiece | endingPiece;
			
			
			/*
			 * example: startingPieceBitboard = 0b0000100
			 * 					 moveBitboard = 0b0100100
			 * 					   XOR result = 0b0100000
			 */
			
			// now, we aren't quite finished yet. What if the move results in a capture?
			// we must also update the bitboard of the captured piece
			
			AtomicLong endingPieceBitboard = null;
			
			for(AtomicLong bitboard : bitboards) {
				if((bitboard.get() & endingPiece) != 0) {
					endingPieceBitboard = bitboard;
					break;
				}
			}
			
			// if the move results in a capture, the endingPieceBitboard will not be null.
			// in order to remove the captured piece from its bitboard, we can use a bitwise
			// and with the complement of the endPiece position bitboard
			
			if(endingPieceBitboard != null) {
				endingPieceBitboard.set(~endingPiece & endingPieceBitboard.get());
			}
			
			// now we can finally use the moveBitboard that we made earlier. We do this
			// now so that the code doesn't interpret the moved piece as a piece to be
			// captured.
			
			startingPieceBitboard.set(moveBitboard ^ startingPieceBitboard.get());
			
			// the last thing we need to do to complete the move is to update the encompassing
			// bitboards. This can be done with the bitwise OR operation
			
			whitePieces.set(whitePawns.get() | whiteRooks.get() | whiteKnights.get() | whiteBishops.get() | whiteQueens.get() | whiteKing.get());
			blackPieces.set(blackPawns.get() | blackRooks.get() | blackKnights.get() | blackBishops.get() | blackQueens.get() | blackKing.get());
			allPieces.set(whitePieces.get() | blackPieces.get());
		
			
			// POSSIBLE IMPROVEMENTS:
			// 1. instead of using AtomicLong we could technically do an array of primitive longs, but I think that
			// AtomicLongs are easier and we should just stick with them until(if) it becomes a problem
	}
	
	
	// commented out bc dont need this yet
//	private String parseMove(String move) {
//		int strlen = move.length();
//		
//		if(strlen < 3) return null;
//		else if(strlen > 4) return null;
//		
//		char first = move.charAt(0);
//		if("abcdefgh".indexOf(first) < 0 && "RKNQB".indexOf(first) < 0) return null;
//		
//	}

	

	public List<AtomicLong>	getBitBoards() {
		return bitboards;
	}



}
