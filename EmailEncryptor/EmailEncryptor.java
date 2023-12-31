/*
 * Created on Apr 26, 2004
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
import javax.swing.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.awt.event.*;
import java.io.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EmailEncryptor {

	private Key pub;
	private Key priv;
	private SecretKey secret;
	private SecretKeySpec secretSpec;
	static boolean input = false;
	static JFileChooser fcIn = new JFileChooser();
	static JFileChooser fcOut = new JFileChooser();
	static File email = null;
	static File encryptedEmail = new File("encryptedemail.txt");

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		EmailEncryptor ee = new EmailEncryptor();
		ee.input();
		ee.symmetricKeys();
		ee.asymmetricKeys();
		ee.encryptKey();
		ee.encryptEmail(email);
		ee.decryptKey (encryptedEmail);
		ee.decryptEmail(encryptedEmail);
		

	}

	public EmailEncryptor() {
		fcIn.addActionListener(new openListener());

	}

	void input() {

		try {

			input = true;
			//In response to a button click:
			int returnVal = fcIn.showOpenDialog(null);

		} catch (Exception e) {
			System.out.println("Problem opening file. " + e);
		}

	}

	void asymmetricKeys() {

		/*KeyStoreGenerator ksg = new KeyStoreGenerator();
		priv = ksg.getPrivate();
		pub = ksg.getPublic();*/
		try {

			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");

			//1024 bit key generated by a random number
			keyGen.initialize(1024, new SecureRandom());

			//generates a public and private key
			KeyPair keyPair = keyGen.generateKeyPair();
			priv = keyPair.getPrivate();
			pub = keyPair.getPublic();

		} catch (Exception e) {
			System.out.println("Key generation failed" + e);
		}
	}

	void symmetricKeys() {
		try {

			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128); // 192 and 256 bits may not be available
			secret = kg.generateKey();
			byte[] raw = secret.getEncoded();

			secretSpec = new SecretKeySpec(raw, "AES");
			
			System.out.println("Original Secret Key = " + secret);

		} catch (Exception e) {
			System.out.println("Key generation failed " + e);
		}

	}
	void encryptEmail(File f) {

		try {

			FileInputStream fis = new FileInputStream(email);
			int byteArraySize = fis.available();
			byte[] plaintext = new byte[byteArraySize];

			//read file into byte[] plaintext 
			fis.read(plaintext);

			//Instantiate the cipher
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretSpec);

			//test to see the plaintext byte[]
			System.out.println("Byte []: \n" + plaintext);

			//check the size of the result after encrypted and create a byte[] of that size
			int encryptSize = cipher.getOutputSize(byteArraySize);
			byte[] encryptOutput = new byte[encryptSize];

			//encrypt the byte[] plaintext
			encryptOutput = cipher.doFinal(plaintext);

			FileOutputStream fos = new FileOutputStream(encryptedEmail, true);
			fos.write(encryptOutput);
			fos.close();

		} catch (Exception e) {
			System.out.println("Encryption failed. " + e);
		}

	}

	void encryptKey() {
		try {

			//convert key to byte[]
			Cipher aes = Cipher.getInstance("AES");
			aes.init(Cipher.WRAP_MODE, secret);
			byte[] secretKey = aes.wrap(secret);

			//initialize encryption of secret key
			Cipher rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.ENCRYPT_MODE, pub);

			//check the size of the result after encrypted and create a byte[] of that size
			int encryptSize = rsa.getOutputSize(secretKey.length);
			byte[] encryptedKey = new byte[encryptSize];
			System.out.println("encrypted key will be: " + encryptSize);

			//encrypt
			encryptedKey = rsa.doFinal(secretKey);

			FileOutputStream fos = new FileOutputStream(encryptedEmail);
			fos.write(encryptedKey);
			fos.close();

		} catch (Exception e) {

			System.out.println("Error encrypting Key. " + e);
		}
	}

	void decryptEmail(File f) {
		try{
		
		FileInputStream fis = new FileInputStream(encryptedEmail);
		fis.skip(128);
		byte[] ciphertext = new byte[fis.available()];
		
		fis.read(ciphertext);
		
		Cipher aes = Cipher.getInstance("AES");
		aes.init(Cipher.DECRYPT_MODE, secretSpec);
		
		byte[] plaintext = aes.doFinal(ciphertext);
		String plain = new String (plaintext);
		System.out.println("Decrypted Message = " + plain);
		
		} catch (Exception e){
			System.out.println("Email decryption failed " + e);
		}
	}

	void decryptKey(File f) {
		try {

			FileInputStream fos = new FileInputStream(encryptedEmail);
			byte [] encryptedKey = new byte [128];
			fos.read( encryptedKey, 0, 128 );
			
			Cipher rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.DECRYPT_MODE, priv);
			byte[] key = new byte[128];//space for decrypted key
			
			key = rsa.doFinal(encryptedKey);//puts decrypted key into "key"
			
			System.out.println("key before unwrapped = " + key );
			
			Cipher aes = Cipher.getInstance("AES");
			aes.init(Cipher.UNWRAP_MODE, secretSpec);
			secret = (SecretKey) aes.unwrap ( key, "AES", Cipher.SECRET_KEY );
			
			System.out.println("Decrypted Secret Key = " + secret);
			
		} catch (Exception e) {
			System.out.println("Key decryption failed" + e);
		}
	}

}

class openListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {

		//open the email
		EmailEncryptor.email = EmailEncryptor.fcIn.getSelectedFile();

	}

}
