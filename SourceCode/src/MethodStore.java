import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/**
 * class for storing and handling methods in code given
 * @author Craig
 *
 */
public class MethodStore extends CodeChunk{
	private String returnType;
	private List<VariableStore> inputs;
	private String privacy = "";
	private int aboveCommentLength;
	private boolean staticMethod = false;
	private List<String> cleanedCode;
	private ClassStore parent;
	
	/**
	 * Constructor
	 * @param myCode code in this method
	 */
	public MethodStore(List<String> myCode){
		super(myCode);
		initMethod();
	}
	/**
	 * Constructor
	 * @param myCode code in this method
	 * @param fileExtention the file type
	 */
	public MethodStore(List<String> myCode, String fileExtention){
		super(myCode);
		fileType=fileExtention;
		initMethod();
	}
	/**
	 * Constructor
	 * @param myCode code in this method
	 * @param fileExtention the file type
	 * @param starting The line of the file this method starts on
	 */
	public MethodStore(List<String> myCode, String fileExtention,int starting){
		super(myCode);
		fileType=fileExtention;
		startLine = starting;
		initMethod();
	}
	/**
	 * Constructor
	 * @param myCode code in this method
	 * @param fileExtention the file type
	 * @param starting The line of the file this method starts on
	 * @param tempParent class that stores this method
	 */
	public MethodStore(List<String> myCode, String fileExtention,int starting,ClassStore tempParent){
		super(myCode);
		parent = tempParent;
		fileType=fileExtention;
		startLine = starting;
		initMethod();
	}
	/**
	 * Constructor
	 * @param myCode code in this method
	 * @param myName the name of this method
	 * @param fileExtention the file type
	 */
	public MethodStore(List<String> myCode, String myName, String fileExtention){
		super(myCode);
		name=myName;
		fileType = fileExtention;
		initMethod();
	}
	/**
	 * constructor 
	 * @param myCode code in this method
	 * @param myName the name of this method
	 * @param fileExtention the file type
	 * @param tempParent the class that this method belongs to
	 */
	public MethodStore(List<String> myCode, String myName, String fileExtention, ClassStore tempParent){
		super(myCode);
		parent = tempParent;
		name=myName;
		fileType = fileExtention;
		initMethod();
	}
	/**
	 * Basic initialise method. Run after every constructor. Sets up variables
	 */
	private void initMethod(){
		inputs = new Vector<VariableStore>();
		if(removeComments(myCode).isEmpty()) {
			return;
		}
		getMethodInfo();
		
		myVariables.addAll(inputs);
		findVariables(removeComments(myCode));
		
		runChecks();
//		System.out.println("Method x: "+name);
//		System.out.println(myCode.get(0));
	}
	
	/**
	 * Returns the path of the method
	 * @return the method path
	 */
	public String getMethodPath() {
		if(parent!=null) {
			return parent.getClassPath()+"<em>"+name+"("+getInputs()+")[Line "+startLine+"-"+(startLine+myCode.size()-1)+"]</em>";
		}
		return null;
	}
	
	/**
	 * returns the return type
	 * @return the return type
	 */
	public String getReturnType() {
		return returnType;
	}
	
	/**
	 * sets cleaned code
	 * @param input what to set variable to
	 */
	public void setCleanedCode(List<String> input) {
		cleanedCode = input;
	}
	
	/**
	 * basic getter
	 * @return cleaned code
	 */
	public List<String> getCleanedCode() {
		return cleanedCode;
	}
	
	/**
	 * Gets and saves the length of the comment above the method given the first line of the method
	 * @param methodHeaderLine The first line of the method. The method definition
	 */
	public void getAboveCommentLength(String methodHeaderLine) {
		int codeLength = myCode.size();
		for(int i =0;i<codeLength;i++) {
			if(myCode.get(i).equals(methodHeaderLine)){
				aboveCommentLength = i;
				return;
			}
		}
	}
	
	/**
	 * Looks at the start of a method definition and saved the word given appropriately
	 * @param curWord A word from before the brackets of a method definition
	 * @return if not static or privacy type
	 */
	public boolean outsideBracketMethodDefinitionWordCheck(String curWord,boolean firstFind) {
		//Look for static
		if(curWord.equals("static")) {
			staticMethod = true;
			return false;
		}
		//Look for privacy label
		for(int counter = 0; counter<CodeCheckerConstants.privacyTypes.length; counter++) {
			if(curWord.equals(CodeCheckerConstants.privacyTypes[counter])) {
				privacy = curWord;
				return false;
			}
		}
		
		if(!firstFind) {
			returnType = curWord;
		}else {
			name = curWord;
		}
		return true;
	}
	
	/**
	 * Gets all the info needed to be saved from the method header
	 */
	public void getMethodInfo() {
		//Local variables
		String methodDefinitionString = removeComments(myCode).get(0);
		getAboveCommentLength(methodDefinitionString);
		char[] methodDefinition = methodDefinitionString.trim().toCharArray();
		String curWord = "";
		boolean insideInputs = false;
		String gottenType = "";
		int lineLength = methodDefinition.length;
		boolean firstFind = false;
				
		//Loop through line
		for(int i = 0;i<lineLength;i++) {
			//Find Spaces
			if(methodDefinition[i] == ' ' && !insideInputs) {
				//Check word and set variables accordingly
				firstFind = outsideBracketMethodDefinitionWordCheck(curWord,firstFind);
				curWord = "";
			}
			//If going inside brackets
			else if(methodDefinition[i] == '(') {
				insideInputs = true;
				if(!curWord.isBlank()) {
					name = curWord;
				}
				curWord = "";
			}
			
			//For grabbing input variables types
			else if(methodDefinition[i] == ' ' && insideInputs && gottenType.equals("")) {
				gottenType = curWord;
				curWord = "";
			}
			//Saving input variables
			else if((methodDefinition[i] == ',' || methodDefinition[i] == ' '|| methodDefinition[i] == ')') && (insideInputs && !gottenType.equals(""))) {
				if(!gottenType.strip().equals("=")) {
					inputs.add(new VariableStore(gottenType,curWord));
				}
				
				gottenType = "";
				curWord = "";
			}
			//If there's not a space then add letter to the cur word
			else {
				curWord += methodDefinition[i];
			}
			
			//For ending
			if(methodDefinition[i] == ')') {
				break;
			}
		}
		
		if(returnType == null) {
			returnType = "";
		}
		
		//For printing
//		System.out.println("Privacy; static; return type; name: "+privacy+" "+staticMethod+" "+returnType+" "+name+"");
//		for(int i = 0;i<lineLength;i++) {
//			System.out.print(methodDefinition[i]);
//		}
//		System.out.println("\n");
		
	}
	
	/**
	 * Method to return the inputs in the form that they would be written in the method definition
	 * @return the inputs as they would be written in a method definition
	 */
	public String getInputs() {
		String output = "";
		boolean first = true;
		//If there are inputs
		if(!inputs.isEmpty()) {
			int inputSize = inputs.size();
			//For each input
			for(int i =0;i<inputSize;i++) {
				//Boolean to make sure we don't print a comma before the first input but all subsequent ones
				if(!first) {
					output+=", ";
				}else {
					first = false;
				}
				//Add the input to the output for the method
				output+=inputs.get(i).getType()+" "+inputs.get(i).getName();
			}
		}
		
		return output;
	}
	
	/**
	 * Saves inputed errors to the error output list
	 * @param theError The error that is needing to be added to the error output file
	 * @param errorColor the colour that you want the error to be shown as to specify the severity
	 */
	public void writeError(String theError, String errorColor) {
		if(!checkForErrors()) {
			errorOutput = new Vector<String>();
			errorOutput.add("Method&nbsp;"+CodeCheckerConstants.htmlMethodColor+name+"("+getInputs()+")"+CodeCheckerConstants.htmlColorClose+"[Line "+startLine+"-"+(startLine+myCode.size()-1)+"]:");
		}
		errorOutput.add("-"+errorColor+theError+CodeCheckerConstants.htmlColorClose);
	}
	
	
	/**
	 * Runs all the checks selected from the options menu
	 */
	public void runChecks() {
		errorOutput = new Vector<String>();
		errorCounter = new ErrorSummaryCounter();
		if(Options.checkNames) {
			checkNameOfMethod();
			printVariablesNamedWrong();
		}
		if(Options.checkCorrectUseOfAccessModifiers) {
			checkAccessModifier();
		}
		if(Options.checkCommentsAboveMethodsAndClasses) {
			checkMethodAboveComments();
		}
		if(fileType.equals("java")) {
			//Check for java doc comments
			if(Options.checkJavadocComments) {
				checkJavadocComments();
			}
			if(Options.checkThatScannerIsClosedOnceOpened) {
				checkScannerClosed();
			}
			if(name!=null) {
				if(Options.checkMainMethodLength && name.equals("main")) {
					checkMainMethod();
				}
			}
			if(Options.compareToOtherInputs) {
				checkStringComparison();
			}
		}
		if(Options.checkForLargeMethods) {
			checkMethodLength();
		}
		if(Options.checkRecursionOnMethodsWithNoInput) {
			checkRecursionWithNoInput();
		}
		if(Options.checkSemicolonAfterOpeningStatements) {
			checkSemicolonAfterOpeningStatements();
		}
		if(Options.checkForLargeIfThenChains) {
			checkForLargeIfElseChains();
		}
//		if(Options.checkIndentation) {
//			checkIndentation();
//		}
	}
	
	/**
	 * Checks if there is a comment above a method
	 */
	public void checkMethodAboveComments() {
		//Checks if there are any comments present
		if(aboveCommentLength == 0) {
			errorCounter.incrementNoComments();
			writeError("No above comment",CodeCheckerConstants.htmlYellowText);
		}
	}
	
	/**
	 * Checks if the javadoc comments are present and correct
	 * If not then adds to the error output the reason why the javadocs aren't correct
	 */
	public void checkJavadocComments() {
		//Checks if there are any comments present
		if(aboveCommentLength == 0) {
			if(!Options.checkCommentsAboveMethodsAndClasses) {
				checkMethodAboveComments();
			}
			return;
		}
		
		int inputsFound = 0;
		int numOfReturns = 0;
		String myLine = "";
		int numberOfInputs = inputs.size();
		
		//Count java docs comments
		for(int i = 0;i<aboveCommentLength;i++) {
			myLine = myCode.get(i);
			//Check there are @param comments for all of the inputs
			if(myLine.contains("@param")) {
				for(int counter = 0;counter<numberOfInputs;counter++) {
					//Make sure that the @param is for one of the variables inputed
					if(myLine.contains(inputs.get(counter).getName())) {
						inputsFound++;
						break;
					}
				}
			}
			//Check for @returns on methods that require them
			if(myLine.contains("@return")) {
				if(returnType.equals("void")) {
					errorCounter.incrementJavadocComments();
					writeError("@return java doc comment when the nothing being returning",CodeCheckerConstants.htmlYellowText);
				}
				numOfReturns++;
			}
		}
		//Write the error messages
		if(numOfReturns>1) {
			errorCounter.incrementJavadocComments();
			writeError("Multiple @return comments",CodeCheckerConstants.htmlYellowText);
		}
		if(!returnType.equals("void") && !returnType.equals("") && numOfReturns == 0) {
			errorCounter.incrementJavadocComments();
			writeError("No @return java docs comment",CodeCheckerConstants.htmlYellowText);
		}
		if(inputsFound!= numberOfInputs) {
			if(inputsFound>numberOfInputs) {
				errorCounter.incrementJavadocComments();
				writeError("Too many @param comments. There are "+numberOfInputs+" inputs but there are "+inputsFound+" @param comments",CodeCheckerConstants.htmlYellowText);
			}else {
				errorCounter.incrementJavadocComments();
				writeError("Too few @param comments. There are "+(numberOfInputs-inputsFound)+" inputs that don't have an @param comment",CodeCheckerConstants.htmlYellowText);
			}
		}
	}
	
	/**
	 * Makes sure that the name of methods match name conventions
	 * Purposefully named with a capital letter for testing purposes
	 */
	public void checkNameOfMethod() {
		//If name is blank
		if(name==null || name.isBlank()) {
			errorCounter.incrementMethodNameConvenstion();
			writeError("Method doesn't have a name",CodeCheckerConstants.htmlRedText);
			return;
		}
		//If first letter is a capital and it isn't a constructor
		if(!Character.isLowerCase(name.toCharArray()[0]) && !returnType.isBlank()) {
			errorCounter.incrementMethodNameConvenstion();
			writeError("Method name starts with a capital letter",CodeCheckerConstants.htmlOrangeText);
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
				writeError("Variable <em>"+printers.get(i)+"</em> starts with a capital letter",CodeCheckerConstants.htmlOrangeText);
			}
		}
	}
	
	/**
	 * returns if this stores a static method or not
	 * @return true if static
	 */
	public boolean getStatic() {
		return staticMethod;
	}
	
	/**
	 * checks to make sure the method has a privacy status
	 */
	public void checkAccessModifier() {
		//If no privacy is set
		if(privacy.isBlank()) {
			errorCounter.incrementPrivacyMethods();
			writeError("No privacy set (eg public, protected, private)",CodeCheckerConstants.htmlOrangeText);
		}
	}
	
	/**
	 * Checks to make sure the main method in java code isn't larger than the specified length
	 */
	public void checkMainMethod() {
		if(staticMethod && !inputs.isEmpty()) {
			if(inputs.get(0).getName().equals("args")) {
				int mainMethodSize = removeComments(myCode).size();
				if(mainMethodSize>AdvOptions.mainMethodMaxLength) {
					errorCounter.incrementMainMethodLength();
					writeError("Main method may be too large at a size of "+mainMethodSize+" lines",CodeCheckerConstants.htmlOrangeText);
				}
			}
		}
	}
	
	/**
	 * Checks to see if there is a scanner variable in this method and if so if it's been closed
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
			if(!checkScannerClosed(scannerName)) {
				errorCounter.incrementScannerClosed();
				writeError("Scanner <em>"+scannerName+"</em> has not been closed",CodeCheckerConstants.htmlRedText);
			}
		}
	}
	
	/**
	 * Checks to see if the scanner of the name specified has been closed in this method
	 * @param scannerName the name of the scanner
	 * @return if the scanner has been closed
	 */
	public boolean checkScannerClosed(String scannerName) {
		if(!scannerName.isBlank()) {
			List<String> uncommentedCode = removeComments(myCode);
			int methodLength = uncommentedCode.size();
			String stringToLookFor = scannerName.strip()+".close();";
			for(int i = 0;i<methodLength;i++) {
				String curLine = uncommentedCode.get(i);
				if(curLine.contains(stringToLookFor)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * checks for methods that are too large so should be split into separate ones
	 */
	public void checkMethodLength(){
		int methodLength = getMethodLength();
		if(methodLength>AdvOptions.maxMethodLength) {
			errorCounter.incrementLargeMethods();
			if(methodLength>AdvOptions.maxMethodLength*2) {
				writeError("Method may be too large with it being lines "+methodLength+" long",CodeCheckerConstants.htmlRedText);
			}
			else if(methodLength>AdvOptions.maxMethodLength*3/2){
				writeError("Method may be too large with it being lines "+methodLength+" long",CodeCheckerConstants.htmlOrangeText);
			}
			else {
				writeError("Method may be too large with it being lines "+methodLength+" long",CodeCheckerConstants.htmlYellowText);
			}
		}
	}
	
	/**
	 * Method to get the length of the stored method
	 * @return the length
	 */
	public int getMethodLength() {
		return removeComments(myCode).size();
	}
	
	/**
	 * Compares 2 methods to say if they're too similar
	 * @param comparer the method this method is being compared against
	 * @return true if too similar, false if not
	 */
	public int compareMethod(MethodStore comparer) {
//		//Make sure not too small to be a coincidence
//		if(cleanedCode.size()<AdvOptions.minMethodSizeToCompare||comparer.getCleanedCode().size()<AdvOptions.minMethodSizeToCompare) {
//			return -1;
//		}
		int min = cleanedCode.size()*3/4;
		int max= cleanedCode.size()*5/4;
		List<String> comparerCode = comparer.getCleanedCode();
		if(comparerCode==null) {
//			System.out.println(comparer.getName());
//			Main.printList(comparer.getCode());
			return -1;
		}
		int comparersSize = comparerCode.size();
		//Make sure lengths are roughly equal
		if(comparersSize<min ||comparersSize>max) {
			return -1;
		}
		
		//Find the num of times to loop
		int loopNum = cleanedCode.size();
		List<String> smallerCode = cleanedCode;
		List<String> biggerCode = comparerCode;
		if(comparersSize<loopNum) {
			loopNum = comparersSize;
			smallerCode = comparerCode;
			biggerCode = cleanedCode;
		}
		int startLine = 0;
		
		//Find line to start searching from
		for(int i = 0;i<loopNum;i++) {
			if(compareLines(smallerCode.get(0),biggerCode.get(i))) {
				startLine = i;
				break;
			}
		}
		//Loop through all lines comparing them
		int numSimilar = 0;
		for(int i = startLine;i<loopNum;i++) {
			if(compareLines(smallerCode.get(i),biggerCode.get(i-startLine))) {
				numSimilar++;
			}
		}
		//return the percentage of lines that were similar
		return ErrorSummaryCounter.toPercentage(numSimilar, cleanedCode.size());
	}
	
	/**
	 * Compares two lines to see if they are the same
	 * @param lineOne first line
	 * @param lineTwo second line
	 * @return true if they are the same
	 */
	public boolean compareLines(String lineOne,String lineTwo) {
		lineOne= removeBlankSpace(lineOne);
		lineTwo = removeBlankSpace(lineTwo);
		if(lineOne.equals(lineTwo)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Writes the errors for another method being similar
	 * @param similarMethod the method that is similar
	 * @param percentSimilar the percentage of lines that are the same between them
	 * @param sameFolder if the two methods come from the same folder
	 */
	public void addSimilarMethodError(MethodStore similarMethod, int percentSimilar,boolean sameFolder) {
		if(sameFolder) {
			errorCounter.incrementCopying();
			if(percentSimilar==100) {
				writeError(percentSimilar+"% similar to own method <em>"+similarMethod.getMethodPath()+"</em>", CodeCheckerConstants.htmlRedText);
			}else {
				writeError(percentSimilar+"% similar to own method <em>"+similarMethod.getMethodPath()+"</em>", CodeCheckerConstants.htmlOrangeText);
			}
		}
		else {
			errorCounter.incrementCheating();
			if(percentSimilar==100) {
				writeError(percentSimilar+"% similar to other uploads method <em>"+similarMethod.getMethodPath()+"</em>", CodeCheckerConstants.htmlRedText);
			}else {
				writeError(percentSimilar+"% similar to other uploads method <em>"+similarMethod.getMethodPath()+"</em>", CodeCheckerConstants.htmlOrangeText);
			}
		}
	}
	
	/**
	 * Removes all variables that aren't of the type specified
	 * @param varList the list of variables
	 * @param type the type you want the list of
	 */
	public static void removeAllNotOfType(List<VariableStore> varList,String type) {
		int numOfVars = varList.size();
		for(int i =0;i<numOfVars;i++) {
			if(!varList.get(i).getType().equals(type) || varList.get(i).getName().isBlank()) {
				varList.remove(i);
				i--;
				numOfVars--;
			}
		}
	}
	
	/**
	 * Checks if a line contains an improper use of string comparison
	 * @param line the line to check
	 * @param variableName the name of the variable to check if compared wrong
	 * @return if compared wrong
	 */
	public boolean checkStringComparisonLine(String line, String variableName) {
		//System.out.println("Checking Line "+line);
		String blankSpaceLine = removeBlankSpace(line.strip());
		int sizeOfVar = variableName.length();
		List<Integer> occurancesOfVar = new Vector<Integer>();
		int varPos=-1;
		
		do{
			varPos = blankSpaceLine.indexOf(variableName,varPos+1);
			if(varPos!=-1) {
				occurancesOfVar.add(varPos);
			}else {
				break;
			}
		}while(varPos!=-1);
		
		int numOfOccurances = occurancesOfVar.size();
		char[] lineChars = blankSpaceLine.toCharArray();
		for(int i = 0;i<numOfOccurances;i++) {
			varPos = occurancesOfVar.get(i);
			if(varPos-2>=0) {
				//System.out.println("Checking Letter "+lineChars[varPos-2]+lineChars[varPos-1]);
				if(lineChars[varPos-2]=='='||lineChars[varPos-2]=='!') {
					if(lineChars[varPos-1]=='=') {
						if(varPos-6>=0) {
							//Concatenating strings taken from
							//https://stackoverflow.com/questions/328249/how-to-concatenate-characters-in-java
							String ifNull = new StringBuilder().append(lineChars[varPos-6]).append(lineChars[varPos-5]).append(lineChars[varPos-4]).append(lineChars[varPos-3]).toString();
							//System.out.println(ifNull);
							if(!ifNull.equals("null")) {
								return true;
							}
						}else {
							return true;
						}
					}
				}
			}
			varPos+=sizeOfVar;
			if(varPos+1<=lineChars.length) {
				//System.out.println("Checking Letter "+lineChars[varPos]+lineChars[varPos+1]);
				if(lineChars[varPos]=='='||lineChars[varPos]=='!') {
					if(lineChars[varPos+1]=='=') {
						if(varPos+5<=lineChars.length) {
							String ifNull = new StringBuilder().append(lineChars[varPos+2]).append(lineChars[varPos+3]).append(lineChars[varPos+4]).append(lineChars[varPos+5]).toString();
							//System.out.println(ifNull);
							if(!ifNull.equals("null")) {
								return true;
							}
						}else {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * adds errors if strings are compared in an improper way
	 */
	public void checkStringComparison() {
		List<VariableStore> allVars = new Vector<VariableStore>();
		allVars.addAll(myVariables);
		if(parent!=null) {
			allVars.addAll(parent.getVariables());
		}
		removeAllNotOfType(allVars,"String");
		if(allVars.isEmpty()) {
			return;
		}
		List<String> checkCode = removeComments(myCode);
		int sizeOfCode = checkCode.size();
		int numOfStrings = allVars.size();
		String curLine;
		String curVar;
		for(int i = 0;i<sizeOfCode;i++) {
			curLine = checkCode.get(i);
			if(!(curLine.contains("==")||curLine.contains("!="))) {
				continue;
			}
			for(int varNum = 0;varNum<numOfStrings;varNum++) {
				curVar = allVars.get(varNum).getName();
				if(curLine.contains(curVar)) {
					if(checkStringComparisonLine(curLine,curVar)) {
						writeError("Strings possibly compared incorrectly in line: <em>"+curLine.strip()+"</em>",CodeCheckerConstants.htmlRedText);
						errorCounter.incrementStringComparison();
					}
				}
			}
		}
	}
	
	/**
	 * A method with problematic code in it to flag errors
	 */
	static String ProblemMethod() {
		String Potato = "9";
		if(Potato=="fihwaf");
		if("fhwaifwa"==Potato);
			int Sheep = 5;
		boolean Igloo = false;
		if(Potato.equals("9")) Igloo=true;
		if(Igloo) {;
			int Flag = 1+1;
			if(Flag==9) {
				
			}
		}else if(2==Sheep) {
			
		}
		else if(Sheep==5) {
		
		}else if(9==Sheep) {;
			
		}else {;
			
		}
		for(int i = 0;i<Sheep;i++) {;
			
		}
		if(!Igloo) {Potato+="dwa";}
		Scanner Block= new Scanner("C:/Users");
		Block.equals(Block);
		ProblemMethod();
		return Potato;
	}
	
	/**
	 * Checks to see if recursion is used properly (only on methods that require an input)
	 */
	public void checkRecursionWithNoInput() {
		int amountFound = 0;
		if(!inputs.isEmpty()) {
			return;
		}
		String searchString = name+"(";
		List<String> checkCode = removeComments(myCode);
		int codeSize = checkCode.size();
		for(int i = 1;i<codeSize;i++) {
			String line = checkCode.get(i);
			if(line.contains(searchString)) {
				int stringPos = line.indexOf(searchString);
				//If this string is just the suffix of another variable or calling a method of the same name that takes inputs ignore the call
				if(stringPos>0) {
					char[] charLine = line.toCharArray();
					if(Character.isLetter(charLine[stringPos-1])||charLine[stringPos-1]=='.'||!(charLine[stringPos+searchString.length()]==' '||charLine[stringPos+searchString.length()]==')')) {
						continue;
					}
				} 
				errorCounter.incrementRecursionInput();
				amountFound++;
				
			}
		}
		if(amountFound==1) {
			writeError("Method used recursively without taking an input",CodeCheckerConstants.htmlRedText);
		}else if(amountFound>1) {
			writeError("Method used recursively "+amountFound+" times without taking an input",CodeCheckerConstants.htmlRedText);
		}
	}
	
	/**
	 * Gets the position of the closing bracket for the opening bracket specified
	 * @param theLine the line the bracket is on
	 * @param bracketPos the position of the opening bracket
	 * @return the position of the closing bracket
	 */
	public static int getPosOfMatchingClosingBracket(String theLine, int bracketPos) {
		char[] charLine = theLine.toCharArray();
		int sizeOfLine = charLine.length;
		int numOpen = 0;
		for(int i = bracketPos;i<sizeOfLine;i++) {
			if(charLine[i]=='(') {
				numOpen++;
			}
			else if(charLine[i]==')') {
				numOpen--;
			}
			if(numOpen == 0) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Gets the character following a string specified
	 * @param line the line the string is in
	 * @param searchItem the item to get the char following
	 * @return the next char
	 */
	public char findFollowingChar(String line, String searchItem) {
		char[] charLine = line.toCharArray();
		return charLine[line.indexOf(searchItem)+searchItem.length()];
	}
	
	/**
	 * Checks to see if semi colons are closing opening statements straight away
	 */
	public void checkSemicolonAfterOpeningStatements() {
		List<String> checkCode = removeComments(myCode);
		int codeSize = checkCode.size();
		int numOfOpeners = CodeCheckerConstants.multilineTypes.length;
		String line;
		String nextLine;
		for(int i = 0;i<codeSize;i++) {
			nextLine=checkCode.get(i);
			line = checkCode.get(i);
			if(!line.contains(";")) {
				continue;
			}
			if(i+1<codeSize) {
				nextLine = checkCode.get(i+1);
			}
			if(line.contains("{")||nextLine.contains("{")) {
				for(int counter = 0;counter<numOfOpeners;counter++) {
					if(line.strip().replace("}", "").startsWith(CodeCheckerConstants.multilineTypes[counter])) {
						String curMultiline = CodeCheckerConstants.multilineTypes[counter];
						if(Character.isLetter(findFollowingChar(line,curMultiline))) {
							return;
						}
						int closeBracketPos = line.indexOf(curMultiline)+curMultiline.length();
						int openBracketPos = line.indexOf("(", line.indexOf(curMultiline)+curMultiline.length()-1);
						//System.out.println(line.charAt(openBracketPos));
						if(openBracketPos!=-1) {
							closeBracketPos = getPosOfMatchingClosingBracket(line,openBracketPos);
						}
						if(closeBracketPos==-1) {
							continue;
						}
						String followingBrackets = line.substring(closeBracketPos+1);
						//System.out.println(followingBrackets);
						followingBrackets = followingBrackets.replace("{", "");
						if(followingBrackets.strip().equals(";")) {
							errorCounter.incrementInstantSemiColon();
							writeError("Semicolon may stop program going into "+curMultiline+" statement in line: <em>"+line.strip()+"</em>",CodeCheckerConstants.htmlRedText);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Removes all of the methods that don't have a return type from the list
	 * @param theList the list you want the non returning methods remvoed from
	 */
	public static void removeMethodsWithoutReturns(List<MethodStore> theList) {
		int numOfMethods = theList.size();
		for(int i = 0;i<numOfMethods;i++) {
			if((theList.get(i).getReturnType().isBlank() || theList.get(i).getReturnType().equalsIgnoreCase("void")) && !theList.get(i).getName().isBlank()) {
				theList.remove(i);
				i--;
				numOfMethods--;
			}
		}
	}
	
	/**
	 * Removes all of the methods that don't have a return type from the list
	 * @param theList the list you want the non returning methods remvoed from
	 */
	public static void removeMethodsWithDuplicatNames(List<MethodStore> theList) {
		int numOfMethods = theList.size();
		for(int i = 0;i<numOfMethods;i++) {
			for(int counter = i+1;counter<numOfMethods;counter++) {
				if(theList.get(i).getName().strip().equals(theList.get(counter).getName().strip())) {
					//System.out.println(theList.get(counter).getName());
					theList.remove(counter);
					counter--;
					numOfMethods--;
				}
			}
		}
	}
	
	/**
	 * Gets a list containing all of the method names 
	 * @param theList the list of methods you want the names of 
	 * @return the list of names
	 */
	public static List<String> methodListToNameList(List<MethodStore> theList) {
		List<String> methodNames = new Vector<String>();
		int numOfMethods = theList.size();
		for(int i = 0;i<numOfMethods;i++) {
			methodNames.add(theList.get(i).getName());
		}
		return methodNames;
	}
	
	/**
	 * Gets a list containing all of the method names followed by their type
	 * @param theList the list of methods you want the names and types of 
	 * @return the list of names
	 */
	public static List<String> methodListToNameAndTypeList(List<MethodStore> theList) {
		List<String> methodNames = new Vector<String>();
		int numOfMethods = theList.size();
		for(int i = 0;i<numOfMethods;i++) {
			methodNames.add(theList.get(i).getName());
			methodNames.add(theList.get(i).getReturnType());
		}
		return methodNames;
	}
	
	/**
	 * Checks if the methods called store the variable they return
	 * @param methodNames the list of methods to check
	 */
	public void checkIfReturnStored(List<String> methodNames) {
		List<String> codeRunning = removeComments(myCode);
		
		int codeSize = codeRunning.size();
		int numOfMethodNames = methodNames.size();
		//Start at 1 to ignore method declaration 
		for(int i = 1;i<codeSize;i++) {
			//System.out.println("Looping");
			for(int counter = 0;counter<numOfMethodNames;counter+=2) {
				if(codeRunning.get(i).contains(methodNames.get(counter)+"(")) {
					String line = codeRunning.get(i);
					if(!line.contains("=")||line.contains("==")) {
						errorCounter.incrementStoredReturnedVars();
						writeError("Didn't store returned "+methodNames.get(counter+1)+" variable from the method <em>"+methodNames.get(counter)+"()</em> in line: <em>"+line.strip()+"</em>",CodeCheckerConstants.htmlYellowText);
					}
					if(line.indexOf("=")>line.indexOf(methodNames.get(counter))) {
						writeError("Didn't store returned "+methodNames.get(counter+1)+" variable from the method <em>"+methodNames.get(counter)+"()</em> in line: <em>"+line.strip()+"</em>",CodeCheckerConstants.htmlYellowText);
					}
				}
			}
		}
	}
	
	/**
	 * finds the line of the closing bracket
	 * @param theCode the list that contains the code
	 * @param startLineNum the line to find the matching closing bracket of
	 * @return the line of the closing bracket
	 */
	public int findClosingBracketLine(List<String> theCode, int startLineNum) {
		int open = 1;
		int i = startLineNum;
		
		if(!theCode.get(startLineNum).contains("{")) {
			if(theCode.get(startLineNum+1).contains("{")){
				i++;
			}
			else {
				return startLineNum;
			}
		}
		while(open!=0) {
			i++;
			if(i>=theCode.size()) {
				return startLineNum;
			}
			String curLine =theCode.get(i);
			if(curLine.contains("}")||curLine.contains("{")) {
				char[] curCharLine = curLine.toCharArray();
				int lengthOfLine = curCharLine.length;
				for(int counter = 0;counter<lengthOfLine;counter++) {
					if(curCharLine[counter]=='}') {
						open--;
					}
					if(curCharLine[counter]=='{') {
						open++;
					}
					if(open==0) {
						break;
					}
				}
			}
		}
		return i;
	}
	
	/**
	 * Counts the number of elses connected to the inputed statement
	 * @param theCode the list of strings representing the code
	 * @param lineNum the line number the opening statement starts on
	 * @return the number of else statements
	 */
	public int findElses(List<String> theCode, int lineNum) {
		int endLineNum = findClosingBracketLine(myCode,lineNum);
		if(endLineNum!=lineNum) {
			if(myCode.get(endLineNum).contains("else")) {
				//System.out.println(myCode.get(endLineNum));
				return findElses(myCode, endLineNum)+1;
			}else if(endLineNum<theCode.size()-1) {
				if(theCode.get(endLineNum+1).strip().startsWith("else")){
					//System.out.println(myCode.get(endLineNum+1));
					return findElses(myCode, endLineNum+1)+1;
				}	
			}
			return 1;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Checks if method has a large if then statement
	 */
	public void checkForLargeIfElseChains() {
		List<String> codeRunning = removeComments(myCode);
		int largestChain = 0;
		int curChain = 0;
		
		int codeSize = codeRunning.size();
		for(int i = 0;i<codeSize;i++) {
			if(codeRunning.get(i).contains("if")) {
				curChain=findElses(myCode, i);
			}
			if(curChain>largestChain) {
				largestChain = curChain;
			}
		}
		if(largestChain>=AdvOptions.numOfElseNeededToBeConsideredLarge) {
			errorCounter.incrementIfThenChains();
			writeError("Contains large if/then chain containing "+largestChain+" comparison statements",CodeCheckerConstants.htmlOrangeText);
		}
	}
	
	/**
	 * Checks the indentation of the code
	 */
	public void checkIndentation() {
		List<String> codeRunning = removeComments(myCode);
		String theLine = codeRunning.get(0).replaceAll("   ", "\t");
		int startIndent = theLine.length()-theLine.stripLeading().length();
		int bracketsOpen = 0;
		//Num1 = indent size; Num2 = brackets open
		List<Duos> indentCount = new Vector<Duos>();
		
		int codeSize = codeRunning.size();
		for(int i = 0;i<codeSize;i++) {
			theLine = codeRunning.get(i).replaceAll("   ", "\t");
			int indentation = theLine.length()-theLine.stripLeading().length()-startIndent;
			boolean startsWithClose = codeRunning.get(i).stripLeading().startsWith("}");
			if(startsWithClose) {
				bracketsOpen--;
			}
			indentCount.add(new Duos(indentation,bracketsOpen));
			bracketsOpen+= countCurlyBrackets(codeRunning.get(i));
			if(startsWithClose) {
				bracketsOpen++;
			}
		}
		int numOfDuos = indentCount.size();
		int indentSize=-1;
		for(int i = 0;i<numOfDuos;i++) {
			Duos curDuo = indentCount.get(i);
			if(curDuo.getIntTwo()!=0 && curDuo.getIntOne()!=0) {
				if(curDuo.getIntOne()/curDuo.getIntTwo()!=1) {
					System.out.println(codeRunning.get(i)+ ": "+curDuo.getIntOne()+" : "+curDuo.getIntTwo());
				}
				if(indentSize==-1) {
					indentSize = curDuo.getIntOne()/curDuo.getIntTwo();
				}
				else if(indentSize!=curDuo.getIntOne()/curDuo.getIntTwo()) {
					errorCounter.incrementIndentation();
					writeError("Indentation isn't even",CodeCheckerConstants.htmlOrangeText);
					return;
				}
			}
		}
	}
}
