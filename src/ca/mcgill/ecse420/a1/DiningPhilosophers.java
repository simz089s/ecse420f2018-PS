package ca.mcgill.ecse420.a1;

import java.util.concurrent.Executor;
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
      philosophers[i] = new Philosopher(i, chopsticks, numberOfPhilosophers);
      execs.execute(philosophers[i]);
    }

    execs.shutdown();
  }

  public static class Philosopher implements Runnable {

    private int id;
    private Object[] chopsticks;
    private int numberOfPhilosophers;

    Philosopher(int pId, Object[] pChopsticks, int pNumberOfPhilosophers) {
      id = pId;
      chopsticks = pChopsticks;
      numberOfPhilosophers = pNumberOfPhilosophers;
      System.out.println("Philosopher #" + id + " created.");
    }

    @Override
    public void run() {
      System.out.println("Philosopher #" + id + " started running.");
      while (true) {
        synchronized (chopsticks[id]) {
          synchronized (chopsticks[(id + 1) % numberOfPhilosophers]) {
            System.out.println("Philosopher #" + id + " eating.");
            try {
              Thread.sleep((int) (Math.random() % 100));
            } catch (InterruptedException ex) {
            }
          }
        }
        System.out.println("Philosopher #" + id + " thinking.");
        try {
          Thread.sleep((int) (Math.random() % 100));
        } catch (InterruptedException ex) {
        }
      }
    }
  }

  public static class Philosyncher implements Runnable {

    private int id;
    private Object[] chopsticks;
    private int numberOfPhilosophers;

    Philosyncher(int pId, Object[] pChopsticks, int pNumberOfPhilosophers) {
      id = pId;
      chopsticks = pChopsticks;
      numberOfPhilosophers = pNumberOfPhilosophers;
      System.out.println("Philosopher #" + id + " created.");
    }

    @Override
    public void run() {
      System.out.println("Philosopher #" + id + " started running.");

      while (true) {
        pickUpChopstick(id);
        System.out.println("Philosopher #" + id + " eating.");
        try {
          Thread.sleep((int) (Math.random() % 100));
        } catch (InterruptedException ex) {
        }

        putDownChopstick(id);
        System.out.println("Philosopher #" + id + " thinking.");
        try {
          Thread.sleep((int) (Math.random() % 100));
        } catch (InterruptedException ex) {
        }
      }
    }

    private void pickUpChopstick(int pId) {
      checkChopstick();

    }

    private void checkChopstick() {
//      TODO
    }

    private void putDownChopstick(int pId) {
//      TODO
    }

    private void freeChopstick() {
//      TODO
    }
  }
}
