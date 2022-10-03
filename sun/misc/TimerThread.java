package sun.misc;

class TimerThread extends Thread
{
    public static boolean debug;
    static TimerThread timerThread;
    static boolean notified;
    static Timer timerQueue;
    
    protected TimerThread() {
        super("TimerThread");
        (TimerThread.timerThread = this).start();
    }
    
    @Override
    public synchronized void run() {
        while (true) {
            if (TimerThread.timerQueue == null) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {}
            }
            else {
                TimerThread.notified = false;
                final long n = TimerThread.timerQueue.sleepUntil - System.currentTimeMillis();
                if (n > 0L) {
                    try {
                        this.wait(n);
                    }
                    catch (final InterruptedException ex2) {}
                }
                if (TimerThread.notified) {
                    continue;
                }
                final Timer timerQueue = TimerThread.timerQueue;
                TimerThread.timerQueue = TimerThread.timerQueue.next;
                final TimerTickThread call = TimerTickThread.call(timerQueue, timerQueue.sleepUntil);
                if (!TimerThread.debug) {
                    continue;
                }
                final long n2 = System.currentTimeMillis() - timerQueue.sleepUntil;
                System.out.println("tick(" + call.getName() + "," + timerQueue.interval + "," + n2 + ")");
                if (n2 <= 250L) {
                    continue;
                }
                System.out.println("*** BIG DELAY ***");
            }
        }
    }
    
    protected static void enqueue(final Timer timer) {
        Timer next = TimerThread.timerQueue;
        if (next == null || timer.sleepUntil <= next.sleepUntil) {
            timer.next = TimerThread.timerQueue;
            TimerThread.timerQueue = timer;
            TimerThread.notified = true;
            TimerThread.timerThread.notify();
        }
        else {
            Timer timer2;
            do {
                timer2 = next;
                next = next.next;
            } while (next != null && timer.sleepUntil > next.sleepUntil);
            timer.next = next;
            timer2.next = timer;
        }
        if (TimerThread.debug) {
            final long currentTimeMillis = System.currentTimeMillis();
            System.out.print(Thread.currentThread().getName() + ": enqueue " + timer.interval + ": ");
            for (Timer timer3 = TimerThread.timerQueue; timer3 != null; timer3 = timer3.next) {
                System.out.print(timer3.interval + "(" + (timer3.sleepUntil - currentTimeMillis) + ") ");
            }
            System.out.println();
        }
    }
    
    protected static boolean dequeue(final Timer timer) {
        Timer timer2 = null;
        Timer timer3;
        for (timer3 = TimerThread.timerQueue; timer3 != null && timer3 != timer; timer3 = timer3.next) {
            timer2 = timer3;
        }
        if (timer3 == null) {
            if (TimerThread.debug) {
                System.out.println(Thread.currentThread().getName() + ": dequeue " + timer.interval + ": no-op");
            }
            return false;
        }
        if (timer2 == null) {
            TimerThread.timerQueue = timer.next;
            TimerThread.notified = true;
            TimerThread.timerThread.notify();
        }
        else {
            timer2.next = timer.next;
        }
        timer.next = null;
        if (TimerThread.debug) {
            final long currentTimeMillis = System.currentTimeMillis();
            System.out.print(Thread.currentThread().getName() + ": dequeue " + timer.interval + ": ");
            for (Timer timer4 = TimerThread.timerQueue; timer4 != null; timer4 = timer4.next) {
                System.out.print(timer4.interval + "(" + (timer4.sleepUntil - currentTimeMillis) + ") ");
            }
            System.out.println();
        }
        return true;
    }
    
    protected static void requeue(final Timer timer) {
        if (!timer.stopped) {
            final long currentTimeMillis = System.currentTimeMillis();
            if (timer.regular) {
                timer.sleepUntil += timer.interval;
            }
            else {
                timer.sleepUntil = currentTimeMillis + timer.interval;
            }
            enqueue(timer);
        }
        else if (TimerThread.debug) {
            System.out.println(Thread.currentThread().getName() + ": requeue " + timer.interval + ": no-op");
        }
    }
    
    static {
        TimerThread.debug = false;
        TimerThread.notified = false;
        TimerThread.timerQueue = null;
    }
}
