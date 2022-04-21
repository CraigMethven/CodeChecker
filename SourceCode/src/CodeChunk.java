import java.util.List;
import java.util.Vector;

/*
 * The base class for handling inputed code
 */
public class CodeChunk {
	//Name of the code chunk
	protected String name;
	protected String fileType;
	//The lines of code and the variables in the code
	protected List<String> myCode;
	protected List<VariableStore> myVariables;
	//The output for the errors
	protected List<String> errorOutput;
	protected boolean firstError = true;
	//Variable to store the line of the file which this code chunk starts
	protected int startLine = 0;
	protected ErrorSummaryCounter errorCounter;
	protected boolean alreadyCountedErrors = false;
	
	//Constructors
	public CodeChunk(){
		initLists();
	}
	/**
	 * Constructor
	 * @param theCode the code of this chunk
	 */
	public CodeChunk(List<String> theCode) {
		initLists();
		myCode = theCode;
		//findVariables(removeComments(theCode));
		//printAllVariables();
	}
	/**
	 * Initialises the components
	 */
	public void initLists(){
		myCode = new Vector<String>();
		myVariables = new Vector<VariableStore>();
		errorCounter = new ErrorSummaryCounter();
	}
	
	/**
	 * basic getters
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Getter
	 * @return gets the code
	 */
	public List<String> getCode() {
		return myCode;
	}
	/**
	 * Getter
	 * @return gets the variables
	 */
	public List<VariableStore> getVariables() {
		return myVariables;
	}
	/**
	 * getter 
	 * @return the line this code chunk starts in the file it's in
	 */
	public int getStartPos() {
		return startLine;
	}
	/**
	 * getter 
	 * @return the length of the code
	 */
	public int getLength() {
		return myCode.size();
	}
	/**
	 * getter 
	 * @return the error counter
	 */
	public ErrorSummaryCounter getErrorSummary() {
		return errorCounter;
	}
	/**
	 * gets the number of class variables
	 * @return the number of class variables
	 */
	public int getNumOfVariables() {
		return myVariables.size();
	}
	
	/**
	 * Gets the variables and saves them to the myVariables list
	 * @param theLines the list that contains all of the lines that we want to check for variables
	 */
	public void findVariables(List<String> theLines) {
		int numOfLines = theLines.size();
		//int numOfTypes = CodeCheckerConstants.variableTypes.length;
		for(int i = 0;i<numOfLines;i++) {
			String curLine = theLines.get(i);
			int prevNumOfVariables = myVariables.size();
			if(curLine.contains("=")) {
				checkIfVariableFromEquals(curLine);
			}
			//Look for variable definition lines
			else if (!curLine.contains("(") && curLine.strip().contains(" ")&& !curLine.strip().contains("return")&& !curLine.strip().contains("{")&& !curLine.strip().contains("\"")&& !curLine.strip().contains(":")) {
				checkIfVariableFromEquals(curLine);
				//System.out.println(curLine);
			}
			
			//If new variables were added that don't have a type look for the type
			if(prevNumOfVariables != myVariables.size()) {
				int numOfNewVariables = myVariables.size();
				List<VariableStore> varsWithoutTypes = new Vector<VariableStore>();
				for(int counter = prevNumOfVariables;counter<numOfNewVariables;counter++) {
					if(myVariables.get(counter).getType().isBlank()) {
						varsWithoutTypes.add(myVariables.get(counter));
					}
				}
				//If some variables have been added that don't have a type
				if(!varsWithoutTypes.isEmpty()) {
					//Loop through every line that's come before current line
					for(int counter = 0;counter<i;counter++) {
						if(theLines.get(counter).contains("=")){
							continue;
						}
						//For all variables that need checked
						for(int varLoop = 0;varLoop<varsWithoutTypes.size();varLoop++) {
							VariableStore curVar = varsWithoutTypes.get(varLoop);
							//If variable is already complete then continue
							if(!curVar.getType().isBlank()) {
								continue;
							}
							//If the line contains the current variable
							if(theLines.get(counter).contains(curVar.getName())) {
								//saveVariable(curLine.substring(0, curLine.length()-2));
								//myVariables.remove(varLoop);
								variableDefinitionChecker(curVar, theLines.get(counter).strip());
								varsWithoutTypes.remove(varLoop);
								if(varsWithoutTypes.isEmpty()) {
									break;
								}
								varLoop--;
							}
						}
						if(varsWithoutTypes.isEmpty()) {
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Method get the code without any variable names or unique identifying features
	 * @param inputCode the code you want cleaned
	 * @param classVariables extra variables that you want included in with the ones already in the code chunk
	 * @return the cleaned code
	 */
	public List<String> cleanCode(List<String> inputCode,List<VariableStore> classVariables) {
		List<String> output = new Vector<String>();
		int numOfVars = myVariables.size();
		int numOfExtraVars = classVariables.size();
		int sizeOfInput = inputCode.size();
		for(int i = 0;i<sizeOfInput;i++) {
			String myLine = inputCode.get(i);
			for(int counter = 0;counter<numOfVars;counter++) {
				if(myLine.contains(myVariables.get(counter).getName())) {
					myLine = removeVariable(myLine, myVariables.get(counter));
				}
			}
			for(int counter = 0;counter<numOfExtraVars;counter++) {
				if(myLine.contains(classVariables.get(counter).getName())) {
					myLine = removeVariable(myLine, classVariables.get(counter));
				}
			}
			if(!myLine.isBlank()) {
				myLine = removeMethodRuns(myLine);
				output.add(myLine);
			}
		}
		//Main.printList(output);
		return output;
	}
	
	/**
	 * Removes the variable name from the line given
	 * @param theLine the line that needs the variable name removed
	 * @param theVariable the variable in the line
	 * @return the line without the variable name
	 */
	public String removeVariable(String theLine, VariableStore theVariable) {
		if(theVariable.getType().isBlank()) {
			return theLine;
		}
		String output = theLine;
		
		int varStart = theLine.indexOf(theVariable.getName());
		if(varStart!=0) {
			char prevChar = theLine.charAt(varStart-1);
			int nextCharPos = varStart+theVariable.getName().length();
			char nextChar = ' ';
			if(theLine.length()>nextCharPos) {
				nextChar = theLine.charAt(nextCharPos);
			}
			
			if(!Character.isLetter(prevChar)) {
				//return if the string is longer than the name of the variable inputed
				if(Character.isLetter(nextChar)) {
					return output;
				}
				output = theLine.substring(0, varStart);
				//What the variable is replaced with
				output+="Variable";
				output+=theLine.substring(varStart+theVariable.getName().length(), theLine.length());
				//System.out.println(output);
			}
			else {
				return output;
			}
//				else {
//					System.out.println("Removing "+theVariable.getName()+": "+theLine);
//				}
		}
		return output;
	}
	
	/**
	 * removes the blank spaces in a line
	 * @param input the string to remove the blank spaces of
	 * @return the link without spaces
	 */
	public static String removeBlankSpace(String input) {
		String output = "";
		char[] theString = input.strip().toCharArray();
		int length = theString.length;
		for(int i = 0;i<length;i++) {
			if(theString[i]!=' ') {
				output+=theString[i];
			}
		}
		return output;
	}
	
	/**
	 * replaces all the method calls with a common phrase so easier to compare
	 * @param theLine the line to replace the method calls in
	 * @return the new string
	 */
	public static String removeMethodRuns(String theLine) {
		if(theLine.contains("(")&&theLine.contains(")")) {
			int bracketOccurances = countBrackets(theLine);
			//System.out.println(bracketOccurances);
			int lastBracketSpaceFound = 0;
			boolean firstLetterFound = false;
			String[] methodNames = new String[bracketOccurances];
			char[] lineArray = theLine.toCharArray();
			int startPos = -1;
			//For all of the bracket occurances find the word before the brackets
			for(int i = 0;i<bracketOccurances;i++) {
				lastBracketSpaceFound=theLine.indexOf('(', lastBracketSpaceFound+1);
				firstLetterFound = false;
				//loop backwards to find last non letter character
				for(int counter = lastBracketSpaceFound; counter>=0;counter--) {
					//Save the position of the first non letter character
					if(!Character.isLetter(lineArray[counter])&&firstLetterFound == true) {
						startPos = counter+1;
						break;
					} else {
						firstLetterFound = true;
					}
				}
				//Add all the characters to a string starting at the first non letter character and ending at the bracket
				String curMethodName="";
				if(startPos!=-1) {
					for(int counter = startPos; counter<lastBracketSpaceFound;counter++) {
						curMethodName+=lineArray[counter];
					}
					startPos=-1;
				}
				//Save the string in the array
				methodNames[i]=curMethodName.strip();
			}
			//Replace the method names with a unified string
			int numOfMultilines = CodeCheckerConstants.multilineTypes.length;
			for(int i = 0;i<bracketOccurances;i++) {
				boolean isMethodName = true;
				if(methodNames[i].isBlank()) {
					continue;
				}
				//Make sure it's not a different type of multiline
				for(int counter = 0;counter<numOfMultilines;counter++) {
					if(methodNames[i].equals(CodeCheckerConstants.multilineTypes[counter])) {
						isMethodName=false;
						break;
					}
				}
				//if not a multiline replace the method name
				if(isMethodName) {
					//System.out.println(methodNames[i]);
					theLine=theLine.replace(methodNames[i], "methodCall");
					//System.out.println(theLine+":"+methodNames[i]);
				}
			}
		}
		//System.out.println(theLine);
		return theLine;
	}
	
	/**
	 * Counts the number of opening and closing bracket sets in a line 
	 * @param theLine the line to count brackets of
	 * @return the num of bracket sets
	 */
	public static int countBrackets(String theLine) {
		int numOfOpening = 0;
		int numOfClosing =0;
		char[] array = theLine.toCharArray();
		int sizeOfString = theLine.length();
		for(int i = 0;i<sizeOfString;i++) {
			if(array[i]=='(') {
				numOfOpening++;
			}
			if(array[i]==')') {
				numOfClosing++;
			}
		}
		if(numOfClosing<numOfOpening) {
			return numOfClosing;
		}
		return numOfOpening;
	}
	
	/**
	 * Counts the number of opening and closing bracket sets in a line 
	 * @param theLine the line to count brackets of
	 * @return the num of bracket sets
	 */
	public static int countCurlyBrackets(String theLine) {
		int numOpen = 0;
		char[] array = theLine.toCharArray();
		int sizeOfString = array.length;
		for(int i = 0;i<sizeOfString;i++) {
			if(array[i]=='{') {
				numOpen++;
			}
			if(array[i]=='}') {
				numOpen--;
			}
		}
		return numOpen;
	}
	
	/**
	 * Methods for checking if the inputted line is a variable definition for the variable store inputted
	 * @param saver the variable store that should store the data in the line if it is a variable definition
	 * @param myLine the line that could contain a variable defition
	 */
	public void variableDefinitionChecker(VariableStore saver, String myLine) {
		if(myLine.contains("{") || myLine.contains("(")) {
			return;
		}
		String myName = saver.getName();
		int namePos = -1;
		String[] splitUp = myLine.split(" ");
		for(int i = 0;i<splitUp.length;i++) {
			if(splitUp[i].strip().equals(myName)) {
				namePos = i;
			}
		}
		if(namePos == -1 || namePos == 0) {
			return;
		}
		saver.setType(splitUp[namePos-1]);
		if(myLine.contains("static")) {
			saver.setStatic(true);
		}
		if(myLine.contains("final")) {
			saver.setFinal(true);
		}
		int numOfPrivacy = CodeCheckerConstants.privacyTypes.length;
    	for(int counter = 0;counter<numOfPrivacy;counter++) {
    		if(myLine.contains(CodeCheckerConstants.privacyTypes[counter])) {
    			saver.setPrivacy(CodeCheckerConstants.privacyTypes[counter]);
    			break;
    		}
    	}
		//saver.print();
	}
	
	
	/**
	 * Prints all of the variables in this code chunk
	 */
	public void printAllVariables() {
		System.out.println("Variables for "+name);
		int numOfVars = myVariables.size();
		for(int i = 0;i<numOfVars;i++) {
			myVariables.get(i).print();
		}
	}
	
	/**
	 * Checks to see if a line that contains an equal sign contains a variable
	 * @param line the line that contains an equal sign
	 */
	public void checkIfVariableFromEquals(String line) {
		String workingLine = line.split("=")[0];
		workingLine = workingLine.strip();
//		if(!workingLine.contains(" ")) {
//			return;
//		}
		//Remove lines changing class variables
//		if(workingLine.contains(".")) {
//			return;
//		}
		//If contains one compare operator but not the other (hence they're being used as compare operators)
		if(workingLine.contains("<")^workingLine.contains(">")) {
			return;
		}
		//If it is an item in an array then don't use it
		if(workingLine.contains("[")) {
			char[] tempCharLine = workingLine.toCharArray();
			int charLength = tempCharLine.length;
			String tempS = "";
			boolean bracketFound = false;
			for(int i = 0;i<charLength;i++) {
				char myChar = tempCharLine[i];
				if(myChar == ']') {
					break;
				}
				if(bracketFound) {
					tempS += myChar;
				}
				if(myChar == '[') {
					bracketFound = true;
				}
			}
			if(tempS.strip()!="") {
				return;
			}
		}
//		//Remove == signs
//		if(line.charAt(workingLine.length()+1)=='='){
//			return;
//		}
//		//remove != signs
//		if(line.charAt(workingLine.length()-1)=='!'){
//			return;
//		}
		//remove symbols that we don't want
		if(workingLine.contains("+")||workingLine.contains("-")||workingLine.contains("/")||workingLine.contains("*")||workingLine.contains("!")||workingLine.contains(")")||workingLine.contains(";")) {
			char[] tempCharLine = workingLine.toCharArray();
			int charLength = tempCharLine.length;
			String tempS = "";
			for(int i = 0;i<charLength;i++) {
				char myChar = tempCharLine[i];
				if(myChar=='+'||myChar=='-'||myChar=='/'||myChar=='*'||myChar=='!'||myChar==')'||myChar==';'){
					myChar = ' ';
				}
				tempS+=myChar;
			}
			workingLine = tempS;
		}
		if(workingLine.contains("(")) {
			char[] tempCharLine = workingLine.toCharArray();
			int charLength = tempCharLine.length;
			String tempS = "";
			boolean foundCurvy = false;
			for(int i = 0;i<charLength;i++) {
				char myChar = tempCharLine[i];
				if(foundCurvy) {
					tempS += myChar;
				}
				if(myChar=='(') {
					foundCurvy = true;
					tempS = "";
				}
			}
			workingLine = tempS;
		}

		saveVariable(workingLine.strip());
		//System.out.println(workingLine);
		
	}
	
	/**
	 * Saves the variable to the list if it doesn't already exist
	 * @param variableLine
	 */
	public void saveVariable(String variableLine) {
		String name="";
		String type="";
		boolean isStatic = false;
		boolean isFinal = false;
		String privacy="";
		
		String[] splitLine = variableLine.split(" ");
		//Split the line up and set the parts of it which are appropriate
		switch(splitLine.length) {
		  case 1:
		    name = splitLine[0].strip();
		    break;
		  case 2:
		    type = splitLine[0].strip();
		    name = splitLine[1].strip();
		    break;
		  default:
		    name = splitLine[splitLine.length-1].strip();
		    type = splitLine[splitLine.length-2].strip();
		    int splitLength = splitLine.length-2;
		    //Loop through the left over parts and assign static, final and privacy type as appropriate
		    for(int i = 0;i<splitLength;i++) {
		    	String curWord = splitLine[i].strip();
		    	if(curWord.equals("final")) {
		    		isFinal = true;
		    		continue;
		    	}
		    	if(curWord.equals("static")) {
		    		isStatic = true;
		    		continue;
		    	}
		    	int numOfPrivacy = CodeCheckerConstants.privacyTypes.length;
		    	for(int counter = 0;counter<numOfPrivacy;counter++) {
		    		if(curWord.equals(CodeCheckerConstants.privacyTypes[counter])) {
		    			privacy = CodeCheckerConstants.privacyTypes[counter];
		    			break;
		    		}
		    	}
		    }
		}
		
		//Remove things that slipped through the cracks
		if(name.contains("{")||name.contains(".")||name.contains("\"")||isNumeric(name) || name.isBlank() || type.toLowerCase().equals("class")) {
			return;
		}
		
		//System.out.println("Name: "+name+";\tType:"+type+ ";\tPrivacy: "+privacy+";\tStatic: "+isStatic+";\tFinal: "+isFinal);
		//Check to see if variable is already in the system
		int numOfVariables = myVariables.size();
		for(int i = 0;i<numOfVariables;i++) {
			VariableStore curVar = myVariables.get(i);
			//If already in the system
			if(curVar.getName().equals(name)) {
				//If we only just got the type then save the type
				if(!type.isBlank() && curVar.getType().isBlank()) {
					curVar.setType(type);
				}
				return;
			}
		}
		//Add the variable
		myVariables.add(new VariableStore(type,name,isStatic,isFinal,privacy));
	}
	
	/**
	 * Checks if the string inputted is a number
	 * Taken from: https://www.baeldung.com/java-check-string-number 
	 * @param strNum the string you want checked 
	 * @return true if it's a number
	 */
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        Integer.parseInt(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Prints the number of tabs inputed to indent lines correctly
	 * @param numOfTabs number of tabs you want the line forward
	 * @param allErrors The list to print the tabs to (if null print to terminal)
	 */
	public static void printTabs(int numOfTabs, List<String> allErrors) {
		for(int counter= 0;counter<numOfTabs;counter++) {
			if(allErrors == null) {
				System.out.print("\t");
			}else {
				allErrors.add(CodeCheckerConstants.htmlTabCharacter);
			}
		}
	}
	
	/**
	 * Way to find out if an error has been found in this code chunk
	 * @return true if an error is current saved
	 */
	public boolean checkForErrors() {
		if(errorOutput!=null) {
			if(!errorOutput.isEmpty()) {
				int sizeOfErrorList = errorOutput.size();
				for(int i = 0;i<sizeOfErrorList;i++) {
					if(!errorOutput.get(i).isBlank()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Prints the errors saved in the errorOuput list with the appropriate amount of tabs inputted
	 * @param numOfTabs How far indented the line should be
	 * @param allErrors the list to print the errors to. If null print to terminal
	 * @return True if there were errors to print
	 */
	public boolean printErrors(int numOfTabs, List<String> allErrors){
		if(errorOutput!=null) {
			if(!errorOutput.isEmpty()) {
				if(allErrors == null) {
					System.out.print("\n");
				}else {
					allErrors.add("\n");
				}
				int listSize = errorOutput.size();
				for(int i =0;i<listSize;i++) {
					if(allErrors == null) {
						printTabs(numOfTabs,null);
						System.out.println(errorOutput.get(i));
					}else {
						printTabs(numOfTabs,allErrors);
						allErrors.add(errorOutput.get(i)+"\n");
					}
					
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Prints the code that is saved in the myCode list
	 */
	public void print() {
		System.out.print("\n");
		for(int i = 0; i<myCode.size(); i++) {
			System.out.println(myCode.get(i));
		}
		System.out.println("\n");
	}
	
	/**
	 * returns a duo containing the int of the first line and last line of the statement
	 * @param useCode the code used to find the statement
	 * @param startPositions the position in the myCode list of the header of the multiline object
	 * @return A duo (2 ints) of the start and last position of the multiline object in the list
	 */
	public List<Duos> findEntireStatement(List<String> useCode, List<Integer> startPositions) {
		List<Duos> statementStartAndEnd = new Vector<Duos>();
		
		if(!startPositions.isEmpty()) {
			for(int i = 0; i<startPositions.size();i++) {
				//System.out.println(myCode.get(startPositions.get(i)));
				//Finds the end and if it never opens then skip over it
				int statementEnd = findClose(useCode,startPositions.get(i));
				if(statementEnd == startPositions.get(i) || statementEnd == -1) {
					continue;
				}
				//Find the start
				int statementStart = findAboveComment(useCode, startPositions.get(i));
				if(statementStart == -1) {
					statementStart = startPositions.get(i);
				}
				//Save the end and the start to the list
				statementStartAndEnd.add(new Duos(statementStart,statementEnd+1));
			}
		}
		return statementStartAndEnd;
	}
	
	/**
	 * returns a duo containing the int of the first line and last line of the statement
	 * @param searchItem the item being searched for (eg a class)
	 * @return the list of duos containing the start and end of the lines. So the list contains all of the start and end points
	 */
	public List<Duos> findEntireStatement(String searchItem) {
		return findEntireStatement(myCode,findFollowing(searchItem));
	}
	
	/**
	 * Finds the all of the words following the word inputed
	 * @param keyWord the word to find
	 * @return a list containing the all of the first words following the word inputed
	 */
	public List<Integer> findFollowing(String keyWord) {
		List<Integer> returnList = new Vector<Integer>();
		List<String> noComments = removeComments(myCode);
		List<String> containedLines = new Vector<String>();
		keyWord = keyWord.toLowerCase();
		
		//Find lines that contain the keyword that aren't in the comments
		String curLine;
		for(int i = 0; i<noComments.size(); i++) {
			curLine = noComments.get(i).toLowerCase();
			//System.out.println(curLine);
			if(curLine.contains(keyWord) && !curLine.contains("\"")) {
				//System.out.println("found it!");
				containedLines.add(curLine);
			}
		}
		
		if(!containedLines.isEmpty()) {
			//Get the line numbers from the text with the comments
			int counter = 0;
			int containedLineLength = containedLines.size();
			String curComparer;
			for(int i = 0; i<myCode.size(); i++) {
				curLine = myCode.get(i).toLowerCase();
				curComparer = containedLines.get(counter);
				if(curLine.contains(curComparer)) {
					returnList.add(i);
					counter++;
					if(counter>= containedLineLength) {
						break;
					}
				}
			}
		}
		return returnList;
	}
	
	/**
	 * Method for counting the brackets being opened in a line
	 * @param myLine the line to count the brackets of
	 * @return the number of brackets in terms of brackets being opened. Negative means brackets closed
	 */
	public int bracketCount(String myLine) {
		int brackets = 0;
		myLine = removeQuotes(myLine);
		char[] line = myLine.toCharArray();
		for(int counter = 0; counter<line.length; counter++) {
			if(line[counter]=='{') {
				brackets++;
			}else if(line[counter]=='}') {
				brackets--;
			}
		}
		return brackets;
	}
	
	/**
	 * Finds the line number of the close of an expression that was opened on the line given
	 * @param listToCheck the list of strings to find the close bracket on
	 * @param lineNum the line number to start on
	 * @return the line number of where the close of the bracket is
	 */
	public int findClose(List<String> listToCheck, int lineNum) {
		//List<String> codeToCheck = removeComments(myCode.subList(lineNum, myCode.size()-1));
		int open = 0;
		int i =0;
		
		if(lineNum+1>listToCheck.size()) {
			return -1;
		}
		
		//Sets the initial bracket count for the line given
		open = bracketCount(listToCheck.get(lineNum));
		if(open==0) {
			if(listToCheck.get(lineNum+1).stripLeading().startsWith("{")){
				i++;
				open++;
			}
		}
		//until the brackets are closed
		while(open > 0) {
			i++;
			//System.out.println(myCode.get(lineNum+i) + " Num of brackets:"+ open);
			if(lineNum+i+1>listToCheck.size()) {
				return -1;
			}
			open += bracketCount(listToCheck.get(lineNum+i));
		}
		return lineNum+i;
	}
	
	/**
	 * NOTE! SHOULD FIX TO NOT COUNT LINES THAT DON'T START WITH A COMMENT
	 * Returns the line that the comments above a line given start or -1 if no comments are above
	 * @param useCode the code looped through
	 * @param lineNum The line number in which the above comment should be found
	 * @return The line which the comment starts on
	*/
	public int findAboveComment(List<String> useCode, int lineNum) {
		if(lineNum == 0) {
			return -1;
		}
		//i is the line counter for the amount of lines above the line given the comment starts
		int i = 1;
		//Variables keeping track of if a comment has been found
		boolean endFound = false;
		int partFound = -1;
		
		//Loop, exiting when comment has been found
		while(true) {
			//Get the line
			String myLine = useCode.get(lineNum - i);
			//For if the end of a multiline comment has been found
			if(endFound) {
				//If start of multiline has been found return the line number
				if(myLine.contains("/*")) {
					//System.out.println("Start of comment");
					return(lineNum - i);
				}
			}
			//If the line is implying a comment
			else if(myLine.contains("*/") || myLine.contains("//")) {
				if(myLine.contains("*/")) {
					endFound = true;
				}else {
					partFound = lineNum - i;
				}
			//If the line is blank then skip over it
			}else if(myLine.isBlank() && partFound == -1) {
				
			//If no comment has been found return the position of the last comment
			}else {
				//System.out.println("Other code found");
				return partFound;
			}

			i++;
			if(lineNum - i < 0) {
				//System.out.println("Start of file");
				return partFound;
			}
		}
	}
	
	/**
	 * Basic converter to convert the string inputed to a list and vise versa so that it works in remove quote
	 * @param inputtedCode the string you want the quotes removed from
	 * @return the string without anything in the quotes
	 */
	public String removeQuotes(String inputtedCode) {
		List<String> tempList = new Vector<String>();
		tempList.add(inputtedCode);
		return removeQuotes(tempList).get(0);
	}
	
	/**
	 * Method to remove the text in quotes from a list of strings
	 * @param inputtedCode the strings you want quote text removed from
	 * @return the list without text in the quotes
	 */
	public List<String> removeQuotes(List<String> inputtedCode) {
		List<String> codeNoQuotes = new Vector<String>();
		boolean slashFound = false;
		boolean charFound = false;
		boolean inQuote = false;
		char[] lineInArray;
		//For every line
		for(int i = 0; i<inputtedCode.size(); i++) {
			String curLine = inputtedCode.get(i);
			if(curLine.contains("\"")||curLine.contains("\'")) {
				lineInArray = curLine.toCharArray();
				String lineToBeAdded = "";
				
				//For all characters in the line
				int lineLength = lineInArray.length;
				for(int counter = 0; counter<lineLength;counter++) {
					//Get cur letter
					char curLetter = lineInArray[counter];
					//For strings
					if(curLetter == '"' && !slashFound && !charFound) {
						inQuote = !inQuote;
						if(inQuote) {
							lineToBeAdded+="\"PredefinedString";
						}
					}
					//For chars
					if(curLetter == '\'' && !slashFound && !inQuote) {
						charFound = !charFound;
						if(charFound) {
							lineToBeAdded+="'PredefinedChar";
						}
					}
					//For commented out quotes
					slashFound = false;
					if(curLetter == '\\') {
						slashFound = true;
					}
					//Add letter if it's not in a quote
					if(!inQuote && !charFound) {
						lineToBeAdded+=curLetter;
					}
				}
				codeNoQuotes.add(lineToBeAdded);
				charFound = false;
				slashFound = false;
			}
			else {
				if(!inQuote) {
					codeNoQuotes.add(curLine);
				}
			}
		}
		return codeNoQuotes;
	}
	
	/**
	 * Removes comments from the code 
	 * @param list you want the comments removed from
	 * @return Outputs the code without comments as a list of strings
	 */
	public List<String> removeComments(List<String> inputtedCode) {
		inputtedCode = removeQuotes(inputtedCode);
		List<String> codeNoComments = new Vector<String>();
		boolean slashFound = false;
		boolean starFound = false;
		boolean inMultiline = false;
		int lineCommented = -1;
		int starStop = -1;
		int multilineStart = -1;
		char[] lineInArray;
		//For every line
		for(int i = 0; i<inputtedCode.size(); i++) {
			if(inputtedCode.get(i).strip().startsWith("//")) {
				continue;
			}
			lineCommented = -1;
			lineInArray = inputtedCode.get(i).toCharArray();
			//For all characters in the line
			int lineLength = lineInArray.length;
			for(int counter = 0; counter<lineLength;counter++) {
				//Get cur letter
				char curLetter = lineInArray[counter];
				//If we aren't in a multiline comment
				if(!inMultiline) {
					//If there has already been 1 slash
					if(slashFound) {
						//If the line has been commented out then ignore it
						if(curLetter == '/') {
							lineCommented = counter-1; //Minus 1 to get the correct place to cut
							break;
						}
						//Start a multiline comment if a / then astrix was found
						else if(curLetter == '*') {
							inMultiline = true;
							multilineStart = counter-1;
						}
					}
					if(curLetter == '/') {
						slashFound  = true;
					}else {
						slashFound = false;
					}
				}
				//If in a multiline comment check to see if it should be ended
				if(inMultiline) {
					if(starFound) {
						if(curLetter == '/') {
							inMultiline = false;
							starFound = false;
							starStop = counter + 1;
						}else {
							starFound = false;
						}
					}
					if(curLetter == '*') {
						starFound = true;
					}
				}
			}
			//For every line that isn't commented:
			String myLine = inputtedCode.get(i);
			if(!inMultiline || multilineStart != -1) {
				//Remove comments that are part way through a line
				if(lineCommented != -1) {
					myLine = myLine.substring(0,lineCommented);
					lineCommented = -1;
				}
				if(starStop!=-1 && multilineStart!=-1) {
					String tempLine = myLine.substring(0,multilineStart);
					myLine = tempLine + myLine.substring(starStop,myLine.length());
					starStop = -1;
					multilineStart = -1;
				}
				//Remove multiline comments that end at the start of a line
				if(starStop != -1) {
					myLine = myLine.substring(starStop,myLine.length());
					starStop = -1;
				}
				//Remove multi line comments that start at the end of a line
				if(multilineStart != -1) {
					myLine = myLine.substring(0,multilineStart);
					multilineStart = -1;
				}
				//Save all lines that aren't blank
				if(myLine.trim() != "") {
					codeNoComments.add(myLine);
				}
			}
		}
		return codeNoComments;
	}
	
	/*
	 * For error checks
	 */
	
	/**
	 * Gets the list of the variables that don't meet naming conventions
	 * @return a list containing the names of variables that are wrong
	 */
	public List<String> getVariablesNamedWrong() {
		List<String> variablesNamedWrong = new Vector<String>();
		if(!myVariables.isEmpty()) {
			int numOfVars = myVariables.size();
			for(int i = 0;i<numOfVars;i++) {
				String curName = myVariables.get(i).getName();
				if(!curName.isBlank()) {
					if(!Character.isLowerCase(curName.toCharArray()[0]) && Character.isLetter(curName.toCharArray()[0])) {
						variablesNamedWrong.add(curName);
					}
				}
			}
		}
		return variablesNamedWrong;
	}
}
