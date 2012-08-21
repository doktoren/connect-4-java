public interface ConnectFourPlayer {

	// -3 : quit
	// -2 : newGame
	// -1 : undo
	// [0, ..., numColumns[ : put in column
	public int requestMove();
}
