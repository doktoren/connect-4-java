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
		
		{
			//GeneratePatterns gp = new GeneratePatterns();
			//gp.connectFour();
			//gp.connectSquare();
		}

		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
		
		Game g;
		System.out.println("Select game:");
		System.out.println("    0: Normal connect 4");
		System.out.println("    1: Connect 4 with wrap around and bounce (cpu plays perfect)");
		System.out.println("    2: Connect the corners of a square (cpu plays perfect)");
		{
			char c;
			do {
				c = GetChar(keyboard);
			} while (c<'0'  ||  '2'<c);
			g = new Game(c - '0');
		}
		
		g.newGame();
		g.print();
		System.out.println();
		System.out.println("Press q/Q to quit, c/C for a computer move,");
		System.out.println("u/U to undo a move, n/N for new game or 1-7 to put in a column.");
		// System.out.println("Pressing # will initiate an infinite self training program.");
		
		while (true) {
			char c = GetChar(keyboard);
			c = Character.toLowerCase(c);
			switch (c) {
			
				// Initiate self play.
			case '#':
				g.trainCPU();
				break;
				
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
			{	
				int move = g.calculateMove(MAX_EVALUATIONS);
				if (!g.put(move)) {
					System.out.println("Computer couldn't play or played a filled column...");
				} else {
					System.out.println("Computer plays column " + move);
				}
				g.print();
				break;
			}
				
				// Probe opening library
			case 'l':
				g.showLibraryInfo();
				break;
				
				// New game:
			case 'n':
				g.newGame();
				g.print();
				break;
				
				// Do a one ply probe in the opening library
			case 'p':
				for (int i=0; i<7; i++)
					if (g.put(i)) {
						System.out.print("Move " + (i+1) + ": ");
						g.showLibraryInfo();
						g.undo();
					}
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
