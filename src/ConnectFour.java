import java.io.*;

public class ConnectFour {

	private static final long MAX_EVALUATIONS = 1000000L;
	
	
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
			c = Character.toLowerCase(c);
			switch (c) {
			// Moves:
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
				if (!g.put(c - '1')) {
					System.out.println("The column is filled your stupid ass!");
				} else {
					g.print();
				}
				break;
				
				// Compute a move:
			case 'c':
				if (!g.put(g.calculateMove(MAX_EVALUATIONS))) {
					System.out.println("Computer couldn't play or played a filled column...");
				}
				g.print();
				break;
				
				// New game:
			case 'n':
				g.newGame();
				g.print();
				break;
				
				// Quit:
			case 'q':
				return;
				
				// Undo move:
			case 'u':
				if (!g.undo()) {
					System.out.println("Undo what?! Stupid fuck face!");
				}
				g.print();
				break;
				
			default:
				System.out.println("Unrecognized input - what an idiot!");
				break;
			}
			
			if (g.gameOver()) {
				System.out.println("Game over!");
			}
		}
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
