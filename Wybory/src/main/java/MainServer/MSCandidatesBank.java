package MainServer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class MSCandidate{
	String forename;
	String name;
	Integer Id;
	Integer nrOfVotes;
	public MSCandidate(String forename, String name, int id){
		this.forename=forename;
		this.name=name;
		this.Id=id;
	}
	public String toString(){
		return Id+". "+forename+" "+name+"\n";
	}
	public boolean equals(Object o){
		if(o==null|| !(o instanceof MSCandidate)){
			return false;
		}
		return ((MSCandidate)o).Id==this.Id;
	}
}


public class MSCandidatesBank {
	private List<MSCandidate> candidatesList;
	private List<MSCandidate> tempCandidates;
	public MSCandidatesBank(LinkedList<MSCandidate> candidatesList){
		this.candidatesList=candidatesList;
		this.tempCandidates=(LinkedList<MSCandidate>) candidatesList.clone();
	}
	
	public List<MSCandidate> getTempCandidatesList(){
		return Collections.unmodifiableList(tempCandidates);
	}
	
	synchronized void addVotes(int[] arr)
	{
		int i=0;
		for (MSCandidate c : tempCandidates)
			c.nrOfVotes += arr[i++];
	}
}

