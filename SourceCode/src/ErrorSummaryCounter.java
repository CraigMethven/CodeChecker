import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

/**
 * Class to count the errors that have occurred
 * @author Craig
 *
 */
public class ErrorSummaryCounter {
	//All public so that I can grab them easier although they are only accessed in a private way
	public int noComments=0;
	public int javadocComments = 0;
	public int largeMethods = 0;
	public int sanitizedInputs=0;
	public int repeatedCode=0;
	public int storedReturnedVars=0;
	public int staticMethods=0;
	public int staticVariable=0;
	public int mainMethodLength=0;
	public int ifThenChains=0;
	public int privacyMethods = 0;
	public int privacyVariables = 0;
	public int publicVariable=0;
	public int scannerClosed=0;
	public int indentation=0;
	public int instantSemiColon=0;
	public int classNameConvenstion=0;
	public int methodNameConvenstion=0;
	public int variableNameConvenstion=0;
	public int stringComparison=0;
	public int recursionInput=0;
	public int spelling=0;
	public int copying=0;
	public int cheating=0;
	public List<String> wordsFound;
	
	/**
	 * initialises the list
	 */
	public ErrorSummaryCounter() {
		wordsFound = new Vector<String>();
	}
	
	/**
	 * Method to add together 2 error summary counters
	 * @param adder the error summary counter to add to the current one
	 */
	public void add(ErrorSummaryCounter adder) {
		noComments+=adder.getNoComments();
		javadocComments+=adder.getJavadocComments();
		largeMethods +=adder.getLargeMethods();
		sanitizedInputs+=adder.getSanitizedInputs();
		repeatedCode+=adder.getRepeatedCode();
		storedReturnedVars+=adder.getStoredReturnedVars();
		staticMethods+=adder.getStaticMethods();
		staticVariable+=adder.getStaticVariable();
		mainMethodLength+=adder.getMainMethodLength();
		ifThenChains+=adder.getIfThenChains();
		privacyMethods+=adder.getPrivacyMethods();
		privacyVariables+=adder.getPrivacyVariables();
		publicVariable+=adder.getPublicVariable();
		scannerClosed+=adder.getScannerClosed();
		indentation+=adder.getIndentation();
		instantSemiColon+=adder.getInstantSemiColon();
		classNameConvenstion+=adder.getClassNameConvenstion();
		methodNameConvenstion+=adder.getMethodNameConvenstion();
		variableNameConvenstion+=adder.getVariableNameConvenstion();
		stringComparison+=adder.getStringComparison();
		recursionInput+=adder.getRecursionInput();
		spelling+=adder.getSpelling();
		copying+=adder.getCopying();
		cheating+=adder.getCheating();
		wordsFound.addAll(adder.getWords());
	}	
	/**
	 * Prints a summary of all the errors to the list
	 * @param name the name of the item this is an error summary for
	 * @param myList the list to print to
	 * @param numOfClasses the number of classes represented by this report
	 * @param numOfMethods the number of methods represented by this report
	 * @param numOfVariables the number of variables represented by this report
	 */
	public void printSummary(String name, List<String> myList, int numOfClasses, int numOfMethods, int numOfVariables) {
		if(!checkIfErrors()) {
			return;
		}
		myList.add("Report Summary for "+name+":\n");
		findErrorColor(myList,toPercentage(noComments, numOfClasses+numOfMethods)+"% of classes and methods don't have a comment above them",noComments,getPercentage(numOfClasses+numOfMethods,5),getPercentage(numOfClasses+numOfMethods,35),getPercentage(numOfClasses+numOfMethods,70));
		findErrorColor(myList,toPercentage(javadocComments,numOfMethods)+"% of methods don't have a correct use of javadocs comments",javadocComments,getPercentage(numOfMethods,5),getPercentage(numOfMethods,35),getPercentage(numOfMethods,70));
		findErrorColor(myList,largeMethods+" methods may be too large",largeMethods,0, getPercentage(numOfMethods,10),getPercentage(numOfMethods,20));
		findErrorColor(myList,sanitizedInputs+" inputs haven't been sanitised", sanitizedInputs,0,10,20);
		findErrorColor(myList,repeatedCode+" instances of code being repeated have been found", repeatedCode,1,10,20);
		findErrorColor(myList,storedReturnedVars+" methods have been run without storing the variable it returns", storedReturnedVars,4,20,30);
		findErrorColor(myList,toPercentage(staticMethods,numOfMethods)+"% of methods are static", staticMethods, getPercentage(numOfMethods,10), getPercentage(numOfMethods,35),getPercentage(numOfMethods,70));
		findErrorColor(myList,toPercentage(staticVariable,numOfVariables)+"% of variables are static", staticVariable, getPercentage(numOfMethods,10), getPercentage(numOfMethods,35),getPercentage(numOfVariables,70));
		findErrorColor(myList,mainMethodLength+" main methods may be too large", mainMethodLength,0, 0,100);
		findErrorColor(myList,ifThenChains+" large if/then chains", ifThenChains,0, 10,20);
		findErrorColor(myList,toPercentage(privacyMethods,numOfMethods)+"% of methods don't have a privacy set", privacyMethods,getPercentage(numOfMethods,3), getPercentage(numOfMethods,15),getPercentage(numOfMethods,30));
		findErrorColor(myList,toPercentage(privacyVariables,numOfVariables)+"% of class variables don't have a privacy set", privacyVariables,getPercentage(numOfMethods,5), getPercentage(numOfVariables,15),getPercentage(numOfVariables,30));
		findErrorColor(myList,toPercentage(publicVariable,numOfVariables)+"% of class variables are public", publicVariable, getPercentage(numOfVariables,15), getPercentage(numOfVariables,35),getPercentage(numOfVariables,70));
		findErrorColor(myList,scannerClosed+" scanners were created but never closed", scannerClosed,0, 0,10);
		findErrorColor(myList,indentation+" occurances found of indentation not being correct", indentation,10, 50,100);
		findErrorColor(myList,instantSemiColon+" occurances found of a opening statements having a semi colon stopping them from functioning", instantSemiColon,0, 10,20);
		findErrorColor(myList,toPercentage(classNameConvenstion,numOfClasses)+"% of class names don't meet naming conventions", classNameConvenstion, getPercentage(numOfClasses,5), getPercentage(numOfClasses,10),getPercentage(numOfClasses,20));
		findErrorColor(myList,toPercentage(methodNameConvenstion,numOfMethods)+"% of method names don't meet naming conventions", methodNameConvenstion, getPercentage(numOfClasses,5), getPercentage(numOfMethods,10),getPercentage(numOfMethods,20));
		findErrorColor(myList,variableNameConvenstion+" variables don't meet naming convention", variableNameConvenstion, 5,15,50);
		findErrorColor(myList,stringComparison+" strings have been compared using <em>==</em> or <em>!=</em> rather than <em>.equals()</em>", stringComparison,0, 0,5);
		findErrorColor(myList,recursionInput+" recursive methods that don't have an input", recursionInput, 0,0,5);
		findErrorColor(myList,spelling+" words haven't been spelt correctly", spelling, 10,50,100);
		findErrorColor(myList,copying/2+" of their own methods are similar", copying/2,0,5,10);
		findErrorColor(myList,cheating/2+" methods are similar to other inputs", cheating/2,0,5,10);
		printFoundWords(myList);
		myList.add("\n");
	}
	
	/**
	 * Prints if words searched for appear or not
	 * @param myList the list to add the words to
	 */
	public void printFoundWords(List<String> myList) {
		if(AdvOptions.searchTerms !=null) {
			if(!AdvOptions.searchTerms.isEmpty()) {
				int numOfSearchTerms = AdvOptions.searchTerms.size();
				int numOfFoundWords = wordsFound.size();
				for(int i = 0;i<numOfSearchTerms;i++) {
					String curWord = AdvOptions.searchTerms.get(i);
					int occurances = 0;
					for(int counter = 0;counter<numOfFoundWords;counter++) {
						if(wordsFound.get(counter).equals(curWord)) {
							occurances++;
						}
					}
					if(occurances==0) {
						myList.add(writeError("<em>"+curWord+"</em> never appears",CodeCheckerConstants.htmlRedText));
					}else {
						myList.add(writeError("<em>"+curWord+"</em> occurs "+ occurances+" times",CodeCheckerConstants.htmlGreenText));
					}
				}
			}
		}
	}
	/**
	 * Finds out what colour the error should be based on severity and prints to the list
	 * @param myList the list to print to
	 * @param errorText the text that displays upon error being there
	 * @param curValue the occurrences of this error
	 * @param threshold the threshold for occurrences to not appear as a problem
	 * @param orangeMin the threshold for occurrences to appear orange
	 * @param redMin the threshold for occurrences to appear red
	 */
	public void findErrorColor(List<String> myList, String errorText, int curValue, int threshold, int orangeMin, int redMin) {
		if(curValue == 0) {
			return;
		}
		if(curValue<threshold) {
			myList.add(writeError(errorText,CodeCheckerConstants.htmlNormalText));
		}
		else if(curValue<orangeMin) {
			myList.add(writeError(errorText,CodeCheckerConstants.htmlYellowText));
		}
		else if(curValue<redMin){
			myList.add(writeError(errorText,CodeCheckerConstants.htmlOrangeText));
		}
		else {
			myList.add(writeError(errorText,CodeCheckerConstants.htmlRedText));
		}
	}
	/**
	 * Gets the string that makes up the error
	 * @param theError the error
	 * @param errorColor the colour it should be
	 * @return the string to be added to the list
	 */
	public String writeError(String theError,String errorColor) {
		return "\n-"+errorColor+theError+CodeCheckerConstants.htmlColorClose;
	}
	/**
	 * gets the percentage 
	 * @param value the value needing the percentage of
	 * @param total the total 
	 * @return the percentage
	 */
	public static int toPercentage(int value,int total) {
		if(total!=0) {
			return (value*100)/total;
		}
		return 0;
	}
	/**
	 * Gets a percentage of an integer
	 * @param value the value to get a percentage of
	 * @param percentage the percentage to get
	 * @return the value
	 */
	public static int getPercentage(int value,int percentage) {
		return (value*percentage)/100;
	}
	/**
	 * Checks to see if any errors have been found
	 * Based off of https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection
	 * @return true if errors have been found else false
	 */
	public boolean checkIfErrors() {
		//For all fields in this class
		for (Field field : this.getClass().getDeclaredFields()) {
			//Get the value of the field
		    Object value = null;
			try {
				value = field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		    if (value != null) {
		    	//If the value is greater than 0 then errors have been found
		    	try {
		    		if(Integer.valueOf(value.toString())>0) {
			    		return true;
			    	}
		    	}catch(NumberFormatException e) {
		    		continue;
		    	}
		    	
		    }
		}
		return false;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getNoComments() {
		return noComments;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementNoComments() {
		this.noComments++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getJavadocComments() {
		return javadocComments;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementJavadocComments() {
		this.javadocComments++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getLargeMethods() {
		return largeMethods;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementLargeMethods() {
		this.largeMethods++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getSanitizedInputs() {
		return sanitizedInputs;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementSanitizedInputs() {
		this.sanitizedInputs++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getRepeatedCode() {
		return repeatedCode;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementRepeatedCode() {
		this.repeatedCode++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getStoredReturnedVars() {
		return storedReturnedVars;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementStoredReturnedVars() {
		this.storedReturnedVars++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getStaticMethods() {
		return staticMethods;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementStaticMethods() {
		this.staticMethods++;
	}
	/**
	 * basic setter
	 * @param temp the num to set static methods to
	 */
	public void setStaticMethods(int temp) {
		this.staticMethods=temp;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getStaticVariable() {
		return staticVariable;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementStaticVariable() {
		this.staticVariable++;
	}
	/**
	 * basic setter
	 * @param temp the num to set static vars to
	 */
	public void setStaticVariable(int temp) {
		this.staticVariable=temp;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getMainMethodLength() {
		return mainMethodLength;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementMainMethodLength() {
		this.mainMethodLength++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getIfThenChains() {
		return ifThenChains;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementIfThenChains() {
		this.ifThenChains++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getPrivacyMethods() {
		return privacyMethods;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementPrivacyMethods() {
		this.privacyMethods++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getPrivacyVariables() {
		return privacyVariables;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementPrivacyVariables() {
		this.privacyVariables++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getPublicVariable() {
		return publicVariable;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementPublicVariable() {
		this.publicVariable++;
	}
	/**
	 * basic setter
	 * @param temp the num to set public vars to
	 */
	public void setPublicVariable(int temp) {
		this.publicVariable=temp;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getScannerClosed() {
		return scannerClosed;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementScannerClosed() {
		this.scannerClosed++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getIndentation() {
		return indentation;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementIndentation() {
		this.indentation++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getInstantSemiColon() {
		return instantSemiColon;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementInstantSemiColon() {
		this.instantSemiColon++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getClassNameConvenstion() {
		return classNameConvenstion;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementClassNameConvenstion() {
		this.classNameConvenstion++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getMethodNameConvenstion() {
		return methodNameConvenstion;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementMethodNameConvenstion() {
		this.methodNameConvenstion++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getVariableNameConvenstion() {
		return variableNameConvenstion;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementVariableNameConvenstion() {
		this.variableNameConvenstion++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getStringComparison() {
		return stringComparison;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementStringComparison() {
		this.stringComparison++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getRecursionInput() {
		return recursionInput;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementRecursionInput() {
		this.recursionInput++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getSpelling() {
		return spelling;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementSpelling() {
		this.spelling++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getCopying() {
		return copying;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementCopying() {
		this.copying++;
	}
	/**
	 * basic getter
	 * @return num of this error
	 */
	public int getCheating() {
		return cheating;
	}
	/**
	 * increments the value of this error by 1
	 */
	public void incrementCheating() {
		this.cheating++;
	}
	/**
	 * basic adder
	 * @param theWord the word to add to the list
	 */
	public void addWordFound(String theWord) {
		wordsFound.add(theWord);
	}
	/**
	 * Getting the list of words
	 * @return list of words
	 */
	public List<String> getWords(){
		return wordsFound;
	}
}
