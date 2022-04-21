import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

public class AdvOptionsPanel {
	private Window f;
	private JTextArea backgroundPanel;
	private JScrollPane myPanel;
	private JCheckBox darkMode;
	private List<JLabel> myLabels;
	private List<JFormattedTextField> myFields;
	private JLabel wordSearchLabel;
	private JTextArea wordSearchArea;
	private JScrollPane wordSearchScroll;
	
	/**
	 * basic constructor
	 * @param temp the window that this panel is to be displayed on
	 */
	public AdvOptionsPanel(Window temp){
		f = temp;
		init();
	}
	
	/**
	 * initialises all of the components
	 */
	public void init(){
		//The back panel
		backgroundPanel = new JTextArea();
		backgroundPanel.setVisible(false);
		backgroundPanel.setBackground(Window.backColor);
		backgroundPanel.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		//backgroundPanel.setBorder(null);
		backgroundPanel.setEditable(false);
		backgroundPanel.setLayout(null);
		backgroundPanel.setHighlighter(null);
		//The scroll pane
		myPanel = f.getNewScrollPane(backgroundPanel);
		backgroundPanel.setAutoscrolls(true);
		myPanel.setViewportView(backgroundPanel);
		myPanel.setVisible(false);
		
		darkMode = new JCheckBox();
		initContainer(darkMode);
		darkMode.setSelected(AdvOptions.darkMode);
		darkMode.setText("<html><body>Dark Mode");
		darkMode.setToolTipText("<html><body style = \"font-size: 11px;\"><b>Dark Mode:</b><br>Changes the colouring of the GUI.<br>If selected then dark mode is enabled else it will be in dark mode<br><b>Restart required for change to take action</b>");
		darkMode.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        AdvOptions.darkMode = darkMode.isSelected();
		    }
		});
		
		myLabels = new Vector<JLabel>();
		myFields = new Vector<JFormattedTextField>();
		
		//Try catch incase fields don't exist
		try {
			//For main method size
			setUpIntInput(1,100,"<html><body>Max Main Method Size:","<html><body style = \"font-size: 11px;\"><b>Main Method Length:</b><br>The maximum number of lines long the main method can be (excluding comments and blank space) before it flags an error.",AdvOptions.mainMethodMaxLength,AdvOptions.class.getField("mainMethodMaxLength"));
			//For method size
			setUpIntInput(5,100,"<html><body>Max Method Size:","<html><body style = \"font-size: 11px;\"><b>Method Length:</b><br>The maximum number of lines long a method can be before it flags an error.",AdvOptions.maxMethodLength,AdvOptions.class.getField("maxMethodLength"));
			//For the minimum size of methods to compare
			setUpIntInput(1,100,"<html><body>Min Size of Methods Compared:","<html><body style = \"font-size: 11px;\"><b>Min Compare Size:</b><br>The minimum size a method (excluding comments and blank space) can be before it is compared to others.",AdvOptions.minMethodSizeToCompare,AdvOptions.class.getField("minMethodSizeToCompare"));
			//For the % of similarities needed for methods to be marked as similar
			setUpIntInput(5,100,"<html><body>Compare Percentage to Flag Error:","<html><body style = \"font-size: 11px;\"><b>Error Percentage:</b><br>The minimum percentage of similarity that will cause an error to be flagged.",AdvOptions.percentOfSimilarities, AdvOptions.class.getField("percentOfSimilarities"));
			//for the number of statements needed for if/elses to be marked as large
			setUpIntInput(3,20,"<html><body>Number of If/Thens:","<html><body style = \"font-size: 11px;\"><b>Number of Elses:</b><br>The min number of elses needed to flag an error.",AdvOptions.numOfElseNeededToBeConsideredLarge, AdvOptions.class.getField("numOfElseNeededToBeConsideredLarge"));
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		wordSearchLabel = new JLabel();
		initContainer(wordSearchLabel);
		wordSearchLabel.setText("<html><body>Enter phrases that you want to search for in the code.<br><em>Take a new line for each:</em>");
		
		wordSearchArea = new JTextArea();
		initContainer(wordSearchArea);
		wordSearchScroll= f.getNewScrollPane(wordSearchArea);
		backgroundPanel.setAutoscrolls(true);
		wordSearchScroll.setViewportView(wordSearchArea);
		backgroundPanel.add(wordSearchScroll);
		wordSearchArea.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		wordSearchArea.setBackground(Window.otherBackColor);
		wordSearchArea.setCaretColor(Window.secondaryColor);
		wordSearchArea.getDocument().addDocumentListener(new DocumentListener() {
			/**
			 * Method to save the input of search terms if not blank
			 */
			public void updateChecker() {
				if(!wordSearchArea.getText().isBlank()) {
					f.setSidebarCheckButtonHighlight(true);
					AdvOptions.searchTerms = new Vector<String>();
					String input = wordSearchArea.getText();
					//System.out.println("Input: "+input);
					String[] tempArray= input.split("\n");
					int sizeOfArray = tempArray.length;
					//System.out.println("inputs:");
					for(int i = 0;i<sizeOfArray;i++) {
						if(!tempArray[i].isBlank()) {
							AdvOptions.searchTerms.add(tempArray[i].strip());
							//System.out.println(tempArray[i]);
						}
					}
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				updateChecker();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				updateChecker();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				updateChecker();
			}
	    });
	}
	
	/**
	 * make sure the current values are saved 
	 */
	public void saveValues() {
		try {
			AdvOptions.mainMethodMaxLength = Integer.parseInt(myFields.get(0).getText()); 
			AdvOptions.maxMethodLength = Integer.parseInt(myFields.get(1).getText());
			AdvOptions.minMethodSizeToCompare = Integer.parseInt(myFields.get(2).getText());
			AdvOptions.percentOfSimilarities=Integer.parseInt(myFields.get(3).getText());
			AdvOptions.numOfElseNeededToBeConsideredLarge = Integer.parseInt(myFields.get(4).getText());;
		}catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	/**
	 * creates and sets default values that for containers that take in an int
	 * @param min the min value the int can be
	 * @param max the max value the int can be
	 * @param labelText the label for the into input
	 * @param toolTip the tool tip for when hovering over the containers
	 * @param startValue the starting value of the int
	 * @param toUpdate the field that changing this field should update
	 */
	public void setUpIntInput(int min, int max, String labelText, String toolTip, int startValue, Field toUpdate) {
		JLabel tempLabel =new JLabel();
		initContainer(tempLabel);
		tempLabel.setText(labelText);
		tempLabel.setToolTipText(toolTip);
		tempLabel.setVisible(true);
		
		NumberFormat numForm = NumberFormat.getInstance();
		NumberFormatter numFormatter = new NumberFormatter(numForm);
		numFormatter.setMinimum(min);
		numFormatter.setMaximum(max);
		numFormatter.setAllowsInvalid(true);
	
		JFormattedTextField tempField = new JFormattedTextField(numFormatter);
		initContainer(tempField);
		tempField.setBorder(BorderFactory.createLineBorder(Window.secondaryColor));
		tempField.setBackground(Window.otherBackColor);
		tempField.setCaretColor(Window.secondaryColor);
		tempField.setValue(startValue);
		tempField.setToolTipText(toolTip);
		tempField.setVisible(true);
		tempField.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				f.setSidebarCheckButtonHighlight(true);
				String input = tempField.getText();
				if(input.isBlank()) {
					return;
				}
				AdvOptions temp = new AdvOptions();
				try {
					int intInput = Integer.parseInt(input);
					if(intInput>=min&&intInput<=max) {
						toUpdate.set(temp,intInput);
						//System.out.println("Updates to "+intInput);
					}
				}catch(NumberFormatException nfe) {
					nfe.printStackTrace();
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	    });
		
		myLabels.add(tempLabel);
		myFields.add(tempField);
	}
	
	/**
	 * sets basic properties for containers
	 * @param myC the container to set properties of
	 */
	public void initContainer(Container myC) {
		myC.setBackground(Window.backColor);
		myC.setForeground(Window.textColor);
		myC.setFont(Window.bodyFont);
		backgroundPanel.add(myC);
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
		}else {
			saveValues();
		}
	}
	
	/**
	 * sets the size of the scroll pane to allow the user to see all of the checkboxes
	 * @param height the height of the scroll area
	 */
	public void setScrollArea(int height) {
		backgroundPanel.setFont(darkMode.getFont());
		//Take a new line for the height of the checkboxes
		//Num of lines tall the box is
		int numOfLines = (height/darkMode.getFont().getSize())*5;
		String tempNewLine="";
		for(int i =0;i<numOfLines;i++) {
			tempNewLine+="\n";
		}
		//Set the text to the multiple line breaks
		backgroundPanel.setText(tempNewLine);
		backgroundPanel.setCaretPosition(0);
	}
	
	/**
	 * gets the height that the label should be based on the length of text
	 * @param myLabel the label to get the height of
	 * @param width the width of the label
	 * @return the height that the box should be
	 */
	public int getLabelHeight(JLabel myLabel, int width) {
		int fontSize = myLabel.getFont().getSize();
		int labelTextSize = myLabel.getText().length();
		int charsPerLine = width/fontSize;
		return labelTextSize/charsPerLine*fontSize;
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
		int height = buffer/8;
		Font labelFont = new Font(Window.bodyFont.getFamily(), Font.PLAIN, buffer*7/5);
		sidebarWidth-=33;
		darkMode.setFont(labelFont);
		darkMode.setBounds(buffer/2, height+buffer,sidebarWidth-buffer*2, buffer*2);
		height+=buffer*3;
		int numOfIntInputs = myLabels.size();
		for(int i = 0;i<numOfIntInputs;i++) {
			myLabels.get(i).setFont(labelFont);
			myLabels.get(i).setBounds(buffer/2, height, sidebarWidth*2/3-buffer/2, getLabelHeight(myLabels.get(i), sidebarWidth*2/3-buffer/2));
			myFields.get(i).setFont(labelFont);
			myFields.get(i).setBounds(sidebarWidth*2/3, height+(myLabels.get(i).getHeight()-labelFont.getSize())/2-labelFont.getSize()/5, (sidebarWidth/3)-(buffer), labelFont.getSize()*7/5);
			height+=myLabels.get(i).getHeight()+buffer/3;
		}
		wordSearchLabel.setFont(labelFont);
		wordSearchLabel.setBounds(buffer/2, height, sidebarWidth-buffer*2, getLabelHeight(wordSearchLabel, sidebarWidth-buffer*2));
		height+=wordSearchLabel.getHeight()-buffer;
		wordSearchArea.setFont(labelFont);
		wordSearchScroll.setBounds(buffer/2,height,sidebarWidth-buffer*2,labelFont.getSize()*28/5);
		wordSearchArea.setBounds(0,0,sidebarWidth-buffer*2,labelFont.getSize()*28/5);
		height+=labelFont.getSize()*28/5+buffer;
		//Set the new height of the scroll pane
		setScrollArea(labelFont.getSize()*28/5+buffer);
	}
}
