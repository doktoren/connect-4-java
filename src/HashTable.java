import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//ArrayList implementation failed:
//table = new ArrayList<TransitionTableEntry>[1 << logSize]; doh!

public class HashTable {
	
	private static double MAX_FILL_RATE = 0.5;
	private int filled, maxFilled;
	
	private TTE2[] table;
	private int predSize;
	
	private String fileName;
	
	public HashTable(String fileName) {
		this.fileName = fileName;
		clear();
	}
	
	public void clear() {
		int logSize = 4;
		filled = 0;
		maxFilled = (int)(MAX_FILL_RATE * (1 << logSize));
		table = new TTE2[1 << logSize];
		predSize = (1<<logSize)-1;
	}
	
	public void insert(TTE2 entry) {
		if (++filled == maxFilled)
			resize(2*predSize + 2);
		
		int index = predSize & (int)(entry.getHashValue()); 
		while (table[index] != null) {
			if (table[index].matches(entry.getHashValue())) {
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
	public TTE2 lookup(long hashValue) {
		int index = predSize & (int)hashValue;
		while (table[index] != null) {
			if (table[index].matches(hashValue)) {
				return table[index];
			}
			index = (index + 1) & predSize;
		}
		return null;
	}
	
	public void save() {
		try
		{
			ObjectOutputStream file = 
				new ObjectOutputStream(
						new FileOutputStream(fileName));
			file.writeInt(predSize);
			for (int i=0; i<=predSize; i++) {
				file.writeBoolean(table[i] != null);
				if (table[i] != null) {
					file.writeObject(table[i]);
				}
			}
			file.close();
		}
		catch (Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	public boolean load() {
		try
		{
			ObjectInputStream file = 
				new ObjectInputStream(
						new FileInputStream(fileName));
			predSize = file.readInt();
			table = new TTE2[predSize + 1];
			filled = 0;
			maxFilled = (int)(MAX_FILL_RATE * (predSize + 1));
			
			for (int i=0; i<=predSize; i++) {
				if (file.readBoolean()) {
					++filled;
					table[i] = (TTE2)file.readObject();
				}
			}
			
			file.close();
			return true;
		}
		catch (Exception ex)
		{
			System.err.println("Could not load file " + fileName);
			System.err.println("Creating empty opening library.");
			clear();
			return false;
		}
	}
	
	private void resize(int newSize) {
		int newLogSize = 1;
		while ((1 << newLogSize) < newSize)
			++newLogSize;
		
		// Remember old data
		TTE2[] oldTable = table;
		int oldSize = predSize + 1;
		
		// Create new table
		filled = 0;
		maxFilled = (int)(MAX_FILL_RATE * (1 << newLogSize));
		table = new TTE2[1 << newLogSize];
		predSize = (1<<newLogSize)-1;
		
		// Insert all entries into new table
		for (int i=0; i<oldSize; i++)
			if (oldTable[i] != null)
				insert(oldTable[i]);
	}
}
