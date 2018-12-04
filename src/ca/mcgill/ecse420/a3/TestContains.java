package ca.mcgill.ecse420.a3;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestContains {
  private static ExecutorService exec = Executors.newCachedThreadPool();

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Random ran = new Random();
    int bound = 2;
    FineGrainedList<Integer> intList = new FineGrainedList<>();
    boolean testResult = true;
    boolean[] answer = new boolean[15];
    for (int i = 0; i < 15; i++) {
      /* chance to add a number depends on previous
       * number added starting at 1/2 then 1/3 and so on as we add
       * more numbers to the list.
       */
      if (ran.nextInt(bound) == 0) {
        bound++;
        System.out.println("Adding number " + i + " to the list");
        intList.add(i);
        answer[i] = true;
      }
      else {
          answer[i] = false;
      }
    }
    for (int i = 0; i < 15; i++) {
      int testNum = i;
      Future<Boolean> containsResult = exec.submit(() -> intList.contains(testNum));
      boolean ans = containsResult.get();
      System.out.println("The method contains with param " + i + " outputs " + ans);
      if (answer[i] != ans) {
          testResult = false;
      }
    }
    if (testResult) {
      System.out.println("Test passed");
    }
    else {
      System.out.println("Test failed");
    }
    exec.shutdown();
  }
}
