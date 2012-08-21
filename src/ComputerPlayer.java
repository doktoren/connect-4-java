
public class ComputerPlayer {
	
	boolean p = false;
	
	boolean useSemiOracles = true;
	
	static final int INFINITE_VALUE = 32760;
	static final int GUARANTEED_WIN = INFINITE_VALUE - 64;
	
	// logSize >= 17 MUST be satisfied!
	static final int logSize = 20;
	// emptyHashValue = f[0] ^ f[1] ^ f[2] ^ f[3] ^ f[4] ^ f[5] ^ f[6];
	static long emptyHashValue;
	
	
	// Normal connect 4
	private static final long[] CONNECT_4_PATTERNS = {
		34630287360L, 270549120L, 2113665L, 69260574720L, 541098240L, 4227330L, 
		138521149440L, 1082196480L, 8454660L, 277042298880L, 2164392960L, 16909320L, 
		554084597760L, 4328785920L, 33818640L, 1108169195520L, 8657571840L, 67637280L, 
		2216338391040L, 17315143680L, 135274560L, 15L, 1920L, 245760L, 31457280L, 
		4026531840L, 515396075520L, 30L, 3840L, 491520L, 62914560L, 8053063680L, 
		1030792151040L, 60L, 7680L, 983040L, 125829120L, 16106127360L, 2061584302080L, 
		120L, 15360L, 1966080L, 251658240L, 32212254720L, 4123168604160L, 34905128960L, 
		275955859456L, 272696320L, 2155905152L, 2130440L, 16843009L, 69810257920L, 
		551911718912L, 545392640L, 4311810304L, 4260880L, 33686018L, 139620515840L, 
		1103823437824L, 1090785280L, 8623620608L, 8521760L, 67372036L, 279241031680L, 
		2207646875648L, 2181570560L, 17247241216L, 17043520L, 134744072L};

	// Connect 4 with wrap and bounce
	private static final long[] WRAP_AND_BOUNCE_PATTERNS = {
		15L, 30L, 60L, 71L, 99L, 113L, 120L, 1920L,
		3840L, 7680L, 9088L, 12672L, 14464L, 15360L, 17668L, 26656L,
		33410L, 35336L, 37056L, 70672L, 73985L, 131714L, 141344L, 245760L,
		263428L, 266432L, 491520L, 526856L, 532737L, 534560L, 983040L, 1049218L,
		1053712L, 1163264L, 1622016L, 1851392L, 1966080L, 2113665L, 2130440L, 3149840L,
		4210948L, 4218912L, 4227330L, 4260880L, 8421506L, 8421568L, 8454660L, 8521760L,
		16843009L, 16909320L, 17043520L, 31457280L, 33686018L, 33818640L,
		34086913L, 34086944L, 62914560L, 67372036L, 67637280L, 68157570L,
		68161552L, 125829120L, 134234372L, 134744072L, 135274560L, 148897792L,
		207618048L, 236978176L, 251658240L, 270549120L, 272696320L, 403179520L,
		539001344L, 540020736L, 541098240L, 545392640L, 1077952768L, 1077960704L,
		1082196480L, 1090785280L, 2155905152L, 2164392960L, 2181570560L, 4026531840L,
		4311810304L, 4328785920L, 4363124864L, 4363128832L,	8053063680L, 8623620608L,
		8657571840L, 8724168960L, 8724678656L, 16106127360L, 17181999616L, 17247241216L,
		17315143680L, 19058917376L, 26575110144L, 30333206528L,
		32212254720L, 34359754881L, 34359869954L, 34360004672L,
		34628173953L, 34630270977L, 34630287360L, 34896609794L,
		34896613440L, 34904997890L, 34904997952L, 34905128960L,
		51539608066L, 51606716418L, 51606978560L, 52084867072L,
		52143587328L, 68719509762L, 68719542529L, 68719739908L,
		68720009217L, 68987913220L, 68992106500L, 68992172032L,
		69122129924L, 69122654208L, 69256347906L, 69260541954L,
		69260574720L, 69793218817L, 69793226753L, 69809995777L,
		69810257920L, 70065848320L, 70078431232L, 70195871744L,
		137438986370L, 137439019524L, 137439479816L, 137440002178L,
		137975826440L, 137977921544L, 137977954304L, 137978970112L,
		138512695812L, 138521083908L, 138521149440L, 139586437250L,
		139619991554L, 139620515840L, 140125405184L, 140156862464L,
		274877923588L, 274878039048L, 274878959632L, 275951652880L,
		275955843088L, 275955859456L, 277025391624L, 277042167816L,
		277042298880L, 279172874500L, 279239983108L, 279241031680L,
		280250810368L, 280313724928L, 515396075520L, 549755838496L,
		549755847176L, 549756078096L, 549756346400L, 551903305760L,
		551911686176L, 551911718912L, 554050783248L, 554084335632L,
		554084597760L, 558345749000L, 558479966216L, 558479982592L,
		558480490496L, 560501620736L, 560627449856L, 1030792151040L,
		1099511660736L, 1099511694352L, 1099511894080L, 1099512156192L,
		1103806595264L, 1103806599232L, 1103823372352L, 1103823437824L,
		1108101566496L, 1108168671264L, 1108169195520L, 1116691498000L,
		1116693594128L, 1116693626880L, 1116758605840L, 1116758867968L,
		1120988561408L, 1121003241472L, 1121053573120L, 2061584302080L,
		2199023321345L, 2199023388704L, 2199024312384L, 2199291693088L,
		2199295885344L, 2199295950848L, 2207613190401L, 2207613192224L,
		2207646744577L, 2207646744608L, 2207646875648L, 2207885819904L,
		2207915180032L, 2216203132992L, 2216337342528L, 2216338391040L,
		2439541424128L, 3401614098432L, 3882650435584L, 4123168604160L};
	
	// Connect square
	private static final long[] SQUARE_PATTERNS = {
		387L, 81925L, 18874377L, 4563402769L, 1133871366177L, 49536L, 10486400L, 
		2415920256L, 584115554432L, 387L, 33410L, 4325508L, 570425480L, 77309411472L, 
		6340608L, 1342259200L, 309237792768L, 49536L, 4276480L, 553665024L, 
		73014461440L, 81925L, 8406018L, 1074020356L, 137506078728L, 811597824L, 
		171809177600L, 6340608L, 547389440L, 70869123072L, 10486400L, 1075970304L, 
		137474605568L, 18874377L, 2149582850L, 274880528388L, 103884521472L, 
		811597824L, 70065848320L, 1342259200L, 137724198912L, 2415920256L, 
		275146604800L, 4563402769L, 550024253442L, 103884521472L, 171809177600L, 
		309237792768L, 584115554432L, 1133871366177L, 774L, 163850L, 37748754L, 
		9126805538L, 2267742732354L, 99072L, 20972800L, 4831840512L, 1168231108864L, 
		774L, 66820L, 8651016L, 1140850960L, 154618822944L, 12681216L, 2684518400L, 
		618475585536L, 99072L, 8552960L, 1107330048L, 146028922880L, 163850L, 
		16812036L, 2148040712L, 275012157456L, 1623195648L, 343618355200L, 12681216L, 
		1094778880L, 141738246144L, 20972800L, 2151940608L, 274949211136L, 37748754L, 
		4299165700L, 549761056776L, 207769042944L, 1623195648L, 140131696640L, 
		2684518400L, 275448397824L, 4831840512L, 550293209600L, 9126805538L, 
		1100048506884L, 207769042944L, 343618355200L, 618475585536L, 1168231108864L, 
		2267742732354L, 1548L, 327700L, 75497508L, 18253611076L, 198144L, 41945600L, 
		9663681024L, 2336462217728L, 1548L, 133640L, 17302032L, 2281701920L, 25362432L, 
		5369036800L, 1236951171072L, 198144L, 17105920L, 2214660096L, 292057845760L, 
		327700L, 33624072L, 4296081424L, 3246391296L, 687236710400L, 25362432L, 
		2189557760L, 283476492288L, 41945600L, 4303881216L, 549898422272L, 75497508L, 
		8598331400L, 415538085888L, 3246391296L, 280263393280L, 5369036800L, 
		550896795648L, 9663681024L, 1100586419200L, 18253611076L, 415538085888L, 
		687236710400L, 1236951171072L, 2336462217728L, 3096L, 655400L, 150995016L, 
		396288L, 83891200L, 19327362048L, 3096L, 267280L, 34604064L, 50724864L, 
		10738073600L, 2473902342144L, 396288L, 34211840L, 4429320192L, 655400L, 
		67248144L, 6492782592L, 1374473420800L, 50724864L, 4379115520L, 566952984576L, 
		83891200L, 8607762432L, 150995016L, 831076171776L, 6492782592L, 560526786560L, 
		10738073600L, 1101793591296L, 19327362048L, 831076171776L, 1374473420800L, 
		2473902342144L, 6192L, 1310800L, 792576L, 167782400L, 6192L, 534560L, 
		101449728L, 21476147200L, 792576L, 68423680L, 1310800L, 12985565184L, 
		2748946841600L, 101449728L, 8758231040L, 167782400L, 1662152343552L, 
		12985565184L, 1121053573120L, 21476147200L, 1662152343552L, 2748946841600L, 
		12384L, 1585152L, 12384L, 202899456L, 1585152L, 25971130368L, 202899456L, 
		3324304687104L, 25971130368L, 3324304687104L};
	
	private final long[] PATTERNS;
	
	// Used to maintain hash value of symmetric position
	public static final byte[] REFLECT =
		{6, 5, 4, 3, 2, 1, 0,
		13,12,11,10, 9, 8, 7,
		20,19,18,17,16,15,14,
		27,26,25,24,23,22,21,
		34,33,32,31,30,29,28,
		41,40,39,38,37,36,35,
		48,47,46,45,44,43,42};

	
	
	// hashValue is 49 bits long.
	private long hv0, hv1;
	// hashValue is the lexicographically least value representing a simple transformation of this position.
	private long hashValue;
	
	private long[] f;
	
	private long[] board;
	
	private int[] insertPos;
	
	// 'X' is 0, 'O' is 1. 
	private int lastPlayer;
	
	// Warning! First move indexed 1.
	private int[] movesPlayed;
	private int numMovesPlayed;
	private int rootDepth;
	
	private int[] patternIndex;
	private long[] patterns;
	private int[] patternRef;
	
	// one index for each pattern.
	// 'X' counts 1b, 'O' counts 100b
	private int[] completionCount;
	// Relative for 'X' (good is positive)
	static final private int[] CC_VALUES =
       {0,1,4,10,
		-1,0,0,0,
		-4,0,0,0,
		-10,0,0,0, 0}; // last element is a dummy
	private int evalValue;
	
	private long computingPerformed;
	
	// lowDepthTransitionTable, highDepthTransitionTable
	private TransitionTableEntry[][] tt;
	// if (depth <= cutDepth) store in tt[0] else store in tt[1]
	private int cutDepth;
	
	private HashTable openingLibrary;
	
		// game = 0: Normal connect 4
	// game = 1: Wrap & bounce
	// game = 2: Square
	public ComputerPlayer(int game) {
		if (game == 1) PATTERNS = WRAP_AND_BOUNCE_PATTERNS;
		else if (game == 2) PATTERNS = SQUARE_PATTERNS;
		else PATTERNS = CONNECT_4_PATTERNS;
		
		board = new long[2];
		insertPos = new int[7];
		movesPlayed = new int[42+1];
		
		
		completionCount = new int[PATTERNS.length];

		
		initHashValues();
		initPatterns();
		initTT();
		
		switch (game) {
		case 0:
			openingLibrary = new HashTable("openingDatabase_connect_4.dat");
			break;
		case 1:
			openingLibrary = new HashTable("openingDatabase_wrap_and_bounce.dat");
			break;
		case 2:
			openingLibrary = new HashTable("openingDatabase_square.dat");
			break;
		}
		openingLibrary.load();
	}
	
	public void newGame() {
		hv0 = hv1 = hashValue = emptyHashValue;
		board[0] = board[1] = 0;
		for (int i=0; i<7; i++)
			insertPos[i] = i;
	
		lastPlayer = 1;
		
		movesPlayed[0] = 99999; // Should NEVER get used.
		numMovesPlayed = 0;
		
		for (int i=0; i<PATTERNS.length; i++)
			completionCount[i] = 0;
		evalValue = 0;
	}
	
	private TransitionTableEntry internalCalculateMove(long allowedComputing) {
		computingPerformed = 0;
		rootDepth = numMovesPlayed;
		
		int thinkDepth = 0;
		long lastComputingPerformed = 0;
		long lastThinkingRequired = 1;
		
		cutDepth = 2;
		do {
			++thinkDepth;
			// Fixed cutDepth experiments. logSize = 20
			//  cutDepth  | search depth 13 | search depth 14 | search depth 15 | search depth 16 |
			// -----------+-----------------+-----------------+-----------------+-----------------+
			//       1    |                 |                 |                 |                 |
			//       2    |       3504779   |     14651931    |                 |                 |
			//       3    |       3530808   |     12421216    |      49660198   |                 |
			//       4    |       3727459   |     12626514    |      35626434   |     120398323   |
			//       5    |       3911385   |     13244968    |      35566640   |      71641498   |
			//       6    |       5298651   |     13878304    |      38219926   |      79365261   |
			//  Implemented incrementing cutDepth:
			//            |  (3)  3533960   | (4) 12518056    | (5)  36922681   | (6)  74740710   |
			//  Implemented incrementing cutDepth (logSize only 17):
			//            |  (5)  4222860   | (6) 13151248    | (7)  63921658   |                 |
			//  Implemented incrementing cutDepth (logSize only 17), check before overwrite:
			//            |  (5)  4574241   | (6) 13784163    | (7)  70206359   |                 |
			//  Implemented incrementing cutDepth (logSize only 17), check before overwrite (weaker):
			//            |  (5)  4613851   | (6) 13913321    | (7)  68826774   |                 |
			if (computingPerformed >= (1 << logSize)) ++cutDepth;
			
			// TODO: fortsæt her
			int alpha = -INFINITE_VALUE + (numMovesPlayed + thinkDepth);
			int beta = INFINITE_VALUE - (numMovesPlayed + thinkDepth);
			
			if (p) System.out.println("thinkDepth = " + thinkDepth + ":");

			TransitionTableEntry entry = recurseMove(thinkDepth, alpha, beta);
			if (p) System.out.println(entry);
			
			/*
			if (value > -INFINITE_VALUE+42) {
				// Will not lose by playing this move
				++numNonLosingMoves;
			}
			*/
			
			if (entry.value >= beta) {
				// Optimal move found.
				if (p) System.out.println("Optimal move found!");
				return entry;
			}
			
			if (p) System.out.println("computingPerformed = " + computingPerformed + ", cutDepth = " + cutDepth);
			
			if (entry.value <= alpha) {
				if (p) System.out.println("The game is lost! Give up searching any further.");
				return entry;
			}
			
			if (entry.forcedMove  &&  entry.value < beta) {
				if (p) System.out.println("Only one reasonable move - play this!");
				return entry;
			}
			
			double d = computingPerformed - lastComputingPerformed;
			boolean continueSearch = allowedComputing-computingPerformed > d*d / lastThinkingRequired;
			lastThinkingRequired = computingPerformed - lastThinkingRequired;
			lastComputingPerformed = computingPerformed;
			
			if (!continueSearch  ||  numMovesPlayed+thinkDepth == 42) {
				if (p) System.out.println("Computer plays column " + (entry.bestMove+1));
				return entry;
			}
			
		} while (true);
	}
	
	public int calculateMove(long allowedComputing) {
		if (isGameOver()  ||  numMovesPlayed == 42) {
			System.err.println("Error: calculateMove: Game has terminated.");
			return 0;
		}
		
		System.err.println("calculateMove(" + allowedComputing + "):");
		
		// boolean lastP = p;
		// p = true;
		int move = internalCalculateMove(allowedComputing).bestMove;
		// p = lastP;

		return move;
	}
	
	
	public void selfPlay() {
		long allowedComputing = 100000L;
		
		for (int numGames=1; ; numGames++) {
			System.out.println();
			System.out.println("Selfplay: Game number " + numGames);
			
			int score = 0;
			short lastValue = 0;
			newGame();
			while (true) {
				// Make move
				clearTT();
				TransitionTableEntry entry = internalCalculateMove(allowedComputing);
				if (entry.isGameTheoreticalValue()) {
					
					print();
					System.out.println(entry);
					System.out.flush();

					lastValue = (short)-entry.value;
					
					TTE2 entry2 = new TTE2(hashValue, entry.value);
					openingLibrary.insert(entry2);
					score = entry.value > 0 ? 1 : (entry.value < 0 ? -1 : 0);
					{
						System.out.println("################################");
						if (score == 0) {
							System.out.println("########   A DRAW!!!   #########");
						} else {
							int p = lastPlayer;
							if (score < 0) p ^= 1;
							if (p == 0) System.out.println("######    O WINS!!!!   #########");
							if (p == 1) System.out.println("######    X WINS!!!!   #########");
						}
						System.out.println("################################");
					}
					
					if (numMovesPlayed == 0) {
						System.out.println("The game has been solved :-)");
						openingLibrary.save();
						return;
					}
					
					break;
					
				} else {
					put(entry.bestMove);
				}
			}
			
			while (numMovesPlayed > 0) {
				undo();
				score = -score;
				
				// See if search can be improved now
				clearTT();
				//useSemiOracles = false;
				TransitionTableEntry entry = internalCalculateMove(allowedComputing);
				//useSemiOracles = true;

				// The search might did not see an actual winning play
				if (entry.value < lastValue)
					entry.value = lastValue;
				lastValue = (short)-entry.value;
				
				if (entry.isGameTheoreticalValue()) {
					System.out.println("Added game theoretical value!");
					print();
					System.out.println(entry);
					System.out.flush();
					
					TTE2 entry2 = new TTE2(hashValue, entry.value);
					openingLibrary.insert(entry2);

					
				} else if (entry.value <= -GUARANTEED_WIN  ||  GUARANTEED_WIN <= entry.value) {
					// We know that the game is lost or won in at most n moves, but
					// this is not necessarily optimal.
					System.out.println("Added won/lost value!");
					print();
					System.out.println(entry);
					System.out.flush();
					
					TTE2 entry2 = openingLibrary.lookup(hashValue);
					
					if (entry2 == null) {
						openingLibrary.insert(new TTE2(hashValue, entry.value, false));
					} else {
						entry2.setLostOrWonValue(entry.value);
					}
					
				} else {
					
					TTE2 entry2 = openingLibrary.lookup(hashValue);
					
					if (entry2 == null) {
						openingLibrary.insert(new TTE2(hashValue, score));
					} else {
						entry2.modifyScore(score);
					}
				}
			}
			
			openingLibrary.save();
		}
	}

	// Returns value relative to player
	private TransitionTableEntry recurseValue(int depth, int alpha, int beta) {
		
		if (numMovesPlayed == 42)
			return new TransitionTableEntry((int)(hashValue >>> 32), (short)0,
					TransitionTableEntry.EXACT, TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false);
		if (depth <= 0) {
			int value = evaluate();
			return new TransitionTableEntry((int)(hashValue >>> 32), (short)value,
					TransitionTableEntry.EXACT, (byte)0, (byte)-1, false);
		}
		
		{ // Lookup opening library
			TTE2 entry2 = openingLibrary.lookup(hashValue);
			if (entry2 != null) {
				if (entry2.isGameTheoreticalValue())
					return new TransitionTableEntry((int)(hashValue >>> 32), entry2.getValue(),
							TransitionTableEntry.EXACT, TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false);
				
				if (useSemiOracles  ||
						(entry2.isWonLostValue()  &&  (entry2.getValue() <= alpha  ||  beta <= entry2.getValue())))
					return new TransitionTableEntry((int)(hashValue >>> 32), entry2.getValue(),
							TransitionTableEntry.EXACT, (byte)(42 - numMovesPlayed), (byte)-1, false);
			}
		}
		
		{ // Lookup transition table
			
			TransitionTableEntry entry = null;
			if (tt[0][(int)hashValue].hashValue == (int)(hashValue >>> 32))
				entry = tt[0][(int)hashValue];
			if (tt[1][(int)hashValue].hashValue == (int)(hashValue >>> 32))
				entry = tt[1][(int)hashValue];
			
			if (entry != null) {
				// Either entry is not initialized (and then it has search depth -1)
				// or otherwise it is the same position.
				if (entry.searchDepth >= depth  &&
						(entry.searchDepth == depth  ||  entry.isGameTheoreticalValue()  ||
								((entry.searchDepth ^ depth) & 1) == 0)) {
					// if (!tt[index].cmp(board)) System.err.println("Hash value error :-(");
					
					switch (entry.evaluationType) {
					case TransitionTableEntry.EXACT:
						return entry;
					case TransitionTableEntry.LOWER:
						if (entry.value < alpha)
							return entry;
						break;
					case TransitionTableEntry.UPPER:
						if (entry.value >= beta)
							return entry;
						break;
					}
				}
			}
		}
		
		int lastDepthBestMove = -1;
		// Internal iterative deepening for best move
		if (depth > 2) {
			
			TransitionTableEntry entry = null;
			if (tt[0][(int)hashValue].hashValue == (int)(hashValue >>> 32))
				entry = tt[0][(int)hashValue];
			if (tt[1][(int)hashValue].hashValue == (int)(hashValue >>> 32))
				entry = tt[1][(int)hashValue];
			
			if (entry == null  ||  entry.searchDepth < depth-2  ||  entry.bestMove == -1) {
				recurseMove(depth-2, alpha, beta);
				entry = tt[depth-2 <= cutDepth ? 0 : 1][(int)hashValue];
				// entry.bestMove might still be undefined (-1)
			}
			lastDepthBestMove = entry.bestMove;
		}
		
		TransitionTableEntry entry = new TransitionTableEntry((int)(hashValue >>> 32), (short)-INFINITE_VALUE,
				TransitionTableEntry.UPPER, (byte)TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false);
		
		int[] perm = {0,1,2,3,4,5,6};
		if (lastDepthBestMove != -1) {
			perm[0] = lastDepthBestMove;
			perm[lastDepthBestMove] = 0;
		}
		for (int ii=0; ii<7; ii++) {
			int i = perm[ii];
			if (insertPos[i] < 42) {
				
				put(i);
				TransitionTableEntry next = gameOver() ? 
						new TransitionTableEntry((int)(hashValue >>> 32), (short)(INFINITE_VALUE-numMovesPlayed),
								TransitionTableEntry.EXACT, TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false) :
						recurseValue(depth - 1, -beta, -alpha).stepOneMoveBack();
				undo();
				
				if (next.searchDepth < entry.searchDepth)
					entry.searchDepth = next.searchDepth;
				
				if (next.value > entry.value) {
					entry.value = next.value;
					entry.bestMove = (byte)i;
					
					if (entry.value > alpha) {
						entry.evaluationType = TransitionTableEntry.EXACT;
						alpha = entry.value;
						
						if (entry.value >= beta) {
							entry.evaluationType = TransitionTableEntry.LOWER;
							break;
						}
					}
				}
			}
		}
		
		if (entry.value >= (INFINITE_VALUE - (numMovesPlayed + depth))) {
			// A win in at most depth moves, this is optimal!
			entry.setGameTheoreticalValue();
			entry.forcedMove = true;
		}

		if (entry.value <= -(INFINITE_VALUE - (numMovesPlayed + depth))) {
			// A lose in at most depth moves, this is worst possible!
			entry.setGameTheoreticalValue();
			entry.forcedMove = true;
		}

		{   // Update transition table
			// Don't care whats already in the transition table entry!
			
			tt[0][(int)hashValue] = entry;

			if (depth > cutDepth)
				tt[1][(int)hashValue] = entry;
		}
		
		// max may be smaller than alpha - usefull in connection with a transition table.
		return entry;
	}
	
	// Returns the best move. May not be called with depth < 1.
	// The value is set too, but it might be based on a lower search depth!
	private TransitionTableEntry recurseMove(int depth, int alpha, int beta) {

		{ // Lookup transition table
			
			TransitionTableEntry entry = null;
			if (tt[0][(int)hashValue].hashValue == (int)(hashValue >>> 32))
				entry = tt[0][(int)hashValue];
			if (tt[1][(int)hashValue].hashValue == (int)(hashValue >>> 32))
				entry = tt[1][(int)hashValue];
			
			if (entry != null) {
				if (entry.forcedMove)
					return entry;
				
				// Either entry is not initialized (and then it has search depth -1)
				// or otherwise it is the same position.
				if (entry.searchDepth >= depth  &&  entry.bestMove != -1  &&
						(entry.searchDepth == depth  ||  entry.isGameTheoreticalValue()  ||
								((entry.searchDepth ^ depth) & 1) == 0)) {
					// if (!tt[index].cmp(board)) System.out.println("Hash value error :-(");
					
					switch (entry.evaluationType) {
					case TransitionTableEntry.EXACT:
						return entry;
					case TransitionTableEntry.LOWER:
						if (entry.value < alpha)
							return entry;
						break;
					case TransitionTableEntry.UPPER:
						if (entry.value >= beta)
							return entry;
						break;
					}
				}
			}
		}
		
		int lastDepthBestMove = -1;
		// Internal iterative deepening for best move
		if (depth > 2) {
			
			TransitionTableEntry entry = null;
			if (tt[0][(int)hashValue].hashValue == (int)(hashValue >>> 32))
				entry = tt[0][(int)hashValue];
			if (tt[1][(int)hashValue].hashValue == (int)(hashValue >>> 32))
				entry = tt[1][(int)hashValue];
			
			if (entry != null  &&  entry.forcedMove)
				return entry;
			
			if (entry == null  ||  entry.searchDepth < depth-2  ||  entry.bestMove == -1) {
				recurseMove(depth-2, alpha, beta);
				entry = tt[depth-2 <= cutDepth ? 0 : 1][(int)hashValue];
				// entry.bestMove might still be undefined (-1)
			}
			lastDepthBestMove = entry.bestMove;
		}
		
		TransitionTableEntry entry = new TransitionTableEntry((int)(hashValue >>> 32), (short)-INFINITE_VALUE,
				TransitionTableEntry.UPPER, (byte)TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false);
		
		int numNonLosingMoves = 0;
		
		int[] perm = {0,1,2,3,4,5,6};
		if (lastDepthBestMove != -1) {
			perm[0] = lastDepthBestMove;
			perm[lastDepthBestMove] = 0;
		}
		for (int ii=0; ii<7; ii++) {
			int i = perm[ii];
			if (insertPos[i] < 42) {
				
				put(i);
				TransitionTableEntry next = gameOver() ? 
						new TransitionTableEntry((int)(hashValue >>> 32), (short)(INFINITE_VALUE-numMovesPlayed),
								TransitionTableEntry.EXACT, TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false) :
						recurseValue(depth - 1, -beta, -alpha).stepOneMoveBack();
				undo();
				
				if (next.searchDepth < entry.searchDepth)
					entry.searchDepth = next.searchDepth;
				
				if (next.value >= -GUARANTEED_WIN)
					++numNonLosingMoves;
				
				if (next.value > entry.value) {
					entry.value = next.value;
					entry.bestMove = (byte)i;
					
					if (entry.value > alpha) {
						entry.evaluationType = TransitionTableEntry.EXACT;
						alpha = entry.value;
						
						if (entry.value >= beta) {
							entry.evaluationType = TransitionTableEntry.LOWER;
							break;
						}
					}
				}
			}
		}
		
		if (entry.value >= (INFINITE_VALUE - (numMovesPlayed + depth))) {
			// A win in at most depth moves, this is optimal!
			entry.setGameTheoreticalValue();
			entry.forcedMove = true;
		}

		if (entry.value <= -(INFINITE_VALUE - (numMovesPlayed + depth))) {
			// A lose in at most depth moves, this is worst possible!
			entry.setGameTheoreticalValue();
			entry.forcedMove = true;
		}
		
		if (numNonLosingMoves == 1)
			entry.forcedMove = true;
		
		{   // Update transition table
			// Don't care whats already in the transition table entry!
			
			tt[0][(int)hashValue] = entry;

			if (depth > cutDepth)
				tt[1][(int)hashValue] = entry;
		}
		
		// max may be smaller than alpha - usefull in connection with a transition table.
		return entry;
	}
	
	// WARNING! May only be called if the last move is stored.
	private boolean gameOver() {
		int stop = patternIndex[movesPlayed[numMovesPlayed] + 1];
		long b = board[lastPlayer];
		//print(b);
		for (int i=patternIndex[movesPlayed[numMovesPlayed]]; i<stop; i++) {
			//System.err.println("i = "+ i + ", ");
			//print(patterns[i]);
			// TODO: if ((~b & ~patterns[i]) == 0) return true;
			if ((b & patterns[i]) == patterns[i]) return true;
		}
		return false;
	}
	
	public boolean isGameOver() {
		return numMovesPlayed > 0  &&  gameOver();
	}

	private int evaluate() {
		if (lastPlayer == 0) {
			// Current player is 'O'
			return -evalValue;
		} else {
			// Current player is 'X*
			return evalValue;
		}
	}
	
	
	public void showLibraryInfo() {
		TTE2 e2 = openingLibrary.lookup(hashValue);
		if (e2 == null) {
			System.out.println("Library info not available in current position!");
			return;
		}
		e2.print();
	}
	
	// WARNING! No error checking!
	public void put(int column) {
		++computingPerformed;
		/*
		if ((computingPerformed % 100) == 0) {
			if (((100*computingPerformed)/allowedComputing)*allowedComputing == 100*computingPerformed)
				System.out.print("#");
		}
		*/
		
		int pos = movesPlayed[++numMovesPlayed] = insertPos[column];
		insertPos[column] += 7;

		lastPlayer ^= 1;
		board[lastPlayer] |= 1L << pos;
		
		{ // Update hash values
			if (lastPlayer == 0) {
				hv0 ^= f[pos];
				hv1 ^= f[REFLECT[pos]];
			}
			hv0 ^= f[pos + 7];
			hv1 ^= f[REFLECT[pos + 7]];
			hashValue = hv0 < hv1 ? hv0 : hv1;
		}
		
		{ // Adjust evalValue
			int adjustment = 1 << (lastPlayer << 1);
			int stop = patternIndex[pos + 1];
			for (int i=patternIndex[pos]; i<stop; i++) {
				evalValue -= CC_VALUES[completionCount[patternRef[i]]];
				completionCount[patternRef[i]] += adjustment;
				evalValue += CC_VALUES[completionCount[patternRef[i]]];
			}
		}
	}
	
	public void undo() {
		int pos = movesPlayed[numMovesPlayed];

		{ // Adjust evalValue
			int adjustment = 1 << (lastPlayer << 1);
			int stop = patternIndex[pos + 1];
			for (int i=patternIndex[pos]; i<stop; i++) {
				evalValue -= CC_VALUES[completionCount[patternRef[i]]];
				completionCount[patternRef[i]] -= adjustment;
				evalValue += CC_VALUES[completionCount[patternRef[i]]];
			}
		}
		
		numMovesPlayed--;
		
		// TODO: % is expensive.
		insertPos[pos % 7] -= 7;
		
		{   // Update hash values
			// TODO: Maybe it is faster to stack/pop these values.
			hv0 ^= f[pos+7];
			hv1 ^= f[REFLECT[pos+7]];
			if (lastPlayer == 0) {
				hv0 ^= f[pos];
				hv1 ^= f[REFLECT[pos]];
			}
			hashValue = hv0 < hv1 ? hv0 : hv1;
		}
		
		board[lastPlayer] ^= 1L << pos;
		
		lastPlayer ^= 1;
	}
	
	private void initHashValues() {
		f = new long[49];
		f[0] = 0x12C701D20654CL;
		f[1] = 0x05B37B8EC22D5L;
		f[2] = 0x0535941379CF2L;
		f[3] = 0x003CCF778392AL;
		f[4] = 0x13FE3CBB8CF94L;
		f[5] = 0x11D543FBD4E56L;
		f[6] = 0x172225A5C5814L;
		f[7] = 0x1B89EF727CA59L;
		f[8] = 0x124DD28F5983EL;
		f[9] = 0x12671071D7D85L;
		f[10] = 0x1D7F2B3EE3E77L;
		f[11] = 0x0BBCEC18E1390L;
		f[12] = 0x034809CBAB084L;
		f[13] = 0x1E4FBF3423B81L;
		f[14] = 0x1028856CDDBB8L;
		f[15] = 0x19CAC89F484B6L;
		f[16] = 0x04975CFE12118L;
		f[17] = 0x0ABE08E5FE685L;
		f[18] = 0x1C23DC6FBC49DL;
		f[19] = 0x078A716385093L;
		f[20] = 0x18DB680F271B4L;
		f[21] = 0x0159E33CED46DL;
		f[22] = 0x0E8B1E0F81239L;
		f[23] = 0x068A150190706L;
		f[24] = 0x1A7D4598560CDL;
		f[25] = 0x066768F8701EBL;
		f[26] = 0x0A3B826061391L;
		f[27] = 0x1455D065FA669L;
		f[28] = 0x1FD0F9F5A134EL;
		f[29] = 0x00AE9C053C166L;
		f[30] = 0x1E772D50C4380L;
		f[31] = 0x083D7CC2385BDL;
		f[32] = 0x05EE70E982EA8L;
		f[33] = 0x1EB18631A8D1EL;
		f[34] = 0x01691B9927F64L;
		f[35] = 0x10B8C775BC150L;
		f[36] = 0x0D0969C538A62L;
		f[37] = 0x0A051B33E4187L;
		f[38] = 0x18E6C30E61A0DL;
		f[39] = 0x0C425C0A25E59L;
		f[40] = 0x02C5A40B8B7FFL;
		f[41] = 0x1B0F5352B93D1L;
		f[42] = 0x160639929EF27L;
		f[43] = 0x11D6C2E4CD986L;
		f[44] = 0x1289317FDB589L;
		f[45] = 0x068BE4529C9B0L;
		f[46] = 0x04B2EC62E6A22L;
		f[47] = 0x0647693535332L;
		f[48] = 0x17CFFF7FD1703L;

		// The low 32 bit part of f will be used to index the transition table.
		// Copy (logSize-17) from the lower 32 bits to the higher 32 bits, such
		// that the entire range of the transition table is exploitet.
		long p = ((1L << (logSize-17)) - 1L) << (32+17);
		for (int i=0; i<49; i++) {
			f[i] |= (f[i] << 32) & p;
			
			// swap low and high 32 bits
			f[i] = (f[i] << 32) | (f[i] >>> 32);
		}
		
		emptyHashValue = f[0] ^ f[1] ^ f[2] ^ f[3] ^ f[4] ^ f[5] ^ f[6];
	}
	
	private void initPatterns() {
		patternIndex = new int[4*PATTERNS.length + 1];
		patterns = new long[4*PATTERNS.length];
		patternRef = new int[4*PATTERNS.length];
		int k = 0;
		long b = 1;
		for (int i=0; i<42; i++) {
			patternIndex[i] = k;
			for (int j=0; j<PATTERNS.length; j++)
				if ((PATTERNS[j] & b) != 0) {
					// Remove the bit for the square itself
					patterns[k] = PATTERNS[j] ^ (1L << i);
					patternRef[k] = j;
					k++;
				}
			b <<= 1;
			//System.err.print("("+i+":"+(patternIndex[i]-k)+")");
			//if ((i%7)==6) System.out.println();
		}
		patternIndex[42] = k;
	}
	
	private void initTT() {
		System.err.println("logSize = " + logSize);
		tt = new TransitionTableEntry[2][1 << logSize];
		for (int i=0; i<1<<logSize; i++) {
			tt[0][i] = new TransitionTableEntry();
			tt[1][i] = new TransitionTableEntry();
		}
	}
	
	private void clearTT() {
		for (int i=0; i<1<<logSize; i++) {
			tt[0][i].clear();
			tt[1][i].clear();
		}
	}
	
	
	private void printRow(int r) {
		System.out.print("    ");
		for (int i=0; i<7; i++)
			System.out.print((char)(((r >> i) & 1) + '0'));
		System.out.println();
	}
	
	private void print(long l) {
		System.out.println("" + l + ": ");
		if ((l & 0x1FC0000000000L) != 0) printRow((int)((l >> 21) >> 21));
		printRow((int)(((l >> 14) >> 21) & 0x7F));
		printRow((int)((l >> 28) & 0x7F));
		printRow((int)((l >> 21) & 0x7F));
		printRow((int)((l >> 14) & 0x7F));
		printRow((int)((l >> 7) & 0x7F));
		printRow((int)(l & 0x7F));
	}

	public void print() {
		System.out.println("Active player = " + (lastPlayer^1));
	    System.out.println();
	    //c.print();
	    for (int row=6; row>=1; row--){
	    	System.out.print(row+" | ");
	    	for (int column=1; column<8; column++) {
	    		long bit = 1L << (7*(row-1)+(column-1));
	    		System.out.print((board[0] & bit) != 0 ? "X " : ((board[1] & bit) != 0 ? "O " : "  "));
	    	}
	    	System.out.println("|");
	    }
	    System.out.println("  \\---------------/");
	    System.out.println("    1 2 3 4 5 6 7");
	    System.out.print("Moves: "); printMoves(); System.out.println();
		/*
		System.out.println("Player 0:");
		print(board[0]);
		System.out.println("Player 1:");
		print(board[1]);
		*/
		System.out.println("Hash value = "+ hashValue);
	}
	
	
	public void printMoves() {
		for (int i=1; i<=numMovesPlayed; i++)
			System.out.print(" " + (1 + movesPlayed[i] % 7) + "-" + (1 + movesPlayed[i] / 7) + "");
	}
}
