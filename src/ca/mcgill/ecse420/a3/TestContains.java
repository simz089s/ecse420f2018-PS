package ca.mcgill.ecse420.a3;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestContains {
  private static ExecutorService exec = Executors.newCachedThreadPool();

  public static void main(String[] args) {
    Random ran = new Random();
    int bound = 2;
    FineGrainedList<Integer> intList = new FineGrainedList<>();
    for (int i = 0; i < 15; i++) {
      /* chance to add a number depends on previous
       * number added starting at 1/2 then 1/3 and so on as we add
       * more numbers to the list.
       */
      if (ran.nextInt(bound) == 0) {
        bound++;
        System.out.println("Adding number " + i + " to the list");
        intList.add(i);
      }
    }
    for (int i = 0; i < 15; i++) {
      int testNum = i;
      exec.execute(
          () ->
              System.out.println(
                  "The method contains with param "
                      + testNum
                      + " outputs "
                      + intList.contains(testNum)));
    }
    exec.shutdown();
  }
}
