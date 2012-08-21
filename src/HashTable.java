import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//ArrayList implementation failed:
//table = new ArrayList<TransitionTableEntry>[1 << logSize]; doh!

/**
 * A linear probe implementation of a hash table.
 * The implementation is only used by {@link ComputerPlayer} for the opening database
 * and the hash table can only contain the object {@link TTE2}.
 * At construction the instanse is bound to a file name that will be used by <code>load</code> and <code>save</code>.
 * 
 * @see TTE2
 * @see ComputerPlayer
 * @author Jesper Kristensen
 */
public class HashTable {
	
	private static double MAX_FILL_RATE = 0.5;
	private int filled, maxFilled;
	
	private TTE2[] table;
	private int predSize;
	
	private String fileName;
	
	/**
	 * Constructs an initially small and empty hash table.
	 * 
	 * @param fileName name of the file associated with this hash table. 
	 */
	public HashTable(String fileName) {
		this.fileName = fileName;
		clear();
	}
	
	/**
	 * Clears all the entries. Takes time linearly in the size.
	 */
	public void clear() {
		int logSize = 4;
		filled = 0;
		maxFilled = (int)(MAX_FILL_RATE * (1 << logSize));
		table = new TTE2[1 << logSize];
		predSize = (1<<logSize)-1;
	}
	
	/**
	 * Inserts a new entry or overwrites an existing one if the same key (hashValue) is already present.
	 * 
	 * @param entry the object to insert.
	 */
	public void insert(TTE2 entry) {
		if (++filled == maxFilled)
			resize(2*predSize + 2);

		int i = predSize & (int)(entry.getKey());
		while (table[i] != null) {
			if (table[i].matches(entry.getKey(), entry.getIndex())) {
				// Overwrite this entry
				table[i] = entry;
				return;
			}
			i = (i + 1) & predSize;
		}
		
		// Insert new entry
		table[i] = entry;
	}
	
	/**
	 * Returns the object with the given key (hashValue) or null if no such object exists.
	 * 
	 * @return null or object with same hashValue.
	 */
	public TTE2 lookup(long key, int index) {
		int i = predSize & (int)key;
		while (table[i] != null) {
			if (table[i].matches(key, index)) {
				return table[i];
			}
			i = (i + 1) & predSize;
		}
		return null;
	}
	
	/**
	 * Saves the contents of the hash table to the file chosen at construction time.
	 */
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
	
	/**
	 * Reconstructs the hash table from the data in the file chosen at construction time.
	 * Clears the table if the file is not present.
	 * 
	 * @return boolean whether the table was succesfully loaded.
	 */
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

					// System.out.println("key = " + table[i].getKey() + ", index = " + table[i].getIndex());
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
