package MainServer;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import LoginsAndPasswords.LoginsPasswordsStore;
import LoginsAndPasswords.PasswordEncryptionService;

public class MainServerApp {
	static int numberOfThreads = 0;
	static int roundTime = 60000, initializationTime = 60000;
	static long time=new Date().getTime()+roundTime + initializationTime;
	static boolean end;
	

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




