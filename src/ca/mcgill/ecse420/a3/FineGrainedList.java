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
      /* if the list only contains the head then check the head key
       * if the head key == the item key then return true else we have to iterate through the list
       */
      if (curr == null || pred.key == key) {
        return pred.key == key;
      }
      curr.lock();
      try {
        while (curr.key < key && curr.next != null) {
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

  // non concurrent add method
  public boolean add(Item item) {
    if (head.next == null) {
      if (head.key < item.hashCode()) {
        head.next = new LockableNode<>(item);
        return true;
      }
      LockableNode<Item> newNode = new LockableNode<>(item);
      newNode.next = head;
      head = newNode;
      return true;
    }
    LockableNode<Item> pre = head;
    LockableNode<Item> cur = head.next;

    while (cur != null) {
      if (cur.key < item.hashCode()) {
        pre = cur;
        cur = pre.next;
      }
      else if (cur.key == item.hashCode()) {
        return false;
      }
      else {
        LockableNode<Item> newItem = new LockableNode<>(item);
        pre.next = newItem;
        newItem.next = cur;
        return true;
      }
    }

    pre.next = new LockableNode<>(item);
    return true;
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
