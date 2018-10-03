package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

  public static void main(String[] args) {

    int numberOfPhilosophers = 5;
//    Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
    Philosyncher[] philosophers = new Philosyncher[numberOfPhilosophers];
    Object[] chopsticks = new Object[numberOfPhilosophers];

    for (int i = 0; i < chopsticks.length; i++) {
      chopsticks[i] = new ReentrantLock(true);
    }

    ExecutorService execs = Executors.newFixedThreadPool(numberOfPhilosophers);

    for (int i = 0; i < numberOfPhilosophers; i++) {
//      philosophers[i] = new Philosopher(i, chopsticks, numberOfPhilosophers);
      philosophers[i] = new Philosyncher(i, chopsticks, numberOfPhilosophers);
      execs.execute(philosophers[i]);
    }

    execs.shutdown();

    while (!execs.isTerminated()) {
      System.out.println("Running/waiting... (If you see this more than twice in a row, there is probably a deadlock)");
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ex) {
      }
    }
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
      int a = 0;
      while (true) {
        synchronized (chopsticks[id]) {
          synchronized (chopsticks[(id + 1) % numberOfPhilosophers]) {
            System.out.println("Philosopher #" + id + " eating. " + a);
            try {
              Thread.sleep((int) (Math.random() % 100));
            } catch (InterruptedException ex) {
            }
          }
        }
        System.out.println("Philosopher #" + id + " thinking. " + a);
        try {
          Thread.sleep((int) (Math.random() % 100));
        } catch (InterruptedException ex) {
        }
        a++;
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
      long startTime;
      long endTime;
      long sumTime = 0;
      int countTime = 1;

      while (true) {
        startTime = System.nanoTime();
        pickUpChopstick(id);
        endTime = System.nanoTime();
        sumTime += endTime - startTime;
        System.out.println("Philosopher #" + id + " eating. Average wait time (ns): " + (sumTime / countTime) + " and ate " + countTime + " times.");
        try {
          Thread.sleep((int) (Math.random() % 100));
        } catch (InterruptedException ex) {
        } finally{
          putDownChopstick(id);
        }
        System.out.println("Philosopher #" + id + " thinking.");
        try {
          Thread.sleep((int) (Math.random() % 100));
        } catch (InterruptedException ex) {
        }
        countTime++;
      }
    }

    private void pickUpChopstick(int pId) {
      ((Lock)chopsticks[pId]).lock();
    }

    private void putDownChopstick(int pId) {
      ((Lock)chopsticks[pId]).unlock();
    }
  }
}
