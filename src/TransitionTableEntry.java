/**
 * This class encapsulates the information used by the search algorithm in {@link ComputerPlayer} about each position.
 * One exception however is that only 32 bits of the 49 bits defining a position is stored -
 * the last 17 bits are implicitly given by the low 17 bits of its index in an array. 
 * 
 * @author Jesper Kristensen
 * @see ComputerPlayer
 */
public class TransitionTableEntry {

	// Evaluation types:
	static final byte UNDEFINED = 0;
	static final byte EXACT = 1;
	static final byte LOWER = 2;
	static final byte UPPER = 3;
	
	static final byte INFINITE_DEPTH = 64;
	
	/**
	 * Constructs an empty <code>TransitionTableEntry</code>
	 */
	public TransitionTableEntry() {
		key = 0;
		value = 0;
		evaluationType = 0;
		searchDepth = -1;
		bestMove = -1;
		forcedMove = false;
	}
	
	/**
	 * Clears the contents.
	 */
	public void clear() {
		key = 0;
		value = 0;
		evaluationType = 0;
		searchDepth = -1;
		bestMove = -1;
		forcedMove = false;
	}
	
	/**
	 * Constructs a new entry identical to an existing one.
	 *  
	 * @param entry the entry to copy from.
	 */
	public TransitionTableEntry(TransitionTableEntry entry) {
		this.key = entry.key;
		this.value = entry.value;
		this.evaluationType = entry.evaluationType;
		this.searchDepth = entry.searchDepth;
		this.bestMove = entry.bestMove;
		this.forcedMove = entry.forcedMove;
	}
	
	/*
	public TransitionTableEntry(short value, byte evaluationType, byte searchDepth) {
		this.key = 0;
		this.value = value;
		this.evaluationType = evaluationType;
		this.searchDepth = searchDepth;
		this.bestMove = -1;
	}
	*/
	
	/**
	 * Constructs a new entry accordingly.
	 */
	public TransitionTableEntry(long key, short value, byte evaluationType, byte searchDepth, byte bestMove, boolean forcedMove) {
		//if (-32700 < value  &&  value < 32700  &&  value != 0  &&  searchDepth >= 64)
		//	System.out.println("" + 1/0);
		this.key = key;
		this.value = value;
		this.evaluationType = evaluationType;
		this.searchDepth = searchDepth;
		this.bestMove = bestMove;
		this.forcedMove = forcedMove;
	}
	
	/**
	 * Copies the contents of the applied entry to this entry.
	 *  
	 * @param entry the entry to copy from.
	 */
	public void set(TransitionTableEntry entry) {
		this.key = entry.key;
		this.value = entry.value;
		this.evaluationType = entry.evaluationType;
		this.searchDepth = entry.searchDepth;
		this.bestMove = entry.bestMove;
		this.forcedMove = entry.forcedMove;
	}
	
	/**
	 * Returns whether this entry represents a position.
	 * 
	 * @return true if this entry represents a position.
	 */
	public boolean isDefined() { return evaluationType != 0; }
	
	/**
	 * Examines whether the game theoretical value for this position has been found.
	 * Notice that this includes the exact depth to the fastest win if possible.
	 * 
	 * @return true if the game theoretical value is known.
	 */
	public boolean isGameTheoreticalValue() {
		return searchDepth >= INFINITE_DEPTH;
	}
	
	/**
	 * Tells the entry that its value is the correct game theoretical value.
	 */
	public void setGameTheoreticalValue() {
		// if (-32700 < value  &&  value < 32700  &&  value != 0) System.out.println("" + 1/0);
		searchDepth = INFINITE_DEPTH;
		evaluationType = EXACT;
	}
	
	/**
	 * Constructs a new TransitionTableEntry...
	 */
	public TransitionTableEntry stepOneMoveBack() {
		TransitionTableEntry newEntry = new TransitionTableEntry(this);
		newEntry.value = (short)-newEntry.value;
		++newEntry.searchDepth;
		//if (newEntry.searchDepth == 64)	System.out.println("" + 1/0);
		// Swap between LOWER and UPPER evaluation types:
		if ((newEntry.evaluationType & 2) != 0)
			newEntry.evaluationType ^= 1;
		return newEntry;
	}
	
	/**
	 * Returns a short textual representation of this entry.
	 */
	public String toString() {
		return "TTE(hV " + key + ", v " + (int)value + ", eT " + (int)evaluationType + ", sD " +
				(int)searchDepth + ", bM " + (int)bestMove + ", fM " + forcedMove + ")";
	}
	
	long key;
	short value;
	byte evaluationType;
	byte searchDepth;
	byte bestMove; // -1 if undefined
	boolean forcedMove; // A move is forced if it results in a win within search depth or is the only one avoiding to lose.
	
	/*
	// debug
	long board0,board1;
	
	public boolean cmp(long[] board) {
		if (board[0] == board0  &&  board[1] == board1) return true;
		// Test if reflected version
		for (int i=0; i<42; i++) {
			if ((board[0] & (1L<<i)) != 0  &&  (board0 & (1L<<ComputerPlayer.REFLECT[i])) == 0)
				return false;
			if ((board[1] & (1L<<i)) != 0  &&  (board1 & (1L<<ComputerPlayer.REFLECT[i])) == 0)
				return false;
		}
		return true;
	}
	*/
}
