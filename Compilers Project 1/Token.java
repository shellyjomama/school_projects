/*
 * Created on Sep 24, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author shellyjo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Token {
	public static final int PROG = 1;
	public static final int VAR = 2;
	public static final int ARRAY = 3;
	public static final int OF = 4;
	public static final int FUNCTION = 5;
	public static final int PROCEDURE = 6;
	public static final int BEGIN = 7;
	public static final int END = 8;
	public static final int IF = 9;
	public static final int THEN = 10;
	public static final int ELSE = 11;
	public static final int WHILE = 12;
	public static final int DO = 13;
	public static final int NOT = 14;
	public static final int EOF = 15;
	
	public static final int NUM = 20;
	public static final int INT = 21;
	public static final int REAL = 22;
	
	public static final int MULOP = 30;
	public static final int ASTERISK = 31;
	public static final int SLASH = 32;
	public static final int DIV = 33;
	public static final int MOD = 34;
	public static final int AND = 35;
	
	public static final int ADDOP = 40;
	public static final int PLUS = 41;
	public static final int MINUS = 42;
	public static final int OR = 43;
	
	public static final int RELOP = 50;
	public static final int EQUALTO = 51;
	public static final int NE = 52;
	public static final int LESS = 53;
	public static final int LE = 54;
	public static final int GREATER = 55;
	public static final int GE = 56;
	
	public static final int SYMBOL = 60;
	public static final int DD = 61;
	public static final int DOT = 62;
	public static final int ASSIGN = 63;
	public static final int COLON = 64;
	public static final int OPENSQ = 65;
	public static final int CLOSESQ = 66;
	public static final int OPENPRN = 67;
	public static final int CLOSEPRN = 68;
	public static final int SEMI = 69;
	public static final int COMMA = 59;
	public static final int OPENCR = 58;
	public static final int CLOSECR = 57;
	public static final int ID = 100;
	
	public static final int ERROR = 70;
	public static final int LONGINT = 71;
	public static final int ZEROINT = 72;
	public static final int ZEROLONGINT = 73;
	public static final int LONGID = 74;
	public static final int LONGX = 75;
	public static final int LONGY = 76;
	public static final int LONGZ = 77;
	public static final int LONGXY = 78;
	public static final int LONGXZ = 79;
	public static final int LONGYZ = 80;
	public static final int LONGXYZ = 81;
	public static final int ZEROX = 82;
	public static final int ZEROLONGX = 83;
	public static final int ZEROLONGXY = 84;
	public static final int ZEROLONGXZ = 85;
	public static final int ZEROLONGXYZ = 86;
	public static final int ZEROXLONGY = 87;
	public static final int ZEROXLONGZ = 88;
	public static final int ZEROXLONGYZ = 89;
	public static final int UNRECOG = 90;
	
	String value = "";
	int type = 0;
	int attribute = 0;
	int lineNum = 0;
	boolean initialized = false;
	
	public static void main(String[] args) {
	}

	public Token (String v, int t, int a, int line){
		value = v;
		type = t;
		attribute = a;
		lineNum = line;
		
		//tokenDebugger();
	}
	
	public String getValue(){
		return this.value;
	}
	
	public int getType(){
		return this.type;
	}
	
	public int getAttribute(){
		return this.attribute ;
	}
	
	public int getLineNum(){
		
		return lineNum;
	}
	
	public boolean isInitialized(){
		return initialized;
	}
	
	public void initialize(){
		this.initialized = true;
	}
	
	public void setValue(String v){
		this.value = v;
	}
	
	public void tokenDebugger(){
		System.out.println(getLineNum() +" "+ getValue() +" "+ getType() +" "+ getAttribute());
		System.out.println("line number= " + getLineNum() );
	}
	
}//end of Token class
