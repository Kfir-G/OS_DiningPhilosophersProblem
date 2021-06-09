import java.util.concurrent.locks.*; //Import for Locks

public class DiningPhilosophers {
    //5 Dining Philosophers
    private static final int N = 5;

    //Enumeration type for Philosopher's States
    private enum PHIL_STATES {THINKING, HUNGRY, EATING};
    //When you use the constants inside it, you must use these as //follows. For example,
    //		state[i] = PHIL_STATES.THINKING;

    //The state Array, holding the current state for each Philosopher
    private PHIL_STATES[] state;

    //The State Lock - which is effectively the Monitor Lock
    //This needs to be Acquired at the beginning of every Public
    //Method in this class And Released at the end of each
    //Public Method.
    final Lock stateLock;

    //The Condition Array, holding the Condition Variable for Each
    //Philosopher
    final Condition[] self;


    //Constructor - Creates the state Array
    // Array Each of these arrays is of size N, of course!

    public DiningPhilosophers() {
        state = new PHIL_STATES[N];
        self = new Condition[N];
        stateLock = new ReentrantLock();
        for (int i = 0; i < N; i++){
            self[i] = stateLock.newCondition();
            state[i] = PHIL_STATES.THINKING;
        }
    }
    //pickup Method - implemented as defined in Lecture Notes.
//Note that Lock is Acquired at the Beginning of this Method and //Released at the end of it! Note that "await();" Throws an
// InterruptedExceptionexception that needs to be caught in a try..catch
// block. The Release of the Lock should, therefore, take place in
// "finally" block after the catch - it will be executed in any case
// (exception or not!), so that Lock will be released anyway by this
// Method (whether or not exception has occured!)

    public void pickup(int i) {
        stateLock.lock();
        try {
        state[i] = PHIL_STATES.HUNGRY;
            System.out.println("Phil "+i+":" +state[i]);
        test(i);
         while(state[i] != PHIL_STATES.EATING)
                self[i].await();
        }
        catch(InterruptedException e) {
            System.out.println("InterruptedException:"+e);
        } finally {
            stateLock.unlock();
        }
    }

    //putdown Method - implemented as defined in Lecture Notes.
//Note that Lock is Acquired at the Beginning of this Method and
// Released at the end of it! No exception is thrown here, so no need for
// try..catch block! However, need to release lock in ALL
// normal/abnormal cases!!!!!!

    public void putdown(int i) {
        stateLock.lock();
        try {
            state[i] = PHIL_STATES.THINKING;
            System.out.println("Phil "+i+":" +state[i]);
            test((i + N - 1) % N);// test left
            test((i + 1) % N);// test right
        }
        catch(Exception e) {
            System.out.println("Exception: "+ e);
        }
        finally {
            stateLock.unlock();
        }
    }
    //test Method - implemented as defined in Lecture Notes.
    //Notice this is a private Method, that will be called only by
    // pickup/putdown Methods. Since both pickup and putdown are using
    // the Lock (acquired at the beginning and released at the end), then No
    // need to use the Lock here, because it is anyway invoked when the
    // Lock is acquired!

    private void test(int i) {
        if ((state[i] == PHIL_STATES.HUNGRY)&&//Philosopher i is HUNGRY
                (state[(i + N - 1) % N] != PHIL_STATES.EATING)&&//LEFT Not EATING
                (state[(i + 1) % N] != PHIL_STATES.EATING)) {//RIGHT Not EATING
            state[i] = PHIL_STATES.EATING;
            System.out.println("Phil "+i+":" +state[i]);
            self[i].signal();
        }

}
}