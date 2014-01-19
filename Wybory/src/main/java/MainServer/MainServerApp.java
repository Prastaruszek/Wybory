package MainServer;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.io.IOException;

public class MainServerApp {
	static int numberOfThreads = 0;
	public static void main(String args[]){	
		try{
	    	System.setProperty("javax.net.ssl.keyStore","mySrvKeystore");
			System.setProperty("javax.net.ssl.keyStorePassword","123456");
			SSLServerSocketFactory SocketFactory=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

			SSLServerSocket welcomeSocket=(SSLServerSocket)SocketFactory.createServerSocket(20006);

			//welcomeSocket.setEnabledCipherSuites(new String[] {"TLS_RSA_WITH_AES_128_CBC_SHA"});
			MainServerThread.loadCandidates();
			new Thread(new MainServerPrimaryThread()).start();
			while(true){
				SSLSocket connectionSocket=(SSLSocket)welcomeSocket.accept();
				/*for(String x : connectionSocket.getEnabledCipherSuites()){
					System.out.println(x);
				}*/
				numberOfThreads++;
				new Thread(new MainServerCommunicationThread(connectionSocket)).start();
			}
		}
		catch(IOException e){
			System.out.println(e+"tata");
		}
	}
}
