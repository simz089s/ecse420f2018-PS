package ca.mcgill.ecse420.a1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlocker {

  private static Lock[] lockArray = new Lock[2];

  public static void main(String[] args) {
    lockArray[0] = new ReentrantLock();
    lockArray[1] = new ReentrantLock();

    // We keep restarting if it doesn't deadlock on the first try and until it deadlocks
    while (true) {
      System.out.println("New attempt...");

      // Create the two threads that will deadlock and run them

      Thread th1 =
          new Thread(
              () -> {
                System.out.println("Thread 1 started");
                System.out.println("Thread 1 waits to acquire lock 0");
                lockArray[0].lock();
                System.out.println("Thread 1 acquired lock 0");
                double rand = Math.random();
                int time = (int) (1000 * rand);
                System.out.println("Thread 1 sleeps for " + time + " milliseconds");
                try {
                  Thread.sleep(time);
                  System.out.println("Thread 1 waits to acquire lock 1");
                  lockArray[1].lock();
                  System.out.println("Thread 1 acquired lock 1");
                  System.out.println("Thread 1 unlocked both locks");
                  lockArray[1].unlock();
                } catch (InterruptedException e) {
                } finally {
                  lockArray[0].unlock();
                  System.out.println("Thread 1 terminated");
                }
              });
      Thread th2 =
          new Thread(
              () -> {
                System.out.println("Thread 2 started");
                double rand = Math.random();
                int time = (int) (1000 * rand);
                System.out.println("Thread 2 sleeps for " + time + " milliseconds");
                try {
                  Thread.sleep(time);
                  System.out.println("Thread 2 waits to acquire lock 1");
                  lockArray[1].lock();
                  System.out.println("Thread 2 acquired lock 1");
                  System.out.println("Thread 2 waits to acquire lock 0");
                  lockArray[0].lock();
                  System.out.println("Thread 2 acquired lock 0");
                  System.out.println("Thread 2 unlocked both locks");
                  lockArray[0].unlock();
                  lockArray[1].unlock();
                  System.out.println("Thread 2 terminated");
                } catch (InterruptedException e) {
                }
              });
      th1.start();
      th2.start();
      try {
        th1.join();
        th2.join();
      } catch (InterruptedException e) {
      }
    }
  }
}
