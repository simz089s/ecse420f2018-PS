package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;
/*
 * We run into issues when we can no longer add or remove elements from the array
 *
 */
public class BoundedLockFreeBasedQueue<Item> {
  private AtomicInteger head = new AtomicInteger();
  private AtomicInteger tail = new AtomicInteger();
  private Object[] items;

  public BoundedLockFreeBasedQueue(int size) {
    this.items = new Object[size];
    head.set(0);
    tail.set(0);
  }

  public void enqueue(Item item) throws InterruptedException {
    boolean wait = true;
    while (wait) {
      int myHead = head.getAndIncrement()
      int myTail = tail.get();
    }
  }

  public Item dequeue(){
    return null;
  }
}
