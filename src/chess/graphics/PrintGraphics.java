package chess.graphics;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import chess.util.Constants;

public class PrintGraphics {

	/*
	 * 	My goal for this class is to create some methods that will print formatted data to the console. These
	 * 	methods will likely only be used for debugging purposes, for example not having to load a whole GUI to
	 *  depict the board as a whole.
	 */
	
	
	/**
	 * This method prints a depiction of an 8x8 chess board given the list of bitboards that
	 * represent the pieces on the board.
	 * @param bitboards
	 */
	public static void printBoard(List<AtomicLong> bitboards) {
		
		// first we need a way to represent the board, I will choose a char array. This will
		// allow us to modify individual characters as opposed to a String which is immutable
		char[] board = new char[64];

		// now that we have the baseline laid out, we can go through each bitboard and insert a 
		// representing character for each piece.
		char[] pieceChars = {'P', 'R', 'N', 'B', 'Q', 'K', 'p', 'r', 'n', 'b', 'q', 'k'};

		for(int i = Constants.WHITE_PAWNS; i <= Constants.BLACK_KING; i++) {
			AtomicLong bitboard = bitboards.get(i);
			for(int j = 0; j < 64; j++) {
				long compare = 1L << (63 - j);
				if((bitboard.get() & compare) != 0) {
					board[j] = pieceChars[i];
				}
			}
		}
		
		// now, we can print out our board with 8 chars on each line.
		for(int i = 0; i < 64; i++) {
			if(board[i] == 0) System.out.print("- "); 
			else System.out.print(board[i] + " ");
			if((i + 1) % 8 == 0) System.out.println();
		}
		

	}
	
	
	public static void printBitBoard(AtomicLong l) {
		for(int i = 0; i < Long.numberOfLeadingZeros(l.get()); i++) {
			System.out.print('0');
		}
		System.out.println(Long.toBinaryString(l.get()));
		System.out.println();
	}


}
