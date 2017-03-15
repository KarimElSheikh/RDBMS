package FauxBots;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;

    /**
     * Quick illustration of a two-dimensional tree.
     */

    /**
     * @author Karim
     *
     */
    public class KdTree implements Serializable
    {
        private class KdNode implements Serializable
        {
            Comparable data[ ];
            KdNode     left;
            KdNode     right;

            KdNode( Comparable item[ ] )
            {
                data = new Comparable[ 2 ];
                data[ 0 ] = item[ 0 ];
                data[ 1 ] = item[ 1 ];
                left = right = null;
            }
        }

        private KdNode root;
        private String tableName;
        private String key1;
        private String key2;
        private HashSet<Pair3<Integer, Integer>> r;

        /**
         * Constructor for the KDTree that takes 2 keys
         * @param tableName
         * @param key1
         * @param key2
         */
        public KdTree(String tableName, String key1, String key2)
        {
        	this.tableName = tableName;
        	this.key1 = key1;
        	this.key2 = key2;
            root = null;
        }
        
        /**
         * Default Constructor for the KDTree
         */
        public KdTree()
        {
            root = null;
        }

        /**
         * Inserts a Comparable to the KDTree
         * @param x
         */
        public void insert( Comparable [ ] x )
        {
            root = insert( x, root, 0 );
        }

        /**
         * Helper method for public void insert( Comparable [ ] x )
         * @param x
         * @param t
         * @param level
         * @return
         */
        private KdNode insert( Comparable [ ] x, KdNode t, int level )
        {
            if( t == null )
                t = new KdNode( x );
            else if( x[ level ].compareTo( t.data[ level ] ) < 0 )
                t.left = insert( x, t.left, 1 - level );
            else
                t.right = insert( x, t.right, 1 - level );
            return t;
        }

        /**
         * Gets the items satisfying and puts them in the Hashset r
         * Retreive them by calling getRange()
         * low[ 0 ] <= x[ 0 ] <= high[ 0 ] and
         * low[ 1 ] <= x[ 1 ] <= high[ 1 ].
         */
        public void printRange( Comparable [ ] low, Comparable [ ] high )
        {
        	r = new HashSet<Pair3<Integer, Integer>>();
            printRange( low, high, root, 0 );
        }

        /**
         * Helper method for the method printRange( Comparable [ ] low, Comparable [ ] high )
         * @param low
         * @param high
         * @param t
         * @param level
         */
        private void printRange( Comparable [ ] low, Comparable [ ] high,
                                 KdNode t, int level )
        {
            if( t != null )
            {
                if( low[ 0 ].compareTo( t.data[ 0 ] ) <= 0 &&
                            low[ 1 ].compareTo( t.data[ 1 ] ) <= 0 &&
                           high[ 0 ].compareTo( t.data[ 0 ] ) >= 0 &&
                           high[ 1 ].compareTo( t.data[ 1 ] ) >= 0 ) {
                	r.add(new Pair3(((Pair2)t.data[0]).pageNumber, ((Pair2)t.data[0]).rowNumber));
                r.add(new Pair3(((Pair2)t.data[1]).pageNumber, ((Pair2)t.data[1]).rowNumber));
                }

                if( low[ level ].compareTo( t.data[ level ] ) <= 0 )
                    printRange( low, high, t.left, 1 - level );
                if( high[ level ].compareTo( t.data[ level ] ) >= 0 )
                    printRange( low, high, t.right, 1 - level );
            }
        }
        
        /**
         * returns the range
         * @return
         */
        public HashSet<Pair3<Integer, Integer>> getRange() {
        	return r;
        }
        
    	/**
    	 * Writes the KDTree to the disk
    	 * @throws IOException
    	 * @throws ClassNotFoundException
    	 */
    	public void write() throws IOException, ClassNotFoundException {
    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
    		new File("data/" + tableName + "-KDTree-" + key1 + '-' + key2 + ".class")));
    		oos.writeObject(this);
    		oos.close();
    	}

//        public static void main( String [ ] args )
//        {
//            KdTree t = new KdTree( );
//            
//            System.out.println( "Starting program" );
//            for( int i = 300; i < 370; i++ )
//            {
//                Integer [ ] it = new Integer[ 2 ];
//                it[ 0 ] = new Integer( i );
//                it[ 1 ] = new Integer( 2500 - i );
//                t.insert( it );
//            }
//
//            Integer [ ] low = new Integer[ 2 ];
//            low[ 0 ] = new Integer( 70 );
//            low[ 1 ] = new Integer( 2186 );
//            Integer [ ] high = new Integer[ 2 ];
//            high[ 0 ] = new Integer( 1200 );
//            high[ 1 ] = new Integer( 2200 );
//
//            t.printRange( low, high );
//        }
    }
