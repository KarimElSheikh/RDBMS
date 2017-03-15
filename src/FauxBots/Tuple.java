package FauxBots;

import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 * @author andrewmagdy
 * @author karimelsheikh
 * @author minahany
 * @author ahmedabdelwahab
 */
public class Tuple implements Serializable {

	private static final long serialVersionUID = 116275884317573586L;
	private Hashtable<String, String> htblColNameValue;
    private boolean deleted;

    
    /**
     * Constructor for Tuple.
     * @param htblColNameValue The Table attribute names and their corresponding values
     */
    public Tuple(Hashtable<String, String> htblColNameValue) {
        this.htblColNameValue = htblColNameValue;
    }
    
    /**
     * Returns the hashtable of the tuple
     * @return
     */
    public Hashtable<String, String> getHashtable() {
    	return htblColNameValue;
    }
    
    
    /**
     * Gets the value of the specified attribute name
     * @param key The attribute name
     * @return The value of the specified attribute name
     */
    public Object get(String key) {
    	return htblColNameValue.get(key);
    }
    
    /**
     * Checks if the tuple is deleted
     * @return
     */
    public boolean isDeleted() {
    	return deleted;
    }
    
    /**
     * Sets the deleted status of the tuple to the specified value
     * @param value
     */
    public void setDeleted(boolean value) {
    	deleted = value;
    }
    
    @Override
	public String toString() {
		return "Tuple [htblColNameValue=" + htblColNameValue + "]";
	}

}
