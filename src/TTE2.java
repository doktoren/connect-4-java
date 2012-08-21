import java.io.Serializable;

/**
 * Object used only by {@link HashTable} to implement the opening database in {@link ComputerPlayer}
 * 
 * @author Jesper Kristensen
 * @see HashTable
 * @see ComputerPlayer
 */
public class TTE2 implements Serializable {
	static final long serialVersionUID = 0xFECADBCE1238CD56L;

	public final byte UNKNOWN = 0;
	public final byte GAME_THEORETICAL = 1;
	public final byte SCORE_DECIDED = 2;
	public final byte WON_OR_LOST = 3;
	
	// yadda...
	public final int DEFAULT_COUNT = 2;
	
	/*
	public TTE2() {
		hashValue = 0xFFFFFFFFFFFFFFFFL;
		evaluationType = UNKNOWN;
		score = 0;
		count = DEFAULT_COUNT;
	}
	*/
	
	// position was eventually lost => score = -1, drawn => score = 0, won => 1
	
	/**
	 * Construct an object representing a position for which the outcome of one self playing game has been established.
	 * 
	 * @param hashValue the representation of the position.
	 * @param score -1 if the position was eventually lost for the player to move, 0 if drawn and 1 if won.
	 */
	public TTE2(long key, int index, int score) {
		this.key = key;
		this.index = index;
		evaluationType = SCORE_DECIDED;
		this.score = score;
		this.count = DEFAULT_COUNT + 1;
	}
	
	/**
	 * Construct an object representing a position for which the game theoretical value has been deduced.
	 * 
	 * @param key the representation of the position.
	 * @param gameTheoreticalValue 0 is a draw, ({@link ComputerPlayer#INFINITE_VALUE} -
	 * number of half moves played) is a win and with opposite sign it is a loss.
	 */
	public TTE2(long key, int index, short gameTheoreticalValue) {
		this.key = key;
		this.index = index;
		evaluationType = GAME_THEORETICAL;
		value = gameTheoreticalValue;
	}
	
	/**
	 * Like <code>TTE2(long, short)</code> with the exception that the value of the position is not
	 * necessarily optimal.
	 * 
	 * @param key the representation of the position.
	 * @param wonOrLostValue 0 is a draw, ({@link ComputerPlayer#INFINITE_VALUE} -
	 * number of half moves played) is a win and with opposite sign it is a loss.
	 * @param dummy ignored
	 */
	public TTE2(long key, int index, short wonOrLostValue, boolean dummy) {
		this.key = key;
		this.index = index;
		evaluationType = WON_OR_LOST;
		value = wonOrLostValue;
	}
	
	/**
	 * Returns the value of the position represented by this object.
	 * 
	 * @return the value of the position.
	 */
	public short getValue() {
		switch (evaluationType) {
		case GAME_THEORETICAL:
			return value;
		case SCORE_DECIDED:
			return (short)(((double)ComputerPlayer.GUARANTEED_WIN * score) / count);
		case WON_OR_LOST:
			return value;
		}
		return 0;
	}

	// position was eventually lost => score = -1, drawn => score = 0, won => 1
	
	/**
	 * Updates the value of this position in accordance with a new self played game.
	 * 
	 * @param score -1 if the position was eventually lost for the player to move, 0 if drawn and 1 if won.
	 */
	public void modifyScore(int score) {
		this.score += score;
		++count;
	}
	
	/**
	 * This position has been determined to be a win or loss in at most some number of moves.
	 * 
	 * @param value 0 is a draw, ({@link ComputerPlayer#INFINITE_VALUE} -
	 * number of half moves played) is a win and with opposite sign it is a loss.
	 */
	public void setLostOrWonValue(short value) {
		evaluationType = WON_OR_LOST;
		this.value = value;
	}
	
	/**
	 * Examines whether the game theoretical value for this position has been found.
	 * Notice that this includes the exact depth to the fastest win if possible.
	 * 
	 * @return true if the game theoretical value is known.
	 */
	public boolean isGameTheoreticalValue() {
		return evaluationType == GAME_THEORETICAL;
	}
	
	/**
	 * Examines whether this position has been determined to be a win or loss.
	 * Notice that we are satisfyed by an upper limit on the length to any winning move.
	 * 
	 * @return true if this position is known to be lost or won.
	 */
	public boolean isWonLostValue() {
		return evaluationType == WON_OR_LOST;
	}
	
	/**
	 * Compares this position against another one.
	 * 
	 * @param key the representation of the position we wish to compare against.
	 * @return true if <code>key</code> represents the same position.
	 */
	public boolean matches(long key, int index) {
		//System.out.println("("+this.key+","+this.index+") = ("+key+","+index+") ?");
		return this.key == key  &&  this.index == index;
	}
	
	/**
	 * Acquire the representation of this position.
	 * 
	 * @return the representation of this position.
	 */
	public long getKey() {
		return key;
	}
	
	public int getIndex() {
		return index;
	}
	
	/**
	 * Writes to <code>System.out</code> a short textual description of this object.
	 */
	public void print() {
		System.out.println("TTE2(hV " + key + ", eT " + (int)evaluationType + ", v " + value +
				", s " + score + ", c " + count + ")");
	}
	
	private long key;
	private int index;
	private byte evaluationType;
	private short value;
	
	// For each win 1 is added to score. For each loss 1 is subtracted
	private int score,count;
}
