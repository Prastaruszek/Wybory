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

public class ServerThread implements Runnable {
	SSLSocket socket;
	public ServerThread(SSLSocket socket){
		this.socket=socket;
	}
	public void run() {
		try{
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter toClient=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//AUTHENTICATION
			String s;
			s=inFromClient.readLine();
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
				if(s==null || !s.matches("VOTE( \\d+)+")){
					System.out.println("bad");
					inFromClient.close();
					toClient.close();
					return;
				}
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
				/*TUTAJ MUSIMY ZMIENIC, PRZY WERYFIKACJI NAZWISK, ŻEBY BYŁO ZAMIAST 0 user_id */
				List<Integer> accepted=LocalServerApp.candidatesBank.verifyVotes(votes,0);
				toClient.write("VOTE OK REM_TIME 3 "+accepted.size());
				System.out.println("accepting vote");
				toClient.flush();
				for(Integer i: accepted){
					toClient.write(" "+i.toString());
					toClient.flush();
				}
				toClient.write("\n");
				toClient.flush();
				while(temp_tour==LocalServerApp.curtur || !LocalServerApp.candidatesBank.sendList.contains(myId)){
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
				temp_tour++;
				toClient.write("SEND LIST ");
				System.out.println("Sendeing List to:" + myId);
				toClient.flush();
				toClient.write(new Integer(LocalServerApp.toures.get(temp_tour).size())+" ");
				toClient.flush();
				for(Candidate c: LocalServerApp.toures.get(temp_tour)){
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
