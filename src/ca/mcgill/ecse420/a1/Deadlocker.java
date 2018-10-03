package ca.mcgill.ecse420.a1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlocker implements Runnable {

  private static Lock lock1 = new ReentrantLock();
  private static Lock lock2 = new ReentrantLock();

  private int id;

  public static void main(String[] args) {
    Runnable th1 = new Deadlocker(1);
    Runnable th2 = new Deadlocker(2);
    th1.run();
    th2.run();
  }

  Deadlocker(int pId) {
    id = pId;
  }

  @Override
  public void run() {
    System.out.println("Thread " + id + " started.");
    while (true) {
      lock1.lock();
      lock2.lock();
      System.out.println("Sleeping.");
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
      }
      System.out.println("Done.");
      lock1.unlock();
      lock2.unlock();
    }
  }
}
