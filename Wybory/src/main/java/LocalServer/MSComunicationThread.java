package LocalServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MSComunicationThread implements Runnable {
	public void run(){
		SSLSocket socket = null;
		try{
		   	System.setProperty("javax.net.ssl.trustStore","mySrvKeystore");
	   		System.setProperty("javax.net.ssl.trustStorePassword","123456");
			SSLSocketFactory mySocketFactory=(SSLSocketFactory)SSLSocketFactory.getDefault();
			socket=(SSLSocket)mySocketFactory.createSocket("localhost", 20006);
			
			BufferedReader input=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter output=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	
			output.write("HELLO\n");
			output.flush();
			String s, t;
			s=input.readLine();
			System.out.println(s);
			if(!s.equals("HELLO. WHO ARE YOU?")){
				System.out.println("PROTOCOL FAILED");
				input.close();
				output.close();
				return;
			}
			output.write("LOGIN: ls1, PASS: admin1 \n");
			output.flush();
			s = input.readLine();
			System.out.println(s);
			if(!s.equals("LOGIN OK")){
				input.close();
				output.close();
				return;
			}
			s = input.readLine();
			//tu beda cuda z czasem sie dzialy
			
			s = input.readLine();
			System.out.println(s);
			System.out.println("Candidates are:");
			s = input.readLine();
			System.out.println(s);
			LinkedList<Candidate> tempCand=new LinkedList<Candidate>();
			int size=Integer.parseInt(s);
			for(int i=0; i<size; ++i){
				s=input.readLine();
				System.out.println(s);
				Scanner sca=new Scanner(s);
				tempCand.add(new Candidate(sca.next(),
							sca.next(),
							new Integer(s.replaceAll("\\D+", ""))));
				sca.close();
			}
			LocalServerApp.candidatesBank=new CandidatesBank(tempCand, 4);
			while(true){
				Thread.sleep(100000);
				System.out.println("nieee");
				List<Integer> li=LocalServerApp.candidatesBank.countVotes();
				output.write("VOTES_COUNTED");
				output.flush();
				for(Integer i: li){
					output.write(" "+i);
					output.flush();
				}
				s=input.readLine();
				System.out.println(s);
				//LOOSER?
			}
		} catch (UnknownHostException e) {
		}
		catch(IOException e){
			System.out.println(e+"cl_app_beggining");
		} catch (InterruptedException e) {
			System.out.println(e);
		}
		
	}
}
