package LocalServer;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LocalServerApp {
	public static void main(String args[]){
		
		
		try{
	    	System.setProperty("javax.net.ssl.keyStore","mySrvKeystore");
			System.setProperty("javax.net.ssl.keyStorePassword","123456");
			SSLServerSocketFactory SocketFactory=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			SSLServerSocket welcomeSocket=(SSLServerSocket)SocketFactory.createServerSocket(20002);
			//welcomeSocket.setEnabledCipherSuites(new String[] {"TLS_RSA_WITH_AES_128_CBC_SHA"});
			while(true){
				SSLSocket connectionSocket=(SSLSocket)welcomeSocket.accept();
				System.out.println("waiting");
				/*for(String x : connectionSocket.getEnabledCipherSuites()){
					System.out.println(x);
				}*/
				new Thread(new ServerThread(connectionSocket)).start();
			}
		}
		catch(IOException e){
			System.out.println(e+"tata");
		}
	}
}
