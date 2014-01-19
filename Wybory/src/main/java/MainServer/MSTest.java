package MainServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MSTest {
	public static void main(String args[]){	
	SSLSocket socket = null;
	Scanner sc = new Scanner(System.in);
		try{
	    	System.setProperty("javax.net.ssl.trustStore","mySrvKeystore");
    		System.setProperty("javax.net.ssl.trustStorePassword","123456");
			SSLSocketFactory mySocketFactory=(SSLSocketFactory)SSLSocketFactory.getDefault();
			socket=(SSLSocket)mySocketFactory.createSocket("localhost", 20001);
			//System.out.println(ssl.getEnableSessionCreation()+"ramada");
			//ssl.setEnabledProtocols(new String[]{"SSLv3", "TLSv1"});
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		catch(IOException e){
			System.out.println(e+"cl_app_beggining");
		}
		
		
		try
		{
			BufferedReader input=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter output=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			output.write("HELLO\n");
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

			s = input.readLine();
			System.out.println(s);
			s = input.readLine();
			System.out.println(s);
			s = input.readLine();
			System.out.println(s);
			s = input.readLine();
			System.out.println(s);
			s = input.readLine();
			System.out.println(s);
			s = input.readLine();
			System.out.println(s);
			output.write("VOTES_COUNTED 1 2 3\n");
			output.flush();
			s = input.readLine();
			System.out.println(s);
			output.write("VOTES_COUNTED 10 3\n");
			output.flush();
			s = input.readLine();
			System.out.println(s);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
