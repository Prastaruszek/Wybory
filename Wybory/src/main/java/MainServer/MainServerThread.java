package MainServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import javax.net.ssl.SSLSocket;


public abstract class MainServerThread implements Runnable {
	static Integer monitor;
	public static MSCandidatesBank candidatesBank;
	public static void loadCandidates(){
		LinkedList<MSCandidate> cl=new LinkedList<MSCandidate>();
		cl.add(new MSCandidate("Piotr", "Kawałek",1));
		cl.add(new MSCandidate("Krzysztof", "Kleiner",2));
		cl.add(new MSCandidate("Edward", "Szczypka",3));
		candidatesBank=new MSCandidatesBank(cl);
	}
	
	
}
