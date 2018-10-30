package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class FilterLock implements Lock {

  int numLevel;
  int[] level; // level[i] for thread i
  int[] victim; // victim[L] for level L

  public FilterLock(int pNumLevel) {
    level = new int[pNumLevel];
    victim = new int[pNumLevel];
    for (int i = 0; i < pNumLevel; i++) {
      level[i] = 0;
    }
  }

  @Override
  public void lock() {
    for (int L = 1; L < numLevel; L++) { // One level at a time
      //      level[i] = L; // Announce intention to enter level L
      //      victim[L] = i; // Give priority to anyone but me
      //      while (/*(there exists k != i level[k] >= L) && victim[L] == i*/) {} // Wait as long
      // as someone else is at same or higher level, and I'm designated victim. Thread enters level
      // L when it completes the loop
    }
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
    //    level[i] = 0;
  }

  @Override
  public Condition newCondition() {
    return null;
  }
}
