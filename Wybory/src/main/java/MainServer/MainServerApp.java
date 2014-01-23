package MainServer;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * Here is the Main Server loop used to accept connections from local servers.
 *
 */
public class MainServerApp {
	static int numberOfThreads = 0;
	static int roundTime = 60000, initializationTime = 140000;
	static long time;
	static boolean end;
	

	public static void main(String args[]){	
		Scanner sc = new Scanner(System.in);
		try{
	    	System.setProperty("javax.net.ssl.keyStore","mySrvKeystore");
			System.setProperty("javax.net.ssl.keyStorePassword","123456");
			SSLServerSocketFactory SocketFactory=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

			SSLServerSocket welcomeSocket=(SSLServerSocket)SocketFactory.createServerSocket(20006);
			
			while(true)
			{
				System.out.println("Type duration (in seconds) of one round except the first.");
				System.out.println("Press enter to set the default duration [60 s].");
				String s = sc.nextLine();
				if(s.equals("")) break;
				Integer dur;
				try{
					dur = Integer.parseInt(s);
					if(dur <= 0) System.out.println("Duration invalid");
					else
					{
						roundTime = dur*1000;
						break;
					}
				}catch (NumberFormatException e)
				{
					System.out.println("Duration invalid");
				}
			}
			
			while(true){
				System.out.println("Type duration (in seconds) of the first round");
				System.out.println("You will have to run all the LocalServer and all the Client applications,");
				System.out.println("log in and send votes for all of them during this time.");
				System.out.println("Press enter to set the default duration [200 s].");
			
				String s = sc.nextLine();
				if(s.equals("")) break;
				Integer dur;
				try{
					dur = Integer.parseInt(s);
					if(dur <= 0) System.out.println("Duration invalid");
					else if(dur <roundTime/1000) System.out.println("This value cannot be less than the previous one ("
							+ roundTime/1000 + ")");
					else
					{
						initializationTime = dur*1000-roundTime;
						break;
					}
				}catch (NumberFormatException e)
				{
					System.out.println("Duration invalid");
				}
			}
			
			System.out.println("Server succesfully started.");
			time=new Date().getTime()+roundTime + initializationTime;
			
			//welcomeSocket.setEnabledCipherSuites(new String[] {"TLS_RSA_WITH_AES_128_CBC_SHA"});
			MainServerThreadAbstractClass.loadCandidates();
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
			//System.out.println(e+"tata");
		}
	}
}




