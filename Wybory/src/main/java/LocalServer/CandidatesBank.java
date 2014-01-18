package LocalServer;

import java.util.Collections;
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
}

public class CandidatesBank {
	private LinkedList<Candidate> candidatesList;
	private LinkedList<Candidate> tempCandidates;
	public CandidatesBank(LinkedList<Candidate> candidatesList){
		this.candidatesList=candidatesList;
		this.tempCandidates=(LinkedList<Candidate>) candidatesList.clone();
	}
	public List<Candidate> getTempCandidatesList(){
		return Collections.unmodifiableList(tempCandidates);
	}
	public boolean verifyVotes(List<Integer> votes){
		
		return true;
	}
}
