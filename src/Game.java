
public class Game {
	
	// game = 0: Normal connect 4
	// game = 1: Wrap & bounce
	// game = 2: Square
	public Game(int game) {
		board = new byte[7][6];
		moves = new int[42];
		c = new ComputerPlayer(game); 
	}
	
	public void newGame() {
		for (int column=0; column<7; column++)
			for (int row=0; row<6; row++)
				board[column][row] = ' ';
		startingPlayerTurn = true;
		numMoves = 0;
		
		c.newGame();
	}
	
	public void print() {
	    System.out.println();
	    //c.print();
	    for (int row=6; row>=1; row--){
	    	System.out.print(row+" | ");
	    	for (int column=1; column<8; column++)
	    		System.out.print((char)board[column-1][row-1] + " ");
	    	System.out.println("|");
	    }
	    System.out.println("  \\---------------/");
	    System.out.println("    1 2 3 4 5 6 7");
	    System.out.print("Moves: "); c.printMoves(); System.out.println();
	}
	
	public boolean put(int column) {
		if (board[column][5] != ' ') return false;
		if (c.isGameOver()) return false;
		
		c.put(column);
		
		moves[numMoves++] = column;
		
		for (int row=0; ; row++)
			if (board[column][row] == ' ') {
				board[column][row] = (byte)(startingPlayerTurn ? 'X' : 'O');
				startingPlayerTurn = !startingPlayerTurn;
				break;
			}
		
		return true;
	}
	
	public boolean undo() {
		if (numMoves==0) return false;
		
		c.undo();
		
		int column = moves[--numMoves];
		
		for (int row=5; ; row--)
			if (board[column][row] != ' ') {
				board[column][row] = ' ';
				startingPlayerTurn = !startingPlayerTurn;
				break;
			}
		
		return true;
	}
	
	// calculateMove stops thinking when either thinkDepth is reached
	// or maxPositions has been evaluated. The move is not executed.
	public int calculateMove(long allowedComputing) {
		if (c.isGameOver()) return 0;
		return c.calculateMove(allowedComputing);
	}
	
	public boolean gameOver() {
		return c.isGameOver();
	}
	
	public void trainCPU() {
		c.selfPlay();
	}
	
	public void showLibraryInfo() {
		c.showLibraryInfo();
	}
	
	
	// ' ' is an empty square
	// 'X' is the starting player
	// 'O' is the other player
	// The squares are numbered left to right starting with the lowest line.
	private byte[][] board;
	
	private int[] moves;
	private int numMoves;
	
	private boolean startingPlayerTurn;
	
	private ComputerPlayer c;
}
