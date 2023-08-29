/*
 * Created on Nov 6, 2004
 */
package edu.utulsa.unet;
import java.net.*;
import java.io.*;
/**
 * @author shellyjo
 */
public class RReceiveUDPThread extends Thread{
	UDPSocket socket = null;
	int packetSize = 0;
	
	public static void main(String[] args) {
	}//end main()
	
	public RReceiveUDPThread (UDPSocket s, int p){
		this.socket = s;
		this.packetSize = p;
	}//end constructor
	
	public int receive(){
		int rval = -1;
		byte[] data = new byte[packetSize];
		DatagramPacket packet = null; //TODO this may be a problem area if the file is empty  
										//TODO before the prev line, the returned packet could not be resolved
		System.out.println("In receive thread -------------------------");
		try{
			packet = new DatagramPacket(data, data.length);
			socket.receive(packet);
			System.out.println("Data length: " + data.length);
			
			rval = getSeqNum(packet);
						
		}catch(IOException e){
			System.out.println("Could not receive packet in Receive Thread " + e);
			return rval;
		}//end catch
		
		System.out.println("Out receive thread ------------------------");
		
		return rval;
	}//end receive
	
	private static int byteToInt(int offset, byte[] src) {
		
		return (src[offset + 3] & 0xff) | ((src[offset + 2] & 0xff) << 8) |
		 	((src[offset + 1] & 0xff) << 16) | ((src[offset] & 0xff) << 24);
		 		 
	}//end byteToInt()
	
	private int getSeqNum(DatagramPacket packet){
		int rval = -1;
		
		byte[] pData = packet.getData();
		byte[] seq = new byte[4];
		
		for(int i = 0; i < 4; i++){
			seq[i]=pData[i];
		}//end for seq
		
		rval = byteToInt(0, seq);
		
		return rval;
	}//end getSeqNum
	
}//end RReceiveUDP
