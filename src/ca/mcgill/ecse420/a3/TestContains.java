package ca.mcgill.ecse420.a3;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestContains {
  private static ExecutorService exec  = Executors.newCachedThreadPool();
  private static FineGrainedList<Integer> l = new FineGrainedList<>(-1);
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Random rand = new Random();
    for (int i = 0; i < 10; i++) {
      if (rand.nextInt(3) == 0) {
        final int jFinal = i;
        exec.execute(() -> l.add(jFinal));
      }
      }
//      if(rand.nextInt(3) == 0) {
//        final int jFinal = i-4;
//        exec.execute(() -> l.remove(jFinal));
//      }
//    }
//
//    for (int i = 0; i < 10; i++) {
//      if(rand.nextInt(3) == 0) {
//        final int jFinal = i-4;
//        Future<Boolean> isTrue = exec.submit(() -> l.contains(jFinal));
//        System.out.println("The number " +i+ " is " + isTrue.get());
//      }
//    }
    exec.shutdown();
    while(!exec.isTerminated());
  }
}
