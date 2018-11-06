package ca.mcgill.ecse420.a2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

public class TestLocks {
  private int counter = 0;
  private Lock lock;
  private int numThread;

  public TestLocks(Lock lock, int numThread) {
    this.lock = lock;
    this.numThread = numThread;
  }

  public static void main(String[] args) {
    System.out.println("Testing FilterLock");
    TestLocks filterLocks = new TestLocks(new FilterLock(50), 50);
    filterLocks.testTheLock();
    System.out.println("Testing BakeryLock");
    TestLocks bakeryLock = new TestLocks(new LamportBakeryLock(50), 50);
    bakeryLock.testTheLock();
  }

  public void testTheLock() {
    ExecutorService executor = Executors.newFixedThreadPool(numThread);
    for (int i = 0; i < numThread; i++) {
      executor.execute(
          new Runnable() {
            @Override
            public void run() {
              lock.lock();
              counter++;
              lock.unlock();
            }
          });
    }
    executor.shutdown();
    while (!executor.isTerminated()) ;
    if (counter == numThread) {
      System.out.println("The Lock worked successfully");
    } else {
      System.out.println(
          "The counter value wad: " + counter + "but was expected to be: " + numThread);
    }
  }
}
