package LocalServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import LoginsAndPasswords.PasswordEncryption;
import LoginsAndPasswords.PasswordProtocol;

/**
 * This is the Thread responsible for contacting with Main Server, sending there partial 
 * results and receiving the final results of the rounds.
 *
 */

public class LocalServerMSCommunicationThread implements Runnable {
	
	private void read_time(BufferedReader input) throws IOException{
		String s = input.readLine();
		long time=Long.parseLong(s.replaceFirst("DEADLINE ", ""));
		LocalServerApp.end_of_turn=time;
	}
	public void run(){
		SSLSocket socket = null;
		try{
		   	System.setProperty("javax.net.ssl.trustStore","mySrvKeystore");
	   		System.setProperty("javax.net.ssl.trustStorePassword","123456");
			SSLSocketFactory mySocketFactory=(SSLSocketFactory)SSLSocketFactory.getDefault();
			socket=(SSLSocket)mySocketFactory.createSocket("localhost", 20006);
			
			BufferedReader input=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter output=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			String s;
			if(!PasswordProtocol.attempt(input, output, LocalServerApp.sc))
				return;
			System.out.println("Ready");
			read_time(input);
			
			s = input.readLine();
			s = input.readLine();
			LinkedList<Candidate> tempCand=new LinkedList<Candidate>();
			int size=Integer.parseInt(s);
			for(int i=0; i<size; ++i){
				s=input.readLine();
				Scanner sca=new Scanner(s);
				sca.next();
				tempCand.add(new Candidate(sca.next(),
							sca.next(),
							new Integer(s.replaceAll("\\D+", ""))));
				sca.close();
			}
			LocalServerApp.candidatesBank=new LocalServerCandidatesBank(tempCand, 100);
			while(true){
				while(true)
				{
					try {
						Thread.sleep(LocalServerApp.end_of_turn-new Date().getTime());
					} catch (Exception e) {
					}
					if(LocalServerApp.end_of_turn <= new Date().getTime()) break;
				}
				do{
					List<Integer> li=LocalServerApp.candidatesBank.countVotes();
					output.write("VOTES_COUNTED");
					output.flush();
					int j=0;
					for(Integer i: li){
						output.write(" "+i);
						output.flush();
						++j;
					}
					output.write("\n");
					output.flush();
					s=input.readLine();
					if(s.matches("LOOSER .*")){
						LocalServerApp.candidatesBank.loses(Integer.parseInt(s.replaceFirst("LOOSER ", "")));
						
						List<Candidate> tempAdd=new ArrayList<Candidate>();
						for(Candidate c: LocalServerApp.candidatesBank.getTempCandidatesList()){
							tempAdd.add(c);
						}
						new String();
						LocalServerApp.toures.add(tempAdd);
						LocalServerApp.curtur++;
						synchronized(Integer.class){
							Integer.class.notifyAll();
						}
						read_time(input);
					}
					else{
						LocalServerApp.winner=Integer.parseInt(s.replaceFirst("WINNER ", ""));
						LocalServerApp.win=true;
						synchronized(Integer.class){
							Integer.class.notifyAll();
						}
						//System.out.println(s);
						try{
							Thread.sleep(30000);
						}catch(InterruptedException e){
							
						}
						System.exit(0);
					}
					//System.out.println("Czy mam przyspieszyc?"+LocalServerApp.candidatesBank.canSendImmediatly);
				}while(LocalServerApp.candidatesBank.canSendImmediatly);
			}
		} catch (UnknownHostException e) {
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
}
