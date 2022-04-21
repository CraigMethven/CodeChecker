import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * 
 * Class to store the main display of the code and errors, including the labels and buttons needed that aren't included in the side bar
 * @author Craig
 *
 */
public class CodeDisplay {
	private Window f;
	//For if there is nothing to be displayed
	private JLabel textEmptyNotif;
	//General
	private JLabel fileTitle;
	private JPanel background;
	//For the error display
	private JEditorPane errorArea;
	private JScrollPane scrollPanel;
	private JLabel errorIcon;
	//For document display
	private JEditorPane codeArea;
	private JScrollPane codeScrollPanel;
	private JLabel codeIcon;
	//For the buttons
	private Icon iconArray[];
	private JButton errorTab;
	private JButton codeTab;
	private JButton splitTab;
	private JPanel buttonBackground;
	private boolean folderDisplayed = false;
	//0 = error display; 1 = document display; 2 = split screen
	private int displayType = 0;
	
	/**
	 * basic constructor to store the window this should be displayed on
	 * @param tempF window to put the code onto
	 */
	public CodeDisplay(Window tempF){
		f = tempF;
		init();
	}
	
	/**
	 * initialises all the components
	 */
	private void init() {
		//Get the base filepath used as a constant to base the folders being read off of
		//String tempFilePath = System.getProperty("java.class.path");
		//Get an array of the icons
		iconArray = new ImageIcon[3];
		BufferedImage b[] = new BufferedImage[3];
		//Try getting all of the icons
		try {
//			b[0] = Window.toBufferedImage(ImageIO.read(new File(tempFilePath+"/images/errorTab.png")));
//			b[1] = Window.toBufferedImage(ImageIO.read(new File(tempFilePath+"/images/codeTab.png")));
//			b[2] = Window.toBufferedImage(ImageIO.read(new File(tempFilePath+"/images/splitTab.png")));
//			b[0] = Window.toBufferedImage(Main.getImage("/images/errorTab.png"));
//			b[1] = Window.toBufferedImage(Main.getImage("/images/codeTab.png"));
//			b[2] = Window.toBufferedImage(Main.getImage("/images/splitTab.png"));
			b[0] = Main.getBufferedImage("/errorTab.png");
			b[1] = Main.getBufferedImage("/codeTab.png");
			b[2] = Main.getBufferedImage("/splitTab.png");

			//Recolour each of the icons
			for(int i =0;i<3;i++) {
				Window.colourIcon(b[i], Window.backColor.getRGB());	
				//System.out.println("Suppose to be: " +Window.backColor.getRGB()+ " is: "+ b[i].getRGB(b[i].getWidth()/2, b[i].getHeight()/2));
				iconArray[i] = new ImageIcon((Image) b[i],"An icon");
			}
		} catch (NullPointerException/* | IOException*/ e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			System.out.println("Problem reading in images in code display");
		}	
//		iconArray[0] = new ImageIcon(tempFilePath+"/../images/errorTab.png","A error icon");
//		iconArray[1] = new ImageIcon(tempFilePath+"/../images/codeTab.png","A code icon");
//		iconArray[2] = new ImageIcon(tempFilePath+"/../images/splitTab.png","A split icon");
		
		//For the tab buttons in the top right
		errorTab = f.getNewButton();
		errorTab.setToolTipText("<html><body style = \"font-size: 12px;\"><b>Error Tab: </b><br>Shows the errors in the currently selected file");
		addButtonListener(errorTab,0);
		codeTab = f.getNewButton();
		codeTab.setToolTipText("<html><body style = \"font-size: 12px;\"><b>Code Tab: </b><br>Displays the code of the currently selected file");
		addButtonListener(codeTab,1);
		splitTab = f.getNewButton();
		splitTab.setToolTipText("<html><body style = \"font-size: 12px;\"><b>Split Tab: </b><br>Shows both the code (left) and errors (right) of the currently selected file.");
		addButtonListener(splitTab,2);
		//splitTab.addMouseListener(new rightClickListener());
		
		//For the background colour of the buttons
		buttonBackground = f.getNewPanel();
		buttonBackground.setBackground(Window.secondaryColor);
		buttonBackground.setVisible(false);
		
		//For the icons that show what tab is being shown
		errorIcon = f.getNewLabel("");
		errorIcon.setBackground(Window.secondaryColor);
		errorIcon.setVisible(false);
		errorIcon.setOpaque(true);
		codeIcon = f.getNewLabel("");
		codeIcon.setBackground(Window.secondaryColor);
		codeIcon.setVisible(false);
		codeIcon.setOpaque(true);
		
		//The title displayed above the bodies of text
		fileTitle = f.getNewLabel(null);
		fileTitle.setText(null);
		
		//Basic notification when nothing is selected
		textEmptyNotif = f.getNewLabel("<html><body>Please upload the files that you want checked<br><em>Right click on uploaded folders to display them</em>");
		
		//The stores for the bodies of text
		errorArea = f.getNewEditorPane();
		errorArea.setBackground(Window.backColor);
		codeArea = f.getNewEditorPane();
		codeArea.setBackground(Window.backColor);
		//Disable editing code panel:
		codeArea.addFocusListener(new FocusListener() {
	        @Override //Method to make it so that the code are can still have a carrot
	        public void focusLost(FocusEvent e) {
	        	codeArea.setEditable(true);
	        }

	        @Override //Method to make it so that the code can't be editted
	        public void focusGained(FocusEvent e) {
	        	codeArea.setEditable(false);
	        }
	    });
		
		//The scroll panels to add scroll functionality
		scrollPanel = f.getNewScrollPane(errorArea);
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setVisible(false);
		codeScrollPanel = f.getNewScrollPane(codeArea);
		codeScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		codeScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		codeScrollPanel.setVisible(false);
		
		//Adds the coloured border for the text bodies
		background = f.getNewPanel();
		background.setBackground(Window.secondaryColor);
		background.setVisible(false);
		
		//Sets initial size of everything
		resize(f.getFrame().getHeight()/3,true);
	}
	
	/**
	 * Resets code display to how it would be upon initialisation
	 */
	public void reset() {
		f.setIgnoreTextFileSave(true);
		//For if there is nothing to be displayed
		textEmptyNotif.setVisible(true);
		textEmptyNotif.setText("<html><body>Select the file/folder to view the errors of it<br><em>Right click on uploaded folders to display them</em>");
		//General
		fileTitle.setVisible(false);
		fileTitle.setText("");
		background.setVisible(false);
		//For the error display
		errorArea.setText("");
		scrollPanel.setVisible(false);
		errorIcon.setVisible(false);
		//For document display
		codeArea.setText("");
		codeScrollPanel.setVisible(false);
		codeIcon.setVisible(false);
		//For the buttons
		errorTab.setVisible(false);
		codeTab.setVisible(false);
		splitTab.setVisible(false);
		buttonBackground.setVisible(false);
		folderDisplayed = false;
		//0 = error display; 1 = document display; 2 = split screen
		displayType = 0;
	}
	
	/**
	 * method to add some basic listeners to a button to change colour upon hover and give click functionality
	 * @param b the button needing the listeners
	 * @param type the the integer representing the type of button pressed. 0 = error display; 1 = document display; 2 = split screen
	 */
	public void addButtonListener(JButton b, int type) {
		// Action to change type
		b.addActionListener(new ActionListener() {
		    @Override //If button clicked run method
		    public void actionPerformed(ActionEvent e) {
		    	changeDisplayType(type);
		    }
		});
		//Random setup
		b.setBackground(Window.secondaryColor);
		b.addMouseListener(new java.awt.event.MouseAdapter() {
			//Action to change look upon hover
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		        b.setBackground(Window.secondaryColor);
		        b.setBorder(BorderFactory.createRaisedBevelBorder());
		    }
		    //Action to change look upon unhovering
		    public void mouseExited(java.awt.event.MouseEvent evt) {
		        b.setBackground(Window.secondaryColor);
		        b.setBorder(null);
		    }
		});
		b.setVisible(false);
	}
	
	/**
	 * Sets the change tab buttons visibilities
	 * @param v if they should be visible
	 */
	public void setButtonVisibility(boolean v) {
		folderDisplayed = !v;
		errorTab.setVisible(v);
		codeTab.setVisible(v);
		splitTab.setVisible(v);
		buttonBackground.setVisible(v);
		if(!v) {
			changeDisplayType(0);
		}
	}
	
	/**
	 * Changes if the code, errors or split screen should be shown
	 * @param type 0 = error display; 1 = document display; 2 = split screen
	 */
	public void changeDisplayType(int type) {
		//Only change if not already being displayed
		if(displayType != type) {
			displayType = type;
			f.resizeCodeDisplay(false);			
		}
	}
	
	/**
	 * Sets visibility of all items based off of what should be displayed
	 * @param textPresent if items should be visible or not
	 */
	public void setVisible(boolean textPresent) {
		background.setVisible(textPresent);
		fileTitle.setVisible(textPresent);
		
		//Depending on tab selected - effect only components shown
		if(displayType == 0 || displayType == 2) {
			scrollPanel.setVisible(textPresent);
			scrollPanel.getVerticalScrollBar().setValue(0);
			scrollPanel.getHorizontalScrollBar().setValue(0);
		}
		if(displayType == 1 || displayType == 2) {
			codeScrollPanel.setVisible(textPresent);
			codeScrollPanel.getVerticalScrollBar().setValue(0);
			codeScrollPanel.getHorizontalScrollBar().setValue(0);
		}
		if(!folderDisplayed) {
			codeTab.setVisible(textPresent);
			errorTab.setVisible(textPresent);
			splitTab.setVisible(textPresent);
			buttonBackground.setVisible(textPresent);
		}
		textEmptyNotif.setVisible(!textPresent);
	}
	
	/**
	 * Resizes each element on the screen 
	 * @param sidebarWidth the width of the sidebar
	 * @param forced if the resize is being forced or based off of variables
	 */
	public void resize(int sidebarWidth, boolean forced) {
		Dimension windowSize = f.getFrame().getSize();
		int buffer = (int) (windowSize.getHeight()/30);
		int startPosX = sidebarWidth+buffer/2;
		int width = (int) windowSize.getWidth()-(sidebarWidth+buffer*2);
		
		//If there is text to display or it is being forced
		if(fileTitle.getText()!=null || forced) {
			int iconSize = buffer;
			setVisible(true);
			int startPosY = buffer*5/2;
			int height = (int) windowSize.getHeight()-(buffer*3+36);
			background.setBounds(startPosX-1, startPosY-1, width+2,height+2);
			//Sets the title size based on what tab is selected
			if(displayType!=2) {
				fileTitle.setBounds(startPosX+iconSize+buffer/3, 0, width*2/3, buffer*5/2);
			}else {
				fileTitle.setBounds(startPosX, 0, width*2/3, buffer*5/2);
			}
			fileTitle.setFont(new Font(Window.titleFont.getFamily(),Font.BOLD,buffer*3/2));
			resizeErrorTab(buffer,(int) windowSize.getWidth());
			resizeCodeTab(buffer,(int) windowSize.getWidth());
			resizeSplitTab(buffer,(int) windowSize.getWidth());
			buttonBackground.setBounds((int) windowSize.getWidth()-(buffer*6)-buffer/2-2, buffer*7/8-1, (buffer*5)+2, buffer*2-buffer/3);
			//Resize items based on what needs displayed
			if(displayType==0) {
				codeScrollPanel.setVisible(false);
				scrollPanel.setVisible(true);
				errorArea.setBounds(0, 0, width,height);
				scrollPanel.setBounds(startPosX, startPosY, width,height);
				resizeErrorIcon(true,iconSize);
				resizeCodeIcon(false,iconSize);
			}
			else if(displayType==1) {
				scrollPanel.setVisible(false);
				codeScrollPanel.setVisible(true);
				codeArea.setBounds(0, 0, width,height);
				codeScrollPanel.setBounds(startPosX, startPosY, width,height);
				resizeErrorIcon(false,iconSize);
				resizeCodeIcon(true,iconSize);
			}
			else if(displayType==2) {
				codeScrollPanel.setVisible(true);
				scrollPanel.setVisible(true);
				codeArea.setBounds(0, 0, (width)/2,height);
				codeScrollPanel.setBounds(startPosX, startPosY, (width)/2,height);
				errorArea.setBounds(0, 0, (width)/2,height);
				scrollPanel.setBounds(startPosX+width/2, startPosY, (width)/2,height);
				resizeErrorIcon(true,iconSize);
				resizeCodeIcon(true,iconSize);
			}
		}
		else if(fileTitle.getText()==null || forced) {
			setVisible(false);
			textEmptyNotif.setFont(new Font(Window.titleFont.getFamily(), Font.BOLD, (int) windowSize.getHeight()/24));
			textEmptyNotif.setBounds(startPosX+buffer*2, (int) windowSize.getHeight()*3/4-36, width, (int) windowSize.getHeight()/3);
		}
		if(forced) {
			setVisible(false);
			errorIcon.setVisible(false);
		}
	}
	
	/**
	 * Resize the error icon (to show the the code displayed is the error text)
	 * @param visible if it should be visible
	 * @param iconSize the size of the icon
	 */
	public void resizeErrorIcon(boolean visible, int iconSize) {
		errorIcon.setVisible(visible);
		if(visible) {
			if(displayType==0) {
				errorIcon.setBounds((int) background.getX(), (int) (background.getY()-iconSize), iconSize, iconSize);
			}else {
				errorIcon.setBounds((int) scrollPanel.getX(), (int) (scrollPanel.getY()-1-iconSize), iconSize, iconSize);
			}
			//Resize the image to match the size of the button
			Sidebar.resizeImage(errorIcon, ((ImageIcon) iconArray[0]).getImage(), iconSize);	
		}
	}
	
	/**
	 * Resizes the code icon that illustrates which text tab shows the code
	 * @param visible if it should be visible
	 * @param iconSize the size of the icon
	 */
	public void resizeCodeIcon(boolean visible, int iconSize) {
		codeIcon.setVisible(visible);
		if(visible) {
			if(displayType==1) {
				codeIcon.setBounds((int) background.getX(), (int) (background.getY()-iconSize), iconSize, iconSize);
			}else {
				codeIcon.setBounds((int) (codeScrollPanel.getX()+codeScrollPanel.getWidth())-iconSize, (int) (codeScrollPanel.getY()-1-iconSize), iconSize, iconSize);
			}
			Sidebar.resizeImage(codeIcon, ((ImageIcon) iconArray[1]).getImage(), iconSize);	
		}
	}
	
	/**
	 * Resize the button that you click to display the error text
	 * @param buffer the size in bits that should be between elements
	 * @param width the width of the button
	 */
	public void resizeErrorTab(int buffer, int width) {
		int buttonWidth = buffer*3/2;
		errorTab.setBounds(width-(buttonWidth*2+buffer*3)-buffer/2, buffer*7/8, buttonWidth, buttonWidth);
		Sidebar.resizeImage(errorTab, ((ImageIcon) iconArray[0]).getImage(), buttonWidth);	
	}
	
	/**
	 * Resize the button that you click to display the code text
	 * @param buffer the size in bits that should be between elements
	 * @param width the width of the button
	 */
	public void resizeCodeTab(int buffer, int width) {
		int buttonWidth = buffer*3/2;
		codeTab.setBounds(width-(buttonWidth+buffer*3)-buffer/4, buffer*7/8, buttonWidth, buttonWidth);
		Sidebar.resizeImage(codeTab, ((ImageIcon) iconArray[1]).getImage(), buttonWidth);	
	}
	
	/**
	 * Resize the button that you click to display both the code and the errors
	 * @param buffer the size in bits that should be between elements
	 * @param width the width of the button
	 */
	public void resizeSplitTab(int buffer, int width) {
		int buttonWidth = buffer*3/2;
		splitTab.setBounds(width-buffer*3, buffer*7/8, buttonWidth, buttonWidth);
		Sidebar.resizeImage(splitTab, ((ImageIcon) iconArray[2]).getImage(), buttonWidth);	
	}
	
	/**
	 * sets the text of the error tab
	 * @param theCode the list of text to be displayed
	 */
	public void setErrorText(List<String> theCode) {
		errorArea.setText("");
		
		setVisible(true);
		f.resizeCodeDisplay(false);
		
		String entireThing = getHTMLForm(theCode);
		
		//System.out.println(entireThing);
		errorArea.setText(entireThing);
		errorArea.setCaretPosition(0);
	}
	
	/**
	 * sets the text of the error tab
	 * @param theCode the text to be displayed
	 */
	public void setErrorText(String theCode) {
		setVisible(true);
		f.resizeCodeDisplay(false);

		//System.out.println(entireThing);
		errorArea.setText(theCode);
		errorArea.setCaretPosition(0);
	}
	
	/**
	 * sets the text of the code tab
	 * @param theCode the list of text to be displayed
	 */
	public void setCodeText(List<String> theCode) {
		codeArea.setText("");
		
		setVisible(true);
		f.resizeCodeDisplay(false);
		
		String entireThing = getHTMLForm(theCode);
		
		//System.out.println(entireThing);
		codeArea.setText(entireThing);
		codeArea.setCaretPosition(0);
	}
	
	/**
	 * Converts the inputted text to formatted html
	 * @param theCode the list of strings you want in the HTML style
	 * @return the html (WARNING! VERY LARGE STRING!)
	 */
	public static String getHTMLForm(List<String> theCode) {
		
		int listSize = theCode.size();
		int counter = 2;
		//Took hex convertion formula from:
		//https://stackoverflow.com/questions/3607858/convert-a-rgb-color-value-to-a-hexadecimal-string
		String textColour = String.format("#%02x%02x%02x", Window.textColor.getRed(), Window.textColor.getGreen(), Window.textColor.getBlue());  
		//String textColour = "#E6E6E6";
		String backgroundColour = String.format("#%02x%02x%02x", Window.backColor.getRed(), Window.backColor.getGreen(), Window.backColor.getBlue());  
		//String backgroundColour = "#262E38";
		int textSize = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()/60;

		//Add the intro HTML
		String entireThing = "<html>"
				+ "<head>"
				+ "<style>"
				+ "body {"
				+ "  color:"+textColour+";font-size: "+textSize+"px;background-color:"+backgroundColour+";font-family:\""+/*Window.bodyFont.getFamily()*/"Consolas"+"\";"
				+ "}"
//				+ "h1 {"
//				+ "  color: maroon;"
//				+ "  margin-left: 40px;"
//				+ "}"
				+ "div {"
				+ "	  margin-left: 20px;color:"+textColour+";font-size: 12px;display:inline-block;"
				+ "}"
				+ "</style>"
				+ "</head>"
				+ "<body>";
		
		//For if blank
		if(theCode.isEmpty()) {
			entireThing += "Nothing wrong here";
			return entireThing;
		}
		
		//Start of adding
		entireThing += "1:"+getStartTab(1);
		
		//For every line
		for(int i =0;i<listSize;i++) {
			String theLine = theCode.get(i);
			//Remove < and > from code so it can be read properly
			if(theLine.contains("<") || theLine.contains(">")) {
				theLine = convertBracketsToHTMLForm(theLine);
			}else if(theLine.contains(" ")) {
				theLine = replaceSpacesWithHTMLFriendly(theLine);
			}
			//Convert tabs to html form
			if(theLine.contains("\t")||theLine.contains("	")) {
				int tabNum = countTabs(theLine);
				for(int myI = 0;myI<tabNum;myI++) {
					entireThing+=CodeCheckerConstants.htmlTabCharacter;
				}
			}
			
			//Add the line
			entireThing += theLine;
			//Take a new line
			if(theLine.contains("\n") || theLine.contains("<br>")){
				entireThing+="<br>";
				//Add line counter
				entireThing +=counter+":";
				entireThing+=getStartTab(counter);
				counter++;
			}
		}
		return entireThing;
	}
	
	/**
	 * Method to get the amount of spaces after a line number to have them all line up
	 * @param counter the line number
	 * @return the number of spaces
	 */
	public static String getStartTab(int counter) {
		//INTEGER ON THIS LINE DETERMINES WHERE THE CODE STARTS
		int tempSize = 5 - Integer.toString(counter).length();
		String mySpaces = "";
		for(int myI =0;myI<tempSize;myI++) {
			mySpaces+="&nbsp;";
		}
		return mySpaces;
	}
	
	/**
	 * counts the number of tabs in a line and returns it
	 * @param theLine the string you want the number of tabs of
	 * @return the number of tabs
	 */
	public static int countTabs(String theLine) {
		int lineLength = theLine.length();
		int tabCount = 0;
		boolean slashFound = false;
		char curChar;
		char[] charArray = theLine.toCharArray();
		
		//For every character
		for(int i =0;i<lineLength;i++) {
			curChar = charArray[i];
			//Check if it is a slash
			if(curChar=='\\') {
				slashFound = true;
				continue;
			}
			//Check if it is a t following a slash or just a tab character
			if((curChar=='t' && slashFound) || curChar =='	') {
				tabCount++;
				slashFound = false;
			}
		}
		return tabCount;
	}
	
	/**
	 * replaces the spaces in lines with HTML friendly spaces that wont automatically take a new line
	 * @param theLine the line getting the spaces replaced
	 * @return the line with the spaces replaced
	 */
	public static String replaceSpacesWithHTMLFriendly(String theLine) {
		char[] charLine = theLine.toCharArray();
		String myChar;
		String newLine = "";
		int lineLength = theLine.length();
		
		//For every character
		for(int i =0;i<lineLength;i++) {
			myChar = Character.toString(charLine[i]);
			if(myChar.equals(" ")) {
				myChar = "&nbsp;";
			}
			newLine+=myChar;
		}
		return newLine;
	}
	
	/**
	 * Converts the angular brackets in a string to characters that display properly in html
	 * @param theLine the line to be converted
	 * @return the line with angular brackets that can be displayed
	 */
	public static String convertBracketsToHTMLForm(String theLine) {
		char[] charLine = theLine.toCharArray();
		String myChar;
		String newLine = "";
		int lineLength = theLine.length();
		
		//^ = Exclusive Or (XOR)
		//Checking if < or > are being used independently. eg as less than or greater than operators
		if(theLine.contains("<") ^ theLine.contains(">")) { 
			//For every character
			for(int i =0;i<lineLength;i++) {
				myChar = Character.toString(charLine[i]);
				if(myChar.equals("<")) {
					myChar = CodeCheckerConstants.htmlOpenBracketCharacter;
				}
				if(myChar.equals(">")) {
					myChar = CodeCheckerConstants.htmlCloseBracketCharacter;
				}
				if(myChar.equals(" ")) {
					myChar = "&nbsp;";
				}
				newLine+=myChar;
			}
		}
		//Check if the string between the < > symbols is a html tag, if not replace the symbols
		else {
			//If brackets are open
			boolean open = false;
			//String between the brackets
			String curString = "";
			//pos of brackets opening
			int openOn=0;
			//variable to keep track of how many extra characters I have added to the line
			int newLineAdder=0;
			//for every character in the line
			for(int i =0;i<lineLength;i++) {
				myChar = Character.toString(charLine[i]);
				//For closing brackets
				if(myChar.equals(">")) {
					if(!open) {
						myChar = CodeCheckerConstants.htmlCloseBracketCharacter;
						newLineAdder+=3;
					}
					else {
						//Set to close
						open = false;
						//if it isn't a html tag set the symbols to the html friendly ones
						if(!isHTMLTag(curString)) {
							//System.out.println(newLine);
							newLine =  newLine.substring(0,openOn) + CodeCheckerConstants.htmlOpenBracketCharacter + newLine.substring(openOn+1, i+newLineAdder);
							myChar = CodeCheckerConstants.htmlCloseBracketCharacter;
							newLineAdder+=6;
							//System.out.println(newLine);
						}
					}
				}
				//if open add the char to the inside brackets word
				if(open) {
					curString += myChar;
				}else if(myChar.equals(" ")) {
					myChar = "&nbsp;";
					newLineAdder+=5;
				}
				//Upon opening brackets
				if(myChar.equals("<")) {
					if(open) {
						//System.out.println(newLine);
						newLine =  newLine.substring(0,openOn) + CodeCheckerConstants.htmlOpenBracketCharacter + newLine.substring(openOn+1);
						//System.out.println(newLine);
						newLineAdder+=3;
					}
					open = true;
					curString = "";
					openOn = i+newLineAdder;
				}
				//If just closed don't add the letter to the word
				newLine+=myChar;
			}
		}
		return newLine;
	}
	
	/**
	 * Checks if a string inputted is a html tag
	 * @param myTag the string to be checked 
	 * @return if it is a tag
	 */
	public static boolean isHTMLTag(String myTag) {
		if(myTag.contains("/")||myTag.contains("\"")||myTag.contains("=")||myTag.contains(":")||myTag.equals("em")||myTag.equals("/em")) {
			return true;
		}
		return false;
	}
	
	/**
	 * sets the title displayed above the bodies of text
	 * @param title the new title
	 */
	public void setTitle(String title) {
		if(title.contains(".")) {
			fileTitle.setForeground(Window.textColor);
		}else {
			fileTitle.setForeground(Window.secondaryColor);
		}
		fileTitle.setText(title);
	}
	
	/**
	 * simple getter
	 * @return the title 
	 */
	public String getTitle() {
		return fileTitle.getText();
	}
	
	/**
	 * Simple getter
	 * @return the code in the error area
	 */
	public String getErrorText() {
		return errorArea.getText();
	}
}
