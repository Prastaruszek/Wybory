package MainServer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains informations about Canidates
 *
 */
class MSCandidate{
	String forename;
	String name;
	Integer Id;
	Integer nrOfVotes;
	public MSCandidate(String forename, String name, int id){
		this.forename=forename;
		this.name=name;
		this.Id=id;
		nrOfVotes = 0;
	}
	public String toString(){
		return Id+" "+forename+" "+name+"\n";
	}
	public boolean equals(Object o){
		if(o==null|| !(o instanceof MSCandidate)){
			return false;
		}
		return ((MSCandidate)o).Id==this.Id;
	}
}

/**
 * Here the informations about current result and candidates are stored.
 *
 */
public class MainServerCandidatesBank {
	private List<MSCandidate> candidatesList;
	private List<MSCandidate> tempCandidates;
	public MainServerCandidatesBank(LinkedList<MSCandidate> candidatesList){
		this.candidatesList=candidatesList;
		this.tempCandidates=(LinkedList<MSCandidate>) candidatesList.clone();
	}
	
	public List<MSCandidate> getTempCandidatesList(){
		return Collections.unmodifiableList(tempCandidates);
	}
	
	public void remove(MSCandidate c)
	{
		tempCandidates.remove(c);
	}
	
	synchronized void addVotes(int[] arr)
	{
		int i=0;
		for (MSCandidate c : tempCandidates)
			c.nrOfVotes += arr[i++];
	}
}

