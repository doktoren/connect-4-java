import java.util.LinkedList;

/**
 * This class provides a simple text interface to the connect 4 engine.
 * 
 * @author Jesper Kristensen
 * @see ComputerPlayer
 */
public class ConnectFour {
	
	//private boolean normalBoard = true;

	private boolean p = true;
	
	private ConnectFourRules cfr;
	private LinkedList<ConnectFourListener> listeners;
	
	public ConnectFour(ConnectFourRules cfr) {
		this.cfr = cfr;
		listeners = new LinkedList<ConnectFourListener>();
	}

	public void addListener(ConnectFourListener cfl) {
		if (!listeners.contains(cfl))
			listeners.add(cfl);
	}
	
	public void removeListener(ConnectFourListener cfl) {
		listeners.remove(cfl);
	}
	
	public void newGame() {
		if (p) System.out.println("new game");
		
		for (int i=0; i<listeners.size(); i++)
			listeners.get(i).newGame();
	}
	
	/**
	 * Informs the listener to update its state according to the played move.
	 * @param column a number between 0 and 6 indicating where the piece was put.
	 */
	public boolean put(int column) {
		if (p) System.out.println("put " + column + "(num listeners = " + listeners.size() + ")");
		
		if (getRules().getGameVariant() != 3) {
			System.out.println("Column " + (column+1) + " played.");
		} else {
			System.out.println("Column " + ((column&3)+1) + "-" + (char)('A' + (column>>2)) + " played.");
		}
		
		switch (column) {
		case -3:
			quit();
			break;
		case -2:
			newGame();
			break;
		case -1:
			undo();
			break;
		default:
		{
			if (!cfr.canPut(column)) 
				return false;
			for (int i=0; i<listeners.size(); i++)
				listeners.get(i).put(column);
			return true;
		}
		}
		return true;
	}
	
	/**
	 * Tells the listener to undo the last played move.
	 */
	public boolean undo() {
		if (!cfr.canUndo())
			return false;
		
		if (p) System.out.println("undo");
		
		for (int i=0; i<listeners.size(); i++)
			listeners.get(i).undo();
		return true;
	}
	
	/**
	 * Tells the listener to undo the last played move.
	 */
	public void quit() {
		if (p) System.out.println("quit");
		
		for (int i=0; i<listeners.size(); i++)
			listeners.get(i).quit();
	}
	
	public ConnectFourRules getRules() {
		return cfr;
	}
}
