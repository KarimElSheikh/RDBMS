package FauxBots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 *
 * @author andrewmagdy
 * @author karimelsheikh
 * @author minahany
 * @author ahmedabdelwahab
 */
@SuppressWarnings("unused")
public class Page implements Serializable {

	private static final long serialVersionUID = 9056391283383238173L;
	private static final String configFile = "DBApp";
	private static final String key = "MaximumRowsCountinPage";
	private int n;
	private String strTableName;
	private int index;
	private ArrayList<Tuple> Tuples;

	/**
	 * Constructor for Page.
	 * 
	 * @param table
	 *            the table this page belongs to
	 * @throws IOException
	 */
	public Page(String strTableName, int index) throws IOException {
		ResourceBundle bundle = ResourceBundle.getBundle(configFile);
		this.n = Integer.parseInt(bundle.getString(key));
		this.strTableName = strTableName;
		this.index = index;
		this.Tuples = new ArrayList<Tuple>();
	}

	public Tuple getTuple(int index) {
		return Tuples.get(index);
	}

	/**
	 * Creates a tuble from an argument Hashtable and attempts to insert it in
	 * the page Insertion is succesful if the page has less than the maximum
	 * amount of tuples allowed.
	 * 
	 * @param htblColNameValue
	 *            the Hashtable
	 * @return <tt>true</tt> if the insertion is succesful
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public boolean insert(Hashtable<String, String> htblColNameValue)
			throws FileNotFoundException, IOException {
		if (Tuples.size() < n) {
			Tuples.add(new Tuple(htblColNameValue));
			write();
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String res = "";
		for (Tuple t : Tuples) {
			res += t.toString();
			res += '\n';
		}
		return res;
	}

	/**
	 * Attempts to insert the argument tuple in the page. Insertion is succesful
	 * if the page has less than the maximum amount of tuples allowed.
	 * 
	 * @param tuple
	 *            the Tuple
	 * @return <tt>true</tt> if the insertion is succesful
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public boolean insert(Tuple tuple) throws FileNotFoundException,
			IOException {
		if (Tuples.size() < n) {
			Tuples.add(tuple);
			write();
			return true;
		}
		return false;
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
	 */
	public void deleteFromPage(Hashtable<String, String> htblColNameValue,
			String strOperator) throws DBEngineException,
			FileNotFoundException, IOException {
		for (String key : htblColNameValue.keySet()) {
			if (Tuples.get(0).get(key) == null)
				throw new DBEngineException(
						"Table doesn't contain matching keys");
		}
		if (strOperator.equalsIgnoreCase("and")) {
			for (int i = 0; i < Tuples.size(); i++) {
				if (!Tuples.get(i).isDeleted()) {
					boolean found = true;
					for (String key : htblColNameValue.keySet()) {
						if (!Tuples.get(i).get(key)
								.equals(htblColNameValue.get(key))) {
							found = false;
							break;
						}
					}
					if (found) {
						Tuples.get(i).setDeleted(true);
						write();
					}
				}
			}
		} else if (strOperator.equalsIgnoreCase("or")) {
			for (int i = 0; i < Tuples.size(); i++) {
				if (!Tuples.get(i).isDeleted()) {
					for (String key : htblColNameValue.keySet()) {
						if (Tuples.get(i).get(key)
								.equals(htblColNameValue.get(key))) {
							Tuples.get(i).setDeleted(true);
							write();
						}
					}
				}
			}
		} else
			throw new DBEngineException("Invalid String Operator");
	}

	/**
	 * returns an iterator to the page if the page contains a Tuple that
	 * satisfies the condition specified by the Operator and the Hashtable
	 * containing Column Name/Value Pairs
	 * 
	 * @return the iterator to the page is a tuple is found, otherwise null
	 */
	public ArrayList<Tuple> selectFromPage(
			Hashtable<String, String> htblColNameValue, String strOperator)
			throws DBEngineException {
		for (String key : htblColNameValue.keySet()) {
			if (Tuples.get(0).get(key) == null)
				throw new DBEngineException(
						"Table doesn't contain matching keys");
		}
		ArrayList<Tuple> res = new ArrayList<Tuple>();
		if (strOperator.equalsIgnoreCase("and")) {
			for (int i = 0; i < Tuples.size(); i++) {
				if (!Tuples.get(i).isDeleted()) {
					boolean found = true;
					for (String key : htblColNameValue.keySet()) {
						if (!Tuples.get(i).get(key)
								.equals(htblColNameValue.get(key))) {
							found = false;
							break;
						}
					}
					if (found) {
						res.add(Tuples.get(i));
					}
				}
			}
			return res;
		} else if (strOperator.equalsIgnoreCase("or")) {
			for (int i = 0; i < Tuples.size(); i++) {
				if (!Tuples.get(i).isDeleted()) {
					boolean found = false;
					for (String key : htblColNameValue.keySet()) {
						if (Tuples.get(i).get(key)
								.equals(htblColNameValue.get(key))) {
								found=true;
								break;
						}
					}
					if(found)
					res.add(Tuples.get(i));
				}
			}
			return res;
		} else
			throw new DBEngineException("Invalid String Operator");
	}

	/**
	 * Called by the Table Procedure that creates the hashtable for the specified column,
	 * the procedure passes by all the pages
	 * @param x
	 * @param colName
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void createIndex(ExtensibleHashTable x, String colName)
			throws FileNotFoundException, ClassNotFoundException, IOException {
		for (int i = 0; i < Tuples.size(); i++) {
			x.insert(new Pointer(Tuples.get(i).get(colName), index, i));
		}
	}
	
	
	/**
	 * Called by the Table Procedure that creates the KDTree for the specified columns,
	 * the procedure passes by all the pages
	 * @param x
	 * @param colName
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void createMultiDimIndex(KdTree x, Pair pair) throws NumberFormatException, ClassNotFoundException, IOException {
    	for ( int i = 0; i < Tuples.size(); i++ ) {
    		Tuple tuple = Tuples.get(i);
    		Pair2[] pairs = new Pair2[2];
    		pairs[0] = new Pair2((String)tuple.get(pair.s1), index, i);
    		pairs[1] = new Pair2((String)tuple.get(pair.s2), index, i);
    		x.insert(pairs);
    	}
	}

	/**
	 * Writes the page to the disk in a binary .class file
	 */
	public void write() throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				new File("data/~" + strTableName + '-' + index + ".class")));
		oos.writeObject(this);
		oos.close();
		File originalFile = new File("data/" + strTableName + '-' + index
				+ ".class");
		File tmpFile = new File("data/~" + strTableName + '-' + index
				+ ".class");
		originalFile.delete();
		tmpFile.renameTo(originalFile);
	}

}
