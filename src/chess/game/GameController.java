package chess.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import chess.graphics.PrintGraphics;

public class GameController {

	
	
	public static void main(String[] args) {

		Board game = new Board();
		
		List<AtomicLong> bitboards = game.getBitBoards();
		
		for(AtomicLong l : bitboards) {
			PrintGraphics.printBitBoard(l);
		}

		PrintGraphics.printBoard(bitboards);
		
		game.move("e2e4", bitboards);
		
		System.out.println();
		
		PrintGraphics.printBoard(bitboards);
		
		
		
		
		
		

	}
}
