package java.lang.ref;

import sun.misc.SharedSecrets;
import sun.misc.VM;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.JavaLangAccess;

final class Finalizer extends FinalReference<Object>
{
    private static ReferenceQueue<Object> queue;
    private static Finalizer unfinalized;
    private static final Object lock;
    private Finalizer next;
    private Finalizer prev;
    
    private boolean hasBeenFinalized() {
        return this.next == this;
    }
    
    private void add() {
        synchronized (Finalizer.lock) {
            if (Finalizer.unfinalized != null) {
                this.next = Finalizer.unfinalized;
                Finalizer.unfinalized.prev = this;
            }
            Finalizer.unfinalized = this;
        }
    }
    
    private void remove() {
        synchronized (Finalizer.lock) {
            if (Finalizer.unfinalized == this) {
                if (this.next != null) {
                    Finalizer.unfinalized = this.next;
                }
                else {
                    Finalizer.unfinalized = this.prev;
                }
            }
            if (this.next != null) {
                this.next.prev = this.prev;
            }
            if (this.prev != null) {
                this.prev.next = this.next;
            }
            this.next = this;
            this.prev = this;
        }
    }
    
    private Finalizer(final Object o) {
        super(o, Finalizer.queue);
        this.next = null;
        this.prev = null;
        this.add();
    }
    
    static ReferenceQueue<Object> getQueue() {
        return Finalizer.queue;
    }
    
    static void register(final Object o) {
        new Finalizer(o);
    }
    
    private void runFinalizer(final JavaLangAccess javaLangAccess) {
        synchronized (this) {
            if (this.hasBeenFinalized()) {
                return;
            }
            this.remove();
        }
        try {
            final Object value = this.get();
            if (value != null && !(value instanceof Enum)) {
                javaLangAccess.invokeFinalize(value);
            }
        }
        catch (final Throwable t) {}
        super.clear();
    }
    
    private static void forkSecondaryFinalizer(final Runnable runnable) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                ThreadGroup threadGroup2;
                ThreadGroup threadGroup;
                for (threadGroup = (threadGroup2 = Thread.currentThread().getThreadGroup()); threadGroup2 != null; threadGroup2 = threadGroup.getParent()) {
                    threadGroup = threadGroup2;
                }
                final Thread thread = new Thread(threadGroup, runnable, "Secondary finalizer");
                thread.start();
                try {
                    thread.join();
                }
                catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                return null;
            }
        });
    }
    
    static void runFinalization() {
        if (!VM.isBooted()) {
            return;
        }
        forkSecondaryFinalizer(new Runnable() {
            private volatile boolean running;
            
            @Override
            public void run() {
                if (this.running) {
                    return;
                }
                final JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
                this.running = true;
                while (true) {
                    final Finalizer finalizer = (Finalizer)Finalizer.queue.poll();
                    if (finalizer == null) {
                        break;
                    }
                    finalizer.runFinalizer(javaLangAccess);
                }
            }
        });
    }
    
    static void runAllFinalizers() {
        if (!VM.isBooted()) {
            return;
        }
        forkSecondaryFinalizer(new Runnable() {
            private volatile boolean running;
            
            @Override
            public void run() {
                if (this.running) {
                    return;
                }
                final JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
                this.running = true;
                while (true) {
                    final Finalizer access$300;
                    synchronized (Finalizer.lock) {
                        access$300 = Finalizer.unfinalized;
                        if (access$300 == null) {
                            break;
                        }
                        Finalizer.unfinalized = access$300.next;
                    }
                    access$300.runFinalizer(javaLangAccess);
                }
            }
        });
    }
    
    static {
        Finalizer.queue = new ReferenceQueue<Object>();
        Finalizer.unfinalized = null;
        lock = new Object();
        ThreadGroup threadGroup2;
        ThreadGroup threadGroup;
        for (threadGroup = (threadGroup2 = Thread.currentThread().getThreadGroup()); threadGroup2 != null; threadGroup2 = threadGroup.getParent()) {
            threadGroup = threadGroup2;
        }
        final FinalizerThread finalizerThread = new FinalizerThread(threadGroup);
        finalizerThread.setPriority(8);
        finalizerThread.setDaemon(true);
        finalizerThread.start();
    }
    
    private static class FinalizerThread extends Thread
    {
        private volatile boolean running;
        
        FinalizerThread(final ThreadGroup threadGroup) {
            super(threadGroup, "Finalizer");
        }
        
        @Override
        public void run() {
            if (this.running) {
                return;
            }
            while (!VM.isBooted()) {
                try {
                    VM.awaitBooted();
                }
                catch (final InterruptedException ex) {}
            }
            final JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
            this.running = true;
        Label_0033_Outer:
            while (true) {
                while (true) {
                    try {
                        while (true) {
                            ((Finalizer)Finalizer.queue.remove()).runFinalizer(javaLangAccess);
                        }
                    }
                    catch (final InterruptedException ex2) {
                        continue Label_0033_Outer;
                    }
                    continue;
                }
            }
        }
    }
}
