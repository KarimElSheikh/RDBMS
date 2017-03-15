package FauxBots;

/**
 *
 * @author andrewmagdy
 * @author karimelsheikh
 * @author minahany
 * @author ahmedabdelwahab
 */
@SuppressWarnings("serial")
class DBAppException extends Exception {
	
    /**
     * Constructor for DBAppException.
     * @param message the Exception's message
     */
    public DBAppException (String message) {
    	super(message);
    }
}
