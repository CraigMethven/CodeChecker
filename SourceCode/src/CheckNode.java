import java.awt.Container;
import java.util.List;

import javax.swing.JCheckBox;

/**
 * Class to store a checkbox to be displayed beside a tree node, to store if that tree node has been selected or not
 * @author Craig
 *
 */
public class CheckNode {
	private Container parent;
	private JCheckBox myBox;
	private List<String> nodePath;
	private boolean isFile;
	public static int boxSize = 15;
	
	/**
	 * Constructor to initialise the elements
	 * @param inside the container to display the checkbox on
	 * @param myPath the ID of the node, made of a file path to the file the node represents
	 */
	public CheckNode(Container inside, List<String> myPath){
		nodePath = myPath;
		//Check if the input is a file from the last item in the list
		isFile = myPath.get(myPath.size()-1).contains(".");
		myBox = new JCheckBox();
		parent = inside;
		//Create the box
		parent.add(myBox);
		myBox.setBackground(Window.backColor);
		//Main.printList(nodePath);
	}
	
	/**
	 * returns true if the box is ticked
	 * @return if the box is ticked
	 */
	public boolean getChecked() {
		return myBox.isSelected();
	}
	
	/**
	 * reverses the checkbox's tick
	 */
	public void flipChecked() {
		myBox.setSelected(!myBox.isSelected());
	}
	
	/**
	 * gets the ID of the node
	 * @return the node ID (filepath)
	 */
	public List<String> getID() {
		return nodePath;
	}
	
	/**
	 * set location of the box
	 * @param x the starting position on the x axis
	 * @param y the starting position on the y axis
	 */
	public void setBoxBounds(int x, int y) {
		myBox.setBounds(x, y, boxSize, boxSize);
		myBox.setVisible(true);
	}
	
	/**
	 * see if the list inputted matches the ID of this node
	 * @param input the list to check against
	 * @return true if they match
	 */
	public boolean compareID(List<String> input) {
		return input.equals(nodePath);
	}
	
	/**
	 * gets the file/folder name of the file/folder this checknode represents
	 * @return the file/folder name
	 */
	public String getNodeText() {
		return nodePath.get(nodePath.size()-1);
	}
	
	/**
	 * prints the node ID
	 */
	public void print() {
		Main.printList(nodePath);
	}
	
	/**
	 * sets if the node should be shown
	 * @param visible if it should be shown
	 */
	public void setVisible(boolean visible) {
		myBox.setVisible(visible);
	}
	
	/**
	 * checks if the node represents a file or folder
	 * @return true if this node stores a file, false if folder
	 */
	public boolean isFile() {
		return isFile;
	}
}
