package LocalServer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


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
	public synchronized void addVotes(int[] arr)
	{
		int i=0;
		for (Candidate c : tempCandidates)
		{
			c.nrOfVotes += arr[i++];
		}
	}
}
