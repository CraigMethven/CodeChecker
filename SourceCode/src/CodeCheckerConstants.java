/**
 * static final class for constants needed
 * @author Craig
 *
 */
public final class CodeCheckerConstants {
	//arrays of strings needed for constants
	public static final String[] variableTypes = {"int","String","bool","boolean","float","void","double","char","long"};
	public static final String[] multilineTypes = {"if","for","else","while","class","switch","case","do"};
	public static final String[] privacyTypes = {"private","public","protected"};
	//Variables needed for html styling
	//&#09; for tab
	public static final String htmlTabCharacter = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	public static final String htmlOpenBracketCharacter = "&lt;";
	public static final String htmlCloseBracketCharacter = "&gt;";
	//For colouring text
	public static final String htmlColorClose = "</span>";
	public static String htmlRedText = "<span style=\"color: #FF4B4B\">";
	public static String htmlOrangeText = "<span style=\"color: #FF7404\">";
	public static String htmlYellowText = "<span style=\"color: #FFBE28\">";
	public static final String htmlNormalText = "<span style=\"\">";
	public static String htmlGreenText = "<span style=\"color: #00cc00\">";
	public static final String htmlFolderColor= "<span style=\"color: #de9d00\">";
	public static String htmlFileColor= "<span style=\"color: #39EBDA\">";
	public static final String htmlClassColor= "<span style=\"color: #D16AD4\">";
	public static String htmlMethodColor= "<span style=\"color: #8DC0F3\">";
}