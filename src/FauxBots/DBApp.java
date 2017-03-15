package FauxBots;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
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
/**
 * @author Karim
 *
 */
public class DBApp {

	private ArrayList<String> existingTables;
	private MetaData metaData;

	/**
	 * Gets the table with the specified table name
	 * @param strTableName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Table getTable(String strTableName) throws IOException,
			ClassNotFoundException {
		if (existingTables.contains(strTableName)) {
			FileInputStream fileIn = new FileInputStream("data/" + strTableName
					+ ".ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Table table = (Table) in.readObject();
			in.close();
			fileIn.close();
			return table;
		}
		return null;
	}

	/**
	 * Displays all the pages and tuples of the Table, used for testing
	 * @param strTableName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public String displayTable(String strTableName)
			throws ClassNotFoundException, IOException {
		return getTable(strTableName).toString();
	}

	/**
	 * Constructor for the DBApp
	 */
	public DBApp() {
		existingTables = new ArrayList<String>();
		init();
	}

	/**
	 * initialize the DBApp
	 */
	public void init() {
		metaData = new MetaData();
		existingTables = metaData.getExistingTables();
	}

	/**
	 * Creates a table
	 * @param strTableName
	 * @param htblColNameType
	 * @param htblColNameRefs
	 * @param strKeyColName
	 * @throws DBAppException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void createTable(String strTableName,
			Hashtable<String, String> htblColNameType,
			Hashtable<String, String> htblColNameRefs, String strKeyColName)
			throws DBAppException, IOException, ClassNotFoundException {
		metaData.insertTable(strTableName, htblColNameType, htblColNameRefs,
				strKeyColName);
		existingTables.add(strTableName);
		(new Table(strTableName, htblColNameType, htblColNameRefs,
				strKeyColName)).write();
	}

	/**
	 * Creates an Extensible Hashtable that indexes a certain column of the table
	 * @param strTableName
	 * @param strColName
	 * @throws DBAppException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void createIndex(String strTableName, String strColName)
			throws DBAppException, ClassNotFoundException, IOException {
		metaData.createIndex(strTableName, strColName);
		getTable(strTableName).createIndex(strColName);
	}

	/**
	 * Creates a KDTree for the table
	 * @param strTableName
	 * @param htblColNames
	 * @throws DBAppException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void createMultiDimIndex(String strTableName,
			ArrayList<String> htblColNames) throws DBAppException,
			ClassNotFoundException, IOException {
		getTable(strTableName).createMultiDimIndex(
				new Pair(htblColNames.get(0), htblColNames.get(1)));
	}

	/**
	 * Inserts a record into the specified table
	 * @param strTableName
	 * @param htblColNameValue
	 * @throws DBAppException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void insertIntoTable(String strTableName,
			Hashtable<String, String> htblColNameValue) throws DBAppException,
			FileNotFoundException, IOException, ClassNotFoundException {
		getTable(strTableName).insert(htblColNameValue);
	}

	/**
	 * Deletes a tuple that satisfies a specified condition
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
		getTable(strTableName).deleteFromTable(htblColNameValue, strOperator);
	}

	/**
	 * Returns an iterator to elements that satisfy a specified condition
	 * @param strTable
	 * @param htblColNameValue
	 * @param strOperator
	 * @return
	 * @throws DBEngineException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public Iterator selectFromTable(String strTable,
			Hashtable<String, String> htblColNameValue, String strOperator)
			throws DBEngineException, ClassNotFoundException, IOException {
		return getTable(strTable)
				.selectFromTable(htblColNameValue, strOperator);
	}

	/**
	 * Selects a tuple from the Hashtable
	 * @param strTable
	 * @param strColName
	 * @param strValue
	 * @return
	 * @throws DBEngineException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Tuple selectFromHtable(String strTable, String strColName,
			String strValue) throws DBEngineException, ClassNotFoundException,
			IOException {
		return getTable(strTable)
				.selectFromHtable(strColName, strValue);
	}

	/**
	 * Writes all tables to the disk
	 * @throws DBEngineException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void saveAll() throws DBEngineException, IOException,
			ClassNotFoundException {
		for (String tableName : existingTables) {
			getTable(tableName).write();
		}
	}

	// General Notes:
	// For the parameters, the name documents what is
	// being passed � for example htblColNameType
	// is a hashtable with key as ColName and value is
	// the Type. //
	// strOperator can either be OR or AND (just two) //
	// DBEngineException is a generic exception to avoid
	// breaking the test cases when they run. You can
	// customize the Exception by passing a different message
	// upon creation. //
	// Iterator is java.util.Iterator It is an interface that
	// enables client code to iterate over the results row
	// by row. Do not implement the remove method. //
	// The method saveAll saves all data pages and indices to
	// disk and can be called at any time (not just before
	// the program terminates)
}
