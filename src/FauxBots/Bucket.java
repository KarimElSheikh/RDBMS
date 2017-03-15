package FauxBots;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Bucket implements Serializable {
	
	private Pointer[] array;
	private int n;
	private int j;
	private String tableName;
	private String columnName;
	private int ID;
	
	/**
	 * Constructor for Bucket
	 * @param bucketSize
	 * @param j
	 * @param tableName
	 * @param columnName
	 * @param ID
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Bucket(int bucketSize, int j, String tableName, String columnName, int ID) throws FileNotFoundException, IOException {
		array = new Pointer[bucketSize];
		this.j = j;
		this.tableName = tableName;
		this.columnName = columnName;
		this.ID = ID;
	}

	/**
	 * Insert a pointer in the bucket
	 * @param o
	 * @return
	 */
	boolean insert(Pointer o) {
		if ( n < array.length ) {
			array[n] = o;
			n++;
			return true;
		}
		return false;
	}
	
	/**
	 * Get the pointer in the specified index
	 * @param index
	 * @return
	 */
	public Pointer get(int index) {
		return array[index];
	}
	
	/**
	 * Delete a pointer from the specified index in the bucket
	 * @param o
	 * @return
	 */
	public boolean delete(Pointer o) {
		for ( int p = 0; p < n; p++ ) {
			if ( array[p].equals(o) ) {
				n--;
				shift(p);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Shift the pointers in the bucket
	 * @param index
	 */
	public void shift(int index) {
		for ( int p = index; p < n; p++ )
			array[p] = array[p+1];
	}
	
	/**
	 * gets N, the number of pointers in the bucket
	 * @return
	 */
	public int getN() {
		return n;
	}
	
	/**
	 * gets the ID of the bucket
	 * @return
	 */
	public int getID() {
		return ID;
	}

	/**
	 * gets J, the number of bits needed to compare to say that the object belongs to this bucket
	 * @return
	 */
	public int getJ() {
		return j;
	}

	/**
	 * Sets J to a specified value
	 * @param value
	 */
	public void setJ( int value) {
		j = value;
	}

	/**
	 * Writes the bucket to the disk
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void write() throws FileNotFoundException, IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("data/~" + tableName + "-Hashtable-" + columnName + '-' + ID + ".class")));
        oos.writeObject(this);
        oos.close();
        File originalFile = new File("data/" + tableName + "-Hashtable-" + columnName + '-' + ID + ".class");
        File tmpFile = new File("data/~" + tableName + "-Hashtable-" + columnName + '-' + ID + ".class");
        originalFile.delete();
        tmpFile.renameTo(originalFile);
	}
}
