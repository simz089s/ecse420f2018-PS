package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;

public class TryLockFreeBoundedQueue<Item> {
  private Object[] items;
  private AtomicInteger head = new AtomicInteger(0);
  private AtomicInteger tail = new AtomicInteger(0);

  public TryLockFreeBoundedQueue(int size) {
    this.items = new Object[size];
  }

  public void enqueue(Item item) {
    while (true) {
      int myTail = tail.get();
      int myHead = head.get();
      if (myTail - myHead == items.length) {
        //System.out.println("Queue is full");
        continue;
      }
      int newTail = myTail++;
      if (tail.compareAndSet(myTail, newTail)) {
        items[myTail % items.length] = item;
        return;
      }
    }
  }

  public Item dequeue() {
    while (true) {
      int myTail = tail.get();
      int myHead = head.get();
      if (myTail == myHead) {
        //System.out.println("Queue is Empty");
        continue;
      }
      int newHead = myHead++;
      if (tail.compareAndSet(myHead, newHead)) {
        return (Item) items[myHead % items.length];
      }
    }
  }
}
