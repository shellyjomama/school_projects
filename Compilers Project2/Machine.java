/*
 * Created on Sep 24, 2004
 */
import java.util.*;

/**
 * @author shellyjo - Shelly Seier
 */
public abstract class Machine {

	public static void main(String[] args) {
	}

	String check(
		String str,
		ListFileMaker listFile,
		TokenFileMaker tokenFile,
		int lineNum,
		ArrayList resWord,
		SymbolFileMaker symbolFile, Lex l) {
			
			return str;

	}
}

class IdResMachine extends Machine {

	public static void main(String[] args) {
	}

	String check(
		String str,
		ListFileMaker listFile,
		TokenFileMaker tokenFile,
		int lineNum,
		ArrayList resWord,
		SymbolFileMaker symbolFile, Lex l) {
			
			
			int b = 0;
			int f = 0;
			boolean c = false;
			
			while((str.charAt(f) >= 'A' && str.charAt(f)<='Z') || (str.charAt(f)>='a' && str.charAt(f)<='z')){
				f++;
				while((str.charAt(f) >= '0') && (str.charAt(f) <= '9')){
					f++;
				}
				c = true;
			}//end while alpha characters
			if (c){//if there were alpha characters
				String tok = str.substring( b, f);//string of alpha chars
				String rval = str.substring( f );//leftover string
				if(tok.length() > 10){
					Token t = new Token(tok, Token.ERROR, Token.LONGID, lineNum);
					tokenFile.printToken(t);
					l.addToken( t );
					listFile.printError("Lex Error: ID length exceeded");
					return rval;
				}else
				
				if(resWord.contains(tok.toUpperCase())){//if it is already a resWord
					//do nothing, just return the leftovers


					if(tok.toUpperCase().equals("PROGRAM")){
						Token t = new Token(tok, Token.PROG, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("VAR")){
						Token t = new Token(tok, Token.VAR, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("ARRAY")){
						Token t = new Token(tok, Token.ARRAY, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("OF")){
						Token t = new Token(tok, Token.OF, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("INTEGER")){
						Token t = new Token(tok, Token.INT, Token.NUM, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("REAL")){
						Token t = new Token(tok, Token.REAL, Token.NUM, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("PROCEDURE")){
						Token t = new Token(tok, Token.PROCEDURE, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("BEGIN")){
						Token t = new Token(tok, Token.BEGIN, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else
					if(tok.toUpperCase().equals("CALL")){
						Token t = new Token(tok, Token.CALL, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else  
					if(tok.toUpperCase().equals("END")){
						Token t = new Token(tok, Token.END, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("IF")){
						Token t = new Token(tok, Token.IF, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("THEN")){
						Token t = new Token(tok, Token.THEN, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("ELSE")){
						Token t = new Token(tok, Token.ELSE, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("WHILE")){
						Token t = new Token(tok, Token.WHILE, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else  
					if(tok.toUpperCase().equals("DO")){
						Token t = new Token(tok, Token.DO, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("NOT")){
						Token t = new Token(tok, Token.NOT, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("EOF")){
						Token t = new Token(tok, Token.EOF, 0, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else 
					if(tok.toUpperCase().equals("OR")){
						Token t = new Token(tok, Token.ADDOP, Token.OR, lineNum);
						tokenFile.printToken(t);	
						l.addToken( t );					 
					}else
					if(tok.toUpperCase().equals("DIV")){
						Token t = new Token(tok, Token.MULOP, Token.DIV, lineNum);
						tokenFile.printToken(t);	
						l.addToken(t);					 
					}else
					if(tok.toUpperCase().equals("MOD")){
						Token t = new Token(tok, Token.MULOP, Token.MOD, lineNum);
						tokenFile.printToken(t);
						l.addToken(t);						 
					}else
					if(tok.toUpperCase().equals("AND")){
						Token t = new Token(tok, Token.MULOP, Token.AND, lineNum);
						tokenFile.printToken(t);	
						l.addToken(t);					 
					}else{
						int type = resWord.indexOf(tok.toUpperCase());
						Token t = new Token(tok, type+1, 0, lineNum);
						tokenFile.printToken(t);
						l.addToken(t);
					}
					
					return rval;
				}else if(isPresent(str, symbolFile)) {//is it in the symbol table
					Token t = new Token(tok, Token.ID, Token.ID, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
					//do nothing, just return the eftovers
					return rval;
				}else{//not in symbol table
					Token t = new Token(tok, Token.ID, Token.ID, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
					symbolFile.addSymbol( t );
					return rval;
				}
			}else{//there were no alpha characters
				return str;
			}//end else there were no alpha chars
			

	}//end check method
	
	boolean isPresent(String str, SymbolFileMaker sfm){
		boolean present = false;
		ArrayList symbols = sfm.getSymbolList();
		if(symbols.contains( str )){
			present = true;
		}
		return present;
	}

}//end IDRes machine


class NumMachine extends Machine {

	public static void main(String[] args) {
	}

	String check(
		String str,
		ListFileMaker listFile,
		TokenFileMaker tokenFile,
		int lineNum,
		ArrayList resWord,
		SymbolFileMaker symbolFile, Lex l) {
			
		int b = 0; //back pointer
		int f = 0; //front pointer
		boolean c = false;

		while ((str.charAt(f) >= '0') && (str.charAt(f) <= '9')) {
			f++;
			c = true;
		} //if it starts with a digit, get all the digits in xx position
		
		String y = "";
		String x = "";
		String z = "";
		
		if (c) { //if there were digits check the next character?

			if ((str.charAt(f)) == '.') { //if there is a decimal point
				
				x = str.substring(b, f); //get the xx portion
				str = str.substring(f + 1); //only take what is left (avoid decimal) and check			
				f = 0; //start the pointers again
				boolean d = false;
				
				while ((str.charAt(f) >= '0') && (str.charAt(f) <= '9')) {
					f++;
					d = true;
				} //while there are more digits in yy position
				
				if (d) { //if there were more digits after the decimal 'E+'
					if ( (str.charAt(f) == 'E' && str.charAt(f + 1) == '+') ) {
						
						//if there was an E+ or E-
						y = str.substring(b, f);
						str = str.substring(f + 2);
						
						f = 0;
						boolean e = false;
						
						while((str.charAt(f) >= '0') && (str.charAt(f) <= '9')){
							f++;
							e=true;
						}//while there are more digits in the z position
					
						if (e){
							String rval = str.substring( f );
							z = str.substring( b, f);
							Token tkn = checkRealEErrors(x, y, z, listFile, tokenFile, lineNum);
							String tk = x.concat(".");
							tk = tk.concat(y);
							tk = tk.concat("E+");
							tk = tk.concat(z);
							tkn.setValue( tk );
							tokenFile.printToken( tkn );
							l.addToken(tkn);
							return rval;
								
						}else{//no digits after E+, real number, put E+ back and return
							
							String rval = "E+".concat(str);
							Token t = checkRealErrors(x, y, listFile, tokenFile, lineNum, l);
							tokenFile.printToken(t);
							l.addToken(t);
	
							return rval;						
						}
					}//end check for e+ 
					
					else //check for e-
					if ((str.charAt(f) == 'E'	&& str.charAt(f + 1) == '-') ){

						
						//if there was an E+ or E-
						y = str.substring(b, f);
						str = str.substring(f + 2);
						
						f = 0;
						boolean e = false;
						
						while((str.charAt(f) >= '0') && (str.charAt(f) <= '9')){
							f++;
							e=true;
						}//while there are more digits in the z position
					
						if (e){
							String rval = str.substring(f);
							z = str.substring( b, f);
							Token tkn = checkRealEErrors(x, y, z, listFile, tokenFile, lineNum);
							String tk = x.concat(".");
							tk = tk.concat(y);
							tk = tk.concat("E-");
							tk = tk.concat(z);
							tkn.setValue( tk );	
							tokenFile.printToken( tkn );
							l.addToken(tkn);
							return rval;
						}else {//no digits afer E-, Real number, put E- back and return
							
							String rval = "E-".concat(str);
							Token t = checkRealErrors(x, y, listFile, tokenFile, lineNum, l);
	
							return rval;
						}
					}//end check for e-
					
					else if (str.charAt(f) == 'E') {
						
						//if there was an exponent get the y portion
						y = str.substring(b, f);
						str = str.substring(f + 1);		
						f = 0;
						boolean e = false;
						
						while ( (str.charAt(f) >= '0') && (str.charAt(f) <= '9') ) {
							f++;
							e = true; //yes there were digits
						}//end while digit
						
						if (e) { //if there was an exponent
							String rval = str.substring(f);
							z = str.substring(b, f);
							Token tkn = checkRealEErrors(x, y, z, listFile, tokenFile, lineNum);
							String tk = x.concat(".");
							tk = tk.concat(y);
							tk = tk.concat("E");
							tk = tk.concat(z);
							tkn.setValue( tk );
							tokenFile.printToken( tkn );
							l.addToken(tkn);
							return rval;
						}else{//no digits after E, Real number, put E back and return
							String rval = "E".concat(str);
							Token t = checkRealErrors(x, y, listFile, tokenFile, lineNum, l);
							
							return rval;
						}
				}//end else if 'E'
				else {//digits after decimal did not end in E 
					//it is a real number
					y = (str.substring( b, f ));
					String rval = str.substring( f );
					Token t = checkRealErrors(x, y, listFile, tokenFile, lineNum, l);
					
					return rval;
				}

			} else{ //end digits after decimal ie. no digits after decimal
				//it was an integer
				
				String tok = str.substring(b,f);
				String rval = str.substring( f );
				Token t = checkIntErrors(tok, tokenFile, listFile, lineNum, l);
				
				return rval;
				
			}//end else digits after decimal
		
		}else{ //end decimal if  //it was an integer
			String tok = str.substring(b,f);
			String rval = str.substring(f);
			Token t = checkIntErrors(tok, tokenFile, listFile, lineNum, l);
			
			return rval;

		}//end decimal else
		}else {//end if (C) there were digits in the first place
			return str;
		}//end else, there were digits

	} //end of check method	
	
	boolean checkZero(String str) {
		//leading zero check return true if there is a leading zero
		boolean match = false;
		if ((str.charAt(0) == '0')& str.length() > 1) {
			match = true;
		}
		return match;
	} //end checkZero method
	
	boolean checkZeroOld(String str) {
		//leading zero check return true if there is a leading zero
		boolean match = false;
		if (str.charAt(0) == '0') {
			match = true;
		}
		return match;
	} //end checkZero method
	
	boolean longXY(String str) {
		//XX length check return true if too long
		boolean match = false;
		if (str.length() > 5) {
			match = true;
		}
		return match;
	} //end longXY
	
	boolean longInt(String str){
		boolean match = false;
		if(str.length()>10){
			match = true;
		}
		return match;
	}
	
	boolean longZ(String str){
		boolean match = false;
		if(str.length() > 2){
			match = true;
		}
		return match;
	}//end longZ method

	Token checkRealEErrors(String x, String y, String z, ListFileMaker listFile, TokenFileMaker tokenFile, int lineNum) {

		//leading zero, x too big, y too big, z too big
		if (checkZero(x) && longXY(x) && longXY(y) && longZ(z)) {

			//put it all back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			//create a token
			Token t = new Token(tok, Token.ERROR, Token.ZEROLONGXYZ, lineNum);
			listFile.printError(
				"Lex Error: Leading zero, xx too long, yy too long, zz too long");
			return t;
		}else  //end leadzero, longx, longy, longz
		
		//leading zero, x too big, y too big
		if (checkZero(x) && longXY(x) && longXY(y)) {

			//put the number back together
			String tok = x.concat(y);
			tok = tok.concat(z);

			Token t = new Token(tok, Token.ERROR, Token.ZEROLONGXY, lineNum);
			
			listFile.printError("Lex Error: Leading zero error, xx too long, yy too long");
			return t;
		} else //end zerolongxy error

			//leading zero, x too big, z too big
		if (checkZero(x) && longXY(x) && longZ(z)) {
			
			//put the number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t =
				new Token(tok, Token.ERROR, Token.ZEROLONGXZ, lineNum);
		
			listFile.printError(
				"Lex Error: Leading zero error, xx too long, zz too long");
			return t;
		} else //end zerolongxz error

		//leading zero, y too big, z too big
		if (checkZero(x) && longXY(y) && longZ(z)) {

			//put the number back together
			String tok = x.concat(y);
			tok = tok.concat(z);

			Token t =
				new Token(tok, Token.ERROR, Token.ZEROXLONGYZ, lineNum);

			listFile.printError(
				"Lex Error: Leading zero error, yy too long, zz too long");
			return t;
		} else //end zerolongyz error

		//leading zero, x too big
		if (checkZero(x) && longXY(x)) {
	
			//put the number back together
			String tok = x.concat(y);
			tok = tok.concat(z);

			Token t =
				new Token(tok, Token.ERROR,	Token.ZEROLONGX, lineNum);
			
			listFile.printError("Lex Error: Leading zero error, xx too long");
			return t;
		} else //end lead zero, x too big

		//leading zero, y too big
		if (checkZero(x) && longXY(y)) {
			
			//put the nubmer back together
			String tok = x.concat(y);
			tok = tok.concat(z);
		
			Token t = new Token(tok, Token.ERROR, Token.ZEROXLONGY,	lineNum);
			
			listFile.printError("Lex Error: Leading zero error, yy too long");
			return t;
		} else //end leading zero, z too big

		//leading zero, z too big
		if (checkZero(x) && longZ(z)) {
		
			//put string back together
			String tok = x.concat(y);
			tok = tok.concat(z);
		
			Token t = new Token(tok, Token.ERROR, Token.ZEROXLONGZ,	lineNum);
			
			listFile.printError("Lex Error: Leading zero error, zz too long");
			return t;
		} else //end leading zero, z too big

		//xx too long, yy too long, zz too long
		if (longXY(x) && longXY(y) && longZ(z)) {
			
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGXYZ, lineNum);
			
			listFile.printError("Lex Error: xx too long, yy too long, zz too long");
			return t;
		} else //end xx, yy, zz too long

		//xx too long, yy too long
		if (longXY(x) && longXY(y)) {
			
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGXY,	lineNum);
			
			listFile.printError("Lex Error: xx too long, yy too long");
			return t;
		} else //end of xx, yy too long
		
		//xx, zz too long
		if (longXY(x) && longZ(z)) {
		
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGXZ, lineNum);
			
			listFile.printError("Lex Error: xx too long, zz too long");
			return t;
		} else //end long xx, zz

		//yy, zz too long
		if (longXY(y) && longZ(z)) {
		
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);

			Token t = new Token(tok, Token.ERROR, Token.LONGYZ, lineNum);
			
			listFile.printError("Lex Error: yy too long, zz too long");
			return t;
		}else //end yy, zz too long

		//x too big
		if (longXY(x)) {
			//put number back together
			String tok = x.concat(y); 
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGX, lineNum);
			
			listFile.printError("Lex Error: xx too long");
			return t;
		} else //end x too big

		//y too big
		if (longXY(y)) {
			
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGY, lineNum);
			
			listFile.printError("Lex Error: yy too long");
			return t;
		} else //end y too big

		//z too big
		if (longZ(z)) {
		
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGZ, lineNum);
			
			listFile.printError("Lex Error: zz too long");
			return t;
		} else //end z too big

		//leading zero in x error
		if (x.charAt(0) == '0') {
		
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
		
			Token t = new Token(tok, Token.ERROR, Token.ZEROX, lineNum);
			
			listFile.printError("Lex Error: Leading zero error");
			return t;
		} //end leading zero x error
		
		//the nubmer is well formed 
		else {
			
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.NUM, Token.REAL, lineNum);
			//do not print here because need to put decimal and E's back into place
			return t;
		}//end number is well formed
	
	} //end of checkRealEErrors(x,y,z) method
	
	Token checkRealErrors(String x, String y, ListFileMaker listFile, TokenFileMaker tokenFile, int lineNum, Lex l){
		//leading zero, x too big, y too big
		 if (checkZero(x) && longXY(x) && longXY(y)) {
								
			//put the number back together
			String tok = x.concat(".");
			tok = tok.concat(y);
							
			Token t = new Token(tok, Token.ERROR, Token.ZEROLONGXY, lineNum);
			tokenFile.printToken(t);
			l.addToken(t);
			listFile.printError("Lex Error: Leading zero error, xx too long, yy too long");
			return t;
							
		  } else //end zerolongxy error
		  	  //leading zero, x too big
			  if (checkZero(x) && longXY(x)) {
						
				  //put the number back together		
				  String tok = x.concat(".");
				  tok = tok.concat(y);
			
				  Token t = new Token(tok, Token.ERROR, Token.ZEROLONGX, lineNum);
				  tokenFile.printToken(t);
				  l.addToken( t );
				  listFile.printError("Lex Error: Leading zero error, xx too long");
				  return t;
						
			} else //end lead zero, x too big
								
			//leading zero, y too big
			if (checkZero(x) && longXY(y)) {
						
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
						
				Token t =new Token(tok,Token.ERROR,	Token.ZEROLONGX,lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
				listFile.printError("Lex Error: Leading zero error, yy too long");
				return t;
			} else //end leading zero, y too big
						
			//xx too long, yy too long
			if (longXY(x) && longXY(y)) {
						
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
						
				Token t =new Token(tok,	Token.ERROR,Token.ZEROLONGX,lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
				listFile.printError("Lex Error: xx too long, yy too long");
				return t;
			} else //end xx, yy too long
						 
			//x too big
			if (longXY(x)) {
							
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
						
				Token t = new Token(tok,Token.ERROR,Token.LONGX,lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
				listFile.printError("Lex Error: xx too long");
				return t;
			} else //end x too big
						
			//y too big
			if (longXY(y)) {
						
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
						
				Token t =new Token(tok,Token.ERROR,Token.LONGY,lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
				listFile.printError("Lex Error: yy too long");
				return t;
			} else //end y too big
						
			//leading zero in x error
			if (x.charAt(0) == '0') {
						
				//put the nubmer back together
				String tok = x.concat(".");
				tok = tok.concat(y);
					
				Token t =new Token(tok,Token.ERROR,Token.ZEROX,lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
				listFile.printError("Lex Error: Leading zero error");
				return t;
			} else {//end leading zero x error
				
				//the number is a well formed real
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
				Token t =new Token(tok, Token.NUM, Token.REAL, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
				return t;
			}//end number is well formed
	}//end checkRealErrors method
	
	Token checkIntErrors(String x, TokenFileMaker tokenFile, ListFileMaker listFile, int lineNum, Lex l){
		//leading zero and int length error check
		if (longInt(x) && checkZero(x)) {
			Token t = new Token(x, Token.ERROR, Token.ZEROINT, lineNum);
			listFile.printError("Lex Error: Leading zero error and Integer too long");
			tokenFile.printToken(t);
			l.addToken(t);
					
			return t;
		} else //integer length check
		
		if (longInt(x)) {
			Token t = new Token(x, Token.ERROR, Token.LONGINT, lineNum);
			listFile.printError("Lex Error: Integer too long");
			tokenFile.printToken(t);
			l.addToken(t);
						
			return t;
		} else //leading zero error
		if (checkZero(x)) {
			Token t = new Token(x, Token.ERROR, Token.ZEROLONGINT, lineNum);
			listFile.printError("Lex Error: Leading zero error");
			tokenFile.printToken(t);
			l.addToken(t);
						
			return t;
		} else {
			//integer is well formed 				
			//create token
			Token t = new Token(x, Token.NUM, Token.INT, lineNum);
			//print token to token file
			tokenFile.printToken(t);
			l.addToken(t);
							
			return t;
		} //return what is left over after token taken off
		
	}//end checkIntErrors method

} //end RealEMachine


class SymbolMachine extends Machine {
	//includes Addop, Mulop, and Assignop
	public static void main(String[] args) {
	}

	String check(
		String str,
		ListFileMaker listFile,
		TokenFileMaker tokenFile,
		int lineNum,
		ArrayList resWord,
		SymbolFileMaker symbolFile, Lex l) {
			
			int b = 0;
			int f = 0;
			
			//check for dots
			
			if(str.charAt(f) == '.'){
				if(str.charAt(f+1)=='.'){//if is it ".."
					String tok = str.substring(b, f+2);

					str = str.substring(f+2);
					Token t = new Token(tok, Token.DD, Token.DD, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
					
				}else {//it is single dot
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.DOT, Token.DOT, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
				}
			}else
			
			
			//check for assignop or colon
			if (str.charAt(f)==':'){
					if (str.charAt(f+1)=='='){//assignop
						String tok = str.substring(b, f+2);
						str = str.substring(f+2);
						Token t = new Token(tok, Token.ASSIGN, Token.ASSIGN, lineNum);
						tokenFile.printToken(t);
						l.addToken(t);						
					}else {//colon
						String tok = str.substring(b, f+1);
						str = str.substring(f+1);
						Token t = new Token(tok, Token.COLON, Token.COLON, lineNum);
						tokenFile.printToken(t);	
						l.addToken(t);	
					}
				}
				else if(str.charAt(f)=='+'){
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.ADDOP, Token.PLUS, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
				}
				else if(str.charAt(f)=='-'){
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.ADDOP, Token.MINUS, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
				}
				else if(str.charAt(f)=='*'){
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.MULOP, Token.ASTERISK, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
				}
				else if(str.charAt(f)=='/'){
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.MULOP, Token.SLASH, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
				}
			else if(str.charAt(f)=='['){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.OPENSQ, Token.OPENSQ, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}
			else if(str.charAt(f)==']'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.CLOSESQ, Token.CLOSESQ, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}
			else if(str.charAt(f)=='('){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.OPENPRN, Token.OPENPRN, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}
			else if(str.charAt(f)==')'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.CLOSEPRN, Token.CLOSEPRN, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}
			else if(str.charAt(f)==';'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SEMI, Token.SEMI, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}
			else if(str.charAt(f)==','){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.COMMA, Token.COMMA, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}
			else if(str.charAt(f)=='{'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.OPENCR, Token.OPENCR, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}
			else if(str.charAt(f)=='}'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.CLOSECR, Token.CLOSECR, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}else if(str.charAt(f)== '='){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.RELOP, Token.EQUALTO, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}
		
			return str; //do this later when you get rid of string tokenizer
	} //end check
	
}//end of Symbol machine

class WhiteSpaceMachine extends Machine {

	public static void main(String[] args) {
	}

	String check(
		String str,
		ListFileMaker listFile,
		TokenFileMaker tokenFile,
		int lineNum,
		ArrayList resWord,
		SymbolFileMaker symbolFile, Lex l) {
			
		int b = 0;
		//back pointer
		int f = 0;
		//front pointer
		//not working because after new line f counts but the string is less than 1 so out of bounds
		if ((str.charAt(f) == ' ')
			|| (str.charAt(f) == '\t')
			|| (str.charAt(f) == '\n')) {
			f++;
		}
		str = str.substring(f);
		return str;
	} //end check
	
}

class RelopMachine extends Machine {

	public static void main(String[] args) {
	}

	String check(
		String str,
		ListFileMaker listFile,
		TokenFileMaker tokenFile,
		int lineNum,
		ArrayList resWord,
		SymbolFileMaker symbolFile, Lex l) {
			
			int b = 0;
			int f = 0;
			
			if(str.charAt(f)=='<'){
				if(str.charAt(f+1) == '>'){//not equal
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.RELOP, Token.NE, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
				}else if(str.charAt(f+1)=='='){//less than or equal
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.RELOP, Token.LE, lineNum);
				}else{//less than
					String tok = str.substring(b, f);
					str = str.substring(f);
					Token t = new Token(tok, Token.RELOP, Token.LESS, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
				}
			}else if(str.charAt(f)=='>'){
				if(str.charAt(f+1)=='='){//greater than or equal
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.RELOP, Token.GE, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
				}else{//greater than
					String tok = str.substring(b, f);
					str = str.substring(f);
					Token t = new Token(tok, Token.RELOP, Token.GREATER, lineNum);
					tokenFile.printToken(t);
					l.addToken(t);
				}
			}else if(str.charAt(f)=='='){//relop
				String tok = str.substring(b, f);
				str = str.substring(f);
				Token t = new Token(tok, Token.RELOP, Token.EQUALTO, lineNum);
				tokenFile.printToken(t);
				l.addToken(t);
			}
			
			return str;

	} //end check
}
