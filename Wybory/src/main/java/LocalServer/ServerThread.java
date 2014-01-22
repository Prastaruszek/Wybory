package LocalServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import LoginsAndPasswords.PasswordProtocol;

public class ServerThread implements Runnable {
	SSLSocket socket;
	public ServerThread(SSLSocket socket){
		this.socket=socket;
	}
	Integer myId;
	
	public void run() {
		try{
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter toClient=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//AUTHENTICATION
			String s;
			myId = PasswordProtocol.vertify(inFromClient, toClient, LocalServerApp.loginsPasswordsStore);
			if(myId == -1)
				return;
			System.out.println("to jest to: " + myId);
			/*s=inFromClient.readLine();
			System.out.println(s);
			if(s==null || !s.equals("HELLO")){
				toClient.close();
				inFromClient.close();
				return;
			}
			toClient.write("HELLO. WHO ARE YOU?\n");
			toClient.flush();
			s=inFromClient.readLine();
			if(s==null){
				toClient.close();
				inFromClient.close();
				return;
			}
			if(!s.matches("LOGIN: .+, PASS: .+")){
				toClient.write("BAD LOGIN OR PASS\n");
			}
			String login=s.replaceFirst("LOGIN: ", "").replaceFirst(",.+","");
			String pass=s.replaceFirst(".+, PASS: ", "");
			Integer myId=0;
			System.out.println(login + " " + pass);
			toClient.write("LOGIN OK\n");
			toClient.flush();
			*/
			//\AUTHENTICATION
			//SHOW CANDIDATES
			
			toClient.write("REM_TIME: ");
			toClient.flush();
			toClient.write(LocalServerApp.end_of_turn+"\n");
			toClient.flush();
			System.out.println("czas: "+(LocalServerApp.end_of_turn));
			toClient.write("CANDIDATES ARE:\n");
			toClient.flush();
			toClient.write(new Integer(LocalServerApp.candidatesBank.getTempCandidatesList().size())
					.toString().toCharArray());
			toClient.flush();
			toClient.write("\n");
			toClient.flush();
			for(Candidate c: LocalServerApp.candidatesBank.getTempCandidatesList()){
				toClient.write(c.toString().toCharArray());
				toClient.flush();
			}
			
			//\SHOW CANDIDATES
			//VOTING
			char[] buff=new char[1024];
			while(true){
				int temp_tour=LocalServerApp.curtur;
				s=inFromClient.readLine();
				System.out.println(s);
				if(s==null || !s.matches("VOTE( \\d+)+") && !s.matches("GET LIST")){
					System.out.println("bad hacker\n");
					inFromClient.close();
					toClient.close();
					return;
				}
				if(s.matches("VOTE -1.*")){
					if(LocalServerApp.win){
						toClient.write("VOTE OK -1\n");
						toClient.flush();
						return;
					}
					else{
						continue;
					}
				}
				if(!s.matches("GET LIST")){
						
					List<Integer> votes=new LinkedList<Integer>();
					s=s.replaceFirst("VOTE ", "");
					Pattern pat=Pattern.compile("\\d+");
					Matcher mat=pat.matcher(s);
					mat.find();
					s=mat.group();
					int num=Integer.parseInt(s);
					Integer temp;
					for(int i=0; i<num ; ++i){
						if(!mat.find()){
							/*ktos nas hackuje*/
							inFromClient.close();
							toClient.close();
							return;
						}
						temp=Integer.parseInt(mat.group());
						System.out.println(temp);
						if(temp>=0)
							votes.add(temp);
					}
					if(mat.find()){
						System.out.println("bad protocol");
						inFromClient.close();
						toClient.close();
						return;
					}
					List<Integer> accepted=LocalServerApp.candidatesBank.verifyVotes(votes,myId.intValue());
					toClient.write("VOTE OK " + accepted.size());
					System.out.println("accepting vote");
					toClient.flush();
					for(Integer i: accepted){
						toClient.write(" "+i.toString());
						toClient.flush();
					}
					toClient.write("\n");
					toClient.flush();
					while(!LocalServerApp.win && (temp_tour==LocalServerApp.curtur || !LocalServerApp.candidatesBank.sendList.contains(myId))){
						try{
								synchronized(Integer.class){
									Integer.class.wait();
									Integer.class.notifyAll();
								}
						}
						catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				}
				if(LocalServerApp.win){
					System.out.println("odded");
					toClient.write("SEND LIST 0 ");
					toClient.write(LocalServerApp.winner+"\n");
					toClient.flush();
					return;
				}
				temp_tour++;
				toClient.write("SEND LIST REM_TIME" + LocalServerApp.end_of_turn + " ");
				System.out.println("Sendeing List to:" + myId);
				toClient.flush();
				toClient.write(new Integer((LocalServerApp.toures.get(LocalServerApp.curtur)).size())+" ");
				toClient.flush();
				for(Candidate c: LocalServerApp.toures.get(LocalServerApp.curtur)){
					System.out.println(c);
					toClient.write(c.Id.toString()+" ");
					toClient.flush();
				}
				toClient.write("\n");
				toClient.flush();
			}

			
			//\VOTING
		}
		catch(IOException e){
			System.out.println(e);
		}
		System.out.println("petelicki");
	}
		
}
