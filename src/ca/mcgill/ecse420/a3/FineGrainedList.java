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

  public boolean add(Item item) {
    int key = item.hashCode();
    head.lock();
    LockableNode pred = head;
    try {
      LockableNode<Item> curr = pred.next;
      if (curr == null) {
        LockableNode<Item> newNode = new LockableNode<>(item);
        pred.next = newNode;
        return true;
      }
      curr.lock();
      try {
        while (curr.key < key) {
          pred.unlock();
          pred = curr;
          curr = curr.next;
          curr.lock();
        }
        if (curr.key == key) {
          return false;
        }
        LockableNode<Item> newNode = new LockableNode<>(item);
        newNode.next = curr;
        pred.next = newNode;
        System.out.println("Added the item " + key);
        return true;
      } finally {
        curr.unlock();
      }
    } finally {
      pred.unlock();
    }
  }

  public boolean remove(Item item) {
    LockableNode<Item> pred = null;
    LockableNode<Item> curr = null;
    int key = item.hashCode();
    head.lock();
    try {
      pred = head;
      curr = pred.next;
      if (curr == null) {
        return false;
      }
      curr.lock();
      try {
        while (curr.key < key) {
          pred.unlock();
          pred = curr;
          curr = curr.next;
          curr.lock();
        }
        if (curr.key == key) {
          pred.next = curr.next;
          System.out.println("Removed item " + item);
          return true;
        }
        return false;
      } finally {
        curr.unlock();
      }
    } finally {
      pred.unlock();
    }
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
