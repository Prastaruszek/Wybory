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
import LoginsAndPasswords.PasswordEncryptionService;

public class MainServerCommunicationThread extends MainServerThread{
	
	SSLSocket socket;
	int userId;
		
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
			s=input.readLine();
			if(s==null || !s.equals("HELLO")){
				output.close();
				input.close();
				return;
			}
			output.write("HELLO. WHO ARE YOU?\n");
			output.flush();
			while(true)
			{
				s=input.readLine();
				if(s==null){
					output.close();
					input.close();
					return;
				}

				/*if(!s.matches("LOGIN: .+, PASS: .+")){
					output.write("PROTOCOL ERROR\n");
				}
				String login=s.replaceFirst("LOGIN: ", "").replaceFirst(",.+","");
				*/
				if(!s.matches("LOGIN: .+")){
					output.write("PROTOCOL ERROR\n");
				}
				String login=s.replaceFirst("LOGIN: ", "");
			
				for (int i=1; i<=loginsPasswordsStore.numberOfUsers; i++)
				{
					if(login.equals(loginsPasswordsStore.logins[i]))
					{
						userId = i;
						break;
					}
				}
				for (int j=0; j<8; j++)
					output.write(new Byte(loginsPasswordsStore.salts[userId][j]).toString() + " ");
				output.write("\n");
				output.flush();	
				
				s = input.readLine();
				String pass=s.replaceFirst("PASS: ", "");
				Pattern pat = Pattern.compile("-?\\d+");
				Matcher mat = pat.matcher(pass); 
				System.out.println(mat.groupCount());
				System.out.println(pass);
				byte[] encPass = new byte[1000];
				int j;
				for (j=0; ; j++)
				{
					if(!mat.find())
						break;
					encPass[j] = new Byte(mat.group());
				}
				byte[] encPassCutToLength = new byte[j];
				for (int k=0; k<j; k++)
					encPassCutToLength[k] = encPass[k];
				
				if(PasswordEncryptionService.authenticate(encPassCutToLength, loginsPasswordsStore.encryptedPasswords[userId],
						loginsPasswordsStore.salts[userId]))
				{
					output.write("LOGIN OK\n");
					output.flush();
					System.out.println("LOGIN OK!!");
					break;
				}
				System.out.println("WRONG");
				output.write("WRONG PASSWORD\n");
				output.flush();
				
			}
			
			System.out.println("em here");
			
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
