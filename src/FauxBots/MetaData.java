package FauxBots;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrewmagdy
 * @author karimelsheikh
 * @author minahany
 * @author ahmedabdelwahab
 */
public class MetaData {
	private static final String configFile = "DBApp";
	private static final String key1 = "MetadataFile";
	private static final String key2 = "TemporaryMetadataFile";
	private String strFile;
	private String strTmpFile;

	/**
	 * Constructor for MetaData. It loads the filenames from the Database config
	 * file.
	 */
	public MetaData() {
		ResourceBundle bundle = ResourceBundle.getBundle(configFile);
		strFile = bundle.getString(key1);
		strTmpFile = bundle.getString(key2);
	}

	/**
	 * Adds the index information to the metadata when creating a KDTree
	 * @param strTableName
	 * @param htblColNames
	 * @throws DBAppException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void createMultiDimIndex(String strTableName,
			ArrayList<String> htblColNames) throws DBAppException,
			ClassNotFoundException, IOException {
		for (String str : htblColNames) {
			createIndex(strTableName, str);
		}
	}

	/**
	 * Returns the all the table names in the database in an Array List
	 * @return
	 */
	public ArrayList<String> getExistingTables() {
		HashSet<String> tables = new HashSet<String>();
		ArrayList<String> res = new ArrayList<String>();
		BufferedReader br = openRead(strFile);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String strTableName = line.split(",")[0];
				tables.add(strTableName);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.addAll(tables);
		return res;

	}

	/**
	 * Adds Index To the MetaData Table
	 * 
	 * @param strTableName
	 *            Name Of the table to add index to
	 * @param strColName
	 *            Name of the index column
	 */
	public void createIndex(String strTableName, String strColName) {
		try {
			BufferedReader br = openRead(strFile);
			BufferedWriter bf = openWrite(strTmpFile, false); // Write To temp
																// file
			String line;
			while ((line = br.readLine()) != null) {
				String strParts[] = line.split(",");
				boolean bFound = strParts[0].equals(strTableName);// check if it
																	// is the
																	// required
																	// table
				bFound &= strParts[1].equals(strColName); // check if it is
															// required column
				if (bFound) {
					strParts[4] = "true"; // Change Index Field To True
					for (String str : strParts) { // Rewrite The line with the
													// changes
						bf.write(str + ',');
					}

				} else {
					bf.write(line); // No changes Write Line as it is
				}
				bf.write('\n');

			}
			br.close();
			bf.close();
			editFile(); // Replace metadata with the new file
			// if(!bfound) throw exception
		} catch (IOException ex) {
			Logger.getLogger(MetaData.class.getName()).log(Level.SEVERE, null,
					ex);
		}
	}

	// handling duplicates should be added
	/*
	 * if(!isAlpha(strTableName)) throw exception ; for (String key :
	 * htblColNameType.keySet()) { //if(!isAlpha(key)) throw exception; }
	 */

	/**
	 * Inserts Table to the MetaData Table
	 * 
	 * @param strTableName
	 *            Name Of the table to add index to
	 * @param htblColNameType
	 *            The table attributes' names and corresponding types
	 * @param htblColNameRefs
	 *            The table attributes's names and which ones refer to other
	 *            tables
	 * @param strKeyColName
	 *            The table's key attribute name
	 */
	public void insertTable(String strTableName,
			Hashtable<String, String> htblColNameType,
			Hashtable<String, String> htblColNameRefs, String strKeyColName)
			throws DBAppException {
		try {
			BufferedWriter bf = openWrite(strFile, true);
			for (String key : htblColNameType.keySet()) {
				try {
					bf.write(strTableName + ',' + key);
					bf.write(',' + htblColNameType.get(key));
					bf.write("," + key.equals(strKeyColName));
					bf.write(",false," + htblColNameRefs.get(key));
					bf.write('\n');
				} catch (IOException ex) {
					Logger.getLogger(MetaData.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
			bf.flush();
			bf.close();// clean up
		} catch (IOException ex) {
			Logger.getLogger(MetaData.class.getName()).log(Level.SEVERE, null,
					ex);
		}
	}

	/**
	 * Opens File For Writing
	 * 
	 * @param strPath
	 *            The Required File Path
	 * @param bAppend
	 *            Specifies if to append to existing file or create new file
	 */
	private BufferedWriter openWrite(String strPath, boolean bAppend) {
		BufferedWriter bf = null;
		File file = new File(strPath);
		try {
			bf = new BufferedWriter(new FileWriter(file, bAppend));
		} catch (IOException ex) {
			Logger.getLogger(MetaData.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		return bf;
	}

	/**
	 * Opens File For Reading
	 * 
	 * @param strPath
	 *            The Path of The required file
	 */
	private BufferedReader openRead(String strPath) {
		BufferedReader br = null;
		File file = new File(strPath);
		
		
		try {
			if(!file.exists()) 
			file.createNewFile();
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(MetaData.class.getName()).log(Level.SEVERE, null,
					ex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return br;
	}

	/**
	 * Replaces The Original File With The Temp one
	 */
	private void editFile() {
		File originalFile = new File(strFile);
		File tmpFile = new File(strTmpFile);
		originalFile.delete();
		tmpFile.renameTo(originalFile);
	}

	/**
	 * Checks if string consist of only alphanumeric characters
	 * 
	 * @return <tt>true</tt> if the string consists only of alphanumeric
	 *         characters
	 */
	@SuppressWarnings("unused")
	private boolean isAlpha(String name) {
		char[] chars = name.toCharArray();

		for (char c : chars) {
			if (!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}
}
