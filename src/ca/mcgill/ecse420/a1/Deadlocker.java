package ca.mcgill.ecse420.a1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlocker {
  private static Runnable th1;
  private static Runnable th2;
  private static boolean alive1 = false;
  private static boolean alive2 = false;
  private static Lock lock1 = new ReentrantLock();
  private static Lock lock2 = new ReentrantLock();

  public static void main(String[] args) {
    th1 =
        () -> {
          alive1 = true;
          lock1.lock();
          System.out.println("Thread 1 started.");
          while (true) {
            if (alive2) {
              lock2.unlock();
              lock1.unlock();
              break;
            }
          }
          System.out.println("Done.");
        };
    th2 =
        () -> {
          alive2 = true;
          lock2.lock();
          System.out.println("Thread 2 started.");
          while (true) {
            if (alive1) {
              lock1.unlock();
              lock2.unlock();
              break;
            }
          }
          System.out.println("Done.");
        };
    th1.run();
    th2.run();
  }
}
