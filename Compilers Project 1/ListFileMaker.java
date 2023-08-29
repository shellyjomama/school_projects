/*
 * Created on Sep 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.io.*;
/**
 * @author shellyjo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ListFileMaker {
	File listFile;
	PrintWriter outputFile;

	public static void main(String[] args) {
	}

	public ListFileMaker(File listFile) {
		this.listFile = listFile;
		try {

			listFile.createNewFile(); //create the file if it does not exist
			outputFile = new PrintWriter(new FileWriter(listFile));
			
		} catch (IOException e) {
			System.out.println("Problems in ListFileMaker constructor" + e);
		}
	}

	void printLine(String str, int lineNum) {
		outputFile.flush() ;
		outputFile.println(lineNum + ".\t" + str);
		
	}
	
	void closeListFile(){
		
		outputFile.close();
	}
	
	void printError(String error) {
		outputFile.println("\t" + error);

	}
}
