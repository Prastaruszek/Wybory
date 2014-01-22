package Wybory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.ObjectInputStream.GetField;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import LoginsAndPasswords.PasswordProtocol;


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
	static Integer candidatesQuantity;
	static Long end_of_turn;
	
	static void setAndWriteTimeRemaining(String s)
	{
		Long time = new Long(s)-new Date().getTime();
		end_of_turn = new Long(s);
		time/=1000;
		System.out.println("Time remaining to send fist part of votes: " + time/60 + " minutes " + time%60 + " seconds.");
	}
	static void writeTimeRemaining()
	{
		Long time = end_of_turn - new Date().getTime();
		System.out.println("Time remaining to send fist part of votes: " + time/60 + " minutes " + time%60 + " seconds.");
	}

	
	public static void main(String args[]){	
		SSLSocket socket = null;
		Scanner sc = new Scanner(System.in);
		try{
	    	System.setProperty("javax.net.ssl.trustStore","LsKeystore");
    		System.setProperty("javax.net.ssl.trustStorePassword","admin12");
			Integer portNr = 30001;
			System.out.println("Choose local server you want to connect to, entering its port. \n"
					+ "Press ENTER to set default port [30001].");
			String s = sc.nextLine();
			if(!s.equals(""))
				portNr = new Integer(s);
			SSLSocketFactory mySocketFactory=(SSLSocketFactory)SSLSocketFactory.getDefault();
			socket=(SSLSocket)mySocketFactory.createSocket("localhost", portNr);
			//System.out.println(ssl.getEnableSessionCreation()+"ramada");
			//ssl.setEnabledProtocols(new String[]{"SSLv3", "TLSv1"});
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		
		try
		{
			BufferedReader input=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter output=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			String s;
			if(!PasswordProtocol.attempt(input, output, sc))
				return;
			/*output.write("HELLO\n");
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
			*/
			
			//SHOW CANDIDATES

				
			s = input.readLine();
			s = s.replaceFirst("REM_TIME ", "");
			setAndWriteTimeRemaining(s);
			
			s = input.readLine();
			System.out.println("Candidates are:");
			s = input.readLine();
			candidatesQuantity = new Integer(s);
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
				if(end_of_turn - new Date().getTime() <= 0)
				{
					output.write("VOTE -1\n");
					output.flush();
					s = input.readLine();
					receiveList(s, true);
				}
				System.out.println("[While entering votes, send the line ended with 'c' to cancel.]\n"
						+ "[v - vote, t - get remaining time]");
				s = sc.nextLine();
				if(s.equals("t"))
				{
					writeTimeRemaining();
					continue;
				}
				else if(!s.equals("v")) continue;
				
				StringBuilder mes = new StringBuilder();
				String temp;
				int howManyVotes;
				
				typeVotesLoop:
				while(true)
				{
					if(end_of_turn - new Date().getTime() <= 0)
					{
						output.write("VOTE -1");
						output.flush();
						s = input.readLine();
						receiveList(s, true);
					}

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
					
					for(howManyVotes =0; true ; ++howManyVotes){
						if(!mat.find()){
							if(howManyVotes!=0)
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
							System.out.println("Candidate " + candidates[voteNr].name + " has already lost!");
							continue vtLoop;							
						}
						mes.append(" " + temp);
					}
				}	
				
				while(true)
				{
					System.out.println("Votes format correct. Proceed? You cannot cancel this operation. [y/n]");
					s = sc.nextLine();
					if(s.equals("y"))
					{
						output.write("VOTE " + howManyVotes + mes.toString()+"\n");
						output.flush();
						break;
					}
					else if (s.equals("n"))
						break;
				}
				
				for (int i=0; i<2; i++)
				{

					s = input.readLine();
					if(s.startsWith("VOTE OK "))
					{
						s = s.replaceFirst("VOTE OK ", "");
						
						Pattern pat=Pattern.compile("\\d+");
						Matcher mat=pat.matcher(s);
						
						mat.find();
						int howManyVotesAccepted = new Integer(mat.group());
						if(howManyVotesAccepted == howManyVotes)
						{
							System.out.println("Votes accepted. Await next monition.");
						}
						else if(howManyVotesAccepted == 0)
						{
							System.out.println("None of your votes were valid!");
						}
						else
						{
							System.out.println("Some of your votes were invalid, but these were accepted:");
							for(int j=0; j<howManyVotesAccepted ; ++j)
							{
								if(!mat.find())
									break;
								else
									System.out.print(mat.group()+ " ");
							}
							System.out.print("\n");
						}
					}
					else //SEND_LIST
					{
						receiveList(s, false);
					}
				}	
			//\VOTING
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

/*******************************************************************/

	
	static void receiveList(String s, boolean afterEmptyVoting)
	{
		s = s.replaceFirst("SEND LIST ", "");
		if(s.matches("1 .*")){
			s = s.replaceFirst("1 ", "");
			Integer candNr = new Integer(s);
			System.out.println("Candidate " + candidates[candNr].name + " has won. Voting ended.");
			try {
				//TODO
				Thread.sleep(100000000);
			} catch (InterruptedException e){
				
			}
		}
		
		s = s.replaceFirst("REM_TIME ", "");
		Pattern pat=Pattern.compile("\\d+");
		Matcher mat=pat.matcher(s);
		mat.find();
		setAndWriteTimeRemaining(mat.group());
		mat.find();
		int candidatesPresentQuantity = new Integer(mat.group());
		if(afterEmptyVoting)
			System.out.println("This round has ended! Remaining candidates are:");
		else
			System.out.println("All candidates you voted on have lost. Remaining candidates are:");
		for(int j=1; j<=candidatesQuantity; j++)
		{
			candidates[j].exists = false;
		}
		for(int j=0; j<candidatesPresentQuantity ; ++j)
		{
			mat.find();
			Integer candNr = new Integer(mat.group());
			candidates[candNr].exists = true;
			System.out.println(candidates[candNr].name);
		}
	}
}