/**
 * Class to store and and retrieve 2 ints
 * @author Craig
 *
 */
public class Duos {
	private int int1;
	private int int2;
	
	/**
	 * Constructor
	 * @param one the first num
	 * @param two the second num
	 */
	public Duos(int one, int two){
		int1 = one;
		int2 = two;
	}
	/**
	 * Getters
	 * @return num one
	 */
	public int getIntOne() {
		return int1;
	}
	/**
	 * Getters
	 * @return num two
	 */
	public int getIntTwo() {
		return int2;
	}
	/**
	 * Adds to one
	 * @param adder the amount to increment 1 by
	 */
	public void addToOne(int adder) {
		int1 +=adder;
	}
	/**
	 * Adds to two
	 * @param adder the amount to increment 2 by
	 */
	public void addToTwo(int adder) {
		int2 +=adder;
	}
	/**
	 * Adds onto both numbers stored
	 * @param one the amount to increment 1 by
	 * @param two the amount to increment 2 by
	 */
	public void increment(int one, int two) {
		int1+=one;
		int2+=two;
	}
}
