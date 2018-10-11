package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiningPhilosophers {

  public static void main(String[] args) {

    int numberOfPhilosophers = 5;
    Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
    Object[] chopsticks = new Object[numberOfPhilosophers];

    for (int i = 0; i < chopsticks.length; i++) {
      chopsticks[i] = new Object();
    }

    ExecutorService execs = Executors.newFixedThreadPool(numberOfPhilosophers);

    for (int i = 0; i < numberOfPhilosophers; i++) {
      philosophers[i] = new Philosopher(i, numberOfPhilosophers, chopsticks);
      execs.execute(philosophers[i]);
    }

    execs.shutdown();

    while (!execs.isTerminated()) {
      System.out.println(
          "Running/waiting... (If you see this more than twice in a row, there is probably a deadlock)");
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ex) {
      }
    }
  }

  public static class Philosopher implements Runnable {

    private int id; // To identify each thread/philosopher
    private int numberOfPhilosophers;
    private Object[] chopsticks;

    Philosopher(int pId, int pNumberOfPhilosophers, Object[] pChopsticks) {
      id = pId;
      numberOfPhilosophers = pNumberOfPhilosophers;
      chopsticks = pChopsticks;
      System.out.println("Philosopher #" + id + " created.");
    }

    @Override
    public void run() {
      System.out.println("Philosopher #" + id + " started running.");
      int count = 0; // How many times this philosopher has eaten in a run
      while (true) {
        // Lock ("reserve"/use) both chopsticks on each side. ID-th chopstick is left and next one
        // is the right
        // as an easy and clean way to divide them (wraps around the array to form the "circular
        // table").
        synchronized (chopsticks[id]) {
          synchronized (chopsticks[(id + 1) % numberOfPhilosophers]) {
            System.out.println("Philosopher #" + id + " eating. " + count);
            try {
              Thread.sleep(0); // Eats for 0 ms. Otherwise it takes a while to deadlock.
            } catch (InterruptedException ex) {
            }
          }
        }
        System.out.println("Philosopher #" + id + " thinking. " + count);
        try {
          Thread.sleep(0);
        } catch (InterruptedException ex) {
        }
        count++;
      }
    }
  }
}
