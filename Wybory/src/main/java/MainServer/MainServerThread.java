package MainServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import javax.net.ssl.SSLSocket;

import LocalServer.Candidate;
import LocalServer.CandidatesBank;

public abstract class MainServerThread implements Runnable {
	static Object monitor;
	public static CandidatesBank candidatesBank;
	public static void loadCandidates(){
		LinkedList<Candidate> cl=new LinkedList<Candidate>();
		cl.add(new Candidate("Piotr", "Kawałek",1));
		cl.add(new Candidate("Krzysztof", "Kleiner",2));
		cl.add(new Candidate("Edward", "Szczypka",3));
		candidatesBank=new CandidatesBank(cl);
	}
	
	
}

class MainServerLSThread extends MainServerThread {
	
	SSLSocket socket;
	public MainServerLSThread(SSLSocket socket){
		this.socket=socket;
	}
	public void run() {
		try{
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter toClient=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch(IOException e){
			e.printStackTrace();;
		}
	}
}