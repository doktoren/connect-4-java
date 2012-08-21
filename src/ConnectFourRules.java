
public interface ConnectFourRules {
	public int getActivePlayer();
	public int getMovesPlayed();
	
	public boolean canPut(int column);
	public boolean canUndo();
	
	public boolean isGameOver();
	public int getGameVariant();
}
