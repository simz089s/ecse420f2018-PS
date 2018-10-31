1. ***Locks***
    1. A. See `FilterLock.java`.
    2. Q. Does the Filter lock allow some threads to overtake others an arbitrary number of times? Explain.
    3. A. See `LamportBakeryLock.java`.
    4. Q. Does the Bakery lock allow some threads to overtake others an arbitrary number of times? Explain.
    5. Q. Propose a test that verifies if a lock works, i.e., if it provides mutual exclusion.
    6. Q. Provide an implementation for the proposed test and verify if the implemented locks do provide mutual exclusion.

2. Q. Consider LockOne and LockTwo introduced in Chapter 2 of the course text; do they still satisfy two-thread mutual exclusion if the shared atomic registers - “flag” in LockOne and “victim” in LockTwo - are replaced by regular registers?

    ```java
    class LockOne implements Lock {
        private boolean[] flag = new boolean[2]; // Each thread has flag
        public void lock() {
            flag[i] = true; // Set my flag
            while (flag[j]) {} // Wait for other flag to become false
        }
    }

    public class LockTwo implements Lock {
        private int victim;
        public void lock() {
            victim = i; // Let other go first
            while (victim == i) {}; // Wait for permission
        }
        public void unlock() {} // Nothing to do
    }
    ```

    A: No. On a simultaneous read and write, the register might return the old value, making one or both threads think it is their turn to enter the critical section when it is not. On the other hand, an atomic register is linearizable.

    ![Registers](https://stackoverflow.com/a/8872960 "StackOverflow")

3. Consider the protocol shown in Fig. 1 which is supposed to achieve n-thread mutual exclusion.

    ```java
    // Fig. 1
    class LockThree implements Lock {       //  1
        private int turn;                   //  2
        private boolean busy = false;       //  3
        public void lock() {                //  4
            int me = ThreadID.get();        //  5
            turn = me;                      //  6
            do {                            //  7
                busy = true;                //  8
            } while ( turn == me && busy);  //  9
        }                                   // 10
        public void unlock() {              // 11
            busy = false;                   // 12
        }                                   // 13
    }                                       // 14
    ```

    1. Q. Does this protocol satisfy mutual exclusion? (Hint: Start the proof by assuming that two
threads A and B are in the critical section at the same time.)

        A: Assuming there are two threads A and B and that they are in the CS at the same time (CS_A and CS_B).
        This implies that `busy == false` as the only way to have passed the busy-waiting part is for the loop condition to have been false, which means `busy==false||turn!=me`.
        It cannot have been `turn != me`.
        It can only ever be the turn of a single thread at any time.
        To enter the for loop a thread must have set `busy = true` and this happens *after* the `turn = me` as such if more than one thread are at the while-loop step, then all except the last to have set `turn = me` will have `turn != me` relative to them and we know they must have all set `busy = true`.
        This means that at least one thread will proceed and unlock, so setting `busy = false` to allow the thread whose `turn == me && busy == true` to proceed from the busy-wait.

        ***Therefore, it is 2-thread mutual exclusive, but not more.*** For more than 2 threads, say they all execute the `turn = me` line consecutively and set `busy = true` and enter the while-loop consecutively, then as `turn` can only take the value of one thread, this means `turn != me && busy` for all the others thus allowing them to break from the while-loop busy-wait and all proceed.

    2. Q. Is this protocol deadlock-free? Explain.

        A: Yes, as the `turn` value can only ever be one thread's at any time, so the while-loop condition can only ever be true for a single thread.

    3. Q. Is this protocol starvation-free? Explain.

        A: No, as there is nothing to ensure fairness. The "victim" (`turn == me`) will always be last to enter, if at all, as nothing stops other threads from completing the CS, unlocking it, locking it again before the previous victim has a chance to complete the while-loop (as now `busy == false`) , though there must be a new victim who has to wait. This means certain threads can go through the CS multiple times before others even complete it once. (i+1 times???)

4. Q. For each of the histories shown in Figs. 2 and 3, are they sequentially consistent? Linearizable? Justify your answer.

```
     r.write(0)    r.read(1)          r.write(2)
A --<-------|-->--<-|------->--<-|--------------------->-------------------
                              r.write(1)                      r.read(2)
B ------------<-|----------------------------------------->--<--------->---
                            r.read(2)     r.write(3)
C ------------------------<---------|->--<---------->----------------------

Fig. 2 History (a)

                    r.read(1)
A -----------------<--------->---------------------------------------------
                           r.write(1)                  r.read(2)
B -----------<------------------------------------->--<--------->----------
                         r.write(2)    r.read(1)
C ----------------------<---------->--<--------->--------------------------

Fig. 3 History (b)
```

A: (a) is certainly not linearizable as we can see that `r.write(3)` happens entirely between two `r.read(2)`, yet we only have a single `r.write(2)`.
For (b), a `r.read(1)` happens entirely before a `r.read(2)` and entirely after a `r.write(2)` with only a single `r.write(1)`, which must have happened before as there is another `r.read(1)` entirely before this `r.read(1)`.

5. Q. Consider the class shown in Fig. 4.

    ```java
    // Fig. 4
    1 | class VolatileExample {
    2 |     int x = 0;
    3 |     volatile boolean v = false;
    4 |     public void writer() {
    5 |         x = 42;
    6 |         v = true;
    7 |     }
    8 |     public void reader() {
    9 |         if (v == true) {
    10|             int y = 100/x;
    11|         }
    12|     }
    13| }
    ```

    Suppose two threads A and B are concurrently calling the methods writer and reader.

    1. Q. According to what you have been told about the Java memory model, will the reader method ever divide by zero? If yes, describe the order in which writer and reader should be invoked (by threads A and B) and take effect for a division by zero to happen.

        A:

    2. Q. Is division by zero possible if both x and v are volatile? What happens if none of x and v are volatile? Justify your answer.

        A:

6. Consider the regular M-valued MRSW construction shown in Fig. 5

    ```java
    // Fig. 5
    1 | public class RegMRSWRegister implements Register<Byte> {
    2 |     private static int RANGE = Byte.MAX_VALUE - Byte.MIN_VALUE + 1;
    3 |     boolean[] r_bit = new boolean[RANGE]; // regular boolean MRSW
    4 |     public RegMRSWRegister(int capacity) {
    5 |         for (int i = 1; i < r_bit.length; i++)
    6 |             r_bit[i] = false;
    7 |         r_bit[0] = true;
    8 |     }
    9 |     public void write(Byte x) {
    10|         r_bit[x] = true;
    11|         for (int i = x - 1; i >= 0; i--)
    12|             r_bit[i] = false;
    13|     }
    14|     public Byte read() {
    15|         for (int i = 0; i < RANGE; i++)
    16|             if (r_bit[i]) {
    17|                 return i;
    18|             }
    19|         return -1; // impossible
    20|     }
    21| }
    ```

    True or false:

   1. Q. If we change the loop at line 11 to `for (int i = x + 1; i < RANGE; i++)`, then the construction is still a regular M-valued MRSW register. Justify your answer.

        A:

   2. Q. If we change the loop at line 11 to `for (int i = x + 1; i < RANGE; i++)`, then the construction yields a safe Boolean MRSW register. Justify your answer.

        A:

7. Q. Show that if binary consensus using atomic registers is impossible for two threads, then it is also impossible for ___n___ threads, where ***n > 2***. (Hint: argue by reduction: if we had a protocol to
solve binary consensus for n threads, then we can transform it into a two-thread protocol.)

    A:

8. Q. Show that if binary consensus using atomic registers is impossible for ***n*** threads, then so is consensus over __*k*__ values, where ___k > 2___.

    A:
