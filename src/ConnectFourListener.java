/**
 * <code>Connect4Listener</code> defines the basic actions of a connect 4 game.
 * 
 * @author Jesper Kristensen
 */
public interface ConnectFourListener {
	public void quit();
	
	/**
	 * Tells the listener to reset the game to the starting position.
	 */
	public void newGame();
	
	/**
	 * Tells the listener to undo the last played move.
	 */
	public void undo();

	/**
	 * Informs the listener to update its state according to the played move.
	 * @param column a number between 0 and 6 indicating where the piece was put.
	 */
	public void put(int column);
}