package FauxBots;

import java.io.Serializable;

public class Pair implements Serializable {
	String s1;
	String s2;
	/**
	 * Constructor for the Pair class
	 * @param s1
	 * @param s2
	 */
	public Pair (String s1, String s2) {
		this.s1 = s1;
		this.s2 = s2;
	}
}
