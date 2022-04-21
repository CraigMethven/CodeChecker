import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Stores a checkbox for a single check that can be run
 * @author Craig
 *
 */
public class OptionPanelItems {
	private JCheckBox myBox;
	//Stores the number of lines the text in this box take up
	private int lines = 1;
	private OptionsPanel parent;
	//A pointer to the variable this box toggles upon click
	private java.lang.reflect.Field variablePointer;
	private String description;
	
	/**
	 * Basic constructor
	 * @param tempParent the options panel the items are to be displayed on
	 * @param tempPointer pointer to the field this box is to represent
	 * @param tempD the description of what this check does
	 */
	OptionPanelItems(OptionsPanel tempParent,java.lang.reflect.Field tempPointer, String tempD){
		parent = tempParent;
		variablePointer = tempPointer;
		description = tempD;
		init();
	}
	
	/**
	 * Initialises the components
	 */
	public void init() {
		//For the checkbox
		myBox = new JCheckBox();
		myBox.setSelected(getFieldValue());
		myBox.setBackground(null);
		myBox.setForeground(Window.textColor);
		myBox.setText(variablePointer.getName());
		myBox.setBorder(BorderFactory.createRaisedBevelBorder());
		myBox.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		
		//Sets the click operation of toggling the variable upon click
		myBox.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        setFieldValue(!getFieldValue());
		        parent.setSidebarCheckButtonHighlight(true);
		        //System.out.println(myBox.getText()+ " "+getFieldValue());
		    }
		});
		
		setLabel();
		//Sets the hover tooltip to have the description of what the check does
		myBox.setToolTipText("<html><body style = \"font-size: 11px;\"><b>"+myBox.getText()+": </b><br>"+description);
		parent.addComponant(myBox);
	}
	
	/**
	 * resizes the box
	 * @param myHeight the height of the box
	 * @param buffer the space between elements
	 * @param sidebarWidth the width of the sidebar the elements are a part of
	 */
	public void resize(int myHeight, int buffer, int sidebarWidth) {
		myBox.setBounds(1,myHeight,sidebarWidth-buffer*4,buffer*11/5*lines+buffer);
		//myLabel.setBounds(buffer*5/2,myHeight,sidebarWidth-buffer*3,buffer*3);
		//Set font to fit in the size of box
		myBox.setFont(new Font(Window.bodyFont.getFamily(), Font.PLAIN,buffer*7/5));
	}
	
	/**
	 * Sets the text on the checkbox
	 */
	public void setLabel() {
		char[] labelChar = myBox.getText().toCharArray();
		String completeLine = "";
		int lineLength = labelChar.length;
		char myChar;
		int nextLineNum = (int) myBox.getWidth()/myBox.getFont().getSize();
		
		//Add html tags and make the first letter a captal
		completeLine += "<html><body>"+ Character.toUpperCase(labelChar[0]);
		//For every character
		//take a space before every captial letter
		for(int i = 1;i<lineLength;i++) {
			myChar=labelChar[i];
			if(Character.isUpperCase(myChar)) {
				completeLine += ' ';
			}
			completeLine+=myChar;
		}
		//CHANGE TO BE SOMETHING MORE RELIABLE. NEEDED FOR SCROLLBAR TO BE CORRECT.
		if(lineLength>nextLineNum) {
			lines++;
		}
		//System.out.println(lines+ " "+completeLine);
		myBox.setText(completeLine);
	}
	
	/**
	 * Gets the current toggled state of the variable this box represents
	 * @return if the variable is true or false
	 */
	public boolean getFieldValue() {
		//Need an instance of the options class even though we're only reading static variables
		Options tempClass = new Options();
		try {
			return variablePointer.getBoolean(tempClass);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Sets the current toggled state of the variable this box represents
	 * @param setVariable if it should be true or false
	 */
	public void setFieldValue(boolean setVariable) {
		//Need an instance of the options class even though we're only changing static variables
		Options tempClass = new Options();
		try {
			variablePointer.setBoolean(tempClass,setVariable);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * basic getter
	 * @return number of lines the text in the box takes up
	 */
	public int getLines() {
		return lines;
	}
	
	/**
	 * basic getter
	 * @return the height of the box
	 */
	public int getHeight() {
		return myBox.getHeight();
	}
	
	/**
	 * basic getter
	 * @return the font size
	 */
	public int getFontSize() {
		return myBox.getFont().getSize();
	}
}
