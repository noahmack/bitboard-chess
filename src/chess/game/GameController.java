package chess.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import chess.graphics.PrintGraphics;

public class GameController {

	
	
	public static void main(String[] args) {

		Board game = new Board();
		
		List<AtomicLong> bitboards = game.getBitBoards();

		PrintGraphics.printBoard(bitboards);
		
		String userInput = "";
		Scanner scnr = new Scanner(System.in);
		
		while(!userInput.equals("end")) {
			
			System.out.println((game.getMoveNum() % 2 == 0? "White's" : "Black's") + " Move.");
			System.out.println("Enter move:");
			userInput = scnr.nextLine();
			if(userInput.equals("end")) continue;
			
			
			game.move(userInput, bitboards);
			PrintGraphics.printBoard(bitboards);
			System.out.println();
			
		}
		
		
		
		
		
		

	}
}
