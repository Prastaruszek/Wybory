package LocalServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
	private boolean[] active;
	public CandidatesBank(LinkedList<Candidate> candidatesList, int n){
		this.candidatesList=candidatesList;
		this.tempCandidates=(LinkedList<Candidate>) candidatesList.clone();
		votes=new ArrayList<List<Integer>>(n+1);
		for(int i=0; i<=n; ++i){
			votes.add(new LinkedList<Integer>());
		}
		active=new boolean[n+1];
		for(int i=0; i<=n; ++i){
			active[i]=true;
		}
	}
	public List<Candidate> getTempCandidatesList(){
		return Collections.unmodifiableList(tempCandidates);
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
			votes.get(user_id).add(i);
			accepted.add(i);
		}
		return accepted;
	}
}
