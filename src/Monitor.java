import java.util.concurrent.locks.*;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
enum State { THINKING, HUNGRY, EATING }

public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	private final Lock lock = new ReentrantLock();
	int total;
	State[] status;
	Condition[] self;
	boolean avail;	

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		total = piNumberOfPhilosophers;
		status = new State[total];
		self = new Condition[total];
		avail = true;
		
		for (int i = 0; i < total; i++) {
			status[i] = State.THINKING;
			self[i] = lock.newCondition();
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		// ...
		status[piTID] = State.HUNGRY;
        test(piTID);
        try
		{
	        if (status[piTID] != State.EATING)
	            wait();
		}
        catch(InterruptedException e)
		{
			System.err.println("Monitor Error");
			System.exit(1);
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		// ...
		//System.err.println("putDown " + status[piTID]);
		status[piTID] = State.THINKING;        
        test((piTID + 1) % total);
        test((piTID + total-1) % total);
	}
	
	public synchronized void test(int i) {	
        if ((status[(i + total-1) % total] != State.EATING) &&
        (status[i] == State.HUNGRY) &&
        (status[(i + 1) % total] != State.EATING)) {
        	status[i] = State.EATING;
        	//System.err.println(i + " Philosopher " + status[i]);
            notifyAll();
        }
    }

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk()
	{
		// ...
		if (!avail)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		avail = false;
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk()
	{
		// ...
		avail = true;;
		notifyAll();
	}
}

// EOF
