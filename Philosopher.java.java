import java.util.*; //imported for Random class

public class Philosopher extends Thread {

    //Each Philosopher will hold a reference to the (single)
    // DiningPhilosophers table in the System.
    private DiningPhilosophers table;

    //Each Philosopher is assigned a unique ID (between 0 and 4) by the
    // DPManager via the Philosoper's constructor!
    private int philID;

    //Max Sleep time is 500 msecs.
    private static final int MAX_SLEEP = 500;

    //Each Philosopher is Philosophising 5 rounds
    private static final int MAX_PHILOSOPHY_ROUNDS = 5;

    //Random number generator class to randomize sleeping time before
    // each Philosopher's action. You have example of using this in previous Assignments!
    private Random rand;

    //The constructor gets the reference to the (single) DiningPhilosophers
    //table in the System and the Philosopher's unique ID in the system
    //and initialises local variables accordingly. It also initialises the
    // Random Number Generator.

    public Philosopher(DiningPhilosophers table,int philID) {
        this.table = table;
        this.philID = philID;
        rand = new Random();
    }
    //Each Philosopher spends its life pickup forks (in order to Eat) and
    // putting down forks, in order to Think. Before picking forks and before
    // putting them, the Philosopher sleeps random time, between 1 &
    // MAX_SLEEP Milliseconds, in order to artificially slow things a bit!
    // Use the "rand" for this! You have example from previous work.
    // Each Philosopher will have a Life Cycle of 5 rounds and then finish!

    public void run(){
        for(int i = 0; i < MAX_PHILOSOPHY_ROUNDS; i++){
            int sleepRand = rand.nextInt(MAX_SLEEP) + 1;
            try {
                Thread.sleep(sleepRand);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            table.pickup(philID);

            sleepRand = rand.nextInt(MAX_SLEEP) + 1;
            try {
                Thread.sleep(sleepRand);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            table.putdown(philID);

        }
    }

}