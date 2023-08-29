/*
 * Created on Nov 6, 2004
 */
package edu.utulsa.unet;
import java.io.*;
import java.net.*;
import java.util.*;
//import java.nio.*;
/**
 * @author shellyjo
 */
public class RReceiveUDP implements RReceiveUDPI{
	
	int mode = 0;
	long modeParam = 0;
	String filename = "";
	int port = 12987;
	TreeMap tempStore = new TreeMap();
	//int seqNum = 0;//packet sequence number
	//FileOutputStream fos = null;
	//BufferedOutputStream bos = null;
	int rPort = -1;
	InetAddress rAddr = null;
	boolean open = false;//if file has been opened already
	File output = null;
	int totalPackets = 0;

	
	public static void main(String[] args) {
	}//end main method
	
	public boolean setMode(int m){
		boolean rval = true;
		this.mode = m;
		return rval;
	}//end setMode
	
	public int getMode(){
		return this.mode;		
	}//end getMode
	
	public boolean setModeParameter(long n){
		boolean rval = true;
		this.modeParam  = n;
		return rval;
	}//end setModeParameter
	
	public long getModeParameter(){
		return this.modeParam;
	}//end getModeParameter
	
	public void setFilename(String fname){
		this.filename = fname;
	}//end setFilename method
	
	public String getFilename(){
		return this.filename;
	}//end getFilename method
	
	public boolean setLocalPort(int p){
		boolean rval = true;
		this.port = p;
		return rval;
	}//end setLocalPort method
	
	public int getLocalPort(){
		return this.port;
	}//end getLocalPort method
	
	public boolean receiveFile(){
		boolean rval = false;
		
		switch(getMode()){
			case(0): stopAndWait();
				break;
			
			case(1): logicalChannels();
				break;
			
			case(2): slidingWindow();
				break;
			
			default: System.out.println("Incorrect Mode Parameter in receiveFile()");  
		}//end switch(getMode)
		
		return rval;
	}//end receiveFile method
	
	private int getSeqNum( DatagramPacket packet){
		int rval = 0;
		byte[] p = packet.getData();
		
		
		return rval;
	}//end getSeqNum()
	
	private boolean stopAndWait(){
		boolean rval = false;
		
		try{
			//create socket
			UDPSocket socket = new UDPSocket(getLocalPort());
			int packetSize = socket.getSendBufferSize();
			
			receivePacket(socket);
			
			rval = true;
			
		}catch(SocketException e){
			System.out.println("Could not create socket for receiver in stopAndWait" + e);
			rval = false;
		}//end catch(SocketException)
				
		//rebuild file
		rebuild();
		
		//print FINISHED
		System.out.println("Finished the Transfer");
		
		return rval;
	}//end stopAndWait()
	
	private boolean logicalChannels(){
		boolean rval = false;
		System.out.println("The Concurrent Logical Channel algorithm has not been implemented.  Please reset mode parameter");
		return rval;
	}//end logicalChannels()
	
	private boolean slidingWindow(){
		boolean rval = false;
		System.out.println("The Sliding Window algorithm has not been implemented.  Please reset mode parameter");
		return rval;
	}//end slidingWindow()

	private DatagramPacket createAck(DatagramPacket packet){
		byte[] pData = packet.getData();
		int ptr = 0;
		
		byte[] sNum = new byte[4];
		for(int i = 0; i < 4; i++){
			sNum[i]=pData[ptr];
			ptr++;
		}//end for seqNum
		byte[] pSize = new byte[4];
		for(int x = 0; x < 4; x++){
			pSize[x]=pData[ptr];
		}//end for packet size
		
		byte[] n = toByteArray(Integer.MAX_VALUE);
		byte[] data = concat(sNum, pSize, n);
		
		DatagramPacket ack = new DatagramPacket(data, data.length, rAddr, rPort);
		
		return ack;
		
	}//end createPacket()
	
	private void rebuild (){
		
		//TODO tree map write curly brace to beginning and end so only write from [1] to [EOF-1]
		int totalPackets = tempStore.size();
		System.out.println("Intend to write " + tempStore.size());

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		//open file
		try{
			output = new File(getFilename());
			output.createNewFile();
			fos = new FileOutputStream(output);
			//bos = new BufferedOutputStream(fos);
			open = true;
			
			if(output.canWrite()){ 
				System.out.println("File " + getFilename() + " has been opened ");
			}else{
				System.out.println("Cannot write to file");
			}//end else
			
			//write data to file
			for(int i=0; i < totalPackets; i++){
				String index = new Integer(i).toString();
				
				//TODO check here if file writing does not work
				if(tempStore.containsKey(index)){
					
					//fos.flush();
					//bos.flush();
					
					System.out.println("Writing packet " + index.toString());
					byte[] bb = (byte[]) tempStore.get(index);
										
					fos.write(bb);
				}else {//end if(tempStore contains key)
					System.out.println("Packet " + index.toString() + " missing.");
				}			
			}//end for
		
			//close file
			fos.close();
			//bos.close();
	
			open = false;
			System.out.println("File has been closed.");		
		
		}catch(IOException e){
			System.out.println("Could  not open file. "+e);
		}//end catch
		
	}//end rebuild(packet)
	
	private void setReceiver(DatagramPacket packet, UDPSocket socket){
		this.rPort = packet.getPort();
		this.rAddr = packet.getAddress();
		System.out.println("Receiver is " + this.rPort + this.rAddr );

		try{
			socket.bind(packet.getSocketAddress());
			
		}catch(SocketException e){
			System.out.println("Could not bind socket");
		}//end catch(SocketException)
	}//end setReceiver(packet())
	
	private void receivePacket(UDPSocket socket){
		System.out.println("Receiving Packet...");
		
		try{
			byte[] data = new byte[socket.getSendBufferSize()];
			DatagramPacket ack = null;
			DatagramPacket packet = new DatagramPacket(data, data.length);
						
			socket.receive(packet);
			
			if(this.rPort < 0 && packet!=null){
				setReceiver(packet, socket);
			}
			
			if(nullPayload(packet)){
				System.out.println("Packet data == null");
				return;
				
			}else {//end if(packet.getData)
				System.out.println("Packet data not null");
				//System.out.println("Packet received is: " + display(packet));
				
				store(packet);//put packet in holding spot
				
				try{
					//send ack
					ack = createAck(packet);
					System.out.println("Ack created is: ");//debugging
					System.out.println(display(ack));//debugging
					socket.send( ack );
					
				}catch(Exception e){
					System.out.println("Ack not sent in RReceiveUDP receivePacket()");
				}//end catch(SocketException)
					
				receivePacket(socket);
			}
			
		}catch(Exception e){
			System.out.println("Problems receiving in receivePacket RReceiveUDP. " + e);
		}
		
		

	}//end receivePacket()
	
	private boolean nullPayload(DatagramPacket packet){
		boolean rval = false;
		byte[] data = packet.getData();
		int max = Integer.MAX_VALUE;
		
		int p = byteToInt(8, data);
		
		//debugging 
		System.out.println("Max value is " + max);
		
		if(max==p){
			rval=true;
			System.out.println("Packet is null");
		}

		return rval;
		
	}//end nullPayload
	
/*	private void finish(){
		closeFile();
	}//end finish
*/
	
	private static int byteToInt(int offset, byte[] src) {
		
		return (src[offset + 3] & 0xff) | ((src[offset + 2] & 0xff) << 8) |
		 	((src[offset + 1] & 0xff) << 16) | ((src[offset] & 0xff) << 24);
		 		 
	}//end byteToInt()
	
	private static byte[] toByteArray(int i){
		byte [] dest =new byte[4];
		int temp = 0;
        dest[temp] = (byte) ((i >> 24) & 0xff);
        dest[temp + 1] = (byte) ((i >> 16) & 0xff);
        dest[temp + 2] = (byte) ((i >> 8) & 0xff);
        dest[temp + 3] = (byte) (i & 0xff);
        return dest;
	}
	
	private void store(DatagramPacket packet){
		byte[] pData = packet.getData();
		byte[] sequence = new byte[4];
		byte[] packetSize = new byte[4];
		byte[] data = new byte[pData.length - 8];
		
		int ptr = 0;
		for (int i = 0; i < 4; i++){
			sequence[i]=pData[ptr];
			ptr++;
		}//end for seqNum
		for (int x = 0; x < 4; x++){
			packetSize[x]=pData[ptr];
			ptr++;
		}//end for packetsize
		for (int y = 0; y < data.length; y++){
			data[y]=pData[ptr];
			ptr++;
		}//end for data
		
		int seqNum = byteToInt(0, sequence);
		System.out.println("Sequence Number of packet being stored: " + seqNum);
		int pSize = byteToInt(0, packetSize);
		System.out.println("Size of packet being stored " + pSize);
		
		System.out.println("Going to store data in " + seqNum);
		
		//tempStore.add(seqNum, data);
		if(tempStore.containsKey(Integer.toString(seqNum))){
			System.out.println("Packet has been received already.");
		}else{
			//System.out.println("contents of file: " + seqNum + " " + new String(data));
			tempStore.put(Integer.toString(seqNum), data);
			System.out.println("Stored packet " + Integer.toString(seqNum));
		}
		
	}//end store(packet)
	
	private byte[] concat(byte[] first, byte[]second, byte[]third){
		byte[] rval = new byte[first.length + second.length + third.length];
		
		int ptr = 0;
		for(int i = 0; i < first.length; i++){
			rval[ptr]=first[i];
			ptr++;
		}//end for first
		for(int x = 0; x < second.length; x ++){
			rval[ptr]=second[x];
			ptr++;
		}//end for second
		for(int y = 0; y < third.length; y++){
			rval[ptr]=third[y];
			ptr++;
		}//end for third
		
		return rval;
	}//end concat
		
	private String display(DatagramPacket packet){
		String rval = "";
		
		byte[] pData = packet.getData();
		byte[] seqNum = new byte[4];
		byte[] pSize = new byte[4];
		byte[] data = new byte[pData.length - 8];
		
		int f = 0;
		for(int i = 0; i < 4; i++){
			seqNum[i]=pData[f];
			f++;
		}//end for seqNum
		for(int x = 0; x < 4; x++){
			pSize[x]=pData[f];
			f++;
		}//end for pSize
		for(int y = 0; y < data.length; y++){
			data[y]=pData[f];
			f++;
		}//end for data
		
		int seq = byteToInt(0, seqNum);
		int size = byteToInt(0, pSize);
		System.out.println("_____________________________________");
		System.out.println("Packet Sequence Number: " + seq);
		System.out.println(" Packet Size: " + size);
		System.out.println(" Packet data: " + data.toString());
		System.out.println("_____________________________________");
		
		return rval;
	}//end display
	
}//end RReceiveUDP class
