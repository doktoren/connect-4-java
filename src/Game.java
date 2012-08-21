
public class Game {
	
	public Game() {
		board = new byte[42];
		moves = new int[42];
		c = new ComputerPlayer();
	}
	
	public void newGame() {
		for (int i=0; i<42; i++)
			board[i] = ' ';
		starting_player_turn = true;
		num_moves = 0;
		
		c.newGame();
	}
	
	public void print() {
	    System.out.println();
	    //c.print();
	    for (int j=6; j>=1; j--){
	    	System.out.print(j+" | ");
	    	for (int i=1; i<8; i++)
	    		System.out.print((char)board[7*(j-1) + (i-1)] + " ");
	    	System.out.println("|");
	    }
	    System.out.println("  \\---------------/");
	    System.out.println("    1 2 3 4 5 6 7");
	    System.out.print("Moves: "); c.print_moves(); System.out.println();
	}
	
	public boolean put(int column) {
		if (board[7*5+column] != ' ') return false;
		if (c.isGameOver()) return false;
		
		c.put(column);
		
		moves[num_moves++] = column;
		
		for (int i=0; ; i++)
			if (board[7*i+column] == ' ') {
				board[7*i+column] = (byte)(starting_player_turn ? 'X' : 'O');
				starting_player_turn = !starting_player_turn;
				break;
			}
		
		return true;
	}
	
	public boolean undo() {
		if (num_moves==0) return false;
		
		c.undo();
		
		int column = moves[--num_moves];
		
		for (int i=5; ; i--)
			if (board[7*i+column] != ' ') {
				board[7*i+column] = ' ';
				starting_player_turn = !starting_player_turn;
				break;
			}
		
		return true;
	}
	
	// calculateMove stops thinking when either thinkDepth is reached
	// or maxPositions has been evaluated. The move is not executed.
	public int calculateMove(long allowed_computing) {
		if (c.isGameOver()) return 0;
		return c.calculateMove(allowed_computing);
	}
	
	public boolean gameOver() {
		return c.isGameOver();
	}
	
	// ' ' is an empty square
	// 'X' is the starting player
	// 'O' is the other player
	// The squares are numbered left to right starting with the lowest line.
	private byte[] board;
	
	private int[] moves;
	private int num_moves;
	
	private boolean starting_player_turn;
	
	private ComputerPlayer c;
}
