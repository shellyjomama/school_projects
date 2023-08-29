import java.io.*;
import java.net.*;
import java.util.*;
/*
 * Created on Dec 2, 2004
 */
/**
 * @author Shelly Seier
 */
public class HttpServer extends Thread {
	Socket socket = null;
	

	public static void main(String[] args) {
		try{
			int port = 80;
			//TODO what is args length is greater than one
			if (args.length == 1) {
				Integer p = new Integer(args[0]);
				port = p.intValue();
			}//end if
			
			ServerSocket server = new ServerSocket(port);
			
			while(true){
				HttpServer http = new HttpServer(server.accept());
				http.start();
			}//end while
			
		}catch(IOException e){
			System.out.println("Problmes with server socket in main() " + e);
		}
	}//end main
	
	public HttpServer(Socket s){
		this.socket = s;
	}//constructor
	
	
	public void run() {

		try {

			int i = 0;
			String line = "";
			String other = "";
			boolean valid = false;

			BufferedReader buff = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			line = buff.readLine();
			System.out.println("Recieved Request:\n" + line);

			StringTokenizer st = new StringTokenizer(line);

			while (!(other = buff.readLine()).equals("")) {
				System.out.println(other);
				other = other.toUpperCase();
				StringTokenizer strtok = new StringTokenizer(other);

				while (strtok.hasMoreElements()) {
					if (strtok.nextToken().equals("HOST:")) {
						valid = true;
						//System.out.println("Found HOST:");
					}//end if
				}//end while
			}

			BufferedOutputStream byteOut = new BufferedOutputStream(socket
					.getOutputStream());
			BufferedOutputStream textOut = new BufferedOutputStream(socket
					.getOutputStream());
			PrintStream byteWrite = new PrintStream(byteOut);
			PrintWriter textWrite = new PrintWriter(textOut);

			File f = null;	
			
			if (!valid) {
				f = new File("./public_html/Error400.html");
				sendResponse(400, textWrite);
				sendHeader(f, textWrite);
				transferText(f, textWrite);
			} else {//end if
				System.out.println("Valid is true");

				StringTokenizer str = new StringTokenizer(line);
				String method = str.nextToken();

				String file = str.nextToken();
				
				if(file.startsWith("/../")){
					method = "BAD";
				}
				
				f = new File("./public_html/" + file);

				if (method.equals("GET")) {
					System.out.println("method: GET");
					System.out.println("File path is: " + f.getAbsolutePath());
					if (f.exists()) {
						System.out.println("File exists");
						sendResponse(200, textWrite);
						sendHeader(f, textWrite);
						String ext = getExt(f.getAbsolutePath());
						if (ext.equals("text/html")) {
							transferText(f, textWrite);
						} else {
							transferBytes(f, byteWrite);
						}

					} else {
						String ext = getExt(f.getAbsolutePath());
						int resp = 404;
						if (ext.equals("NOT")) {
							resp = 501;
							f = new File("./public_html/Error501.html");
						}else
							f = new File("./public_html/Error404.html");						
						sendResponse(resp, textWrite);
						sendHeader(f, textWrite);
						transferText(f, textWrite);
					}//end if/else (f.exists()

				} else if (method.equals("HEAD")) {
					System.out.println("File path is: " + f.getAbsolutePath());
					if (f.exists()) {
						System.out.println("File exists");
						sendResponse(200, textWrite);
						sendHeader(f, textWrite);
					} else {
						String ext = getExt(f.getAbsolutePath());
						int resp = 404;
						if (ext.equals("NOT")) {
							resp = 501;
							f = new File("./public_html/Error501.html");
						}else
							f = new File("./public_html/Error404.html");						
						sendResponse(resp, textWrite);
						sendHeader(f, textWrite);
						transferText(f, textWrite);
					}//end if/else (f.exists()

				} else if (method.equals("OPTIONS")) {
					f = new File("./public_html/Error501.html");
					sendResponse(501, textWrite);
					sendHeader(f, textWrite);
					transferText(f, textWrite);
				} else if (method.equals("POST")) {
					f = new File("./public_html/Error501.html");
					sendResponse(501, textWrite);
					sendHeader(f, textWrite);
					transferText(f, textWrite);
				} else if (method.equals("PUT")) {
					f = new File("./public_html/Error501.html");
					sendResponse(501, textWrite);
					sendHeader(f, textWrite);
					transferText(f, textWrite);
				} else if (method.equals("DELETE")) {
					f = new File("./public_html/Error501.html");
					sendResponse(501, textWrite);
					sendHeader(f, textWrite);
					transferText(f, textWrite);
				} else if (method.equals("TRACE")) {
					f = new File("./public_html/Error501.html");
					sendResponse(501, textWrite);
					sendHeader(f, textWrite);
					transferText(f, textWrite);
				} else if (method.equals("CONNECT")) {
					f = new File("./public_html/Error501.html");
					sendResponse(501, textWrite);
					sendHeader(f, textWrite);
					transferText(f, textWrite);
				} else {//error
					f = new File("./public_html/Error400.html");
					sendResponse(400, textWrite);
					sendHeader(f, textWrite);
					transferText(f, textWrite);
				}//end else
			}//end else

			socket.close();
			System.out.println("Socket has been disconnected");
		} catch (IOException e) {
			System.out.println("Could not create ServerSocket. " + e);
		}//end catch

	}//end run()

	public void transferText(File f, PrintWriter out) {
		try {
			System.out.println("Sending Content...");
			FileInputStream fis = new FileInputStream(f);

			System.out.println("File has " + fis.available()
					+ " bytes available");

			byte[] data = new byte[fis.available()];
			fis.read(data);
				
			for(int i = 0; i < data.length; i++){
				out.write(data[i]);	
				out.flush();
			}//end for
			
		} catch (IOException e) {
			System.out.println("Could not read File " + e);
		}//end try/catch
	}//end transfer()
	
	public void transferBytes(File f, PrintStream out) {
		try {
			System.out.println("Sending Content...");
			FileInputStream fis = new FileInputStream(f);

			System.out.println("File has " + fis.available()
					+ " bytes available");

			byte[] data = new byte[fis.available()];
			fis.read(data);
				
			for(int i = 0; i < data.length; i++){
				out.write(data[i]);	
				out.flush();
			}//end for
			
		} catch (IOException e) {
			System.out.println("Could not read File " + e);
		}//end try/catch
	}//end transfer()
	
	public void sendHeader(File f, PrintWriter out) {
		Date d = new Date();

		out.println("Server: HttpServer/1.0");
		System.out.println("Server: HttpServer/1.0");

		out.println("Date: " + d);
		System.out.println("Date: " + d);

		out.println("Content-Length: " + f.length());
		System.out.println("Content-Length: " + f.length());

		out.println("Content-Type: " + getExt(f.getAbsolutePath()));
		System.out.println("Content-Type: " + getExt(f.getAbsolutePath()));

		out.println();
		System.out.println();
	}//end sendHeader()

	public void sendResponse(int status, PrintWriter out) {
		String rval = "";
		switch (status) {
		case (200):
			rval = "HTTP/1.1 200 OK";
			break;
		case (400):
			rval = "HTTP/1.1 400 Bad Request";
			break;
		case (404):
			rval = "HTTP/1.1 404 Not Found";
			break;
		case (501):
			rval = "HTTP/1.1 501 Not Implemented";
			break;
		default:
			System.out.println("Error in response code");
		}
		System.out.println("Responding with:\n" + rval);
		out.println(rval);
	}//end sendResponse

	public String getExt(String s) {
		String rval = "";

		if (s.endsWith("htm") || s.endsWith("html")) {
			rval = "text/html";
		} else if (s.endsWith("gif")) {
			rval = "image/gif";
		} else if (s.endsWith("jpg") || s.endsWith("jpeg")) {
			rval = "image/jpeg";
		} else if (s.endsWith("pdf")) {
			rval = "application/pdf";
		} else {
			rval = "NOT";
		}

		return rval;
	}//end getExt

}//end HttpServer
