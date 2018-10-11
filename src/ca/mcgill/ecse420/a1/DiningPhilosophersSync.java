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
  }

  // Mostly the same as DiningPhilosopher.java in general except for the locking
  public static class Philosopher implements Runnable {

    private int id;
    private int numberOfPhilosophers;
    private Lock[] chopsticks = new ReentrantLock[2];
    private Lock mutex; // Guard critical section of counting and checking the number of eaters
    private Condition
        freeChopstick; // If there are available chopsticks for a philosopher such that <=n-1 are
    // eating

    Philosopher(
        int pId,
        int pNumberOfPhilosophers,
        Object[] pChopsticks,
        Lock pMutex,
        Condition pFreeChopstick) {
      id = pId;
      numberOfPhilosophers = pNumberOfPhilosophers;
      // More convenient way of keeping track of left and right chopsticks
      chopsticks[0] = (Lock) pChopsticks[id];
      chopsticks[1] = (Lock) pChopsticks[(id + 1) % numberOfPhilosophers];
      mutex = pMutex;
      freeChopstick = pFreeChopstick;
      System.out.println("Philosopher #" + id + " created.");
    }

    @Override
    public void run() {
      System.out.println("Philosopher #" + id + " started running.");
      // Calculate running average wait time and amount of times having eaten
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
      // Entered critical section
      eaters++; // Show interest in eating
      if (eaters >= numberOfPhilosophers) {
        try {
          freeChopstick.await(); // Too many eaters, so wait
        } catch (InterruptedException e) {
        }
        mutex.unlock();
      } else mutex.unlock();
      // "Pick up" chopsticks
      chopsticks[0].lock();
      chopsticks[1].lock();
    }

    private void putDownChopsticks() {
      // "Put down" chopsticks
      chopsticks[0].unlock();
      chopsticks[1].unlock();
      mutex.lock();
      // Entered critical section
      eaters--; // To "signal" the philosopher is not (or rather, is done) eating
      if (eaters == numberOfPhilosophers - 1)
        freeChopstick.signal(); // The actual lock condition signal
      mutex.unlock();
    }
  }
}
