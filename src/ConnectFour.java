import java.io.*;

public class ConnectFour {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		HashValueClass hvc = new HashValueClass();
		System.out.println();
		hvc.print();
		System.out.println();
		hvc.verifyHashing();
		*/

		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
		Game g = new Game();
		g.newGame();
		g.print();
		System.out.println("Press q/Q to quit, c/C for a computer move,");
		System.out.println("u/U to undo a move, n/N for new game or 1-7 to put in a column.");
		while (true) {
			char c = GetChar(keyboard);
			if (c=='q' || c=='Q') return;
			if (c=='n' || c=='N') {
				g.newGame();
				g.print();
			} else if (c=='u' || c=='U') {
				if (!g.undo()) {
					System.out.println("Undo what?! Stupid fuck face!");
				}
				g.print();
			} else if (c=='c' || c=='C') {
				if (!g.put(g.calculateMove(200000L))) {
					System.out.println("Computer couldn't play or played a filled column...");
				}
				g.print();
			} else if ('1'<=c  &&  c<='7') {
				if (!g.put(c-'1')) {
					System.out.println("The column is filled your stupid ass!");
				} else {
					g.print();
				}
			} else {
				System.out.println("Unrecognized input - what an idiot!");
			}
			if (g.gameOver()) {
				System.out.println("Game over!");
			}
		}
		
		
		/*
		int x = 42;
		int y = 24;
		System.out.println("x = " + x + ", y = " + y);
		x ^= y;
		y ^= x;
		x ^= y;
		System.out.println("x = " + x + ", y = " + y);
		*/
	}

	private static String GetString(BufferedReader keyboard){
		String s="";
		try {s=keyboard.readLine();}
		catch (IOException e){System.out.println("GetChar input error!");}
		return s;
	}
	
	private static char GetChar(BufferedReader keyboard){
		String s=GetString(keyboard);
		while (s.equals("")){
			System.out.print("-Input Error-"); 
			s=GetString(keyboard);      
		}
		return s.charAt(0);
	}
}
