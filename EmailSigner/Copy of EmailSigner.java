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

	public static void main(String[] args) throws IOException {
		Security.addProvider(new BouncyCastleProvider());

		EmailSigner emSign = new EmailSigner();
		emSign.getKeys();
		//System.out.println(emSign.pub);
		//System.out.println(emSign.priv);
		
		//read message in from file
		byte[] message = emSign.input();
		System.out.print("The original message: " + emSign.printable(message));
		
		//sign the message
		byte[] signhash = emSign.tag(message);
		System.out.println("signhash = " + signhash);
		System.out.print("Signature printed as String: " + emSign.printable(signhash));
		
		//verify the message
		
	}
	
	byte[] verifyTag (byte[] signhash){
		byte[] checked = null;
		
		try{
			Signature sig = Signature.getInstance("SHA1withRSA", "BC");
			sig.initVerify(pub);
		}
		catch (Exception e){
			System.out.println("Could not verify" + e);
		}
		
		return checked;
	
	}

	void getKeys() {

		KeyStoreGenerator ksg = new KeyStoreGenerator();
		priv = ksg.getPrivate();
		pub = ksg.getPublic();
	}

	byte[] tag(byte[] message) {
		byte[] signhash = null;//byte [] that holds signature

		try {
			
			
			Signature sig = Signature.getInstance("SHA1withRSA", "BC");
			sig.initSign(priv);
			//System.out.println(sig);
			sig.update( message );
			int hashValue = sig.hashCode();
			System.out.println("\n" + "the hash is " + hashValue);
			
			signhash = sig.sign();
			
			

		} catch (Exception e) {
			System.out.println("Error occured" + e);
		}
		return signhash;
	}

	/* 
	 * reads bytes from a file and returns them in a byte array
	 */
	byte[] input() throws IOException {
		//byte array to be returned

		String filename = "C:\\java\\Project3\\email.txt";
		FileInputStream emfis = new FileInputStream("C:\\java\\email.txt");

		byte[] email = new byte[emfis.available()];
		emfis.read(email);

		return email;

	}

	/*
	 * converts byte[] to String and prints it to the screen
	 */
	String printable(byte[] message) {

		String printable = new String(message);
		return printable;

	}
}
