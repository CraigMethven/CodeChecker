import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.awt.image.BufferedImage;


import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.tree.DefaultMutableTreeNode;  

/**
 * Class for creating the window shown
 * Started by following: https://www.javatpoint.com/java-swing 
 * @author Craig
 *
 */
public class Window {
	private JFrame f;
	private Dimension previousDims;
	public static Color backColor;
	public static Color secondaryColor;
	public static Color textColor;
	public static Color otherBackColor;
	public static Font titleFont;
	public static Font bodyFont;
	private Sidebar mySidebar;
	private CodeDisplay myCodeDisplay;
	private List<FolderStore> importedFiles;
	private FolderStore curDisplayedFolder = null;
	private FileStore curDisplayedFile = null;
	private boolean ignoreTextFileSave = false;
	
	/**
	 * Constructor
	 */
	public Window(){  
		init();	
	}
	/**
	 * Initialises the form
	 */
	public void init(){
		importedFiles = new Vector<FolderStore>();
		
		bodyFont = new Font("Baxter Sans", Font.PLAIN, 12);
		
		//bodyFont = new Font("Consolas", Font.PLAIN, 12);
		if(AdvOptions.darkMode) {
			//For Dark Mode
			titleFont = new Font("Abadi", Font.BOLD, 22);
			backColor = new Color(0.27f,0.30f,0.35f);
			otherBackColor = new Color(0.15f,0.18f,0.22f);
			secondaryColor = new Color(0.2f,0.8f,0.8f,1f);
			textColor = new Color(0.9f,0.9f,0.9f);
		}else {
			//For "Uni" mode
			titleFont = new Font("Baxter Sans", Font.BOLD, 22);
			backColor = new Color(1.0f,0.98f,0.96f);
			otherBackColor = new Color(0.92f,0.91f,0.9f);
			secondaryColor = new Color(0.27f,0.4f,0.89f);
			textColor = new Color(0.23f,0.23f,0.23f);
			CodeCheckerConstants.htmlYellowText = "<span style=\"color: #E69F00\">";
			CodeCheckerConstants.htmlFileColor = "<span style=\"color: #13B9A9\">";
			CodeCheckerConstants.htmlMethodColor = "<span style=\"color: #519FED\">";
			CodeCheckerConstants.htmlRedText = "<span style=\"color: #DE0000\">";
			CodeCheckerConstants.htmlOrangeText = "<span style=\"color: #E3722D\">";
			CodeCheckerConstants.htmlGreenText = "<span style=\"color: #00aa00\">";
		}
		
		f=new JFrame("Code Checker");//creating instance of JFrame  
		try {
			ImageIcon icon = new ImageIcon(Main.getImage("/logo.png"),"Programs logo");
			f.setIconImage(icon.getImage());
		}
		catch(NullPointerException e) {
			System.out.println("Problem reading in the logo image");
		}
		//ImageIcon icon = new ImageIcon(System.getProperty("java.class.path")+"/images/logo.png","Programs logo");
		//f.setIconImage(icon.getImage());
		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//f.setSize(screenSize);//Set the size to the screen size
		f.setSize(720,520);//Set default size for not maximized window
		f.setExtendedState(JFrame.MAXIMIZED_BOTH); //Make full screen
		f.setLayout(null);//using no layout managers  
		f.setVisible(true);//making the frame visible  
		f.getContentPane().setBackground(backColor);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent event) {
		        close();
		    }
		});
		
		//Set initial screen size
		previousDims = Toolkit.getDefaultToolkit().getScreenSize();
		
		//Create a panel to hide the flashes upon things being created
		JPanel initHideSquare = new JPanel();
		initHideSquare.setBackground(backColor);
		initHideSquare.setBounds(0, 0, f.getWidth(), f.getHeight());
		f.add(initHideSquare);
		
		myCodeDisplay = new CodeDisplay(this);
		mySidebar = new Sidebar(this);
		
		//Add event manager for resizing the window
		f.getRootPane().addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            resize(false);
	        }
		});
		
		RightClickMenu.setWindow(this);
		resize(true);
		runChecks();
		
		initHideSquare.setVisible(false);
		setSidebarCheckButtonHighlight(false);
	}
	
	/**
	 * basic getter
	 * @return the jframe
	 */
	public JFrame getFrame() {
		return f;
	}
	
	/**
	 * Runs when the window is being closed to save settings
	 */
	public void close() {
		//Save the check options that the user wants completed
		saveChecks();
		saveAdvOptions();
		//Save the names of the files the user has uploaded
		saveUploads();
		
		System.out.println("Program Ended");
		f.dispose();
	    System.exit(0);
	}
	
	/**
	 * Runs when the window has been resized
	 * @param forced if the resize should be forced so every item gets sized no matter if it's displayed or not
	 */
	public void resize(boolean forced){
		//Set min height:
		if(f.getSize().getHeight()<520) {
			//System.out.println("Setting height: "+f.getSize().getHeight());
			f.setSize((int) f.getSize().getWidth(), 540);
		}
		//set min width
		if(f.getSize().getWidth()<640) {
			//System.out.println("Setting width: "+f.getSize().getWidth());
			f.setSize(640, (int) f.getSize().getHeight());
		}
		//Check ratio
//		if(f.getSize().getHeight()>f.getSize().getWidth()/2) {
//			f.setSize((int) f.getSize().getHeight()*2, (int) f.getSize().getHeight());
//		}
		
		//If the window size is different than before
		if((f.getSize().getHeight() != previousDims.getHeight()) || (f.getSize().getWidth() != previousDims.getWidth()) || forced) {
			previousDims = f.getSize();
			mySidebar.resize();
			myCodeDisplay.resize(mySidebar.getWidth(),false);
			
			//System.out.println(f.getSize().getHeight() + " : "+f.getSize().getWidth());
		}
	}
	
	/**
	 * gets a default button that will be displayed on this window
	 * @return the new button
	 */
	public JButton getNewButton() {
		JButton b=new JButton(/*can input text on button here*/);//creating instance of JButton  
		addContainer(b);
		b.setBackground(otherBackColor);
		b.setFocusPainted(false);
		//b.setBorder(BorderFactory.createLineBorder(secondaryColor));
		b.setBorder(null);
		b.setFont(titleFont);
		//Set default hover colours
		b.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		        b.setBackground(backColor);
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		        b.setBackground(otherBackColor);
		    }
		});
		return b;
	}
	
	/**
	 * gets a default label that will be displayed on this window
	 * @param labelText the text that will be on the label
	 * @return the new label
	 */
	public JLabel getNewLabel(String labelText) {
		JLabel l=new JLabel(/*can input text on button here*/);//creating instance of JButton  
		addContainer(l);
		l.setText(labelText);
		l.setForeground(textColor);
		return l;
	}
	
	/**
	 * gets a default text area that will be displayed on this window
	 * @return a new text area
	 */
	public JTextArea getNewTextArea() {
		JTextArea t=new JTextArea(/*can input text on button here*/);//creating instance of JButton  
		addContainer(t);
		//t.setBackground(backColor);
		t.setForeground(textColor);
		t.setEditable(false);
		return t;
	}
	
	/**
	 * gets a default text field that will be displayed on this window
	 * @return a new text field
	 */
	public JTextField getNewTextField() {
		JTextField t=new JTextField(/*can input text on button here*/);//creating instance of JButton  
		addContainer(t);
		//t.setBackground(backColor);
		t.setForeground(textColor);
		t.setEditable(false);
		t.setHorizontalAlignment(SwingConstants.RIGHT);
		t.setAlignmentY(0);
		return t;
	}
	
	/**
	 * gets a default editor pane that will be displayed on this window
	 * @return a new editor pane
	 */
	public JEditorPane getNewEditorPane() {
		JEditorPane e=new JEditorPane(/*can input text on button here*/);//creating instance of JButton  
		addContainer(e);
		//t.setBackground(backColor);
		e.setForeground(textColor);
		e.setEditable(true);
		e.setContentType("text/html");
		e.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		e.setCaretColor(secondaryColor);
		return e;
	}
	
	/**
	 * gets a default panel that will be displayed on this window
	 * @return a new panel
	 */
	public JPanel getNewPanel() {
		JPanel p=new JPanel(/*can input text on button here*/);//creating instance of JButton  
		addContainer(p);
		p.setForeground(textColor);
		return p;
	}
	
	/**
	 * gets a default scroll pane that will be displayed on this window
	 * @param c an item that is contained within the scroll pane
	 * @return a new scroll pane
	 */
	public JScrollPane getNewScrollPane(Container c) {
		JScrollPane s = new JScrollPane(c);
		addContainer(s);
		s.setAutoscrolls(true);
		s.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		s.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		//Set scroll bar colours
		s.getVerticalScrollBar().setBackground(Window.backColor);
		s.getHorizontalScrollBar().setBackground(Window.backColor);
		s.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
		    @Override
		    protected void configureScrollBarColors() {
		        this.thumbColor = Window.otherBackColor;
		    }
		});
		s.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
		    @Override
		    protected void configureScrollBarColors() {
		        this.thumbColor = Window.otherBackColor;
		    }
		});
		return s;
	}
	
	/**
	 * Sets the default parts of a container. This is used to set up all of the other default items
	 * @param c the container to set up
	 */
	public void addContainer(Container c) {
		c.setBounds(0,0,5,5);  //X cord, Y cord, Width, Height       
		f.add(c);//adding button in JFrame  
		c.setFont(bodyFont);		
		c.setBackground(otherBackColor);
		c.setForeground(secondaryColor);
		c.setVisible(true);
	}
	
	/**
	 * gets the options that were previously inputted and saved them
	 */
	public static void getOptions() {
		try {
//			Toolkit.getDefaultToolkit().getImage(getClass().getResource("/options/checks.txt"));
			File myObj = new File(System.getProperty("java.class.path")+"/../CodeCheckerOptions/checks.txt");
	      	Scanner myReader = new Scanner(myObj);
	      	
	      	Options tempClass = new Options();
			java.lang.reflect.Field[] myOptions = Options.class.getFields();
			
			int i=0;
	      	while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        if(data.equals("true")) {
		        	myOptions[i].set(tempClass, true);
		        }else {
		        	myOptions[i].set(tempClass, false);
		        }
		        i++;
		    }
		    myReader.close();
		} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
			System.out.println("An error occurred when getting options from file");
			//e.printStackTrace();
		}
	}
	
	/**
	 * upon closing saved the checks that are currently selected to read in upon load
	 */
	public static void saveChecks() {
		try {
			File tempF = new File(System.getProperty("java.class.path")+"/../CodeCheckerOptions/checks.txt");
			tempF.getParentFile().mkdirs();
			FileWriter myWriter = new FileWriter(tempF);

			Options tempClass = new Options();
			java.lang.reflect.Field[] myOptions = Options.class.getFields();
			int numOfOptions = myOptions.length-1;
			for(int i = 0; i<numOfOptions; i++) {
				myWriter.write(myOptions[i].getBoolean(tempClass)+"\n");
			}
			myWriter.close();
		} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
			System.out.println("An error occurred when saving which checks to run to file");
			e.printStackTrace();
		}
	}
	
	/**
	 * gets the options that were previously inputted and saved them
	 */
	public static void getAdvOptions() {
		try {
			File myObj = new File(System.getProperty("java.class.path")+"/../CodeCheckerOptions/advOptions.txt");
	      	Scanner myReader = new Scanner(myObj);
	      	
	      	if(myReader.nextLine().equals("false")){
	      		AdvOptions.darkMode=false;
	      	}
	      	if(myReader.hasNextLine()) {
	      		AdvOptions.mainMethodMaxLength = Integer.parseInt(myReader.nextLine());
	      	}if(myReader.hasNextLine()) {
	      		AdvOptions.maxMethodLength = Integer.parseInt(myReader.nextLine());
	      	}if(myReader.hasNextLine()) {
	      		AdvOptions.minMethodSizeToCompare = Integer.parseInt(myReader.nextLine());
	      	}if(myReader.hasNextLine()) {
	      		AdvOptions.percentOfSimilarities = Integer.parseInt(myReader.nextLine());
	      	}if(myReader.hasNextLine()) {
	      		AdvOptions.numOfElseNeededToBeConsideredLarge = Integer.parseInt(myReader.nextLine());
	      	}
		    myReader.close();
		} catch (IOException | IllegalArgumentException e) {
			System.out.println("An error occurred when getting advanced options from file");
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the advanced options selected into a file
	 */
	public static void saveAdvOptions() {
		try {
			File tempF = new File(System.getProperty("java.class.path")+"/../CodeCheckerOptions/advOptions.txt");
			tempF.getParentFile().mkdirs();
			FileWriter myWriter = new FileWriter(tempF);
			
			myWriter.write(AdvOptions.darkMode+"\n");
			myWriter.write(AdvOptions.mainMethodMaxLength+"\n");
			myWriter.write(AdvOptions.maxMethodLength+"\n");
			myWriter.write(AdvOptions.minMethodSizeToCompare+"\n");
			myWriter.write(AdvOptions.percentOfSimilarities+"\n");
			myWriter.write(AdvOptions.numOfElseNeededToBeConsideredLarge+"\n");
			myWriter.close();
		} catch (IOException | IllegalArgumentException e) {
			System.out.println("An error occurred when saving advanced options to file");
			e.printStackTrace();
		}
	}
	
	/**
	 * Save the files currently uploaded to import upon loading
	 */
	public void saveUploads() {
//		List<String> myUploads = new Vector<String>();
//		myUploads.add("");
		
		try {
			File tempF = new File(System.getProperty("java.class.path")+"/../CodeCheckerOptions/uploads.txt");
			tempF.getParentFile().mkdirs();
			FileWriter myWriter = new FileWriter(tempF);
			
			int numOfFolders = importedFiles.size();
			for(int i =0;i<numOfFolders;i++) {
				myWriter.write(importedFiles.get(i).getFilePath()+"\n");
			}
			myWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error when saving the uploaded documents");
			//e.printStackTrace();
		}
	}
	
	/**
	 * Adds a file to the system to be checked
	 * @param temp the file that is getting added
	 */
	public void addFile(FolderStore temp) {
		importedFiles.add(temp);
	}
	
	/**
	 * gets a folder store from an index
	 * @param index the index in the list that you want the folder store from
	 * @return the folder store saved at that index
	 */
	public FolderStore getFile(int index) {
		return importedFiles.get(index);
	}
	
	/**
	 * gets all of the imported files
	 * @return all of the files stored
	 */
	public List<FolderStore> getFileList() {
		return importedFiles;
	}
	
	/**
	 * Sets the error display to the text required
	 * @param tempS the text we want displayed in the error display
	 */
	public void setErrorDisplay(List<String> tempS) {
		myCodeDisplay.setErrorText(tempS);
	}
	
	/**
	 * Sets the code display to the text required
	 * @param tempS the text we want displayed in the code display
	 */
	public void setCodeDisplay(List<String> tempS) {
		myCodeDisplay.setCodeText(tempS);
	}
	
	/**
	 * Resizes the code display class
	 * @param forced
	 */
	public void resizeCodeDisplay(boolean forced) {
		myCodeDisplay.resize(mySidebar.getWidth(), forced);
	}
	
	/**
	 * Recolours an image to the colour given
	 * altered from https://stackoverflow.com/questions/48796888/swing-graphics-color-image-directly
	 * @param bImage the image needing recoloured
	 * @param iconColor the colour to colour all non transparent pixels
	 */
	public static void colourIcon(BufferedImage bImage, int iconColor) {
		for (int x = 0; x < bImage.getWidth(); x++) {
            for (int y = 0; y < bImage.getHeight(); y++) {
            	if(bImage.getRGB(x, y)!=0){
            		//System.out.println(bImage.getRGB(x, y));
            		Color imageColor = new Color(iconColor);
            		bImage.setRGB(x, y, imageColor.getRGB());
            	}
            }
        }
    }
	
	/**
	 * Converts a given Image into a BufferedImage
	 * Modified from https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) throws NullPointerException{
		//If already a buffered image return
	    if (img instanceof BufferedImage){
	        return (BufferedImage) img;
	    }
	    if(img.getWidth(null)<=0 || img.getHeight(null)<=0) {
	    	System.out.println("Problem converting to buffer image: "+img.toString()+ "; width: "+img.getWidth(null)+ "; height: "+img.getHeight(null));
	    	throw new NullPointerException();
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    //Draw the image on to the buffered image
	    bimage.getGraphics().drawImage(img, 0, 0, null);
	    	    
	    // Return the buffered image
	    return bimage;
	}
	
	/**
	 * Saves the error files to the place required
	 * @param filePath the place that we want the file to be saved to
	 */
	public void saveOutput(String filePath) {
		try {
			List<String> myExport = new Vector<String>();
			String title = myCodeDisplay.getTitle();
			title = title.substring(0, title.lastIndexOf('.'));
			FileWriter myWriter = new FileWriter(filePath+title+".html");
			int importedFilesLength = importedFiles.size();
			for(int i =0;i<importedFilesLength;i++) {
				FolderStore myStore = importedFiles.get(i);
				myStore.printErrorsFolder(0, myExport);
				myWriter.write(CodeDisplay.getHTMLForm(myExport));
				myWriter.close();
			}
		} catch (IOException e) {
			System.out.println("An error occurred when writing to the file "+filePath +".");
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the error files to the place required
	 * @param filePath the place that we want the file to be saved to
	 * @param output the text we want saved in the file
	 */
	public void saveOutput(String filePath, String output) {
		try {
			String title = myCodeDisplay.getTitle();
			title = title.substring(0, title.lastIndexOf('.'));
			FileWriter myWriter = new FileWriter(filePath+title+".html");
			myWriter.write(output);
			myWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Gets the list of imported files
	 * @return the list of folderstores uploaded to the program
	 */
	public List<FolderStore> getAllImportedFiles(){
		return importedFiles;
	}
	
	/**
	 * gets the file store represented by the file path given
	 * @param filePath the file path to the folder wanted
	 * @return the file store wanted
	 */
	public FileStore getFile(List<String> filePath) {
		int importedFileLength = importedFiles.size();
		for(int i =0;i<importedFileLength;i++) {
			FileStore tempF = importedFiles.get(i).getFile(filePath, 0);
			if(tempF != null) {
				return tempF;
			}
		}
		return null;
	}
	
	/**
	 * gets the folder store represented by the file path given
	 * @param filePath the file path to the folder wanted
	 * @return the folder store wanted
	 */
	public FolderStore getFolder(List<String> filePath) {
		int importedFileLength = importedFiles.size();
		for(int i =0;i<importedFileLength;i++) {
			FolderStore tempF = importedFiles.get(i).getFolder(filePath, 0);
			if(tempF != null) {
				return tempF;
			}
		}
		return null;
	}
	
	/**
	 * displays a folder that is represented by the tree node given
	 * @param treeName the tree that the node belongs to
	 * @param myNode the node needing to be displayed
	 */
	public void displayFolderStoreFromNode(String treeName, DefaultMutableTreeNode myNode) {
		FolderStore tempF = mySidebar.getFolderStoreFromNode(treeName,myNode);
		if(tempF != null) {
			//System.out.println(tempF.getName());
			displayFolderStore(tempF);
		}
		else {
			System.out.println("Sadness");
		}
	}
	
	/**
	 * displays a file that is represented by the tree node given
	 * @param treeName the tree that the node belongs to
	 * @param myNode the node needing to be displayed
	 */
	public void displayFileStoreFromNode(String treeName, DefaultMutableTreeNode myNode) {
		FileStore tempF = mySidebar.getFileStoreFromNode(treeName,myNode);
		if(tempF != null) {
			displayFileStore(tempF);
		}
	}
	
	/**
	 * displays the contents of a file store in the code display
	 * @param myFile the file store that you want displayed
	 */
	public void displayFileStore(FileStore myFile) {
		if(curDisplayedFolder != null && !ignoreTextFileSave) {
			curDisplayedFolder.setErrorReport(myCodeDisplay.getErrorText());
		}
		if(curDisplayedFile != null && !ignoreTextFileSave) {
			curDisplayedFile.setErrorReport(myCodeDisplay.getErrorText());
		}
		ignoreTextFileSave = false;
		
		myCodeDisplay.setTitle(myFile.getName());
		if(myFile.getErrorReport()==null) {
			List<String> tempLS = new Vector<String>();
			myFile.getErrorSummary(tempLS);
			myFile.printErrorsFile(0,tempLS);
			setErrorDisplay(tempLS);
			myFile.setErrorReport(getErrorText());
		}
		else {
			myCodeDisplay.setErrorText(myFile.getErrorReport());
		}
		List<String> myCode = myFile.getCode();
		int myFileLength = myCode.size();
		for(int i =0;i<myFileLength;i++) {
			myCode.set(i, myCode.get(i)+"\n");
		}
		curDisplayedFolder = null;
		curDisplayedFile = myFile;
		setCodeDisplay(myFile.getCode());
		setCodeTabButtonVisibility(true);
	}
	
	/**
	 * displays the contents of a folder store in the code display
	 * @param myFile the folder store that you want displayed
	 */
	public void displayFolderStore(FolderStore myFile) {
		if(curDisplayedFolder != null) {
			curDisplayedFolder.setErrorReport(myCodeDisplay.getErrorText());
		}
		if(curDisplayedFile != null) {
			curDisplayedFile.setErrorReport(myCodeDisplay.getErrorText());
		}
		
		myCodeDisplay.setTitle(myFile.getName());
		
		if(myFile.getErrorReport()==null) {
			List<String> tempLS = new Vector<String>();
			myFile.getErrorSummary(tempLS);
			myFile.printErrorsFolder(0, tempLS);
			setErrorDisplay(tempLS);
			myFile.setErrorReport(getErrorText());
		}else {
			myCodeDisplay.setErrorText(myFile.getErrorReport());
		}
		curDisplayedFile = null;
		curDisplayedFolder = myFile;
		setCodeDisplay(new Vector<String>());
		setCodeTabButtonVisibility(false);
	}
	
	/**
	 * gets the text stored in the error text area from the code display
	 * @return the text in that text area
	 */
	public String getErrorText() {
		return myCodeDisplay.getErrorText();
	}
	
	/**
	 * Deletes a folder from being stored in the program
	 * @param filePath the file path to the file that is needing deleted
	 */
	public void deleteFolder(List<String> filePath) {
		int numOfFolders = importedFiles.size();
		//If it is a root folder delete it from here
		if(filePath.size()==1) {
			//System.out.println("Deleting folder in window");
			for(int i =0;i<numOfFolders;i++) {
				if(importedFiles.get(i).getFilePath().equals(filePath.get(0))) {
					importedFiles.remove(i);
					return;
				}
			}
		}
		//If it is a nested folder then send the file path down the chain
		else {
			//System.out.println("Deleting folder in folder"+filePath.get(0)+filePath.size());
			for(int i =0;i<numOfFolders;i++) {
				//System.out.println(importedFiles.get(i).getFilePath());
				if(importedFiles.get(i).getFilePath().equals(filePath.get(0))) {
					//System.out.println("Getting here");
					importedFiles.get(i).removeFolder(filePath,0);
				}
			}
		}
	}
	
	/**
	 * delete a file given its file path
	 * @param filePath the filepath (ID) of the file being deleted
	 */
	public void deleteFile(List<String> filePath) {
		int numOfFolders = importedFiles.size();
		//if it is a root file
		if(filePath.size()==1) {
			//System.out.println("Deleting folder in window");
			for(int i =0;i<numOfFolders;i++) {
				if(importedFiles.get(i).getFilePath().equals(filePath.get(0))) {
					importedFiles.remove(i);
					return;
				}
			}
		}
		//if not a root file then search for it in the folders
		else {
			//System.out.println("Deleting folder in folder"+filePath.get(0)+filePath.size());
			for(int i =0;i<numOfFolders;i++) {
				//System.out.println(importedFiles.get(i).getFilePath());
				if(importedFiles.get(i).getFilePath().equals(filePath.get(0))) {
					//System.out.println("Getting here");
					importedFiles.get(i).removeFile(filePath,0);
				}
			}
		}
	}
	
	/**
	 * Reruns the checks of the directories stored
	 */
	public void rerunChecks() {
		resetCodeDisplay();
		int numOfDirectories = importedFiles.size();
		for(int i = 0;i<numOfDirectories;i++) {
			importedFiles.get(i).rerunChecks();
			runChecks();
		}
	}
	
	/**
	 * Set the visibility of the tab selection in the code display
	 * @param v if the buttons should be visible
	 */
	public void setCodeTabButtonVisibility(boolean v) {
		myCodeDisplay.setButtonVisibility(v);
	}
	
	/**
	 * deleted a tree from being in the file tab
	 * @param myTree the tree being removed
	 */
	public void deleteTree(JTree myTree) {
		mySidebar.deleteTree(myTree);
	}
	
	/**
	 * removes all of the files and folders stored
	 */
	public void deleteAll() {
		importedFiles = new Vector<FolderStore>();
		resetCodeDisplay();
	}
	
	/**
	 * Gets the list of trees stored in the file tab
	 * @return the list of trees
	 */
	public List<JTree> getTrees(){
		return mySidebar.getTrees();
	}
	
	/**
	 * basic setter
	 * @param setter what you want ignoreTextFileSave to be
	 */
	public void setIgnoreTextFileSave(boolean setter) {
		ignoreTextFileSave = setter;
	}
	
	/**
	 * Resets the code display to like nothing has been displayed on it
	 */
	public void resetCodeDisplay() {
		curDisplayedFolder = null;
		curDisplayedFile = null;
		myCodeDisplay.reset();
	}
	
	/**
	 * simple getter
	 * @return buffer variable
	 */
	public int getBuffer() {
		//If sidebar has been initialised
		if(mySidebar != null) {
			return mySidebar.getBuffer();
		}
		return 9;
	}
	
	/*
	 * FOR ERROR CHECKS
	 */
	
	/**
	 * runs checks of the saved directories
	 */
	public void runChecks() {
		if(Options.compareToOtherInputs) {
			compareMethodsToOtherUploads();
		}
	}
	
	/**
	 * Gets all of the methods
	 * @return a list containing all of the methods from the imported files and folders
	 */
	public List<MethodStore> getAllMethods(){
		int numOfDirs = importedFiles.size();
		List<MethodStore> allMethods = new Vector<MethodStore>();
		for(int i = 0;i<numOfDirs;i++) {
			importedFiles.get(i).getAllMethods(allMethods);
		}
		return allMethods;
	}
	
	/**
	 * Gets all of the methods
	 * @return a list for each directory storing a list of each of that directories methods
	 */
	public List<List<MethodStore>> getAllMethodsWithDirectory(){
		int numOfDirs = importedFiles.size();
		List<List<MethodStore>> methodList = new Vector<List<MethodStore>>();
		List<MethodStore> tempList;
		for(int i = 0;i<numOfDirs;i++) {
			tempList = new Vector<MethodStore>();
			importedFiles.get(i).getAllMethods(tempList);
			methodList.add(tempList);
		}
		return methodList;
	}
	
	/**
	 * Removes all of the short methods form the list
	 * @param theList the list to remove short methods from
	 * @param minSize the minimum size of method to keep
	 */
	public static void removeShortMethods(List<MethodStore> theList, int minSize) {
		int numOfMethods = theList.size();
		for(int i = 0;i<numOfMethods;i++) {
			List<String> cleanedCode = theList.get(i).getCleanedCode();
			if(cleanedCode!=null) {
				if(cleanedCode.size()<minSize) {
					theList.remove(i);
					i--;
					numOfMethods--;
				}
			}
		}
	}
	
	/**
	 * Compares the methods to each other, printing errors if they're similar
	 */
	public void compareMethods() {
		List<MethodStore> myMethods = getAllMethods();
		removeShortMethods(myMethods,AdvOptions.minMethodSizeToCompare);
		MethodStore methodi;
		MethodStore methodCounter;
		int percentageSimilar;
		
		int numOfMethods = myMethods.size();
		for(int i = 0;i<numOfMethods;i++) {
			methodi = myMethods.get(i);
			for(int counter = i+1;counter<numOfMethods;counter++) {
				methodCounter=myMethods.get(counter);
				if(i!=counter && methodi!=methodCounter) {
					percentageSimilar = methodi.compareMethod(methodCounter);
					if(percentageSimilar>AdvOptions.percentOfSimilarities) {
						//Print errors
						methodi.addSimilarMethodError(methodCounter,percentageSimilar,false);
						methodCounter.addSimilarMethodError(methodi,percentageSimilar,false);
					}
				}
			}
		}
	}
	
	/**
	 * Compares the methods to each other, printing errors if they're similar
	 */
	public void compareMethodsToOtherUploads() {
		List<List<MethodStore>> myMethods = getAllMethodsWithDirectory();
		int numOfDirs = myMethods.size();
		for(int i = 0;i<numOfDirs;i++) {
			removeShortMethods(myMethods.get(i),AdvOptions.minMethodSizeToCompare);
		}
		int percentageSimilar;
		//For every directory
		for(int i = 0;i<numOfDirs;i++) {
			List<MethodStore> curDir = myMethods.get(i);
			int curNumOfMethods = curDir.size();
			//For every method in current directory
			for(int counter = 0; counter<curNumOfMethods;counter++) {
				//Loop through every other directory
				for(int i2 = i;i2<numOfDirs;i2++) {
					List<MethodStore> curDir2 = myMethods.get(i2);
					int curNumOfMethods2 = curDir2.size();
					//Loop through all methods not checked before and compare them
					for(int counter2 = counter; counter2<curNumOfMethods2;counter2++) {
						//If not comparing to self
						if(curDir.get(counter)!=myMethods.get(i2).get(counter2) && !(i==i2 && counter==counter2)) {
							//Get percentage of similar
							percentageSimilar = curDir.get(counter).compareMethod(curDir2.get(counter2));
							if(percentageSimilar>AdvOptions.percentOfSimilarities) {
								//Print errors
								//If not from the same directory
								if(i!=i2) {
									curDir.get(counter).addSimilarMethodError(curDir2.get(counter2),percentageSimilar,false);
									curDir2.get(counter2).addSimilarMethodError(curDir.get(counter),percentageSimilar,false);
								}
								//If from same directory
								else {
									curDir.get(counter).addSimilarMethodError(curDir2.get(counter2),percentageSimilar,true);
									curDir2.get(counter2).addSimilarMethodError(curDir.get(counter),percentageSimilar,true);
								}
								
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * sets the check button to be highlighted or not
	 * @param highlighted if it should be highlighted
	 */
	public void setSidebarCheckButtonHighlight(boolean highlighted) {
		mySidebar.setCheckButtonText(highlighted);
	}
}
