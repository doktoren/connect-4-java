
public class GeneratePatterns {

	public final int MAX_LINE_LENGTH = 80;
	
	private int count;
	private int lineLength;
	
	private long b(int column, int row) {
		return 1L << (7*row + column);
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
		int w = (int)(Math.log(1.0*l)/Math.log(10.0)) + 4;
		if (lineLength + w > MAX_LINE_LENGTH) {
			System.out.println();
			lineLength = 0;
		}
		System.out.print("" + l + "L");
		lineLength += w; 
	}
	
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
}
