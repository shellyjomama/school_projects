/*
 * Created on Oct 27, 2004
 */
import java.util.*;
import java.io.*;
/**
 * @author shellyjo - Shelly Seier
 */
public class Parser {
	Lex l = new Lex();//the lexical analyzer
	Token tok = null;
	Token tokenHold = null;
	Symbol symbolHold = null;
	boolean sync = false;
	Stack stack = null;
	Stack offset = null;
	Stack informal = null;
	SymbolFileMaker symbolTable = null;
	PrintWriter pw = null;
	File address = null;
	
	public static void main(String[] args) {
		Parser p = new Parser();
		p.symbolTable = new SymbolFileMaker();
		p.stack = new Stack();
		p.offset = new Stack();
		p.informal = new Stack();
		
		try{
			p.address = new File("address.txt");
			p.address.createNewFile();
			p.pw = new PrintWriter(p.address);
		}catch(IOException e){
			System.out.println("Cannot create address file " + e);
		}
		
		p.parse();
		p.symbolTable.printSymbolFile();
		
		//debugging
		if(p.stack.isEmpty())
			System.out.println("Stack is empty");
		else
			System.out.println("Stack is " + p.stack.peek());
		p.pw.close();
		
	}//end main methood
	
	public void parse(){
		tok = l.getToken();
		prog();
		match(Token.EOF);
		l.finish();
		//match(Token.EOF);
	}//end parse()
	
	public void match(int t){
		tokenHold = tok;
		if(tok.type == t){
			if(t != Token.EOF){
				tok = l.getToken();
				//System.out.println("Token received in match() is " + tok.display());
			}else{
				//System.out.println("Success! Parsing complete");
				l.lfm.printError( "Success! Parsing complete" );
			}
		}else{
			//System.out.println("Syntax error: In Match, Expecting " + tok.converter(t) + " received " + tok.converter(tok.type));
			l.lfm.printError("Syntax error: In Match, Expecting " + tok.converter(t) + " received " + tok.converter(tok.type));
		}
	}//end match(Token t)
	
	public void prog(){
		//debugging
		//System.out.println("In prog()");
		
		switch (tok.type) {
			//prog -> prog id ( idlist ) ; prog’’
			case(Token.PROG): match(Token.PROG); 
			
				//match ID then bird walk
				match(Token.ID); 
				rootNode(tokenHold, Symbol.PGN);
				//newScope(tokenHold);
								
				//pick up production rules here
				match(Token.OPENPRN); idlist(); match(Token.CLOSEPRN); match(Token.SEMI); progPP();
				
				break;
				
			default: System.out.println("Syntax error: In prog() Expecting " + tok.converter(Token.PROG) + " received " + tok.converter(tok.type));
				l.lfm.printError("Syntax error: In prog() Expecting " + tok.converter(Token.PROG) + " received " + tok.converter(tok.type));
				//Sync = { EOF }
				while(tok.type != Token.EOF){
					tok = l.getToken();
					//System.out.println("In prog default token is " + tok.display());
				}//end while()
		}//end switch
	}//end of prog()
	
	public void progPP(){
		//debugging
		//System.out.println("In progPP()");
		
		switch(tok.type){
			//prog’’ -> compstmt .
			case(Token.BEGIN): compstmt(); match(Token.DOT); 
			
				//program is finised pop stack after "end."
				popStack();
				break;
				
			//prog’’ -> subdeclars compstmt .
			case(Token.PROCEDURE): subdeclars(); compstmt(); match(Token.DOT);
			
				//program is finised pop stack after "end."
				popStack();
				break;
				
			//prog’’ -> declar prog’’’
			case(Token.VAR): declar(); progPPP();
				break;
			
			default: System.out.println("Syntax Error: In progPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + ", or " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead"); 
				l.lfm.printError("Syntax Error: In progPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + ", or " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { EOF }
				while(tok.type != Token.EOF){
					tok = l.getToken();
					System.out.println("In progPP default token is " + tok.display());
				}//end while
		}//end switch
	}//end progPP
	
	public void progPPP(){
		//debugging
		//System.out.println("In progPPP()");
		
		switch(tok.type){
			//prog’’’ -> compstmt .
			case(Token.BEGIN): compstmt(); match(Token.DOT);
			
				//program is finised pop stack after "end."
				popStack();
				break;
				
			//prog’’’ -> subdeclars compstmt .
			case(Token.PROCEDURE): subdeclars(); compstmt(); match(Token.DOT);
				//program is finised pop stack after "end."
				popStack();
				break;
				
			default: System.out.println("Syntax Error: In progPPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In progPPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { EOF }
				while(tok.type != Token.EOF){
					tok = l.getToken();
					System.out.println("Token in progPPP is " + tok.display());
				}//end while
				
		}//end switch
	}//end progPPP
	
	public void idlist(){
		//debugging
		//System.out.println("In idlist()");
		Symbol s = null;
		switch (tok.type){
			case(Token.ID): 
				
				//production rule
				match(Token.ID);
				childNode(tokenHold, Symbol.PGP);
				s = getSymbolHold();
				s.setNodeType(Symbol.PGP);
				symbolTable.addSymbol(s);
				
				//pick up production rule
				idlistP();
				break;
			
			default: System.out.println("Syntax Error: In idlist() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In idlist() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");	
			//Sync = { ) EOF }
			while(tok.type != Token.CLOSEPRN && tok.type != Token.EOF){
				tok = l.getToken();
				System.out.println("In idlist default token is " + tok.display());
			}
		}//end switch
	}//end idlist()
	
	public void idlistP(){
		//debugging
		//System.out.println("In idlistP()");
		Symbol s = null;
		switch(tok.type){
			//idlist’ -> , id idlist’
			case(Token.COMMA): match(Token.COMMA); 
			
				//match id and bird walk
				match(Token.ID); 
				childNode(tokenHold, Symbol.PGP);
				s = getSymbolHold();
				s.setNodeType(Symbol.PGP);
				symbolTable.addSymbol(s);
				
				//pick up production rules here
				idlistP();
				break;
			
			//idlist’ -> ?
			case(Token.CLOSEPRN): //do nothing
				break;
								
			default: System.out.println("Syntax Error: In idlistP() Expected to receive " + tok.converter(Token.BEGIN) + ", or " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError( "Syntax Error: In idlistP() Expected to receive " + tok.converter(Token.BEGIN) + ", or " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead" );
				//Sync = { ) EOF }
				while(tok.type != Token.CLOSEPRN && tok.type != Token.EOF){
					tok = l.getToken();
					System.out.println("In idlistP default token is " + tok.display());
				}//end while
		}//end switch()
	}//end idlistP()
	
	public void declar(){
		//debugging
		//System.out.println("In declar()");
		int symbolType = 0;
		Symbol s = null;
		switch(tok.type){

			//declar -> var id : type ; declar’
			case(Token.VAR): match(Token.VAR); 
			
				//match id and bird walk
				match(Token.ID); 
				Symbol symbol = childNode(tokenHold, 0);
				
				//pick back up with production rules here
				match(Token.COLON); 
				
				//match type
				symbolType = type();
				if(symbol.getNodeType() > 0){
					symbol.setNodeType(symbolType);
					symbolTable.addSymbol(symbol);
				}
				
				//production rules again
				match(Token.SEMI); declarP();
				break;
			
			default: System.out.println("Syntax Error: In declar() Expected to receive " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In declar() Expected to receive " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { begin procedure EOF }
				while(tok.type != Token.BEGIN && tok.type != Token.PROCEDURE && tok.type != Token.EOF){
					tok = l.getToken();
					System.out.println("In declar default token is " + tok.display());
				}//end while
				//s = getSymbolHold();
				//s.setNodeType(Symbol.ERR);
				//symbolTable.addSymbol(s);
		}//end switch()
	}//end declar()
	
	public void declarP(){
		//debugging
		//System.out.println("In declarP()");
		int symbolType = 0;
		Symbol s = null;
		switch (tok.type){
			//declar’ -> ?
			case(Token.BEGIN): //do nothing
				break;
			
			//declar’ -> ?
			case(Token.PROCEDURE): //do nothing
				break;
				
			//declar’ -> var id : type ; declar’
			case(Token.VAR): match(Token.VAR); 
				
				//match id here then bird walk
				match(Token.ID); 
				Symbol symbol = childNode(tokenHold, 0);
				
				//pick back up with production rules here
				match(Token.COLON); 
				
				//match type
				symbolType = type();
				if(symbol.getNodeType() > 0){
					symbol.setNodeType(symbolType);
					symbolTable.addSymbol(symbol);
				}
				
				//production rules again
				match(Token.SEMI); declarP();
				break;
			
			default: System.out.println("Syntax Error: In declarP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + ", " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In declarP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + ", " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { begin procdure EOF }
				while(tok.type != Token.BEGIN && tok.type != Token.PROCEDURE && tok.type != Token.EOF){
					tok = l.getToken();
					System.out.println("In declarP default token is " + tok.display());
				}//end while
				//s = getSymbolHold();
				//s.setNodeType(Symbol.ERR);
				//symbolTable.addSymbol(s);
		}//end switch()
	}//end declarP()
	
	public int type(){
		//debugging
		//System.out.println("In type()");
		int rval = 0;
		int determine = 0;
		
		switch(tok.type){
			//type -> array [ num . . num ] of standtype
			case(Token.ARRAY): 
				match(Token.ARRAY); 
				match(Token.OPENSQ); 
				match(Token.NUM);
				
				String b = tokenHold.value;
				Integer b1 = new Integer(b);
				int begin = b1.intValue();
				
				match(Token.DD); 
				match(Token.NUM); 
				
				String e = tokenHold.value;
				Integer e1 = new Integer(b);
				int end = b1.intValue();
				
				int arraySize = begin - end + 1;
				
				match(Token.CLOSESQ); 
				match(Token.OF); 
				determine = standtype();
				rval = getArrayType(determine);
				
				int off = 0;				
				if(rval == Symbol.AINT){
					off = 4;
				}//end if
				if(rval==Symbol.AREAL){
					off=8;
				}//end if
					
				arraySize = arraySize*off;
				
				updateOffset(symbolHold.getToken().value, arraySize);
				break;
				
			//type -> standtype
			case(Token.INT): rval = standtype();
				updateOffset(symbolHold.getToken().value, 4);
				break;
				
			//type -> standtype
			case(Token.REAL): rval = standtype();
			updateOffset(symbolHold.getToken().value, 8);
				break;
			
			default: System.out.println("Syntax Error: In type() Expected to receive " + tok.converter(Token.ARRAY) + ", " + tok.converter(Token.INT) + ", " + tok.converter(Token.REAL) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In type() Expected to receive " + tok.converter(Token.ARRAY) + ", " + tok.converter(Token.INT) + ", " + tok.converter(Token.REAL) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ; ) EOF}
				while(tok.type!=Token.SEMI && tok.type!=Token.CLOSEPRN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In type default token is " + tok.display());
				}//end while
				rval = Symbol.ERR;
		}//end switch()
		return rval;
	}//end type()
	
	public int standtype(){
		//debugging
		//System.out.println("In standtype()");
		int rval = 0;
		switch(tok.type){
			//standtype -> integer
			case(Token.INT): match(Token.INT);
				rval = Symbol.INT;
				break;
			
			//standtype -> real
			case(Token.REAL): match(Token.REAL);
				rval = Symbol.REAL;
				break;
				
			default: System.out.println("Syntax Error: In standtype() Expected to receive " + tok.converter(Token.INT) + ", " + tok.converter(Token.REAL) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In standtype() Expected to receive " + tok.converter(Token.INT) + ", " + tok.converter(Token.REAL) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ; ) EOF}
				while(tok.type!=Token.SEMI && tok.type!=Token.CLOSEPRN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In standtype default token is " + tok.display());
				}//end while
				rval = Symbol.ERR;
		}//end switch()
		return rval;
		
	}//end standtype()
	
	public void subdeclars(){
		//debugging
		//System.out.println("In subdeclars() ");
		
		switch(tok.type){
			//subdeclars -> subdeclar ; subdeclars’
			case(Token.PROCEDURE): subdeclar(); 
			
				//match ; then pop stack because this is the end of procedure
				match(Token.SEMI);
				popStack();
			
				subdeclarsP();
				break;
			
			default: System.out.println("Syntax Error: In subdeclars() Expected to receive " + tok.converter(Token.PROCEDURE) + "Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In subdeclars() Expected to receive " + tok.converter(Token.PROCEDURE) + "Received " + tok.converter(tok.type) + " instead");
				//Sync = { begin EOF }
				while(tok.type!=Token.BEGIN && tok.type!= Token.EOF){
					tok = l.getToken();
					System.out.println("In subdeclars default token is " + tok.display());
				}//end while
		}//end switch
		
	}//end subdeclars()
	
	public void subdeclarsP(){
		//debugging
		//System.out.println("In subdeclarsP() ");
		
		switch(tok.type){
			//subdeclars’ -> ?
			case(Token.BEGIN): //do nothing
				break;
				
			//subdeclars’ -> subdeclar ; subdeclars’
			case(Token.PROCEDURE): subdeclar(); 
				
				//match ; popStack
				match(Token.SEMI); 
				popStack();
				
				subdeclarsP();
				break;
				
			default: System.out.println("Syntax Error: In subdeclarsP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In subdeclarsP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { begin EOF }
				while(tok.type!=Token.BEGIN && tok.type!= Token.EOF){
					tok = l.getToken();
					System.out.println("In subdeclarsP default token is " + tok.display());
				}//end while
		}//end switch()
		
	}//end subdeclarsP()
	
	public void subdeclar(){
		//debuggin
		//System.out.println("In subdeclar()");
		
		switch(tok.type){
			//subdeclar -> subhead subdeclar’’
			case(Token.PROCEDURE): subhead(); subdeclarPP();
				break;
			
			default: System.out.println("Syntax Error: In subdeclar() Expected to receive " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In subdeclar() Expected to receive " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ; EOF }
				while(tok.type!=Token.SEMI && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In subdeclar default token is " + tok.display());
				}//end while
		}//end switch()
		
	}//end subdeclar()
	
	public void subdeclarPP(){
		//debugging 
		//System.out.println("In subdeclarPP() ");
		
		switch(tok.type){
			//subdeclar’’ -> compstmt
			case(Token.BEGIN): compstmt();
				break;
			
			//subdeclar’’ -> subdeclars compstmt
			case(Token.PROCEDURE): subdeclars(); compstmt();
				break;
			
			//subdeclar’’ -> declar subdeclar’’’
			case(Token.VAR): declar(); subdeclarPPP();
				break;
				
			default: 
				//System.out.println("Syntax Error: In subdeclarPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + ",or " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In subdeclarPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + ",or " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ; EOF }
				while(tok.type!=Token.SEMI && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In subdeclarPP default token is " + tok.display());
				}//end while
				
		}//end switch()
	}//end subdeclarPP()
	
	public void subdeclarPPP(){
		//debugging
		//System.out.println("In subdeclarPPP()");
		
		switch(tok.type){
			//subdeclar’’’ -> compstmt
			case(Token.BEGIN): compstmt();
				break;
			
			//subdeclar’’’ -> subdeclars compstmt
			case(Token.PROCEDURE): subdeclars(); compstmt();
				break;
			
			default: System.out.println("Syntax Error: In subdelcarPPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError( "Syntax Error: In subdeclarPPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead" );
				//Sync = { ; EOF }
				while(tok.type!=Token.SEMI && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In subdeclarPPP default token is " + tok.display());
				}//end while
		}//end switch
	}//end subdeclarPP()
	
	public void subhead(){
		//debugging
		//System.out.println("In subhead() ");
		
		switch(tok.type){
			//subhead -> procedure id subhead’’
			case(Token.PROCEDURE): match(Token.PROCEDURE); 
				
				//match id then add to symbol table
				match(Token.ID); 
				rootNode(tokenHold, Symbol.PROCN);
				
				subheadPP();
				break;
				
			default: System.out.println("Syntax Error: In subhead() Expected to receive " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In subhead() Expected to receive " + tok.converter(Token.PROCEDURE) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { var begin procedure EOF }
				while(tok.type!=Token.VAR && tok.type!=Token.BEGIN && tok.type!=Token.PROCEDURE && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In subhead default token is " + tok.display());
				}//end while
		}//end switch()
	}//end subhead()
	
	public void subheadPP(){
		//debugging
		//System.out.println("In subheadPP() ");
		
		switch(tok.type){
			//subhead’’ -> ;
			case(Token.SEMI): match(Token.SEMI);
				break;
				
			//subhead’’ -> args ;
			case(Token.OPENPRN): args(); match(Token.SEMI);
				break;
				
			default: System.out.println("Syntax Error: In subheadPP() Expected to receive " + tok.converter(Token.SEMI)+ tok.converter(Token.OPENPRN) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError( "Syntax Error: In subheadPP() Expected to receive " + tok.converter(Token.SEMI)+ tok.converter(Token.OPENPRN) + " Received " + tok.converter(tok.type) + " instead" );
				//Sync = { var begin procedure EOF }
				while(tok.type!=Token.VAR && tok.type!=Token.BEGIN && tok.type!=Token.PROCEDURE && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In subheadPP default token is " + tok.display());
				}//end while
		}
	}//end subheadPP()
	
	public void args(){
		//debugging
		//System.out.println("In args()");
		 
		switch(tok.type){
			//args -> ( paramlist )
			case(Token.OPENPRN): match(Token.OPENPRN); paramlist(); match(Token.CLOSEPRN);
				break;
			
			default: System.out.println("Syntax Error: In args() Expected to receive " + tok.converter(Token.OPENPRN) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In args() Expected to receive " + tok.converter(Token.OPENPRN) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ; EOF }
				while(tok.type!=Token.SEMI && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In args default token is " + tok.display());
				}//end while
		}//end switch()
	}//end args()
	
	public void paramlist(){
		//debugging
		//System.out.println("In paramlist() ");
		int symbolType = 0;
		Symbol s = null;
		switch(tok.type){
			//paramlist -> id : type paramlist’
			case(Token.ID): 
				//match id and bird walk
				match(Token.ID); 
				childNode(tokenHold, 0);
					
				//pick up production rules here
				match(Token.COLON); 
				
				//match type
				symbolType = type();
				symbolType = getProcParamType(symbolType);
				symbolType = setProcParamType(symbolType);
				
				s = getSymbolHold();
				s.setNodeType(symbolType);
				symbolTable.addSymbol(s);
												
				//production rules again
				paramlistP();
				break;
			
			default: System.out.println("Syntax Error: In paramlist() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In paramlist() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In paramlist default token is " + tok.display());
				}//end while
				//s = getSymbolHold();
				//s.setNodeType(Symbol.ERR);
				//symbolTable.addSymbol(s);
				
		}//end switch()
	}//end paramlist()
	
	public void paramlistP(){
		//debugging
		//System.out.println("In paramlistP() ");
		int symbolType = 0;
		Symbol s = null;
		
		switch(tok.type){
			//paramlist’ -> ; id : type paramlist’
			case(Token.SEMI): match(Token.SEMI); 
				
				//match id and bird walk
				match(Token.ID); 
				childNode(tokenHold, 0);
					
				//pick up productions here
				match(Token.COLON); 
				
				//match type
				symbolType = type();
				symbolType = getProcParamType(symbolType);
				symbolType = setProcParamType(symbolType);
				s = getSymbolHold();
				s.setNodeType(symbolType);
				symbolTable.addSymbol(s); 
				
				//production rules again
				paramlistP();
				break;
			
			//paramlist’ -> ?
			case(Token.CLOSEPRN): //do nothing
				break;
				
			default: System.out.println("Syntax Error: In paramlistP() Expected to receive " + tok.converter(Token.SEMI)+ tok.converter(Token.CLOSEPRN) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In paramlistP() Expected to receive " + tok.converter(Token.SEMI)+ tok.converter(Token.CLOSEPRN) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In paramlistP default token is " + tok.display());
				}//end while
		}//end switch()
	}//end paramlist()
	
	public void compstmt(){
		//debugging
		//System.out.println("In compstmt()");
		
		switch(tok.type){
			//compstmt -> begin compstmt’’
			case(Token.BEGIN): match(Token.BEGIN); compstmtPP();
				break;
				
			default: System.out.println("Syntax Error: In compstmt() Expected to receive " + tok.converter(Token.BEGIN) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In compstmt() Expected to receive " + tok.converter(Token.BEGIN) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = {  else ; end . EOF }
				while(tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DOT && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In compstmt default token is " + tok.display());
				}//end while
		}//end switch()
	}//end compstmt()
	
	public void compstmtPP(){
		//debugging
		//System.out.println("In compstmtPP()");
		
		switch(tok.type){
			//compstmt’’ -> optstmts end
			case(Token.BEGIN): optstmts(); match(Token.END);
				break;
				
			//compstmt’’ -> optstmts end
			case(Token.CALL): optstmts(); match(Token.END);
				break;
			
			//compstmt’’ -> end
			case(Token.END): match(Token.END);
				break;
			
			//compstmt’’ -> optstmts end
			case(Token.ID): optstmts(); match(Token.END);
				break;
				
			//compstmt’’ -> optstmts end
			case(Token.IF): optstmts(); match(Token.END);
				break;
			
			//compstmt’’ -> optstmts end
			case(Token.WHILE): optstmts(); match(Token.END);
				break;
			
			default: System.out.println("Syntax Error: In compstmtPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.CALL) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.ID) + ", " + tok.converter(Token.IF) + ",or " + tok.converter(Token.WHILE)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In compstmtPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.CALL) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.ID) + ", " + tok.converter(Token.IF) + ",or " + tok.converter(Token.WHILE)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = {  else ; end . EOF }
				while(tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DOT && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In compstmtPP default token is " + tok.display());
				}//end while			
		}//end switch
	}//end compstmt()
	
	public void optstmts(){
		//debugging
		//System.out.println("In optstmts() ");
		
		switch(tok.type){
			//optstmts -> stmtlist
			case(Token.BEGIN): stmtlist();
				break;
			
			//optstmts -> stmtlist
			case(Token.CALL): stmtlist();
				break;
				
			//optstmts -> stmtlist
			case(Token.ID): stmtlist();
				break;
			
			//optstmts -> stmtlist
			case(Token.IF): stmtlist();
				break;
			
			//optstmts -> stmtlist
			case(Token.WHILE): stmtlist();
				break;
				
			default: System.out.println("Syntax Error: In optstmts() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.CALL) + ", " + tok.converter(Token.ID) + ", " + tok.converter(Token.IF)  + ", or " + tok.converter(Token.WHILE)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In optstmts() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.CALL) + ", " + tok.converter(Token.ID) + ", " + tok.converter(Token.IF)  + ", or " + tok.converter(Token.WHILE)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { end EOF }
				while(tok.type!=Token.END && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In optstmts default token is " + tok.display());
				}//end while
		}//end switch()
	}//end optstmts
	
	public void stmtlist(){
		//debugging
		//System.out.println("In stmtlist()");
		
		switch(tok.type){
			//stmtlist -> stmt stmtlist’
			case(Token.BEGIN): stmt(); stmtlistP();
				break;
			
			//stmtlist -> stmt stmtlist’
			case(Token.CALL): stmt(); stmtlistP();
				break;

			//stmtlist -> stmt stmtlist’
			case(Token.ID): stmt(); stmtlistP();
				break;
			//stmtlist -> stmt stmtlist’
			case(Token.IF): stmt(); stmtlistP();
				break;
			
			//stmtlist -> stmt stmtlist’
			case(Token.WHILE): stmt(); stmtlistP();
				break;
			
			default: System.out.println("Syntax Error: In stmtlist() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.CALL) + ", " + tok.converter(Token.ID) + ", " + tok.converter(Token.IF)  + ", or " + tok.converter(Token.WHILE)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In stmtlist() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.CALL) + ", " + tok.converter(Token.ID) + ", " + tok.converter(Token.IF)  + ", or " + tok.converter(Token.WHILE)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { end EOF }
				while(tok.type!=Token.END && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In stmtlist default token is " + tok.display());
				}//end while
		}//end switch()
	}//end stmtlist()
	
	public void stmtlistP(){
		//debugging 
		//System.out.println("In stmtlistP()");
		
		switch(tok.type){
			//stmtlist' -> ?
			case(Token.END): //do nothing
				break;
			
			//stmtlist' -> ; stmt stmtlist’
			case(Token.SEMI): match(Token.SEMI); stmt(); stmtlistP();
				break;
			
			default: System.out.println("Syntax Error: In stmtlistP() Expected to receive " + tok.converter(Token.SEMI)+ ",or " + tok.converter(Token.END) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In stmtlistP() Expected to receive " + tok.converter(Token.SEMI)+ ",or " + tok.converter(Token.END) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { end EOF }
				while(tok.type!=Token.END && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In stmtlistP default token is " + tok.display());
				}//end while
		}//end switch()
	}//end stmtlistP()
	
	public int stmt(){
		//debugging
		//System.out.println("In stmt() ");
		int sType = 0;
		switch(tok.type){
			//stmt -> compstmt
			case(Token.BEGIN): compstmt();
				break;
			
			//stmt -> procstmt
			case(Token.CALL): procstmt();
				break;
			
			//stmt -> variable assignop exp
			case(Token.ID): 
				int vType = variable(); 
				match(Token.ASSIGN);
				int eType = exp();
				if(vType==eType){
					if(eType==Symbol.INT){
						sType=Symbol.INT;
					}else if(eType==Symbol.REAL){
						sType=Symbol.REAL;
					}else if(eType==Symbol.ERR){
						sType=Symbol.ERR;
					}
				}else if(vType != Symbol.ERR && eType != Symbol.ERR){//end if
					sType=Symbol.ERR;
					l.lfm.printError("Semantic Error: Type Mismatch in stmt prob is here");
					//l.lfm.printError("vType: " + vType + "  eType: " + eType);
				}else{
					sType = Symbol.ERR;
				}
				break;
			
			//stmt -> if exp then stmt stmt’’
			case(Token.IF): match(Token.IF); exp(); match(Token.THEN); stmt(); stmtPP();
				break;
			
			//stmt -> while exp do stmt
			case(Token.WHILE): match(Token.WHILE); exp(); match(Token.DO); stmt();
				break;
			
			default: System.out.println("Syntax Error: In stmt() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.CALL) + ", " + tok.converter(Token.ID) + ", " + tok.converter(Token.IF)  + ", or " + tok.converter(Token.WHILE)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In stmt() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.CALL) + ", " + tok.converter(Token.ID) + ", " + tok.converter(Token.IF)  + ", or " + tok.converter(Token.WHILE)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = {  else ; end EOF }
				while(tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In stmt default token is " + tok.display());
				}//end while
				sType = Symbol.ERR;
		}//end switch()
		System.out.println("Returning from stmt with type \t\t" + sType);
		return sType;
	}//end stmt()
	
	public void stmtPP(){
		//debugging
		//System.out.println("In stmtPP()");
		
		switch(tok.type){
			//stmt’’ -> else stmt
			case(Token.ELSE): match(Token.ELSE); stmt();
				break;
			
			//stmt’’ -> ?
			case(Token.END): //do nothing
				break;
			
			//stmt’’ -> ?
			case(Token.SEMI): //do nothing
				break;
			
			default: System.out.println("Syntax Error: In stmtPP() Expected to receive " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ",or " + tok.converter(Token.SEMI) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In stmtPP() Expected to receive " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ",or " + tok.converter(Token.SEMI) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = {  else ; end EOF }
				while(tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In stmtPP default token is " + tok.display());
				}//end while
		}//end switch()
	}//end stmtPP()
	
	public int variable(){
		//debugging
		//System.out.println("In variable() ");
		int vType = 0;
		switch(tok.type){
			//variable -> id variable’’
			case(Token.ID): 
				//System.out.println("About to match Token ID " + tok.getType());
				match(Token.ID);
				System.out.println("Token Should be, name: " + tokenHold.getValue() + " type: " + tokenHold.getType());
				int nodeType = symbolTable.checkType(tokenHold, getCurrentPath());
				int nodeTypeClean = getProcParamType(nodeType);
				System.out.println(">>Converted " + nodeType + " to " + nodeTypeClean);
				
				System.out.println("**Calling variablePP with \t\t" + nodeTypeClean);
				vType = variablePP(nodeTypeClean);
				break;
			
			default: System.out.println("Syntax Error: In variable() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In variable() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { assign EOF }
				while(tok.type!=Token.ASSIGN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In variable default token is " + tok.display());
				}//end while
				vType = Symbol.ERR;
		}//end switch()
		System.out.println("Returning from variable with type \t\t" + vType);
		return vType;
	}//end variable()
	
	public int variablePP(int type){
		System.out.println("**Called variablePP with type \t\t" + type);
		//debugging
		//System.out.println("In variablePP() ");
		int vType = type;
		switch(tok.type){
			//variable’’ -> ?
			case(Token.ASSIGN): //do nothing
				break;
			
			//variable’’ -> [ exp ]
			case(Token.OPENSQ): match(Token.OPENSQ); 
			int etype = exp();
			switch(etype){
				case(Symbol.INT):
					if(type==Symbol.AINT){
						vType = Symbol.INT;
					}else if (type==Symbol.ERR){
						vType = Symbol.ERR;
					}else{
						vType = Symbol.ERR;
						l.lfm.printError("Semantic Error: Attempt to use non INT as index in variablePP case int");
						
					}//else
					break;
					
				case(Symbol.REAL):
					if(type==Symbol.AREAL){
						vType = Symbol.REAL;
					}else if (type==Symbol.ERR){
						vType = Symbol.ERR;
					}else{
						vType = Symbol.ERR;
						l.lfm.printError("Semantic Error: Attempt to use non INT as index in variablePP case real");
					}//else
					break;
					
				case(Symbol.ERR):
					vType = Symbol.ERR;
					l.lfm.printError("Semantic Error: Attempt to use non INT as index in variable case err");
					break;
					
				default:
					vType = Symbol.ERR;
			}
			match(Token.CLOSESQ);
				break;
			
			default: //System.out.println("Syntax Error: In variablePP() Expected to receive " + tok.converter(Token.ASSIGN) + ",or " + tok.converter(Token.OPENSQ)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In variablePP() Expected to receive " + tok.converter(Token.ASSIGN) + ",or " + tok.converter(Token.OPENSQ)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { assign EOF }
				while(tok.type!=Token.ASSIGN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In variablePP default token is " + tok.display());
				}//end while
				vType = Symbol.ERR;
		}//end switch()
		//System.out.println("Returning from variablePP with type \t\t" + vType);
		return vType;
	}//end variablePP()
	
	public void procstmt(){
		//debugging
		//System.out.println("In procstmt() ");
		
		switch(tok.type){
			//procstmt -> call id procstmt’’
			case(Token.CALL): match(Token.CALL); 
				//match id
				match(Token.ID);
				String id = tokenHold.getValue();
				//l.lfm.printError("Checking for procedure " + id);
				informal = new Stack();
				//call procstmtPP
				procstmtPP();
				int check = getProcParams(id);

				break;
			
			default: System.out.println("Syntax Error: In procstmt() Expected to receive " + tok.converter(Token.ASSIGN) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In procstmt() Expected to receive " + tok.converter(Token.ASSIGN) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = {  else ; end EOF }
				while(tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In procstmt default token is " + tok.display());
				}//end while
		}//end switch()
		
	}//end procstmt
	
	public void procstmtPP(){
		//debugging
		//System.out.println("In procstmtPP() ");
		
		switch(tok.type){
			//procstmt’’ -> ?
			case(Token.ELSE): //do nothing
				break;
			
			//procstmt’’ -> ?
			case(Token.END): //do nothing
				break;
			
			//procstmt’’ -> ?
			case(Token.SEMI): //do nothing
				break;
			
			//procstmt’’ -> ( explist )
			case(Token.OPENPRN): match(Token.OPENPRN); explist(); match(Token.CLOSEPRN);
				break;
			
			default: System.out.println("Syntax Error: In procstmtPP() Expected to receive " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.SEMI) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In procstmtPP() Expected to receive " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.SEMI) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
				//Sync = {  else ; end EOF }
				while(tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In procstmtPP default token is " + tok.display());
				}//end while
		}//end switch()
	}//end procstmtPP
	
	public void explist(){
		//debugging
		//System.out.println("In explist() ");
		
		int type = 0;
		Integer t = null;
		
		switch(tok.type){
			//explist -> exp explist’
			case(Token.ID): 
				type = exp();
				type = setProcParamType(type);
				t = new Integer(type);
				informal.push(t);
				explistP();
				break;
			
			//explist -> exp explist’
			case(Token.NOT): 
				type = exp();
				type = setProcParamType(type);
				t = new Integer(type);
				informal.push(t);
				explistP();
				break;
			
			//explist -> exp explist’
			case(Token.NUM): 
				type = exp();
				type = setProcParamType(type);
				t = new Integer(type);
				informal.push(t);
				explistP();
				break;
			
			//explist -> exp explist’
			case(Token.PLUS): 
				type = exp();
				type = setProcParamType(type);
				t = new Integer(type);
				informal.push(t);
				explistP();
				break;
			
			//explist -> exp explist’
			case(Token.MINUS): 
				type = exp();
				type = setProcParamType(type);
				t = new Integer(type);
				informal.push(t);
				explistP();
				break;
			
			//explist -> exp explist’
			case(Token.OPENPRN): 
				type = exp();
				type = setProcParamType(type);
				t = new Integer(type);
				informal.push(t);
				explistP();
				break;
			
			default: System.out.println("Syntax Error: In explist() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In explist() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In explist default token is " + tok.display());
				}//end while
		}//end switch()
	}//end explist()
	
	public void explistP(){
		//debugging
		//System.out.println("In explistP() ");
		
		int type = 0;
		Integer t = null;
		
		switch(tok.type){
			//explist’ -> , exp explist’
			case(Token.COMMA): match(Token.COMMA); 
				type = exp();
				type = setProcParamType(type);
				t = new Integer(type);
				informal.push(t);
				explistP();
				break;
			
			//explist’ -> ?
			case(Token.CLOSEPRN): //do nothing
				break;
			
			default: System.out.println("Syntax Error: In explistP() Expected to receive " + tok.converter(Token.COMMA) + ",or " + tok.converter(Token.CLOSEPRN)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In explistP() Expected to receive " + tok.converter(Token.COMMA) + ",or " + tok.converter(Token.CLOSEPRN)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In explistP default token is " + tok.display());
				}//end while			
		}//end switch()
	}//end explistP()
	
	public int exp(){
		//debugging
		//System.out.println("In exp() ");
		int eType = 0; 
		int ei = 0;
		switch(tok.type){
			//exp -> simpexp exp’’
			case(Token.ID): 
				ei = simpexp(); 
				eType = expPP(ei);
				break;
			
			//exp -> simpexp exp’’
			case(Token.NOT):
				ei = simpexp(); 
				eType = expPP(ei);
				break;
			
			//exp -> simpexp exp’’
			case(Token.NUM): 
				ei = simpexp(); 
				eType = expPP(ei);
				break;
			
			//exp -> simpexp exp’’
			case(Token.PLUS): 
				ei = simpexp(); 
				eType = expPP(ei);
				break;
			
			//exp -> simpexp exp’’
			case(Token.MINUS): 
				ei = simpexp(); 
				eType = expPP(ei);
				break;
			
			//exp -> simpexp exp’’
			case(Token.OPENPRN):
				ei = simpexp(); 
				eType = expPP(ei);
				break;
			
			default: System.out.println("Syntax Error: In exp() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In exp() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) ] , else ; end do then EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In exp default token is " + tok.display());
				}//end while
				eType = Symbol.ERR;
				
		}//end switch
		//System.out.println("Returning from exp with type \t\t" + eType);
		return eType;
	}//end exp()
	
	public int expPP(int type){
		//debugging
		//System.out.println("In expPP()");
		int eType = type;
		switch(tok.type){
			//exp’’ -> ?
			case(Token.DO): //do nothing
				break;
			
			//exp’’ -> ?
			case(Token.ELSE): //do nothing
				break;
			
			//exp’’ -> ?
			case(Token.END): //do nothing
				break;	
			
			//exp’’ -> relop simpexp
			case(Token.RELOP): 
				match(Token.RELOP); 
				Token token = tokenHold;
				int relop = token.getAttribute();
				int si = simpexp();
				if(type==si && type==Symbol.INT){
					eType=Symbol.BOOL;
				}else if(type==si && type==Symbol.REAL){
					eType=Symbol.BOOL;
				}else if(type==si && type==Symbol.BOOL){
					eType=Symbol.BOOL;
				}else if(type==Symbol.ERR || si==Symbol.ERR){
					eType = Symbol.ERR;
				}else{
					eType = Symbol.ERR;
					l.lfm.printError("Semantic Error: Type Mismatch expPP");
				}
				break;
			
			//exp’’ -> ?
			case(Token.THEN): //do nothing
				break;
			
			//exp’’ -> ?
			case(Token.COMMA): //do nothing
				break;
			
			//exp’’ -> ?
			case(Token.SEMI): //do nothing
				break;
			
			//exp’’ -> ?
			case(Token.CLOSEPRN): //do nothing
				break;
			
			//exp’’ -> ?
			case(Token.CLOSESQ): // do nothing
				break;
			
			default: System.out.println("Syntax Error: In expPP() Expected to receive " + tok.converter(Token.DO) + ", " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.RELOP) + ", " + tok.converter(Token.THEN) + ", " + tok.converter(Token.COMMA) + ", " + tok.converter(Token.SEMI) + ", " + tok.converter(Token.CLOSEPRN) + ", or " + tok.converter(Token.CLOSESQ)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In expPP() Expected to receive " + tok.converter(Token.DO) + ", " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.RELOP) + ", " + tok.converter(Token.THEN) + ", " + tok.converter(Token.COMMA) + ", " + tok.converter(Token.SEMI) + ", " + tok.converter(Token.CLOSEPRN) + ", or " + tok.converter(Token.CLOSESQ)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) ] , else ; end do then EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In expPP default token is " + tok.display());
				}//end while			
				eType = Symbol.ERR;
		}//end switch()
		return eType;
	}//end expPP
	
	public int simpexp(){
		//debugging
		//System.out.println("In simpexp() ");
		int sType = 0;
		int t = 0;
		switch(tok.type){
			//simpexp -> term simpexp’
			case(Token.ID): 
				t = term(); 
				//TODO may need to put the checks here
				sType = simpexpP(t);
				break;
			
			//simpexp -> term simpexp’
			case(Token.NOT): 
				t = term(); 
				sType = simpexpP(t);
				break;
			
			//simpexp -> term simpexp’
			case(Token.NUM): 
				t = term(); 
				sType = simpexpP(t);
				break;
			
			//simpexp -> sign term simpexp’
			case(Token.PLUS): sign(); 
				t = term(); 
				sType = simpexpP(t);
				break;
			
			//simpexp -> sign term simpexp’
			case(Token.MINUS): sign(); 
				t = term(); 
				sType = simpexpP(t);
				break;
			
			//simpexp -> term simpexp’
			case(Token.OPENPRN): 
				t = term(); 
				sType = simpexpP(t);
				break;
			
			default: System.out.println("Syntax Error: In simpexp() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In simpexp() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) ] , else ; end do then relop EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.RELOP && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In simpexp default token is " + tok.display());
				}//end while			
				sType = Symbol.ERR;
		}//end switch()
		//System.out.println("Returning from simpexp with type \t\t" + sType);
		return sType;
	}//end simpexp()
	
	public int simpexpP(int type){
		//debugging 
		//System.out.println("In simpexpP() ");
		int sType = 0;
		sType = type;
		int tt = 0;
		int sendVal = 0;
		switch(tok.type){
			//simpexp’ -> addop term simpexp’
			case(Token.ADDOP): 
				match(Token.ADDOP); 
				Token token = tokenHold;
				int addop = tokenHold.getAttribute();
				tt = term(); 
				int si = type;
				switch(addop){
					case(Token.PLUS):
						if(tt==Symbol.INT){
							if(si==Symbol.INT){
								sendVal = Symbol.INT;
							}else if(si==Symbol.ERR){//end if si
								sendVal = Symbol.ERR;
							}else{
								sendVal = Symbol.ERR;
								l.lfm.printError("Semantic Error: Type Mismatch simpexpP");
							}//end else
						}else if(tt==Symbol.REAL){//end if tt
							if(si==Symbol.REAL){
								sendVal = Symbol.REAL;
							}else if(si==Symbol.ERR){//end if si
								sendVal = Symbol.ERR;
							}else{//end else if
								sendVal = Symbol.ERR;
								l.lfm.printError("Semantic Error: Type Mismatch in simpexpP");
							}//end else
						}else if(tt==Symbol.ERR){
							sendVal = Symbol.ERR;
						}else{//end else if
							sendVal = Symbol.ERR;
							l.lfm.printError("Semantic Error: Type Mismatch in simpexpP");
						}//end else
						break;
					case(Token.MINUS):
						if(tt==Symbol.INT){
							if(si==Symbol.INT){
								sendVal = Symbol.INT;
							}else if(si==Symbol.ERR){//end if si
								sendVal = Symbol.ERR;
							}else{
								sendVal = Symbol.ERR;
								l.lfm.printError("Semantic Error: Type Mismatch in simpexpP");
							}//end else
						}else if(tt==Symbol.REAL){//end if tt
							if(si==Symbol.REAL){
								sendVal = Symbol.REAL;
							}else if(si==Symbol.ERR){//end if si
								sendVal = Symbol.ERR;
							}else{//end else if
								sendVal = Symbol.ERR;
								l.lfm.printError("Semantic Error: Type Mismatch in simpexpP");
							}//end else
						}else if(tt==Symbol.ERR){
							sendVal = Symbol.ERR;
						}else{//end else if
							sendVal = Symbol.ERR;
							l.lfm.printError("Semantic Error: Type Mismatch in simpexpP");
						}//end else
						break;
					case(Token.OR):
						if(tt==Symbol.BOOL){
							if(si==Symbol.BOOL){
								sendVal = Symbol.BOOL;
							}else if(si==Symbol.ERR){//end if si
								sendVal = Symbol.ERR;
							}else{
								sendVal = Symbol.ERR;
								l.lfm.printError("Semantic Error: Type Mismatch in simpexpP");
							}//end else
						}else if(tt==Symbol.ERR){//end if tt
								sendVal = Symbol.ERR;
						}else{//end else if
							sendVal = Symbol.ERR;
							l.lfm.printError("Semantic Error: Type Mismatch in simpexpP");
						}//end else
						break;
					default:
						//this should never print because addop cannot be anything else
						sendVal = Symbol.ERR;
						l.lfm.printError("Semantic Error: Type Mismatch, should never print");
				}//end switch
				sType = simpexpP(sendVal);
				break;
			
			//simpexp’ -> ?
			case(Token.DO): //do nothing
				break;
			
			//simpexp’ -> ?
			case(Token.ELSE): //do nothing
				break;
			
			//simpexp’ -> ?
			case(Token.END): //do nothing
				break;
			
			//simpexp’ -> ?
			case(Token.RELOP): //do nothing
				break;
			
			//simpexp’ -> ?
			case(Token.THEN): //do nothing
				break;
			
			//simpexp’ -> ?
			case(Token.COMMA): //do nothing
				break;
			
			//simpexp’ -> ?
			case(Token.SEMI): //do nothing
				break;
			
			//simpexp’ -> ?
			case(Token.CLOSEPRN): //do nothing
				break;
			
			//simpexp’ -> ?
			case(Token.CLOSESQ): //do nothing
				break;
			
			default: System.out.println("Syntax Error: In simpexpP() Expected to receive " + tok.converter(Token.ADDOP) + ", " + tok.converter(Token.DO) + ", " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.RELOP) + ", " + tok.converter(Token.THEN) + ", " + tok.converter(Token.COMMA) + ", " + tok.converter(Token.SEMI) + ", " + tok.converter(Token.CLOSEPRN) + ", or " + tok.converter(Token.CLOSESQ)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In simpexpP() Expected to receive " + tok.converter(Token.ADDOP) + ", " + tok.converter(Token.DO) + ", " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.RELOP) + ", " + tok.converter(Token.THEN) + ", " + tok.converter(Token.COMMA) + ", " + tok.converter(Token.SEMI) + ", " + tok.converter(Token.CLOSEPRN) + ", or " + tok.converter(Token.CLOSESQ)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) ] , else ; end do then relop EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.RELOP && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In simpexpP default token is " + tok.display());
				}//end while			
				sType = Symbol.ERR;
		}//end switch()
		return sType;
	}//end simpexpP()
	
	public int term(){
		//debugging
		//System.out.println("In term() ");
		int tType = 0;
		switch(tok.type){
			//term -> factor term’
			case(Token.ID): 
				int type = factor();
				//System.out.println("Switching on " + type + " in term case ID");
				System.out.println("-------------factor returned type \t" + type);
				switch(type){
					
					case(Symbol.INT): tType = Symbol.INT;
						break;
					//TODO Am I sure that this case is true?
					//pretty sure because factor can be...
					case(Symbol.AINT):tType = Symbol.AINT;
						break;
					case(Symbol.REAL): tType = Symbol.REAL;
						break;
					//TODO Am I sure that this case is true?
					//pretty sure because factor can be...
					case(Symbol.AREAL):tType = Symbol.AREAL;
						break;
					case(Symbol.BOOL): tType = Symbol.BOOL;
						break;
					case(Symbol.ERR): tType = Symbol.ERR;
						break;
					default:
						tType = Symbol.ERR;
						l.lfm.printError("Semantic Error: Type Mismatch in term case id default.");
				}// end switch
				
				//back to production errors
				tType = termP(tType);
				break;
			//term -> factor term’
			case(Token.NOT): 
			int typ = factor();
			switch(typ){
				case(Symbol.INT): tType = Symbol.INT;
				break;
				case(Symbol.REAL): tType = Symbol.REAL;
				break;
				case(Symbol.BOOL): tType = Symbol.BOOL;
				break;
				case(Symbol.ERR): tType = Symbol.ERR;
				break;
				default:
					tType = Symbol.ERR;
					l.lfm.printError("Semantic Error: Type Mismatch in term case not.");
			}// end switch
			
			//back to production errors
			tType = termP(tType);
			break;
			
			//term -> factor term’
			case(Token.NUM):
				int ty = factor();
				switch(ty){
					case(Symbol.INT): tType = Symbol.INT;
					break;
					case(Symbol.REAL): tType = Symbol.REAL;
					break;
					case(Symbol.BOOL): tType = Symbol.BOOL;
					break;
					case(Symbol.ERR): tType = Symbol.ERR;
					break;
					default:
						tType = Symbol.ERR;
						l.lfm.printError("Semantic Error: Type Mismatch in term num.");
				}// end switch
				
				//back to production errors
				tType = termP(tType);
				break;
			
			//term -> factor term’
			case(Token.OPENPRN): 				
				int t = factor();
				switch(t){
					case(Symbol.INT): tType = Symbol.INT;
					break;
					case(Symbol.REAL): tType = Symbol.REAL;
					break;
					case(Symbol.BOOL): tType = Symbol.BOOL;
					break;
					case(Symbol.ERR): tType = Symbol.ERR;
					break;
					default:
						tType = Symbol.ERR;
						l.lfm.printError("Semantic Error: Type Mismatch in term in case openprn.");
				}// end switch
		
				//back to production errors
				tType = termP(tType);
				break;
			
			default: System.out.println("Syntax Error: In term() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In term() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
			//Sync = { addop ) ] , else ; end do then relop EOF }
			while(tok.type!=Token.ADDOP && tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.RELOP && tok.type!=Token.EOF){
				tok = l.getToken();
				System.out.println("In term default token is " + tok.display());
			}//end while			
			tType = Symbol.ERR;
		}//end switch
		//System.out.println("\nReturning from Term with type " + tType +"\n");
		return tType;
	}//end term()
	
	public int termP(int type){
		//debugging
		//System.out.println("In termP() ");
		int tType = type;		
		switch(tok.type){
			//term’ -> ?
			case(Token.ADDOP): //do nothing
				break;
			
			//term’ -> ?
			case(Token.DO): //do nothing
				break;
			
			//term’ -> ?
			case(Token.ELSE): //do nothing
				break;
			
			//term’ -> ?
			case(Token.END): //do nothing
				break;
			
			//term’ -> mulop factor term’
			case(Token.MULOP): 
				int val = 0;
				match(Token.MULOP);
				Token token = tokenHold;
				int mop = token.getAttribute();
				int ft = factor();
				switch(mop){
					case(Token.ASTERISK):
						if(ft==Symbol.INT){
							val = Symbol.ERR;
							l.lfm.printError("Semantic Error: Incompatible Types in termP ");
						}else if(ft==Symbol.REAL){//end if
							if(type==Symbol.REAL){
								val = Symbol.REAL; 
							}else if(type==Symbol.ERR){
								//do nothing, error exists
								val = Symbol.ERR;
							}else{
								val = Symbol.ERR;
								l.lfm.printError("Semantic Error: Imcompatibile Types");
							}//end else
						}else if(ft==Symbol.ERR){//end else
							//do nothing
							val = Symbol.ERR;
						}else{//end else if
							val = Symbol.ERR;
							l.lfm.printError("Semantic Error: Imcompatible Types");
						}//end else
						break;
						
					case(Token.SLASH):
						if(ft==Symbol.INT){
							val = Symbol.ERR;
							l.lfm.printError("Semantic Error: Incompatible Types");
						}else if(ft==Symbol.REAL){//end if
							if(type==Symbol.REAL){
								val = Symbol.REAL; 
							}else if(type==Symbol.ERR){
								//do nothing, error exists
								val = Symbol.ERR;
							}else{
								val = Symbol.ERR;
								l.lfm.printError("Semantic Error: Imcompatibile Types");
							}//end else
						}else if(ft==Symbol.ERR){//end else
							//do nothing
							val = Symbol.ERR;
						}else{//end else if
							val = Symbol.ERR;
							l.lfm.printError("Semantic Error: Imcompatible Types");
						}//end else
						break;
					case(Token.MOD):
						if(ft==Symbol.REAL){
							val = Symbol.ERR;
							l.lfm.printError("Semantic Error: Incompatible Types");
						}else if(ft==Symbol.INT){//end if
							if(type==Symbol.INT){
								val = Symbol.INT; 
							}else if(type==Symbol.ERR){
								//do nothing, error exists
								val = Symbol.ERR;
							}else{
								val = Symbol.ERR;
								l.lfm.printError("Semantic Error: Imcompatibile Types");
							}//end else
						}else if(ft==Symbol.ERR){//end else
							//do nothing
							val = Symbol.ERR;
						}else{//end else if
							val = Symbol.ERR;
							l.lfm.printError("Semantic Error: Imcompatible Types");
						}//end else
						break;
					case(Token.DIV):
						if(ft==Symbol.REAL){
							val = Symbol.ERR;
							l.lfm.printError("Semantic Error: Incompatible Types");
						}else if(ft==Symbol.INT){//end if
							if(type==Symbol.INT){
								val = Symbol.INT; 
							}else if(type==Symbol.ERR){
								//do nothing, error exists
								val = Symbol.ERR;
							}else{
								val = Symbol.ERR;
								l.lfm.printError("Semantic Error: Imcompatibile Types");
							}//end else
						}else if(ft==Symbol.ERR){//end else
							//do nothing
							val = Symbol.ERR;
						}else{//end else if
							val = Symbol.ERR;
							l.lfm.printError("Semantic Error: Imcompatible Types");
						}//end else
						break;
					case(Token.AND):
						if(ft==Symbol.BOOL){
							if(type==Symbol.BOOL){
								val = Symbol.BOOL;
							}else if(type==Symbol.ERR){//end if
								val = Symbol.ERR;
							}else{//end else if
								l.lfm.printError("Semantic Error: Imcompatible Types");
							}
						}else if(ft==Symbol.ERR){
							val = Symbol.ERR;
						}
						else {//end
							l.lfm.printError("Semantic Error: Incompatible Types");
							val = Symbol.ERR;
						}//end else
						break;
					default:
						//cannot print mulop cannot be anything else
						val = Symbol.ERR;
						l.lfm.printError("Semantic Error: Imcompatible Types, should never print.");
				}//end switch
				
				tType = termP(val);
				break;
			
			//term’ -> ?
			case(Token.RELOP): //do nothing
				break;
			
			//term’ -> ?
			case(Token.THEN): //do nothing
				break;
				
			//term’ -> ?
			case(Token.COMMA): //do nothing
				break;
				
			//term’ -> ?
			case(Token.SEMI): //do nothing
				break;
				
			//term’ -> ?
			case(Token.CLOSEPRN): //do nothing
				break;
			
			//term’ -> ?
			case(Token.CLOSESQ): //do nothing	
				break;
				
			default: System.out.println("Syntax Error: In termP() Expected to receive " + tok.converter(Token.ADDOP) + ", " + tok.converter(Token.DO) + ", " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.MULOP)+ ", " + tok.converter(Token.RELOP) + ", " + tok.converter(Token.THEN) + ", " + tok.converter(Token.COMMA) + ", " + tok.converter(Token.SEMI) + ", " + tok.converter(Token.CLOSEPRN) + ", or " + tok.converter(Token.CLOSESQ)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In termP() Expected to receive " + tok.converter(Token.ADDOP) + ", " + tok.converter(Token.DO) + ", " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.MULOP)+ ", " + tok.converter(Token.RELOP) + ", " + tok.converter(Token.THEN) + ", " + tok.converter(Token.COMMA) + ", " + tok.converter(Token.SEMI) + ", " + tok.converter(Token.CLOSEPRN) + ", or " + tok.converter(Token.CLOSESQ)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { addop ) ] , else ; end do then relop EOF }
				while(tok.type!=Token.ADDOP && tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.RELOP && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In termP default token is " + tok.display());
				}//end while
				tType = Symbol.ERR;
		}//end switch()
		//System.out.println("Returning from termP with type \t\t" + tType);
		return tType;
		
	}//end termP()
	
	public int factor(){
		//debugging
		//System.out.println("In factor() ");
		int fType = 0;
		switch(tok.type){
			//factor -> id factor’’
			case(Token.ID): 
				
				match(Token.ID);
				Token token = tokenHold;
				//System.out.println("Calling checkType with: " + token.getValue());
				int varType = symbolTable.checkType(token, getCurrentPath());
				System.out.println("~           ************Checking varable." + token.getValue());
				System.out.println("************Returning from checkType with " + varType);
				varType = getProcParamType(varType);
				System.out.println("************Converted varType to \t" + varType );
				fType = factorPP(varType);
				break;
				
			//factor -> not factor
			case(Token.NOT): match(Token.NOT); 
				int type = factor();
				//TODO how to assign boolean types in the first place
				if(type == Symbol.BOOL){
					fType = Symbol.BOOL;
				}else if(type==Symbol.ERR){//end if
					fType = Symbol.ERR;
					//do nothing
				}else{//end else
					fType = Symbol.ERR;
					l.lfm.printError("Semantic Error: Type Mismatch in factor");
				}//end else
				break;
			
			//factor -> num
			case(Token.NUM): match(Token.NUM);
				int t = tokenHold.getAttribute();
				//System.out.println("token attr in factor case num is... " + t + " " + tokenHold.converter(t));
				if(t == Token.INT){
					fType = Symbol.INT;
				}else if(t == Token.REAL){
					fType = Symbol.REAL;
				}else if(t == Token.ERROR){//end else if
					fType = Symbol.ERR;
				}else{//end else if
					fType = Symbol.ERR;
					l.lfm.printError("Semantic Error: Type mismatch in factor");
				}//end else
				break;
			
			//factor -> ( exp )
			case(Token.OPENPRN): match(Token.OPENPRN); 

				//call exp() and other
				fType = exp();
				if(fType != Symbol.INT && fType != Symbol.REAL && fType != Symbol.ERR && fType!=Symbol.BOOL){
					l.lfm.printError("Semantic Error: Type mismatch in factor case open prn");
					l.lfm.printError("fType: " + fType);
				}//end if

				//pick up productions here
				match(Token.CLOSEPRN);
				break;
			
			default: System.out.println("Syntax Error: In factor() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In factor() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { mulop addop ) ] , else ; end do then relop EOF }
				while(tok.type!=Token.MULOP && tok.type!=Token.ADDOP && tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.RELOP && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In factor default token is " + tok.display());
				}//end while				
				fType = Symbol.ERR;
		}//end switch()
		//System.out.println("Returning from factor with type \t\t" + fType);
		return fType;
	}//end factor()
	
	public int factorPP(int type){
		//debugging	
		System.out.println("**Called factorPP with type \t\t " + type);
		int fType = type;
		
		//drop the array type by default just in case epsilon
/*		if(type == Symbol.AINT){
			fType = Symbol.INT;
		}else if(type == Symbol.AREAL){
			fType = Symbol.REAL;
		}
	*/	
		
		switch(tok.type){
			//factor’’ -> ?
			case(Token.ADDOP): //do nothing
				break;
				
			//factor’’ -> ?
			case(Token.DO): // do nothing
				break;
				
			//factor’’ -> ?
			case(Token.ELSE): //do nothing	
				break;
			//factor’’ -> ?
			case(Token.END): //do nothing
				break;
			
			//factor’’ -> ?
			case(Token.MULOP): //do nothing
				break;
			
			//factor’’ -> ?
			case(Token.RELOP): //do nothing
				break;
				
			//factor’’ -> ?
			case(Token.THEN): //do nothing
				break;
			
			//factor’’ -> ?
			case(Token.COMMA): //do nothing
				break;
			
			//factor’’ -> ?
			case(Token.SEMI): //do nothing
				break;
			
			//factor’’ -> ?
			case(Token.CLOSEPRN): //do nothing
				break;
			
			//factor’’ -> [ exp ]
			case(Token.OPENSQ): match(Token.OPENSQ); 
				
				//call exp() and check types
				int eType = exp(); 
				switch(eType){
					case(Symbol.INT):
						if(type==Symbol.AINT){
							fType = Symbol.INT;
						}else{//end if
							fType = Symbol.ERR;
						}
						break;
						
					case(Symbol.REAL):
						fType = Symbol.ERR;
						if(type==Symbol.AREAL){
							l.lfm.printError("Semantic Error: Attempted to use non INT index in factorPP case opensquare case real");
						}//end if
						break;
						
					case(Symbol.ERR):
						fType = Symbol.ERR;
						break;
						
					default:
						fType = Symbol.ERR;
						l.lfm.printError("Semantic Error: Attempted to use non INT index case opensquare default");
						
					
				}//end switch
				
				//production rules again
				match(Token.CLOSESQ);
				break;
				
			//factor’’ -> ?
			case(Token.CLOSESQ): //do nothing
				break;
			
			default: System.out.println("Syntax Error: In factorPP() Expected to receive " + tok.converter(Token.ADDOP) + ", " + tok.converter(Token.DO) + ", " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.MULOP)+ ", " + tok.converter(Token.RELOP) + ", " + tok.converter(Token.THEN) + ", " + tok.converter(Token.COMMA) + ", " + tok.converter(Token.SEMI) + ", " + tok.converter(Token.CLOSEPRN) + ", " + tok.converter(Token.OPENSQ) + ", or " + tok.converter(Token.CLOSESQ)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In factorPP() Expected to receive " + tok.converter(Token.ADDOP) + ", " + tok.converter(Token.DO) + ", " + tok.converter(Token.ELSE) + ", " + tok.converter(Token.END) + ", " + tok.converter(Token.MULOP)+ ", " + tok.converter(Token.RELOP) + ", " + tok.converter(Token.THEN) + ", " + tok.converter(Token.COMMA) + ", " + tok.converter(Token.SEMI) + ", " + tok.converter(Token.CLOSEPRN) + ", " + tok.converter(Token.OPENSQ) + ", or " + tok.converter(Token.CLOSESQ)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { mulop addop ) ] , else ; end do then relop EOF }
				while((tok.type!=Token.MULOP) && (tok.type!=Token.ADDOP) && (tok.type!=Token.CLOSEPRN) && (tok.type!=Token.CLOSESQ) && (tok.type!=Token.COMMA) && (tok.type!=Token.ELSE) && (tok.type!=Token.SEMI) && (tok.type!=Token.END) && (tok.type!=Token.DO) && (tok.type!=Token.THEN) && (tok.type!=Token.RELOP) && (tok.type!=Token.EOF)){
					tok = l.getToken();
					System.out.println("In factorPP default token is " + tok.display());
				}//end while	
				fType = Symbol.ERR;
		}//end switch()
		//System.out.println("_____ in factorPP ______Took in type: " + type);
		//System.out.println("Returning from factorPP with type \t\t" + fType);
		return fType;
	}//end factorPP()
	
	public void sign(){
		//debugging
		//System.out.println("In sign()");
		
		switch(tok.type){
			//sign -> +
			case(Token.PLUS): match(Token.PLUS);
				break;
			
			//sign -> -
			case(Token.MINUS): match(Token.MINUS);
				break;
			
			default: System.out.println("Syntax Error: In sign() Expected to receive " + tok.converter(Token.PLUS) + ",or " + tok.converter(Token.MINUS)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In sign() Expected to receive " + tok.converter(Token.PLUS) + ",or " + tok.converter(Token.MINUS)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { num ( not id EOF }
				while(tok.type!=Token.NUM && tok.type!=Token.OPENPRN && tok.type!=Token.NOT && tok.type!=Token.ID && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In sign default token is " + tok.display());
				}//end while
		}//end switch()
	}//end sign()
	
	public String getCurrentPath(){
		String rval = "";
		
		if(!stack.empty()){
			rval = (String) stack.peek();
		}//end if
		
		return rval;
	}//end getPath
	
	public void setPath(){
		
	}//end setPath()
	
	public void rootNode(Token t, int symbolType){
		
		if(symbolTable.checkExistence(t) < 1){
			//System.out.println(symbolTable.checkExistence(t) + " " + t.getType() + " exist in the symbol table.");
			String path = getCurrentPath();//current path string
			String p = t.getValue();//string to add to the end of the path
			
			path = path.concat(p);
			path = path.concat(".");	
			stack.push(path);
			Integer i = new Integer(0);
			offset.push(i);
			Symbol s = new Symbol(t, path, symbolType);
			symbolTable.addSymbol(s);

		}else{
			System.out.println("Semantic Error: Duplicate Declaration");
			l.lfm.printError("Semantic Error: Duplicate Declaration");
		}//end else
		
		//System.out.println("****************** Path is: " + getCurrentPath());
		
	}//end newScope
	

	private void popStack(){
		
		//System.out.println("\nBefore pop..... " + stack.toString());
		if(!stack.isEmpty()){
			stack.pop();
			offset.pop();
		}else
			l.lfm.printError("Semantic Error: Mismatch procedure declaration");
		
		
		//System.out.println("After pop..... " + stack.toString() + "\n");
	}//end popStack
	
	public Symbol childNode(Token t, int symbolType){
		Symbol s = new Symbol(t, getCurrentPath());
		if(symbolTable.varExists(t, getCurrentPath())){
			l.lfm.printError("Semantic Error: Duplicate local variable.");
			s.setNodeType(0);
		}else{//end if
			s.setNodeType(Symbol.INT);
			//symbolTable.addSymbol(s);
			setSymbolHold(s);
			
		}//end else
		return s;
	}//end childNode
	
	public void checkUse(Token t){
		if(symbolTable.checkType(t, getCurrentPath()) < Symbol.ERR){
			l.lfm.printError("Semantic Error: Undeclared Variable");
		}//end if
	}//end checkUse
	
	private void setSymbolHold(Symbol s){
		this.symbolHold = s;
	}//end setSymbolHold
	
	public Symbol getSymbolHold(){
		return symbolHold;
	}
	
	public int getArrayType(int i){
		int rval = 0;
		
		switch(i){
			case(Symbol.INT): rval = Symbol.AINT;
				break;
			case(Symbol.REAL): rval = Symbol.AREAL;
				break;
		}//end switch
		return rval;
	}//end getArrayType
	
	private int getProcParamType(int symbolType){
		int rval = 0;
		
		switch(symbolType){
			case(Symbol.PPAINT): rval = Symbol.AINT;
				break;
			case(Symbol.PPAREAL): rval = Symbol.AREAL;
				break;
			case(Symbol.PPINT): rval = Symbol.INT;
				break;
			case(Symbol.PPREAL): rval = Symbol.REAL;
				break;
			default:rval = symbolType;
		}//end switch
		return rval;
	}//end getProcParamType

	private int setProcParamType(int symbolType){
		int rval = 0;
		
		switch(symbolType){
			case(Symbol.AINT): rval = Symbol.PPAINT;
				break;
			case(Symbol.AREAL): rval = Symbol.PPAREAL;
				break;
			case(Symbol.INT): rval = Symbol.PPINT;
				break;
			case(Symbol.REAL): rval = Symbol.PPREAL;
				break;
			default:rval = symbolType;
		}//end switch
		return rval;
	}//end getProcParamType

	public boolean matchType(){
		boolean rval = false;
		
		return rval;
	}//end matchType
	
	//switches symbol types to correct number types
	//only switches symbol types of the procedure parameters
	//the rest of the types match
	public int symbolToType(int i){
		int rval = 0;
		switch(i){
			case(Symbol.PPAINT): rval = Symbol.AINT;
				break;
			case(Symbol.PPAREAL): rval = Symbol.AREAL;
				break;
			case(Symbol.PPINT): rval = Symbol.INT;
				break;
			case(Symbol.PPREAL): rval = Symbol.REAL;
				break;
			default:  //all other types match
				rval = i;
		
		}//end switch
		return rval;
	}//end symbolToType
		
	public int getProcParams(String id){
		int rval = 0;
		Stack params = new Stack();
		params = symbolTable.returnProcParams(id);
		System.out.println(informal.toString());
		Integer formalI = null;
		int f = 0;
		Integer informalI = null;
		int i = 0;
		
		if(params.size()== informal.size()){
			while(!params.empty()){//while there is still something in the stack
				formalI = (Integer) params.pop();
				informalI = (Integer) informal.pop();
				f = formalI.intValue();
				i = informalI.intValue();
				
				if(i==f){
					//then we are doing good
				}else{
					rval = Symbol.ERR;
					l.lfm.printError("Semantic Error: Incorrect Procedure call. Type mismatch.");
					return rval;
				}
			}
		}else{//end if number of params is not equal to number of declared params
			rval = Symbol.ERR;
			l.lfm.printError("Semantic Error: Incorrect Procedure call. Incorrect number of parameters.");
			//l.lfm.printError("Stacks are not the same size");
			//l.lfm.printError("Formal Parameters: " + params.toString());
			//l.lfm.printError("Informal Parameters: " + informal.toString());
		}
		
		return rval;
	}//end getProcParams
	
	public void updateOffset(String name, int i){
		Integer o = (Integer) offset.pop();
		int num = o.intValue();
		
		pw.println(name + "\t" + num);
		pw.flush();
		
		num = i + num;
		o = new Integer(num);
		offset.push(o);
		
	}
	
}//end parse
