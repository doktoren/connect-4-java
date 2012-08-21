import java.io.Serializable;

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
	public TTE2(long hashValue, int score) {
		this.hashValue = hashValue;
		evaluationType = SCORE_DECIDED;
		this.score = score;
		this.count = DEFAULT_COUNT + 1;
	}
	
	public TTE2(long hashValue, short gameTheoreticalValue) {
		this.hashValue = hashValue;
		evaluationType = GAME_THEORETICAL;
		value = gameTheoreticalValue;
	}
	
	public TTE2(long hashValue, short wonOrLostValue, boolean dummy) {
		this.hashValue = hashValue;
		evaluationType = WON_OR_LOST;
		value = wonOrLostValue;
	}
	
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
	public void modifyScore(int score) {
		this.score += score;
		++count;
	}
	
	public void setLostOrWonValue(short value) {
		evaluationType = WON_OR_LOST;
		this.value = value;
	}
	
	public boolean isGameTheoreticalValue() {
		return evaluationType == GAME_THEORETICAL;
	}
	
	public boolean isWonLostValue() {
		return evaluationType == WON_OR_LOST;
	}
	
	public boolean matches(long hashValue) {
		return this.hashValue == hashValue;
	}
	
	public long getHashValue() {
		return hashValue;
	}
	
	public void print() {
		System.out.println("TTE2(hV " + hashValue + ", eT " + (int)evaluationType + ", v " + value +
				", s " + score + ", c " + count + ")");
	}
	
	private long hashValue;
	private byte evaluationType;
	private short value;
	
	// For each win 1 is added to score. For each loss 1 is subtracted
	private int score,count;
}
