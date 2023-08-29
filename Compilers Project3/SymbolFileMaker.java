/*
 * Created on Oct 2, 2004
 */
import java.io.*;
import java.util.*;
/**
 * @author shellyjo
 */
public class SymbolFileMaker {
		File symbolFile;
		ArrayList symbolTable = null;
		PrintWriter outputFile;
		
	public static void main(String[] args) {
	}
	
	public SymbolFileMaker() {
		symbolFile = new File("SymbolTable.txt");
			try {
				symbolFile.createNewFile(); //create the file if it does not exist
				outputFile = new PrintWriter(new FileWriter(symbolFile));
			
			} catch (IOException e) {
				System.out.println("Problems in SymbolFileMaker constructor" + e);
			}
			symbolTable = new ArrayList();
		}//end constructor
		
	//returns list of words in symbol table
	public ArrayList getSymbolList(){
		ArrayList symbolList = new ArrayList();
		
		for(int i = 0; i < symbolTable.size(); i ++){
			symbolList.add(symbolTable.get(i));
		}//end while
		
		return symbolList;
	}//end getSymbolList method
	
	Token addSymbol(Symbol s){
		int index = symbolTable.size();
		Token t = s.getToken();
		t.setAttribute(index);
		symbolTable.add(s);
		return t;
	}//end addSymbol
	
	void closeSymbolFile(){
		outputFile.close();
	}
	
	//check if a param has been declared
	public int checkExistence(Token token){
		String s = token.getValue();//string to compare to Symbol Table
		Symbol sym = null;//string from symbol table
		int rval = 0;
		String temp = null;
		Token t = null;
		if(!symbolTable.isEmpty()){
			for(int i = 0; i < symbolTable.size(); i++){
				 sym = (Symbol) symbolTable.get(i);//string from symbol table
				 t = sym.getToken();
				 temp = t.getValue();
				 //System.out.println("Checking if " + temp + " equals " + s);
				 if(temp.equals(s)){
				 	rval++;
				 }//end if
			}//end for
		}//end if
		
		return rval;
	}//end checkExist
	
	public boolean varExists(Token token, String path){
		boolean rval = false;
		
		String s = token.getValue();//token to compare to symbol table
		Symbol sym = null;//symbol from symbol table
		String temp = null;//string to compare to symbol table
		Token t = null;
		
		if(!symbolTable.isEmpty()){
			for(int i = 0; i < symbolTable.size(); i++){
				 sym = (Symbol) symbolTable.get(i);//string from symbol table
				 t = sym.getToken();
				 temp = t.getValue();
				 //System.out.println("Checking if " + temp + " equals " + s);
				 if(temp.equals(s)){
				 	if(sym.getPath().equals(path)){
				 		rval = true;
				 	}//end if
				 }//end if
			}//end for
		}//end if
		
		return rval;
	}//end checkVar
	
	void printSymbolFile(){
		Token t = null;
		Symbol s = null;
		//System.out.println("Symbol Table Arraylist is ... " + symbolTable.toString());
		while(!symbolTable.isEmpty()){
			//System.out.println("Printing SymbolTable");
			s = (Symbol) symbolTable.remove(0);
			t = s.getToken();
			outputFile.println(t.getValue() + "\t" + t.getType() + "\t" + t.getAttribute() + "\t" + nodeToString(s.getNodeType())+ "\t\t" + s.getPath() );	
		}
		closeSymbolFile();
	}//end printSymbolFile
	
	private String nodeToString(int s){
		String rval = null;
		switch(s){

			case(1): rval = "PGN";
				break;
			case(2): rval = "PGP";
				break;
			case(3): rval = "PPINT";
				break;
			case(4): rval = "PPREAL";
				break;
			case(5): rval = "PPAINT";
				break;
			case(6): rval = "PPAREAL";
				break;
			case(7): rval = "INT";
				break;
			case(8): rval = "REAL";
				break;
			case(9): rval = "AINT";
				break;
			case(10): rval = "AREAL";
				break;
			case(11): rval = "PROCN";
				break;
		}//end switch
		return rval;
	}//end nodeToString
	
	//check Type
	public int checkType(Token token, String path){
		//System.out.println("*************Checking Type on " + token.getValue() + " with path " + path); 
		
		int rval = Symbol.ERR;
		
		String s = token.getValue();//token to compare to symbol table
		
		Symbol sym = null;//symbol from symbol table
		String temp = null;//string to compare to parse string
		Token t = null;//token holder for symbol table token
		
		
		
		boolean exists = false;
		boolean p = false;
		
		if(!symbolTable.isEmpty()){
			int length = Integer.MAX_VALUE;
			
			//go through the entire symbol table
			for(int i = 0; i < symbolTable.size(); i++){
				 sym = (Symbol) symbolTable.get(i);//string from symbol table
				 //System.out.println("Symbol from the symbol table........." + sym);
				 t = sym.getToken();//token from that symbol
				 //System.out.println("Token from that symbol......." + t.getValue());
				 temp = t.getValue();//token name
				 //System.out.println("Once again, Token from that symbol......." + t.getValue());
				 

				 //System.out.println("---Comparing string from parse: " + temp + " to string from symbolTable: " + s);
				 if(temp.equals(s)){//if the two strings are equal, then a declaration exists
				 	
				 	exists = true;
				 	//System.out.println(temp + "^^^^^^^^^^^^^^^is equal^^^^^^^^^^^^^^" + s);
				 	
				 	if(path.startsWith(sym.getPath())){//if the paths are substrings, then it is in scope
				 		
				 		//System.out.println(path + "^^^^^^^^^^^^^^^ paths are similar" + sym.getPath());
				 		int tlen = (path.length()) - (sym.getPath().length());//get the difference in length of paths
				 		//System.out.println("Difference in path lengths " + tlen);
				 		if(tlen < length){//get the smallest difference, that will be the variable in scope
				 			//System.out.println("-----Symbol Table-----  path of symbol: " + sym.getPath());
				 			//System.out.println("length before change to smaller: " + length);
				 			length = tlen;
				 			//System.out.println("length after change to smaller: " + length);
				 			rval = sym.getNodeType();
				 			//System.out.println("Return value " + rval);
				 		}//end if(tlen < lenght)
				 		
				 		//System.out.println("Finished if(tlen < length)");
				 		
				 	}else{//else if paths are not substrings
				 		//System.out.println("Paths are not substrings, rval will change");
				 		//rval = Symbol.ERR;//variable is out of scope
				 	}//end else
				 }else {//strings are not equal
				 	//System.out.println("Original strings are not equal, rval will change");
				 	//rval = Symbol.ERR;//variable has not been defined
				 }//end else
				 
			}//end for
		}//end if
		//System.out.println("--------------- returning " + rval);
		return rval;
	}//end checkType()
	
	//find all procedure parameters for this id
	//find the id in the symbol table and get the exact path for that
	//then find all proc params for that path
	public Stack returnProcParams(String id){
		Stack rval = new Stack();
		Symbol symbol = null;
		String sName = null;
		String path = null;
		
		//find the id in the symbol table
		for(int i = 0; i < symbolTable.size(); i++){
			symbol = (Symbol) symbolTable.get(i);
			sName = symbol.getToken().getValue();
			if(sName.equals(id) && symbol.getNodeType()==Symbol.PROCN){
				System.out.println("\n ______________Procedure Found: " + symbol.getToken().getValue());
				path = symbol.getPath();
			}
			
		}//go through entire symbol table
		
		for(int i = 0; i < symbolTable.size(); i++){
			symbol = (Symbol) symbolTable.get(i);
			if(symbol.getPath().equals(path)){//if paths are equal
				System.out.println("Paths are equal: sPath " + symbol.getPath() + "\n path: " + path);
				System.out.println("Node name is: " + symbol.getToken().getValue());
				//take only node types >= 3 and <= 6
				if(symbol.getNodeType() >= Symbol.PPINT && symbol.getNodeType() <= Symbol.PPAREAL){
					Integer n = new Integer(symbol.getNodeType());
					System.out.print(">>>> push >>>>   " + n.toString() + "   ");
					rval.push(n);
				}else
					System.out.println("Numbers are never equal");
					System.out.println("Node is: " + symbol.getNodeType());
				
			}else//end if path equals spath
				System.out.println("Paths were not equal");
		}//end for through entire symbol table
		return rval;
	}//end returnProcParams
	
	public Symbol getSymbol(Token t, String path){
		Symbol rval = null;
		String tName = t.getValue();
		Symbol temp = null;
		for(int i = 0; i < symbolTable.size(); i++){
			temp = (Symbol) symbolTable.get(i);
			
			String name = temp.getToken().getValue();
			String sPath = temp.getPath();
			
			if(name.equals(tName) && sPath.equals(path)){
				rval = temp;
			}else if (tName.equals(name) && path.startsWith(sPath)){
				rval = temp;
			}else{
				System.out.println("Could not find symbol in get Symbol for Token: " + t.getValue());
			}
		}//end for
		
		return rval;
	}//end get Symbol
		
}//end SymbolFileMaker class
