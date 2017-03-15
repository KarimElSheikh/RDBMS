package FauxBots;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Pointer implements Serializable {
	
	Object value;
	int pageNumber;
	int rowNumber;
	/**
	 * Constructor for the Pointer class
	 * @param value
	 * @param pageNumber
	 * @param rowNumber
	 */
	public Pointer(Object value, int pageNumber, int rowNumber) {
		this.value = value;
		this.pageNumber = pageNumber;
		this.rowNumber = rowNumber;
	}
}
