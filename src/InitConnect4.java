import java.io.IOException;

public class InitConnect4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (false) {
			// Generate hash values
			HashValueClass hvc = new HashValueClass(false);
			System.out.println();
			hvc.print();
			System.out.println();
			hvc.verifyHashing();
		}
		
		if (false) {
			// Generate patterns
			GeneratePatterns gp = new GeneratePatterns();
			gp.connectFour();
			gp.connectSquare();
			gp.connect3D();
		}
		
		int gameVariant;
		System.out.println("Select game variant:");
		System.out.println("    0: Normal connect 4");
		System.out.println("    1: Connect 4 with wrap around and bounce (cpu plays perfect)");
		System.out.println("    2: Connect the corners of a square (cpu plays perfect)");
		System.out.println("    3: Connect 4 i 3D");
		{
			char c;
			do {
				c = TextInput.getChar();
			} while (c<'0'  ||  '3'<c);
			gameVariant = c - '0';
		}

		ComputerPlayer cp = new ComputerPlayer(gameVariant);
		ConnectFour cf = new ConnectFour(cp);
		ConnectFourACSIHuman cfh = new ConnectFourACSIHuman(cp, cf);
		
		cf.addListener(cp);
		cf.addListener(cfh);
		
		try {
			ConnectFourFileCommunicatePlayer cffcp = new ConnectFourFileCommunicatePlayer(cf, "receive.txt", "send.txt");
			cf.addListener(cffcp);
		} catch (IOException e) {
			System.out.println(e);
		}

		cfh.takeControl();
	}
}
