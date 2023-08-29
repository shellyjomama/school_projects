/*
 * Created on Sep 18, 2004
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
public class Lex {
	File listFile = new File("ListingFile.txt");
	File tokenFile = new File("TokenFile.txt");
	File resWords = new File("ReservedWords.txt");
	File symbolTable = new File("SymbolTable.txt");
	
	public static void main(String[] args) {
		Lex l = new Lex();
		l.run();
	}

	public void run() {
		String source = "Source.txt";

		try {

			FileReader fRead = new FileReader(source);
			BufferedReader bufr = new BufferedReader(fRead);

			String line = ""; //72 character buffer read in from source code
			int lineCount = 0;
			//File listFile = new File("ListingFile.txt");
			//outputFile = new PrintWriter(new FileWriter(listFile));

			ListFileMaker lfm = new ListFileMaker(listFile);
			TokenFileMaker tfm = new TokenFileMaker(tokenFile);
			SymbolFileMaker sfm = new SymbolFileMaker(symbolTable);
			ArrayList resWordFile = getResWordFile();
			
			String str = "";
			
			//get the next line of source code
			while ((line = bufr.readLine()) != null) {
				//StringTokenizer stok = new StringTokenizer(line);
				lineCount++; //current line number
				lfm.printLine(line, lineCount);
	
				/*for (int i = stok.countTokens() ; i > 0; i--){
				
					str = stok.nextToken();
					str = str.concat("`");
					System.out.println(lineCount +". " + str);
				*/
				line = line.concat( "`" );
				machineLoop(line, lineCount, lfm, tfm, resWordFile, sfm);

				} //end while hasMoreTokens
			
			Token t = new Token("EOF", Token.SYMBOL, Token.EOF, 0);
			tfm.printToken(t);
			
			lfm.closeListFile();
			tfm.closeTokenFile();
			sfm.closeSymbolFile();

		} catch (IOException e) {
			System.out.println(e);
		}
	} //end of run method

	void machineLoop(
		String line,
		int lineCount,
		ListFileMaker lfm,
		TokenFileMaker tfm, 
		ArrayList rwf, 
		SymbolFileMaker sfm) {

		
		Machine id = new IdResMachine();
		Machine ws = new WhiteSpaceMachine();
		Machine nm = new NumMachine();
		Machine sm = new SymbolMachine();
		Machine rm = new RelopMachine();

		//begin checking characters
		int i = 0;
		while(line.length() > 1) {

			System.out.println("String before machine " + line);
			
			String original = line;		
			
			line = id.check(line, lfm, tfm, lineCount, rwf, sfm);
			line = ws.check(line, lfm, tfm, lineCount, rwf, sfm);
			line = nm.check(line, lfm, tfm, lineCount, rwf, sfm);
			line = sm.check(line, lfm, tfm, lineCount, rwf, sfm);
			line = rm.check(line, lfm, tfm, lineCount, rwf, sfm);

			if(line.equals(original)){
				String tok = line.substring(0, 1);
				line = line.substring(1);
				Token t = new Token(tok, Token.ERROR, Token.UNRECOG, lineCount);
				tfm.printToken(t);
				lfm.printError("Unrecognized string.");
			}

			System.out.println("String after machine " + line);
			System.out.println("String length " + line.length());
			
		}

	} //end of machineLoop method
	
	ArrayList getResWordFile(){
		ArrayList resWordFile = new ArrayList();
		try{
			FileReader fRead = new FileReader(resWords);
			BufferedReader bufr = new BufferedReader(fRead);
			String word = "";
			StringTokenizer stok;
			
			while((word = bufr.readLine()) != null ){//read a line from the reserved word file
				stok = new StringTokenizer(word);
				String rword = stok.nextToken();//get the first word out of the line
				rword = rword.toUpperCase();
				resWordFile.add( rword );				
			}//end while
			
		}catch (IOException e){
			System.out.println("Problems getting Reserved Word file. " + e );
		}
		System.out.println(resWordFile.toString());
		return resWordFile;
	}//end getResWordFile()
} //end of Lex class
