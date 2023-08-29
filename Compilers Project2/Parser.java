/*
 * Created on Oct 27, 2004
 */

/**
 * @author shellyjo - Shelly Seier
 */
public class Parser {
	Lex l = new Lex();//the lexical analyzer
	Token tok = null;
	boolean sync = false;
	
	public static void main(String[] args) {
		Parser p = new Parser();
		p.parse();
	}//end main methood
	
	public void parse(){
		tok = l.getToken();
		prog();
		match(Token.EOF);
		l.finish();
		//match(Token.EOF);
	}//end parse()
	
	public void match(int t){
		if(tok.type == t){
			if(t != Token.EOF){
				tok = l.getToken();
				System.out.println("Token received in match() is " + tok.display());
			}else{
				System.out.println("Success! Parsing complete");
				l.lfm.printError( "Success! Parsing complete" );
			}
		}else{
			System.out.println("Syntax error: In Match, Expecting " + tok.converter(t) + " received " + tok.converter(tok.type));
			l.lfm.printError("Syntax error: In Match, Expecting " + tok.converter(t) + " received " + tok.converter(tok.type));
		}
	}//end match(Token t)
	
	public void prog(){
		//debugging
		System.out.println("In prog()");
		
		switch (tok.type) {
			//prog -> prog id ( idlist ) ; prog’’
			case(Token.PROG): match(Token.PROG); match(Token.ID); match(Token.OPENPRN); idlist(); match(Token.CLOSEPRN); match(Token.SEMI); progPP();
				break;
				
			default: System.out.println("Syntax error: In prog() Expecting " + tok.converter(Token.PROG) + " received " + tok.converter(tok.type));
				l.lfm.printError("Syntax error: In prog() Expecting " + tok.converter(Token.PROG) + " received " + tok.converter(tok.type));
				//Sync = { EOF }
				while(tok.type != Token.EOF){
					tok = l.getToken();
					System.out.println("In prog default token is " + tok.display());
				}//end while()
		}//end switch
	}//end of prog()
	
	public void progPP(){
		//debugging
		System.out.println("In progPP()");
		
		switch(tok.type){
			//prog’’ -> compstmt .
			case(Token.BEGIN): compstmt(); match(Token.DOT);
				break;
				
			//prog’’ -> subdeclars compstmt .
			case(Token.PROCEDURE): subdeclars(); compstmt(); match(Token.DOT);
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
		System.out.println("In progPPP()");
		
		switch(tok.type){
			//prog’’’ -> compstmt .
			case(Token.BEGIN): compstmt(); match(Token.DOT);
				break;
				
			//prog’’’ -> subdeclars compstmt .
			case(Token.PROCEDURE): subdeclars(); compstmt(); match(Token.DOT);
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
		System.out.println("In idlist()");
		
		switch (tok.type){
			case(Token.ID): match(Token.ID); idlistP();
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
		
		switch(tok.type){
			//idlist’ -> , id idlist’
			case(Token.COMMA): match(Token.COMMA); match(Token.ID); idlistP();
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
		System.out.println("In declar()");
		
		switch(tok.type){

			//declar -> var id : type ; declar’
			case(Token.VAR): match(Token.VAR); match(Token.ID); match(Token.COLON); type(); match(Token.SEMI); declarP();
				break;
			
			default: System.out.println("Syntax Error: In declar() Expected to receive " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In declar() Expected to receive " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { begin procedure EOF }
				while(tok.type != Token.BEGIN && tok.type != Token.PROCEDURE && tok.type != Token.EOF){
					tok = l.getToken();
					System.out.println("In declar default token is " + tok.display());
				}//end while
		}//end switch()
	}//end declar()
	
	public void declarP(){
		//debugging
		System.out.println("In declarP()");
		
		switch (tok.type){
			//declar’ -> ?
			case(Token.BEGIN): //do nothing
				break;
			
			//declar’ -> ?
			case(Token.PROCEDURE): //do nothing
				break;
				
			//declar’ -> var id : type ; declar’
			case(Token.VAR): match(Token.VAR); match(Token.ID); match(Token.COLON); type(); match(Token.SEMI); declarP();
				break;
			
			default: System.out.println("Syntax Error: In declarP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + ", " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In declarP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + ", " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { begin procdure EOF }
				while(tok.type != Token.BEGIN && tok.type != Token.PROCEDURE && tok.type != Token.EOF){
					tok = l.getToken();
					System.out.println("In declarP default token is " + tok.display());
				}//end while
		}//end switch()
	}//end declarP()
	
	public void type(){
		//debugging
		System.out.println("In type()");
		
		switch(tok.type){
			//type -> array [ num . . num ] of standtype
			case(Token.ARRAY): match(Token.ARRAY); match(Token.OPENSQ); match(Token.NUM);
				match(Token.DD); match(Token.NUM); match(Token.CLOSESQ); match(Token.OF); standtype();
				break;
				
			//type -> standtype
			case(Token.INT): standtype();
				break;
				
			//type -> standtype
			case(Token.REAL): standtype();
				break;
			
			default: System.out.println("Syntax Error: In type() Expected to receive " + tok.converter(Token.ARRAY) + ", " + tok.converter(Token.INT) + ", " + tok.converter(Token.REAL) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In type() Expected to receive " + tok.converter(Token.ARRAY) + ", " + tok.converter(Token.INT) + ", " + tok.converter(Token.REAL) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ; ) EOF}
				while(tok.type!=Token.SEMI && tok.type!=Token.CLOSEPRN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In type default token is " + tok.display());
				}//end while
		}//end switch()
	}//end type()
	
	public void standtype(){
		//debugging
		System.out.println("In standtype()");
		
		switch(tok.type){
			//standtype -> integer
			case(Token.INT): match(Token.INT);
				break;
			
			//standtype -> real
			case(Token.REAL): match(Token.REAL);
				break;
				
			default: System.out.println("Syntax Error: In standtype() Expected to receive " + tok.converter(Token.INT) + ", " + tok.converter(Token.REAL) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In standtype() Expected to receive " + tok.converter(Token.INT) + ", " + tok.converter(Token.REAL) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ; ) EOF}
				while(tok.type!=Token.SEMI && tok.type!=Token.CLOSEPRN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In standtype default token is " + tok.display());
				}//end while
		}//end switch()
		
	}//end standtype()
	
	public void subdeclars(){
		//debugging
		System.out.println("In subdeclars() ");
		
		switch(tok.type){
			//subdeclars -> subdeclar ; subdeclars’
			case(Token.PROCEDURE): subdeclar(); match(Token.SEMI); subdeclarsP();
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
		System.out.println("In subdeclarsP() ");
		
		switch(tok.type){
			//subdeclars’ -> ?
			case(Token.BEGIN): //do nothing
				break;
				
			//subdeclars’ -> subdeclar ; subdeclars’
			case(Token.PROCEDURE): subdeclar(); match(Token.SEMI); subdeclarsP();
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
		System.out.println("In subdeclar()");
		
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
		System.out.println("In subdeclarPP() ");
		
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
				
			default: System.out.println("Syntax Error: In subdeclarPP() Expected to receive " + tok.converter(Token.BEGIN) + ", " + tok.converter(Token.PROCEDURE) + ",or " + tok.converter(Token.VAR) + " Received " + tok.converter(tok.type) + " instead");
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
		System.out.println("In subdeclarPPP()");
		
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
		System.out.println("In subhead() ");
		
		switch(tok.type){
			//subhead -> procedure id subhead’’
			case(Token.PROCEDURE): match(Token.PROCEDURE); match(Token.ID); subheadPP();
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
		System.out.println("In subheadPP() ");
		
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
		System.out.println("In args()");
		 
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
		System.out.println("In paramlist() ");
		
		switch(tok.type){
			//paramlist -> id : type paramlist’
			case(Token.ID): match(Token.ID); match(Token.COLON); type(); paramlistP();
				break;
			
			default: System.out.println("Syntax Error: In paramlist() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In paramlist() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In paramlist default token is " + tok.display());
				}//end while
		}//end switch()
	}//end paramlist()
	
	public void paramlistP(){
		//debugging
		System.out.println("In paramlistP() ");
		
		switch(tok.type){
			//paramlist’ -> ; id : type paramlist’
			case(Token.SEMI): match(Token.SEMI); match(Token.ID); match(Token.COLON); type(); paramlistP();
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
		System.out.println("In compstmt()");
		
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
		System.out.println("In compstmtPP()");
		
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
		System.out.println("In optstmts() ");
		
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
		System.out.println("In stmtlist()");
		
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
		System.out.println("In stmtlistP()");
		
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
	
	public void stmt(){
		//debugging
		System.out.println("In stmt() ");
		
		switch(tok.type){
			//stmt -> compstmt
			case(Token.BEGIN): compstmt();
				break;
			
			//stmt -> procstmt
			case(Token.CALL): procstmt();
				break;
			
			//stmt -> variable assignop exp
			case(Token.ID): variable(); match(Token.ASSIGN); exp();
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
		}//end switch()
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
	
	public void variable(){
		//debugging
		System.out.println("In variable() ");
		
		switch(tok.type){
			//variable -> id variable’’
			case(Token.ID): match(Token.ID); variablePP();
				break;
			
			default: System.out.println("Syntax Error: In variable() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In variable() Expected to receive " + tok.converter(Token.ID) + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { assign EOF }
				while(tok.type!=Token.ASSIGN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In variable default token is " + tok.display());
				}//end while
		}//end switch()
	}//end variable()
	
	public void variablePP(){
		//debugging
		System.out.println("In variablePP() ");
		
		switch(tok.type){
			//variable’’ -> ?
			case(Token.ASSIGN): //do nothing
				break;
			
			//variable’’ -> [ exp ]
			case(Token.OPENSQ): match(Token.OPENSQ); exp(); match(Token.CLOSESQ);
				break;
			
			default: System.out.println("Syntax Error: In variablePP() Expected to receive " + tok.converter(Token.ASSIGN) + ",or " + tok.converter(Token.OPENSQ)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In variablePP() Expected to receive " + tok.converter(Token.ASSIGN) + ",or " + tok.converter(Token.OPENSQ)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { assign EOF }
				while(tok.type!=Token.ASSIGN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In variablePP default token is " + tok.display());
				}//end while
		}//end switch()
	}//end variablePP()
	
	public void procstmt(){
		//debugging
		System.out.println("In procstmt() ");
		
		switch(tok.type){
			//procstmt -> call id procstmt’’
			case(Token.CALL): match(Token.CALL); match(Token.ID); procstmtPP();
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
		System.out.println("In procstmtPP() ");
		
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
		System.out.println("In explist() ");
		
		switch(tok.type){
			//explist -> exp explist’
			case(Token.ID): exp(); explistP();
				break;
			
			//explist -> exp explist’
			case(Token.NOT): exp(); explistP();
				break;
			
			//explist -> exp explist’
			case(Token.NUM): exp(); explistP();
				break;
			
			//explist -> exp explist’
			case(Token.PLUS): exp(); explistP();
				break;
			
			//explist -> exp explist’
			case(Token.MINUS): exp(); explistP();
				break;
			
			//explist -> exp explist’
			case(Token.OPENPRN): exp(); explistP();
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
		System.out.println("In explistP() ");
		
		switch(tok.type){
			//explist’ -> , exp explist’
			case(Token.COMMA): match(Token.COMMA); exp(); explistP();
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
	
	public void exp(){
		//debugging
		System.out.println("In exp() ");
		
		switch(tok.type){
			//exp -> simpexp exp’’
			case(Token.ID): simpexp(); expPP();
				break;
			
			//exp -> simpexp exp’’
			case(Token.NOT): simpexp(); expPP();
				break;
			
			//exp -> simpexp exp’’
			case(Token.NUM): simpexp(); expPP();
				break;
			
			//exp -> simpexp exp’’
			case(Token.PLUS): simpexp(); expPP();
				break;
			
			//exp -> simpexp exp’’
			case(Token.MINUS): simpexp(); expPP();
				break;
			
			//exp -> simpexp exp’’
			case(Token.OPENPRN): simpexp(); expPP();
				break;
			
			default: System.out.println("Syntax Error: In exp() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In exp() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) ] , else ; end do then EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In exp default token is " + tok.display());
				}//end while
		}//end switch
		
	}//end exp()
	
	public void expPP(){
		//debugging
		System.out.println("In expPP()");
		
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
			case(Token.RELOP): match(Token.RELOP); simpexp();
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
		}//end switch()
		
	}//end expPP
	
	public void simpexp(){
		//debugging
		//System.out.println("In simpexp() ");
		
		switch(tok.type){
			//simpexp -> term simpexp’
			case(Token.ID): term(); simpexpP();
				break;
			
			//simpexp -> term simpexp’
			case(Token.NOT): term(); simpexpP();
				break;
			
			//simpexp -> term simpexp’
			case(Token.NUM): term(); simpexpP();
				break;
			
			//simpexp -> sign term simpexp’
			case(Token.PLUS): sign(); term(); simpexpP();
				break;
			
			//simpexp -> sign term simpexp’
			case(Token.MINUS): sign(); term(); simpexpP();
				break;
			
			//simpexp -> term simpexp’
			case(Token.OPENPRN): term(); simpexpP();
				break;
			
			default: System.out.println("Syntax Error: In simpexp() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In simpexp() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", " + tok.converter(Token.PLUS) + ", " + tok.converter(Token.MINUS) + ",or " + tok.converter(Token.OPENPRN)+ " Received " + tok.converter(tok.type) + " instead");
				//Sync = { ) ] , else ; end do then relop EOF }
				while(tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.RELOP && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In simpexp default token is " + tok.display());
				}//end while			
		}//end switch()
		
	}//end simpexp()
	
	public void simpexpP(){
		//debugging 
		//System.out.println("In simpexpP() ");
		
		switch(tok.type){
			//simpexp’ -> addop term simpexp’
			case(Token.ADDOP): match(Token.ADDOP); term(); simpexpP();
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
		}//end switch()
		
	}//end simpexpP()
	
	public void term(){
		//debugging
		//System.out.println("In term() ");
		
		switch(tok.type){
			//term -> factor term’
			case(Token.ID): factor(); termP();
				break;
			//term -> factor term’
			case(Token.NOT): factor(); termP();
				break;
			
			//term -> factor term’
			case(Token.NUM): factor(); termP();
				break;
			
			//term -> factor term’
			case(Token.OPENPRN): factor(); termP();	
				break;
			
			default: System.out.println("Syntax Error: In term() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In term() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
			//Sync = { addop ) ] , else ; end do then relop EOF }
			while(tok.type!=Token.ADDOP && tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.RELOP && tok.type!=Token.EOF){
				tok = l.getToken();
				System.out.println("In term default token is " + tok.display());
			}//end while			
		}//end switch
	}//end term()
	
	public void termP(){
		//debugging
		//System.out.println("In termP() ");
		
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
			case(Token.MULOP): match(Token.MULOP); factor(); termP();
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
		}//end switch()
		
	}//end termP()
	
	public void factor(){
		//debugging
		//System.out.println("In factor() ");
		
		switch(tok.type){
			//factor -> id factor’’
			case(Token.ID): match(Token.ID); factorPP();
				break;
				
			//factor -> not factor
			case(Token.NOT): match(Token.NOT); factor();
				break;
			
			//factor -> num
			case(Token.NUM): match(Token.NUM);
				break;
			
			//factor -> ( exp )
			case(Token.OPENPRN): match(Token.OPENPRN); exp(); match(Token.CLOSEPRN);
				break;
			
			default: System.out.println("Syntax Error: In factor() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
				l.lfm.printError("Syntax Error: In factor() Expected to receive " + tok.converter(Token.ID) + ", " + tok.converter(Token.NOT) + ", " + tok.converter(Token.NUM) + ", or " + tok.converter(Token.OPENPRN)  + " Received " + tok.converter(tok.type) + " instead");
				//Sync = { mulop addop ) ] , else ; end do then relop EOF }
				while(tok.type!=Token.MULOP && tok.type!=Token.ADDOP && tok.type!=Token.CLOSEPRN && tok.type!=Token.CLOSESQ && tok.type!=Token.COMMA && tok.type!=Token.ELSE && tok.type!=Token.SEMI && tok.type!=Token.END && tok.type!=Token.DO && tok.type!=Token.THEN && tok.type!=Token.RELOP && tok.type!=Token.EOF){
					tok = l.getToken();
					System.out.println("In factor default token is " + tok.display());
				}//end while				
		}//end switch()
	}//end factor()
	
	public void factorPP(){
		//debugging	
		//System.out.println("In factorPP() ");
		
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
			case(Token.OPENSQ): match(Token.OPENSQ); exp(); match(Token.CLOSESQ);
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
		}//end switch()
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
}
