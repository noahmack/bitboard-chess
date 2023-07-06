package chess.game;

import java.util.ArrayList;
import java.util.List;

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
	
	
	private static long whitePawns = 0x000000000000FF00L;
	private static long whiteRooks = 0x0000000000000081L;
	private static long whiteKnights = 0x0000000000000042L;
	private static long whiteBishops = 0x0000000000000024L;
	private static long whiteQueens = 0x0000000000000010L;
	private static long whiteKing = 0x0000000000000008L;
	
	private static long blackPawns = 0x00FF000000000000L;
	private static long blackRooks = 0x8100000000000000L;
	private static long blackKnights = 0x4200000000000000L;
	private static long blackBishops = 0x2400000000000000L;
	private static long blackQueens = 0x1000000000000000L;
	private static long blackKing = 0x0800000000000000L;
	
	private static long whitePieces = whitePawns | whiteRooks | whiteKnights | whiteBishops | whiteQueens | whiteKing;
	private static long blackPieces = blackPawns | blackRooks | blackKnights | blackBishops | blackQueens | blackKing;
	private static long allPieces = whitePieces | blackPieces;

	public String move(String move, List<Long> bitboards) {
		
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
		
		if(move.charAt(0) == move.charAt(2)) {	// rankwise move
			// now, we need to determine what piece is moving. To do this, we can find the index of the starting square
			// and bitwise and it with each bitboard until the result is not equal to 0.
			
			// so, lets start by finding the index of the starting square. We can do this by multiplying the letter
			// index alphabet value by the rank number:
			
			int startIndex = Integer.parseInt("" + move.charAt(1)) * 8 - (move.charAt(0) - 97);
			
			
		}
		
		
		
		return null;
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

	public static void main(String[] args) {

		List<Long> bitboards = new ArrayList<>();
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
		
		List<Long> whiteBitBoards = new ArrayList<>();
		
		whiteBitBoards.add(whitePawns);
		whiteBitBoards.add(whiteRooks);
		whiteBitBoards.add(whiteKnights);
		whiteBitBoards.add(whiteBishops);
		whiteBitBoards.add(whiteQueens);
		whiteBitBoards.add(whiteKing);

		for(Long l : bitboards) {
			for(int i = 0; i < Long.numberOfLeadingZeros((long)l); i++) {
				if(i % 8 == 0) System.out.println();
				System.out.print('0');
			}
			System.out.println(Long.toBinaryString((long)l));
			System.out.println();
		}

		String move = "f7e4";
		
		int startIndex = Integer.parseInt("" + move.charAt(1)) * 8 - (move.charAt(0) - 97) - 1;
		System.out.println(startIndex);
		
		
		
		
		// end of main
	}





}
