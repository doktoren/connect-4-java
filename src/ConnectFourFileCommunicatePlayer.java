import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConnectFourFileCommunicatePlayer implements ConnectFourListener, ConnectFourPlayer {
	
	private ConnectFour cf;
	
	private FileOutputStream outFile;
	private FileInputStream inFile;
	
	public ConnectFourFileCommunicatePlayer(ConnectFour cf, String inFileName, String outFileName)
	throws IOException {
		this.cf = cf;
		inFile = new FileInputStream(inFileName);
		outFile = new FileOutputStream(outFileName);
	}
	
	public void quit() {
		send("-3");
		if (receive().compareTo("-3") != 0)
			System.out.println("AAAAAAAAARRRRRRRRRGGGGGGGGGHHHHHHHHHHH!!!!!! -3");
	}
	
	public void newGame() {
		// TODO Auto-generated method stub
		send("-2");
		String r = receive();
		if (r.compareTo("-2") != 0)
			System.out.println("AAAAAAAAARRRRRRRRRGGGGGGGGGHHHHHHHHHHH!!!!!! -2: >" + r + "<");
	}

	public void undo() {
		// TODO Auto-generated method stub
		send("-1");
		if (receive().compareTo("-1") != 0)
			System.out.println("AAAAAAAAARRRRRRRRRGGGGGGGGGHHHHHHHHHHH!!!!!! -1");
	}
	
	public void put(int column) {
		// TODO Auto-generated method stub
		send("" + column);
		if (receive().compareTo("" + column) != 0)
			System.out.println("AAAAAAAAARRRRRRRRRGGGGGGGGGHHHHHHHHHHH!!!!!! 0+");
	}

	public int requestMove() {
		// TODO Auto-generated method stub
		return (new Integer(receive())).intValue();
	}
	
	private void send(String s) {
		try
		{
			System.out.println("Trying to send: " + s);
			for (int i=0; i<s.length(); i++)
				outFile.write(s.charAt(i));
			outFile.write(' ');
			outFile.flush();
			System.out.println("Succesfull!");
		}
		catch (Exception ex)
		{
			System.out.println("send error: " + ex);
		}
	}

	private String receive() {
		try
		{
			System.out.println("Trying to receive.");
			byte[] b = new byte[32];
			int index = 0;
			int next;
			next = inFile.read();
			while (next == -1) {
			      try{
			    	  System.out.println("Sleeping...");
			    	  Thread.sleep(100);
			      }
			      catch(InterruptedException e){
			    	  System.out.println("Sleep Interrupted");
			      }
			}
			do {
				System.out.println("Read character " + (char)next);
				b[index++] = (byte)next;
				next = inFile.read();
				while (next == -1) {
					try{
				    	System.out.println("Sleeping...");
						Thread.sleep(100);
					}
					catch(InterruptedException e){
						System.out.println("Sleep Interrupted");
					}
					next = inFile.read();
				}
			} while (next != ' ');
			String r = new String(b, 0, index);

			System.out.println("Succesfully received " + r);
			return r;
		}
		catch (Exception ex)
		{
			System.out.println("receive error: " + ex);
			return "error";
		}
	}
}
