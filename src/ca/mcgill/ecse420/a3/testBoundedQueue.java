package ca.mcgill.ecse420.a3;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class testBoundedQueue {
    private static BoundedLockBasedQueue<Integer> syncQueue = new BoundedLockBasedQueue<>(10);
    private static ExecutorService executor = Executors.newCachedThreadPool();
  public static void main(String[] args) {
    Random rand = new Random();
    for (int i = 0; i <10; i++) {
      if(rand.nextInt(3) == 0) {
        System.out.println("Launch a dequeue task");
        executor.execute(new dequeueTask());
      }
      if(rand.nextInt(3) == 0) {
        System.out.println("Launch an enqueue tasks "+ i);
        executor.execute(new enqueueTask(i));
      }
    }
    executor.shutdown();
    while(!executor.isTerminated());
  }

  static class enqueueTask implements Runnable {
    private int num;

    public enqueueTask(int num) {
      this.num = num;
    }

    @Override
    public void run() {
      try {
        syncQueue.enqueue(num);
        System.out.println("Enqueud " + num);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  static class dequeueTask implements Runnable {

    @Override
    public void run() {
      try {
        System.out.println(syncQueue.dequeue());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
