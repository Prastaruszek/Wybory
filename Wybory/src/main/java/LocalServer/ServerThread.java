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
			inFromClient.close();
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