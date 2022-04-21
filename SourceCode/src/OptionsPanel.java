import java.awt.Font;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

/**
 * The panel for the options tab in the sidebar
 * @author Craig
 *
 */
public class OptionsPanel {
	private Window f;
	private JTextArea backgroundPanel;
	private JScrollPane myPanel;
	private List<OptionPanelItems> myItems;
	
	/**
	 * basic constructor
	 * @param temp the window that this panel is to be displayed on
	 */
	public OptionsPanel(Window temp){
		f = temp;
		init();
	}
	
	/**
	 * initialises all of the components
	 */
	public void init(){
		//The back panel
		backgroundPanel = new JTextArea();
		backgroundPanel.setBackground(Window.backColor);
		backgroundPanel.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		backgroundPanel.setForeground(Window.textColor);
		//backgroundPanel.setBorder(null);
		backgroundPanel.setEditable(false);
		backgroundPanel.setLayout(null);
		backgroundPanel.setHighlighter(null);
		//The scroll pane
		myPanel = f.getNewScrollPane(backgroundPanel);
		backgroundPanel.setAutoscrolls(true);
		myPanel.setViewportView(backgroundPanel);
		
		//For each boolean in the options menu create an option panel item
		java.lang.reflect.Field[] myOptions = Options.class.getFields();
		myItems = new Vector<OptionPanelItems>();
		int arraySize = myOptions.length-1;
		//System.out.println(arraySize);
		//Create each item
		for(int i =0;i<arraySize;i++) {
			myItems.add(new OptionPanelItems(this, myOptions[i],Options.checkDescriptions[i]));
		}
		setVisibility(false);
	}
	
	/**
	 * sets the size of the scroll pane to allow the user to see all of the checkboxes
	 */
	public void setScrollArea() {
		int numOfLines = (getHeight()/myItems.get(0).getFontSize())-(myItems.size()*2/3);
		
		//Sets the font to the same as the checkboxes
		backgroundPanel.setFont(new Font(Window.bodyFont.getFamily(), Font.ITALIC,myItems.get(0).getFontSize()));
		//Take a new line for the height of the checkboxes
		String tempNewLine="    Select the checks that\n you want run.\n"
				+ "    If changed rerun the\n checks using the button\n at the bottom.";
		for(int i =0;i<numOfLines;i++) {
			tempNewLine+="\n";
		}
		//Set the text to the multiple line breaks
		backgroundPanel.setText(tempNewLine);
	}
	
	/**
	 * Resizes all of the elements in the options panel
	 * @param sidebarWidth the width of the sidebar of the window
	 * @param topBottomBarSize the height of the tab selection at the top of the screen
	 * @param screenHeight the height of the screen
	 * @param buffer the distance between items
	 */
	public void resize(int sidebarWidth, int topBottomBarSize, int screenHeight, int buffer) {
		myPanel.setBounds(buffer, topBottomBarSize+buffer*2,sidebarWidth-buffer*2,screenHeight-(topBottomBarSize*2+buffer*4));
		backgroundPanel.setBounds(0, 0,sidebarWidth-buffer*2,screenHeight-(topBottomBarSize*2+buffer*4));
		//backgroundPanel.setBounds(buffer, topBottomBarSize+buffer*2,sidebarWidth-buffer*2,screenHeight*20);
		int height = buffer*19/2;
		int numOfOptions = myItems.size();
		//Resize all of the option panel items
		for(int i =0;i<numOfOptions;i++) {
			myItems.get(i).resize(height, buffer, sidebarWidth);
			height += myItems.get(i).getHeight();
		}
		//Set the new height of the scroll pane
		setScrollArea();
		backgroundPanel.setCaretPosition(0);
	}
	
	/**
	 * sets the visibility of this panel
	 * @param visible if it should be visible
	 */
	public void setVisibility(boolean visible) {
		myPanel.setVisible(visible);
		backgroundPanel.setVisible(visible);
		//Scroll to the top of the scroll pane
		if(visible) {
			backgroundPanel.setCaretPosition(0);
		}
	}
	
	/**
	 * Add a component to this option panel
	 * @param temp the component to add
	 */
	public void addComponant(JComponent temp) {
		backgroundPanel.add(temp);
	}
	
	/**
	 * gets the total height of all of the items
	 * @return the height
	 */
	public int getHeight() {
		int numberOfItems = myItems.size();
		int myHeight = 0;
		//for each item add its height
		for(int i =0;i<numberOfItems;i++) {
			myHeight += myItems.get(i).getHeight();
		}
		return myHeight;
	}
	
	/**
	 * Gets the total number of lines contained within each item added together
	 * @return the number of lines
	 */
	public int getLines() {
		int checkLength = myItems.size();
		int numOfLines=0;
		//for each item add its number of lines
		for(int i =0;i<checkLength;i++) {
			numOfLines += myItems.get(i).getLines();
		}
		return numOfLines;
	}
	
	/**
	 * sets the check button to be highlighted or not
	 * @param highlighted if it should be highlighted
	 */
	public void setSidebarCheckButtonHighlight(boolean highlighted) {
		f.setSidebarCheckButtonHighlight(highlighted);
	}
}
