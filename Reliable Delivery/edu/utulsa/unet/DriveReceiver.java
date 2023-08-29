/*
 * Created on Nov 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.utulsa.unet;

/**
 * @author shellyjo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DriveReceiver {

	public static void main(String[] args) {
		RReceiveUDP receiver = new RReceiveUDP();
		receiver.setMode(0);
		receiver.setModeParameter(512);
		receiver.setFilename("important.txt");
		receiver.setLocalPort(32456);
		receiver.receiveFile();
		System.out.println("Finished Main()");
	}
}
