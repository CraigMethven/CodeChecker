import java.util.List;
import java.util.Vector;

/**
 * Class to store classes of the code inputed
 */
public class ClassStore extends CodeChunk {
	private List<MethodStore> myMethods;
	private int aboveCommentLength;
	private FileStore parent;
	
	/**
	 * Constructor
	 */
	public ClassStore(){
		super();
		initClass();
	}
	/**
	 * Constructor
	 * @param myCode the code of this class
	 */
	public ClassStore(List<String> myCode){
		super(myCode);
		initClass();
	}
	/**
	 * Constructor
	 * @param myCode the code of this class
	 * @param fileExtention the file type of this class
	 */
	public ClassStore(List<String> myCode, String fileExtention){
		super(myCode);
		fileType = fileExtention;
		initClass();
	}
	/**
	 * Constructor
	 * @param myCode the code of this class
	 * @param fileExtention the file type of this class
	 * @param starting the starting position of this class in the file
	 */
	public ClassStore(List<String> myCode, String fileExtention, int starting){
		super(myCode);
		fileType = fileExtention;
		startLine = starting;
		initClass();
	}
	
	/**
	 * Constructor
	 * @param myCode the code of this class
	 * @param fileExtention the file type of this class
	 * @param starting the starting position of this class in the file
	 * @param tempParent the parent of this class
	 */
	public ClassStore(List<String> myCode, String fileExtention, int starting, FileStore tempParent){
		super(myCode);
		parent = tempParent;
		fileType = fileExtention;
		startLine = starting;
		initClass();
	}
	/**
	 * Initialises the method. Basic method called after every constructor to set up variables
	 */
	private void initClass() {
		myMethods = new Vector<MethodStore>();
		errorOutput = new Vector<String>();
		name = findClassName();
//		System.out.println("Class x: "+name);
//		System.out.println(myCode.get(0));
		findMethods();
		removeWrongMethods();
		
		findVariables(removeComments(getClassWithoutMethods()));
		
		//printAllVariables();
		initCleanedCode();
		runChecks();
	}
	
	/**
	 * returns the path of the class
	 * @return the class path
	 */
	public String getClassPath() {
		if(parent!=null) {
			return "<em>"+parent.getFilePath()+"</em>: <em>"+name+".</em>";
		}
		return null;
	}
	
	/**
	 * Returns parent
	 * @return the parent
	 */
	public FileStore getParent() {
		return parent;
	}
	
	/**
	 * gets the number of methods
	 * @return the number of methods
	 */
	public int getNumOfMethods() {
		return myMethods.size();
	}
	/**
	 * Basic getter
	 * @return the methods
	 */
	public List<MethodStore> getMethods(){
		return myMethods;
	}
	/**
	 * gets the number of class variables
	 * @return the number of class variables
	 */
	public int getNumOfVariables() {
		int numOfVariables = 0;
		int numOfMethods = myMethods.size();
		for(int i = 0;i<numOfMethods;i++) {
			numOfVariables+=myMethods.get(i).getNumOfVariables();
		}
		return myVariables.size()+numOfVariables;
	}
	/**
	 * gets the number of class variables
	 * @return the number of class variables
	 */
	public int getNumOfClassVariables() {
		return myVariables.size();
	}
	
	/**
	 * Gets the classes code without any of the methods inside it
	 * @return the list of strings containing lines of code
	 */
	public List<String> getClassWithoutMethods() {
		List<String> tempCode = new Vector<String>(myCode);
		int numOfMethods = myMethods.size();
		int prevHighest = 0;
		int totalRemoved = 0;
		
		//For all methods
		for(int i = 0;i<numOfMethods;i++) {
			MethodStore curMethod = myMethods.get(i);
			//Get start position based on classes start position
			int curStartPos = curMethod.getStartPos() - getStartPos();
			//Get the length of the method
			int theLength = curMethod.getLength();
			//If the method is below the past removed methods
			if(curStartPos>prevHighest) {
				removeLines(tempCode, curStartPos-totalRemoved, theLength);
				totalRemoved += theLength;
				prevHighest = curStartPos+theLength;
			}
			//If the method ends below the previously removed methods
			else if(curStartPos+theLength>prevHighest) {
				removeLines(tempCode, prevHighest-totalRemoved, (curStartPos-prevHighest)+theLength);
				totalRemoved += (curStartPos-prevHighest)+theLength;
				prevHighest = curStartPos+theLength;
			}
		}
		//Main.printList(tempCode);
		return removeComments(tempCode);
	}
	
	/**
	 * Removes a ground of lines from the list inputed
	 * @param myLines the list needing lines removed
	 * @param startPos the position in the list to remove lines from
	 * @param numLinesToRemove the number of lines to remove from that position
	 */
	public static void removeLines(List<String> myLines, int startPos, int numLinesToRemove) {
		for(int i = 0;i<numLinesToRemove;i++) {
			if(myLines.size()>startPos) {
				myLines.remove(startPos);
			}
			else {
				break;
			}
		}
	}
	
	//CURRENTLY DOESN'T WORK IF CLASS HAS A CAPITAL C
	/**
	 * Finds the name of this class
	 * @return the name of the class
	 */
	private String findClassName() {
		List<String> tempCode = removeComments(myCode);
		String className = "";
		int codeSize = tempCode.size();
		//Loop through the uncommented code
		for(int i = 0; i<codeSize;i++) {
			String myLine = tempCode.get(i);
			//If the currently line is the class definition
			if(myLine.contains("class ")) {
				char[] myLineChars = myLine.split("class ")[1].toCharArray();
				int lineSize = myLineChars.length;
				//Find the word before the first { or space //}
				for(int counter = 0; counter<lineSize;counter++) {
					if(myLineChars[counter] == ' ' || myLineChars[counter] == '{') {
						return className;
					}
					className += myLineChars[counter];
				}
				return className;
			}
		}
		return "No Name Found";
	}
	
	/**
	 * Gets and saves the length of the comment above the method given the first line of the method
	 * @param methodHeaderLine The first line of the method. The method definition
	 */
	public void getAboveCommentLength() {
		String classHeaderLine = removeComments(myCode).get(0);
		int codeLength = myCode.size();
		for(int i =0;i<codeLength;i++) {
			if(myCode.get(i).equals(classHeaderLine)){
				aboveCommentLength = i;
				return;
			}
		}
	}
	
	/**
	 * Finds a word on a inputed line that comes before an inputed character
	 * @param myLine The line to search through
	 * @param finder what character to check before
	 * @return the word before the character inputed
	 */
	public static String findWordBeforeChar(String myLine, char finder) {
		char[] tempLine = myLine.toCharArray();
		int myLineSize = myLine.length();
		int bracketPos;
		String theWord = "";
		//Loop through the line
		for(int i = 0; i<myLineSize;i++) {
			if(tempLine[i] == finder) {
				bracketPos = i;
				//Loop backwards through the line
				while(i>0) {
					i--;
					if(i<0) {
						return "Error finding name";
					}
					//If a tab or space was found then save the word
					else if(tempLine[i] == ' ' || tempLine[i] == '\t') {
						//Loop forward through the line again to save the word
						for(int counter =i+1;counter<bracketPos;counter++) {
							theWord += tempLine[counter];
						}
						return theWord;
					}
				}
			}
		}
		return theWord;
	}
	
	/**
	 * Finds the methods within the class and saves them as methodStores
	 */
	private void findMethods() {
		List<Integer> methodPos = new Vector<Integer>();
		int classLength = myCode.size();
		boolean usable;
		
		//Part for finding methods
		//For every line of code
		for(int i = 0; i<classLength;i++) {
			usable = true;
			String myLine = myCode.get(i);
			//Make sure it contains the characters that a method statement must
			if(myLine.contains("(") && myLine.contains(")") && !myLine.contains(".")) {
				if(i+1>=classLength) {
					continue;
				}
				if(myLine.contains("{")||myCode.get(i+1).strip().equals("{")){
					//Make sure that it isn't a different statement type
					for(int counter = 0; counter<CodeCheckerConstants.multilineTypes.length; counter++) {
						if(myLine.strip().startsWith(CodeCheckerConstants.multilineTypes[counter]) || myLine.contains("class ")) {
							usable = false;
							break;
						}
					}
					if(usable) {
						methodPos.add(i);
					}
				}	
			}	
		}
		//Part for saving methods
		if(!methodPos.isEmpty()) {			
			List<Duos> myPoints = findEntireStatement(myCode,methodPos);
			//Saves all the methods
			if(!myPoints.isEmpty()) {
				int numberOfClasses = myPoints.size();
				for(int i = 0; i<numberOfClasses; i++) {
//					if(myPoints.get(i).getIntTwo()-myPoints.get(i).getIntOne()<2) {
//						continue;
//					}
					myMethods.add(new MethodStore(myCode.subList(myPoints.get(i).getIntOne(), myPoints.get(i).getIntTwo()),fileType,startLine+myPoints.get(i).getIntOne(),this));
				}
			}
		}
	}
	
	/**
	 * Removes any lines that were marked as a false positive for being a method
	 */
	public void removeWrongMethods() {
		int numOfMethods = myMethods.size();
		for(int i = 0;i<numOfMethods;i++) {
			MethodStore curMethod = myMethods.get(i);
			if(curMethod.getName()==null || curMethod.getName().contains("//")||curMethod.getName().equals("null")||curMethod.getName().equals("catch")) {
				myMethods.remove(i);
				i--;
				numOfMethods = myMethods.size();
			}
		}
	}
	
	/**
	 * Checks to see if the class or its methods contain any errors
	 * @return true if there are errors
	 */
	public boolean checkClassForErrors() {
		//If this class contains errors
		if(checkForErrors()) {
			return true;
		//Check all of the methods
		}else if(!myMethods.isEmpty()) {
			int numOfMethods = myMethods.size();
			for(int i = 0;i<numOfMethods;i++) {
				//If method contains error return true
				if(myMethods.get(i).checkForErrors()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Runs the checks again 
	 */
	public void rerunChecks() {
		runChecks();
		int numOfMethods = myMethods.size();
		for(int i = 0;i<numOfMethods;i++) {
			myMethods.get(i).runChecks();
		}
	}
	
	/*
	 * START OF ERROR CHECKING 
	 */
	
	/**
	 * populates the error summary with the correct numbers
	 * @return the completed error summary
	 */
	public ErrorSummaryCounter populateErrorSummary() {
		if(!alreadyCountedErrors) {
			int numOfMethods = myMethods.size();
			for(int i = 0;i<numOfMethods;i++) {
				errorCounter.add(myMethods.get(i).getErrorSummary());
			}
			alreadyCountedErrors = true;
		}
		
		return errorCounter;
	}
	
	/**
	 * gets all of the errors of this class and all of its methods
	 * @param numberOfTabs the number of tabs to start printing the errors at
	 * @param allErrors the list to save the errors to. Set as null to print to console
	 */
	public void printErrorsClass(int numberOfTabs, List<String> allErrors){
		//If there are no errors then return
		if(!checkClassForErrors()) {
			return;
		}
		//If we are just printing to console
		if(allErrors == null) {
			System.out.print("\n");
			CodeChunk.printTabs(numberOfTabs,null);
			System.out.println("Class "+name+"[Line "+startLine+"-"+(startLine+myCode.size()-1)+"]:");
		//For printing to the string list
		}else {
			allErrors.add("\n");
			CodeChunk.printTabs(numberOfTabs,allErrors);
			
			if(!checkForErrors()) {
				allErrors.add("Class&nbsp;"+CodeCheckerConstants.htmlClassColor+name+CodeCheckerConstants.htmlColorClose+"[Line "+startLine+"-"+(startLine+myCode.size()-1)+"]:\n");
			}
			else {
				allErrors.add("Class&nbsp;"+CodeCheckerConstants.htmlClassColor+name+CodeCheckerConstants.htmlColorClose+"[Line "+startLine+"-"+(startLine+myCode.size()-1)+"]:");
			}
		}
		printErrors(numberOfTabs, allErrors);
		
		
//		if(!printErrors(numberOfTabs)) {
//			printTabs(numberOfTabs);
//			System.out.println("<No Errors>");
//		}
		//Print the errors of all the methods in the class
		if(!myMethods.isEmpty()) {
			int numOfMethods = myMethods.size();
			//for all methods
			for(int i = 0;i<numOfMethods;i++) {
				MethodStore gottenMethod = myMethods.get(i);
				//If they contain errors print them
				if(gottenMethod.checkForErrors()) {
					gottenMethod.printErrors(numberOfTabs+1, allErrors);
				}
			}
		}
	}
	
	/**
	 * Sets each of the classes methods to have the cleaned version of their code stored
	 */
	public void initCleanedCode(){
		int numOfMethods = myMethods.size();
		for(int i = 0;i<numOfMethods;i++) {
			MethodStore curMethod = myMethods.get(i);
			curMethod.setCleanedCode(curMethod.cleanCode(removeComments(curMethod.getCode()), myVariables));
		}
	}
	
	/**
	 * Saves inputed errors to the error output list
	 * @param theError The error that is needing to be added to the error output file
	 * @param errorColor the color the error should be printed in to specify the severity
	 */
	public void writeError(String theError, String errorColor) {
		errorOutput.add("-"+errorColor+theError+CodeCheckerConstants.htmlColorClose);
	}
	
	/**
	 * Runs all the checks selected from the options menu
	 */
	public void runChecks() {
		errorOutput = new Vector<String>();
		errorCounter = new ErrorSummaryCounter();
		alreadyCountedErrors = false;
		if(Options.checkCommentsAboveMethodsAndClasses) {
			checkAboveComment();
		}
		if(Options.checkNames) {
			checkNameOfClass();
			printVariablesNamedWrong();
		}
		if(Options.checkCorrectUseOfAccessModifiers) {
			checkPrivacySettings();
		}
//		if(Options.checkOveruseOfStaticVariables) {
//			checkStaticVariables();
//		}
//		if(Options.checkOveruseOfStaticMethods) {
//			checkStaticMethods();
//		}
		if(fileType.equals("java")) {
			if(Options.checkThatScannerIsClosedOnceOpened) {
				checkScannerClosed();
			}
		}	
//		if(Options.checkStorageOfReturnedVariable) {
//			checkStorageOfReturnedVars();
//		}
	}
	
	/**
	 * Checks to see if there is a comment above this class
	 */
	public void checkAboveComment() {
		getAboveCommentLength();
		
		if(aboveCommentLength == 0) {
			errorCounter.incrementNoComments();
			writeError("No above comment",CodeCheckerConstants.htmlYellowText);
		}
	}
	
	/**
	 * Checks the name of the class to make sure that it matches naming conventions
	 */
	public void checkNameOfClass() {
		if(name==null || name.isBlank()) {
			errorCounter.incrementClassNameConvenstion();
			writeError("Class doesn't have a name",CodeCheckerConstants.htmlRedText);
			return;
		}
		if(Character.isLowerCase(name.toCharArray()[0])) {
			errorCounter.incrementClassNameConvenstion();
			writeError("Class name starts with lowercase letter",CodeCheckerConstants.htmlOrangeText);
		}
	}
	
	/**
	 * Prints the variables that are named wrong
	 */
	public void printVariablesNamedWrong() {
		List<String> printers = getVariablesNamedWrong();
		if(!printers.isEmpty() && printers!=null) {
			int numOfPrints = printers.size();
			for(int i = 0;i<numOfPrints;i++) {
				errorCounter.incrementVariableNameConvenstion();
				writeError("Class variable <em>"+printers.get(i)+"</em> starts with a capital letter",CodeCheckerConstants.htmlOrangeText);
			}
		}
	}
	
	/**
	 * Counts the number of static methods stored
	 * @return the number of static methods
	 */
	public int getStaticMethodCount() {
		int numOfMethods = myMethods.size();
		int numOfStatic = 0;
		for(int i = 0;i<numOfMethods;i++) {
			if(myMethods.get(i).getStatic()) {
				numOfStatic++;
			}
		}
		return numOfStatic;
	}
	
	/**
	 * increments the duo inputed with the method size and static method count
	 * @param staticCounter a duo. Num one stores the total number of methods, num two stores the number of static methods
	 */
	public void incrementDuoFromStaticMethods(Duos staticCounter) {
		staticCounter.increment(myMethods.size(), getStaticMethodCount());
	}
	
	/**
	 * increments the duo inputed with the variables size and static variables count
	 * @param staticCounter a duo. Num one stores the total number of variables, num two stores the number of static variables
	 */
	public void incrementDuoFromStaticVariables(Duos staticCounter) {
		staticCounter.increment(myVariables.size(), getNumOfStaticVariables());
	}
	
	/**
	 * increments the duo inputed with the variables size and public variables count
	 * @param staticCounter a duo. Num one stores the total number of variables, num two stores the number of public variables
	 */
	public void incrementDuoFromPublicVariables(Duos staticCounter) {
		staticCounter.increment(myVariables.size(), getNumOfPublicVariables());
	}
	
	/**
	 * gets the number of static class variables
	 * @return num of static variables
	 */
	public int getNumOfStaticVariables() {
		int numOfVars = myVariables.size();
		int numOfStatic = 0;
		for(int i = 0;i<numOfVars;i++) {
			if(myVariables.get(i).getIfStatic()) {
				numOfStatic++;
			}
		}
		return numOfStatic;
	}
	
	/**
	 * gets the number of public class variables
	 * @return num of public variables
	 */
	public int getNumOfPublicVariables() {
		int numOfVars = myVariables.size();
		int numOfPublic = 0;
		for(int i = 0;i<numOfVars;i++) {
			if(myVariables.get(i).getPrivacy().toLowerCase().equals("public")) {
				numOfPublic++;
			}
		}
		return numOfPublic;
	}
	
	/**
	 * Checks to see if the class variables have a privacy rating and prints the amount of them that are public
	 */
	public void checkPrivacySettings(){
		int numOfVariables = myVariables.size();
//		int publicCount = 0;
		//Checks that variables have a privacy
		for(int i = 0;i<numOfVariables;i++) {
			VariableStore curVariable = myVariables.get(i);
			if(curVariable.getPrivacy().isBlank()) {
				errorCounter.incrementPrivacyVariables();
				writeError("Class variable <em>"+curVariable.getName()+ "</em> doesn't have a privacy set (eg public, protected, private)",CodeCheckerConstants.htmlOrangeText);
			}
//			else if(curVariable.getPrivacy().equals("public")) {
//				errorCounter.incrementPublicVariable();
//				publicCount++;
//			}
		}
		//Checks how many of them are public
//		if(publicCount!=0) {
//			int publicPercentage = (publicCount*100)/numOfVariables;
//			if(publicPercentage<=25) {
//				writeError(publicPercentage+"% of the class variables are public",CodeCheckerConstants.htmlYellowText);
//			}
//			else if(publicPercentage<=50) {
//				writeError(publicPercentage+"% of the class variables are public",CodeCheckerConstants.htmlOrangeText);
//			}
//			else {
//				writeError(publicPercentage+"% of the class variables are public",CodeCheckerConstants.htmlRedText);
//			}
//		}
	}
	
	/**
	 * Checks to see how many of the class variables are static
	 */
	public void checkStaticVariables() {
		int numOfVariables = myVariables.size();
		int staticCount = 0;
		for(int i = 0;i<numOfVariables;i++) {
			if(myVariables.get(i).getIfStatic()) {
				errorCounter.incrementStaticVariable();
				staticCount++;
			}
		}
		if(staticCount>0) {
			int staticPercentage = (staticCount*100)/numOfVariables;
			if(staticPercentage<10) {
				return;
			}
			if(staticPercentage<=33) {
				writeError(staticPercentage+"% of the class variables are static",CodeCheckerConstants.htmlYellowText);
			}
			else if(staticPercentage<=66) {
				writeError(staticPercentage+"% of the class variables are static",CodeCheckerConstants.htmlOrangeText);
			}
			else {
				writeError(staticPercentage+"% of the class variables are static",CodeCheckerConstants.htmlRedText);
			}
		}
	}
	
	/**
	 * Checks to see how many of the classes methods are static
	 */
	public void checkStaticMethods() {
		int numOfMethods = myMethods.size();
		int staticCount = 0;
		for(int i = 0;i<numOfMethods;i++) {
			if(myMethods.get(i).getStatic()) {
				errorCounter.incrementStaticMethods();
				staticCount++;
			}
		}
		if(staticCount>0) {
			int staticPercentage = (staticCount*100)/numOfMethods;
			if(staticPercentage<10) {
				return;
			}
			if(staticPercentage<=33) {
				writeError(staticPercentage+"% of the methods in this class are static",CodeCheckerConstants.htmlYellowText);
			}
			else if(staticPercentage<=66) {
				writeError(staticPercentage+"% of the methods in this class are static",CodeCheckerConstants.htmlOrangeText);
			}
			else {
				writeError(staticPercentage+"% of the methods in this class are static",CodeCheckerConstants.htmlRedText);
			}
		}
	}
	
	/**
	 * Checks to see if a class variable is a scanner and if so that it was closed
	 */
	public void checkScannerClosed() {
		String scannerName = "";
		int numOfVariables = myVariables.size();
		for(int i = 0;i<numOfVariables;i++) {
			if(myVariables.get(i).getType().equals("Scanner")) {
				scannerName = myVariables.get(i).getName();
				break;
			}
		}
		if(!scannerName.isBlank()) {
			int numOfMethods = myMethods.size();
			for(int i = 0;i<numOfMethods;i++) {
				if(myMethods.get(i).checkScannerClosed(scannerName)) {
					return;
				}
			}
			errorCounter.incrementScannerClosed();
			writeError("Scanner <em>"+scannerName+"</em> has not been closed",CodeCheckerConstants.htmlRedText);
		}
	}
	
	
	
	/**
	 * Checks if methods calls to methods that return variables if the variable is stored
	 */
	public void checkStorageOfReturnedVars() {
		List<MethodStore> methodsWithoutOutput = new Vector<MethodStore>();
		parent.getParent().getAllMethods(methodsWithoutOutput);
		MethodStore.removeMethodsWithoutReturns(methodsWithoutOutput);
		MethodStore.removeMethodsWithDuplicatNames(methodsWithoutOutput);
		List<String> methodNames = MethodStore.methodListToNameAndTypeList(methodsWithoutOutput);
		//Main.printList(methodNames);
		
		int numOfAllMethods = myMethods.size();
		for(int i = 0;i<numOfAllMethods;i++) {
			myMethods.get(i).checkIfReturnStored(methodNames);
		}
	}
}
