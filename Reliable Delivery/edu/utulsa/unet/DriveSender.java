/*
 * Created on Nov 7, 2004
 */
package edu.utulsa.unet;
import java.net.*;
/**
 * @author shellyjo
 */
public class DriveSender {

	public static void main(String[] args) {
		RSendUDP sender = new RSendUDP();
		sender.setMode(0);
		sender.setModeParameter(512);
		sender.setTimeout(10000);
		sender.setFilename("untitled.jpg");
		sender.setLocalPort(23456);
		sender.setReceiver(new InetSocketAddress("127.0.0.1", 32456));
		sender.sendFile();
		System.out.println("Finished Main");
	}
}
