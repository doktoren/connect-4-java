// Result of most successfull run:
/*
Attempt 17905:
    Max equal substring = 12
    Max zero substring = 7
    Least difference = 16
    Total difference = 57784
    Average difference = 24.568027210884352
Quality accepted!

		long f[] = new long[49];
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
 */

import java.util.Random;
import java.lang.String;


/**
 * Creates a bijective mapping f: [0...2<SUP>49</SUP>[ -> [0...2<SUP>49</SUP>[ satisfying
 * 1)   f(x xor y) = f(x) xor f(y)<BR>
 * 2)   f(2<SUP>k</SUP>) is a more or less random 49 bit number, ie. its binary representation
 *    contains a "nice" distribution of zeroes and ones.
 * <p>
 * Idea:<BR>
 * Choose f such that f(1b) = 1b, f(10b) = 11b, ..., f(100000b) = 111111b<BR>
 * This f will satisfy 1).<BR>
 * This f could also be defined as f(2<SUP>k</SUP>) = sum<SUB>i<=k</SUB> 2<SUP>i</SUP>
 * <p>
 * We have fixed the number of bits to 49, so let p be a permutation of {0, ..., 48}<BR>
 * For each p we have f<SUB>p</SUB> satisfying 1) given as follows:<BR>
 *   f<SUB>p</SUB>(2<SUP>k</SUP>) = sum<SUB>p(i) <= p(k)</SUB> 2<SUP>i</SUP>
 * <p>
 * Note that given f<SUB>p</SUB> and f<SUB>p'</SUB> we can define a 3. function f<SUB>p'</SUB> o f<SUB>p</SUB> satisfying 1):<BR>
 *   f<SUB>p'</SUB>(f<SUB>p</SUB>(x xor y)) = f<SUB>p'</SUB>(f<SUB>p</SUB>(x) xor f<SUB>p</SUB>(y)) = f<SUB>p'</SUB>(f<SUB>p</SUB>(x)) xor f<SUB>p'</SUB>(f<SUB>p</SUB>(y))
 * <p>
 * Now it is the hope that 2) can be achieved by composing a small number of these functions. eg.<BR>
 *   f<SUB>q</SUB> = f<SUB>p'''''</SUB> o f<SUB>p''''</SUB> o f<SUB>p'''</SUB> o f<SUB>p''</SUB> o f<SUB>p'</SUB> o f<SUB>p</SUB><BR>
 * Because of 1) f<SUB>q</SUB> is given on its values on the set {2<SUP>i</SUP>}
 * 
 * @author Jesper Kristensen
 */
public class HashValueClass {
	
	private void init49() {
		f = new long[49];
		f[0] = 1;
		for (int i=1; i<49; i++)
			f[i] = f[i-1] << 1;
			
		// Debug:
		rh = null;
		
		int last_quality = -999;
		for (int num=0; ; num++) {
			// Set g = f_p o f
			
			int[] p = getPermutation(49);
			// This definition of g can be moved outside the for loop if
			// the debug code is removed.
			long[] g = new long[49];
			
			/*
			for (int i=0; i<49; i++)
				System.out.print("(" + p[i] + ")");
			System.out.println();
			*/
			
			// Define g according to the permutation p
			for (int i=0; i<49; i++) {
				g[i] = 0;
				long b = 1;
				for (int j=0; j<49; j++) {
					if (p[j] <= p[i])
						g[i] |= b;
					b <<= 1;
				}
			}

			// Debug:
			rh = new ReverseHashing(rh, p, g);
			
			// for (int i=0; i<49; i++) f[i] = g[i];
			
			// Set f = g o f
			for (int i=0; i<49; i++) {
				long fi = f[i];
				f[i] = 0;
				// define f(2^i)
				for (int j=0; j<49; j++) {
					if (((int)fi & 1) != 0) {
						f[i] ^= g[j];
					}
					fi >>= 1;
				}
			}
			
			// estimate quality
			int d_least = 49;
			int d_sum = 0;
			int max_equal_substring = 0;
			int max_zero_substring = 0;
			for (int i=0; i<49; i++) {
				{
					int mzs = 0;
					long fi = f[i];
					
					for (int j=0; j<49; j++) {
						if (((int)fi & 1) == 0) {
							++mzs;
						} else {
							if (mzs > max_zero_substring) {
								max_zero_substring = mzs;
							}
							mzs = 0;
						}
						fi >>= 1;
					}
				}
				
				for (int j=0; j<49; j++) if (i!=j) {
					// higher value of difference, d, is better
					int d = 0;
					
					long a = f[i] ^ f[j];
					int mes = 0;
					for (int k=0; k<49; k++) {
						if (((int)a & 1) == 1) {
							++d;
							++mes;
						} else {
							if (mes > max_equal_substring) {
								max_equal_substring = mes;
							}
							mes = 0;
						}
						
						a >>= 1;
					}
					
					if (d < d_least) d_least = d;
					d_sum += d;
				}
			}
			
			int quality = d_least - max_equal_substring - max_zero_substring;
			if (quality > last_quality) {
				System.out.println("Attempt " + (num+1) + ":");
				System.out.println("    Max equal substring = " + max_equal_substring);
				System.out.println("    Max zero substring = " + max_zero_substring);
				System.out.println("    Least difference = " + d_least);
				System.out.println("    Total difference = " + d_sum);
				System.out.println("    Average difference = " + d_sum/(49.0*48));
				if (quality >= -5) {
					System.out.println("Quality accepted!");
					return;
				}
				last_quality = quality;
			}
		}
	}
	
	private void initExtended() {
		F = new byte[num][num];
		for (int i=0; i<num; i++)
			for (int j=0; j<num; j++)
				F[i][j] = 0;

		for (int i=0; i<num; i++)
			F[i][i] = 1;
		
		// Debug:
		erh = null;
		
		int last_quality = -999999999;
		for (int numIt=0; ; numIt++) {
			// Set g = f_p o f
			
			int[] p = getPermutation(num);
			// This definition of g can be moved outside the for loop if
			// the debug code is removed.
			byte[][] G = new byte[num][num];
			
			/*
			 for (int i=0; i<49; i++)
			 System.out.print("(" + p[i] + ")");
			 System.out.println();
			 */
			
			// Define g according to the permutation p
			for (int i=0; i<num; i++) {
				for (int j=0; j<num; j++)
					G[i][j] = 0;

				for (int j=0; j<num; j++) {
					if (p[j] <= p[i]) {
						G[i][j] = 1;
					}
				}
			}
			
			// Debug:
			erh = new ExtendedReverseHashing(erh, p, G);
			
			// for (int i=0; i<49; i++) f[i] = g[i];
			
			// Set f = g o f
			for (int i=0; i<num; i++) {
				byte[] FI = new byte[num];
				for (int j=0; j<num; j++) {
					FI[j] = F[i][j];
					F[i][j] = 0;
				}
				
				// define f(2^i)
				for (int j=0; j<num; j++) {
					if (FI[j] != 0) {
						for (int k=0; k<num; k++)
							F[i][k] ^= G[j][k];
					}
				}
			}
			
			// estimate quality
			int d_least = 49;
			int d_sum = 0;
			int max_equal_substring = 0;
			int max_zero_substring = 0;
			for (int i=0; i<49; i++) {
				{
					int mzs = 0;
					byte[] FI = new byte[num];
					for (int j=0; j<num; j++)
						FI[j] = F[i][j];
					
					for (int j=0; j<num; j++) {
						if (FI[j] != 0) {
							++mzs;
						} else {
							if (mzs > max_zero_substring) {
								max_zero_substring = mzs;
							}
							mzs = 0;
						}
					}
				}
				
				for (int j=0; j<num; j++) if (i!=j) {
					// higher value of difference, d, is better
					int d = 0;
					
					int mes = 0;
					for (int k=0; k<49; k++) {
						if (F[i][k] != F[j][k]) {
							++d;
							++mes;
						} else {
							if (mes > max_equal_substring) {
								max_equal_substring = mes;
							}
							mes = 0;
						}
					}
					
					if (d < d_least) d_least = d;
					d_sum += d;
				}
			}
			
			int quality = d_least - max_equal_substring - max_zero_substring;
			if (quality > last_quality) {
				System.out.println("Attempt " + (numIt+1) + ":");
				System.out.println("    Max equal substring = " + max_equal_substring);
				System.out.println("    Max zero substring = " + max_zero_substring);
				System.out.println("    Least difference = " + d_least);
				System.out.println("    Total difference = " + d_sum);
				System.out.println("    Average difference = " + d_sum/(49.0*48));
				if (quality >= -7) { // -7
					System.out.println("Quality accepted!");
					return;
				}
				last_quality = quality;
			}
		}
	}
	
	/**
	 * This constructor constructs the bijective mapping f.
	 * It is an randomized algorithm, so the mapping produced will vary.<BR>
	 * Some status information will be written to <code>System.out</code>.
	 */
	public HashValueClass(boolean normalGame) {
		useExtended = !normalGame;
		
		if (normalGame) {
			init49();
		} else {
			num = 80;
			initExtended();
		}
	}
	
	/**
	 * Writes a java definition of an array containing the values {f(2<SUP>0</SUP>), ..., f(2<SUP>48</SUP>)} to <code>System.out</code>.
	 * The function is represented as a 49 entry array of <code>long</code>s,
	 * such that <code>f[i]</code> gives the value of f(2^i).
	 */
	public void print() {
		final byte C[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		
		if (!useExtended) {

			System.out.println("long[] fKey = new long[49];");
			for (int i=0; i<49; i++) {
				long fi = f[i];
				byte[] B = new byte[13];
				for (int j=0; j<13; j++) {
					B[12-j] = C[(int)fi & 0xF];
					fi >>= 4;
				}
				System.out.println("fKey["+i+"] = 0x" + new String(B) + "L;");
			}
			System.out.println();
			for (int i=0; i<49; i++) {
				byte[] b = new byte[49];
				
				for (int j=0; j<49; j++)
					b[48 - j] = (byte)(48 + (i==j ? 1 : 0));
				System.out.print("f(" + new String(b) + ") = ");
				
				System.out.println(longToString(f[i]));
			}
			
		} else if (num == 80) {
			
			System.out.println("int[] fIndex = new int[80];");
			System.out.println("long[] fKey = new long[80];");
			
			for (int i=0; i<80; i++) {
				byte[] FI = F[i];
				byte[] B_index = new byte[4];
				byte[] B_key = new byte[16];

				for (int j=0; j<16; j+=4)
					B_index[3-j/4] = C[FI[j] + 2*FI[j+1] + 4*FI[j+2] + 8*FI[j+3]];
				for (int j=16; j<80; j+=4)
					B_key[19-j/4] = C[FI[j] + 2*FI[j+1] + 4*FI[j+2] + 8*FI[j+3]];
				System.out.println("fIndex["+i+"] = 0x" + new String(B_index) + ";");
				System.out.println("fKey["+i+"] = 0x" + new String(B_key) + "L;");
			}
			
			
		} else {
			System.out.println("print num implemented for num != 80");
		}
	}
	
	private String longToString(long fi) {
		byte[] b = new byte[49];
		for (int j=0; j<49; j++) {
			b[48 - j] = (byte)(48 + (int)(fi & 1));
			fi >>= 1;
		}
		return new String(b);
	}
	
	private String byteArrayToString(byte[] FI) {
		byte[] b = new byte[num];
		for (int j=0; j<num; j++)
			b[num - 1 - j] = (byte)(48 + FI[j]);
		return new String(b);
	}
	
	/**
	 * Applies the bijective mapping on an argument.
	 * 
	 * @param h a value in [0..2<SUP>49</SUP>[
	 * @return the value f(h)
	 */
	public long hash(long h) throws RuntimeException {
		if (useExtended)
			throw new RuntimeException();
		long r = 0;
		for (int i=0; i<49; i++) {
			if (((int)h & 1) != 0) 
				r ^= f[i];
			h >>= 1;
		}
		return r;
	}
	
	/**
	 * Applies the bijective mapping on an argument.
	 * 
	 * @param h a value in [0..2<SUP>49</SUP>[
	 * @return the value f(h)
	 */
	public byte[] hash(byte[] H) throws RuntimeException {
		if (!useExtended  ||  F[0].length != H.length)
			throw new RuntimeException();
			
		byte[] R = new byte[num];
		for (int i=0; i<num; i++)
			R[i] = 0;
		
		for (int i=0; i<num; i++) {
			if (H[i] != 0) {
				for (int j=0; j<num; j++)
					R[j] ^= F[i][j];
			}
		}

		return R;
	}
	
	private int[] getPermutation(int num) {
		Random r = new Random();
		int[] A = new int[num];
		for (int i=0; i<num; i++)
			A[i] = i;
		int[] B = new int[num];
		for (int i=0; i<num; i++)
			B[i] = r.nextInt();
		
		// Bubble sort B and move A indexes correspondingly - making A an arbitrary
		// permutation of {0, ..., 48}
		for (int i=1; i<num; i++)
			for (int j=0; j<i; j++)
				if (B[j] > B[i]) {
					// swap A[i] with A[j] and B[i] with B[j]
					// A[i] ^= A[j] ^= A[i] ^= A[j];
					// B[i] ^= B[j] ^= B[i] ^= B[j];
					int tmp = A[i];
					A[i] = A[j];
					A[j] = tmp;
					tmp = B[i];
					B[i] = B[j];
					B[j] = tmp;
				}
		
		return A;
	}
	
	/**
	 * Reverse hashing is only for debugging purposes - to demonstrate that it is really working.
	 * It is really slow. Some information is written to <code>System.out</code>.
	 * 
	 * @return true if no errors were found.
	 */
	public boolean verifyHashing() {
		System.out.println("Verifying reverse hashing:");
		Random r = new Random();
		for (int numIt=0; numIt<5; numIt++) {
			if (numIt != 0)
				System.out.println();
			
			if (useExtended) {
				
				//for (int i=0; i<num; i++) System.out.println(byteArrayToString(F[i]));
				//System.out.println();
				
				byte[] L = new byte[num];
				for (int i=0; i<num; i++)
					L[i] = (byte)r.nextInt(2);
				byte[] H = hash(L);
				byte[] H_inv = reverse_hash(H);

				System.out.println("   f(" + byteArrayToString(L) + ") =");
				System.out.println("     " + byteArrayToString(H) + "  ?");
				System.out.println("f^-1(" + byteArrayToString(H) + ") =");
				System.out.println("     " + byteArrayToString(H_inv) + " ?");
				
				for (int i=0; i<num; i++) {
					if (L[i] != H_inv[i]) {
						System.out.println("Verification failed!");
						return false;
					}
				}
				
			} else {
				
				long l = (0x123456789654323L * r.nextInt()) & 0x1FFFFFFFFFFFFL;
				long h = hash(l);
				long h_inv = reverse_hash(h);
				System.out.println("f(" + longToString(l    ) + ") =      " + longToString(h) + "  ?");
				System.out.println("  " + longToString(h_inv) + "  = f^-1(" + longToString(h) + ") ?");
				if (l != h_inv) {
					System.out.println("Verification failed!");
					return false;
				}
				
				l ^= 1<<(3*numIt);
				h = hash(l);
				h_inv = reverse_hash(h);
				System.out.println("f(" + longToString(l    ) + ") =      " + longToString(h) + "  ?");
				System.out.println("  " + longToString(h_inv) + "  = f^-1(" + longToString(h) + ") ?");
				if (l != h_inv) {
					System.out.println("Verification failed!");
					return false;
				}
			}
		}
		System.out.println("Verification successfull!");
		return true;
	}
	
	/**
	 * Applies the injerse bijective mapping f<SUP>-1</SUP> on an argument.
	 * This should rarely be used as it is utterly slow.
	 * 
	 * @param h a value in [0..2<SUP>49</SUP>[
	 * @return the value f<SUP>-1</SUP>(h)
	 */
	public long reverse_hash(long h) {
		return rh.reverse(h);
	}
	
	public byte[] reverse_hash(byte[] H) {
		return erh.reverse(H);
	}
	
	private class ReverseHashing {
		
		public ReverseHashing(ReverseHashing rh, int[] p, long[] f) {
			this.rh = rh;
			this.p = p;
			this.f = f;
		}
		
		public long reverse(long h) {
			long r = 0;
			for (int i=48; i>=0; i--) {
				long b = 1;
				for (int j=0; j<49; j++) {
					if (p[j]==i) {
						if ((h & b) != 0) {
							h ^= f[j];
							r ^= b;
						}
						break;
					}
					b <<= 1;
				}
			}
			if (rh != null) return rh.reverse(r);
			return r;
		}
		
		ReverseHashing rh;
		int[] p;
		long[] f;
	}
	
	private class ExtendedReverseHashing {
		
		public ExtendedReverseHashing(ExtendedReverseHashing erh, int[] p, byte[][] F) {
			this.erh = erh;
			this.p = p;
			this.F = F;
		}
		
		public byte[] reverse(byte[] _H) {
			byte[] R = new byte[num];
			byte[] H = new byte[num];
			for (int i=0; i<num; i++) {
				R[i] = 0;
				H[i] = _H[i];
			}
			
			for (int i=num-1; i>=0; i--) {
				for (int j=0; j<num; j++) {
					if (p[j]==i) {
						
						if (H[j] != 0) {
							for (int k=0; k<num; k++)
								H[k] ^= F[j][k];
							R[j] = 1;
						}
						break;
					}
				}
			}
			if (erh != null) return erh.reverse(R);
			return R;
		}
		
		ExtendedReverseHashing erh;
		int[] p;
		byte[][] F;
	}
	
	
	private boolean useExtended;
	
	private long[] f;
	private ReverseHashing rh;
	
	private int num;
	private byte[][] F;
	private ExtendedReverseHashing erh;
}
