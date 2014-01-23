package MainServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import javax.net.ssl.SSLSocket;

import LoginsAndPasswords.LoginsPasswordsStore;


public abstract class MainServerThreadAbstractClass implements Runnable {
	static Object monitor = new Object();
	static Integer registeredThreads = 0;
	static Integer looserIndex = -1;
	public static MainServerCandidatesBank candidatesBank;
	public static void loadCandidates(){
		LinkedList<MSCandidate> cl=new LinkedList<MSCandidate>();
		cl.add(new MSCandidate("Piotr", "Kawałek",1));
		cl.add(new MSCandidate("Krzysztof", "Kleiner",2));
		cl.add(new MSCandidate("Mateusz", "Twarog",3));
		cl.add(new MSCandidate("Paweł", "Rokita",4));
		cl.add(new MSCandidate("Maria", "Grob", 5));
		cl.add(new MSCandidate("Zając", "Zając", 6));
		cl.add(new MSCandidate("Król", "Korwin",7));
		candidatesBank=new MainServerCandidatesBank(cl);
	}
	
	
	static LoginsPasswordsStore loginsPasswordsStore = new LoginsPasswordsStore("ls", "p");
	
	
}
