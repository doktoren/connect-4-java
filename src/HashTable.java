import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//ArrayList implementation failed:
//table = new ArrayList<TransitionTableEntry>[1 << logSize]; doh!

public class HashTable {
	
	private static double MAX_FILL_RATE = 0.5;
	private int filled, maxFilled;
	
	private TransitionTableEntry[] table;
	private int predSize; // log(predSize+1) must always be at least 17
	
	HashTable() {
		int logSize = 17;
		filled = 0;
		maxFilled = (int)(MAX_FILL_RATE * (1 << logSize));
		table = new TransitionTableEntry[1 << logSize];
		predSize = (1<<logSize)-1;
	}
	
	public void insert(long hashValue, TransitionTableEntry entry) {
		if (++filled == maxFilled)
			resize(2*predSize + 2);
		
		int index = predSize & (int)hashValue; 
		while (table[index] != null) {
			if (table[index].hash_value == (int)(hashValue >>> 32)) {
				// Overwrite this entry
				table[index] = entry;
				return;
			}
			index = (index + 1) & predSize;
		}
		
		// Insert new entry
		table[index] = entry;
	}
	
	// Returns null if lookup failed.
	public TransitionTableEntry lookup(long hashValue) {
		int index = predSize & (int)hashValue;
		while (table[index] != null) {
			if (table[index].hash_value == (int)(hashValue >>> 32)) {
				return table[index];
			}
			index = (index + 1) & predSize;
		}
		return null;
	}
	
	public void save(String fileName) {
		try
		{
			ObjectOutputStream file = 
				new ObjectOutputStream(
						new FileOutputStream("openingDatabase.dat"));
			file.writeInt(predSize);
			for (int i=0; i<=predSize; i++) {
				file.writeBoolean(table[i] != null);
				if (table[i] != null)
					file.writeObject(table[i]);
			}
			file.close();
		}
		catch (Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	public boolean load(String fileName) {
		try
		{
			ObjectInputStream file = 
				new ObjectInputStream(
						new FileInputStream("openingDatabase.dat"));
			predSize = file.readInt();
			table = new TransitionTableEntry[predSize + 1];
			filled = 0;
			maxFilled = (int)(MAX_FILL_RATE * (predSize + 1));
			
			for (int i=0; i<=predSize; i++) {
				if (file.readBoolean()) {
					++filled;
					table[i] = (TransitionTableEntry)file.readObject();
				}
			}
			
			file.close();
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}
	
	private void resize(int newSize) {
		int newLogSize = 1;
		while ((1 << newLogSize) < newSize)
			++newLogSize;
		
		// Remember old data
		TransitionTableEntry[] oldTable = table;
		int oldSize = predSize + 1;
		
		// Create new table
		filled = 0;
		maxFilled = (int)(MAX_FILL_RATE * (1 << newLogSize));
		table = new TransitionTableEntry[1 << newLogSize];
		predSize = (1<<newLogSize)-1;
		
		// Insert all entries into new table
		// TODO: ARGH! tricket virker ikke med denne type hashtabel!!!
		long p = predSize & ~0x1FFFF; 		
		for (int i=0; i<oldSize; i++)
			if (oldTable[i] != null) {
				long hv = (((long)oldTable[i].hash_value) << 32) |
					((i & 0x1FFFF) | (oldTable[i].hash_value & p)); 
				insert(hv, oldTable[i]);
				
			}
	}
}
