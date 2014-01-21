package LocalServer;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalServerApp {
	public static CandidatesBank candidatesBank;
	public static Long end_of_turn;
	static boolean allow=true;
	static int curtur;
	static List<List<Candidate>> toures=new LinkedList<List<Candidate>>();
	static Scanner sc = new Scanner(System.in);
	
	public static void loadCandidates(){
	}
	public static void main(String args[]){	
		try{
	    	System.setProperty("javax.net.ssl.keyStore","LsKeystore");
			System.setProperty("javax.net.ssl.keyStorePassword","admin12");
			System.setProperty("javax.net.ssl.trustStore","mySrvKeystore");
			System.setProperty("javax.net.ssl.trustStorePassword","123456");
			loadCandidates();
			SSLServerSocketFactory SocketFactory=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			Integer portNr = 30001;
			System.out.println("Choose port number. Press ENTER to set default number [30001].");
			String s = sc.nextLine();
			if(!s.equals(""))
				portNr = new Integer(s);
			SSLServerSocket welcomeSocket=(SSLServerSocket)SocketFactory.createServerSocket(portNr);
						//welcomeSocket.setEnabledCipherSuites(new String[] {"TLS_RSA_WITH_AES_128_CBC_SHA"});
			new Thread(new MSComunicationThread()).start();
			while(true){
				SSLSocket connectionSocket=(SSLSocket)welcomeSocket.accept();
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
