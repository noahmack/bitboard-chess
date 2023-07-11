package chess.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import chess.data.Constants;
import chess.data.ShortBitboard;
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
	
	// white piece bitboards
	private AtomicLong whitePawns;
	private AtomicLong whiteRooks;
	private AtomicLong whiteKnights;
	private AtomicLong whiteBishops;
	private AtomicLong whiteQueens;
	private AtomicLong whiteKing;

	// black piece bitboards
	private AtomicLong blackPawns;
	private AtomicLong blackRooks;
	private AtomicLong blackKnights;
	private AtomicLong blackBishops;
	private AtomicLong blackQueens;
	private AtomicLong blackKing;
	
	// multi-piece bitboards
	private AtomicLong whitePieces;
	private AtomicLong blackPieces;
	private AtomicLong allPieces;

	// special bitboards
	private ShortBitboard pawnsHaveMoved;	// [15..8] = black pawns, [7..0] = white pawns
	
	private List<AtomicLong> bitboards;

	private int moveNum;

	// default constructor: generates a board with standard starting position
	public Board() {

		moveNum = 0;

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
		
		pawnsHaveMoved = new ShortBitboard();

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

	public boolean isLegal(String move, List<AtomicLong> bitboards, AtomicLong startingPieceBitboard, AtomicLong endingPieceBitboard, boolean isCapture) {

		// When determining whether or not a move is legal in a game of chess, there are many, many things that need
		// to be checked. Things like if pieces are in the way, whether the move puts your king in check, or even
		// just moving a piece in a way that it is not allowed to be moved. Before we check these things though, we
		// need to determine what type of piece is moving. For this, we can use the indexOf method on bitboards
		// to find the position of the startingPieceBitboard

		int pieceType = bitboards.indexOf(startingPieceBitboard);

		// There's a lot of work ahead of us, but we have to choose somewhere to start, so let's just start
		// with the simple pawn.

		// oh, and before I forget, let's make a boolean to determine if the moving piece is white or black.
		
		long startSquare = getStartingSquareBitboard(move);
		boolean isWhite = (startSquare & whitePieces.get()) == startSquare? true : false;

		// white moves on even moveNum, black moves on odd

		if(isWhite && moveNum % 2 != 0) return false;
		if(!isWhite && moveNum % 2 == 0) return false;

		// our first legal check is done! Only everything left to go...
		// let's move on with the pawns.

		if(pieceType == Constants.WHITE_PAWNS || pieceType == Constants.BLACK_PAWNS) {
			// The first thing we should do for the pawn is find a way to determine whether this is the pawn's first
			// move or not. This determines if the pawn can move 2 squares or just one square.
			// This is easy, because we can just check if the rank of the first square is a 2 or not
			// since that is where pawns start on the board (rank 7 if black pawn)

			boolean firstMove = (isWhite && move.charAt(1) == '2' || !isWhite && move.charAt(1) == '7')? true : false;

			// next, an easy check is if the ending file of the move is more than 1 away from the starting file.
			// a pawn can never move in this way, so if it is true than the move is illegal.

			if(Math.abs(move.charAt(0) - move.charAt(2)) > 1) return false;

			// going along with that rule, we can also check if the file changes and the move isn't a capture,
			// because pawns can only change files if the move is a capture.

			if(move.charAt(0) != move.charAt(2) && !isCapture) return false;

			// we can also check the inverse of this, to ensure that the pawn cannot make a capture on the same file

			if(move.charAt(0) == move.charAt(2) && isCapture) return false;

			// next, we can use our firstMove boolean to check if the pawn tries to move 2 squares later
			// than its first move

			if(Math.abs(move.charAt(1) - move.charAt(3)) == 2 && !firstMove) return false;

			// while we are at it, let's check to make sure that pawns are never moving more than 2 squares

			if(Math.abs(move.charAt(1) - move.charAt(3)) > 2) return false;

			// the last thing (until en passant) that we need to check is whether or not the pawn is trying to
			// move to a square that is already occupied by its own piece

			if(isCapture) {
				if(bitboards.indexOf(endingPieceBitboard) <= Constants.WHITE_KING && isWhite) {
					return false;
				} else if(bitboards.indexOf(endingPieceBitboard) > Constants.WHITE_KING && !isWhite) {
					return false;
				}
			}
			
			// en passant time.
			// for en passant, we should be able to check if the pawn has an opposite color pawn immediately
			// to the left or right of it. If so, we can then check if the moving pawn is in the rank that
			// allows for en passant. The last thing we need to know is where the pawn was on it's previous move.
			// unless...what if we make a new bitboard? A pawnsHaveMoved bitboard! Using a short, we can create
			// a bitboard with a width of 16 and update it with pawns that have moved already! Then, we can check
			// if the pawn has previously moved or not and we can accurately determine whether en passant is a legal
			// move.
			// Ok, turns out AtomicShort doesn't exist alongside AtomicLong for some reason. This should not be a big
			// deal, because we can simply create our own ShortBitboard class.

			// TODO: en passant
			
			
			
			// Now we know that the move is definitely legal, but there's still some bookkeeping left to do:
			
			// let's set our pawnsMoved bitboard to an accurate state:
			int pawnMoveIndex = 0;
			if(move.charAt(1) == '2' && isWhite || move.charAt(1) == '7' && !isWhite) {	
				pawnMoveIndex = 1 << (7 - (move.charAt(0) - 97) + (isWhite? 0 : 8));
				pawnsHaveMoved.setBitboard((short)(pawnsHaveMoved.get() | pawnMoveIndex));
			}
			
			
			return true;
		}



		
		


		return false;
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

		// so, lets start by finding the index of the starting square. A helper method
		// was written because this function will be useful in multiple places.

		long startingPiece = getStartingSquareBitboard(move);

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
		boolean isCapture = false;

		for(AtomicLong bitboard : bitboards) {
			if((bitboard.get() & endingPiece) != 0) {
				endingPieceBitboard = bitboard;
				break;
			}
		}

		// if the move results in a capture, the endingPieceBitboard will not be null.

		if(endingPieceBitboard != null) {
			isCapture = true;
		}

		// now that we have all the data about the move though, we need to check if the
		// move is legal before we actually manipulate any bitboards:

		if(!isLegal(move, bitboards, startingPieceBitboard, endingPieceBitboard, isCapture)) {
			System.out.println("Move " + move + " is illegal!");
			return;
		}

		// if we make it to here, we know that the move is legal. So, we can pick up where we
		// left off.

		// in order to remove the captured piece from its bitboard, we can use a bitwise
		// and with the complement of the endPiece position bitboard

		if(endingPieceBitboard != null) {
			endingPieceBitboard.set(~endingPiece & endingPieceBitboard.get());
		}

		// now we can finally use the moveBitboard that we made earlier. We do this
		// now so that the code doesn't interpret the moved piece as a piece to be
		// captured.

		startingPieceBitboard.set(moveBitboard ^ startingPieceBitboard.get());

		// now that our piece has officially moved, we can increment the move counter
		moveNum++;

		// the last things we need to do to complete the move is to update the encompassing
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

	/*
	 * -------------------------------------------------------------------------------
	 * |							HELPER METHODS									 |
	 * -------------------------------------------------------------------------------
	 */

	private static long getStartingSquareBitboard(String move) {
		// To find the starting square bitboard, we can start by multiplying the letter
		// index alphabet value by the rank number:

		int startIndex = Integer.parseInt("" + move.charAt(1)) * 8 - (move.charAt(0) - 97) - 1;

		// now that we have our starting index, let's create a long with a 1 at that index

		long startingPiece = 1L << startIndex;
		return startingPiece;
	}
	
	/*
	 * -------------------------------------------------------------------------------
	 * |							GETTERS AND SETTERS								 |
	 * -------------------------------------------------------------------------------
	 */
	
	public List<AtomicLong>	getBitBoards() {
		return bitboards;
	}
	
	public int getMoveNum() {
		return moveNum;
	}

}
