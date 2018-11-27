package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedLockBasedQueue<Item> {
  private Object[] items;
  private ReentrantLock deqLock = new ReentrantLock();
  private ReentrantLock enqLock = new ReentrantLock();
  private Condition notFull = deqLock.newCondition();
  private Condition notEmpty = enqLock.newCondition();
  private int head = 0;
  private int tail = 0;

  public BoundedLockBasedQueue(int size) {
    this.items = new Object[size];
  }

  public void enqueue(Item item) throws InterruptedException {
    enqLock.lock();

    try {
      while (tail - head == items.length) {
        System.out.println("Queue is full");
        notFull.await();
      }
      items[tail % items.length] = item;
      tail++;
      notEmpty.signalAll();
    }
    finally {
      enqLock.unlock();
    }
  }

  public Item dequeue() throws InterruptedException {
    deqLock.lock();

    try {
      while (tail - head == 0) {
        System.out.println("Queue is Empty");
        notEmpty.await();
      }
      Item itemReturn = (Item) items[head % items.length];
      head++;
      notFull.signalAll();
      return itemReturn;
    }
    finally {
      deqLock.unlock();
    }
  }
}
