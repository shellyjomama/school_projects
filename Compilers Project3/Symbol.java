/*
 * Created on Dec 15, 2004
 */

/**
 * @author shellyjo
 */

public class Symbol {
	final static int PGN = 1;
	final static int PGP = 2;
	final static int PPINT = 3;
	final static int PPREAL = 4;
	final static int PPAINT = 5;
	final static int PPAREAL = 6;
	final static int INT = 7;
	final static int REAL = 8;
	final static int AINT = 9;
	final static int AREAL = 10;
	final static int PROCN = 11;
	final static int ERR = 13;
	final static int BOOL = 99;
	
	private Token token = null;
	private String path = null;
	private int nodeType = 0;
	
	public static void main(String[] args) {
	}//end main
	
	public Symbol(Token t, String p){
		this.path = p;
		this.token = t;
	}//end Symbol constructor1 
	
	public Symbol(Token t, String p, int nt){
		this.token = t;
		this.path = p;
		this.nodeType = nt;
	}//end Sybmol constructor2
	
	public Token getToken(){
		return this.token;
	}//end getToken()
	
	public int getNodeType() {
		return nodeType;
	}//end getNodeType
	
	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}//end setNodeType

	public String getPath() {
		return path;
	}//end getPath()

	public void setPath(String path) {
		this.path = path;
	}//end setPath
	
}//end Symbol class
