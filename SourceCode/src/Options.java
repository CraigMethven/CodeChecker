/**
 * 
 * Class for storing which checks should be run
 * @author Craig
 *
 */
public final class Options {
	//Done
	public static boolean checkSearchWords = true; //Checks to see if the words searched for are present
	//Done
	public static boolean compareToOtherInputs = true;//Compare to other inputs to see if there are like methods
	//Done
	public static boolean checkNames = true; //Check that variables and methods match naming conventions
	//Done
	public static boolean checkCommentsAboveMethodsAndClasses = true; //Checks for a comment above a method
	//Done
	public static boolean checkJavadocComments = true; //Checks for @param and @return on methods
	
	//Discarded as was the same as checking for storing method returned variables
	//public static boolean checkSanitisedInputs = true; //Check if inputs have been sanitised 
	//Discarded
	//public static boolean checkForRepeatedCode = true; //Check for repeated code that should be in a method
	//Done
	public static boolean checkOveruseOfStaticVariables = true;//Check for over use of static variables
	//Done
	public static boolean checkOveruseOfStaticMethods= true;//Check for over use of static methods
	//Done
	public static boolean checkForLargeMethods = true; //Checks if methods are too large and should be split up
	//Done
	public static boolean checkMainMethodLength = true; //Check if the main method is too long
	//Done
	public static boolean checkCorrectUseOfAccessModifiers = true;//Check that public/private/protected are used correctly
	//Done
	public static boolean checkForLargeIfThenChains = true;//Check for large if then chains
	//Discarded
	//public static boolean checkIndentation = true;//Check that the indentation is correct
	//Done
	public static boolean checkSemicolonAfterOpeningStatements = true; //Checks that you go into ifs/fors ect
	//Done
	public static boolean checkStringComparisons =true;//Check that strings are compared using correct means
	//Done
	public static boolean checkRecursionOnMethodsWithNoInput = true;//Makes sure recursion is only used properly
	//Discarded
	//public static boolean checkSpelling = true;//Spell check
	//Discarded
	//public static boolean halsteadComplexityOutput = true;//output the halstead complexity of the code
	//Done
	public static boolean checkStorageOfReturnedVariable = false; //Checks that if a method returns a variable that it is stored
	//Done
	public static boolean checkThatScannerIsClosedOnceOpened = true;//Check that scanner is closed properly
	//Descriptions of each of the errors that display upon hover
	public static String[] checkDescriptions = {
			"Checks to see if the words searched for are present",
			"<i>Rerun Checks to get this Information</i><br>Compare to other code inputted to see if there are similarities",
			"Check that variables and methods match naming conventions<br>such as variables starting with lowercase whereas classes start with uppercase",
			"Checks if there is a comment above each method",
			"<i>Java Only</i><br>Checks for @param and @return on java methods",
			//"Check if inputs have been sanitized such as making sure ints inputted are ints",
			//"Check for repeated code that should be in a method",
			"<i>Only Displays For Folders</i><br>Check for over use of static variables",
			"<i>Only Displays For Folders</i><br>Check for over use of static methods",
			"Checks if methods are too large and should be split up to make the code more modular",
			"<i>Java Only</i><br>Check if the main method is too long",
			"Check that public/private/protected are used correctly",
			"Check for large if/then chains",
			//"Check that the indentation is correct",
			"Checks that there isn't a semi-colon after if/for/then/else type operations stopping the code from entering them",
			"Check that strings are compared using correct means (java uses .equals())",
			"Checks to see if all recursive methods takes an input",
			"Checks that if a method returns a variable then the call of that method stores the variable",
			"<i>Java Only</i><br>Check that scanner has been closed"
			//"Spell Check",
			//"Outputs the halstead complexity of the code",
	};
}
