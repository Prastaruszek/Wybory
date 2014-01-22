package MainServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;

import LoginsAndPasswords.LoginsPasswordsStore;
import LoginsAndPasswords.PasswordEncryption;
import LoginsAndPasswords.PasswordProtocol;

/**
 * Thread used to communicate with LocalServers: authorizing, sending the remaining time receiving and
 * sending results
 *
 */
public class MainServerCommunicationThread extends MainServerThreadAbstractClass{
	
	SSLSocket socket;
	Integer userId;
		
	public MainServerCommunicationThread(SSLSocket socket){
		this.socket=socket;
	}
	private void write_time(BufferedWriter output) throws IOException{
		output.write("REM_TIME: ");
		output.flush();
		output.write(MainServerApp.time+"\n");
		output.flush();
	}
	public void run() {
		try{
			BufferedReader input=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter output=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//AUTHENTICATION
			String s;
			userId = PasswordProtocol.vertify(input, output, loginsPasswordsStore);
			if(userId == -1)
				return;				
			
			
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
				//System.out.println(s);
				s=s.replaceFirst("VOTES_COUNTED ", "");
				//System.out.println(s);
				Pattern pat=Pattern.compile("\\d+");
				Matcher mat=pat.matcher(s);
				
				int arr[] = new int[candidatesBank.getTempCandidatesList().size()];
				int i=0;
				while(mat.find())
					arr[i++] = new Integer(mat.group());
				candidatesBank.addVotes(arr);
				synchronized(monitor)
				{
					registeredThreads++;
					if(registeredThreads == MainServerApp.numberOfThreads){
						monitor.notifyAll();
					}
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
			try{
				Thread.sleep(10000);
			}catch(InterruptedException e){
				
			}
			System.exit(0);
		}
		catch(IOException e){
			//e.printStackTrace();
			//System.out.println(e);
		}
	}
}
