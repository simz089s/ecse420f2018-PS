package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainNode<T> {
  private T item;
  private int key;
  private Lock lock = new ReentrantLock();

}
