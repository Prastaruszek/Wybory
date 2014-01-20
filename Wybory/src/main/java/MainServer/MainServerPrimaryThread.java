package MainServer;

import java.util.Date;

public class MainServerPrimaryThread extends MainServerThread {
	public void run() {
		synchronized (this) {
			try {
				this.wait(MainServerApp.initializationTime);
			}catch(Exception e){}
		}
		while(true){
			synchronized (monitor) {
			
				while(registeredThreads != MainServerApp.numberOfThreads) {
					try {
						monitor.wait();
					}
					catch (InterruptedException e){
					}
				}
			}
			MSCandidate looser = new MSCandidate(null, null, -1);
			looser.nrOfVotes = 1000000000;
			for (MSCandidate c: candidatesBank.getTempCandidatesList())
			{
				if(looser.nrOfVotes > c.nrOfVotes)
					looser = c;
			}
			
			looserIndex = looser.Id;
			candidatesBank.remove(looser);
			MainServerApp.time=new Date().getTime()+MainServerApp.roundTime;
			synchronized(monitor){
				registeredThreads = 0;
				monitor.notifyAll();
			}
		}
	}
}
