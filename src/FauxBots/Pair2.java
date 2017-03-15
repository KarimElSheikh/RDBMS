package FauxBots;

import java.io.Serializable;

public class Pair2 implements Comparable, Serializable {
	String s;
	int pageNumber;
	int rowNumber;
	/**
	 * Constructor for the Pair2 class
	 * @param s
	 * @param pageNumber
	 * @param rowNumber
	 */
	public Pair2 (String s, int pageNumber, int rowNumber) {
		this.s = s;
		this.pageNumber = pageNumber;
		this.rowNumber = rowNumber;
	}
	
	public int compareTo(Object o) {
		return s.compareTo(((Pair2)o).s);
	}
	
	public String toString() {
		return "page = " + pageNumber + ", row = " + rowNumber + ", identified by " + s;
	}
}
