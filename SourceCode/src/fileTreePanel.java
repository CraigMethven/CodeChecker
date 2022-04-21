import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Class to display the file tree panel on the sidebar of the Window
 * @author Craig Methven
 *
 */
public class fileTreePanel {
	private Window f;
	//Text area to set the size of the scroll pane 
	private JTextArea backgroundPanel;
	private JScrollPane myPanel;
	private List<JTree> treeList;
	private JButton clearBtn;
	private JButton upload;
	private Image uploadImage;
	private JLabel uploadLabel;
	//Variable to keep track of the length of the text to set the size of the horizontal scroll bar accurately
	public static int maxTextLength = 0;
	
	/**
	 * Basic constructor 
	 * @param temp Window the panel is a part of
	 */
	public fileTreePanel(Window temp){
		f = temp;
		init();
	}
	
	/**
	 * Setting up the class upon an instance being created
	 */
	public void init(){
		//For uploading files to the program
		upload = f.getNewButton();
		try {
			//Read in the icon used
			BufferedImage b;
			//b = Window.toBufferedImage(Main.getImage("/images/upload.png"));
			b = Main.getBufferedImage("/upload.png");
			//b = Window.toBufferedImage(ImageIO.read(new File(System.getProperty("java.class.path")+"/images/upload.png")));
			//Color the icon to the colour wanted and set it to the correct button
			Window.colourIcon(b, Window.secondaryColor.getRGB());
			ImageIcon tempII = new ImageIcon((Image) b,"An upload icon");
			uploadImage = b;
			upload.setIcon(tempII);
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			System.out.println("Problem reading upload image");
		}
		//Setting up the upload button
		upload.setBackground(Window.backColor);
		//Listener to pull up a file directory upon button pressed and saving the file correctly
		upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//Bring up file chooser
                JFileChooser fileSystem = new JFileChooser();
                fileSystem.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                //Upon file being selected save it
                if (fileSystem.showOpenDialog(f.getFrame()) == JFileChooser.APPROVE_OPTION) {
                	uploadFile(fileSystem.getSelectedFile());
                }
            }
        });
		//Change upload button colours upon hover
		upload.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	upload.setBackground(Window.otherBackColor);
		    	upload.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	upload.setBackground(Window.backColor);
		    	upload.setBorder(null);
		    }
		});
		//Basic label that preforms the same actions as the button
		uploadLabel = f.getNewLabel("Upload Files and Folders");
		//Same as the button
		uploadLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JFileChooser fileSystem = new JFileChooser();
                fileSystem.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (fileSystem.showOpenDialog(f.getFrame()) == JFileChooser.APPROVE_OPTION) {
                    uploadFile(fileSystem.getSelectedFile());
                }
            }
        });
		
		//Clear button to clear the file tree of uploaded items
		clearBtn = f.getNewButton();
		clearBtn.setText("Clear");
		clearBtn.setForeground(Window.textColor);
		//Action to clear the file trees
		clearBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	int input = JOptionPane.showConfirmDialog(null, "Are you sure that you want to clear all imported files?\nConsider downloading the results before hand","Clear files",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            	//If user selected yes
            	if(input==0) {
            		int numOfTrees = treeList.size();
            		//Hides the trees and removes all references to them
            		for(int i =0;i<numOfTrees;i++) {
            			treeList.get(i).setVisible(false);
            			backgroundPanel.remove(treeList.get(i));
            		}
            		treeList = new Vector<JTree>();
            		//resize the tree to reflect that it's now empty
            		resizeTree(f.getBuffer());
            		//Set upload text
            		backPanelInitialiseText();
            		//Delete all the stored files
            		f.deleteAll();
            	}
            }
            //Change colour of the button upon hover
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	clearBtn.setBorder(BorderFactory.createLineBorder(Color.red));
            	clearBtn.setForeground(Color.red);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	clearBtn.setBorder(null);
            	clearBtn.setForeground(Window.textColor);
		    }
        });
		
		//Create tree list
		treeList = new Vector<JTree>();
		//Initialise panel and scroll pane
		initBackgroundPanel();
		myPanel = f.getNewScrollPane(backgroundPanel);
		
		//Populate the tree with the uploads that were previously stored upon programme being closed
		getOldUploads();
	}
	
	/**
	 * Sets up the background panel
	 */
	private void initBackgroundPanel() {
		//Set up the text area. This is used for the scroll bars to work properly
		backgroundPanel = f.getNewTextArea();
		backgroundPanel.setBackground(Window.backColor);
		backgroundPanel.setForeground(Window.textColor);
		backgroundPanel.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		backgroundPanel.setLayout(null);
		backgroundPanel.setEditable(false);
		backgroundPanel.setHighlighter(null);
		backPanelInitialiseText();
		//Drag and drop feature editted from: 
		//https://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path
		backgroundPanel.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;
		    public synchronized void drop(DropTargetDropEvent evt) {
		        try {
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            @SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            for (File fileDragged : droppedFiles) {
		                // process files
		            	uploadFile(fileDragged);
		            }
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});
		//Add appropriate elements
		backgroundPanel.add(upload);
		backgroundPanel.add(uploadLabel);
	}
	
	/**
	 * Class to set the font and text of the backgroundPanel when there are no elements in the file tree
	 */
	public void backPanelInitialiseText() {
		backgroundPanel.setFont(new Font(Window.titleFont.getFamily(),Font.BOLD,f.getFrame().getHeight()/16));
		backgroundPanel.setForeground(new Color((int) Window.backColor.getRed()*4/5,(int) Window.backColor.getGreen()*8/10,(int) Window.backColor.getBlue()*8/10));
		backgroundPanel.setText("\nDrag\nand\ndrop\nfiles\nor\nfolders\nhere");
		clearBtn.setVisible(false);
	}
	
	/**
	 * Called upon files being uploaded this saves them to the appropriate places and displays them on the file tree
	 * @param tempFile the file being uploaded
	 */
	public void uploadFile(File tempFile) {
		FolderStore tempf = new FolderStore(tempFile);
		//Check if the folder contains checkable files
		if(tempf.getDirAndFileNum()!=0) {
			//Store the file store
			f.addFile(tempf);
			//Add the new tree from the uploaded file
			backgroundPanel.setText("");
			backgroundPanel.setForeground(Window.backColor);
			clearBtn.setVisible(true);
			addTree(tempFile);
		}
		else {
			JOptionPane.showMessageDialog(null, "This is not a checkable file or there are no checkable files in this folder");;
		}
		//tempf.printAllFolderStores();
	}
	
	/**
	 * Sets the visibility of the file tree side bar
	 * @param visible if it should be visible or not
	 */
	public void setVisibility(boolean visible) {
		myPanel.setVisible(visible);
		backgroundPanel.setVisible(visible);
		clearBtn.setVisible(visible);
		//If visible scroll the scrollpane to the top and set the clear button to only appear if there are trees
		if(visible) {
			backgroundPanel.setCaretPosition(0);
			if(!backgroundPanel.getText().strip().equals("")) {
				clearBtn.setVisible(false);
			}
		}
	}
	
	/**
	 * Resize every element in the file tree
	 * @param sidebarWidth The width of the sidebar element in the window
	 * @param topBottomBarSize the height of the tab selection buttons
	 * @param screenHeight the height of the window
	 * @param buffer the space used between elements
	 */
	public void resize(int sidebarWidth, int topBottomBarSize, int screenHeight, int buffer) {
		//If background panel contains text set the font 
		if(!backgroundPanel.getText().strip().equals("")) {
			backgroundPanel.setFont(new Font(Window.titleFont.getFamily(),Font.BOLD,screenHeight/16));
		}
		//Resize each element
		myPanel.setBounds(buffer, topBottomBarSize+buffer*2,sidebarWidth-buffer*2,screenHeight-(topBottomBarSize*2+buffer*4));
		backgroundPanel.setBounds(0, 0,sidebarWidth-buffer*2,screenHeight-(topBottomBarSize*2+buffer*4));
		resizeUpload(sidebarWidth, topBottomBarSize, screenHeight, buffer);
		resizeTree(buffer);
		clearBtn.setFont(new Font(Window.bodyFont.getFontName(), Font.BOLD, buffer));
		clearBtn.setBounds((sidebarWidth-buffer*2)*3/4, topBottomBarSize+buffer/2+1, (sidebarWidth-buffer*2)/4, buffer*3/2);
	}
	
	/**
	 * Resize the upload button and label
	 * @param sidebarWidth The width of the sidebar element in the window
	 * @param topBottomBarSize the height of the tab selection buttons
	 * @param screenHeight the height of the window
	 * @param buffer the space used between elements
	 */
	public void resizeUpload(int sidebarWidth, int topBottomBarSize, int screenHeight, int buffer) {
		//System.out.println("buffer: "+buffer);
		upload.setBounds(buffer/2,buffer/2,buffer*2,buffer*2);
		if(uploadImage != null) {
			//Resize the image on the button
			ImageIcon tempII = new ImageIcon(uploadImage.getScaledInstance(buffer*2,buffer*2,  java.awt.Image.SCALE_SMOOTH));
			upload.setIcon(tempII);
		}
		
		uploadLabel.setBounds(buffer*3-buffer/3, buffer/2, sidebarWidth-buffer*5, buffer*2);
		uploadLabel.setFont(new Font(Window.bodyFont.getFamily(), Font.PLAIN, buffer*9/7));
	}
	
	/**
	 * Resize the tree to correct dimensions and set the scrollpane to match
	 * @param buffer the distance between elements
	 */
	public void resizeTree(int buffer) {
		//If back panel is blank (AKA something has been uploaded)
		if(backgroundPanel.getText().strip().compareTo("")==0) {
			String backgroundPanelText = "";
			//Add spaces to set the width of the horizontal scroll bar
			for(int i =0;i<maxTextLength;i++) {
				backgroundPanelText+="    ";
			}
			//System.out.println(maxTextLength);
			int numOfLines = totalTreeCellCount()*4/3;
			//Take a new line to set the height of the verticle scroll bar
			for(int i = 0;i<numOfLines;i++) {
				backgroundPanelText+="\n";
			}
			//backgroundPanelText+="\nRight click folders to display them";
			
			backgroundPanel.setText(backgroundPanelText);
			int numOfTrees = treeList.size();
			int treeYStart = buffer*3;
			int treeHeight;
			//Set the dimensions of each tree
			for(int i = 0;i<numOfTrees;i++) {
				JTree myTree = treeList.get(i);
				//If tree contains nodes
				if(myTree.getModel().getRoot()!=null) {
					//Get the height of the tree
					treeHeight = (int) ((countTreeCells(myTree))*myTree.getRowBounds(0).getHeight());
					//System.out.println(countTreeCells(myTree));
					myTree.setBounds(1, treeYStart, backgroundPanel.getWidth()-3,treeHeight);
					//System.out.println(treeHeight);
					treeYStart += treeHeight;
				}
			}
			//Could be used to scroll the user to the top upon folders being expanded or collapsed
			//backgroundPanel.setCaretPosition(0);
		}
	}
	
	/**
	 * Method for starting the tree
	 * @param myFile The top level of the file tree
	 */
	public void addTree(File myFile) {
		//Change background panel font size
		backgroundPanel.setFont(new Font(Window.bodyFont.getFamily(),Font.PLAIN,(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()/60));
		//Create starting node
		DefaultMutableTreeNode n = new DefaultMutableTreeNode(getHTMLStart()+myFile.getName());
		//Remove default node background
		UIManager.put("Tree.rendererFillBackground", false);
		//Create my tree
		JTree myTree = new JTree(n);
		//For coloring selected item
		DefaultTreeCellRenderer renderer =(DefaultTreeCellRenderer) myTree.getCellRenderer();
        renderer.setBorderSelectionColor(Window.secondaryColor);
        myTree.putClientProperty("JTree.lineStyle", "None");
        //Set tree properties
		myTree.setName(myFile.getAbsolutePath());
		myTree.setBackground(Window.backColor);
		myTree.setForeground(Window.textColor);
		backgroundPanel.add(myTree);
		treeList.add(myTree);
		myTree.setVisible(true);
		//If it is a directory then populate the tree
		if(myFile.isDirectory()) {
			if(checkIfRealFolder(myFile)) {
				addFolderNode(n,myFile);
			}
		}
		//Listeners for the tree
		//Listener for when a node is clicked
		myTree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e) {
		        DefaultMutableTreeNode node = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();
		        if (node == null) {
		        	return;
		        }
		        //Object nodeInfo = node.getUserObject();
		        treeClick(myTree, node);
		    }
		});
		
		//Listener to set display to a folder upon triple click
		myTree.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() == 3) {
					JTree myTree = (JTree) e.getComponent();
					TreeNode nodeSelected = (TreeNode) myTree.getLastSelectedPathComponent();
					if(nodeSelected.getChildCount()!=0) {
					 displayFolder(myTree, (TreeNode) myTree.getLastSelectedPathComponent());
					}
			    }
			}
		});
		
		//Listener for when the tree gets collapsed or expanded to resize the tree
		TreeExpansionListener treeExpandListener = new TreeExpansionListener() {
			/**
			 * method to run upon tree expansion
			 * @param event needed 
			 */
		      public void treeExpanded(TreeExpansionEvent event) {
		    	  resizeTree(f.getBuffer());
		      }
		      
		      /**
			  * method to run upon tree collapsion
			  * @param event needed 
			  */
		      public void treeCollapsed(TreeExpansionEvent event) {
		    	  resizeTree(f.getBuffer());
		      }
		};

		myTree.addTreeExpansionListener(treeExpandListener);
		myTree.addMouseListener(new rightClickListener());
		
		//Calculate the maxTextLength, used for setting the width of the horizontal scroll bar
		int myTreeTextLength = maxTextLengthIterator(0, 5, n);
		if(myTreeTextLength>maxTextLength) {
			maxTextLength = myTreeTextLength;
		}
		resizeTree(f.getBuffer());
	}
	
	/**
	 * Adds a tree node to an existing tree
	 * @param parent The new nodes parent
	 * @param myFile The file which is being considered for being added to the tree
	 */
	public void addTreeNode(DefaultMutableTreeNode parent, File myFile) {
		DefaultMutableTreeNode n = new DefaultMutableTreeNode(getHTMLStart()+myFile.getName());
		//System.out.println("|"+n.getUserObject().toString()+"|");
		int fileType = getFileType(myFile);
		//If it is a folder
		if(fileType == 0) {
			//Check if the folder has contents that we care about
			if(checkIfRealFolder(myFile)) {
				parent.add(n);
				addFolderNode(n,myFile);
			}
		}
		//If it is a file
		else if(fileType == 1) {
			parent.add(n);
		}
	}
	
	/**
	 * Adds a folder node to an existing tree
	 * @param foldersNode The node that is a folder that needs files added to it
	 * @param myFolder the folder of which the node refers to
	 */
	public void addFolderNode(DefaultMutableTreeNode foldersNode, File myFolder) {
		File[] allContent = myFolder.listFiles();
		int numberOfItems = allContent.length;
		for(int i = 0; i<numberOfItems;i++) {
			if(getFileType(allContent[i])!=2) {
				//Add the files inside the folder
				addTreeNode(foldersNode,allContent[i]);
			}
		}
	}
	
	/**
	 * returns the html that should superseed all of the tree nodes texts
	 * @return the subseeding html string
	 */
	public String getHTMLStart() {
		//Took hex convertion formula from:
		//https://stackoverflow.com/questions/3607858/convert-a-rgb-color-value-to-a-hexadecimal-string
		String textColour = String.format("#%02x%02x%02x", Window.textColor.getRed(), Window.textColor.getGreen(), Window.textColor.getBlue());  
		String backgroundColour = String.format("#%02x%02x%02x", Window.backColor.getRed(), Window.backColor.getGreen(), Window.backColor.getBlue());  
		int textSize = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()/60;
		
		return 	"<html>"
				+ "<style>"
				+ "body {"
				+ "color:"+textColour+";font-size: "+textSize+"px;background-color:"+backgroundColour+";font-family:\""+Window.bodyFont.getFamily()+"\";"
				+ "}"
				+ "</style>"
				+ "<body>";
	}
	
	/**
	 * Method to check the type of a file inputed. Used to see how it should be used in a tree
	 * @param myFile The file you want the type of
	 * @return 0 = folder, 1 = file of type wanted, 2 = not wanted
	 */
	public int getFileType(File myFile) {
		if (myFile.isFile()) {
			String extention = FolderStore.getExtension(myFile).toLowerCase();
			if(extention.equals("java") || extention.equals("cpp") || extention.equals("cs") || extention.equals("c")) {
				return 1;
			}
			else {
				return 2;
			}
		} else if (myFile.isDirectory()) {
			return 0;
		}
		return 2;
	}
	
	/**
	 * Counts the total number of nodes that can be seen in all the trees
	 * @return the number of nodes that can be seen in the screen
	 */
	public int totalTreeCellCount() {
		int numOfTrees = treeList.size();
		int counter = 0;
		//count each trees nodes and add them together
		for(int i =0;i<numOfTrees;i++) {
			counter += countTreeCells(treeList.get(i));
		}
		return counter+1;
	}
	
	/**
	 * return the viewable nodes of a tree
	 * @param countTree the tree that has its nodes counted
	 * @return the number of viewable nodes
	 */
	public int countTreeCells(JTree countTree) {
		return countTree.getRowCount();
	}
	
	/**
	 * Looks though all folders uploaded to see if one matches the one inputed. Used to see if a folder contains anything we want
	 * @param myFile The file to look for
	 * @return real or not
	 */
	public boolean checkIfRealFolder(File myFile) {
		List<FolderStore> allFiles = f.getAllImportedFiles();
		//System.out.println("Looking for "+myFile.getName());
		int numOfImports = allFiles.size();
		for(int i =0;i<numOfImports;i++) {
			//If eligible folder return tree
			if(allFiles.get(i).eligibleFolder(myFile.getAbsolutePath())) {
				//System.out.println("Found: "+myFile.getName());
				return true;
			}
		}
		return false;
	}
	
//	/**
//	 * Method handling when an item is clicked in the file tree
//	 * @param filePath The file path of the item clicked
//	 */
//	public void treeClick(String filePath) {
//		//String ridHTML = filePath.substring(filePath.lastIndexOf('>')+1);
//		//System.out.println(ridHTML);
//		//resizeTree(9);
//	}
	
	/**
	 * Method to find the maximum length of strings used in node names
	 * @param maxSoFar the maximum length found so far (start at 0) 
	 * @param startingPoint Used if the text starts indented to add space to the start
	 * @param myNode the node currently having its length calculated
	 * @return the max length of string used in the tree
	 */
	public int maxTextLengthIterator(int maxSoFar, int startingPoint, TreeNode myNode) {
		int counted;
		int numOfKids = myNode.getChildCount();
		//Find the length of current node
		int myMax=startingPoint + ridTreeNodeOfHTML(myNode.toString()).length();
		//Set to max if needed
		if(myMax<maxSoFar) {
			myMax = maxSoFar;
		}
		//find all childs lengths
		for(int i =0;i<numOfKids;i++) {
			counted = maxTextLengthIterator(maxSoFar,startingPoint+6,myNode.getChildAt(i));
			//If a child has a larger name then save it as max
			if(counted>maxSoFar) {
				myMax = counted;
			}
		}
		return myMax;
	}
	
	/**
	 * Method handling when an item is clicked in the file tree
	 * @param myTree the tree that was clicked on
	 * @param myNode the node that was clicked
	 */
	public void treeClick(JTree myTree, DefaultMutableTreeNode myNode) {
		//System.out.println(myTree.getName()+" "+ridTreeNodeOfHTML(myNode.toString()));
		FileStore tempF = getFileStoreFromNode(f, myTree.getName(),myNode);
		//Display the node clicked
		if(tempF != null) {
			f.displayFileStore(tempF);
		}
	}
	
	/**
	 * Sets a folder to be displayed
	 * @param myTree The tree that the node belongs to
	 * @param myNode the node that represents the folder
	 */
	public void displayFolder(JTree myTree, TreeNode myNode) {
		//Set the folder to be displayed
		f.displayFolderStore(getFolderStoreFromNode(f, myTree.getName(),(DefaultMutableTreeNode) myNode));
		//Hide the buttons that would allow the user to switch to see the code or split screen
		f.setCodeTabButtonVisibility(false);
	}
	
	/**
	 * Gets a FileStore from a node
	 * @param f the window the tree is in
	 * @param treeName the name of the tree that the node is in
	 * @param myNode the node that the file store is gotten from
	 * @return the file store that is represented by the node
	 */
	public static FileStore getFileStoreFromNode(Window f, String treeName, DefaultMutableTreeNode myNode) {
		FileStore tempF = null;
		//Make sure that it is a file by ensuring the name contains a dot
		if(myNode.toString().contains(".")) {
			//get the parent list, adding the name of the tree (the absolute file path of the root)
			List<String> parentList = new Vector<String>();
			parentList.add(treeName);
			getParentList(myNode, parentList);
			//Get the file store using parent list
			tempF = f.getFile(parentList);
		}
		return tempF;
	}
	
	/**
	 * Gets a FolderStore from a node
	 * @param f the window the tree is in
	 * @param treeName the name of the tree that the node is in
	 * @param myNode the node that the folder store is gotten from
	 * @return the folder store that is represented by the node
	 */
	public static FolderStore getFolderStoreFromNode(Window f, String treeName, DefaultMutableTreeNode myNode) {
		FolderStore tempF = null;
		List<String> parentList = new Vector<String>();
		getParentList(myNode, parentList);
		parentList.add(0, treeName);
		tempF = f.getFolder(parentList);
		return tempF;
	}
	
	/**
	 * Get a string list of all the parents of a node
	 * @param myNode THe node that you want parents of
	 * @param myList the list to be populated with the names of the parents
	 */
	public static void getParentList(TreeNode myNode, List<String> myList){
		TreeNode parentNode = myNode.getParent();
		if(parentNode!=null) {
			getParentList(parentNode, myList);
			myList.add(ridTreeNodeOfHTML(((DefaultMutableTreeNode) myNode).getUserObject().toString()));
		}
	}
	
	/**
	 * gets rid of the HTML at the start of a tree node
	 * @param nodeText the text of the node
	 * @return the text without the html
	 */
	public static String ridTreeNodeOfHTML(String nodeText) {
		return nodeText.substring(nodeText.lastIndexOf('>')+1);
	}
	
	/**
	 * removes a child node from its parent
	 * @param myTree the tree that contains the nodes
	 * @param myNode the node being removed
	 */
	public static void removeChildNode(JTree myTree, MutableTreeNode myNode) {
		DefaultTreeModel model = (DefaultTreeModel) myTree.getModel();
    	model.removeNodeFromParent((MutableTreeNode) myNode);
	}
	
	/**
	 * Removes the root node of a tree (basically deleting it)
	 * Code taken from:
	 * https://softwaretestingboard.com/q2a/515/how-do-i-remove-root-node-from-jtree
	 * @param myTree the tree being deleted
	 */
	public static void removeRootNode(JTree myTree) {
		DefaultTreeModel model = (DefaultTreeModel) myTree.getModel();
		//get the root
	    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
	    //remove all subsequent nodes
	    root.removeAllChildren();
	    //Remove the root
	    model.reload();
	    model.setRoot(null); 
	}
	
	/**
	 * deletes a tree
	 * @param myTree the tree to be deleted
	 */
	public void deleteTree(JTree myTree) {
		int numOfTrees = treeList.size();
		//System.out.println("Tree thinking of deletion?");
		//Find the tree
		for(int i =0;i<numOfTrees;i++) {
			//If tree found
			if(treeList.get(i)==myTree) {
				//System.out.println("Tree deleted?");
				//remove from list
				treeList.remove(i);
				//Go back to treeless state if none left
				if(treeList.size()==0) {
					backPanelInitialiseText();
				}
				//Hide the tree
				myTree.setVisible(false);
				//Remove all references of the tree
				backgroundPanel.remove(myTree);
				return;
			}
		}
	}
	
	/**
	 * Get the files that were previously uploaded when application was closed
	 */
	public void getOldUploads() {
		try {
			//System.out.println("Trying");
			//Read file that stores all previous files names
			File myObj = new File(System.getProperty("java.class.path")+"/../CodeCheckerOptions/uploads.txt");
	      	Scanner myReader;
			myReader = new Scanner(myObj);
			
	      	while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        //System.out.println(data);
		        //Read in previous file
	        	File temp = new File(data);
	        	//If previous file exists then upload it
	        	if(temp.exists()) {
	        		uploadFile(temp);
	        	}
		    }
		    myReader.close();
		} catch (FileNotFoundException e)
		{
			System.out.println("An error occurred when reading in previous files");
		}
	}
	
	/**
	 * Gets the entire tree list
	 * @return all the trees
	 */
	public List<JTree> getTrees(){
		return treeList;
	}
}
