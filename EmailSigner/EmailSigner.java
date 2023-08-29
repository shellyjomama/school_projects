/*
 * Created on Apr 1, 2004
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
import java.security.*;
import java.io.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EmailSigner {
	protected PublicKey pub;
	protected PrivateKey priv;
	public byte[] message = null;
	protected byte[] signature = null;
	protected int hashValue = 0;

	public static void main(String[] args) throws IOException {
		Security.addProvider(new BouncyCastleProvider());

		EmailSigner emSign = new EmailSigner();
		emSign.getKeys();
		//System.out.println(emSign.pub);
		//System.out.println(emSign.priv);
		
		//read message in from file
		emSign.input();
		System.out.print("The original message: " + emSign.printable(emSign.message));
		
		//sign the message
		emSign.tag();
		System.out.println("\n" + "The hash is " + emSign.hashValue);
		System.out.println("Signature = " + emSign.signature);
		System.out.print("Signature printed as String: " + emSign.printable(emSign.signature));
		
		
		//verify the message
		emSign.verifyTag();
	}
	
	void verifyTag (){
		
		try{
			Signature sig = Signature.getInstance("SHA1withRSA", "BC");
			sig.initVerify(pub);
			sig.update(message);
			if(sig.verify(signature))
				System.out.println("\nHash is Verified");
			else
				System.out.println("\nHash is NOT Verified");
		}
		catch (Exception e){
			System.out.println("Could not verify" + e);
		}
		
		
	
	}

	void getKeys() {

		KeyStoreGenerator ksg = new KeyStoreGenerator();
		priv = ksg.getPrivate();
		pub = ksg.getPublic();
	}

	void tag() {
		

		try {
			
			
			Signature sig = Signature.getInstance("SHA1withRSA", "BC");
			sig.initSign(priv);
			//System.out.println(sig);
			sig.update( message );
			hashValue = sig.hashCode();
					
			signature = sig.sign();
			
			

		} catch (Exception e) {
			System.out.println("Error occured" + e);
		}
		
	}

	/* 
	 * reads bytes from a file and returns them in a byte array
	 */
	void input() throws IOException {
		//byte array to be returned

		String filename = "C:\\java\\Project3\\email.txt";
		FileInputStream emfis = new FileInputStream("C:\\java\\email.txt");

		message = new byte[emfis.available()];
		emfis.read(message);

	}

	/*
	 * converts byte[] to String and prints it to the screen
	 */
	String printable(byte[] text) {

		String printable = new String(text);
		return printable;

	}
}
