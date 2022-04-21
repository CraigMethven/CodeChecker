/**
 * basic class to store variables
 * @author Craig
 *
 */
public class VariableStore {
	protected String type;
	protected String name;
	private boolean isStatic = false;
	private boolean isFinal = false;
	private String privacy = "private";
	
	/**
	 * Constructor taking in the type and the name of the variable
	 * @param myType The variable type
	 * @param myName The variables name
	 */
	public VariableStore(String myType,String myName){
		type = myType;
		name = myName;
	}
	
	/**
	 * Basic constructor
	 * @param myType the type
	 * @param myName the name
	 * @param staticStatus if static
	 * @param finalStatus if final
	 * @param privacyStatus the privacy
	 */
	public VariableStore(String myType,String myName, boolean staticStatus, boolean finalStatus, String privacyStatus){
		type = myType;
		name = myName;
		isStatic=staticStatus;
		isFinal=finalStatus;
		privacy=privacyStatus;
		//System.out.println("Name: "+name+";\tType:"+type+ ";\tPrivacy: "+privacy+";\tStatic: "+isStatic+";\tFinal: "+isFinal);
	}
	
	/**
	 * Prints the variable
	 */
	public void print() {
		System.out.println("Name: "+name+";\tType:"+type+ ";\tPrivacy: "+privacy+";\tStatic: "+isStatic+";\tFinal: "+isFinal);
	}
	
	/**
	 * Basic setter
	 * @param tempType the type
	 */
	public void setType(String tempType) {
		type = tempType;
	}
	/**
	 * Basic setter
	 * @param tempBool if static
	 */
	public void setStatic(boolean tempBool) {
		isStatic = tempBool;
	}
	/**
	 * Basic setter
	 * @param tempBool if final
	 */
	public void setFinal(boolean tempBool) {
		isFinal = tempBool;
	}
	/**
	 * Basic setter
	 * @param newPrivacy the privacy
	 */
	public void setPrivacy(String newPrivacy) {
		privacy = newPrivacy;
	}
	
	/**
	 * basic getter
	 * @return the type of the variable
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * basic getter
	 * @return the name of the variable
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns if variable is static
	 * @return true if static
	 */
	public boolean getIfStatic() {
		return isStatic;
	}
	
	/**
	 * gets the privacy of the variable
	 * @return privacy
	 */
	public String getPrivacy() {
		return privacy;
	}
}
