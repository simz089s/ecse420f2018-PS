package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersSync {

  protected static int eaters = 0;

  public static void main(String[] args) {

    int numberOfPhilosophers = 5;
    Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
    Object[] chopsticks = new Object[numberOfPhilosophers];

    Lock mutex = new ReentrantLock(true);
    Condition freeChopstick = mutex.newCondition();

    for (int i = 0; i < chopsticks.length; i++) {
      chopsticks[i] = new ReentrantLock(true);
    }

    ExecutorService execs = Executors.newFixedThreadPool(numberOfPhilosophers);

    for (int i = 0; i < numberOfPhilosophers; i++) {
      philosophers[i] = new Philosopher(i, numberOfPhilosophers, chopsticks, mutex, freeChopstick);
    }

    for (int i = 0; i < numberOfPhilosophers; i++) {
      execs.execute(philosophers[i]);
    }

    execs.shutdown();

    while (!execs.isTerminated()) {
      System.out.println(
          "Running/freeChopstick... (If you see this more than twice in a row, there is probably a deadlock)");
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ex) {
      }
    }
  }

  public static class Philosopher implements Runnable {

    private int id;
    private int numberOfPhilosophers;
    private Lock[] chopsticks = new ReentrantLock[2];
    private Lock mutex;
    private Condition freeChopstick;

    Philosopher(
        int pId,
        int pNumberOfPhilosophers,
        Object[] pChopsticks,
        Lock pMutex,
        Condition pFreeChopstick) {
      id = pId;
      numberOfPhilosophers = pNumberOfPhilosophers;
      chopsticks[0] = (Lock) pChopsticks[id];
      chopsticks[1] = (Lock) pChopsticks[(id + 1) % numberOfPhilosophers];
      mutex = pMutex;
      freeChopstick = pFreeChopstick;
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
        pickUpChopsticks();
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
          Thread.sleep(0);
        } catch (InterruptedException ex) {
        } finally {
          putDownChopsticks();
        }
        System.out.println("Philosopher #" + id + " thinking.");
        try {
          Thread.sleep(0);
        } catch (InterruptedException ex) {
        }
        countTime++;
      }
    }

    private void pickUpChopsticks() {
      mutex.lock();
      eaters++;
      if (eaters >= numberOfPhilosophers) {
        try {
          freeChopstick.await();
        } catch (InterruptedException e) {
        }
        mutex.unlock();
      } else mutex.unlock();
      chopsticks[0].lock();
      chopsticks[1].lock();
    }

    private void putDownChopsticks() {
      chopsticks[0].unlock();
      chopsticks[1].unlock();
      mutex.lock();
      eaters--;
      if (eaters == numberOfPhilosophers - 1) freeChopstick.signal();
      mutex.unlock();
    }
  }
}
