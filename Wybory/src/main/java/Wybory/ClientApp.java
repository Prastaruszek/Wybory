package Wybory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class ClientApp 
{
	static class Candidate
	{
		String name;
		boolean exists;
		
		public Candidate(String name, boolean exists)
		{
			this.name = name;
			this.exists = exists;
		}
	}
	static Candidate[] candidates = new Candidate[100];
	
	
	static void timeRemaining(String s)
	{
		Integer time = new Integer(s.replaceFirst("REM_TIME: ", ""));
		System.out.println("Time remaining to send fist part of votes: " + time/60 + " minutes " + time%60 + " seconds.");
	}
	
	public static void main(String args[]){	
	SSLSocket socket = null;
	Scanner sc = new Scanner(System.in);
		try{
	    	System.setProperty("javax.net.ssl.trustStore","mySrvKeystore");
    		System.setProperty("javax.net.ssl.trustStorePassword","123456");
			SSLSocketFactory mySocketFactory=(SSLSocketFactory)SSLSocketFactory.getDefault();
			socket=(SSLSocket)mySocketFactory.createSocket("localhost", 20002);
			//System.out.println(ssl.getEnableSessionCreation()+"ramada");
			//ssl.setEnabledProtocols(new String[]{"SSLv3", "TLSv1"});
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		catch(IOException e){
			System.out.println(e+"cl_app_beggining");
		}
		
		
		try
		{
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
			
			//AUTHENTICATION
			while(true)
			{
				System.out.println("login: ");
				//s = sc.nextLine();
				s = "a";
				System.out.println("password: ");
				//t = sc.nextLine();
				t = "b";
				output.write("LOGIN: " + s + ", PASS: " + t + "\n");
				output.flush();
				s = input.readLine();
				if(s.equals("LOGIN OK"))
					break;
				System.out.println("Bad login or password.\n");
			}
			//\AUTHENTICATION

			
			//SHOW CANDIDATES

				
			s = input.readLine();
			timeRemaining(s);
			
			s = input.readLine();
			System.out.println("Candidates are:");
			s = input.readLine();
			Integer candidatesQuantity = new Integer(s);
			for (int i=1; i<=candidatesQuantity; i++)
			{
				s = input.readLine();
				candidates[i] = new Candidate(s, true);
				System.out.println(s);
			}
			//\SHOW CANDIDATES
			
			
			//VOTING
			vtLoop:
			while(true)
			{
				System.out.println("[While entering votes, send the line ended with 'c' to cancel.]\n"
						+ "[v - vote, t - get remaining time]");
				s = sc.nextLine();
				if(s.equals("t"))
				{
					timeRemaining(s);
					continue;
				}
				else if(!s.equals("v")) continue;
				
				StringBuilder mes = new StringBuilder("VOTE");
				String temp;
				
				typeVotesLoop:
				while(true)
				{
					System.out.println("Type your votes:");
					s = sc.nextLine();
					if(s.matches(".*c") || s.matches(".*C"))
					{
						System.out.println("Cancelled.");
						continue vtLoop;
					}
					if(!s.matches("( *\\d+ *)+"))
					{
						System.out.println("Wrong votes format!");
						continue;
					}
					Pattern pat=Pattern.compile("\\d+");
					Matcher mat=pat.matcher(s);
					int i;
					for(i=0; true ; ++i){
						if(!mat.find()){
							if(i!=0)
								break typeVotesLoop;
							else
							{
								System.out.println("Your list is empty!");
								break;
							}
						}
						temp = mat.group();
						Integer voteNr = new Integer(temp); 
						if(voteNr <= 0 || voteNr > candidatesQuantity)
						{
							System.out.println("There is no candidate with nr " + voteNr + "!");
							continue vtLoop;
						}
						if(!candidates[voteNr].exists)
						{
							System.out.println("Candidate " + candidates[i].name + " has already lost!");
							continue vtLoop;							
						}
						mes.append(" " + temp);
					}
				}	
				
				while(true)
				{
					System.out.println("Votes correct. Proceed? You cannot cancel this operation. [y/n]");
					s = sc.nextLine();
					if(s.equals("y"))
					{
						output.write(mes.toString()+"\n");
						System.out.println(mes.toString());
						output.flush();
						break;
					}
					else if (s.equals("n"))
						break;
				}
			}	
			//\VOTING
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(Exception e){
			System.out.println("no niefajnie");
			e.printStackTrace();
		}
		

	}
}
