/*
 * Created on Mar 4, 2004
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

//import java.security.KeyPairGeneratorSpi.*;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class KeyStoreGenerator {

	private static KeyPair keyPair;

	public static void main(String[] args) {

		//provider for key pairs
		Security.addProvider(new BouncyCastleProvider());

		KeyStoreGenerator ksg = new KeyStoreGenerator();

		try {

			System.out.println("Please enter alias");
			BufferedReader aliasbuf =
				new BufferedReader(new InputStreamReader(System.in));
			String alias = aliasbuf.readLine();

			System.out.println("Please enter password for keys");
			BufferedReader keypassbuf =
				new BufferedReader(new InputStreamReader(System.in));
			String keypass = keypassbuf.readLine();

			System.out.println("Please enter password for keystore");
			BufferedReader storepassbuf =
				new BufferedReader(new InputStreamReader(System.in));
			String storepass = storepassbuf.readLine();

			ksg.store(keyPair, alias, keypass, storepass);

			System.out.println("Keys have been stored");

		} catch (Exception e) {
			System.out.println(e);
		}

		try {
			System.out.println("RETRIEVING...");

			System.out.println("Please enter alias");
			BufferedReader aliasbuf =
				new BufferedReader(new InputStreamReader(System.in));
			String alias = aliasbuf.readLine();

			System.out.println("Please enter password for keys");
			BufferedReader keypassbuf =
				new BufferedReader(new InputStreamReader(System.in));
			String keypass = keypassbuf.readLine();

			System.out.println("Please enter password for keystore");
			BufferedReader storepassbuf =
				new BufferedReader(new InputStreamReader(System.in));
			String storepass = storepassbuf.readLine();

			ksg.retrieve(keyPair, alias, keypass, storepass);
			
			System.out.println("Keys have been retrieved");


		} catch (Exception e) {
			System.out.println(e);
		}

	}

	private KeyStoreGenerator() {

		try { //Start key pair generation

			//key pair generator with RSA algorithm and BC provider
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");

			//1024 bit key generated by a random number
			keyGen.initialize(1024, new SecureRandom());

			//generates a public and private key
			keyPair = keyGen.generateKeyPair();

			System.out.println(keyPair.getPrivate());
			System.out.println(keyPair.getPublic());

		} catch (Exception exc) { //throw exception if key generation fails
			System.out.println("key generation failed" + exc);
		}

	}

	void store(
		KeyPair keyPair,
		String alias,
		String keypass,
		String storepass) {
		String ksName = "keystore.dat";
		System.out.println("STORING...");

		//alias of the person to which the keys are assigned
		String certauthority = "authority.crt";

		char[] spass = storepass.toCharArray();
		char[] kpass = keypass.toCharArray();

		try {

			//instantiate certificate factory
			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			//read in the certificate from the disk
			FileInputStream crtfis = new FileInputStream(certauthority);
			BufferedInputStream crtbuf = new BufferedInputStream(crtfis);

			//put certificate in array
			Certificate crtarray[] = new Certificate[1];
			crtarray[0] = cf.generateCertificate(crtbuf);

			//BKS is the keystore by bouncycastle
			KeyStore ks = KeyStore.getInstance("BKS");
			//gets instance of a key store
			ks.load(null, null); //loads a null keystore

			//set certificate for alias in the keystore
			System.out.println(ks.getType());
			ks.setKeyEntry(alias, keyPair.getPrivate(), kpass, crtarray);
			//ks.setKeyEntry(alias, keyPair.getPublic(), kpass, crtarray);

			FileOutputStream ksfos = new FileOutputStream(ksName);
			ks.store(ksfos, spass);

		} catch (Exception exc) {
			System.out.println("Key Store generation failed" + exc);
		}

	}
	void retrieve(
		KeyPair keyPair,
		String alias,
		String keypass,
		String storepass) {
		String ksName = "keystore.dat";
		
		char[] kpass = keypass.toCharArray() ;
		char[] spass = storepass.toCharArray() ;
		try {
		
			//�BKS� is the KeyStore implementation by BouncyCastle
			KeyStore ks = KeyStore.getInstance("BKS");
		
			//The keystore name is in the String ksName
			FileInputStream ksfis = new FileInputStream(ksName);
			BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
		
			//The keystore password is in the char array spass
			ks.load(ksbufin, spass);
		
			//The private key password is in the char array kpass
			Key priv = ks.getKey(alias, kpass);
			//PublicKey pub = (PublicKey) ks.getKey(alias, kpass);
			
			System.out.println(priv);
			//System.out.println(pub);
			
		}
		catch (Exception e){
			System.out.println(e);
		}
	}
}