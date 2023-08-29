/*
 * Created on Nov 6, 2004
 */
package edu.utulsa.unet;
import java.net.*;
/**
 * @author shellyjo
 */
public class RSendUDPThread {
	UDPSocket socket = null;
	InetAddress receiver = null;
	int port = 0;
	
	public static void main(String[] args){
	}
	
	public RSendUDPThread (UDPSocket s){
		this.socket = s;
	}//end constructor
	
	public void run(DatagramPacket packet){
		
		try{
			socket.send(packet);
		}catch(Exception e){
			System.out.println("In RSendUDPThread send method. " + e);
		}//end catch(Exception e)
		
		//socket.connect(receiver, port);
	}//end run()
	
	public boolean send(DatagramPacket packet){
		boolean rval = false;
		try{
			socket.send(packet);
			rval = true;
		}catch(Exception e){
			System.out.println("In RSendUDPThread send method. " + e);
			rval = false;
		}//end catch(Exception e)
		return rval;
	}//end send()
	
}//end RSendUDPThread class
