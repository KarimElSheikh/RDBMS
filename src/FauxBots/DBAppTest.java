package FauxBots;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

/**
 *
 * @author andrewmagdy
 * @author karimelsheikh
 * @author minahany
 * @author ahmedabdelwahab
 */
public class DBAppTest {

	DBApp dbApp;

	/**
	 * Constructor for the DBAppTest class
	 */
	public DBAppTest() {

		this.dbApp = new DBApp();
	}

	/**
	 * Testing selectFromTable
	 * @param strTable
	 * @param htblColNameValue
	 * @param strOperator
	 * @throws ClassNotFoundException
	 * @throws DBEngineException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void selectFromTable(String strTable, Hashtable<String, String> htblColNameValue, String strOperator) throws ClassNotFoundException, DBEngineException, IOException {
		Iterator<Tuple> itr = dbApp.selectFromTable(strTable, htblColNameValue, strOperator);
		while (itr.hasNext())
			System.out.println(itr.next());
	}

	/**
	 * Inserting a lot of records in the table
	 * @param strTableName
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws DBAppException
	 */
	public void insertAlot(String strTableName) throws ClassNotFoundException,
			IOException, DBAppException {
		Hashtable<String, String> htblColNameValue = new Hashtable<String, String>();
		for (int i = 0; i < 400; ++i) {
			htblColNameValue.put("id", "" + i);
			htblColNameValue.put("Name", "Name Number " + i);
			htblColNameValue.put("Dept", "1");
			dbApp.insertIntoTable(strTableName, htblColNameValue);
		}
		
		htblColNameValue.put("id", "" + 900);
		htblColNameValue.put("Name", "Andrew");
		htblColNameValue.put("Dept", "5");
		dbApp.insertIntoTable(strTableName, htblColNameValue);
	}

	/**
	 * Testing selectFromHtable (To select using a hashtable)
	 * @param strTableName
	 * @param strColName
	 * @param strValue
	 * @throws ClassNotFoundException
	 * @throws DBEngineException
	 * @throws IOException
	 */
	public void selectFromHtable(String strTableName, String strColName,
			String strValue) throws ClassNotFoundException, DBEngineException,
			IOException {

		System.out.println(dbApp.selectFromHtable(strTableName, strColName,
				strValue));
	}

	/**
	 * Testing deleteFromTable
	 * @param strTableName
	 * @param htblColNameValue
	 * @param strOperator
	 * @throws DBEngineException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void deleteFromTable(String strTableName,
			Hashtable<String, String> htblColNameValue, String strOperator)
			throws DBEngineException, FileNotFoundException, IOException,
			ClassNotFoundException {
		dbApp.deleteFromTable(strTableName, htblColNameValue, strOperator);
	}

	/**
	 * Creating a table
	 * @param strTableName
	 * @throws ClassNotFoundException
	 * @throws DBAppException
	 * @throws IOException
	 * @throws DBEngineException
	 */
	public void createTable(String strTableName) throws ClassNotFoundException, DBAppException, IOException, DBEngineException {
		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
		Hashtable<String, String> htblColNameRefs = new Hashtable<String, String>();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("Name", "java.lang.String");
		htblColNameType.put("Dept", "java.lang.String");
		htblColNameRefs.put("Dept", "Department.ID");
		dbApp.createTable(strTableName, htblColNameType, htblColNameRefs, "id");
		dbApp.saveAll();
	}
	
	/**
	 * Testing selecting a range from the KDTree
	 * @param strTableName
	 * @param low1
	 * @param high1
	 * @param low2
	 * @param high2
	 * @throws ClassNotFoundException
	 * @throws DBAppException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void selectFromKDTree(String strTableName, String low1, String high1, String low2, String high2) throws ClassNotFoundException, DBAppException, IOException {
		KdTree testKDTree = dbApp.getTable(strTableName).getKDTree("id", "Name");
		Pair2 []low = new Pair2[2];
		low[0] = new Pair2(low1, 0, 0); low[1] = new Pair2(low2, 0, 0);
		Pair2 []high = new Pair2[2];
		high[0] = new Pair2(high1, 0, 0); high[1] = new Pair2(high2, 0, 0);
		testKDTree.printRange(low, high);
		HashSet<Pair3<Integer, Integer>> r = testKDTree.getRange();
		ArrayList<Pair3<Integer, Integer>> r2 = new ArrayList<Pair3<Integer, Integer>>();
		for (Pair3<Integer, Integer> pair : r) {
			r2.add(pair);
		}
		Collections.sort(r2);
		for (int i = 0; i < r2.size(); i++) {
			Page p = dbApp.getTable(strTableName).getPage(r2.get(i).getLeft());
			System.out.println(p.getTuple(r2.get(i).getRight()));
		}
		System.out.println("Successfully found " + r2.size() + " records using the KDTree between id = 201 to 209 and Name = \"Name Number 202\" to \"Name Number 209\"\n");
	}

	/**
	 * The main method of the DBAppTest class that runs all the tests
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws DBAppException
	 * @throws DBEngineException
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException, DBAppException, DBEngineException {
		DBAppTest test = new DBAppTest();
		String strTableName = "Employees";
		String strColName = "id";
		String strOperator = "and";

		test.createTable(strTableName);
		test.insertAlot(strTableName);
		System.out.println("Successfully created the table Employees and inserted 401 records in it\n");

		test.dbApp.createIndex(strTableName, strColName);
		test.selectFromHtable(strTableName, strColName, "30");
		test.selectFromHtable(strTableName, strColName, "399");
		System.out.println("Successfully found individual records using the Hashtable\n");

		
		Hashtable<String, String> htblColNameValue = new Hashtable<String, String>();
		htblColNameValue.put("id", "399");
		htblColNameValue.put("Dept", "1");
		test.selectFromTable(strTableName, htblColNameValue, strOperator);
		System.out.println("Successfully found records satisfying the condition using the Iterator\n");
		
		htblColNameValue = new Hashtable<String, String>();
		htblColNameValue.put("id", "42");
		htblColNameValue.put("Dept", "5");
		strOperator = "or";
		test.selectFromTable(strTableName, htblColNameValue, strOperator);
		System.out.println("Successfully found records satisfying the condition using the Iterator, 2nd Test\n");
		
		test.deleteFromTable(strTableName, htblColNameValue, strOperator);
		System.out.println("Successfully deleted a record from the table\n");
		
		ArrayList<String> keys = new ArrayList<String>();
		keys.add("id"); keys.add("Name");
		test.dbApp.createMultiDimIndex(strTableName, keys);
		test.selectFromKDTree(strTableName, "201", "209", "Name Number 202", "Name Number 209");
	}
}
