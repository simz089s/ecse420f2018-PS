package ca.mcgill.ecse420.a2;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class LamportBakeryLock implements Lock {

  boolean[] flag;
  Integer[] label;

  public LamportBakeryLock(int n) {
    flag = new boolean[n];
    label = new Integer[n];
    for (int i = 0; i < n; i++) {
      flag[i] = false;
      label[i] = 0;
    }
  }

  @Override
  public void lock() {
    //    flag[i] = true; // Doorway. true => I'm interested
    //    label[i] = Collections.max(Arrays.asList(label)) + 1; // Doorway. Take increasing label
    // (read labels in some arbitrary order)
    //    while (/*there exists k flag[k]&& (label[i],i) > (label[k],k)*/); // Someone is interested
    // whose (label,i) in lexicographic order is lower
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {}

  @Override
  public boolean tryLock() {
    return false;
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return false;
  }

  @Override
  public void unlock() {
    //    flag[i] = false; // No longer interested
    // labels are always increasing
  }

  @Override
  public Condition newCondition() {
    return null;
  }
}
