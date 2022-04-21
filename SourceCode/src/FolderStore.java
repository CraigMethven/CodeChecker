import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Class for storing all the folders needing to be searches
 */
public class FolderStore {
	//The directory to search though
	private File directory;
	//All the files in this directory
	private List<FileStore> theFiles;
	//All the directories in this directory
	private List<FolderStore> theDirectories;
	//For storing the error report shown on screen
	private String errorReport = null;
	//List for storing the errors in the folder
	private List<String> errorOutput;
	private ErrorSummaryCounter errorCounter;
	private boolean alreadyCountedErrors;
	private FolderStore parent;
	
	/**
	 * Gets the type of the file inputed
	 * adapted method from https://stackoverflow.com/questions/25298691/how-to-check-the-file-type-in-java 
	 * @param myFile the file we want the type of
	 * @return the file type (extension)
	 */
	public static String getExtension(File myFile) {
	    String fileName = myFile.getName();
	    int dotIndex = fileName.lastIndexOf('.');
	    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}
	
	/**
	 * Constructors
	 * @param myDirectory the directory this represents
	 */
	public FolderStore(File myDirectory) {
		directory = myDirectory;
		init();
	}
	/**
	 * Constructors
	 * @param myDirectory the directory this represents
	 * @param tempParent the folder that created this folder
	 */
	public FolderStore(File myDirectory,FolderStore tempParent) {
		directory = myDirectory;
		parent = tempParent;
		init();
	}
	/**
	 * constructor
	 * @param myDirectory the directory this represents
	 */
	public FolderStore(String myDirectory) {
		directory = new File(myDirectory);
		init();	
	}
	
	/**
	 * Initialises the components
	 */
	private void init() {
		String extention = getExtension(directory).toLowerCase();
		//Create lists
		theFiles = new Vector<FileStore>();
		theDirectories = new Vector<FolderStore>();
		
		if(directory.isFile()) {
			if(extention.equals("java") || extention.equals("cpp") || extention.equals("cs") || extention.equals("c")) {
				theFiles.add(new FileStore(directory, extention,this));
			}
		}
		else {
			//Get all the files in this directory
			File[] allContent = directory.listFiles();
			int numberOfItems = allContent.length;
			
			//Add all of the contents of the directory to the appropriate classes
			for (int i = 0; i < numberOfItems; i++) {
				if (allContent[i].isFile()) {
					extention = getExtension(allContent[i]).toLowerCase();
					if(extention.equals("java") || extention.equals("cpp") || (extention.equals("cs")&&!allContent[i].getName().contains(".Designer.")) || extention.equals("c")) {
						theFiles.add(new FileStore(allContent[i], extention,this));
					}
				} else if (allContent[i].isDirectory()) {
					theDirectories.add(new FolderStore(allContent[i],this));
				}
			}
			clean();
			//printFiles();
			//printErrorsFolder(0);
		}
		errorOutput = new Vector<String>();
		errorCounter = new ErrorSummaryCounter();
		alreadyCountedErrors = false;
		runChecks();
	}
	
	/**
	 * returns the name of this folder and all previous ones uploaded
	 * @return the folder path
	 */
	public String getFoldersName() {
		String returner="";
		if(parent!=null) {
			returner+=parent.getFoldersName()+"/";
		}
		return returner+=getName();
	}
	
	/**
	 * Prints all of the files in the directory
	 */
	public void printFiles() {
		for(int i = 0; i< theFiles.size();i++) {
			System.out.println("\nNo Comments: " + theFiles.get(i).getName());
			//System.out.println(theFiles.get(i).removeComments(theFiles.get(i).getCode()) + "\n");
			Main.printList(theFiles.get(i).removeComments(theFiles.get(i).getCode()));
		}
	}
	
	/**
	 * Method for checking if the folder store actually contains any files that have errors to display
	 * @return if the folder contains any files that contain an error
	 */
	public boolean checkFolderForErrors() {
		//If there are files
		if(!theFiles.isEmpty()) {
			int numOfFiles = theFiles.size();
			//Loop through files
			for(int i = 0;i<numOfFiles;i++) {
				//If they contain an error return true
				if(theFiles.get(i).checkFileForErrors()) {
					return true;
				}
			}
		}
		//If there are directories
		if(!theDirectories.isEmpty()){
			int numOfDirs = theDirectories.size();
			//loop through folders
			for(int i = 0;i<numOfDirs;i++) {
				//If a folder has a file that contains an error
				if(theDirectories.get(i).checkFolderForErrors()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Gets the name of the directory
	 * @return the name
	 */
	public String getName() {
		return directory.getName();
	}
	/**
	 * gets the absolute file path of this directory
	 * @return the filepath
	 */
	public String getFilePath() {
		return directory.getAbsolutePath();
	}
	
	/**
	 * Prints the name of this directory and all of the child directories it stores to console
	 */
	public void printAllFolderStores() {
		System.out.println(directory.getName());
		int numOfFolders = theDirectories.size();
		for(int i =0;i<numOfFolders;i++) {
			theDirectories.get(i).printAllFolderStores();
		}
	}
	
	/**
	 * Delete a stored folder from directories list
	 * @param folderName the name of the folder being deleted
	 */
	public void deleteStoredFolder(String folderName) {
		int numOfFolders = theDirectories.size();
		//Loop through all folders
		for(int i =0;i<numOfFolders;i++) {
			//If it matches the name given then remove it
			if(theDirectories.get(i).getName().equals(folderName)) {
				theDirectories.remove(i);
				return;
			}
		}
	}
	
	/**
	 * Removes the directories that don't contain anything we want
	 */
	public void clean() {
		int numOfFolders = theDirectories.size();
		for(int i =0;i<numOfFolders;i++) {
			if(theDirectories.get(i).getDirAndFileNum()==0) {
				theDirectories.remove(i);
				numOfFolders = theDirectories.size();
				i-=1;
			}
		}
	}
	
	/**
	 * gets the number of directories and files this directory directly houses
	 * @return the total number
	 */
	public int getDirAndFileNum() {
		return theDirectories.size()+theFiles.size(); 
	}
		
	/**
	 * 
	 * @param searchPath
	 * @return
	 */
	public boolean eligibleFolder(String searchPath) {
		//System.out.println("Currently on: "+directory.getAbsolutePath());
		if(directory.getAbsolutePath().equals(searchPath)) {
			return true;
		}
		
		int numOfFolders = theDirectories.size();
		for(int i =0;i<numOfFolders;i++) {
			if(theDirectories.get(i).eligibleFolder(searchPath)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes a point to a folder, hence removing it
	 * @param filePath List of strings making up the file path of the folder
	 * @param startPos The position of the list the current iteration should start on
	 */
	public void removeFolder(List<String> filePath, int startPos) {
		//System.out.println("In Folder "+getName());
		int pathSize = filePath.size()-1;
		//System.out.println("At pos: "+startPos+"; Max Pos: "+pathSize);
		if(pathSize<=startPos) {
			//System.out.println("Went past it");
			return;
		}
		else if(pathSize-1==startPos) {
			//System.out.println("Removing folder");
			int directorySize = theDirectories.size();
			FolderStore deleted;
			for(int i =0;i<directorySize;i++) {
				deleted = theDirectories.get(i);
				if(deleted.getName().equals(filePath.get(pathSize))) {
					theDirectories.remove(i);
					//System.out.println("Removed directory: "+deleted.getName());
				}
			}
		}
		else {
			//System.out.println("Going to next folder");
			int directorySize = theDirectories.size();
			FolderStore deleted;
			for(int i =0;i<directorySize;i++) {
				deleted = theDirectories.get(i);
				if(deleted.getName().equals(filePath.get(startPos+1))) {
					deleted.removeFolder(filePath, startPos+1);
				}
			}
		}
	}
	
	/**
	 * Remove a file that is represented by the file path given
	 * @param filePath the file needing removed
	 * @param startPos the position of this folder in the file path list
	 */
	public void removeFile(List<String> filePath, int startPos) {
		int pathSize = filePath.size()-1;
		if(pathSize<=startPos) {
			return;
		}
		else if(pathSize-1==startPos) {
			int directorySize = theFiles.size();
			FileStore deleted;
			for(int i =0;i<directorySize;i++) {
				deleted = theFiles.get(i);
				if(deleted.getName().equals(filePath.get(pathSize))) {
					theFiles.remove(i);
				}
			}
		}
		else {
			//System.out.println("Going to next folder");
			int directorySize = theDirectories.size();
			FolderStore deleted;
			for(int i =0;i<directorySize;i++) {
				deleted = theDirectories.get(i);
				if(deleted.getName().equals(filePath.get(startPos+1))) {
					deleted.removeFolder(filePath, startPos+1);
				}
			}
		}
	}
	
	/**
	 * Gets a file using its file path
	 * @param filePath the file path of the file
	 * @param startPos the position of this folder in the file path list
	 * @return the file represented by the filepath
	 */
	public FileStore getFile(List<String> filePath, int startPos) {
		FileStore returner;
		//System.out.println(getName() + " "+ getFilePath());
		//System.out.println(filePath.get(startPos));
		
		//System.out.println("Currently at: " + directory.getName());
		if(directory.getAbsolutePath().equals(filePath.get(startPos)) || directory.getName().equals(filePath.get(startPos))) {
			if(filePath.size()==1) {
				return theFiles.get(0);
			}
			if(filePath.get(startPos+1).contains(".")) {
				int filesSize = theFiles.size();
				for(int i =0;i<filesSize;i++) {
					if(theFiles.get(i).getName().equals(filePath.get(startPos+1))){
						return theFiles.get(i);
					}
				}
			}else {
				int directorySize = theDirectories.size();
				for(int i =0;i<directorySize;i++) {
					returner = theDirectories.get(i).getFile(filePath, startPos+1);
					if(returner!=null) {
						return returner;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets a folder using its file path
	 * @param filePath the file path of the folder
	 * @param startPos the position of this folder in the file path list
	 * @return the folder represented by the filepath given
	 */
	public FolderStore getFolder(List<String> filePath, int startPos) {
		FolderStore returner;
		//System.out.println("Currently at: " + directory.getName());
		if(directory.getAbsolutePath().equals(filePath.get(startPos)) || directory.getName().equals(filePath.get(startPos))) {
			int pathSize = filePath.size();
			if(pathSize==startPos+1) {
				return this;
			}else {
				int directorySize = theDirectories.size();
				for(int i =0;i<directorySize;i++) {
					returner = theDirectories.get(i).getFolder(filePath, startPos+1);
					if(returner!=null) {
						return returner;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Reruns all of the checks
	 */
	public void rerunChecks() {
		errorReport = null;
		runChecks();
		int numOfDirs = theDirectories.size();
		for(int i = 0;i<numOfDirs;i++) {
			theDirectories.get(i).rerunChecks();
		}
		int numOfFiles = theFiles.size();
		for(int i = 0;i<numOfFiles;i++) {
			theFiles.get(i).rerunChecks();
		}
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
	 * counts the number of classes
	 * @return the number of classes
	 */
	public int countClasses() {
		int numClasses = 0;
		int numOfDirs = theDirectories.size();
		for(int i = 0;i<numOfDirs;i++) {
			numClasses+=theDirectories.get(i).countClasses();
		}
		int numOfFiles = theFiles.size();
		for(int i = 0;i<numOfFiles;i++) {
			numClasses+=theFiles.get(i).getNumOfClasses();
		}
		return numClasses;
	}
	/**
	 * counts the number of methods
	 * @return the number of methods
	 */
	public int countMethods() {
		int numMethods = 0;
		int numOfDirs = theDirectories.size();
		for(int i = 0;i<numOfDirs;i++) {
			numMethods+=theDirectories.get(i).countMethods();
		}
		int numOfFiles = theFiles.size();
		for(int i = 0;i<numOfFiles;i++) {
			numMethods+=theFiles.get(i).getNumOfMethods();
		}
		return numMethods;
	}
	/**
	 * counts the number of class variables
	 * @return the number of class variables
	 */
	public int countVariables() {
		int numVariables = 0;
		int numOfDirs = theDirectories.size();
		for(int i = 0;i<numOfDirs;i++) {
			numVariables+=theDirectories.get(i).countVariables();
		}
		int numOfFiles = theFiles.size();
		for(int i = 0;i<numOfFiles;i++) {
			numVariables+=theFiles.get(i).getNumOfVariables();
		}
		return numVariables;
	}
	
	/*
	 * ERROR CHECKING START
	 */
	
	/**
	 * Gets the summary of all of the errors in this folder
	 * @param myList The list to save the summary to
	 */
	public void getErrorSummary(List<String> myList) {
		//Classes then methods then variables
		populateErrorSummary().printSummary(getName(),myList, countClasses(), countMethods(), countVariables());
	}
	
	/**
	 * populates the error summary with the correct numbers
	 * @return the completed error summary
	 */
	public ErrorSummaryCounter populateErrorSummary() {
		if(!alreadyCountedErrors) {
			int numOfDirs = theDirectories.size();
			for(int i = 0;i<numOfDirs;i++) {
				errorCounter.add(theDirectories.get(i).populateErrorSummary());
			}
			int numOfFiles = theFiles.size();
			for(int i = 0;i<numOfFiles;i++) {
				errorCounter.add(theFiles.get(i).populateErrorSummary());
			}
			alreadyCountedErrors = true;
		}
		
		return errorCounter;
	}
	
	/**
	 * Either saved the errors contained in this folder to the list inputted or prints to terminal if list is null
	 * @param numberOfTabs the number of times to tab in front of these errors
	 * @param allErrors the list to save the errors to
	 */
	public void printErrorsFolder(int numberOfTabs, List<String> allErrors) {
		if(!checkFolderForErrors()) {
			return;
		}
		//Print errors to console 
		if(allErrors==null) {
			System.out.print("\n");
			CodeChunk.printTabs(numberOfTabs,allErrors);
			System.out.println("Directory&nbsp;"+CodeCheckerConstants.htmlFolderColor+directory.getName()+CodeCheckerConstants.htmlColorClose+":");
		}
		//Print errors to list
		else {
			//Don't take a new line as the first item in the error report
			if(!allErrors.isEmpty()) {
				allErrors.add("\n");
			}
			CodeChunk.printTabs(numberOfTabs,allErrors);
			if(errorOutput.isEmpty()) {
				allErrors.add("Directory&nbsp;"+CodeCheckerConstants.htmlFolderColor+directory.getName()+CodeCheckerConstants.htmlColorClose+":\n");
			}
			else {
				allErrors.add("Directory&nbsp;"+CodeCheckerConstants.htmlFolderColor+directory.getName()+CodeCheckerConstants.htmlColorClose+":");
			}
		}
		printErrors(numberOfTabs,allErrors);
		
		//Print all errors with files in this directory
		if(!theFiles.isEmpty()) {
			int numOfFiles = theFiles.size();
			for(int i = 0;i<numOfFiles;i++) {
				theFiles.get(i).printErrorsFile(numberOfTabs+1,allErrors);
			}
		}
		//Print all errors with folders in this directory
		if(!theDirectories.isEmpty()) {
			int numOfDirs = theDirectories.size();
			for(int i = 0;i<numOfDirs;i++) {
				theDirectories.get(i).printErrorsFolder(numberOfTabs+1,allErrors);
			}
		}
	}
	
	/**
	 * Prints the errors of this folder either to terminal or to a list
	 * @param numOfTabs the number of tabs to start at
	 * @param allErrors if null print to terminal else add to this list
	 */
	public void printErrors(int numOfTabs, List<String> allErrors) {
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
						CodeChunk.printTabs(numOfTabs,null);
						System.out.println(errorOutput.get(i));
					}else {
						CodeChunk.printTabs(numOfTabs,allErrors);
						allErrors.add(errorOutput.get(i)+"\n");
					}
					
				}
			}
		}
	}
	
	/**
	 * Writes the errors to the error output
	 * @param theError the text of the error
	 * @param errorColor the colour specifying the severity of the error
	 */
	public void writeError(String theError, String errorColor) {
		errorOutput.add("-"+errorColor+theError+CodeCheckerConstants.htmlColorClose);
	}
	
	/**
	 * Runs the checks needed on folders
	 */
	public void runChecks() {
		errorOutput = new Vector<String>();
		errorCounter = new ErrorSummaryCounter();
		alreadyCountedErrors = false;
		if(Options.checkOveruseOfStaticMethods) {
			checkStaticMethodOveruse();
		}
		if(Options.checkOveruseOfStaticVariables) {
			checkStaticVariableOveruse();
		}
		if(Options.checkCorrectUseOfAccessModifiers) {
			checkPublicVariableOveruse();
		}
		if(Options.checkStorageOfReturnedVariable) {
			checkStorageOfReturnedVarsForAllMethods();
		}
	}
	
	/**
	 * Writes errors for duos used for finding errors
	 * @param message the message needing printed with the numbers
	 * @param nums the duo storing the total number of the item (1) and the number that has issues (2)
	 * @param orangeThreshold the threshold precentage for the text printing as orange
	 */
	public void printErrorFromDuo(String message, Duos nums, int orangeThreshold) {
		//If no errors don't print anything
		if(nums.getIntTwo()==0) {
			return;
		}
		int percentageOfStaticMethods = (nums.getIntTwo()*100)/nums.getIntOne();
		//System.out.println("Static percentage "+percentageOfStaticMethods + myCount.getIntTwo());
		if(percentageOfStaticMethods>orangeThreshold) {
			writeError(percentageOfStaticMethods+"% "+message, CodeCheckerConstants.htmlOrangeText);
		}
		else {
			writeError(percentageOfStaticMethods+"% "+message, CodeCheckerConstants.htmlNormalText);
		}
	}
	
	/**
	 * Checks for static method overuse in the entire file
	 */
	public void checkStaticMethodOveruse() {
		printErrorFromDuo("of methods in this directory are static",staticMethodCount(),AdvOptions.staticMethodOverusePercentage);
	}
	
	/**
	 * Get the count of static methods and regular methods in a folder
	 * @return a duo where number 1 = total num of methods; 2 = num of static methods
	 */
	public Duos staticMethodCount(){
		Duos tempDuo = new Duos(0,0);
		int fileCount = theFiles.size();
		for(int i = 0;i<fileCount;i++) {
			theFiles.get(i).staticMethodCount(tempDuo);
		}
		errorCounter.setStaticMethods(tempDuo.getIntTwo());
		return tempDuo;
	}
	
	/**
	 * Checks for static variable overuse in the entire directory
	 */
	public void checkStaticVariableOveruse() {
		printErrorFromDuo("of class variables in this directory are static",staticVariableCount(),AdvOptions.staticVariableOverusePercentage);
	}
	
	/**
	 * Get the count of static variable and regular variables in a folder
	 * @return a duo where number 1 = total num of variables; 2 = num of static variables
	 */
	public Duos staticVariableCount(){
		Duos tempDuo = new Duos(0,0);
		int fileCount = theFiles.size();
		for(int i = 0;i<fileCount;i++) {
			theFiles.get(i).staticVariableCount(tempDuo);
		}
		errorCounter.setStaticVariable(tempDuo.getIntTwo());
		return tempDuo;
	}
	
	/**
	 * Checks for public variable overuse in the entire directory
	 */
	public void checkPublicVariableOveruse() {
		printErrorFromDuo("of class variables in this directory are public",publicVariableCount(),AdvOptions.publicVariableOverusePercentage);
	}
	
	/**
	 * Get the count of public variable and regular variables in a folder
	 * @return a duo where number 1 = total num of variables; 2 = num of public variables
	 */
	public Duos publicVariableCount(){
		Duos tempDuo = new Duos(0,0);
		int fileCount = theFiles.size();
		for(int i = 0;i<fileCount;i++) {
			theFiles.get(i).publicVariableCount(tempDuo);
		}
		errorCounter.setPublicVariable(tempDuo.getIntTwo());
		return tempDuo;
	}
	
	/**
	 * Gets all of the methods in this folder and saved them to the list inputed
	 * @param theMethods place to save the methods
	 */
	public void getAllMethods(List<MethodStore> theMethods) {
		int numOfDirs = theDirectories.size();
		for(int i = 0;i<numOfDirs;i++) {
			theDirectories.get(i).getAllMethods(theMethods);
		}
		int numOfFiles = theFiles.size();
		for(int i = 0;i<numOfFiles;i++) {
			theFiles.get(i).getAllMethods(theMethods);
		}
	}
	
	/**
	 * Checks if methods calls to methods that return variables if the variable is stored
	 */
	public void checkStorageOfReturnedVarsForAllMethods() {
		List<MethodStore> allMethods = new Vector<MethodStore>();
		int numOfFiles = theFiles.size();
		for(int i = 0;i<numOfFiles;i++) {
			theFiles.get(i).getAllMethods(allMethods);
		}
		if(!allMethods.isEmpty()) {
			List<MethodStore> methodsWithoutOutput = new Vector<MethodStore>(allMethods);
			MethodStore.removeMethodsWithoutReturns(methodsWithoutOutput);
			if(!methodsWithoutOutput.isEmpty()) {
				MethodStore.removeMethodsWithDuplicatNames(methodsWithoutOutput);
				List<String> methodNames = MethodStore.methodListToNameAndTypeList(methodsWithoutOutput);
				int numOfAllMethods = allMethods.size();
				for(int i = 0;i<numOfAllMethods;i++) {
					allMethods.get(i).checkIfReturnStored(methodNames);
				}
			}
		}
	}
}
