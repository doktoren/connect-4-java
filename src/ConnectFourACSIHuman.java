import java.io.IOException;

public class ConnectFourACSIHuman implements ConnectFourListener {
	
	private ComputerPlayer cp;
	private ConnectFour cf;

	private ConnectFourPlayer[] players;
	
	public ConnectFourACSIHuman(ComputerPlayer cp, ConnectFour cf) {
		this.cp = cp;
		this.cf = cf;
		players = new ConnectFourPlayer[2];
		players[0] = players[1] = null;
	}
	
	public void newGame() {
		cp.print();
	}
	
	public void quit() {
		// ignore
	}
	
	/**
	 * Informs the listener to update its state according to the played move.
	 * @param column a number between 0 and 6 indicating where the piece was put.
	 */
	public void put(int column) {
		cp.print();
	}
	
	/**
	 * Tells the listener to undo the last played move.
	 */
	public void undo() {
		cp.print();		
	}
	
	private void removePlayers() {
		if (players[0] != null  &&  players[0].getClass() == ConnectFourFileCommunicatePlayer.class) {
			cf.removeListener((ConnectFourFileCommunicatePlayer)players[0]);
		} else if (players[1] != null  &&  players[1].getClass() == ConnectFourFileCommunicatePlayer.class) {
			cf.removeListener((ConnectFourFileCommunicatePlayer)players[1]);
		}
		
		players[0] = players[1] = null;
	}
	
	public void takeControl() {
		showKeyMapping();
		cf.newGame();
		
		while (true) {
			
			if (players[cf.getRules().getActivePlayer()] != null) {
				if (cf.getRules().isGameOver()) {
					removePlayers();
					break;
				} else {
					// Request a move
					try
					{
						int move = players[cf.getRules().getActivePlayer()].requestMove();
						cf.put(move);
						break;
					}
					catch (RuntimeException r)
					{
						System.out.println("An illegal move was played.");
						return;
					}
				}
			}
			

			String s = TextInput.getString();
			if (s.length() == 0)
				continue;
			
			if (s.compareTo("??") == 0) {
				showAdvancedKeyMapping();
				continue;
			}
			
			char c0 = Character.toLowerCase(s.charAt(0));
			char c1 = s.length() > 1 ? Character.toLowerCase(s.charAt(1)) : ' ';
			char c2 = s.length() > 2 ? Character.toLowerCase(s.charAt(1)) : ' ';
			
			if (s.length() == 3  &&  c0 == 'n'  &&  ('1'<=c1 && c1<='2')  &&  ('1'<=c2 && c2<='2')) {
				
				if (players[0] != null  &&  players[0].getClass() == ConnectFourFileCommunicatePlayer.class) {
					cf.removeListener((ConnectFourFileCommunicatePlayer)players[0]);
				} else if (players[1] != null  &&  players[1].getClass() == ConnectFourFileCommunicatePlayer.class) {
					cf.removeListener((ConnectFourFileCommunicatePlayer)players[1]);
				}

				for (int p=0; p<2; p++) {
					char c = p==0 ? c1 : c2;
					switch (c) {
					case '0':
						players[p] = null;
						break;
					case '1':
						players[p] = cp;
						break;
					case '2':
						try {
							ConnectFourFileCommunicatePlayer cffcp = new ConnectFourFileCommunicatePlayer(cf, "receive.txt", "send.txt");
							players[p] = cffcp;
							cf.addListener(cffcp);
						}
						catch (IOException e) {
							System.out.println(e + " - player " + (p==0 ? 'O' : 'X') + " set to human instead.");
							players[p] = null;
						}
						break;
					}
				}
			}

			if (s.length() > 2) {
				System.out.println("Unrecognized input!");
				break;
			}
			
			
			switch (c0) {
			
				// Initiate self play.
			case '#':
				cp.selfPlay();
				break;
				
				// Initiate self play.
			case '?':
				showKeyMapping();
				break;
				
				// Moves:
			case '1':
			case '2':
			case '3':
			case '4':
				if (c1 != ' '  &&  cf.getRules().getGameVariant() == 3) {
					// Connect 4 i 3D
					if (c1 < 'a'  ||  'd' < c1) {
						System.out.println("Well, gah!");
						break;
					}
					if (!cf.put(4*(c1 - 'a') + (c0 - '1'))) {
						System.out.println("You are incredible stupid!");
					}
					break;
				}
			case '5':
			case '6':
			case '7':
				if (cf.getRules().getGameVariant() != 3) {
					if (!cf.put(c0 - '1')) {
						System.out.println("You are incredible stupid!");
					}
				} else {
					System.out.println("Invalid move description (what an idiot...)");
				}
				break;
				
				// Compute a move:
			case 'c':
			{	
				try
				{
					int move = cp.requestMove();
					cf.put(move);
				}
				catch (RuntimeException r)
				{
					System.out.println("Computer couldn't play or played a filled column...");
				}
				break;
			}
			
			case 'd':
				cp.print();
				break;
			
			// Probe opening library
			case 'l':
				cp.showLibraryInfo();
				break;
				
				// New game:
			case 'n':
				removePlayers();
				
				cf.newGame();
				break;
				
				// Do a one ply probe in the opening library
			case 'p':
				for (int i=0; i<(cp.getGameVariant() != 3 ? 7 : 16); i++)
					try {
						cp.put(i);
						System.out.print("Move " + (i+1) + ": ");
						cp.showLibraryInfo();
						cp.undo();
					} catch (RuntimeException r) {}
				break;
				
				// Quit:
			case 'q':
				return;
				
				// Call (current) test function
			case 't':
				cp.test();
				break;
				
				// Undo move:
			case 'u':
				if (!cf.undo()) {
					System.out.println("Undo what?! Stupid fuck face!");
				}
				break;
				
			default:
				System.out.println("Unrecognized input - what an idiot!");
				break;
			}
			
			if (cf.getRules().isGameOver())
				System.out.println("Game over!");
		}
	}
	
	public void showKeyMapping() {
		System.out.println();
		System.out.println("Supported players:");
		System.out.println("    0 : Human.");
		System.out.println("    1 : Jesper excellent engine.");
		System.out.println("    2 : Communicate with other engine using files.");
		System.out.println("Options:");
		System.out.println("    ?  : Shows this information.");
		System.out.println("    n/N: Starts a new game.");
		System.out.println("    nxy/Nxy: Eg. n12: lets this engine start against other engine.");
		System.out.println("    d/D: (Re)displays the board.");
		System.out.println("    c/C: Computer makes a move.");
		if (cp.getGameVariant() != 3) {
			System.out.println("    1,...,7 : Play column (in a 7x6 dimensioned game).");
		} else {
			System.out.println("    1a,...,4d : Play column (in a 4x4x4 dimensioned game).");
		}
		System.out.println("    u/U: Undoes a move.");
		System.out.println("    q/Q: Quit.");
		System.out.println("    ?? : Further options.");
	}
	
	public void showAdvancedKeyMapping() {
		System.out.println("    l/L,p/P: Probe opening library.");
		System.out.println("    t/T: Call a ComputerPlayer method that does some stuff :-)");
		System.out.println("    #  : Improve the computer player by training it against itself.");
	}
}
