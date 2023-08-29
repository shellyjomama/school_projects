/*
 * Created on Sep 24, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.util.*;

/**
 * @author shellyjo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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
		SymbolFileMaker symbolFile) {
			
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
		SymbolFileMaker symbolFile) {
			
			
			int b = 0;
			int f = 0;
			boolean c = false;
			
			while((str.charAt(f) >= 'A' && str.charAt(f)<='Z') || (str.charAt(f)>='a' && str.charAt(f)<='z')){
				f++;
				c = true;
			}//end while alpha characters
			if (c){//if there were alpha characters
				String tok = str.substring( b, f);//string of alpha chars
				String rval = str.substring( f );//leftover string
				if(tok.length() > 10){
					Token t = new Token(tok, Token.ERROR, Token.LONGID, lineNum);
					tokenFile.printToken(t);
					listFile.printError("ID length exceeded");
					return rval;
				}else
				
				if(resWord.contains(tok.toUpperCase())){//if it is already a resWord
					//do nothing, just return the leftovers

					if(tok.toUpperCase().equals("OR")){
						Token t = new Token(tok, Token.ADDOP, Token.OR, lineNum);
						tokenFile.printToken(t);						 
					}else 
					if(tok.toUpperCase().equals("DIV")){
						Token t = new Token(tok, Token.MULOP, Token.DIV, lineNum);
						tokenFile.printToken(t);						 
					}else
					if(tok.toUpperCase().equals("MOD")){
						Token t = new Token(tok, Token.MULOP, Token.MOD, lineNum);
						tokenFile.printToken(t);						 
					}else
					if(tok.toUpperCase().equals("AND")){
						Token t = new Token(tok, Token.MULOP, Token.AND, lineNum);
						tokenFile.printToken(t);						 
					}else{
						int type = resWord.indexOf(tok.toUpperCase());
						Token t = new Token(tok, type+1, 0, lineNum);
						tokenFile.printToken(t);
					}
					
					return rval;
				}else if(isPresent(str, symbolFile)) {//is it in the symbol table
					Token t = new Token(tok, Token.SYMBOL, Token.ID, lineNum);
					tokenFile.printToken(t);
					//do nothing, just return the eftovers
					return rval;
				}else{//not in symbol table
					Token t = new Token(tok, Token.SYMBOL, Token.ID, lineNum);
					tokenFile.printToken(t);
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
		SymbolFileMaker symbolFile) {
			
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
						
						//debugging
						System.out.println("IN E+");
						
						//if there was an E+ or E-
						y = str.substring(b, f);
						str = str.substring(f + 2);
						
						System.out.println("yy= " + y);
						System.out.println("What is left= " + str);
						
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
							return rval;
								
						}else{//no digits after E+, real number, put E+ back and return
							
							String rval = "E+".concat(str);
							Token t = checkRealErrors(x, y, listFile, tokenFile, lineNum);
							tokenFile.printToken(t);
	
							return rval;						
						}
					}//end check for e+ 
					
					else //check for e-
					if ((str.charAt(f) == 'E'	&& str.charAt(f + 1) == '-') ){
						//debugging
						System.out.println("IN E-");
						
						//if there was an E+ or E-
						y = str.substring(b, f);
						str = str.substring(f + 2);
						
						//debugging
						System.out.println("yy= " + y);
						System.out.println("What is left= " + str);
						
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
							return rval;
						}else {//no digits afer E-, Real number, put E- back and return
							
							String rval = "E-".concat(str);
							Token t = checkRealErrors(x, y, listFile, tokenFile, lineNum);
	
							return rval;
						}
					}//end check for e-
					
					else if (str.charAt(f) == 'E') {
						
						//if there was an exponent get the y portion
						y = str.substring(b, f);
						str = str.substring(f + 1);		
						f = 0;
						boolean e = false;
						
						//debugging
						System.out.println("Inside of if 'E' ");
						System.out.println("x= " + x);
						System.out.println("y= " + y);
						System.out.println("str= " + str);
						
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
							return rval;
						}else{//no digits after E, Real number, put E back and return
							String rval = "E".concat(str);
							Token t = checkRealErrors(x, y, listFile, tokenFile, lineNum);
							
							return rval;
						}
				}//end else if 'E'
				else {//digits after decimal did not end in E 
					//it is a real number
					y = (str.substring( b, f ));
					String rval = str.substring( f );
					Token t = checkRealErrors(x, y, listFile, tokenFile, lineNum);
					
					return rval;
				}

			} else{ //end digits after decimal ie. no digits after decimal
				//it was an integer
				
				String tok = str.substring(b,f);
				String rval = str.substring( f );
				Token t = checkIntErrors(tok, tokenFile, listFile, lineNum);
				
				return rval;
				
			}//end else digits after decimal
		
		}else{ //end decimal if  //it was an integer
			String tok = str.substring(b,f);
			String rval = str.substring(f);
			Token t = checkIntErrors(tok, tokenFile, listFile, lineNum);
			
			return rval;

		}//end decimal else
		}else {//end if (C) there were digits in the first place
			return str;
		}//end else, there were digits

	} //end of check method	
	
	
	boolean checkZero(String str) {
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
				"Leading zero, xx too long, yy too long, zz too long");
			return t;
		}else  //end leadzero, longx, longy, longz
		
		//leading zero, x too big, y too big
		if (checkZero(x) && longXY(x) && longXY(y)) {

			//put the number back together
			String tok = x.concat(y);
			tok = tok.concat(z);

			Token t = new Token(tok, Token.ERROR, Token.ZEROLONGXY, lineNum);
			
			listFile.printError("Leading zero error, xx too long, yy too long");
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
				"Leading zero error, xx too long, zz too long");
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
				"Leading zero error, yy too long, zz too long");
			return t;
		} else //end zerolongyz error

		//leading zero, x too big
		if (checkZero(x) && longXY(x)) {
	
			//put the number back together
			String tok = x.concat(y);
			tok = tok.concat(z);

			Token t =
				new Token(tok, Token.ERROR,	Token.ZEROLONGX, lineNum);
			
			listFile.printError("Leading zero error, xx too long");
			return t;
		} else //end lead zero, x too big

		//leading zero, y too big
		if (checkZero(x) && longXY(y)) {
			
			//put the nubmer back together
			String tok = x.concat(y);
			tok = tok.concat(z);
		
			Token t = new Token(tok, Token.ERROR, Token.ZEROXLONGY,	lineNum);
			
			listFile.printError("Leading zero error, yy too long");
			return t;
		} else //end leading zero, z too big

		//leading zero, z too big
		if (checkZero(x) && longZ(z)) {
		
			//put string back together
			String tok = x.concat(y);
			tok = tok.concat(z);
		
			Token t = new Token(tok, Token.ERROR, Token.ZEROXLONGZ,	lineNum);
			
			listFile.printError("Leading zero error, zz too long");
			return t;
		} else //end leading zero, z too big

		//xx too long, yy too long, zz too long
		if (longXY(x) && longXY(y) && longZ(z)) {
			
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGXYZ, lineNum);
			
			listFile.printError("xx too long, yy too long, zz too long");
			return t;
		} else //end xx, yy, zz too long

		//xx too long, yy too long
		if (longXY(x) && longXY(y)) {
			
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGXY,	lineNum);
			
			listFile.printError("xx too long, yy too long");
			return t;
		} else //end of xx, yy too long
		
		//xx, zz too long
		if (longXY(x) && longZ(z)) {
		
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGXZ, lineNum);
			
			listFile.printError("xx too long, zz too long");
			return t;
		} else //end long xx, zz

		//yy, zz too long
		if (longXY(y) && longZ(z)) {
		
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);

			Token t = new Token(tok, Token.ERROR, Token.LONGYZ, lineNum);
			
			listFile.printError("yy too long, zz too long");
			return t;
		}else //end yy, zz too long

		//x too big
		if (longXY(x)) {
			//put number back together
			String tok = x.concat(y); 
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGX, lineNum);
			
			listFile.printError("xx too long");
			return t;
		} else //end x too big

		//y too big
		if (longXY(y)) {
			
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGY, lineNum);
			
			listFile.printError("yy too long");
			return t;
		} else //end y too big

		//z too big
		if (longZ(z)) {
		
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
			
			Token t = new Token(tok, Token.ERROR, Token.LONGZ, lineNum);
			
			listFile.printError("zz too long");
			return t;
		} else //end z too big

		//leading zero in x error
		if (x.charAt(0) == '0') {
		
			//put number back together
			String tok = x.concat(y);
			tok = tok.concat(z);
		
			Token t = new Token(tok, Token.ERROR, Token.ZEROX, lineNum);
			
			listFile.printError("Leading zero error");
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
	
	Token checkRealErrors(String x, String y, ListFileMaker listFile, TokenFileMaker tokenFile, int lineNum){
		//leading zero, x too big, y too big
		 if (checkZero(x) && longXY(x) && longXY(y)) {
								
			//put the number back together
			String tok = x.concat(".");
			tok = tok.concat(y);
							
			Token t = new Token(tok, Token.ERROR, Token.ZEROLONGXY, lineNum);
			tokenFile.printToken(t);
			listFile.printError("Leading zero error, xx too long, yy too long");
			return t;
							
		  } else //end zerolongxy error
		  	  //leading zero, x too big
			  if (checkZero(x) && longXY(x)) {
						
				  //put the number back together		
				  String tok = x.concat(".");
				  tok = tok.concat(y);
			
				  Token t = new Token(tok, Token.ERROR, Token.ZEROLONGX, lineNum);
				  tokenFile.printToken(t);
				  listFile.printError("Leading zero error, xx too long");
				  return t;
						
			} else //end lead zero, x too big
								
			//leading zero, y too big
			if (checkZero(x) && longXY(y)) {
						
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
						
				Token t =new Token(tok,Token.ERROR,	Token.ZEROLONGX,lineNum);
				tokenFile.printToken(t);
				listFile.printError("Leading zero error, yy too long");
				return t;
			} else //end leading zero, y too big
						
			//xx too long, yy too long
			if (longXY(x) && longXY(y)) {
						
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
						
				Token t =new Token(tok,	Token.ERROR,Token.ZEROLONGX,lineNum);
				tokenFile.printToken(t);
				listFile.printError("xx too long, yy too long");
				return t;
			} else //end xx, yy too long
						 
			//x too big
			if (longXY(x)) {
							
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
						
				Token t = new Token(tok,Token.ERROR,Token.LONGX,lineNum);
				tokenFile.printToken(t);
				listFile.printError("xx too long");
				return t;
			} else //end x too big
						
			//y too big
			if (longXY(y)) {
						
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
						
				Token t =new Token(tok,Token.ERROR,Token.LONGY,lineNum);
				tokenFile.printToken(t);
				listFile.printError("yy too long");
				return t;
			} else //end y too big
						
			//leading zero in x error
			if (x.charAt(0) == '0') {
						
				//put the nubmer back together
				String tok = x.concat(".");
				tok = tok.concat(y);
					
				Token t =new Token(tok,Token.ERROR,Token.ZEROX,lineNum);
				tokenFile.printToken(t);
				listFile.printError("Leading zero error");
				return t;
			} else {//end leading zero x error
				
				//the number is a well formed real
				//put the number back together
				String tok = x.concat(".");
				tok = tok.concat(y);
				Token t =new Token(tok, Token.NUM, Token.REAL, lineNum);
				tokenFile.printToken(t);
				return t;
			}//end number is well formed
	}//end checkRealErrors method
	
	Token checkIntErrors(String x, TokenFileMaker tokenFile, ListFileMaker listFile, int lineNum){
		//leading zero and int length error check
		if (longInt(x) && checkZero(x)) {
			Token t = new Token(x, Token.ERROR, Token.ZEROINT, lineNum);
			listFile.printError("Leading zero error and Integer too long");
			tokenFile.printToken(t);
					
			return t;
		} else //integer length check
		
		if (longInt(x)) {
			Token t = new Token(x, Token.ERROR, Token.LONGINT, lineNum);
			listFile.printError("Integer too long");
			tokenFile.printToken(t);
						
			return t;
		} else //leading zero error
		if (checkZero(x)) {
			Token t = new Token(x, Token.ERROR, Token.ZEROLONGINT, lineNum);
			listFile.printError("Leading zero error");
			tokenFile.printToken(t);
						
			return t;
		} else {
			//integer is well formed 				
			//create token
			Token t = new Token(x, Token.NUM, Token.INT, lineNum);
			//print token to token file
			tokenFile.printToken(t);
							
			return t;
		} //return what is left over after token taken off
		
	}//end checkIntErrors method

} //end RealEMachine


/*
class RealMachine extends Machine {

	public static void main(String[] args) {
	}

	String check(
		String str,
		ListFileMaker listFile,
		TokenFileMaker tokenFile,
		int lineNum,
		ArrayList resWord,
		SymbolFileMaker symbolFile) {
			
			int b = 0;//back pointer
			int f = 0;	//front pointer
	
			boolean c = false;
			while ((str.charAt(f) >= '0') && (str.charAt(f) <= '9')) {

				f++;
				c = true;
			} //if it starts with a digit, get all the digits
			if (c) { //if there were digits check the next character?
				if ((str.charAt(f)) == '.') {
				
					String x = str.substring(b, f);
					str = str.substring(f+1);
					f = 0;
					boolean d = false;
				
					while ((str.charAt(f) >= '0') && (str.charAt(f) <= '9')) {
						f++;
						d = true;
					} //while there are more digits
				
				if (d) { //if there were more digits after the decimal
					if (str.charAt(f) == ' '
						|| str.charAt(f) == '\t'
						|| str.charAt(f) == '\n'
						|| str.charAt(f) == '`') {
						
						String y = str.substring(b, f);
						str.substring(f);
						
						//leading zero, x too big, y too big
						if (checkZero(x) && longXY(x) && longXY(y)) {
								
							//put the number back together
							String tok = x.concat(".");
							tok = tok.concat(y);
							
							Token t =
								new Token(
									tok,
									Token.ERROR,
									Token.ZEROLONGXY,
									lineNum);
							tokenFile.printToken(t);
							listFile.printError(
								"Leading zero error, xx too long, yy too long");
							return str;
							
						} else //end zerolongxy error
						
						//leading zero, x too big
						if (checkZero(x) && longXY(x)) {
						
							//put the number back together		
							String tok = x.concat(".");
							tok = tok.concat(y);
						
							Token t = new Token(tok, Token.ERROR, Token.ZEROLONGX, lineNum);
							tokenFile.printToken(t);
							listFile.printError("Leading zero error, xx too long");
							return str;
							
						} else //end lead zero, x too big
								
						//leading zero, y too big
						if (checkZero(x) && longXY(y)) {
						
							//put the number back together
							String tok = x.concat(".");
							tok = tok.concat(y);
						
							Token t =new Token(tok,Token.ERROR,	Token.ZEROLONGX,lineNum);
							tokenFile.printToken(t);
							listFile.printError("Leading zero error, yy too long");
							return str;
						} else //end leading zero, y too big
						
						//xx too long, yy too long
						if (longXY(x) && longXY(y)) {
						
							//put the number back together
							String tok = x.concat(".");
							tok = tok.concat(y);
						
							Token t =new Token(tok,	Token.ERROR,Token.ZEROLONGX,lineNum);
							tokenFile.printToken(t);
							listFile.printError("xx too long, yy too long");
							return str;
						} else //end xx, yy too long
						 
						//x too big
						if (longXY(x)) {
							
							//put the number back together
							String tok = x.concat(".");
							tok = tok.concat(y);
							
							Token t = new Token(tok,Token.ERROR,Token.LONGX,lineNum);
							tokenFile.printToken(t);
							listFile.printError("xx too long");
							return str;
						} else //end x too big
						
						//y too big
						if (longXY(y)) {
						
							//put the number back together
							String tok = x.concat(".");
							tok = tok.concat(y);
						
							Token t =new Token(tok,Token.ERROR,Token.LONGY,lineNum);
							tokenFile.printToken(t);
							listFile.printError("yy too long");
							return str;
						} else //end y too big
						
						//leading zero in x error
						if (x.charAt(0) == '0') {
						
							//put the nubmer back together
							String tok = x.concat(".");
							tok = tok.concat(y);
						
							Token t =new Token(tok,Token.ERROR,Token.ZEROX,lineNum);
							tokenFile.printToken(t);
							listFile.printError("Leading zero error");
							return str;
						} else {//end leading zero x error
				
							//the number is a well formed real
							//put the number back together
							String tok = x.concat(".");
							tok = tok.concat(y);
							Token t =new Token(tok, Token.NUM, Token.REAL, lineNum);
							tokenFile.printToken(t);
							return str;
						}//end number is well formed
					} else{//not a real number
					
						String rval = x.concat(str);
						return rval;
					}
				}else {//no digits after decimal
					
					String tok = x.concat(str);
					while(tok.charAt(f) != ' ' && tok.charAt(f) != '`'){
						f++;
					}
					tok = tok.substring( b, f );
					Token t = new Token(tok, Token.ERROR, Token.UNRECOG, lineNum );
					tokenFile.printToken(t);
					String rval = tok.substring(f);
					return rval;
				}
			} else {//end decimal if
				return str;//
			}
		}else //end if there were digits check first character
			return str; //there were no digits
			
	} //end of check method
	
	boolean checkZero(String str) {
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
	
} //end Real Machine
*/

/*
class IntMachine extends Machine {

	public static void main(String[] args) {
	}

	String check(
		String str,
		ListFileMaker listFile,
		TokenFileMaker tokenFile,
		int lineNum,
		ArrayList resWord,
		SymbolFileMaker symbolFile) {
			
		int b = 0;
		//back pointer
		int f = 0;
		//front pointer
		boolean c = false;
		
		//debug
		System.out.println("String is " + str);
		
		while ((str.charAt(f) >= '0') && (str.charAt(f) <= '9')) {
			f++;
			c = true;
		}
		if (c) { //if there were some digits
			if (str.charAt(f) == ' '
				|| str.charAt(f) == '\t'
				|| str.charAt(f) == '\n'
				|| str.charAt(f) == '`') {
				//if there were only digits then a space, tab, newline, or EOB (`)
				String sub = str.substring(b, f);
				//create token string
				
				//leading zero and int length error check
				if (sub.length() > 10 && sub.charAt(0) == '0') {
					Token t =
						new Token(sub, Token.ERROR, Token.ZEROINT, lineNum);
					listFile.printError(
						"Leading zero error and Integer too long");
					tokenFile.printToken(t);
					
					return str.substring(f);
				} else //integer length check
					if (sub.length() > 10) {
						Token t =
							new Token(sub, Token.ERROR, Token.LONGINT, lineNum);
						listFile.printError("Integer too long");
						tokenFile.printToken(t);
						
						return str.substring(f);
					} else //leading zero error
						if (sub.charAt(b) == '0' && sub.length() > 1) {
							Token t =
								new Token(
									sub,
									Token.ERROR,
									Token.ZEROLONGINT,
									lineNum);
							listFile.printError("Leading zero error");
							tokenFile.printToken(t);
						
							return str.substring(f);
						} else {
							//integer is well formed 				
							//create token
							Token t =
								new Token(sub, Token.NUM, Token.INT, lineNum);
							//print token to token file
							tokenFile.printToken(t);
							
							return str.substring(f);
						} //return what is left over after token taken off
			} else {
				
				System.out.println("In int machine string is " + str+ " f is " + f);
				//if the next character was not a space then this is not an int
				if (str.charAt(f) != ' '|| str.charAt(f) != '`'){
					while(str.charAt(f) != ' ' && str.charAt(f) != '`'){//get the rest of the unrecognized string
						f++;
					}
				}
				Token t = new Token(str.substring(b, f), Token.ERROR, Token.UNRECOG, lineNum);
				listFile.printError("Unrecognized String");
				tokenFile.printToken(t);
				
				return str.substring(f);
			}
		} //if there were come digits (c = true)
		else{
			return str; 
		}
	} //end check
} //end Int Machine
*/

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
		SymbolFileMaker symbolFile) {
			
			int b = 0;
			int f = 0;
			
			//check for dots
			
			if(str.charAt(f) == '.'){
				if(str.charAt(f+1)=='.'){//if is it ".."
					String tok = str.substring(b, f+2);
										
					//debug
					System.out.println("token value " + tok);
					str = str.substring(f+2);
					Token t = new Token(tok, Token.SYMBOL, Token.DD, lineNum);
					tokenFile.printToken(t);
				}else {//it is single dot
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.SYMBOL, Token.DOT, lineNum);
					tokenFile.printToken(t);
				}
			}else
			
			
			//check for assignop or colon
			if (str.charAt(f)==':'){
					if (str.charAt(f+1)=='='){//assignop
						String tok = str.substring(b, f+2);
						str = str.substring(f+2);
						Token t = new Token(tok, Token.SYMBOL, Token.ASSIGN, lineNum);
						tokenFile.printToken(t);						
					}else {//colon
						String tok = str.substring(b, f+1);
						str = str.substring(f+1);
						Token t = new Token(tok, Token.SYMBOL, Token.COLON, lineNum);
						tokenFile.printToken(t);		
					}
				}
				else if(str.charAt(f)=='+'){
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.ADDOP, Token.PLUS, lineNum);
					tokenFile.printToken(t);
				}
				else if(str.charAt(f)=='-'){
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.ADDOP, Token.MINUS, lineNum);
					tokenFile.printToken(t);
				}
				else if(str.charAt(f)=='*'){
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.MULOP, Token.ASTERISK, lineNum);
					tokenFile.printToken(t);
				}
				else if(str.charAt(f)=='/'){
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.MULOP, Token.SLASH, lineNum);
					tokenFile.printToken(t);
				}
			else if(str.charAt(f)=='['){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SYMBOL, Token.OPENSQ, lineNum);
				tokenFile.printToken(t);
			}
			else if(str.charAt(f)==']'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SYMBOL, Token.CLOSESQ, lineNum);
				tokenFile.printToken(t);
			}
			else if(str.charAt(f)=='('){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SYMBOL, Token.OPENPRN, lineNum);
				tokenFile.printToken(t);
			}
			else if(str.charAt(f)==')'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SYMBOL, Token.CLOSEPRN, lineNum);
				tokenFile.printToken(t);
			}
			else if(str.charAt(f)==';'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SYMBOL, Token.SEMI, lineNum);
				tokenFile.printToken(t);
			}
			else if(str.charAt(f)==','){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SYMBOL, Token.COMMA, lineNum);
				tokenFile.printToken(t);
			}
			else if(str.charAt(f)=='{'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SYMBOL, Token.OPENCR, lineNum);
				tokenFile.printToken(t);
			}
			else if(str.charAt(f)=='}'){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SYMBOL, Token.CLOSECR, lineNum);
				tokenFile.printToken(t);
			}else if(str.charAt(f)== '='){
				String tok = str.substring(b, f+1);
				str = str.substring(f+1);
				Token t = new Token(tok, Token.SYMBOL, Token.EQUALTO, lineNum);
				tokenFile.printToken(t);
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
		SymbolFileMaker symbolFile) {
			
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
		SymbolFileMaker symbolFile) {
			
			int b = 0;
			int f = 0;
			
			if(str.charAt(f)=='<'){
				if(str.charAt(f+1) == '>'){//not equal
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.RELOP, Token.NE, lineNum);
					tokenFile.printToken(t);
				}else if(str.charAt(f+1)=='='){//less than or equal
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.RELOP, Token.LE, lineNum);
				}else{//less than
					String tok = str.substring(b, f);
					str = str.substring(f);
					Token t = new Token(tok, Token.RELOP, Token.LESS, lineNum);
					tokenFile.printToken(t);
				}
			}else if(str.charAt(f)=='>'){
				if(str.charAt(f+1)=='='){//greater than or equal
					String tok = str.substring(b, f+1);
					str = str.substring(f+1);
					Token t = new Token(tok, Token.RELOP, Token.GE, lineNum);
					tokenFile.printToken(t);
				}else{//greater than
					String tok = str.substring(b, f);
					str = str.substring(f);
					Token t = new Token(tok, Token.RELOP, Token.GREATER, lineNum);
					tokenFile.printToken(t);
				}
			}else if(str.charAt(f)=='='){
				String tok = str.substring(b, f);
				str = str.substring(f);
				Token t = new Token(tok, Token.RELOP, Token.EQUALTO, lineNum);
				tokenFile.printToken(t);
			}
			
			return str;

	} //end check
}
/*
class NumMachine extends Machine{

	public static void main(String[] args) {
	}

	String check(
		String str,
		ListFileMaker listFile,
		TokenFileMaker tokenFile,
		int lineNum,
		ArrayList resWord,
		SymbolFileMaker symbolFile) {
			int b = 0; // back pointer
			int f = 0; // front pointer
			String x = "";
			String y = "";
			String z = "";
			
			if(str.charAt(f)>='0' && str.charAt(f)<='9'){
				
				while(str.charAt(f)>='0' && str.charAt(f)<='9'){
					f++;
				}//get the x portion
				
				if(str.charAt(f)=='.'){//keep looking for numbers after the decimal
					f++;
					if(str.charAt(f)>='0'&& str.charAt(f)<='9'){//check for numbers after the decimal
						
					}else {//malformed 
						
					}
				}else{//it must be an integer
					x = str.substring(b,f);
					str = str.substring(f);
					return str;
				}
				
			}else { //not a digit
				return str;
			}
			
		}
}
*/