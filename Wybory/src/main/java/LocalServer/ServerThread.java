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
			System.out.println("tu");
			
			String s;
			while ((s = inFromClient.readLine()) != null) {
                System.out.println(s);
                System.out.flush();
                if(s.equals("dobrze")){
					System.out.println("jest dobrze");
				}
				else{
					System.out.println("nie jest dobrze");
				}
            }
			
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
			os.write("dobrze\n");
			os.flush();
			os.write("cus\n");
			os.flush();
			os.write("trolololo");
			os.flush();
			os.write("dobrze\n");
			os.flush();
			System.out.println("koniec");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		
	}
}