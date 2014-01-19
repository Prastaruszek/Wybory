package MainServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;


public class MainServerCommunicationThread extends MainServerThread{
	
	SSLSocket socket;
	public MainServerCommunicationThread(SSLSocket socket){
		this.socket=socket;
	}
	public void run() {
		try{
			BufferedReader input=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter output=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//AUTHENTICATION
			String s;
			s=input.readLine();
			System.out.println(s);
			if(s==null || !s.equals("HELLO")){
				output.close();
				input.close();
				return;
			}
			output.write("HELLO. WHO ARE YOU?\n");
			output.flush();
			s=input.readLine();
			if(s==null){
				output.close();
				input.close();
				return;
			}
			if(!s.matches("LOGIN: .+, PASS: .+")){
				output.write("BAD LOGIN OR PASS\n");
			}
			String login=s.replaceFirst("LOGIN: ", "").replaceFirst(",.+","");
			String pass=s.replaceFirst(".+, PASS: ", "");
			System.out.println(login + " " + pass);
			output.write("LOGIN OK\n");
			output.flush();
			
			//\AUTHENTICATION
			//SHOW CANDIDATES
			
			output.write("REM_TIME: 10\n");
			output.flush();
			output.write("CANDIDATES ARE:\n");
			output.flush();
			output.write(new Integer(candidatesBank.getTempCandidatesList().size())
					.toString().toCharArray());
			output.flush();
			output.write("\n");
			output.flush();
			for(MSCandidate c: candidatesBank.getTempCandidatesList()){
				output.write(c.toString().toCharArray());
				output.flush();
			}
			//\SHOW CANDIDATES
			//VOTING
			int looser = -1, lastlooser = -1;
			
			while(true)
			{
				s = input.readLine();
				s.replaceFirst("VOTES_COUNTED ", "");
				Pattern pat=Pattern.compile("\\d+");
				Matcher mat=pat.matcher(s);
				
				int arr[] = new int[candidatesBank.getTempCandidatesList().size()];
				int i=0;
				while(mat.find())
					arr[i++] = new Integer(mat.group());
				candidatesBank.addVotes(arr);
				
				synchronized(monitor)
				{
					monitor++;
					if(monitor == MainServerApp.numberOfThreads)
						monitor.notifyAll();
					while(looser == lastlooser)
					{
						try {
							monitor.wait();
						}
						catch(InterruptedException e)
						{}
					}
				}
				
				if(candidatesBank.getTempCandidatesList().size()==1)
				{
					output.write("WINNER " + candidatesBank.getTempCandidatesList().get(0));
					break;
				}
				output.write("LOOSER 2\n");
				output.flush();
			}
		}
		catch(IOException e){
			e.printStackTrace();;
		}
	}
}
