package LocalServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.security.Security;

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
			
			String s;
			s=inFromClient.readLine();
			System.out.println(s);
			if(!s.equals("HELLO")){
				toClient.close();
				inFromClient.close();
				return;
			}
			toClient.write("HELLO. WHO ARE YOU?\n");
			toClient.flush();
			s=inFromClient.readLine();
			if(!s.matches("LOGIN: .+, PASS: .+")){
				toClient.write("BAD LOGIN OR PASS");
			}
			String login=s.replaceFirst("LOGIN: ", "").replaceFirst(",.+","");
			String pass=s.replaceFirst(".+, PASS: ", "");
			System.out.println(login + " " + pass);
			toClient.write("LOGIN OK\n");
			toClient.flush();
			toClient.write("REM_TIME: 10\n");
			toClient.flush();
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
			s=inFromClient.readLine();
			System.out.println(s);
			if(!s.matches("VOTE( \\d+)+")){
				System.out.println("bad");
				inFromClient.close();
				toClient.close();
				return;
			}
			s=s.replaceFirst("VOTE ", "");
			Integer votesNum=Integer.parseInt(s.replaceAll(" \\d+", ""));
			System.out.println(votesNum);
			inFromClient.close();
			toClient.close();
		}
		catch(IOException e){
			e.printStackTrace();;
		}
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