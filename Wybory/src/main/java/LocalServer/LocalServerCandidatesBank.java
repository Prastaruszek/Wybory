package LocalServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * Constains informations about the candidate
 * 
 * 
 *
 * 
 */
class Candidate{
	String forename;
	String name;
	Integer Id;
	
	/**
	 * Constructs candidate with the given forename, name and id
	 * @param forename
	 * @param name
	 * @param id
	 */
	
	public Candidate(String forename, String name, int id){
		this.forename=forename;
		this.name=name;
		this.Id=id;
	}
	
	
	/**
	 * @return String representation of the given candidate in format [Id] [forename] [name]
	 */
	public String toString(){
		return Id+". "+forename+" "+name+"\n";
	}
	/**
	 * 
	 * @return true if and only if the given object is an candidate with
	 * the same id
	 */
	public boolean equals(Object o){
		if(o==null|| !(o instanceof Candidate)){
			return false;
		}
		return ((Candidate)o).Id==this.Id;
	}
}

/**
 * Constains informations about the elector
 *
 */
class Client{
	String nick;
	int id;
}
/**
 * Class responsible for synchronising and managing incoming votes from electors and
 * incoming results from server. It also count votes, here are all the statistics from 
 * the local area
 * @author piotr
 *
 */
public class LocalServerCandidatesBank {
	private List<Candidate> candidatesList;
	private List<Candidate> tempCandidates;
	private ArrayList<List<Integer>> votes;
	boolean canSendImmediatly;
	private ReentrantReadWriteLock daLock = new ReentrantReadWriteLock();
	public List<Integer> sendList=new LinkedList<Integer>();
	private boolean write=true;
	private boolean[] active;
	private int numberOfVoters;
	/**
	 * Constructs the CandidatesBank with the given candidatesList, who will start
	 * in ellections and the given electors number from this local area
	 * @param candidatesList
	 * @param n
	 */
	public LocalServerCandidatesBank(LinkedList<Candidate> candidatesList, int n){
		this.candidatesList=candidatesList;
		tempCandidates=new LinkedList<Candidate>();
		LocalServerApp.toures.add(new ArrayList());
		List<Candidate> t=LocalServerApp.toures.get(0);
		for(Candidate c: candidatesList){
			t.add(c);
			tempCandidates.add(c);
		}
		votes=new ArrayList<List<Integer>>(n+1);
		for(int i=0; i<=n; ++i){
			votes.add(new LinkedList<Integer>());
		}
		active=new boolean[n+1];
		for(int i=0; i<=n; ++i){
			active[i]=true;
		}
		this.numberOfVoters=n;
	}
	/**
	 * Returns view on candidatesList that can not be modyfied
	 * @return
	 */
	public List<Candidate> getTempCandidatesList(){
		return Collections.unmodifiableList(tempCandidates);
	}
	/**
	 * The function used to inform the candidatesBank about the candidate
	 * who lost the previous round. It also decides wheather the next round should
	 * be conducted immediatly or not
	 * @param j Id of the candidate, who has lost the previous round
	 */
	public void loses(Integer j){
		List<Integer> sendList=new LinkedList<Integer>();
		tempCandidates.remove(new Candidate("","",j));
		canSendImmediatly=true;
		for(int i=0; i<numberOfVoters; ++i){
			List<Integer> li=votes.get(i);
			boolean empty=(li.size()==0);
			//System.out.println("hereee");
			//System.out.println("tutaj");
					
			while(li.size()>0 && !tempCandidates.contains(new Candidate("","",li.get(0)))){
				li.remove(0);
			}
			if(li.size()==0 && !empty){
				canSendImmediatly=false;
				sendList.add(i);
			}
		}
		daLock.writeLock().unlock();
		this.sendList=sendList;
		synchronized(Integer.class){
			Integer.class.notifyAll();
		}
	}
	/**
	 * Function counting votes after round have ended
	 * @return List of Integers representing the current situation. Results of Candidates who have not lost yet
	 * in order consistent with Id's order .
	 */
	public List<Integer> countVotes(){
		List<Integer> result=new LinkedList();
		int[] count=new int[candidatesList.size()+1];
		write=false;
		daLock.writeLock().lock();
		for(List<Integer> v: votes){
			if(v.size()>0){
				count[v.get(0)]+=1;
			}
		}
		for(Candidate c: tempCandidates){
			result.add(count[c.Id]);
		}
		write=true;
		//waskie gardlo do wymiany
		synchronized(this){
			notifyAll();
		}
		return result;
	}
	/**
	 * The function used to commit single client votes and save it in Candidates Bank.
	 * @param u_votes
	 * @param user_id
	 * @return The List of votes (candidates Id's), which where accepted according to the current situation.
	 */
	public List<Integer> verifyVotes(List<Integer> u_votes, int user_id){
		List<Integer> accepted=new LinkedList<Integer>();
		Iterator<Integer> it=u_votes.listIterator();
		while(it.hasNext()){
			Integer i=it.next();
			if(!tempCandidates.contains(new Candidate("","",i))){
				continue;
			}
			if(votes.get(user_id).contains(i)){
				continue;
			}
			try{
				
				while(!write)
					wait();
				daLock.readLock().lock();;
					votes.get(user_id).add(i);
					accepted.add(i);
				daLock.readLock().unlock();
				synchronized(this){
					notifyAll();
				}
			}
			catch(InterruptedException e){
			}
			finally{
			}
		}
		return accepted;
	}
}
