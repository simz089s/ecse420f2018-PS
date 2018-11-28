package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<Item> {
  private LockableNode<Item> head;

  public FineGrainedList(Item item) {
    head = new LockableNode<>(item);
  }

  public boolean contains(Item item) {
    int key = item.hashCode();
    head.lock();
    LockableNode pred = head;
    try {
      LockableNode<Item> curr = pred.next;
      curr.lock();
      try {
        while (curr.key < key) {
          pred.unlock();
          pred = curr;
          curr = curr.next;
          curr.lock();
        }
        if (curr.key == key) {
          return true;
        }
      } finally {
        curr.unlock();
      }
    } finally {
      pred.unlock();
    }
    return false;
  }
  }

class LockableNode<Item> {
  protected Item item;
  protected int key;
  protected Lock lock = new ReentrantLock();
  protected LockableNode<Item> next;

  public LockableNode(Item item) {
    this.item = item;
    this.key = item.hashCode();
  }

  public void lock() {
    lock.lock();
  }

  public void unlock() {
    lock.unlock();
  }
}
