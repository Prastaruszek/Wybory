package LocalServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MSComunicationThread implements Runnable {
	
	private void read_time(BufferedReader input) throws IOException{
		String s = input.readLine();
		long time=Long.parseLong(s.replaceFirst("REM_TIME: ", ""));
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
			read_time(input);
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
				Thread.sleep(LocalServerApp.end_of_turn-new Date().getTime());
				List<Integer> li=LocalServerApp.candidatesBank.countVotes();
				System.out.println("counted");
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
				System.out.println(j);
				s=input.readLine();
				System.out.println(s);
				//LOOSER?
				read_time(input);
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
