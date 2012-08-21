/**
 * This simple class generates the rules (patterns) for 2 of the connect 4 variants.
 * 
 * @author Jesper Kristensen
 */
public class GeneratePatterns {

	public final int MAX_LINE_LENGTH = 80;
	
	private int count;
	private int lineLength;
	
	private long b(int column, int row) {
		return 1L << (7*row + column);
	}
	
	private long b(int x, int y, int z) {
		return 1L << (x + 4*y + 16*z);
	}

	private void init() {
		System.out.println("private static final long[] PATTERNS = {");
		count = 0;
		lineLength = 0;
	}
	
	private void finish() {
		System.out.println("};");
		System.out.println("// PATTERNS.length == " + count);
		System.out.flush();
	}
	
	private void put(long l) {
		if (count++ > 0)
			System.out.print(", ");
		int w;
		if (l > 0) {
			w = (int)(Math.log(1.0*l)/Math.log(10.0)) + 4;
		} else {
			w = (int)(Math.log(l + Math.pow(2, 64))/Math.log(10.0)) + 4;
		}
		if (lineLength + w > MAX_LINE_LENGTH) {
			System.out.println();
			lineLength = 0;
		}
		System.out.print("" + l + "L");
		lineLength += w; 
	}
	
	/**
	 * Generates the rules for the normal connect 4 game.
	 * The output is sent to <code>System.out</code> as a java definition of a long array -
	 * one entry for each pattern (representing a winning combination).
	 */
	public void connectFour() {
		init();
		
		// Check vertical lines.
		for (int column=0; column<7; column++)
			for (int row=5; row>=3; row--)
				put(b(column,row) | b(column,row-1) | b(column,row-2) | b(column,row-3));
		
		// Check horizontal lines.
		for (int column=0; column<=3; column++)
			for (int row=0; row<6; row++)
				put(b(column,row) | b(column+1,row) | b(column+2,row) | b(column+3,row));
		
		// Check both kind of diagonal lines.
		for (int column=0; column<=3; column++)
			for (int row=5; row>=3; row--) {
				put(b(column,row) | b(column+1,row-1) | b(column+2,row-2) | b(column+3,row-3));
				put(b(column+3,row) | b(column+2,row-1) | b(column+1,row-2) | b(column,row-3));
			}
		
		finish();
	}
	
	/**
	 * Generates the rules for the square version.
	 * The output is sent to <code>System.out</code> as a java definition of a long array -
	 * one entry for each pattern (representing a winning combination).
	 */
	public void connectSquare() {
		init();
		
		for (int column=0; column<7; column++)
			for (int row=0; row<6; row++)
				for (int x=0; x<6; x++)
					for (int y=0; y<6; y++)
						if ((x!=0  ||  y!=0)  &&
							(0<=column+x  &&  column+x<7  &&  0<=row+y  &&  row+y<6)  &&
							(0<=column+x+y  &&  column+x+y<7  &&  0<=row+y-x  &&  row+y-x<6)  &&
							(0<=column+y  &&  column+y<7  &&  0<=row-x  &&  row-x<6))
							put(b(column,row) | b(column+x,row+y) | b(column+x+y,row+y-x) | b(column+y,row-x));
		
		finish();
	}
	
	/**
	 * Generates the rules for the 3D version.
	 * The output is sent to <code>System.out</code> as a java definition of a long array -
	 * one entry for each pattern (representing a winning combination).
	 */
	public void connect3D() {
		init();
		
		for (int x=0; x<4; x++) {
			for (int y=0; y<4; y++)
				put(b(x,y,0) | b(x,y,1) | b(x,y,2) | b(x,y,3));
			for (int z=0; z<4; z++)
				put(b(x,0,z) | b(x,1,z) | b(x,2,z) | b(x,3,z));
			
			put(b(x,0,0) | b(x,1,1) | b(x,2,2) | b(x,3,3));
			put(b(x,0,3) | b(x,1,2) | b(x,2,1) | b(x,3,0));
		}
		for (int y=0; y<4; y++) {
			for (int z=0; z<4; z++)
				put(b(0,y,z) | b(1,y,z) | b(2,y,z) | b(3,y,z));
			
			put(b(0,y,0) | b(1,y,1) | b(2,y,2) | b(3,y,3));
			put(b(0,y,3) | b(1,y,2) | b(2,y,1) | b(3,y,0));
		}
		for (int z=0; z<4; z++) {
			put(b(0,0,z) | b(1,1,z) | b(2,2,z) | b(3,3,z));
			put(b(0,3,z) | b(1,2,z) | b(2,1,z) | b(3,0,z));
		}
		
		// The 4 diagonals
		put(b(0,0,0) | b(1,1,1) | b(2,2,2) | b(3,3,3));
		put(b(0,0,3) | b(1,1,2) | b(2,2,1) | b(3,3,0));
		put(b(0,3,0) | b(1,2,1) | b(2,1,2) | b(3,0,3));
		put(b(0,3,3) | b(1,2,2) | b(2,1,1) | b(3,0,0));
		
		finish();
	}
}
