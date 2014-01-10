package LocalServer;

import java.io.IOException;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class LocalServerApp {
	public static void main(String args[]){
		SSLServerSocketFactory SocketFactory=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		try{
			SSLServerSocket welcomeSocket=(SSLServerSocket)SocketFactory.createServerSocket(20000);
			while(true){
				SSLSocket connectionSocket=(SSLSocket)welcomeSocket.accept();
			}
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
}
