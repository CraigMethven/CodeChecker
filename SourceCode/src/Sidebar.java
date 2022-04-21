import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;  

/**
 * Class to store the components of the side bar
 * @author Craig
 *
 */
public class Sidebar {
	private Window f;
	private int sidebarWidth;
	private int topBottomBarSize;
	private int screenBottom;
	private int buffer;
	//0 = file, 1 = options, 2 = advOptions
	private int tabSelected = 0;
	//Buttons
	private JButton download;
	private JButton fileTab;
	private JButton checkListTab;
	private JButton advOptionsListTab;
	private JButton check;
	//2 = download; 3 = file; 4 = check mark
	private Icon iconArray[];
	//Panel
	private JPanel background;
	private JPanel highlightSelected;
	private fileTreePanel fileDisplay;
	private OptionsPanel optionDisplay;
	private AdvOptionsPanel advOptionDisplay;
	//For the collapsing feature
	private boolean collapsed = false;
	private JButton collapse;

	/**
	 * Basic constructor
	 * @param tempFrame the window housing this sidebar
	 */
	public Sidebar(Window tempFrame) {
		f = tempFrame;
		init();
	}
	
	/**
	 * Initialises the elements
	 */
	private void init() {
		//Init the icons
		iconArray = new ImageIcon[5];
		//String tempFilePath = System.getProperty("java.class.path");
		BufferedImage b[] = new BufferedImage[5];
		//Get all the icons
		try {
//			b[1] = Window.toBufferedImage(ImageIO.read(new File(tempFilePath+"/images/download.png")));
//			b[2] = Window.toBufferedImage(ImageIO.read(new File(tempFilePath+"/images/files.png")));
//			b[3] = Window.toBufferedImage(ImageIO.read(new File(tempFilePath+"/images/check.png")));
//			b[4] = Window.toBufferedImage(ImageIO.read(new File(tempFilePath+"/images/cog.png")));
			
//			b[1] = Window.toBufferedImage(Main.getImage("/images/download.png"));
//			b[2] = Window.toBufferedImage(Main.getImage("/images/files.png"));
//			b[3] = Window.toBufferedImage(Main.getImage("/images/check.png"));
//			b[4] = Window.toBufferedImage(Main.getImage("/images/cog.png"));
			
			b[1] = Main.getBufferedImage("/download.png");
			b[2] = Main.getBufferedImage("/files.png");
			b[3] = Main.getBufferedImage("/check.png");
			b[4] = Main.getBufferedImage("/cog.png");
			//Recolour each of the icons
			for(int i =1;i<5;i++) {
				Window.colourIcon(b[i], Window.secondaryColor.getRGB());	
				iconArray[i] = new ImageIcon((Image) b[i],"An icon");
			}
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			System.out.println("Problem reading in the images for the sidebar");
		}	

		//For buttons		
		//For downloading the errors
		download = initButton(iconArray[1]);
		download.setForeground(Window.secondaryColor);
		download.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	//If there are files, display the download window
		    	if(!f.getFileList().isEmpty()) {
		    		new DownloadWindow(f);
		    	}
		    	//Error message if no files have been uploaded
		    	else {
		    		JOptionPane.showMessageDialog(null, "Please upload the files that you want error checked");
		    	}
		    }
		});
		download.setToolTipText("<html><body style = \"font-size: 11px;\"><b>Download Results</b><br>Open the download menu to get the results in a shareable format.");
		
		//For switching to the file tab
		fileTab = initButton(iconArray[2]);
		fileTab.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        changeSelectedTab(0);
		    }
		});
		fileTab.setToolTipText("<html><body style = \"font-size: 11px;\"><b>File Menu</b>");
		
		//For switching for the error check list tab
		checkListTab = initButton(iconArray[3]);
		checkListTab.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        changeSelectedTab(1);
		    }
		});
		checkListTab.setToolTipText("<html><body style = \"font-size: 11px;\"><b>Option Menu</b>");
		
		//For switching for the adv options tab
		advOptionsListTab = initButton(iconArray[4]);
		advOptionsListTab.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        changeSelectedTab(2);
		    }
		});
		advOptionsListTab.setToolTipText("<html><body style = \"font-size: 11px;\"><b>Advanced Option Menu</b>");
		
		//Button to perform the checks
		check = initButton("Start Checks");
		check.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	//Reruns all of the checks
            	int input = JOptionPane.showConfirmDialog(null, "Are you sure that you want to rerun the checks?\nThis will reset any errors that you have added manually\nConsider downloading the current results before hand","Rerun Checks",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            	if(input == 0) {
            		setCheckButtonText(false);
            		f.rerunChecks();
            	}
		    }
		});
		check.setBorder(BorderFactory.createRaisedBevelBorder());
		check.setToolTipText("<html><body style = \"font-size: 11px;\"><b>Recheck Code</b><br>Rechecks the code. Useful if you change options.");
		
		//Button for collapsing or expanding the sidebar
		collapse = initButton("<");
		collapse.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
				collapsedAction();
		    }
		});
		//upon hover change colours
		collapse.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	if(collapsed) {
		    		collapse.setBackground(Window.otherBackColor);
		    	}else {
		    		collapse.setBackground(Window.backColor);
		    	}
		        
		    }
		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	if(collapsed) {
		    		collapse.setBackground(Window.backColor);
		    	}
		    	else {
		    		collapse.setBackground(Window.otherBackColor);
		    	}
		    	
		    }
		});
		collapse.setToolTipText("<html><body style = \"font-size: 11px;\">Toggle visibility of the sidebar.");
		
		
		//For the side panels sections
		fileDisplay = new fileTreePanel(f);
		optionDisplay = new OptionsPanel(f);
		advOptionDisplay = new AdvOptionsPanel(f);
		
		//Decorative Panels
		background = f.getNewPanel();
		highlightSelected = f.getNewPanel();

		//Initialise size and mouse over events for the tabs
		changeSelectedTab(0);
		checkListTab.setBackground(Window.backColor);
		checkListTab.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	evt.getComponent().setBackground(Window.otherBackColor);
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	evt.getComponent().setBackground(Window.backColor);
		    }
		});
		advOptionsListTab.setBackground(Window.backColor);
		advOptionsListTab.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	evt.getComponent().setBackground(Window.otherBackColor);
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	evt.getComponent().setBackground(Window.backColor);
		    }
		});
	}
	
	/**
	 * Sets the visibility of the sidebar
	 * @param ifVisible if sidebar should be seen
	 */
	public void setVisibility(boolean ifVisible) {
		download.setVisible(ifVisible);
		check.setVisible(ifVisible);
		fileTab.setVisible(ifVisible);
		checkListTab.setVisible(ifVisible);
		advOptionsListTab.setVisible(ifVisible);
		background.setVisible(ifVisible);
		highlightSelected.setVisible(ifVisible);
		//Only display the menu that's selected
		if(tabSelected==0) {
			fileDisplay.setVisibility(ifVisible);
		}else if(tabSelected==1) {
			optionDisplay.setVisibility(ifVisible);
		}else if(tabSelected==2) {
			advOptionDisplay.setVisibility(ifVisible);
		}
		//always display the collapse button
		collapse.setVisible(true);
	}
	
	/**
	 * Gets a new button with text inside
	 * @param insideText the text that the button holds
	 * @return a new initialised button
	 */
	public JButton initButton(String insideText) {
		JButton tempB = f.getNewButton();
		tempB.setText(insideText);
		return tempB;
	}
	
	/**
	 * get a new button with an icon
	 * @param i
	 * @return
	 */
	public JButton initButton(Icon i) {
		JButton tempB = f.getNewButton();
		tempB.setIcon(i);
		return tempB;
	}
	
	/**
	 * changes the tab selected
	 * @param input tree if file tab is selected, false for check tab
	 */
	public void changeSelectedTab(int input) {
		//If tab is already selected then do nothing
		if (tabSelected == input) {
			return;
		}
		else {
			//2 temp buttons to set background colours of the selected and unselected button
			JButton selected = fileTab;
			JButton prevSelected = checkListTab;
			
			if(input==0) {
				//Display file tab
				fileDisplay.setVisibility(true);
				optionDisplay.setVisibility(false);
				advOptionDisplay.setVisibility(false);
				selected = fileTab;
			}else if(input==1) {
				//Display checks tab
				fileDisplay.setVisibility(false);
				optionDisplay.setVisibility(true);
				advOptionDisplay.setVisibility(false);
				selected = checkListTab;
			}else if(input==2) {
				//Display checks tab
				fileDisplay.setVisibility(false);
				optionDisplay.setVisibility(false);
				advOptionDisplay.setVisibility(true);
				selected = advOptionsListTab;
			}
			
			if(tabSelected==0) {
				prevSelected = fileTab;
			}
			else if(tabSelected==1) {
				prevSelected = checkListTab;
			}
			else if(tabSelected==2) {
				prevSelected = advOptionsListTab;
			}
			
			tabSelected = input;
			//Set colors of buttons
			prevSelected.setBackground(Window.backColor);
			selected.addMouseListener(new java.awt.event.MouseAdapter() {
			    public void mouseEntered(java.awt.event.MouseEvent evt) {
			    	evt.getComponent().setBackground(Window.otherBackColor);
			    	//selected.setBackground(Window.backColor);
			    }

			    public void mouseExited(java.awt.event.MouseEvent evt) {
			    	evt.getComponent().setBackground(Window.otherBackColor);
			    	//selected.setBackground(Window.otherBackColor);
			    }
			});
			prevSelected.addMouseListener(new java.awt.event.MouseAdapter() {
			    public void mouseEntered(java.awt.event.MouseEvent evt) {
			    	evt.getComponent().setBackground(Window.otherBackColor);
			    	//prevSelected.setBackground(Window.otherBackColor);
			    }

			    public void mouseExited(java.awt.event.MouseEvent evt) {
			    	evt.getComponent().setBackground(Window.backColor);
			    	//prevSelected.setBackground(Window.backColor);
			    }
			});
		}
		//Move the panel highlighting the selected tab
		highlightResize();
	}
	
	/**
	 * Resize all elements of the sidebar
	 */
	public void resize() {
		//if collapsed don't resize anything
		if(!collapsed) {
			setWidth();
			screenBottom = f.getFrame().getHeight()-36;
			buffer = sidebarWidth/20;
			setBarSizes();
			downloadResize();
			fileTabResize();
			checkListTabResize();
			advOptionsTabResize();
			checkResize();
			backgroundResize();
			highlightResize();
			fileDisplay.resize(sidebarWidth, topBottomBarSize, screenBottom, buffer);
			optionDisplay.resize(sidebarWidth, topBottomBarSize, screenBottom, buffer);
			advOptionDisplay.resize(sidebarWidth, topBottomBarSize, screenBottom, buffer);
		}
		//always resize the collapse button
		collapseResize();
	}
	
	/**
	 * sets the width of the side bar
	 */
	public void setWidth() {
		//If side bar is collapsed 
		if(collapsed) {
			sidebarWidth =0;
			return;
		}
		//set width to a 3rd of the screens height
		sidebarWidth = f.getFrame().getHeight()/3;
		//If it is taking up more than a quarter of the screen
		if(sidebarWidth > f.getFrame().getWidth()/4) {
			//set it to only take up a quarter
			sidebarWidth = f.getFrame().getWidth()/4;
		}
	}
	
	/**
	 * calculates how tall the top and bottom bars should be
	 */
	public void setBarSizes() {
		topBottomBarSize = f.getFrame().getHeight()/12;
		//set min size
//		if(topBottomBarSize<20) {
//			topBottomBarSize=20;
//		}
	}
	
	/**
	 * Resizes the download button
	 */
	public void downloadResize() {
		download.setBounds(sidebarWidth-(topBottomBarSize-(buffer))/2-buffer, screenBottom-topBottomBarSize-buffer, (topBottomBarSize-(buffer))/2, topBottomBarSize);
		Image tempImage = ((ImageIcon) iconArray[1]).getImage();
		ImageIcon tempII = new ImageIcon(tempImage.getScaledInstance((topBottomBarSize-(buffer))/2, (topBottomBarSize-(buffer))/2,  java.awt.Image.SCALE_SMOOTH));
		download.setIcon(tempII);
	}
	
	/**
	 * Resizes the file tab button
	 */
	public void fileTabResize() {
		fileTab.setBounds(buffer, buffer, topBottomBarSize-buffer, topBottomBarSize-buffer);
		resizeImage(fileTab, ((ImageIcon) iconArray[2]).getImage(), topBottomBarSize-(2*buffer));	
	}
	
	/**
	 * Resizes the list of errors tab button
	 */
	public void checkListTabResize() {
		checkListTab.setBounds(topBottomBarSize+buffer, buffer, topBottomBarSize-buffer, topBottomBarSize-buffer);
		resizeImage(checkListTab, ((ImageIcon) iconArray[3]).getImage(), topBottomBarSize-(2*buffer));	
	}
	
	/**
	 * Resizes the adv options tab button
	 */
	public void advOptionsTabResize() {
		advOptionsListTab.setBounds(topBottomBarSize*2+buffer, buffer, topBottomBarSize-buffer, topBottomBarSize-buffer);
		resizeImage(advOptionsListTab, ((ImageIcon) iconArray[4]).getImage(), topBottomBarSize-(2*buffer));	
	}
	
	/**
	 * Resizes the collapse button
	 */
	public void collapseResize() {
		int myBuffer = f.getFrame().getHeight()/60;
		int buttonSize = myBuffer*7/4;
		collapse.setFont(new Font(Window.bodyFont.getFamily(), Font.PLAIN, buttonSize));
		if(collapsed) {
			collapse.setBounds(0,/*topBottomBarSize+myBuffer*2/3*/0,myBuffer,myBuffer);
			collapse.setText(">");
		}else {
			collapse.setBounds(sidebarWidth-buttonSize-myBuffer/4,topBottomBarSize+myBuffer*2/3,myBuffer,myBuffer);
			collapse.setText("<");
		}
		
	}
	
	/**
	 * Resizes an image to fit onto a button and sets it onto the button
	 * @param b the button to get the image
	 * @param myIcon the icon to put onto the button
	 * @param size the size of the image/button
	 */
	public static void resizeImage(JButton b, Image myIcon, int size) {
		//Window.colourIcon(Window.toBufferedImage(myIcon),Window.secondaryColor.getRGB());
		ImageIcon tempII = new ImageIcon(myIcon.getScaledInstance(size, size,  java.awt.Image.SCALE_SMOOTH));
		b.setIcon(tempII);
	}
	
	/**
	 * Resizes an image to fit onto a label and sets it onto the label
	 * @param b the label to get the image
	 * @param myIcon the icon to put onto the button
	 * @param size the size of the image/button
	 */
	public static void resizeImage(JLabel b, Image myIcon, int size) {
		//Window.colourIcon(Window.toBufferedImage(myIcon),Window.secondaryColor.getRGB());
		ImageIcon tempII = new ImageIcon(myIcon.getScaledInstance(size, size,  java.awt.Image.SCALE_SMOOTH));
		b.setIcon(tempII);
	}

	/**
	 * resizes the check button
	 */
	public void checkResize() {
		check.setBounds(buffer, screenBottom-topBottomBarSize-buffer, sidebarWidth-(topBottomBarSize/2)-2*buffer, topBottomBarSize);
		check.setFont(new Font(Window.titleFont.getFamily(), Font.BOLD, topBottomBarSize*2/5));
	}
	
	/**
	 * resizes the background panel
	 */
	public void backgroundResize() {
		background.setBounds(buffer/2,topBottomBarSize+buffer/2,(sidebarWidth-buffer),screenBottom-buffer-topBottomBarSize);
		//System.out.println(background.getHeight());
	}
	
	/**
	 * Resizes the highlight area to reflect the tab selected
	 */
	public void highlightResize() {
		//If file tab selected
		if(tabSelected==0) {
			highlightSelected.setBounds(buffer/2,buffer/2,topBottomBarSize,topBottomBarSize);
		//If option tab selected
		}else if(tabSelected==1) {
			highlightSelected.setBounds(topBottomBarSize+buffer/2,buffer/2,topBottomBarSize,topBottomBarSize);
		}else if(tabSelected==2) {
			highlightSelected.setBounds(topBottomBarSize*2+buffer/2,buffer/2,topBottomBarSize,topBottomBarSize);
		}
	}
	
	/**
	 * Gets the width of the sidebar
	 * @return the size in pixels
	 */
	public int getWidth() {
		return sidebarWidth;
	}
	
	/**
	 * Collapses/uncollapses the sidebar
	 */
	public void collapsedAction() {
		collapsed = !collapsed;
		setWidth();
		setVisibility(!collapsed);
		collapseResize();
		f.resize(true);
	}
	
	/**
	 * gets the file store that is represented by a tree node
	 * @param treeName the tree which the node is stored in
	 * @param myNode the node that we want the file store that represents it
	 * @return the represented file store
	 */
	public FileStore getFileStoreFromNode(String treeName, DefaultMutableTreeNode myNode) {
		return fileTreePanel.getFileStoreFromNode(f,treeName,myNode);
	}
	
	/**
	 * gets the folder store that is represented by a tree node
	 * @param treeName the tree which the node is stored in
	 * @param myNode the node that we want the folder store that represents it
	 * @return the represented folder store
	 */
	public FolderStore getFolderStoreFromNode(String treeName, DefaultMutableTreeNode myNode) {
		return fileTreePanel.getFolderStoreFromNode(f, treeName,myNode);
	}
	
	/**
	 * Deletes a tree from being stored
	 * @param myTree the tree to be deleted
	 */
	public void deleteTree(JTree myTree) {
		fileDisplay.deleteTree(myTree);
	}
	
	/**
	 * gets all of the trees being displayed
	 * @return the list of trees
	 */
	public List<JTree> getTrees(){
		return fileDisplay.getTrees();
	}
	
	/**
	 * Simple getter
	 * @return buffer variable
	 */
	public int getBuffer() {
		return buffer;
	}
	
	/**
	 * Sets the text of the check button to be highlighted when it should be rerun
	 * @param highlight if it should be highlighted
	 */
	public void setCheckButtonText(boolean highlight) {
		if(highlight) {
			check.setText("Rerun Checks");
			check.setForeground(Window.secondaryColor);
		}
		else {
			check.setText("Start Checks");
			check.setForeground(Window.textColor);
		}
		
	}

}
