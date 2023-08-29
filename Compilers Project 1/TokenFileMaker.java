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
public class TokenFileMaker {

	File tokenFile;
	PrintWriter outputFile;

	public static void main(String[] args) {
	}

	public TokenFileMaker(File tokenFile) {
		this.tokenFile = tokenFile;
		try {

			tokenFile.createNewFile(); //create the file if it does not exist
			outputFile = new PrintWriter(new FileWriter(tokenFile));
			outputFile.flush() ;
			
		} catch (IOException e) {
			System.out.println("Problems in ListFileMaker constructor" + e);
		}
	}

	void printToken(Token t) {
		outputFile.flush();
		outputFile.println(t.getLineNum() + "\t" + t.getValue() + "\t\t" + t.getType() + "\t" + t.getAttribute());
	}//end of printToken method
	
	void closeTokenFile(){
		
		outputFile.close();
	}//end of closeTokenFile method
}//end of TokenFileMaker class
