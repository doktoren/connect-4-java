
public class TransitionTableEntry {
	public TransitionTableEntry() {
		value = 0;
		evaluation_type = 0;
		search_depth = -1;
	}
	
	public TransitionTableEntry(int hash_value, short value, byte evaluation_type, byte search_depth) {
		this.hash_value = hash_value;
		this.value = value;
		this.evaluation_type = evaluation_type;
		this.search_depth = search_depth;
	}
	
	public boolean is_defined() { return evaluation_type != 0; }
	
	int hash_value;
	short value;
	// evaluation_type: 0=undefined, 1=EXACT, 2=LOWER, 3=UPPER
	byte evaluation_type;
	byte search_depth;
	
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
}
