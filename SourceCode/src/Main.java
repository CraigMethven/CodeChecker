import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;

/*
 * Class just used to initialise the other classes
 */
public class Main {
	/**
	 * THe starting method. Initialises things
	 * @param args command line arguements
	 */
	public static void main(String[] args) {
		System.out.println("Program Started");
		Main myMain = new Main();
		myMain.mainMenu();
	}
	
	/**
	 * Initialises and starts the GUI window
	 */
	public void mainMenu(){
		Window.getOptions();
		Window.getAdvOptions();
		new Window();
	}
	
	/**
	 * prints the list given
	 * @param myList the list to print
	 */
	public static void printList(List<String> myList) {
		for(int i = 0; i<myList.size(); i++) {
			System.out.println(myList.get(i));
		}
	}
	
	/**
	 * CHECK FOR WRONG CHARACTERS IN FILE NAME
	 * saves a list to a file with each item taking a new line
	 * @param fileName the name of the file to be saved to
	 * @param myList the list to be saved
	 */
	public static void saveList(String fileName, List<String> myList) {
		try {
			FileWriter myWriter = new FileWriter(fileName);
			for(int i = 1; i<myList.size(); i++) {
				myWriter.write(myList.get(i));
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred when writing to the file "+fileName +".");
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads in a file and returns it as a list of strings
	 * @param fileName the filepath of the file being read
	 * @return the entire file
	 */
	public static List<String> getFile(String fileName){
		List<String> tempList = new Vector<String>();
		File tempF = new File(fileName);
		try {
			Scanner myReader = new Scanner(tempF);
			while (myReader.hasNextLine()) {
				tempList.add(myReader.nextLine()+"\n");
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempList;
	}
	
	/**
	 * Method to read in images
	 * Taken from: http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/LoadanImagefromaJARfile.htm
	 * @param imagePath the path of the image
	 * @return the image
	 */
	public static Image getImage(String imagePath) throws NullPointerException {
    	URL imgURL = Main.class.getResource(imagePath);
	    Toolkit tk = Toolkit.getDefaultToolkit();
	    return(tk.getImage(imgURL));
	}
	
	/**
	 * Gets a buffered image from the path inputted
	 * @param imagePath the path to the image
	 * @return the image at that path
	 * @throws NullPointerException if image wasn't found
	 */
	public static BufferedImage getBufferedImage(String imagePath) throws NullPointerException {
		try {
			return ImageIO.read(Main.class.getResourceAsStream(imagePath));
		} catch (IOException e) {
			System.out.println("Error reading in a buffered image");
			e.printStackTrace();
		}
	   throw new NullPointerException();
	}
}
