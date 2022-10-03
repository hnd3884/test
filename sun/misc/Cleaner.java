package sun.misc;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.PhantomReference;

public class Cleaner extends PhantomReference<Object>
{
    private static final ReferenceQueue<Object> dummyQueue;
    private static Cleaner first;
    private Cleaner next;
    private Cleaner prev;
    private final Runnable thunk;
    
    private static synchronized Cleaner add(final Cleaner cleaner) {
        if (Cleaner.first != null) {
            cleaner.next = Cleaner.first;
            Cleaner.first.prev = cleaner;
        }
        return Cleaner.first = cleaner;
    }
    
    private static synchronized boolean remove(final Cleaner cleaner) {
        if (cleaner.next == cleaner) {
            return false;
        }
        if (Cleaner.first == cleaner) {
            if (cleaner.next != null) {
                Cleaner.first = cleaner.next;
            }
            else {
                Cleaner.first = cleaner.prev;
            }
        }
        if (cleaner.next != null) {
            cleaner.next.prev = cleaner.prev;
        }
        if (cleaner.prev != null) {
            cleaner.prev.next = cleaner.next;
        }
        cleaner.next = cleaner;
        cleaner.prev = cleaner;
        return true;
    }
    
    private Cleaner(final Object o, final Runnable thunk) {
        super(o, Cleaner.dummyQueue);
        this.next = null;
        this.prev = null;
        this.thunk = thunk;
    }
    
    public static Cleaner create(final Object o, final Runnable runnable) {
        if (runnable == null) {
            return null;
        }
        return add(new Cleaner(o, runnable));
    }
    
    public void clean() {
        if (!remove(this)) {
            return;
        }
        try {
            this.thunk.run();
        }
        catch (final Throwable t) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    if (System.err != null) {
                        new Error("Cleaner terminated abnormally", t).printStackTrace();
                    }
                    System.exit(1);
                    return null;
                }
            });
        }
    }
    
    static {
        dummyQueue = new ReferenceQueue<Object>();
        Cleaner.first = null;
    }
}
