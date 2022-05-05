import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Class to display a download window upon pressing the download button
 * @author Craig
 *
 */
public class DownloadWindow {
	private Window parent;
	private JFrame f;
	private JLabel exportFolderLabel;
	private JTextField exportFolder;
	private JButton exportButton;
	private JLabel treeLabel;
	private JScrollPane scrollPanel;
	private JTextArea scrollContainer;
	private List<JTree> trees;
	//Have a list of check nodes for each tree
	private List<List<CheckNode>> boxes;
	private JButton doIt;
	private int buffer;
	private int textSize;
	
	/**
	 * Basic constructor
	 * @param temp The window of which the download window came from
	 */
	public DownloadWindow(Window temp){
		parent = temp;
		init();
	}
	
	/**
	 * Setting up the window
	 */
	private void init() {
		//The frame
		f=new JFrame("Code Checker - Download");//creating instance of JFrame  
		f.setIconImage(parent.getFrame().getIconImage());
		f.setBounds((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()*3/8, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()/6,(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()/4, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()*2/3);//Set default size for not maximized window
		f.setLayout(null);//using no layout managers  
		f.setVisible(true);//making the frame visible  
		f.setResizable(false);
		f.getContentPane().setBackground(Window.backColor);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//Close window upon closing
		f.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent event) {
		        close(false);
		    }
		});
		
		//Set basic sizes
		textSize = f.getWidth()/25;
		buffer = f.getWidth()/40;
		
		//The top label
		//exportFolderLabel = parent.getNewLabel("Choose the folder you would like the downloads to be saved to:");
		exportFolderLabel = new JLabel();
		addContainer(exportFolderLabel);
		exportFolderLabel.setText("<html><body>Choose the folder you would like the downloads<br>to be saved to (you can drag and drop):");
		exportFolderLabel.setBounds(1,1,f.getWidth()-2,textSize*3);
		
		//export folder text area
		exportFolder = new JTextField();
		addContainer(exportFolder);
		//Drag and drop feature taken from: 
		//https://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path
		exportFolder.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;

			public synchronized void drop(DropTargetDropEvent evt) {
		        try {
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            @SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            for (File fileDragged : droppedFiles) {
		                //Save folder location
		            	if(fileDragged.isDirectory()) {
		            		exportFolder.setText(exportFolder.getText() + fileDragged.getAbsolutePath()+";");
		            	}
		            }
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});
		exportFolder.setBounds(1,textSize*3+1,f.getWidth()-2-textSize*13/2-14,textSize*2);
		exportFolder.setBorder(null);
		exportFolder.setCaretColor(Window.secondaryColor);
		//Get the previous download folder location used
		getOldDownloadFolders();
		
		//export button
		exportButton = new JButton("Browse");
		addContainer(exportButton);
		exportButton.setBounds(f.getWidth()-textSize*13/2-16, textSize*3+1, textSize*13/2, textSize*2);
		//exportButton.setIcon((Icon) parent.getFrame().getIconImage());
		exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileSystem = new JFileChooser();
                fileSystem.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileSystem.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                	File temp = fileSystem.getSelectedFile();
                	if(temp.isDirectory()) {
                    	exportFolder.setText(exportFolder.getText()+temp.getAbsolutePath()+";");                		
                	}
                }
            }
        });
		//Change colour on hover
		exportButton.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	exportButton.setBackground(Window.backColor);
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	exportButton.setBackground(Window.otherBackColor);
		    }
		});
		
		//File tree label
		treeLabel = new JLabel();
		addContainer(treeLabel);
		treeLabel.setText("<html><body>Select files you would like to download the errors of.<br>Folders include the errors of each file it holds:");
		treeLabel.setBounds(1,textSize*5+buffer,f.getWidth()-2,textSize*3);
		
		//File tree
		makeFileTreePanel();
		treeDisplay();
		
		//Download button
		doIt = new JButton();
		addContainer(doIt);
		doIt.setBounds(1,f.getHeight()-textSize*3+buffer/2-39, f.getWidth()-16, textSize*3-buffer/2);
		doIt.setText("Download Selected");
		doIt.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		//Make button change colour upon hover
		doIt.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	doIt.setBackground(Window.backColor);
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	doIt.setBackground(Window.otherBackColor);
		    }
		});
		doIt.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	downloadButtonPressed();
		    }
		});
	}
	
	/**
	 * Makes and populates the file tree panel
	 */
	private void makeFileTreePanel() {
		//Make text area for the background
		scrollContainer = new JTextArea();
		scrollContainer.setBackground(Window.backColor);
		scrollContainer.setFont(new Font(Window.bodyFont.getFontName(),Font.PLAIN,textSize));	
		scrollContainer.setBounds(0, 0, f.getWidth()-16, f.getHeight()-(textSize*11+buffer)-37);
		scrollContainer.setBorder(null);
		scrollContainer.setEditable(false);
	
		//make scroll area
		scrollPanel = new JScrollPane(scrollContainer);
		addContainer(scrollPanel);
		scrollPanel.setBackground(Window.backColor);
		scrollPanel.setBounds(1, textSize*8+buffer, f.getWidth()-16, f.getHeight()-(textSize*11+buffer)-37);
		scrollPanel.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		scrollPanel.setAutoscrolls(true);
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		//Set the colours of the scroll bars
		scrollPanel.getVerticalScrollBar().setBackground(Window.backColor);
		scrollPanel.getHorizontalScrollBar().setBackground(Window.backColor);
		scrollPanel.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
		    @Override
		    protected void configureScrollBarColors() {
		        this.thumbColor = Window.otherBackColor;
		    }
		});
		scrollPanel.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
		    @Override
		    protected void configureScrollBarColors() {
		        this.thumbColor = Window.otherBackColor;
		    }
		});
		
		//Clone the tree nodes 
		trees = cloneTreeList(parent.getTrees());
		//Initialise the checkbox list lists
		boxes = new Vector<List<CheckNode>>();		
		int numOfTrees = trees.size();
		//For every tree
		for(int i = 0;i<numOfTrees;i++) {
			JTree curTree = trees.get(i);
			int treeNum = i;
			scrollContainer.add(curTree);
			//resize the trees upon expansion or collapse
			curTree.addTreeExpansionListener(new TreeExpansionListener() {
			      public void treeExpanded(TreeExpansionEvent event) {
			    	  treeDisplay();
			      }

			      public void treeCollapsed(TreeExpansionEvent event) {
			    	  treeDisplay();
			      }
			});
			//Listener for when a node is clicked
			curTree.addTreeSelectionListener(new TreeSelectionListener() {
			    public void valueChanged(TreeSelectionEvent e) {
			        DefaultMutableTreeNode node = (DefaultMutableTreeNode) curTree.getLastSelectedPathComponent();
			        if (node == null) {
			        	return;
			        }
			        treeClick(treeNum, node);
			    }
			});
			//Initialise the checkbox list
			boxes.add(new Vector<CheckNode>());
			List<CheckNode> curBoxes= boxes.get(i);
			//Add checkboxes
			TreeNode root = (TreeNode) curTree.getModel().getRoot();
			//Add root check
			addBox(root, curTree.getName(), curBoxes, true);
			int numOfNodes = curTree.getModel().getChildCount(curTree.getModel().getRoot());
			for(int nodeNum = 0;nodeNum < numOfNodes; nodeNum++) {
				//Add child check
				TreeNode curNode = (TreeNode) curTree.getModel().getChild(root, nodeNum);
				addBox(curNode, curTree.getName(), curBoxes,false);
				addChildBoxes(curNode, curTree.getName(), curBoxes);
			}
		}
	}
	
	/**
	 * resize the tree panel to accomodate for more or less nodes
	 */
	public void treeDisplay() {
		String backgroundPanelText = "";
		//Set width of scroll
		for(int i =0;i<fileTreePanel.maxTextLength;i++) {
			backgroundPanelText+="    ";
		}
		//Set Height of scroll
		int numOfLines = totalTreeCellCount()*4/3;
		for(int i = 0;i<numOfLines;i++) {
			backgroundPanelText+="\n";
		}
		scrollContainer.setText(backgroundPanelText);
		
		//Set dimensions
		int numOfTrees = trees.size();
		int treeCellHeight = (int) trees.get(0).getRowBounds(0).getHeight();
		int myHeight;
		int prevHeight = 0;
		for(int i = 0;i<numOfTrees;i++) {
			JTree curTree = trees.get(i);
			myHeight = prevHeight + countTreeCells(curTree)*treeCellHeight;
			curTree.setBounds(17,prevHeight,f.getWidth(),countTreeCells(curTree)*treeCellHeight);
			prevHeight = myHeight;
		}
		//Display the checkboxes next to the correct nodes
		displayBoxes();
	}
	
	/**
	 * Display check boxes next to corresponding nodes
	 */
	public void displayBoxes() {
		//Hide all boxes so that if we're removing some they don't show up
		hideAllBoxes();
		int numOfTrees = trees.size();
		int treeCellHeight = (int) trees.get(0).getRowBounds(0).getHeight();
		int myHeight;
		List<String> parentList;
		int numOfCell=0;
		//For all trees
		for(int i = 0;i<numOfTrees;i++) {
			JTree curTree = trees.get(i);
			//Get visible row count
			int numOfNodes = curTree.getRowCount();
			//For all the rows
			for(int counter = 0;counter<numOfNodes;counter++) {
				//Get the visible node that corresponds to the row
				TreeNode myNode = (TreeNode) curTree.getPathForRow(counter).getLastPathComponent();
				//Get the parent list of that node to get its ID
				parentList = new Vector<String>();
				parentList.add(curTree.getName());
				fileTreePanel.getParentList(myNode, parentList);
				//Find the box with the corresponding ID
				CheckNode myBox = findCheckNodeFromID(counter, i, parentList);
				//If box exists then set it's dimensions
				if(myBox != null) {
					myHeight = treeCellHeight*numOfCell;
					myBox.setBoxBounds(0, myHeight+2);
				}
				else {
					System.out.println("Error with node: "+fileTreePanel.ridTreeNodeOfHTML(myNode.toString()));
				}
				//Keep track of how many boxes have been displayed
				numOfCell++;
			}
			
		}
	}
	
	/**
	 * Makes all checkboxes invisible
	 */
	public void hideAllBoxes() {
		//loop through all boxes
		int numOfTrees = boxes.size();
		for(int i = 0;i<numOfTrees;i++) {
			List<CheckNode> curTree = boxes.get(i);
			int numOfNodes = curTree.size();
			for(int counter = 0; counter<numOfNodes;counter++) {
				curTree.get(counter).setVisible(false);
			}
		}
	}
	
	/**
	 * Triggers the downloading of the selected files
	 */
	public void downloadButtonPressed() {
		//System.out.println("Do it pressed :)");
		//Get the string of all the folders to export to
		String folderList = exportFolder.getText();
		//If folders have been specified
		if(!folderList.isBlank()) {
			//Get the names of the export folders individually
			String[] saveFolderPaths = folderList.split(";");
			int numOfSaves = saveFolderPaths.length;
			//Get a list of all the nodes that have been selected
			List<CheckNode> saveNodes = getCheckedNodes();
			//If at least 1 node has been selected
			if(!saveNodes.isEmpty()) {
				List<String> output;
				List<CheckNode> checkedNodes = getCheckedNodes();
				//Save all folder nodes selected
				List<FolderStore> checkedFolder = getCheckedFolderStores(checkedNodes);
				int numOfFolders = checkedFolder.size();
				for(int i = 0;i<numOfFolders;i++) {
					FolderStore curFolder = checkedFolder.get(i);
					if(curFolder.getErrorReport() == null) {
						output = new Vector<String>();
						//Get errors
						curFolder.getErrorSummary(output);
						curFolder.printErrorsFolder(0, output);
						String whatToSave = CodeDisplay.getHTMLForm(output);
						//Save the files to every folder required
						for(int counter = 0;counter<numOfSaves;counter++) {
							writeHTMLFile(saveFolderPaths[counter],curFolder.getName()+".html",whatToSave);
						}
					}else {
						for(int counter = 0;counter<numOfSaves;counter++) {
							writeHTMLFile(saveFolderPaths[counter],curFolder.getName()+".html", curFolder.getErrorReport());
						}
					}
					
				}
				//Do the same for files
				List<FileStore> checkedFiles = getCheckedFileStores(checkedNodes);
				int numOfFiles = checkedFiles.size();
				for(int i = 0;i<numOfFiles;i++) {
					FileStore curFile = checkedFiles.get(i);
					if(curFile.getErrorReport()==null) {
						output = new Vector<String>();
						curFile.printErrorsFile(0, output);
						String whatToSave = CodeDisplay.getHTMLForm(output);
						for(int counter = 0;counter<numOfSaves;counter++) {
							writeHTMLFile(saveFolderPaths[counter],curFile.getName()+".html",whatToSave);
						}
					}
					else {
						for(int counter = 0;counter<numOfSaves;counter++) {
							writeHTMLFile(saveFolderPaths[counter],curFile.getName()+".html",curFile.getErrorReport());
						}
					}
					
				}
				//Show success message and close
				JOptionPane.showMessageDialog(null, "Files Saved");
				close(true);
			//Error Messages
			}else {
				JOptionPane.showMessageDialog(null, "Please select the folders and files that you want saved");
			}
		}else {
			JOptionPane.showMessageDialog(null, "Please set the filepath(s) that you want the files to be saved to");
		}
	}
	
	/**
	 * Gets the FolderStores of all the folders in a list a nodes 
	 * @param theNodes The nodes needing to be filtered for folders
	 * @return A list of FolderStores corresponding to the nodes that are folders
	 */
	public List<FolderStore> getCheckedFolderStores(List<CheckNode> theNodes) {
		int numOfNodes = theNodes.size();
		List<FolderStore> folderList = new Vector<FolderStore>();
		for(int i = 0; i<numOfNodes;i++) {
			CheckNode curNode = theNodes.get(i);
			//If it is a folder, get it's ID and use that to get the folder store. Add that folder store to the list
			if(!curNode.isFile()) {
				folderList.add(parent.getFolder(curNode.getID()));
			}
		}
		return folderList;
	}
	
	/**
	 * Gets the FileStores of all the files in a list a nodes 
	 * @param theNodes The nodes needing to be filtered for folders
	 * @return A list of FileStores corresponding to the nodes that are files
	 */
	public List<FileStore> getCheckedFileStores(List<CheckNode> theNodes) {
		int numOfNodes = theNodes.size();
		List<FileStore> fileList = new Vector<FileStore>();
		for(int i = 0; i<numOfNodes;i++) {
			CheckNode curNode = theNodes.get(i);
			//If it is a file, get it's ID and use that to get the file store. Add that file store to the list
			if(curNode.isFile()) {
				fileList.add(parent.getFile(curNode.getID()));
			}
		}
		return fileList;
	}
	
	/**
	 * Gets a list of checknodes corresponding to all the nodes that are currently ticked
	 * @return the ticked nodes
	 */
	public List<CheckNode> getCheckedNodes() {
		List<CheckNode> nodeList = new Vector<CheckNode>();
		int numOfTrees = boxes.size();
		//for every tree
		for(int i = 0;i<numOfTrees;i++) {
			List<CheckNode> curTree = boxes.get(i);
			int numOfNodes = curTree.size();
			//For every node
			for(int counter = 0; counter<numOfNodes;counter++) {
				CheckNode curNode = curTree.get(counter);
				//If checked add it to the list
				if(curNode.getChecked()) {
					nodeList.add(curNode);
				}
			}
		}
		return nodeList;
	}
	
	/**
	 * Creates a new html file based on the inputs given
	 * @param filePath the path of where you want the file saved
	 * @param fileName what you want the file called
	 * @param output the contents of the file
	 */
	public static void writeHTMLFile(String filePath, String fileName, String output) {
		try {
			//Create file
			FileWriter myWriter = new FileWriter(filePath+"\\"+fileName);
			//Write contents
			myWriter.write(output);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred when saving error file "+ filePath+"\\"+fileName);
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new html file based on the inputs given
	 * @param filePath the path of where you want the file saved
	 * @param fileName what you want the file called
	 * @param output the contents of the file
	 */
	public static void writeHTMLFile(String filePath, String fileName, List<String> output) {
		try {
			//Create file
			FileWriter myWriter = new FileWriter(filePath+"\\"+fileName);
			int outputSize = output.size();
			//Write content
			myWriter.write("<html><body>");
			for(int i = 0;i<outputSize;i++) {
				myWriter.write(output.get(i)+"<br>");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred when saving error file "+ filePath+"\\"+fileName);
			e.printStackTrace();
		}
	}
	
	/**
	 * turns the input into an appropriate html file name
	 * @param input the name of the title you want
	 * @return the html file title
	 */
	public static String getFileName(String input) {
		return input.split(".")[0]+".html";
	}
	
	/**
	 * Toggles the check state of the node inputted
	 * @param treeNum the number in the list "trees" of the tree that contains the node
	 * @param myNode the node that needs flipped
	 */
	public void treeClick(int treeNum, TreeNode myNode) {
		//Get the ID of the node
		List<String> parentList = new Vector<String>();
		parentList.add(trees.get(treeNum).getName());
		fileTreePanel.getParentList(myNode, parentList);
		//Find the node
		CheckNode temp = findCheckNodeFromID(0, treeNum, parentList);
		//If the node exists then flip it
		if(temp != null) {
			temp.flipChecked();
			//temp.print();
		}
		else {
			System.out.println("Couldn't find box?");
		}
	}
	
	/**
	 * Method to find a node using a list of strings comprised of the file path to get to that node
	 * @param startPos A number to start at in the search to optimise the time needed to find a node
	 * @param treeNum The number in the trees list that the tree being searches lies
	 * @param myID The ID of the node. Made of the file path. Can be retrieved from a node using the getParents method in fileTreePanel with the tree name in position 0
	 * @return the node of the ID given
	 */
	public CheckNode findCheckNodeFromID(int startPos, int treeNum, List<String> myID) {
		List<CheckNode> myBoxes = boxes.get(treeNum);
		int numOfBoxes = myBoxes.size();
		for(int i =startPos;i<numOfBoxes;i++) {
			CheckNode curBox = myBoxes.get(i);
			//If box is the one we're looking for then return it
			if(curBox.compareID(myID)) {
				return curBox;
			}
		}
		return null;
	}
	
	/**
	 * Gets the total number of viewable cells of all the trees
	 * @return the number of viewable cells
	 */
	public int totalTreeCellCount() {
		int numOfTrees = trees.size();
		int counter = 0;
		//Get cells of each tree
		for(int i =0;i<numOfTrees;i++) {
			counter += countTreeCells(trees.get(i));
		}
		return counter+1;
	}
	
	/**
	 * Get the number of viewable cells of a tree
	 * @param countTree the tree needing the cells counted
	 * @return the number of cells
	 */
	public int countTreeCells(JTree countTree) {
		return countTree.getRowCount();
	}
	
	/**
	 * Add a checknode
	 * @param curNode The tree node that is having a checkNode coinciding with it
	 * @param treeName the name of the tree that the node is in
	 * @param curBoxes the list of boxes that the checkNode needs added to
	 * @param ticked if the box should start ticked
	 */
	public void addBox(TreeNode curNode, String treeName, List<CheckNode> curBoxes, boolean ticked) {
		//Get NodeID
		List<String> parentList = new Vector<String>();
		parentList.add(treeName);
		fileTreePanel.getParentList(curNode, parentList);
		//Create node and add it to the list
		CheckNode myNode = new CheckNode(scrollContainer, parentList);
		curBoxes.add(myNode);
		if(ticked) {
			myNode.flipChecked();
		}
	}
	
	/**
	 * Adds the checkBoxes for the child nodes of the node given
	 * @param input the parent node
	 * @param treeName the name of the tree that the nodes belong to
	 * @param curBoxes the list that the new boxes should be added to
	 */
	public void addChildBoxes(TreeNode input, String treeName, List<CheckNode> curBoxes) {
		int nodeNum = input.getChildCount();
		if(nodeNum >0) {
			//Loop through all the children
			for(int i = 0;i<nodeNum;i++) {
				//get the node from the child number and add a corresponding checkNode
				addBox((TreeNode) input.getChildAt(i), treeName,curBoxes,false);
				List<String> curID = curBoxes.get(curBoxes.size()-1).getID();
				//Add child box if cur node represents a folder
				if(!curID.get(curID.size()-1).contains(".")) {
					addChildBoxes(input.getChildAt(i),treeName,curBoxes);
				}
			}
		}
	}
	
	/**
	 * Saves names of the folders being downloaded to to a file.
	 */
	public void saveDownloadFolder() {
		if(!exportFolder.getText().isEmpty()) {
			try {
				File tempF = new File(System.getProperty("java.class.path")+"/../CodeCheckerOptions/downloadFolders.txt");
				tempF.getParentFile().mkdirs();
				FileWriter myWriter = new FileWriter(tempF);
				myWriter.write(exportFolder.getText());
				myWriter.close();
			} catch (IOException e) {
				System.out.println("An error occurred when saving download folder names.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * gets the names of the folders being downloaded from a file.
	 */
	public void getOldDownloadFolders() {
		try {
			//System.out.println("Trying");
			File myObj = new File(System.getProperty("java.class.path")+"/../CodeCheckerOptions/downloadFolders.txt");
	      	Scanner myReader = new Scanner(myObj);
	      	//Set the export folder text to the first line of the file
		    exportFolder.setText(myReader.nextLine());
		    myReader.close();
		} catch (FileNotFoundException e)
		{
			System.out.println("An error occurred when reading in previous download folder names");
		}
	}
	
	/**
	 * clones a list of trees to another list of trees. This removes all listeners
	 * @param main the list of trees needing clones
	 * @return the freshly cloned trees
	 */
	public static List<JTree> cloneTreeList(List<JTree> main){
		List<JTree> copy = new Vector<JTree>();
		int numOfTrees = main.size();
		//For each tree
		for(int i = 0;i<numOfTrees;i++) {
			JTree curTree = main.get(i);
			//DefaultMutableTreeNode topNode = (DefaultMutableTreeNode) curTree.getModel().getRoot();
			//Set the root
			JTree newTree = new JTree((DefaultMutableTreeNode) curTree.getModel().getRoot());
			//Collapse the tree
			newTree.collapseRow(0);
			//Set name
			newTree.setName(curTree.getName());
			//Set visuals
			newTree.putClientProperty("JTree.lineStyle", "None");
			newTree.setVisible(true);
			newTree.setBackground(Window.backColor);
			copy.add(newTree);
		}
		return copy;
	}
	
	/**
	 * returns a new code with the same text
	 * @param input the old node
	 * @return the new node
	 */
	public static DefaultMutableTreeNode returnCleanNode(DefaultMutableTreeNode input) {
		return new DefaultMutableTreeNode(input.toString());
	}
	
	/**
	 * close the download screen
	 * @param forced if the prompt should be shown (false) or not
	 */
	private void close(boolean forced) {
		if(!forced) {
			//Ask if they are sure they want to close
			int input = JOptionPane.showConfirmDialog(null, "Are you sure that you want close the window and cancel the download?","Clear files",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
	    	//If user selected yes
	    	if(input==0) {
	    		//Close the window, saving the download folder names
	    		saveDownloadFolder();
	    		f.dispose();
	    	}
		}
		else {
			saveDownloadFolder();
    		f.dispose();
		}
		
	}
	
	/**
	 * adds the given container to the download window and sets some default properties
	 * @param c
	 */
	public void addContainer(Container c) {
		c.setBounds(0,0,5,5);  //X cord, Y cord, Width, Height       
		f.add(c);//adding button in JFrame  
		c.setFont(new Font(Window.bodyFont.getFontName(),Font.PLAIN,textSize));		
		c.setBackground(Window.otherBackColor);
		c.setForeground(Window.textColor);
		c.setVisible(true);
	}
}
