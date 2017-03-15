package FauxBots;

/**
 *
 * @author andrewmagdy
 * @author karimelsheikh
 * @author minahany
 * @author ahmedabdelwahab
 */
@SuppressWarnings("serial")
class DBEngineException extends Exception {
	
    /**
     * Constructor for DBEngineException.
     * @param message the Exception's message
     */
	public DBEngineException (String m) {
    	super(m);
    }
}
