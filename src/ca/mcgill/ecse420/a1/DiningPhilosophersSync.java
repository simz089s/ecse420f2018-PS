package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersSync {

  public static void main(String[] args) {

    int numberOfPhilosophers = 5;

    Object[] chopsticks = new Object[numberOfPhilosophers];
    Condition[] eating = new Condition[numberOfPhilosophers];

    Philosyncher[] philosophers = new Philosyncher[numberOfPhilosophers];

    for (int i = 0; i < chopsticks.length; i++) {
      chopsticks[i] = new ReentrantLock(true);
      eating[i] = ((Lock) chopsticks[i]).newCondition();
    }

    ExecutorService execs = Executors.newFixedThreadPool(numberOfPhilosophers);

    for (int i = 0; i < numberOfPhilosophers; i++) {
      philosophers[i] = new Philosyncher(i, numberOfPhilosophers, chopsticks, eating);
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

  public static class Philosyncher implements Runnable {

    /*
    * Different solutions include only allowing n-1 philosophers to eat at any time,
    * making each philosopher pick the chopstick on the same side except for one,
    * 
    * */

    private int id;
    private int numberOfPhilosophers;
    private Object[] chopsticks;
    private Condition[] eating;
    private static Lock mutex = new ReentrantLock(true);
    private static Condition waiting = mutex.newCondition();
    private static int eaters = 0;

    Philosyncher(int pId, int pNumberOfPhilosophers, Object[] pChopsticks, Condition[] pEating) {
      id = pId;
      numberOfPhilosophers = pNumberOfPhilosophers;
      chopsticks = pChopsticks;
      eating = pEating;
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
        pickUpChopstick();
        endTime = System.nanoTime();
        sumTime += endTime - startTime;
        System.out.println(
            "Philosopher #"
                + id
                + " eating. Average wait time (ns): "
                + (sumTime / countTime)
                + " and ate "
                + countTime
                + " times.");
        try {
          Thread.sleep((int) (Math.random() % 100));
        } catch (InterruptedException ex) {
        } finally {
          putDownChopstick();
        }
        System.out.println("Philosopher #" + id + " thinking.");
        try {
          Thread.sleep((int) (Math.random() % 100));
        } catch (InterruptedException ex) {
        }
        countTime++;
      }
    }

    private void pickUpChopstick() {
      mutex.lock();
      eaters++;
      if (eaters >= chopsticks.length) {
        mutex.unlock();
        try {
          waiting.await();
        } catch (InterruptedException e) {
          System.out.println("Philosopher #" + id + " interrupted.");
        }
      } else {
        mutex.unlock();
      }

      try {
        eating[id].await();
        eating[(id + 1) % numberOfPhilosophers].await();
      } catch (InterruptedException e) {
        System.out.println("Philosopher #" + id + " interrupted.");
      }
    }

    private void putDownChopstick() {
      eating[id].signal();
      eating[(id + 1) % numberOfPhilosophers].signal();

      mutex.lock();
      eaters--;
      if (eaters == chopsticks.length - 1) waiting.signal();
      mutex.unlock();
    }
  }
}
