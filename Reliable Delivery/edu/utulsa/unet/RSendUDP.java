/*
 * Created on Nov 6, 2004
 */
package edu.utulsa.unet;
import java.net.*;
import java.io.*;
import java.util.*;
/**
 * @author shellyjo
 */
public class RSendUDP implements RSendUDPI {
	
		int mode = 0;
		long modeParam = 0;
		String filename = "";
		long timeout = 0;
		int port = 12987;
		InetSocketAddress receiver = null;
		boolean open = false;
		int seqNum = 0;//packet seqence number
		private boolean empty = false;//file is empty
		private int totalPackets = 0;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		
		public static void main(String[] args) {
		}//end main
		
		public boolean setMode(int m){
			boolean rval = true;
			this.mode = m;
			return rval;		
		}//end setMode method
		
		public int getMode(){
			return this.mode;
		}//end getMode method
		
		public boolean setModeParameter(long n){
			boolean rval = true;
			this.modeParam = n;
			return rval;
		}//end setModeParameter method
		
		public long getModeParameter(){
			return this.modeParam;
		}//end getModeParameter
		
		public void setFilename(String fname){
			this.filename = fname;
		}//end setFilename method
		
		public String getFilename(){
			return this.filename;
		}//end getFilename method
		
		public boolean setTimeout(long t){
			boolean rval = true;
			this.timeout = t;
			return rval;
		}//end setTimeout method
		
		public long getTimeout(){
			return this.timeout;
		}//end getTimeout method
		
		public boolean setLocalPort(int p){
			boolean rval = true;
			this.port = p;
			return rval;
		}//end setLocalPort method
		
		public int getLocalPort(){
			return this.port;
		}//end getLocalPort method
		
		public boolean setReceiver(InetSocketAddress r){
			boolean rval = true;
			this.receiver = r;
			return rval;
		}//end setReceiver method
		
		public InetSocketAddress getReceiver(){
			return this.receiver;
		}//end getReceiver method
		
		public boolean sendFile(){
			boolean rval = false;
			
			switch(getMode()){
				case(0): stopAndWait();
					break;
				
				case(1): logicalChannels();
					break;
				
				case(2): slidingWindow();
					break;
				
				default: System.out.println("Incorrect Mode Parameter in sendFile()");
			}//end switch(getMode())
			
			return rval;
		}//end sendFile method
		
		private boolean stopAndWait(){
			boolean rval = false;
			
			try{
				//create a sender socket
				UDPSocket socket = new UDPSocket(getLocalPort());
				System.out.println("local port is " + getLocalPort());
				System.out.println("Sender socket port is " + socket.getPort());
				
				int packetSize = socket.getSendBufferSize();
				int to = (int) getTimeout();
				socket.setSoTimeout(to);
				//create a send thread
				RSendUDPThread sendThread = new RSendUDPThread(socket);
				
				//create receive thread
				RReceiveUDPThread receiveThread = new RReceiveUDPThread(socket, socket.getSendBufferSize());
		
				DatagramPacket packet = null;	
					
				//create a packet
				while(!empty){
					packet = createPacket(packetSize);
					sendThread.send( packet );
					System.out.println("\n**********Packet sent.**********");
					
					if(!empty){
						receiveLoopSW(sendThread, socket, packet);
						System.out.println("\n**********Returned from receiveLoopSW.**********");
					}else{
						System.out.println("Finished.  File is empty");
					}
					
				}//end while(createPacket)

				//send final control packets
				long w = getTimeout();
				DatagramPacket control = createNullPacket(packetSize);
				socket.send(control);
				//socket.send(control);
				//socket.send(control);
				
			}catch(Exception e){
				System.out.println("Problem in RSendUDP Stop and Wait " + e);
			}//end catch(Exception e)
			

			
			return rval;
		}//end stopAndWait method
		
		private boolean logicalChannels(){
			boolean rval = false;
			TreeMap pack = new TreeMap();
			
			try{
				//create a sender socket
				UDPSocket socket = new UDPSocket(getLocalPort());
				System.out.println("local port is " + getLocalPort());
				System.out.println("Sender socket port is " + socket.getPort());
				
				int packetSize = socket.getSendBufferSize();
				int to = (int) getTimeout();
				socket.setSoTimeout(to);
				//create a send thread
				RSendUDPThread sendThread = new RSendUDPThread(socket);
				
				//create receive thread
				RReceiveUDPThread receiveThread = new RReceiveUDPThread(socket, socket.getSendBufferSize());
		
				DatagramPacket packet = null;	
					
				//create a packet tree
				while(!empty){
					packet = createPacket(packetSize);
					pack.put( new Integer(getSequence(packet)), packet);	
				}//end create packet tree
					
				sendGroup(pack, sendThread, receiveThread);
					
					if(!empty){
						receiveLoopSW(sendThread, socket, packet);
					}else{
						System.out.println("Finished.  File is empty");
					}

				//send final control packets
				DatagramPacket control = createNullPacket(packetSize);
				socket.send(control);
				socket.send(control);
				socket.send(control);
				
				rval = true;
			}catch(Exception e){
				System.out.println("Problem in RSendUDP Stop and Wait " + e);
			}//end catch(Exception e)
			
			return rval;
		}//end logicalChannels() method
		
		private boolean slidingWindow(){
			boolean rval = false;
			return rval;
		}//end slidingWindow() method
		
		private DatagramPacket createPacket(int packetSize){
			int hlen = 8;
			int payload = packetSize - hlen;
			
			byte[] sNum = new byte[4];
			byte[] pSize = new byte[4];
			
			sNum = toByteArray(getSeqNum());
			System.out.println("Current sequence num: " + getSeqNum());
			//pSize = toByteArray(payload);
						
			//debugging
			System.out.println("Check sequence number " + byteToInt(0,sNum));
			
			if(!open){
				openFile();
			}
			
			DatagramPacket packet = null;
			
			try{
				byte[] data = new byte[payload];
				int check = bis.read(data);
				System.out.println("read in " + check + " characters from file");
				
				byte[] p = null;//byte array to store payload
				
				if(check < payload && check > 0){
					payload = check;
					pSize = toByteArray(payload);					
					
					byte[] temp = new byte[payload];
					for(int i = 0; i < payload; i++){
						temp[i]=data[i];
					}//end transfer
					p = concat(sNum, pSize, temp);
				}else{
					p = concat(sNum, pSize, data);	
					pSize = toByteArray(payload);
				}
				
				if(check > 0){
					packet = new DatagramPacket(p, p.length, getReceiver());
				}else {//end if(check)
					System.out.println("creating null packet");
					packet = createNullPacket(packetSize);
					System.out.println("null packet created");
					empty=true;
				}
			}catch(IOException e){
				System.out.println("Error in createPacket " + e);
			}//end catch(IOException e)
			System.out.println("Returning packet");
			
			return packet;
			
		}//end createPacket()
		
		private void openFile(){			
			try{
				fis = new FileInputStream(getFilename());
				bis = new BufferedInputStream(fis);
							
				open = true;
			}catch(IOException e){
				System.out.println("Could not open File " + e);
			}//end catch(IOException)
		}//end openFile()
		
		private void receiveLoopSW(RSendUDPThread sendThread, UDPSocket socket, DatagramPacket packet){
			boolean rval = false;
			try{
				byte[] buffer = new byte[socket.getSendBufferSize()];
				System.out.println("going to wait for ack");
				DatagramPacket ack = new DatagramPacket(buffer, buffer.length); 
				socket.receive(ack);
				
				int ackNum = checkAck(ack);
				
				System.out.println("Received ack " + ackNum);
				System.out.println("Sent packet " + getSeqNum());
				
				if(ackNum < getSeqNum()){
					sendThread.send(packet);
					receiveLoopSW(sendThread, socket, packet);
					System.out.println("Resending");
				}else{//end if(ack==null)
					incrementSequence();
					System.out.println("Planning to send packet " + getSeqNum());
				}//end else
			}catch(Exception e){
				System.out.println("Problems receiving in receiveLoopSW()" + e);
				try{
					socket.send(packet);
					receiveLoopSW(sendThread, socket, packet);
				}catch(IOException ex){
					System.out.println("in exception, sending packet and starting receive loop again didnt' work " + e);
				}
			}//end catch(Exception e)
			
			
			
		}//end receive(UDPSocket)

		private DatagramPacket createNullPacket(int packetSize){		
			byte[] sNum = new byte[4];
			byte[] pSize = new byte[4];
			
			//debugging
			System.out.println("Creating Null packet " + getSeqNum());
			
			sNum = toByteArray(getSeqNum());
			pSize = toByteArray(packetSize-8);
			
			System.out.println("Sequence number of null packet is " + byteToInt(0, sNum));
			
			if(!open){
				openFile();
			}
			
			int n = Integer.MAX_VALUE;
			
			byte[] max = toByteArray(n);
			byte[] data = concat(sNum, pSize, max);
			DatagramPacket packet = null;
			try{
				packet = new DatagramPacket(data, data.length, getReceiver());
			}catch(SocketException e){
				System.out.println("Could not create null packet in RSendUDP");
			}//end catch()
			
			incrementSequence();
			return packet;
			
		}//end createNullPacket()
		
		private void setTotalPackets(){
			
		}//end setTotalPackets()
		
		private static byte[] toByteArray(int i){
			byte [] dest =new byte[4];
			int temp = 0;
	        dest[temp] = (byte) ((i >> 24) & 0xff);
	        dest[temp + 1] = (byte) ((i >> 16) & 0xff);
	        dest[temp + 2] = (byte) ((i >> 8) & 0xff);
	        dest[temp + 3] = (byte) (i & 0xff);
	        return dest;
		}
		
		private static int byteToInt(int offset, byte[] src) {
			
			return (src[offset + 3] & 0xff) | ((src[offset + 2] & 0xff) << 8) |
			 	((src[offset + 1] & 0xff) << 16) | ((src[offset] & 0xff) << 24);
			 		 
		}//end byteToInt()
		
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
		
		private void incrementSequence(){
			this.seqNum++;
		}//end incrementSequence
		
		private int getSeqNum(){
			return this.seqNum;
		}
		
		private int getSequence(DatagramPacket packet){
			int rval = -1;
			
			byte[] pData = packet.getData();
			byte[] seq = new byte[4];
			
			for(int i = 0; i < 4; i++){
				seq[i]=pData[i];
			}//end for seq
			
			rval = byteToInt(0, seq);
			
			return rval;
		}//end getSequence
		
		private int checkAck(DatagramPacket ack){
			
			int rval = -1;
			
			if(ack.getData() != null){
				byte[] pData = ack.getData();
				byte[] sNum = new byte[4];
				
				for(int i=0; i < 4; i++){
					sNum[i]=pData[i];
				}//end for
				
				rval = byteToInt(0, sNum);
			}
			
			return rval;
		}//end checkAck
		
		private void sendGroup(TreeMap pack, RSendUDPThread sendThread, RReceiveUDPThread receiveThread){
			ArrayList track = new ArrayList();			
			
			for(int i = 0; i < getModeParameter(); i++){
				DatagramPacket packet = (DatagramPacket) pack.firstKey();
				
			}//end for send
			
			track.add( new Integer(receiveThread.receive()));
		}//end sendGroup
		
		
}//end RSendUDP class
