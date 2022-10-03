package java.lang;

import java.util.Iterator;
import java.util.Set;
import java.util.IdentityHashMap;

class ApplicationShutdownHooks
{
    private static IdentityHashMap<Thread, Thread> hooks;
    
    private ApplicationShutdownHooks() {
    }
    
    static synchronized void add(final Thread thread) {
        if (ApplicationShutdownHooks.hooks == null) {
            throw new IllegalStateException("Shutdown in progress");
        }
        if (thread.isAlive()) {
            throw new IllegalArgumentException("Hook already running");
        }
        if (ApplicationShutdownHooks.hooks.containsKey(thread)) {
            throw new IllegalArgumentException("Hook previously registered");
        }
        ApplicationShutdownHooks.hooks.put(thread, thread);
    }
    
    static synchronized boolean remove(final Thread thread) {
        if (ApplicationShutdownHooks.hooks == null) {
            throw new IllegalStateException("Shutdown in progress");
        }
        if (thread == null) {
            throw new NullPointerException();
        }
        return ApplicationShutdownHooks.hooks.remove(thread) != null;
    }
    
    static void runHooks() {
        final Set<Thread> keySet;
        synchronized (ApplicationShutdownHooks.class) {
            keySet = ApplicationShutdownHooks.hooks.keySet();
            ApplicationShutdownHooks.hooks = null;
        }
        final Iterator<Object> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            iterator.next().start();
        }
        for (final Thread thread : keySet) {
            while (true) {
                try {
                    thread.join();
                }
                catch (final InterruptedException ex) {
                    continue;
                }
                break;
            }
        }
    }
    
    static {
        try {
            Shutdown.add(1, false, new Runnable() {
                @Override
                public void run() {
                    ApplicationShutdownHooks.runHooks();
                }
            });
            ApplicationShutdownHooks.hooks = new IdentityHashMap<Thread, Thread>();
        }
        catch (final IllegalStateException ex) {
            ApplicationShutdownHooks.hooks = null;
        }
    }
}
