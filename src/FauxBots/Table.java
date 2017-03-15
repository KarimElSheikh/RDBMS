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
import java.util.Hashtable;
import java.util.Iterator;

/**
 *
 * @author andrewmagdy
 * @author karimelsheikh
 * @author minahany
 * @author ahmedabdelwahab
 */
@SuppressWarnings("unused")
public class Table implements Serializable {

	private static final long serialVersionUID = -3646470884609490516L;
	private String strTableName;
	private Hashtable<String, String> htblColNameType;
	private Hashtable<String, String> htblColNameRefs;
	private Hashtable<String, Boolean> ColsInHashtable;
	private ArrayList<Pair> KDTreePairs;
	private String strKeyColName;
	private int numPages;

	/**
	 * Constructor for Table.
	 * 
	 * @param strTableName
	 *            the table name
	 * @param htblColNameType
	 *            a Hashtable specified the Column Name/Type pairs
	 * @param htblColNameRefs
	 *            a Hashtable that for every Column Name specified whether it
	 *            references another Table Column
	 * @param strKeyColName
	 *            a String that specifies the key attribute of the table
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Table(String strTableName,
			Hashtable<String, String> htblColNameType,
			Hashtable<String, String> htblColNameRefs, String strKeyColName)
			throws IOException, ClassNotFoundException {
		this.strTableName = strTableName;
		this.htblColNameType = htblColNameType;
		this.htblColNameRefs = htblColNameRefs;
		this.strKeyColName = strKeyColName;
		ColsInHashtable = new Hashtable<String, Boolean>();
		KDTreePairs = new ArrayList<Pair>();
		(new Page(strTableName, 0)).write();
		this.numPages = 1;
		for (String key : htblColNameType.keySet()) {
			ColsInHashtable.put(key, false);
		}
	}

	/**
	 * Gets the page from the specified number
	 * @param pageNum
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Page getPage(int pageNum) throws ClassNotFoundException, IOException {

		FileInputStream fileIn = new FileInputStream("data/" + strTableName
				+ '-' + pageNum + ".class");
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Page p = (Page) in.readObject();
		in.close();
		fileIn.close();
		return p;
	}

	/**
	 * returns the hashtable for the specified column
	 * @param strColName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public ExtensibleHashTable getHtbl(String strColName)
			throws ClassNotFoundException, IOException {
		FileInputStream fileIn = new FileInputStream("data/" + strTableName
				+ "-Hashtable-" + strColName + ".class");
		ObjectInputStream in = new ObjectInputStream(fileIn);
		ExtensibleHashTable h = (ExtensibleHashTable) in.readObject();
		in.close();
		fileIn.close();
		return h;
	}

	/**
	 * Creates a tuple from an argument Hashtable and inserts it in the table
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	public void insert(Hashtable<String, String> htblColNameValue)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		if (!getPage(numPages - 1).insert(htblColNameValue)) {
			Page p = new Page(strTableName, numPages);
			p.insert(htblColNameValue);
			p.write();
			numPages++;
			write();
		}
	}

	/**
	 * Deletes all tuples that satisfy a condition which is specified by a
	 * Hashtable containing Column Name/Value Pairs and an operator that
	 * specifies the operation to be done on the pairs
	 * 
	 * @param htblColNameValue
	 *            the Hashtable containing Column Name/Value Pairs
	 * @param strOperator
	 *            the Operator (Can be either OR or AND)
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	public void deleteFromTable(Hashtable<String, String> htblColNameValue,
			String strOperator) throws DBEngineException,
			FileNotFoundException, IOException, ClassNotFoundException {
		for (int i = 0; i < numPages; i++) {
			getPage(i).deleteFromPage(htblColNameValue, strOperator);
		}
	}

	/**
	 * returns an iterator to the first page that contains a Tuple that
	 * satisfies the condition specified by the Operator and the Hashtable
	 * containing Column Name/Value Pairs
	 * 
	 * @return the iterator to the page is a tuple is found, otherwise null
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Iterator<Tuple> selectFromTable(
			Hashtable<String, String> htblColNameValue, String strOperator)
			throws DBEngineException, ClassNotFoundException, IOException {
		ArrayList<Tuple> res = new ArrayList<Tuple>();
		for (int i = 0; i < numPages; i++) {
			res.addAll(getPage(i).selectFromPage(htblColNameValue, strOperator));
		}
		return res.iterator();
	}

	/**
	 * Returns a tuple using the Hashtable
	 * @param strColName
	 * @param strValue
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Tuple selectFromHtable(String strColName, String strValue)
			throws ClassNotFoundException, IOException {
		ExtensibleHashTable x = getHtbl(strColName);
		int pg = x.get(strValue).pageNumber;
		int rw = x.get(strValue).rowNumber;
		return getPage(pg).getTuple(rw);
	}

	/**
	 * Creates a hashtable using the specified column
	 * @param strColName
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void createIndex(String strColName) throws ClassNotFoundException,
			IOException {
		ColsInHashtable.put(strColName, true);
		ExtensibleHashTable x = new ExtensibleHashTable(strTableName,
				strColName);
		for (int i = 0; i < numPages; i++) {
			Page p = getPage(i);
			p.createIndex(x, strColName);
		}
		x.write();

	}

	/**
	 * Returns the table's name
	 * 
	 * @return the table's name
	 */
	public String getTableName() {
		return strTableName;
	}

	/**
	 * Returns the number of pages of the table
	 * 
	 * @return the number of pages of the table
	 */
	public int getNumberOfPages() {
		return numPages;
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < numPages; ++i) {

			Page p = null;
			try {
				p = getPage(i);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			res += "Page " + i + '\n';
			res += p.toString();

		}
		return res;
	}
	
	/**
	 * Gets the KDTree for the specified columns
	 * @param key1
	 * @param key2
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public KdTree getKDTree(String key1, String key2) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("data/" + strTableName + "-KDTree-" + key1 + '-' + key2 + ".class")));
        KdTree returnKdTree = (KdTree)ois.readObject();
        ois.close();
		return returnKdTree;
	}

	/**
	 * Writes the Table's pages to the disk
	 * 
	 * @throws ClassNotFoundException
	 */
	public void write() throws IOException, ClassNotFoundException {
		for (int i = 0; i < numPages; i++) {
			getPage(i).write();
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				new File("data/" + strTableName + ".ser")));
		oos.writeObject(this);
		oos.close();
	}

	/**
	 * Creates a KDTree from the specified pair of column names
	 * @param pair
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void createMultiDimIndex(Pair pair) throws FileNotFoundException, IOException, ClassNotFoundException {
		KdTree x = new KdTree(strTableName, pair.s1, pair.s2);
		for ( int i = 0; i < numPages; i++ ) {
			Page p = getPage(i);
			p.createMultiDimIndex(x, pair);
		}
		KDTreePairs.add(pair);
		x.write();
		write();
	}

}
