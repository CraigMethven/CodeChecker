import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

// Class used for the right click menus. Based on a JPopupMenu shown at:
// https://stackoverflow.com/questions/766956/how-do-i-create-a-right-click-context-menu-in-java-swing
public class RightClickMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	//The window the right click menu should be displayed on
	private static Window myWindow;
	//The title of the right click menu
	private String title= "Title";
	//The buttons to be added to the menu
	private List<JMenuItem> myItems;
	
	/**
	 * constructor to set everything up
	 * @param e the mouse event that occured
	 */
    public RightClickMenu(MouseEvent e) {
    	Component c = e.getComponent();
    	myItems = new Vector<JMenuItem>();
    	
    	//If no component was clicked then return
    	if(c==null) {
    		return;
    	}
    	
    	//Set the title of the right click menu
    	title = c.getName();

    	//If the component clicked was a JTree
    	if (c instanceof JTree) {
    		JTree tempTree = (JTree) c;
    		//System.out.println(tempTree.getLastSelectedPathComponent().toString());
    		//Get the path of the node clicked
    		TreePath tp = tempTree.getPathForLocation(e.getX(), e.getY());
    		//Get the node clicked
    		MutableTreeNode myNode = (MutableTreeNode) tp.getLastPathComponent();
    		//Set the title to the name of the node
    		title = fileTreePanel.ridTreeNodeOfHTML(myNode.toString());
    		//If a file
    		if(title.contains(".")){
    			//Add an option to display the file clicked
    			JMenuItem display = new JMenuItem("Display File");
				display.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent ev) {
				    	//System.out.println("Display File");
				    	myWindow.displayFileStoreFromNode(tempTree.getName(),(DefaultMutableTreeNode) myNode);
				    }
				});
    			myItems.add(display);
    			//Add an option to remove the file clicked
    			JMenuItem delete = new JMenuItem("<html><Style> body{color: red;}</Style><body>Remove File");
    			delete.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent ev) {
				    	//Get the node ID
				    	List<String> parentList = new Vector<String>();
				    	parentList.add(tempTree.getName());
				    	fileTreePanel.getParentList((DefaultMutableTreeNode) myNode, parentList);
				    	//Delete node by ID
				    	myWindow.deleteFile(parentList);
				    	//Remove the node from the tree if not a root
				    	if(parentList.size()!=1) {
					    	fileTreePanel.removeChildNode(tempTree, myNode);
				    	}
				    	//if node is a root then delete the tree
				    	else {
				    		fileTreePanel.removeRootNode(tempTree);
				    		myWindow.deleteTree(tempTree);
				    	}
				    }
				});
    			myItems.add(delete);
    		}
    		//For folders
    		else {
    			//Add title
    			JMenuItem display = new JMenuItem("Display Folder");
    			//Add display button
				display.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent ev) {
				    	myWindow.displayFolderStoreFromNode(tempTree.getName(),(DefaultMutableTreeNode) myNode);
				    }
				});
    			myItems.add(display);
    			//Add delete button
    			JMenuItem delete = new JMenuItem("<html><Style> body{color: red;}</Style><body>Remove Folder");
    			delete.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent ev) {
				    	List<String> parentList = new Vector<String>();
				    	parentList.add(tempTree.getName());
				    	fileTreePanel.getParentList((DefaultMutableTreeNode) myNode, parentList);
				    	myWindow.deleteFolder(parentList);
				    	if(parentList.size()!=1) {
					    	fileTreePanel.removeChildNode(tempTree, myNode);
				    	}
				    	else {
				    		fileTreePanel.removeRootNode(tempTree);
				    		myWindow.deleteTree(tempTree);
				    	}
				    }
				});
    			myItems.add(delete);
    		}
    		
    		//System.out.println(tp.getLastPathComponent());
    		
            // do something
    	}
    	
    	//Add title
        add(new JMenuItem("<html><body><b>"+title));
        addSeparator();
        
        //Add items to menu
        int numOfItems = myItems.size();
        for(int i =0;i<numOfItems;i++) {
        	add(myItems.get(i));
        }
    }
    
    /**
     * sets the window that all right click menus should be on
     * @param w the window
     */
    public static void setWindow(Window w) {
    	myWindow = w;
    }
}

/**
 * Small class to keep track of when a right click occurs on screen. This can be added to most components listeners
 * @author Craig
 *
 */
class rightClickListener extends MouseAdapter {
	/**
	 * For other OS's that have alternatives to right click
	 * @param e needed for checking mouse event
	 */
	public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
        	displayMenu(e);
        }
    }
	
	/**
	 * If mouse was released within the container, check if popup trigger and if so display right click menu
	 * @param e needed for checking mouse event
	 */
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
        	displayMenu(e);
        }
    }

    /**
     * Display right click menu by the mouse
     * @param e the mouse event that activated this
     */
    private void displayMenu(MouseEvent e) {
    	RightClickMenu menu = new RightClickMenu(e);
        menu.show(e.getComponent(), e.getX()+8, e.getY());
    }
}
