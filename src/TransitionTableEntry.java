import java.io.Serializable;

public class TransitionTableEntry {

	// Evaluation types:
	static final byte UNDEFINED = 0;
	static final byte EXACT = 1;
	static final byte LOWER = 2;
	static final byte UPPER = 3;
	
	static final byte INFINITE_DEPTH = 64;
	
	public TransitionTableEntry() {
		hashValue = 0;
		value = 0;
		evaluationType = 0;
		searchDepth = -1;
		bestMove = -1;
		forcedMove = false;
	}
	
	public void clear() {
		hashValue = 0;
		value = 0;
		evaluationType = 0;
		searchDepth = -1;
		bestMove = -1;
		forcedMove = false;
	}
	
	public TransitionTableEntry(TransitionTableEntry entry) {
		this.hashValue = entry.hashValue;
		this.value = entry.value;
		this.evaluationType = entry.evaluationType;
		this.searchDepth = entry.searchDepth;
		this.bestMove = entry.bestMove;
		this.forcedMove = entry.forcedMove;
	}
	
	/*
	public TransitionTableEntry(short value, byte evaluationType, byte searchDepth) {
		this.hashValue = 0;
		this.value = value;
		this.evaluationType = evaluationType;
		this.searchDepth = searchDepth;
		this.bestMove = -1;
	}
	*/
	
	public TransitionTableEntry(int hashValue, short value, byte evaluationType, byte searchDepth, byte bestMove, boolean forcedMove) {
		if (-32700 < value  &&  value < 32700  &&  value != 0  &&  searchDepth >= 64)
			System.out.println("" + 1/0);
		
		this.hashValue = hashValue;
		this.value = value;
		this.evaluationType = evaluationType;
		this.searchDepth = searchDepth;
		this.bestMove = bestMove;
		this.forcedMove = forcedMove;
	}
	
	public void set(TransitionTableEntry entry) {
		this.hashValue = entry.hashValue;
		this.value = entry.value;
		this.evaluationType = entry.evaluationType;
		this.searchDepth = entry.searchDepth;
		this.bestMove = entry.bestMove;
		this.forcedMove = entry.forcedMove;
	}
	
	public boolean isDefined() { return evaluationType != 0; }
	
	public boolean isGameTheoreticalValue() {
		return searchDepth >= INFINITE_DEPTH;
	}
	
	public void setGameTheoreticalValue() {
		if (-32700 < value  &&  value < 32700)
			System.out.println("" + 1/0);
		searchDepth = INFINITE_DEPTH;
		evaluationType = EXACT;
	}
	
	// Constructs a new TransitionTableEntry
	public TransitionTableEntry stepOneMoveBack() {
		TransitionTableEntry newEntry = new TransitionTableEntry(this);
		newEntry.value = (short)-newEntry.value;
		++newEntry.searchDepth;
		if (newEntry.searchDepth == 64)
			System.out.println("" + 1/0);
		// Swap between LOWER and UPPER evaluation types:
		if ((newEntry.evaluationType & 2) != 0)
			newEntry.evaluationType ^= 1;
		return newEntry;
	}
	
	public String toString() {
		return "TTE(hV " + hashValue + ", v " + (int)value + ", eT " + (int)evaluationType + ", sD " +
				(int)searchDepth + ", bM " + (int)bestMove + ", fM " + forcedMove + ")";
	}
	
	int hashValue;
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
