package MainServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;


public class MainServerCommunicationThread extends MainServerThread{
	
	SSLSocket socket;
	public MainServerCommunicationThread(SSLSocket socket){
		this.socket=socket;
	}
	private void write_time(BufferedWriter output) throws IOException{
		output.write("REM_TIME: ");
		output.flush();
		Long time=new Date().getTime()+40000;
		output.write(time.toString()+"\n");
		output.flush();
	}
	public void run() {
		try{
			BufferedReader input=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter output=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//AUTHENTICATION
			String s;
			s=input.readLine();
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
			output.write("LOGIN OK\n");
			output.flush();
			
			//\AUTHENTICATION
			//SHOW CANDIDATES
			

			write_time(output);
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
			while(true)
			{
				s = input.readLine();
				System.out.println(s);
				s.replaceFirst("VOTES_COUNTED ", "");
				Pattern pat=Pattern.compile("\\d+");
				Matcher mat=pat.matcher(s);
				
				int arr[] = new int[candidatesBank.getTempCandidatesList().size()];
				int i=0;
				while(mat.find())
					arr[i++] = new Integer(mat.group());
				candidatesBank.addVotes(arr);
				System.out.println("tu");
				synchronized(monitor)
				{
					registeredThreads++;
					if(registeredThreads == MainServerApp.numberOfThreads)
						monitor.notifyAll();
					//while(monitor != MainServerApp.numberOfThreads || looserIndex == lastlooser)
					while(registeredThreads!=0)
					{
						try {
							monitor.wait();
						}
						catch(InterruptedException e)
						{}
					}
				}
				System.out.println("tu");
				if(candidatesBank.getTempCandidatesList().size()==1)
				{
					output.write("WINNER " + candidatesBank.getTempCandidatesList().get(0).Id + "\n");
					output.flush();
					break;
				}
				output.write("LOOSER " + looserIndex + "\n");
				output.flush();
				write_time(output);
			}
		}
		catch(IOException e){
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
