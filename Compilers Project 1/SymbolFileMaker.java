/*
 * Created on Oct 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.io.*;
import java.util.*;
/**
 * @author shellyjo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SymbolFileMaker {
		File symbolFile;
		PrintWriter outputFile;
		
	public static void main(String[] args) {
	}
	
	public SymbolFileMaker(File symbolFile) {
			this.symbolFile = symbolFile;
			try {

				symbolFile.createNewFile(); //create the file if it does not exist
				outputFile = new PrintWriter(new FileWriter(symbolFile));
			
			} catch (IOException e) {
				System.out.println("Problems in SymbolFileMaker constructor" + e);
			}
		}//end constructor
		
	//returns list of words in symbol table
	ArrayList getSymbolList(){
		ArrayList symbolList = new ArrayList();
		try{
			FileReader fRead = new FileReader(symbolFile);
			BufferedReader bufr = new BufferedReader(fRead);
			String word = "";
			StringTokenizer stok;
			
			while((word = bufr.readLine()) != null ){//read a line from the reserved word file
				stok = new StringTokenizer(word);
				String rword = stok.nextToken();//get the first word out of the line
				rword = rword.toUpperCase();
				symbolList.add( rword );				
			}//end while
		}catch (IOException e){
			System.out.println("Problems getting Reserved Word file. " + e );
		}

		System.out.println(symbolList.toString());
		return symbolList;
	}//end getSymbolList method
	
	void addSymbol(Token t){
		outputFile.println(t.getValue() + "\t" + t.getType() + "\t" + t.getAttribute() );
	}
	
	void closeSymbolFile(){
		outputFile.close();
	}
		
}//end SymbolFileMaker class
