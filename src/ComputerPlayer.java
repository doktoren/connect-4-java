
public class ComputerPlayer {
	
	static int infinite = 32760;
	
	static int log_size = 18;
	// empty_hash_value = f[0] ^ f[1] ^ f[2] ^ f[3] ^ f[4] ^ f[5] ^ f[6];
	static long empty_hash_value;
	
	private static long[] PATTERNS = {
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
	
	// LONG_POWERS contains all values of 2^i with i in [0...64[
	private static long[] LONG_POWERS = {
		0x1L, 0x2L, 0x4L, 0x8L, 0x10L, 0x20L, 0x40L, 0x80L,
		0x100L, 0x200L, 0x400L, 0x800L, 0x1000L, 0x2000L, 0x4000L, 0x8000L,
		0x10000L, 0x20000L, 0x40000L, 0x80000L, 0x100000L, 0x200000L, 0x400000L, 0x800000L,
		0x1000000L, 0x2000000L, 0x4000000L, 0x8000000L,
		0x10000000L, 0x20000000L, 0x40000000L, 0x80000000L,
		0x100000000L, 0x200000000L, 0x400000000L, 0x800000000L,
		0x1000000000L, 0x2000000000L, 0x4000000000L, 0x8000000000L,
		0x10000000000L, 0x20000000000L, 0x40000000000L, 0x80000000000L,
		0x100000000000L, 0x200000000000L, 0x400000000000L, 0x800000000000L,
		0x1000000000000L, 0x2000000000000L, 0x4000000000000L, 0x8000000000000L,
		0x10000000000000L, 0x20000000000000L, 0x40000000000000L, 0x80000000000000L,
		0x100000000000000L, 0x200000000000000L, 0x400000000000000L, 0x800000000000000L,
		0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L, 0x8000000000000000L};
	
	// Used to maintain hash value of symmetric position
	public static byte[] REFLECT =
		{6, 5, 4, 3, 2, 1, 0,
		13,12,11,10, 9, 8, 7,
		20,19,18,17,16,15,14,
		27,26,25,24,23,22,21,
		34,33,32,31,30,29,28,
		41,40,39,38,37,36,35,
		48,47,46,45,44,43,42};

	public ComputerPlayer() {
		board = new long[2];
		insert_pos = new int[7];
		moves_played = new int[42+1];
		completion_count = new int[PATTERNS.length];
		init_hash_values();
		init_patterns();
		init_tt();
	}
		
	public void newGame() {
		hv0 = hv1 = hash_value = empty_hash_value;
		board[0] = board[1] = 0;
		for (int i=0; i<7; i++)
			insert_pos[i] = i;
	
		last_player = 1;
		
		moves_played[0] = 99999; // Should NEVER get used.
		cant_undo_before = num_moves_played = 0;
		
		eval_value = 0;
	}
	
	public int calculateMove(long allowed_computing) {
		// update(_board);
		if (isGameOver()  ||  num_moves_played == 42) {
			System.out.println("Error: calculateMove: Game has terminated.");
			return 0;
		}
		
		System.out.println("calculateMove(" + allowed_computing + "):");
		
		int best_move = -1; // Will ALWAYS be overwritten.
		
		computing_performed = 0;
		int thinkDepth = 0;
		long last_computing_performed = 0;
		long last_thinking_required = 1;
		boolean continue_search = true;
		
		do {
			++thinkDepth;
			
			// TODO: fortsæt her
			int alpha = -infinite + (num_moves_played + thinkDepth);
			int beta = infinite - (num_moves_played + thinkDepth);
			
			int num_non_losing_moves = 0;
			
			System.out.println("thinkDepth = " + thinkDepth + ":");
			int max = -infinite;
			for (int i=0; i<7; i++) {
				if (insert_pos[i] < 42) {
					put(i);
					int value = gameOver() ? (infinite-num_moves_played) : -recurse(thinkDepth - 1, -beta, -alpha);
					undo();
					System.out.print("Move " + (i+1) + " has value ");
					if (value <= alpha) System.out.print("<= ");
					if (value >= beta) System.out.print(">= ");
					System.out.println("" + value);
					
					if (value > -infinite+42) {
						// Will not lose by playing this move
						++num_non_losing_moves;
					}
					
					if (value > alpha) {
						alpha = value;
						
						if (value > max) {
							max = value;
							best_move = i;
							if (value >= beta) {
								// Optimal move found.
								continue_search = false;
								break;
							}
						}
					}
				}
			}
			System.out.println("computing_performed = " + computing_performed + ":");
			
			if (max <= -infinite + (num_moves_played + thinkDepth)) {
				System.out.println("The game is lost! Give up searching any further.");
				continue_search = false;
				break;
			}
			
			if (num_non_losing_moves == 1  &&  max < beta) {
				System.out.println("Only one reasonable move - play this!");
				continue_search = false;
				break;
			}
			
			double d = computing_performed - last_computing_performed;
			continue_search &= allowed_computing-computing_performed > d*d / last_thinking_required;
			last_thinking_required = computing_performed - last_thinking_required;
			last_computing_performed = computing_performed;
			
		} while (continue_search);
		
		System.out.println("Computer plays column " + (best_move+1));
		
		return best_move;
	}
	

	// Returns value relative to player
	private int recurse(int depth, int alpha, int beta) {
		if (num_moves_played == 42) return 0;
		if (depth == 0) return evaluate();
		

		if (true) { // Lookup transition table
			int index = (int)(hash_value >> 32);
			if (tt[index].hash_value == (int)hash_value) {
				// Either tt[index] is not initialized (and then it has search depth -1)
				// or otherwise it is the same position.
				if (tt[index].search_depth == depth) {
					
					// if (!tt[index].cmp(board)) System.out.println("Hash value error :-(");
					
					switch (tt[index].evaluation_type) {
					case 1: // EXACT
						return tt[index].value;
					case 2: // LOWER
						if (tt[index].value < alpha)
							return tt[index].value;
						break;
					case 3: // UPPER
						if (tt[index].value >= beta)
							return tt[index].value;
						break;
					}
				}
			}
		}
		
		
		int max = -infinite;
		for (int i=0; i<7; i++) {
			if (insert_pos[i] < 42) {
				
				int value = 0;
				boolean used_tt = false;
				if (false) { // probe transition table

					long hv_after_move = hash_value ^ f[insert_pos[i] + 7];
					if (last_player == 1) hv_after_move ^= f[insert_pos[i]];
						
					int index = (int)(hv_after_move >> 32);
					if (tt[index].hash_value == (int)hv_after_move) {
						// Either tt[index] is not initialized (and then it has search depth -1)
						// or otherwise it is the same position.
						if (tt[index].search_depth == depth) {
							value = tt[index].value;
							
							switch (tt[index].evaluation_type) {
							case 1: // EXACT
								used_tt = true;
								break;
							case 2: // LOWER, ie. upper (looking one move ahead).
								used_tt |= value < alpha;
								break;
							case 3: // UPPER, ie. lower (looking one move ahead).
								used_tt |= value >= beta;
								break;
							}
						}
					}
				}
				
				if (!used_tt) {
					put(i);
					value = gameOver() ? (infinite-num_moves_played) : -recurse(depth - 1, -beta, -alpha);
					//System.out.print("recurse("+depth+"): ");
					//print_moves();
					//System.out.println(". Value "+value);
					undo();
				}
				
				if (value > max) {
					max = value;
					
					if (value > alpha) {
						alpha = value;
						
						if (value >= beta) {
							break;
							//return value;
						}
					}
				}
			}
		}
		
		if (true) { // Update transition table
			// Don't care whats already in the transition table entry!

			int index = (int)(hash_value >> 32);
			tt[index].hash_value = (int)hash_value;
			
			tt[index].value = (short)max;
			tt[index].search_depth = (byte)depth;
			
			if (max <= alpha) tt[index].evaluation_type = 3; // UPPER BOUND
			else if (max < beta) tt[index].evaluation_type = 1; // EXACT
			else tt[index].evaluation_type = 2; // LOWER BOUND
			
			tt[index].board0 = board[0];
			tt[index].board1 = board[1];
		}
		
		// max may be smaller than alpha - usefull in connection with a transition table.
		return max;
	}
	
	// WARNING! May only be called if the last move is stored.
	private boolean gameOver() {
		int stop = pattern_index[moves_played[num_moves_played] + 1];
		long b = board[last_player];
		//print(b);
		for (int i=pattern_index[moves_played[num_moves_played]]; i<stop; i++) {
			//System.out.println("i = "+ i + ", ");
			//print(patterns[i]);
			// TODO: if ((~b & ~patterns[i]) == 0) return true;
			if ((b & patterns[i]) == patterns[i]) return true;
		}
		return false;
	}
	
	public boolean isGameOver() {
		// Assume we don't know where the last piece was placed (otherwise gameOver() could just be called).
		for (int j=0; j<7; j++) {
			//System.out.println("insert_pos[" + i + "] = " + insert_pos[i]);
			if (7 <= insert_pos[j]  &&
					(board[last_player]  &  (LONG_POWERS[insert_pos[j]-7])) != 0) {
				
				int last_move = insert_pos[j]-7;
				int stop = pattern_index[last_move + 1];
				long b = board[last_player];
				//print(b);
				for (int i=pattern_index[last_move]; i<stop; i++) {
					//System.out.println("i = "+ i + ", ");
					//print(patterns[i]);
					if ((b & patterns[i]) == patterns[i]) {
						//System.out.println("C4 number " + pattern_ref[i] + " on square " + (j+1) + "-" + (insert_pos[j]/7));
						//print(board[0]);
						//print(board[1]);
						//print(patterns[i]);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private int evaluate() {
		if (last_player == 0) {
			// Current player is 'O'
			return -eval_value;
		} else {
			// Current player is 'X*
			return eval_value;
		}
	}
	
	
	// WARNING! No error checking!
	public void put(int column) {
		++computing_performed;
		
		int pos = moves_played[++num_moves_played] = insert_pos[column];
		insert_pos[column] += 7;

		last_player ^= 1;
		board[last_player] |= LONG_POWERS[pos];
		
		{ // Update hash values
			if (last_player == 0) {
				hv0 ^= f[pos];
				hv1 ^= f[REFLECT[pos]];
			}
			hv0 ^= f[pos + 7];
			hv1 ^= f[REFLECT[pos + 7]];
			hash_value = hv0 < hv1 ? hv0 : hv1;
		}
		
		{ // Adjust eval_value
			int adjustment = 1 << (last_player << 1);
			int stop = pattern_index[pos + 1];
			for (int i=pattern_index[pos]; i<stop; i++) {
				eval_value -= cc_values[completion_count[pattern_ref[i]]];
				//print_moves();
				//System.out.println(": put("+pos+"): " + pattern_ref[i] + ": " + completion_count[pattern_ref[i]] + " += " + adjustment);
				completion_count[pattern_ref[i]] += adjustment;
				eval_value += cc_values[completion_count[pattern_ref[i]]];
			}
		}
	}
	
	public void undo() {
		//if (num_moves_played <= cant_undo_before)
		//	return false;
		
		int pos = moves_played[num_moves_played];

		{ // Adjust eval_value
			int adjustment = 1 << (last_player << 1);
			int stop = pattern_index[pos + 1];
			for (int i=pattern_index[pos]; i<stop; i++) {
				eval_value -= cc_values[completion_count[pattern_ref[i]]];
				//print_moves();
				//System.out.println(": remove("+pos+"): " + pattern_ref[i] + ": " + completion_count[pattern_ref[i]] + " -= " + adjustment);
				completion_count[pattern_ref[i]] -= adjustment;
				eval_value += cc_values[completion_count[pattern_ref[i]]];
			}
		}
		
		num_moves_played--;
		
		// TODO: % is expensive.
		insert_pos[pos % 7] -= 7;
		
		{   // Update hash values
			// TODO: Maybe it is faster to stack/pop these values.
			hv0 ^= f[pos+7];
			hv1 ^= f[REFLECT[pos+7]];
			if (last_player == 0) {
				hv0 ^= f[pos];
				hv1 ^= f[REFLECT[pos]];
			}
			hash_value = hv0 < hv1 ? hv0 : hv1;
		}
		
		board[last_player] ^= LONG_POWERS[pos];
		
		last_player ^= 1;
	}
	
	private void init_hash_values() {
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

		// The high 32 bit part of f will be used to index the transition table.
		// Copy (log_size-17) from the lower 32 bits to the higher 32 bits, such
		// that the entire range of the transition table is exploitet.
		long p = ((0x20000 << (log_size - 17)) - 0x20000) << 32;
		for (int i=0; i<49; i++) {
			f[i] |= (f[i] << 32) & p;
		}
		
		empty_hash_value = f[0] ^ f[1] ^ f[2] ^ f[3] ^ f[4] ^ f[5] ^ f[6];
	}
	
	private void init_patterns() {
		pattern_index = new int[4*PATTERNS.length + 1];
		patterns = new long[4*PATTERNS.length];
		pattern_ref = new int[4*PATTERNS.length];
		int k = 0;
		long b = 1;
		for (int i=0; i<42; i++) {
			pattern_index[i] = k;
			for (int j=0; j<PATTERNS.length; j++)
				if ((PATTERNS[j] & b) != 0) {
					// Remove the bit for the square itself
					patterns[k] = PATTERNS[j] ^ (1L << i);
					pattern_ref[k] = j;
					k++;
				}
			b <<= 1;
			System.out.print("("+i+":"+(pattern_index[i]-k)+")");
			if ((i%7)==6) System.out.println();
		}
		pattern_index[42] = k;
	}
	
	private void init_tt() {
		tt = new TransitionTableEntry[1 << log_size];
		for (int i=0; i<1<<log_size; i++)
			tt[i] = new TransitionTableEntry();
	}
	
	
	private void print_row(int r) {
		System.out.print("    ");
		for (int i=0; i<7; i++)
			System.out.print((char)(((r >> i) & 1) + '0'));
		System.out.println();
	}
	
	private void print(long l) {
		System.out.println("" + l + ": ");
		if ((l & 0x1FC0000000000L) != 0) print_row((int)((l >> 21) >> 21));
		print_row((int)(((l >> 14) >> 21) & 0x7F));
		print_row((int)((l >> 28) & 0x7F));
		print_row((int)((l >> 21) & 0x7F));
		print_row((int)((l >> 14) & 0x7F));
		print_row((int)((l >> 7) & 0x7F));
		print_row((int)(l & 0x7F));
	}

	public void print() {
		System.out.println("Active player = " + (last_player^1));
		System.out.println("Player 0:");
		print(board[0]);
		System.out.println("Player 1:");
		print(board[1]);
		System.out.println("Hash value = "+ hash_value);
	}
	
	
	public void print_moves() {
		if (cant_undo_before != 0)
			System.out.print("numP=" + cant_undo_before + ":");
		for (int i=cant_undo_before+1; i<=num_moves_played; i++)
			System.out.print(" " + (1 + moves_played[i] % 7) + "-" + (1 + moves_played[i] / 7) + "");
	}
	
	// hash_value is 49 bits long.
	private long hv0, hv1;
	// hash_value is the lexicographically least value representing a simple transformation of this position.
	private long hash_value;
	
	private long[] f;
	
	private long[] board;
	
	private int[] insert_pos;
	
	// 'X' is 0, 'O' is 1. 
	private int last_player;
	
	// Warning! First move indexed 1.
	private int[] moves_played;
	private int num_moves_played, cant_undo_before;
	
	private int[] pattern_index;
	private long[] patterns;
	private int[] pattern_ref;
	
	// one index for each pattern.
	// 'X' counts 1b, 'O' counts 100b
	private int[] completion_count;
	// Relative for 'X' (good is positive)
	static private int[] cc_values =
       {0,1,4,10,
		-1,0,0,0,
		-4,0,0,0,
		-10,0,0,0, 0}; // last element is a dummy
	private int eval_value;
	
	private long computing_performed;
	
	
	private TransitionTableEntry[] tt;
}
