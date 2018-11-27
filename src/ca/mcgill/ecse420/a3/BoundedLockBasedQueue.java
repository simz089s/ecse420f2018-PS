package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedLockBasedQueue<Item> {
  private Object[] items;
  private ReentrantLock deqLock;
  private ReentrantLock enqLock;
  private Condition notFull;
  private Condition notEmpty;
  private int head = 0;
  private int tail = 0;

  public BoundedLockBasedQueue(int size) {
    this.items = new Object[size];
    deqLock = new ReentrantLock();
    enqLock = new ReentrantLock();
    notFull = enqLock.newCondition();
    notEmpty = deqLock.newCondition();
  }

  public void enqueue(Item item) throws InterruptedException {
    boolean mustWakeDequeue;
    enqLock.lock();
    try {
      while (tail - head == items.length) {
        System.out.println("Queue is full");
        notFull.await();
      }
      items[tail % items.length] = item;
      tail++;
      mustWakeDequeue = tail - head == 1;
    } finally {
      enqLock.unlock();
    }
    if (mustWakeDequeue) {
      try {
        deqLock.lock();
        notEmpty.signalAll();
      } finally {
        deqLock.unlock();
      }
    }
  }

  public Item dequeue() throws InterruptedException {
    Item itemReturn;
    boolean mustWakeEnq;
    deqLock.lock();
    try {
      while (tail - head == 0) {
        System.out.println("Queue is Empty");
        notEmpty.await();
      }
      itemReturn = (Item) items[head % items.length];
      head++;
      mustWakeEnq = tail - head == items.length - 1;
    } finally {
      deqLock.unlock();
    }
    if (mustWakeEnq) {
      try {
        enqLock.lock();
        notFull.signalAll();
      } finally {
        enqLock.unlock();
      }
    }
    return itemReturn;
  }
}
