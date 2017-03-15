package FauxBots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * 
 * @author andrewmagdy
 * @author karimelsheikh
 * @author minahany
 * @author ahmedabdelwahab
 */
@SuppressWarnings("serial")
public class ExtensibleHashTable implements Serializable {
	
	private static final String configFile = "DBApp";
	private static final String key = "ExtensibleHashtableN";
	private String tableName;
	private String columnName;
	
	private int i;
	private int bucketSize;
	private ArrayList<Integer> bucketArray;
	private int bucketID;
	
	/**
	 * Returns the hash value of the pointer, which is the hash value of the column value
	 * @param p
	 * @return
	 */
	public int h(Pointer p) {
		return ( p.value.hashCode() & ((1 << i) - 1) );
	}
	
	/**
	 * Returns the hash value of the Object, which is the hash value of the column value
	 * @param p
	 * @return
	 */
	public int h2(Object o) {
		return ( o.hashCode() & ((1 << i) - 1) );
	}
	
	/**
	 * Gets the Nth bit (0-based) from the right of the entered value
	 * @param value
	 * @param n
	 * @return
	 */
	public int getNthBit(int value, int n) {
		if ( (value & (1 << n)) > 0 )
			return 1;
		return 0;
	}
	
	/**
	 * Zeroes out all the bits starting from the Nth bit (0-based) from the right of the entered value
	 * @param value
	 * @param n
	 * @return
	 */
	public int removeAfterNthBit(int value, int n) {
		return ( value & ((1 << n) - 1) );
	}
	
	/**
	 * Inverts the Nth bit (0-based) from the right of the entered value
	 * @param value
	 * @param n
	 * @return
	 */
	public int invertNthBit(int value, int n) {
		return ( value ^ (1 << n) );
	}
	
	/**
	 * Gets the bucket that the bucketArray in the specified index refers to
	 * @param index
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Bucket getBucket(int index) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("data/"+tableName + "-Hashtable-" + columnName + '-' + bucketArray.get(index) + ".class")));
        Bucket returnBucket = (Bucket)ois.readObject();
        ois.close();
		return returnBucket;
	}
	
	/**
	 * Gets the bucket with the entered ID
	 * @param ID
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Bucket getBucketByID(int ID) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("data/"+tableName + "-Hashtable-" + columnName + '-' + ID + ".class")));
        Bucket returnBucket = (Bucket)ois.readObject();
        ois.close();
		return returnBucket;
	}
	
	/**
	 * Constructor for the Hashtable
	 * @param tableName the table name
	 * @param columnName the collumn name
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public ExtensibleHashTable(String tableName, String columnName) throws FileNotFoundException, ClassNotFoundException, IOException {
		this.tableName = tableName;
		this.columnName = columnName;
        ResourceBundle bundle = ResourceBundle.getBundle(configFile);
		i = 1;
		bucketSize = Integer.parseInt(bundle.getString(key));
		new Bucket(bucketSize, 1, tableName, columnName, 0).write();
		new Bucket(bucketSize, 1, tableName, columnName, 1).write();
		bucketArray = new ArrayList<Integer>();
		bucketArray.add(0);
		bucketArray.add(1);
		bucketID = 2;
	}
	
	/**
	 * Inserts a pointer in the hashtable
	 * @param o the pointer
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void insert(Pointer o) throws FileNotFoundException, ClassNotFoundException, IOException {
		int hash = h(o);
		Bucket B;
		if ( (B = getBucket(hash)).insert(o) ) {
			B.write();
			return;
		}
		int j;
		if ( (j = B.getJ()) < i ) {
			int firstIndex = bucketID;
			Bucket first = new Bucket(bucketSize, j + 1, tableName, columnName, bucketID++);
			int secondIndex = bucketID;
			Bucket second = new Bucket(bucketSize, j + 1, tableName, columnName, bucketID++);
			for ( int m = 0; m < B.getN(); m++ ) {
				Pointer x = (Pointer) B.get(m);
				if ( getNthBit(h(x), j) == 0  )
					first.insert(x);
				else
					second.insert(x);
			}
			first.write();
			second.write();
			for ( int m = 0; m < bucketArray.size(); m++ ) {
				if ( bucketArray.get(m) == B.getID() ) {
					if ( getNthBit(m, j) == 0 )
						bucketArray.set(m, firstIndex);
					else
						bucketArray.set(m, secondIndex);
				}
			}
			insert(o);
		}
		else { //j == i
			rebuild();
			insert(o);
		}
	}
	
	/**
	 * Returns the Pointer to the Object specified if found in the hashtable, null otherwise
	 * @param o the object
	 * @return t the pointer
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Pointer get(Object o) throws FileNotFoundException, ClassNotFoundException, IOException {
		int hash = h2(o);
		Bucket B = getBucket(hash);
		for ( int p = 0; p < B.getN(); p++ ) {
			if ( B.get(p).value.equals(o)) return B.get(p);
		}
		return null;
	}
	
	/**
	 * Resize the bucketArray to have twice as much bucket references
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void rebuild() throws FileNotFoundException, ClassNotFoundException, IOException {
		i++;
		ArrayList<Integer> tempBucketArray = new ArrayList<Integer>();
		for ( int p = 0; p < (1 << i); p++ ) {
			tempBucketArray.add(getBucket(removeAfterNthBit(p, i-1)).getID());
		}
		bucketArray = tempBucketArray;
	}
	
	/**
	 * Resize the bucketArray to have half as much bucket references
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void rebuild2() throws FileNotFoundException, ClassNotFoundException, IOException {
		i--;
		ArrayList<Integer> tempBucketArray = new ArrayList<Integer>();
		for ( int p = 0; p < (1 << i); p++ ) {
			tempBucketArray.add(getBucket(p).getID());
		}
		bucketArray = tempBucketArray;
	}
	
	/**
	 * Deletes the specified column value
	 * @param o
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void delete(Pointer o) throws FileNotFoundException, ClassNotFoundException, IOException {
		int hash = h(o);
		Bucket B;
		if ( (B = getBucket(hash)).delete(o) ) {
			if ( B.getN() == 0 ) {
				(new File("data/" + tableName + "-Hashtable-" + columnName + '-' + B.getID() + ".class")).delete();
				bucketArray.set(hash, getBucket(invertNthBit(hash, i-1)).getID());
				B = getBucket(hash);
				B.setJ(B.getJ() - 1);
				B.write();
				boolean flag = true;
				for ( int p = 0; p < bucketArray.size(); p++ ) {
					if ( getBucket(p).getJ() == i ) {
						flag = false;
						break;
					}
				}
				if ( flag ) rebuild2();
			}
		}
	}
	
	/**
	 * Prints the values in the Hashtable, used for testing
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void print() throws FileNotFoundException, ClassNotFoundException, IOException {
		HashMap<Integer, ArrayList<Integer>> x = new HashMap<Integer, ArrayList<Integer>>();
		for ( int m = 0; m < bucketArray.size(); m++ ) {
			if( x.get(bucketArray.get(m)) != null )
				x.get(bucketArray.get(m)).add(m);
			else
				x.put(bucketArray.get(m), new ArrayList<Integer> (Arrays.asList(m)));
		}
		for (int B : x.keySet()) {
			for ( int m = 0; m < x.get(B).size(); m++ ) {
				System.out.println(String.format("%3s", Integer.toBinaryString(x.get(B).get(m))).replace(' ', '0'));
			}
			System.out.println(getBucketByID(B).getJ() + " ------");
			for( int p = 0; p < getBucketByID(B).getN(); p++ )
				System.out.println("  " + getBucketByID(B).get(p));
			System.out.println();
		}
	}
	
	/**
	 * Writes the Hashtable to the disk
	 * @throws IOException
	 */
	public void write() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("data/~" + tableName + "-Hashtable-" + columnName + ".class")));
        oos.writeObject(this);
        oos.close();
        File originalFile = new File("data/" + tableName + "-Hashtable-" + columnName + ".class");
        File tmpFile = new File("data/~" + tableName + "-Hashtable-" + columnName + ".class");
        originalFile.delete();
        tmpFile.renameTo(originalFile);
	}
	
	/**
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void saveAll() throws FileNotFoundException, ClassNotFoundException, IOException {
		write();
		for ( int p : bucketArray ) {
			getBucket(bucketArray.get(p)).write();
		}
	}
}
