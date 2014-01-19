package LocalServer;

public class Candidate{
	String forename;
	String name;
	Integer Id;
	Integer nrOfVotes;
	public Candidate(String forename, String name, int id){
		this.forename=forename;
		this.name=name;
		this.Id=id;
		nrOfVotes = 0;
	}
	public String toString(){
		return Id+". "+forename+" "+name+"\n";
	}
}
