package FauxBots;

public class Pair3<L,R> implements Comparable {

  final L left;
  final R right;

  /**
   * Constructor for the Pair3 class
 * @param left
 * @param right
 */
public Pair3(L left, R right) {
    this.left = left;
    this.right = right;
  }

  /**
   * gets the Left value of the Pair3
 * @return
 */
public L getLeft() { return left; }
  /**
   * gets the Right value of the Pair3
 * @return
 */
public R getRight() { return right; }

  @Override
  public int hashCode() { return left.hashCode() ^ right.hashCode(); }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair3)) return false;
    Pair3 pairo = (Pair3) o;
    return this.left.equals(pairo.getLeft()) &&
           this.right.equals(pairo.getRight());
  }

@Override
	public int compareTo(Object o) {
	Pair3 pairo = (Pair3) o;
	if((this.left).equals(pairo.getLeft())) {
		return ((Comparable) this.right).compareTo(pairo.getRight());
	}
	return ((Comparable) this.left).compareTo(pairo.getLeft());
}
  
  

}