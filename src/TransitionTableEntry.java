import java.io.Serializable;

public class TransitionTableEntry implements Serializable {
	static final long serialVersionUID = 0xFECADBCE1238CD56L;
	
	public TransitionTableEntry() {
		value = 0;
		evaluation_type = 0;
		search_depth = -1;
		best_move = -1;
	}
	
	public TransitionTableEntry(int hash_value, short value, byte evaluation_type, byte search_depth, byte best_move) {
		this.hash_value = hash_value;
		this.value = value;
		this.evaluation_type = evaluation_type;
		this.search_depth = search_depth;
		this.best_move = best_move;
	}
	
	public void set(TransitionTableEntry entry) {
		this.hash_value = entry.hash_value;
		this.value = entry.value;
		this.evaluation_type = entry.evaluation_type;
		this.search_depth = entry.search_depth;
		this.best_move = entry.best_move;
	}
	
	public boolean is_defined() { return evaluation_type != 0; }
	
	int hash_value;
	short value;
	// evaluation_type: 0=undefined, 1=EXACT, 2=LOWER, 3=UPPER, 4=Game theoretical
	byte evaluation_type;
	byte search_depth;
	byte best_move; // -1 if undefined
	
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
