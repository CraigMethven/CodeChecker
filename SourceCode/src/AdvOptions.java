import java.util.List;

/**
 * Class for storing all of the options that aren't whether a check should be run
 * @author Craig
 *
 */
public class AdvOptions {
	public static boolean darkMode = true; //if dark mode should be active
	public static int staticMethodOverusePercentage = 25; //The percentage threshold of when the program will shout at you for using too many static methods
	public static int staticVariableOverusePercentage = 25;
	public static int publicVariableOverusePercentage = 25;
	public static int mainMethodMaxLength = 10; // the number of lines the main method has to be greater than to display an error
	public static int maxMethodLength = 30; // the number of lines a method can be before you're told to split it up
	public static int minMethodSizeToCompare = 8; // the number of lines line a method needs to be before it is compared to others to look for cheating
	public static int percentOfSimilarities=90;//the percentage of lines that need to be similar before it is marked as suspicious
	public static int numOfElseNeededToBeConsideredLarge = 4;
	public static List<String> searchTerms;
}
