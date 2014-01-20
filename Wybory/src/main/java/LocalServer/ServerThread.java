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
		
			while(true){
				
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
				toClient.flush();
				for(Integer i: accepted){
					toClient.write(" "+i.toString());
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
	
	
	public static void main(String args[]){
		
		try {
        	System.setProperty("javax.net.ssl.trustStore","mySrvKeystore");
    		System.setProperty("javax.net.ssl.trustStorePassword","123456");
			SSLSocketFactory sf=(SSLSocketFactory)SSLSocketFactory.getDefault();
			SSLSocket ssl=(SSLSocket)sf.createSocket("localhost", 20002);
			//System.out.println(ssl.getEnableSessionCreation()+"ramada");
			//ssl.setEnabledProtocols(new String[]{"SSLv3", "TLSv1"});
			BufferedWriter os=new BufferedWriter(new OutputStreamWriter(ssl.getOutputStream()));
			//System.out.println(ssl.getEnableSessionCreation()+"rama");
			os.write("HELLO\n");
			os.flush();
			os.write("LOGIN: Prastaruszek, PASS: joljol\n");
			os.flush();
			os.write("VOTE 3 1 2 3\n");
			os.flush();
			BufferedReader br=new BufferedReader(new InputStreamReader(ssl.getInputStream()));
			String s;
			while((s=br.readLine())!=null){
				System.out.println(s);
			}
			os.close();
			br.close();
			//System.out.println("koniec");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		
	}
}
