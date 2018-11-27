package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;

public class FineGrainNode<T> {
  private T item;
  private int key;
  public volatile FineGrainNode next;
  private static Lock pred;
  private static Lock cur;

}
