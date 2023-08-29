/*
 * Created on Sep 18, 2004
 */
 
import java.io.*;
import java.util.*;

/**
 * @author shellyjo - Shelly Seier
 */
public class Lex {
	File listFile = new File("ListingFile.txt");
	File tokenFile = new File("TokenFile.txt");
	File resWords = new File("ReservedWords.txt");
	File symbolTable = new File("SymbolTable.txt");
	ListFileMaker lfm = null;
	TokenFileMaker tfm = null;
	SymbolFileMaker sfm = null;
	ArrayList resWordFile = new ArrayList();
	FileReader fRead = null;
	BufferedReader bufr = null;
	
	String line = ""; //72 character buffer read in from source code
	ArrayList lot = new ArrayList();//ArrayList of Tokens
	boolean started = false;
	int lineCount = 0;
	
	public static void main(String[] args) {

	}

	public void run() {//initialize everything
		String source = "finalTestProgram.txt";

		try {
			fRead = new FileReader(source);
			bufr = new BufferedReader(fRead);

			lfm = new ListFileMaker(listFile);
			tfm = new TokenFileMaker(tokenFile);
			sfm = new SymbolFileMaker(symbolTable);
			resWordFile = getResWordFile();
			
			started = true;
			
		} catch (IOException e) {
			System.out.println("Creating and Initializing Error" + e);
		}
	} //end of run method

	void machineLoop() {
		
		Machine id = new IdResMachine();
		Machine ws = new WhiteSpaceMachine();
		Machine nm = new NumMachine();
		Machine sm = new SymbolMachine();
		Machine rm = new RelopMachine();

		//begin checking characters
		int i = 0;
		System.out.println("line length is: " + line.length());
		while(line.length() > 1) {
			String original = line;	
				
			
			line = id.check(line, lfm, tfm, lineCount, resWordFile, sfm, this);
			line = ws.check(line, lfm, tfm, lineCount, resWordFile, sfm, this);
			line = nm.check(line, lfm, tfm, lineCount, resWordFile, sfm, this);
			line = sm.check(line, lfm, tfm, lineCount, resWordFile, sfm, this);
			line = rm.check(line, lfm, tfm, lineCount, resWordFile, sfm, this);


			if(line.equals(original)){
				String tok = line.substring(0, 1);
				line = line.substring(1);
				Token t = new Token(tok, Token.ERROR, Token.UNRECOG, lineCount);
				tfm.printToken(t);
				lfm.printError("Unrecognized string.");
			}
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
		//System.out.println("ResFile: " + resWordFile.toString());
		return resWordFile;
	}//end getResWordFile()
	
	public void addToken(Token tok){
		lot.add( tok );
	}//end addToken(tok)
	
	public boolean getLine(){//return true if there was a line to get
		//debugging
		System.out.println("Getting a line in getLine()");
		
		boolean rval = false;
		try{
			if ((line = bufr.readLine()) != null) {
				System.out.println("linecount++");
				lineCount++; //current line number
				System.out.println("lfm.print");
				lfm.printLine(line, lineCount);
				System.out.println("line.concat");
				line = line.concat( "`" );
				System.out.println("rval");
				
				rval = true;
				System.out.println("machinloop");
				machineLoop();
				System.out.println("will never print");
			} //end while hasMoreTokens
		}catch (Exception e){
			System.out.println("Could not read line in. " + e);
		}//end try catch block
		return rval;
	}//end getLine()
	
	public void finish (){
		lfm.closeListFile();
		tfm.closeTokenFile();
		sfm.closeSymbolFile();
	}
	
	public Token getToken(){
		Token rval = null;
		
		if(!started){
			run();
		}
		if (lot.isEmpty()){//if the token array is empty get a new line
			if(getLine()){//getting the new line may produce tokens
				
				//the point of the following code is to skip blank lines
				if(lot.isEmpty()){//if the line was blank, call getToken again
					rval = getToken();
				}else{//if the line was not blank
					rval = (Token) lot.remove(0);
				}			
			}
			else{//end of file, add the EOF token to Token File and Array 
				System.out.println("End of File.  No more Tokens."); 
				Token t = new Token("EOF", Token.EOF, Token.EOF, lineCount+1);
				tfm.printToken(t);
				addToken(t);
				System.out.println("Token List = " + t.converter((((Token) lot.get(0)).type))  );
				rval = (Token) lot.remove(0);
			}
		}else
			rval = (Token) lot.remove(0);
		
		//debugging
		for (int i = 0; i < lot.size(); i++){
			Token hold = null;
			hold = (Token)lot.get(i);
			System.out.print(hold.converter(hold.type) + " ");
		}
		
		System.out.println("Returning token of type: " + rval.converter(rval.type));
				
		return rval;
	}//end getToken()
	
} //end of Lex class
