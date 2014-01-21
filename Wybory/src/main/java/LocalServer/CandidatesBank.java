package LocalServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Candidate{
	String forename;
	String name;
	Integer Id;
	public Candidate(String forename, String name, int id){
		this.forename=forename;
		this.name=name;
		this.Id=id;
	}
	public String toString(){
		return Id+". "+forename+" "+name+"\n";
	}
	public boolean equals(Object o){
		if(o==null|| !(o instanceof Candidate)){
			return false;
		}
		return ((Candidate)o).Id==this.Id;
	}
}

class Client{
	String nick;
	int id;
}

public class CandidatesBank {
	private List<Candidate> candidatesList;
	private List<Candidate> tempCandidates;
	private ArrayList<List<Integer>> votes;
	private ReentrantReadWriteLock daLock = new ReentrantReadWriteLock();
	public List<Integer> sendList=new LinkedList<Integer>();
	private boolean write=true;
	private boolean[] active;
	private int n;
	public CandidatesBank(LinkedList<Candidate> candidatesList, int n){
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
		n=this.n;
	}
	public List<Candidate> getTempCandidatesList(){
		return Collections.unmodifiableList(tempCandidates);
	}
	/* to trzeba zmienić*/
	public void loses(Integer j){
		List<Integer> sendList=new LinkedList<Integer>();
		tempCandidates.remove(new Candidate("","",j));
		for(int i=0; i<n; ++i){
			List<Integer> li=votes.get(i);
			boolean empty=(li.size()==0);
			while(li.size()>0 && !tempCandidates.contains(new Candidate("","",li.get(0)))){
				li.remove(0);
			}
			if(li.size()==0 && !empty){
				sendList.add(i);
			}
		}
		daLock.writeLock().unlock();
		this.sendList=sendList;
		synchronized(Integer.class){
			Integer.class.notifyAll();
		}
	}
	public List<Integer> countVotes(){
		List<Integer> result=new LinkedList();
		int[] count=new int[candidatesList.size()+1];
		write=false;
		daLock.writeLock().lock();
		for(List<Integer> v: votes){
			while(v.size()>0 && !tempCandidates.contains(new Candidate("","",v.get(0))))
				v.remove(0);
			if(v.size()>0){
				System.out.println("hereee");
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
				//TU JEST WASKIE GARDLO TRZEBA TO ZMIENIC
				synchronized(this){
					notifyAll();
				}
			}
			catch(InterruptedException e){
				System.out.println(e);
			}
			finally{
			}
		}
		return accepted;
	}
}
