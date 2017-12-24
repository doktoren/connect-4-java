/**
 * <code>ComputerPlayer</code> implements a connect 4 playing engine.
 * The argument passed to the constructor specializes the engine to a specific variant of the game.
 *
 * @author Jesper Kristensen
 * @see TransitionTableEntry
 * @see HashTable
 * @see TTE2
 */
public class ComputerPlayer implements ConnectFourListener, ConnectFourPlayer, ConnectFourRules {
	
	private static final long MAX_EVALUATIONS = 20000L;
	
	// logSize >= 17 MUST be satisfied!
	// WARNING! If changed, then opening libraries must be rebuild!
	static final int logSize = 17;
	
	// p controls how much is printed.
	private boolean p = false;
	
	// Whether to stop search when a previously played position has been reached -
	// also when no win/loss information is available.
	private boolean useSemiOracles = true;
	
	// Is true if a 7x6 board is used and false if a 4x4x4 board is used.
	private boolean normalBoard;
	private int gameVariant;
	
	// numColumns is 7 or 16
	private int numColumns;
	// boardSize is 42 or 64
	private int boardSize;
	
	public static final int INFINITE_VALUE = 32760;
	public static final int GUARANTEED_WIN = INFINITE_VALUE - 64;
	
	private static final boolean USE_TT = true;

	static long emptyKey0;
	static int emptyKey1;
	
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
	
	private static final long[] THREE_D_PATTERNS = {
		281479271743489L, 4503668347895824L, 72058693566333184L, 1152939097061330944L, 
		4369L, 286326784L, 18764712116224L, 1229764173248856064L, 1152922604119523329L, 
		281543712968704L, 562958543486978L, 9007336695791648L, 144117387132666368L, 
		2305878194122661888L, 8738L, 572653568L, 37529424232448L, 2459528346497712128L, 
		2305845208239046658L, 563087425937408L, 1125917086973956L, 18014673391583296L, 
		288234774265332736L, 4611756388245323776L, 17476L, 1145307136L, 
		75058848464896L, 4919056692995424256L, 4611690416478093316L, 1126174851874816L, 
		2251834173947912L, 36029346783166592L, 576469548530665472L, 
		-9223231297218904064L, 34952L, 2290614272L, 150117696929792L, 
		-8608630687718703104L, -9223363240753364984L, 2252349703749632L, 15L, 983040L, 
		64424509440L, 4222124650659840L, 2251816993685505L, 281483566907400L, 240L, 
		15728640L, 1030792151040L, 67553994410557440L, 36029071898968080L, 
		4503737070518400L, 3840L, 251658240L, 16492674416640L, 1080863910568919040L, 
		576465150383489280L, 72059793128294400L, 61440L, 4026531840L, 263882790666240L, 
		-1152921504606846976L, -9223301667573723136L, 1152956690052710400L, 33825L, 
		4680L, 2216755200L, 306708480L, 145277268787200L, 20100446945280L, 
		-8925852986471612416L, 1317302891005870080L, -9223367638806167551L, 
		281612482805760L, 2252074725150720L, 1152923703634296840L};
	
	private final long[] PATTERNS;
	
	// Used to maintain hash value of symmetric position
	private static final byte[] REFLECT =
		{6, 5, 4, 3, 2, 1, 0,
		13,12,11,10, 9, 8, 7,
		20,19,18,17,16,15,14,
		27,26,25,24,23,22,21,
		34,33,32,31,30,29,28,
		41,40,39,38,37,36,35,
		48,47,46,45,44,43,42};

	private static byte[][] REFLECT_3D;
	private static final byte[][] _REFLECT_3D = {
		{ 0, 1, 2, 3,
		  4, 5, 6, 7,
		  8, 9,10,11,
		 12,13,14,15},
		{12, 8, 4, 0,
		 13, 9, 5, 1,
		 14,10, 6, 2,
		 15,11, 7, 3},
		{15,14,13,12,
	     11,10, 9, 8,
	      7, 6, 5, 4,
	      3, 2, 1, 0},
	    { 3, 7,11,15,
	      2, 6,10,14,
	      1, 5, 9,13,
	      0, 4, 8,12},
	    { 3, 2, 1, 0,
	      7, 6, 5, 4,
	     11,10, 9, 8,
	     15,14,13,12},
	    {15,11, 7, 3,
	     14,10, 6, 2,
	     13, 9, 5, 1,
	     12, 8, 4, 0},
	    {12,13,14,15,
	      8, 9,10,11,
	      4, 5, 6, 7,
	      0, 1, 2, 3},
	    { 0, 4, 8,12,
	      1, 5, 9,13,
	      2, 6,10,14,
	      3, 7,11,15}};

	private int indexTTPattern;
	private int indexTT;
	private long key0;
	private int key1;
	// Stacks
	private int[] indexTTStack;
	private long[] key0Stack;
	private long[][] key0SetStack;
	private int[] key1Stack;
	private int[][] key1SetStack;
	
	private long[] fKey0;
	private int[] fKey1;
	
	private long[] board;
	
	private int[] insertPos;
	
	// 'O' is 0, 'X' is 1. 
	private int lastPlayer;
	
	// Warning! First move indexed 1.
	private int[] movesPlayed;
	private int numMovesPlayed;
	private int rootDepth;
	
	private int[] patternIndex;
	private long[] patterns;
	private int[] patternRef;
	
	// one index for each pattern.
	// 'O' counts 1b, 'X' counts 100b
	private int[] completionCount;
	// Relative for 'O' (good is positive)
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
	
	/**
	 * The constructor binds the engine to a specific connect 4 playing variant.
	 * 
	 * @param gameVariant 0 is the normal connect 4, 1 is with wrap around and bounce and 2 is the square version.
	 * @throws IllegalArgumentException if gameVariant is not 0, 1 or 2.
	 */
	public ComputerPlayer(int gameVariant) throws IllegalArgumentException {
		if (gameVariant<0  ||  3<gameVariant)
			throw new IllegalArgumentException("gameVariant must be 0, 1 or 2.");

		this.gameVariant = gameVariant;
		normalBoard = gameVariant != 3;
		
		if (normalBoard) {
			boardSize = 42;
			numColumns = 7;
			indexTTPattern = ((1 << (logSize-17)) - 1) << 17;
			
			//indexSet = new int[2];
			indexTTStack = new int[boardSize+1];
			key0Stack = new long[boardSize+1];
			key0SetStack = new long[boardSize+1][2];
		} else {
			boardSize = 64;
			numColumns = 16;
			indexTTPattern = ((1 << (logSize-16)) - 1) << 16;

			indexTTStack = new int[boardSize+1];
			
			key0Stack = new long[boardSize+1];
			key0SetStack = new long[boardSize+1][8];
			
			key1Stack = new int[boardSize+1];
			key1SetStack = new int[boardSize+1][8];
		}
		
		switch(gameVariant){
		case 0:
			PATTERNS = CONNECT_4_PATTERNS;
			openingLibrary = new HashTable("openingDatabase_connect_4.dat");
			break;
		case 1:
			PATTERNS = WRAP_AND_BOUNCE_PATTERNS;
			openingLibrary = new HashTable("openingDatabase_wrap_and_bounce.dat");
			break;
		case 2:
			PATTERNS = SQUARE_PATTERNS;
			openingLibrary = new HashTable("openingDatabase_square.dat");
			break;
		case 3:
			PATTERNS = THREE_D_PATTERNS;
			openingLibrary = new HashTable("openingDatabase_3D.dat");
			break;
		default:
			PATTERNS = null; // Can't occur!
		}
		
		board = new long[2];
		insertPos = new int[numColumns];
		movesPlayed = new int[boardSize + 1];
		completionCount = new int[PATTERNS.length];
		
		initHashValues();
		initPatterns();
		initTT();
		initReflections();
		
		openingLibrary.load();
	}
	

	// 'O' is 0, 'X' is 1. 
	public int getActivePlayer() {
		return lastPlayer^1;
	}
	
	public int getGameVariant() {
		return gameVariant;
	}
	
	public int getMovesPlayed() {
		return numMovesPlayed;
	}
	
	public void quit() {
		// ignore
	}
	
	/**
	 * @see Connect4Listener#newGame()
	 */
	public void newGame() {
		key0 = emptyKey0;
		if (normalBoard) {
			key1 = 0;
			key0Stack[0] = key0SetStack[0][0] = key0SetStack[0][0] = key0;
			indexTT = (int)key0 | ((int)(key0 >> 32) & indexTTPattern);
		} else {
			key1 = emptyKey1;
			for (int i=0; i<8; i++) {
				key0SetStack[0][i] = key0;
				key1SetStack[0][i] = key1;
			}
			key0Stack[0] = key0;
			key1Stack[0] = key1;
			indexTT = key1 | (((int)key0) & indexTTPattern);
		}
		
		board[0] = board[1] = 0;
		for (int i=0; i<numColumns; i++)
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
			
			// TODO: Continue here
			int alpha = -INFINITE_VALUE + (numMovesPlayed + thinkDepth);
			int beta = INFINITE_VALUE - (numMovesPlayed + thinkDepth);
			
			if (p) System.out.println("thinkDepth = " + thinkDepth + ":");

			TransitionTableEntry entry = recurseMove(thinkDepth, alpha, beta);
			if (p) System.out.println(entry);
			
			/*
			if (value > -INFINITE_VALUE+boardSize) {
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
			
			if (!continueSearch  ||  numMovesPlayed+thinkDepth == boardSize) {
				// if (p) System.out.println("Computer plays column " + (entry.bestMove+1));
				return entry;
			}
			
		} while (true);
	}
	
	
	/**
	 * Ask the engine for a move in the given position.
	 * 
	 * @param allowedComputing determines how deeply the engine may examine the position (measured in number of positions visited during the search).
	 * @return the index of the column in which to put the piece (between 0 and 6).
	 * @throws RuntimeException if no move can be made (game has ended).
	 */
	public int calculateMove(long allowedComputing) throws RuntimeException {
		if (isGameOver()  ||  numMovesPlayed == boardSize)
			throw new RuntimeException ("calculateMove: Game has terminated.");
		
		System.out.println("calculateMove(" + allowedComputing + "):");
		
		// boolean lastP = p;
		// p = true;
		int move = internalCalculateMove(allowedComputing).bestMove;
		// p = lastP;

		return move;
	}
	
	public int requestMove() {
		return calculateMove(MAX_EVALUATIONS);
	}
	
	/**
	 * Lets the engine improve the opening database through repeatedly self play.
	 * This method terminates only if the game has been solved for the starting position.
	 * 
	 * @param allowedComputing determines how deeply the engine may examine each position (measured in number of positions visited during the search).
	 */
	public void selfPlay() {
		long allowedComputing = MAX_EVALUATIONS;
		
		boolean printMore = false;
		
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
					
					TTE2 entry2 = new TTE2(key0, key1, entry.value);
					openingLibrary.insert(entry2);
					score = entry.value > 0 ? 1 : (entry.value < 0 ? -1 : 0);
					{
						System.out.println("################################");
						if (score == 0) {
							System.out.println("########   A DRAW!!!   #########");
						} else {
							int p = lastPlayer;
							if (score < 0) p ^= 1;
							if (p == 0) System.out.println("######    X WINS!!!!   #########");
							if (p == 1) System.out.println("######    O WINS!!!!   #########");
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
					if (printMore) System.out.println("key0 = " + key0 + ", key1 = " + key1 + ", numMovesPlayed = " + numMovesPlayed);
				}
			}
			
			while (numMovesPlayed > 0) {
				undo();
				if (printMore) System.out.println("key0 = " + key0 + ", key1 = " + key1 + ", numMovesPlayed = " + numMovesPlayed);
				score = -score;
				
				// See if search can be improved now
				clearTT();
				//boolean oldValue = useSemiOracles;
				//useSemiOracles = false;
				TransitionTableEntry entry = internalCalculateMove(allowedComputing);
				//useSemiOracles = oldValue;

				// The search might did not see an actual winning play
				if (entry.value < lastValue)
					entry.value = lastValue;
				lastValue = (short)-entry.value;
				
				if (entry.isGameTheoreticalValue()) {
					System.out.println("Added game theoretical value!");
					print();
					System.out.println(entry);
					System.out.flush();
					
					TTE2 entry2 = new TTE2(key0, key1, entry.value);
					openingLibrary.insert(entry2);

					
				} else if (entry.value <= -GUARANTEED_WIN  ||  GUARANTEED_WIN <= entry.value) {
					// We know that the game is lost or won in at most n moves, but
					// this is not necessarily optimal.
					System.out.println("Added won/lost value!");
					print();
					System.out.println(entry);
					System.out.flush();
					
					TTE2 entry2 = openingLibrary.lookup(key0, key1);
					
					if (entry2 == null) {
						openingLibrary.insert(new TTE2(key0, key1, entry.value, false));
					} else {
						entry2.setLostOrWonValue(entry.value);
					}
					
				} else {
					
					TTE2 entry2 = openingLibrary.lookup(key0, key1);
					
					if (entry2 == null) {
						openingLibrary.insert(new TTE2(key0, key1, score));
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
		
		if (numMovesPlayed == boardSize)
			return new TransitionTableEntry(key0, (short)0,
					TransitionTableEntry.EXACT, TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false);
		if (depth <= 0) {
			int value = evaluate();
			return new TransitionTableEntry(key0, (short)value,
					TransitionTableEntry.EXACT, (byte)0, (byte)-1, false);
		}
		
		{ // Lookup opening library
			TTE2 entry2 = openingLibrary.lookup(key0, key1);
			if (entry2 != null) {
				if (entry2.isGameTheoreticalValue())
					return new TransitionTableEntry(key0, entry2.getValue(),
							TransitionTableEntry.EXACT, TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false);
				
				if (useSemiOracles  ||
						(entry2.isWonLostValue()  &&  (entry2.getValue() <= alpha  ||  beta <= entry2.getValue())))
					return new TransitionTableEntry(key0, entry2.getValue(),
							TransitionTableEntry.EXACT, (byte)(boardSize - numMovesPlayed), (byte)-1, false);
			}
		}
		
		{ // Lookup transition table
			
			TransitionTableEntry entry = null;
			if (tt[0][indexTT].key == key0)
				entry = tt[0][indexTT];
			if (tt[1][indexTT].key == key0)
				entry = tt[1][indexTT];
			
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
			if (tt[0][indexTT].key == key0)
				entry = tt[0][indexTT];
			if (tt[1][indexTT].key == key0)
				entry = tt[1][indexTT];
			
			if (entry == null  ||  entry.searchDepth < depth-2  ||  entry.bestMove == -1) {
				recurseMove(depth-2, alpha, beta);
				entry = tt[depth-2 <= cutDepth ? 0 : 1][indexTT];
				// entry.bestMove might still be undefined (-1)
			}
			lastDepthBestMove = entry.bestMove;
		}
		
		TransitionTableEntry entry = new TransitionTableEntry(key0, (short)-INFINITE_VALUE,
				TransitionTableEntry.UPPER, (byte)TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false);
		
		int[] perm = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		if (lastDepthBestMove != -1) {
			perm[0] = lastDepthBestMove;
			perm[lastDepthBestMove] = 0;
		}
		for (int ii=0; ii<numColumns; ii++) {
			int i = perm[ii];
			if (insertPos[i] < boardSize) {
				
				put(i);
				TransitionTableEntry next = gameOver() ? 
						new TransitionTableEntry(key0, (short)(INFINITE_VALUE-numMovesPlayed),
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

		if (USE_TT) {
			// Update transition table
			// Don't care whats already in the transition table entry!
			
			tt[0][indexTT] = entry;

			if (depth > cutDepth)
				tt[1][indexTT] = entry;
		}
		
		// max may be smaller than alpha - usefull in connection with a transition table.
		return entry;
	}
	
	// Returns the best move. May not be called with depth < 1.
	// The value is set too, but it might be based on a lower search depth!
	private TransitionTableEntry recurseMove(int depth, int alpha, int beta) {

		// if (numForcedMoves >) ...
		
		{ // Lookup transition table
			
			TransitionTableEntry entry = null;
			if (tt[0][indexTT].key == key0)
				entry = tt[0][indexTT];
			if (tt[1][indexTT].key == key0)
				entry = tt[1][indexTT];
			
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
			if (tt[0][indexTT].key == key0)
				entry = tt[0][indexTT];
			if (tt[1][indexTT].key == key0)
				entry = tt[1][indexTT];
			
			if (entry != null  &&  entry.forcedMove)
				return entry;
			
			if (entry == null  ||  entry.searchDepth < depth-2  ||  entry.bestMove == -1) {
				recurseMove(depth-2, alpha, beta);
				entry = tt[depth-2 <= cutDepth ? 0 : 1][indexTT];
				// entry.bestMove might still be undefined (-1)
			}
			lastDepthBestMove = entry.bestMove;
		}
		
		TransitionTableEntry entry = new TransitionTableEntry(key0, (short)-INFINITE_VALUE,
				TransitionTableEntry.UPPER, (byte)TransitionTableEntry.INFINITE_DEPTH, (byte)-1, false);
		
		int numNonLosingMoves = 0;
		
		int[] perm = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		if (lastDepthBestMove != -1) {
			perm[0] = lastDepthBestMove;
			perm[lastDepthBestMove] = 0;
		}
		for (int ii=0; ii<numColumns; ii++) {
			int i = perm[ii];
			if (insertPos[i] < boardSize) {
				
				put(i);
				TransitionTableEntry next = gameOver() ? 
						new TransitionTableEntry(key0, (short)(INFINITE_VALUE-numMovesPlayed),
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
		
		if (USE_TT) {
			// Update transition table
			// Don't care whats already in the transition table entry!
			
			tt[0][indexTT] = entry;

			if (depth > cutDepth)
				tt[1][indexTT] = entry;
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
	
	/**
	 * Performs an efficient check for game over.
	 * 
	 * @return true if the game has ended.
	 */
	public boolean isGameOver() {
		return numMovesPlayed > 0  &&  gameOver();
	}

	private int evaluate() {
		if (lastPlayer == 0) {
			// Current player is 'X'
			return -evalValue;
		} else {
			// Current player is 'O'
			return evalValue;
		}
	}
	
	
	/**
	 * Prints to <code>System.out</code> the information stored in the opening database for this position.
	 */
	public void showLibraryInfo() {
		TTE2 e2 = openingLibrary.lookup(key0, key1);
		if (e2 == null) {
			System.out.println("Library info not available in current position!");
			return;
		}
		e2.print();
	}
	
	public boolean canPut(int column) {
		return insertPos[column] < boardSize;
	}
	
	/**
	 * @see Connect4Listener#put()
	 * @throws RuntimeException if the column is already filled.
	 */
	public void put(int column) throws RuntimeException {
		if (insertPos[column] >= boardSize)
			throw new RuntimeException("The column is already filled.");
		
		++computingPerformed;
		/*
		if ((computingPerformed % 100) == 0) {
			if (((100*computingPerformed)/allowedComputing)*allowedComputing == 100*computingPerformed)
				System.out.print("#");
		}
		*/
		
		int pos = movesPlayed[++numMovesPlayed] = insertPos[column];
		insertPos[column] += numColumns;

		lastPlayer ^= 1;
		board[lastPlayer] |= 1L << pos;
		
		{ // Update hash values

			if (normalBoard) {
				
				key0SetStack[numMovesPlayed][0] = key0SetStack[numMovesPlayed-1][0] ^ fKey0[pos + numColumns];
				key0SetStack[numMovesPlayed][1] = key0SetStack[numMovesPlayed-1][1] ^ fKey0[REFLECT[pos + numColumns]];
				
				if (lastPlayer == 0) {
					key0SetStack[numMovesPlayed][0] ^= fKey0[pos];
					key0SetStack[numMovesPlayed][1] ^= fKey0[REFLECT[pos]];
				}

				key0Stack[numMovesPlayed] = key0 =
					key0SetStack[numMovesPlayed][0] < key0SetStack[numMovesPlayed][1] ?
					key0SetStack[numMovesPlayed][0] : key0SetStack[numMovesPlayed][1];
					
				indexTTStack[numMovesPlayed] = indexTT = (int)key0 | ((int)(key0 >> 32) & indexTTPattern);

			} else {
				
				if (lastPlayer == 0) {
					// Swap two bits.
					
					key0 = key0SetStack[numMovesPlayed][0] = key0SetStack[numMovesPlayed-1][0] ^
							fKey0[pos+numColumns] ^ fKey0[REFLECT_3D[0][pos]];
					key1 = key1SetStack[numMovesPlayed][0] = key1SetStack[numMovesPlayed-1][0] ^
							fKey1[pos+numColumns] ^ fKey1[REFLECT_3D[0][pos]];
					for (int i=1; i<8; i++) {
						key0SetStack[numMovesPlayed][i] = key0SetStack[numMovesPlayed-1][i] ^
								fKey0[REFLECT_3D[i][pos+numColumns]] ^ fKey0[REFLECT_3D[i][pos]];
						key1SetStack[numMovesPlayed][i] = key1SetStack[numMovesPlayed-1][i] ^
								fKey1[REFLECT_3D[i][pos+numColumns]] ^ fKey1[REFLECT_3D[i][pos]];
						if (key0SetStack[numMovesPlayed][i] <= key0  &&
								(key0SetStack[numMovesPlayed][i] != key0  ||  key1SetStack[numMovesPlayed][i] < key1)) {
							key1 = key1SetStack[numMovesPlayed][i];
							key0 = key0SetStack[numMovesPlayed][i];
						}
					}
					
				} else {
					// Swap one bit.
				
					key0 = key0SetStack[numMovesPlayed][0] = key0SetStack[numMovesPlayed-1][0] ^ fKey0[pos+numColumns];
					key1 = key1SetStack[numMovesPlayed][0] = key1SetStack[numMovesPlayed-1][0] ^ fKey1[pos+numColumns];
					for (int i=1; i<8; i++) {
						key0SetStack[numMovesPlayed][i] = key0SetStack[numMovesPlayed-1][i] ^ fKey0[REFLECT_3D[i][pos+numColumns]];
						key1SetStack[numMovesPlayed][i] = key1SetStack[numMovesPlayed-1][i] ^ fKey1[REFLECT_3D[i][pos+numColumns]];
						if (key0SetStack[numMovesPlayed][i] <= key0  &&
								(key0SetStack[numMovesPlayed][i] != key0  ||  key1SetStack[numMovesPlayed][i] < key1)) {
							key1 = key1SetStack[numMovesPlayed][i];
							key0 = key0SetStack[numMovesPlayed][i];
						}
					}
				}		
				
				key0Stack[numMovesPlayed] = key0;
				key1Stack[numMovesPlayed] = key1;
				
				indexTTStack[numMovesPlayed] = indexTT = key1 | (((int)key0) & indexTTPattern);
			}
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
	
	public boolean canUndo() {
		return numMovesPlayed != 0;
	}
	
	/**
	 * @see Connect4Listener#put()
	 * @throws RuntimeException if there is no move to undo.
	 */
	public void undo() throws RuntimeException {
		if (numMovesPlayed == 0)
			throw new RuntimeException("Can't undo - no moves has been played.");
		
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
		
		// TODO: % is expensive.
		insertPos[pos % numColumns] -= numColumns;
	
		numMovesPlayed--;
		
		{   // Update board description
			
			indexTT = indexTTStack[numMovesPlayed];
			key0 = key0Stack[numMovesPlayed];
			if (!normalBoard)
				key1 = key1Stack[numMovesPlayed];
		}
		
		board[lastPlayer] ^= 1L << pos;
		
		lastPlayer ^= 1;
	}
	
	public void test() {
		try {
			analysePosition();
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e);
		}
	}
	
	
	private void analysePosition() throws RuntimeException {
		if (boardSize != 42  ||  numColumns != 7)
			throw new RuntimeException("Illegal dimension of game for analysePosition.");
		
		// First version: Assumptions:
		// Only player O has threats!
		// Only the lowest threat in each column is considered.
		// There may not be any directly winning moves, ie. all threats have an empty square below.
		// Only even threats...
		
		
		/*
		 * Notation:
		 *   Threat (square) : x has a threat at position p if x completes a 4-row by putting a piece in p.
		 *   Even threat     : x has an even threat p if this threat "blocks" an even number of squares for !x
		 *   Odd threat      : guess
		 *   Turn threat     : An even threat that can be sacrificed to change the zugzwang.
		 *                     The opponent may not have a threat above that prevents the players intention of
		 *                     changing the zugwang. A turn threat do not provide any guarantee for the turn...
		 *   Safe turn threat: A turn threat where it is guaranteed that the other player can't gain from the
		 *                     turner being used.
		 * 
		 * We will assume that the board has an even number of squares (otherwise switch O and X).
		 * O is the player who needs an odd threat, X can do with an even threat.
		 * 
		 * Below we need to count the number of threats for O and X.
		 * Only the lowest threat in each column counts - except for an even X threat above and even O threat.
		 * If both players have a lowest threat in a column, it counts for both players
		 * (obviously this can't be a turn threat).
		 * 
		 * Let odd(x) denote the number of odd threats for player x. Similar for even, turn and safe.
		 * win(x) is a predicate saying whether x can win based on follow up play. Similar for lose.
		 * fill(x) says whether x will complete a 4-row during the follow-up play.
		 * turnOdd(x,n) : n of x's turn threats are being turned.
		 * nonTurn(x) is 1 if player x has at least one odd threat not being a turn threat and 0 otherwise.  
		 * 
		 * In the analysis of the follow-up play, the positions whose color is not dictated by the follow
		 * up strategy is given the most pessimistic color.
		 * 
		 * When sacrificing a turn threat, note that each player can enforce that the column is filled alternating.
		 * 
		 * Valid conclusion:
		 * win(O)  <=  !fill(X)  &&  odd(O)>0  &&  odd(X)==0
		 * win(X)  <=  !fill(O)  &&  (even(X)>0  ||  fill(X))  &&  odd(O)==0
		 * 
		 * win(O)  <=  !fill(X)  &&  turn(O)+nonTurn(O)>turn(X)  &&  turnOdd(O,turn(X))  &&  turnOdd(X,turn(X))
		 * 
		 * !win(O) <=  !fill(X)  &&  odd(O)==0
		 * !win(X) <=  !fill(O)  &&  odd(O)==1  &&  odd(X)==1
		 */
	
		
		// board
		long b = board[0] | board[1];
		
		int numThreats = 0;
		int[] threats = new int[7];
		
		// Find threats
		for (int column=0; column<7; column++) {
			boolean threatFoundInColumn = false;
			for (int row=5; row>0; row--) {
				int p = 7*row+column;
				if ((b & (1L << p)) == 0) {
					int stop = patternIndex[p + 1];
					for (int i=patternIndex[p]; i<stop; i++) {
						if ((board[1] & patterns[i]) == patterns[i]) {
							threats[numThreats] = p;
							if (!threatFoundInColumn) numThreats++;	
						}
					}
				}
			}
		}
		
		long column = 1L | (1L << 7) | (1L << 14) | (1L << 21) | (1L << 28) | (1L << 35);
		System.out.println("column:");
		print(column);
		
		// board contour
		long bC = (b << 7) ^ b ^ 0x7FL;
		System.out.println("bC:");
		print(bC);
		
		// upper lines, lower lines
		long bL = 0x7FL | (0x7FL << 14) | (0x7FL << 28);
		System.out.println("bL:");
		print(bL); 
		long bU = (0x7FL | (0x7FL << 14) | (0x7FL << 28)) << 7;
		System.out.println("bU:");
		print(bU);
		
		// b? (bQ) is the squares whose color is not determined by the follow-up strategy.
		long bQ = bU;
		for (int i=0; i<numThreats; i++)
			bQ ^= column << (threats[i]%7);
		bQ &= bC;
		System.out.println("bQ:");
		print(bQ);

		// follow up board
		long bF = b | bQ;
		for (int i=0; i<numThreats; i++)
			bF |= column << threats[i];
		bF = ~bF;
		System.out.println("bF:");
		print(bF);
		
		// new player boards
		//long b1 = board[1] | (bF & bU);
		long b0 = board[0] | (bF & bL) | bQ;
		System.out.println("b1:");
		print(b0);
		
		// See if player 1 has a chance of still completing a line
		for (int i=0; i<PATTERNS.length; i++) {
			//System.err.println("i = "+ i + ", ");
			//print(patterns[i]);
			// TODO: if ((~b & ~patterns[i]) == 0) return true;
			if ((b0 & PATTERNS[i]) == PATTERNS[i]) {
				System.out.println("Nothing can be deduced.");
				return;
			}
		}
		
		if (numThreats == 0) {
			System.out.println("Player O has a non-losing strategy.");
		} else {
			System.out.println("Player O has a winning strategy.");
		}
	}
	
	
	private void initHashValues() {
		if (normalBoard) {
			
			fKey0 = new long[80];
			fKey0[0] = 0x12C701D20654CL;
			fKey0[1] = 0x05B37B8EC22D5L;
			fKey0[2] = 0x0535941379Cf2L;
			fKey0[3] = 0x003CCF778392AL;
			fKey0[4] = 0x13FE3CBB8CF94L;
			fKey0[5] = 0x11D543FBD4E56L;
			fKey0[6] = 0x172225A5C5814L;
			fKey0[7] = 0x1B89EF727CA59L;
			fKey0[8] = 0x124DD28F5983EL;
			fKey0[9] = 0x12671071D7D85L;
			fKey0[10] = 0x1D7F2B3EE3E77L;
			fKey0[11] = 0x0BBCEC18E1390L;
			fKey0[12] = 0x034809CBAB084L;
			fKey0[13] = 0x1E4FBF3423B81L;
			fKey0[14] = 0x1028856CDDBB8L;
			fKey0[15] = 0x19CAC89F484B6L;
			fKey0[16] = 0x04975CFE12118L;
			fKey0[17] = 0x0ABE08E5FE685L;
			fKey0[18] = 0x1C23DC6FBC49DL;
			fKey0[19] = 0x078A716385093L;
			fKey0[20] = 0x18DB680F271B4L;
			fKey0[21] = 0x0159E33CED46DL;
			fKey0[22] = 0x0E8B1E0F81239L;
			fKey0[23] = 0x068A150190706L;
			fKey0[24] = 0x1A7D4598560CDL;
			fKey0[25] = 0x066768F8701EBL;
			fKey0[26] = 0x0A3B826061391L;
			fKey0[27] = 0x1455D065FA669L;
			fKey0[28] = 0x1FD0F9F5A134EL;
			fKey0[29] = 0x00AE9C053C166L;
			fKey0[30] = 0x1E772D50C4380L;
			fKey0[31] = 0x083D7CC2385BDL;
			fKey0[32] = 0x05EE70E982EA8L;
			fKey0[33] = 0x1EB18631A8D1EL;
			fKey0[34] = 0x01691B9927F64L;
			fKey0[35] = 0x10B8C775BC150L;
			fKey0[36] = 0x0D0969C538A62L;
			fKey0[37] = 0x0A051B33E4187L;
			fKey0[38] = 0x18E6C30E61A0DL;
			fKey0[39] = 0x0C425C0A25E59L;
			fKey0[40] = 0x02C5A40B8B7FFL;
			fKey0[41] = 0x1B0F5352B93D1L;
			fKey0[42] = 0x160639929EF27L;
			fKey0[43] = 0x11D6C2E4CD986L;
			fKey0[44] = 0x1289317FDB589L;
			fKey0[45] = 0x068BE4529C9B0L;
			fKey0[46] = 0x04B2EC62E6A22L;
			fKey0[47] = 0x0647693535332L;
			fKey0[48] = 0x17CFFF7FD1703L;
			
			for (int i=0; i<49; i++) {
				// swap low and high 32 bits
				fKey0[i] = (fKey0[i] << 32) | (fKey0[i] >>> 32);
			}
			
			emptyKey0 = fKey0[0] ^ fKey0[1] ^ fKey0[2] ^ fKey0[3] ^ fKey0[4] ^ fKey0[5] ^ fKey0[6];
			
		} else {
			
			fKey1 = new int[80];
			fKey0 = new long[80];
			fKey1[0] = 0xE8C4;
			fKey0[0] = 0x07297C369D63EDC3L;
			fKey1[1] = 0xE66A;
			fKey0[1] = 0x99BCDA2CD98CC9FCL;
			fKey1[2] = 0xE52C;
			fKey0[2] = 0xA36118E61C719237L;
			fKey1[3] = 0x4060;
			fKey0[3] = 0xF6892FD9D5D9D27BL;
			fKey1[4] = 0xF863;
			fKey0[4] = 0xBDDE2F4CE7D98BB3L;
			fKey1[5] = 0xAE11;
			fKey0[5] = 0x3508C9AE68CDFE11L;
			fKey1[6] = 0xBE4D;
			fKey0[6] = 0x3F6FDACBA19BC856L;
			fKey1[7] = 0xC12D;
			fKey0[7] = 0xD310381E21395A3CL;
			fKey1[8] = 0xBECC;
			fKey0[8] = 0x8FA9E7D8EBB76E85L;
			fKey1[9] = 0xBA81;
			fKey0[9] = 0xEEE395187397AF1FL;
			fKey1[10] = 0x53A9;
			fKey0[10] = 0x744299DD4286D6FFL;
			fKey1[11] = 0x95C6;
			fKey0[11] = 0xC547A41DEC80C1A7L;
			fKey1[12] = 0xB868;
			fKey0[12] = 0x7D17C98EE9652F19L;
			fKey1[13] = 0xBEB0;
			fKey0[13] = 0x7D15978DD32C100DL;
			fKey1[14] = 0xBAA3;
			fKey0[14] = 0xD20BD1FBD459A26EL;
			fKey1[15] = 0x0BB7;
			fKey0[15] = 0xE70705BDD319076FL;
			fKey1[16] = 0x73C7;
			fKey0[16] = 0x8B924B41C6104F20L;
			fKey1[17] = 0x7CD1;
			fKey0[17] = 0x50B66568067BC55BL;
			fKey1[18] = 0x81BE;
			fKey0[18] = 0x4989F0FCFC6B0D6AL;
			fKey1[19] = 0x5A81;
			fKey0[19] = 0x68CC8827C407ACD7L;
			fKey1[20] = 0x90E1;
			fKey0[20] = 0x2B1A3267A89FAF23L;
			fKey1[21] = 0x2FE3;
			fKey0[21] = 0xE52D90A061BF3F2FL;
			fKey1[22] = 0xD882;
			fKey0[22] = 0xDC431A29BC3083C7L;
			fKey1[23] = 0x6169;
			fKey0[23] = 0xDC2C12B3ECC47BB8L;
			fKey1[24] = 0xF5DB;
			fKey0[24] = 0x7A24A45DA0EAAD79L;
			fKey1[25] = 0x6D61;
			fKey0[25] = 0xD12828A023D9D624L;
			fKey1[26] = 0x91C9;
			fKey0[26] = 0xE8392A6ED518B2A1L;
			fKey1[27] = 0x4C23;
			fKey0[27] = 0xB7B1B0AB683CF11BL;
			fKey1[28] = 0xC984;
			fKey0[28] = 0xF8DAA323D1A7D061L;
			fKey1[29] = 0xAE1E;
			fKey0[29] = 0x02F4E924DFE0B8F2L;
			fKey1[30] = 0x86A0;
			fKey0[30] = 0x67A07C9E0CBDB55BL;
			fKey1[31] = 0xEA3F;
			fKey0[31] = 0x0E21A03E755E42D7L;
			fKey1[32] = 0x8D47;
			fKey0[32] = 0x3EF2AC0C8E4AE3B6L;
			fKey1[33] = 0x099F;
			fKey0[33] = 0x9D44F7A1DF7D83E8L;
			fKey1[34] = 0x61B4;
			fKey0[34] = 0x2D0B61025C9D3738L;
			fKey1[35] = 0xAC83;
			fKey0[35] = 0xD6DB7ED3C96A6E13L;
			fKey1[36] = 0x179B;
			fKey0[36] = 0xD23A1B183840A713L;
			fKey1[37] = 0x6D88;
			fKey0[37] = 0x43EF32C10C0FC0FDL;
			fKey1[38] = 0xA20C;
			fKey0[38] = 0xB80DA85701EFC505L;
			fKey1[39] = 0xF470;
			fKey0[39] = 0x133EA1220F5BAFC7L;
			fKey1[40] = 0x3A3F;
			fKey0[40] = 0xE0ED6D542D7CD9EEL;
			fKey1[41] = 0x16B0;
			fKey0[41] = 0x2F6FF36C03475E2FL;
			fKey1[42] = 0x38F3;
			fKey0[42] = 0xB741776C4F6C4710L;
			fKey1[43] = 0x70E3;
			fKey0[43] = 0xAD9887F0E136A142L;
			fKey1[44] = 0x7430;
			fKey0[44] = 0x1EE613C4E07AA190L;
			fKey1[45] = 0xBC20;
			fKey0[45] = 0x8C540B04AECA5802L;
			fKey1[46] = 0x2CDB;
			fKey0[46] = 0x9A1FD00A71194B72L;
			fKey1[47] = 0x1272;
			fKey0[47] = 0xB1C378C6EDEB57A1L;
			fKey1[48] = 0x68D5;
			fKey0[48] = 0xE52C728940778C80L;
			fKey1[49] = 0x2CDA;
			fKey0[49] = 0xB78AFA01CD14FAEEL;
			fKey1[50] = 0x7B8D;
			fKey0[50] = 0x53FC139D0E99E534L;
			fKey1[51] = 0x1E68;
			fKey0[51] = 0xECDE86B62DC6C9CCL;
			fKey1[52] = 0xEA41;
			fKey0[52] = 0x54360AC90DCBE232L;
			fKey1[53] = 0xAAD5;
			fKey0[53] = 0xBD66509A815F049EL;
			fKey1[54] = 0xA519;
			fKey0[54] = 0x9504D1BF5D024C83L;
			fKey1[55] = 0xE735;
			fKey0[55] = 0xCCD6EAC4CC5853C1L;
			fKey1[56] = 0x2603;
			fKey0[56] = 0x14579BAE9F1B6928L;
			fKey1[57] = 0xFC12;
			fKey0[57] = 0x335BF1223210DA43L;
			fKey1[58] = 0x6B26;
			fKey0[58] = 0xE3452CE33961E742L;
			fKey1[59] = 0x222F;
			fKey0[59] = 0x1AD9C9AFED617EAAL;
			fKey1[60] = 0xD793;
			fKey0[60] = 0x0F293D2035B3E78EL;
			fKey1[61] = 0x113E;
			fKey0[61] = 0xAB5772D33FA8C0E1L;
			fKey1[62] = 0x5F6E;
			fKey0[62] = 0x200CB7CA7D423E17L;
			fKey1[63] = 0xA830;
			fKey0[63] = 0x7C54F420CD2AE1AAL;
			fKey1[64] = 0xAAAB;
			fKey0[64] = 0x4392CA0637E5A8AEL;
			fKey1[65] = 0xBB17;
			fKey0[65] = 0xC3E6C44C1774B860L;
			fKey1[66] = 0x2A56;
			fKey0[66] = 0x9A4EB5F1034804F7L;
			fKey1[67] = 0x0287;
			fKey0[67] = 0x51A9494FE9EBE17BL;
			fKey1[68] = 0x4BEA;
			fKey0[68] = 0x4D49D858B5311E97L;
			fKey1[69] = 0xF942;
			fKey0[69] = 0x13EF8D963F5A97BCL;
			fKey1[70] = 0xBE6A;
			fKey0[70] = 0xA6B871A87A6B2BF5L;
			fKey1[71] = 0xB0FF;
			fKey0[71] = 0x7EA78D024EB477F8L;
			fKey1[72] = 0x986E;
			fKey0[72] = 0xC526A6D5A407783DL;
			fKey1[73] = 0xC81E;
			fKey0[73] = 0x47BAFEB69692C0A6L;
			fKey1[74] = 0xDE5F;
			fKey0[74] = 0x7868859ED15C8417L;
			fKey1[75] = 0x046F;
			fKey0[75] = 0x272257C7B6568E89L;
			fKey1[76] = 0x77E9;
			fKey0[76] = 0x79A3C48F75C84215L;
			fKey1[77] = 0x28A7;
			fKey0[77] = 0x3F6C73914B82D2A5L;
			fKey1[78] = 0x770B;
			fKey0[78] = 0x38B2770B7722EAB7L;
			fKey1[79] = 0xAFCC;
			fKey0[79] = 0xC20B8D3C2FED7A6FL;

			emptyKey1 = 0;
			emptyKey0 = 0;
			for (int i=0; i<16; i++) {
				emptyKey1 ^= fKey1[i];
				emptyKey0 ^= fKey0[i];
			}
		}
	}
	
	private void initPatterns() {
		patternIndex = new int[boardSize + 1];
		patterns = new long[4*PATTERNS.length];
		patternRef = new int[4*PATTERNS.length];
		int k = 0;
		long b = 1;
		for (int i=0; i<boardSize; i++) {
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
			//if ((i%numColumns)==6) System.out.println();
		}
		patternIndex[boardSize] = k;
	}
	
	private void initTT() {
		System.err.println("logSize = " + logSize);
		tt = new TransitionTableEntry[2][1 << logSize];
		for (int i=0; i<1<<logSize; i++) {
			tt[0][i] = new TransitionTableEntry();
			tt[1][i] = new TransitionTableEntry();
		}
	}
	
	private void initReflections() {
		REFLECT_3D = new byte[8][80];
		
		for (int r=0; r<8; r++)
			for (int p=0; p<80; p++)
				REFLECT_3D[r][p] = (byte)((p & ~0xF) | _REFLECT_3D[r][p & 0xF]);
	}
	
	private void clearTT() {
		for (int i=0; i<1<<logSize; i++) {
			tt[0][i].clear();
			tt[1][i].clear();
		}
	}
	
	
	private void printRow(int r) {
		System.out.print("    ");
		for (int i=0; i<numColumns; i++)
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

	
	private char read(int column, int row) {
		long bit = 1L << (numColumns*(row-1)+(column-1));
		return (board[0] & bit) != 0 ? 'O' : ((board[1] & bit) != 0 ? 'X' : ' ');
	}
	
	private char read(int x, int y, int z) {
		long bit = 1L << (x + 4*y + 16*z);
		return (board[0] & bit) != 0 ? 'O' : ((board[1] & bit) != 0 ? 'O' : '/');
	}
	
	/**
	 * Prints to <code>System.out</code> a textual representation of the current position.
	 * This includes the player to move and the move history so far.
	 */
	public void print() {
		System.out.println("Active player = " + (lastPlayer==0 ? 'X' : 'O'));
	    System.out.println();
	    
	    if (normalBoard) {
	    	for (int row=6; row>=1; row--){
	    		System.out.print(row+" | ");
	    		for (int column=1; column<8; column++) {
	    			System.out.print("" + read(column, row) + " ");
	    		}
	    		System.out.println("|");
	    	}
	    	System.out.println("  \\---------------/");
	    	System.out.println("    1 2 3 4 5 6 7");

	    } else {

	    	System.out.println("         "+read(0,3,3)+"        "+read(1,3,3)+"        "+read(2,3,3)+"        "+read(3,3,3)+"");
	    	System.out.println("        /        /        /        / ");
	    	System.out.println("       "+read(0,3,2)+"        "+read(1,3,2)+"        "+read(2,3,2)+"        "+read(3,3,2)+"  ");
	    	System.out.println("      /        /        /        /   ");
	    	System.out.println("     "+read(0,3,1)+"   "+read(0,2,3)+"    "+read(1,3,1)+"   "+read(1,2,3)+"    "+read(2,3,1)+"   "+read(2,2,3)+"    "+read(3,3,1)+"   "+read(3,2,3)+"");
	    	System.out.println("    /   /    /   /    /   /    /   / ");
	    	System.out.println(" D "+read(0,3,0)+"   "+read(0,2,2)+"    "+read(1,3,0)+"   "+read(1,2,2)+"    "+read(2,3,0)+"   "+read(2,2,2)+"    "+read(3,3,0)+"   "+read(3,2,2)+"  ");
	    	System.out.println("      /        /        /        /   ");
	    	System.out.println("     "+read(0,2,1)+"   "+read(0,1,3)+"    "+read(1,2,1)+"   "+read(1,1,3)+"    "+read(2,2,1)+"   "+read(2,1,3)+"    "+read(3,2,1)+"   "+read(3,1,3)+"");
	    	System.out.println("    /   /    /   /    /   /    /   / ");
	    	System.out.println(" C "+read(0,2,0)+"   "+read(0,1,2)+"    "+read(1,2,0)+"   "+read(1,1,2)+"    "+read(2,2,0)+"   "+read(2,1,2)+"    "+read(3,2,0)+"   "+read(3,1,2)+"  ");
	    	System.out.println("      /        /        /        /   ");
	    	System.out.println("     "+read(0,1,1)+"   "+read(0,0,3)+"    "+read(1,1,1)+"   "+read(1,0,3)+"    "+read(2,1,1)+"   "+read(2,0,3)+"    "+read(3,1,1)+"   "+read(3,0,3)+"");
	    	System.out.println("    /   /    /   /    /   /    /   / ");
	    	System.out.println(" B "+read(0,1,0)+"   "+read(0,0,2)+"    "+read(1,1,0)+"   "+read(1,0,2)+"    "+read(2,1,0)+"   "+read(2,0,2)+"    "+read(3,1,0)+"   "+read(3,0,2)+"  ");
	    	System.out.println("      /        /        /        /   ");
	    	System.out.println("     "+read(0,0,1)+"        "+read(1,0,1)+"        "+read(2,0,1)+"        "+read(3,0,1)+"    ");
	    	System.out.println("    /        /        /        /     ");
	    	System.out.println(" A "+read(0,0,0)+"        "+read(1,0,0)+"        "+read(2,0,0)+"        "+read(3,0,0)+"      ");
	    	System.out.println("   1        2        3        4         ");
	    }
	    
	    /*
		System.out.println("Player 0:");
		print(board[0]);
		System.out.println("Player 1:");
		print(board[1]);
		*/
    	System.out.print("Moves: "); printMoves();
		System.out.println("key0 = "+ key0 + ", key1 = " + key1);
	}
	
	/**
	 * Prints to <code>System.out</code> the sequence of moves made so far.
	 */
	public void printMoves() {
		if (normalBoard) {
			for (int i=1; i<=numMovesPlayed; i++)
				System.out.print(" " + (1 + movesPlayed[i] % numColumns) + "-" + (1 + movesPlayed[i] / numColumns) + "");
		} else {
			for (int i=1; i<=numMovesPlayed; i++)
				System.out.print(" " + (1 + (movesPlayed[i] & 3)) + "-" + (char)('A' + ((movesPlayed[i] >> 2) & 3)) + "-" + (1 + (movesPlayed[i] >> 4)) + "");
		}
		System.out.println();
	}
}
