import java.io.File;  
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/**
 * Class for storing files
 * @author Craig
 *
 */
public class FileStore extends CodeChunk{
	private File myFile;
	private List<ClassStore> myClasses;
	private List<MethodStore> myMethods;
	//For storing the error report shown on screen
	private String errorReport;
	private FolderStore parent;
	
	
	/**
	 * Constructor
	 * @param tempFile file to store
	 */
	public FileStore(File tempFile){
		myFile = tempFile;
		name = myFile.getName();
		fileType = FolderStore.getExtension(myFile);
		init();
	}
	/**
	 * Constructor
	 * @param tempFile file to store
	 * @param tempType the type of the file inputed
	 */
	public FileStore(File tempFile, String tempType){
		myFile = tempFile;
		name = myFile.getName();
		fileType = tempType;
		init();
	}
	/**
	 * Constructor
	 * @param fileName the name of the file stored
	 */
	public FileStore(String fileName){
		name = fileName;
		myFile = new File(name);
		fileType = FolderStore.getExtension(myFile);
		init();
	}
	/**
	 * Constructor
	 * @param tempFile file to store
	 * @param tempType the type of the file inputed
	 * @param tempParent folderstore holding this file
	 */
	public FileStore(File tempFile, String tempType,FolderStore tempParent){
		myFile = tempFile;
		parent = tempParent;
		name = myFile.getName();
		fileType = tempType;
		init();
	}
	/**
	 * Basic method to initialise the components with what they are meant to store
	 */
	private void init() {
		myClasses = new Vector<ClassStore>();
		myMethods = new Vector<MethodStore>();
		readInFile();
		findClasses();
		if(!fileType.equals("java")&&!fileType.equals("cs")) {
			findMethods();
			removeWrongMethods();
		}
		cleanMethods();
		runChecks();
	}
	
	/**
	 * returns the files path
	 * @return the file path
	 */
	public String getFilePath() {
		if(parent!=null) {
			return parent.getFoldersName()+"/"+name;
		}
		return null;
	}
	
	/**
	 * Returns the parent
	 * @return the parent
	 */
	public FolderStore getParent() {
		return parent;
	}
	
	/*
	 * Basic method to read in the file and save every line to a list of strings
	 */
	private void readInFile() {
		try {
			Scanner myReader = new Scanner(myFile);
			while (myReader.hasNextLine()) {
				myCode.add(myReader.nextLine());
			}
			myReader.close();
		} catch (FileNotFoundException e) {
		    System.out.println("An error occurred.");
		    e.printStackTrace();
		}
	}
	
	/**
	 * Gets the file code without any of the classes inside it
	 * @return the list of strings containing lines of code
	 */
	public List<String> getFileWithoutClasses() {
		List<String> tempCode = new Vector<String>(myCode);
		int numOfClasses = myClasses.size();
		int prevHighest = 0;
		int totalRemoved = 0;
		
		//For all methods
		for(int i = 0;i<numOfClasses;i++) {
			ClassStore curClass = myClasses.get(i);
			//curClass.print();
			//Get start position based on classes start position
			int curStartPos = curClass.getStartPos() - getStartPos();
			//Get the length of the method
			int theLength = curClass.getLength();
			//If the method is below the past removed methods
			if(curStartPos>prevHighest) {
				ClassStore.removeLines(tempCode, curStartPos-totalRemoved, theLength);
				totalRemoved += theLength;
				prevHighest = curStartPos+theLength;
			}
			//If the method ends below the previously removed methods
			else if(curStartPos+theLength>prevHighest) {
				ClassStore.removeLines(tempCode, prevHighest-totalRemoved, (curStartPos-prevHighest)+theLength);
				totalRemoved += (curStartPos-prevHighest)+theLength;
				prevHighest = curStartPos+theLength;
			}
		}
		//Main.printList(tempCode);
		return removeComments(tempCode);
	}
	
	/*
	 * Finds the classes within the code and saves them as ClassStores
	 * CURRENTLY DOESN'T WORK IF CLASS HAS A CAPITAL C
	 */
	private void findClasses() {
		//Finds all the classes
		List<Duos> classesLines = findEntireStatement("class ");
		//Saves all the classes
		if(!classesLines.isEmpty()) {
			int numberOfClasses = classesLines.size();
			for(int i = 0; i<numberOfClasses; i++) {
				myClasses.add(new ClassStore(myCode.subList(classesLines.get(i).getIntOne(), classesLines.get(i).getIntTwo()),fileType,startLine+classesLines.get(i).getIntOne()+1,this));
			}
		}
	}
	
	/**
	 * initialises the cleaned code for the methods
	 */
	public void cleanMethods() {
		int numOfMethods = myMethods.size();
		for(int i = 0;i<numOfMethods;i++) {
			MethodStore curMethod = myMethods.get(i);
			curMethod.setCleanedCode(curMethod.cleanCode(myCode, curMethod.getVariables()));
		}
	}
	
	/*
	 * Finds the methods in this class that aren't in a class
	 */
	public void findMethods() {
		List<String> myFile = getFileWithoutClasses();
		//Main.printList(myFile);
		List<Integer> methodPos = new Vector<Integer>();
		int fileLength = myFile.size();
		boolean usable;
		
		//Part for finding methods
		//For every line of code
		for(int i = 0; i<fileLength;i++) {
			usable = true;
			String myLine = myFile.get(i);
			//Make sure it contains the characters that a method statement must
			if(myLine.contains("(") && myLine.contains(")") && !myLine.contains(".")) {
				if(i+1>=fileLength) {
					break;
				}
				if(myLine.contains("{")||myFile.get(i+1).strip().equals("{")){
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
			List<Duos> myPoints = findEntireStatement(myFile,methodPos);
			//System.out.println(name+":"+myPoints.size());
			//Saves all the methods
			if(!myPoints.isEmpty()) {
				int numberOfMethods = myPoints.size();
				for(int i = 0; i<numberOfMethods; i++) {
					int int2 = myPoints.get(i).getIntTwo()-1;
					if(int2>=myFile.size()) {
						//System.out.println(myFile.size()+":"+myPoints.get(i).getIntTwo());
						continue;
					}
					myMethods.add(new MethodStore(myFile.subList(myPoints.get(i).getIntOne(), int2),fileType,myPoints.get(i).getIntOne()));
					//System.out.println(name+" Potato "+myMethods.size()+myMethods.get(0).printErrors(0, null));
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
			if(curMethod.getName()==null || curMethod.getName().contains("//") || curMethod.getName().equals("null")||curMethod.getName().equals("catch")) {
				myMethods.remove(i);
				i--;
				numOfMethods = myMethods.size();
			}
		}
	}
	
	/**
	 * Checks to see if this file contains errors or any methods that have errors
	 * @return true if contains at least 1 error
	 */
	public boolean checkFileForErrors() {
		if(checkForErrors()) {
			return true;
		}
		if(!myClasses.isEmpty()) {
			int numOfClasses = myClasses.size();
			for(int i = 0;i<numOfClasses;i++) {
				if(myClasses.get(i).checkClassForErrors()) {
					return true;
				}
			}
		}
		if(!myMethods.isEmpty()) {
			int numOfMethods = myMethods.size();
			for(int i = 0;i<numOfMethods;i++) {
				if(myMethods.get(i).checkForErrors()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Reruns the checks
	 */
	public void rerunChecks() {
		errorReport = null;
		runChecks();
		int numOfMethods = myMethods.size();
		for(int i = 0;i<numOfMethods;i++) {
			myMethods.get(i).runChecks();
		}
		int numOfClasses = myClasses.size();
		for(int i = 0;i<numOfClasses;i++) {
			myClasses.get(i).rerunChecks();
		}
	}
	
	/**
	 * gets the file
	 * @param myName the name of the file wanted
	 * @return the file if it matches the name given
	 */
	public FileStore getFile(String myName){
		if(name.equals(myName)) {
			return this;
		}
		return null;
	}
	
	/**
	 * Basic setter
	 * @param myReport what to set error report to
	 */
	public void setErrorReport(String myReport) {
		errorReport = myReport;
	}
	
	/**
	 * basic getter
	 * @return error report
	 */
	public String getErrorReport() {
		return errorReport;
	}
	/**
	 * gets the number of classes
	 * @return the number of classes
	 */
	public int getNumOfClasses() {
		return myClasses.size();
	}
	/**
	 * gets the number of class variables
	 * @return the number of class variables
	 */
	public int getNumOfVariables() {
		int numOfVars = 0;
		int numOfClasses = myClasses.size();
		for(int i = 0;i<numOfClasses;i++) {
			numOfVars += myClasses.get(i).getNumOfClassVariables();
		}
		int numOfMethods = myMethods.size();
		for(int i = 0;i<numOfMethods;i++) {
			numOfVars += myMethods.get(i).getNumOfVariables();
		}
		return numOfVars;
	}
	/**
	 * gets the number of methods
	 * @return the number of methods
	 */
	public int getNumOfMethods() {
		int numOfMethods = 0;
		int numOfClasses = myClasses.size();
		for(int i = 0;i<numOfClasses;i++) {
			numOfMethods += myClasses.get(i).getNumOfMethods();
		}
		numOfMethods+=myMethods.size();
		return numOfMethods;
	}
	
	/*
	 * START OF ERROR CHECKS
	 */
	
	/**
	 * Gets the summary of all of the errors in this folder
	 * @param myList The list to save the summary to
	 */
	public void getErrorSummary(List<String> myList) {
		//Classes then methods then variables
		populateErrorSummary().printSummary(name,myList, getNumOfClasses(), getNumOfMethods(), getNumOfVariables());
	}
	
	/**
	 * populates the error summary with the correct numbers
	 * @return the completed error summary
	 */
	public ErrorSummaryCounter populateErrorSummary() {
		if(!alreadyCountedErrors) {
			int numOfClasses = myClasses.size();
			for(int i = 0;i<numOfClasses;i++) {
				errorCounter.add(myClasses.get(i).populateErrorSummary());
			}
			int numOfMethods = myMethods.size();
			for(int i = 0;i<numOfMethods;i++) {
				errorCounter.add(myMethods.get(i).getErrorSummary());
			}
			alreadyCountedErrors = true;
		}
		return errorCounter;
	}
	
	/**
	 * Prints the error report for this file and all methods inside it
	 * @param numberOfTabs the amount of tabs to print before printing the error
	 * @param allErrors the list to add the errors to
	 */
	public void printErrorsFile(int numberOfTabs, List<String> allErrors) {
		if(!checkFileForErrors()) {
			return;
		}
		//Print errors to console if no list was sent
		if(allErrors == null) {
			System.out.print("\n");
			CodeChunk.printTabs(numberOfTabs,null);
			System.out.println("File&nbsp;"+CodeCheckerConstants.htmlFileColor+name+CodeCheckerConstants.htmlColorClose+":");
		//add to list
		}else {
			//Don't take a new line as the first line in the error report
			if(!allErrors.isEmpty()) {
				allErrors.add("\n");
			}
			CodeChunk.printTabs(numberOfTabs,allErrors);
			if(!checkForErrors()) {
				allErrors.add("File&nbsp;"+CodeCheckerConstants.htmlFileColor+name+CodeCheckerConstants.htmlColorClose+":\n");
			}
			else {
				allErrors.add("File&nbsp;"+CodeCheckerConstants.htmlFileColor+name+CodeCheckerConstants.htmlColorClose+":");
			}
		}
		printErrors(numberOfTabs, allErrors);
		
		if(!myMethods.isEmpty()) {
			int numOfMethods = myMethods.size();
			//for all methods
			for(int i = 0;i<numOfMethods;i++) {
				MethodStore gottenMethod = myMethods.get(i);
//				System.out.println(gottenMethod.getName()+":");
//				Main.printList(gottenMethod.getCleanedCode());
				//If they contain errors print them
				if(gottenMethod.checkForErrors()) {
					gottenMethod.printErrors(numberOfTabs+1, allErrors);
					//gottenMethod.printErrors(numberOfTabs+1, null);
				}
			}
		}
		if(!myClasses.isEmpty()) {
			int numOfFiles = myClasses.size();
			for(int i = 0;i<numOfFiles;i++) {
				myClasses.get(i).printErrorsClass(numberOfTabs+1,allErrors);
			}
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
	 * Runs the checks to find the errors needed
	 */
	public void runChecks() {
		errorOutput = new Vector<String>();
		errorCounter = new ErrorSummaryCounter();
		alreadyCountedErrors = false;
		checkSearchWords();
	}
	
	/**
	 * Counts the amount of static methods in this file
	 * @param tempDuo Duo to store the amount of static methods and the amount of total methods. 1 = total num of methods; 2 = num of static methods
	 */
	public void staticMethodCount(Duos tempDuo){
		int classCount = myClasses.size();
		for(int i = 0;i<classCount;i++) {
			myClasses.get(i).incrementDuoFromStaticMethods(tempDuo);
		}
		int methodCount = myMethods.size();
		for(int i = 0;i<methodCount;i++) {
			if(myMethods.get(i).getStatic()) {
				tempDuo.increment(0, 1);
			}
			tempDuo.increment(1, 0);
		}
	}
	
	/**
	 * Counts the amount of static variables in this file
	 * @param tempDuo Duo to store the amount of static variables and the amount of total class variables
	 */
	public void staticVariableCount(Duos tempDuo){
		int classCount = myClasses.size();
		for(int i = 0;i<classCount;i++) {
			myClasses.get(i).incrementDuoFromStaticVariables(tempDuo);
		}
	}
	
	/**
	 * Counts the amount of static variables in this file
	 * @param tempDuo Duo to store the amount of static variables and the amount of total class variables
	 */
	public void publicVariableCount(Duos tempDuo){
		int classCount = myClasses.size();
		for(int i = 0;i<classCount;i++) {
			myClasses.get(i).incrementDuoFromPublicVariables(tempDuo);
		}
	}
	
	/**
	 * gets all of the methods from the classes in this file
	 * @param theMethods the list you want the methods saved to
	 */
	public void getAllMethods(List<MethodStore> theMethods) {
		int classCount = myClasses.size();
		for(int i = 0;i<classCount;i++) {
			theMethods.addAll(myClasses.get(i).getMethods());
		}
		if(myMethods!=null) {
			if(!myMethods.isEmpty()) {
				theMethods.addAll(myMethods);
			}
		}
	}
	
	/**
	 * Search for the search words
	 */
	public void checkSearchWords() {
		if(AdvOptions.searchTerms !=null) {
			if(!AdvOptions.searchTerms.isEmpty()) {
				int numOfLines = myCode.size();
				int numOfSearchTerms = AdvOptions.searchTerms.size();
				for(int i = 0;i<numOfLines;i++) {
					for(int counter = 0;counter<numOfSearchTerms;counter++) {
						if(myCode.get(i).contains(AdvOptions.searchTerms.get(counter))) {
							writeError("Contains <em>"+AdvOptions.searchTerms.get(counter)+"</em> on line "+i+": <em>"+myCode.get(i).strip()+"</em>", CodeCheckerConstants.htmlGreenText);
							errorCounter.addWordFound(AdvOptions.searchTerms.get(counter));
							//System.out.println(AdvOptions.searchTerms.get(counter)+" found");
						}
					}
				}
			}
		}
	}
}

