public class DPManager {
    //5 Philosophers in the System
    private static final int N = 5;

    //Philosophers Array
    private Philosopher[] philosophers;

    //The single ("passive" entity) table of DiningPhilosophers
    private DiningPhilosophers table;


    public DPManager() {
        philosophers = new Philosopher[N];
        table = new DiningPhilosophers();
        // init the philosophers
        for(int i = 0; i < N; i++)
            philosophers[i] = new Philosopher(table, i);
        // start the philosophers
        for(int i = 0; i < N; i++)
            philosophers[i].start();
    }
    //Main method simply creates the DPManager Object, which then
    // creates the DiningPhilosophers table and all the philosophers and get
    // them to run!

    public static void main(String[] args) {
        DPManager manager = new DPManager();
    }
}