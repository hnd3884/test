package java.lang;

class Shutdown
{
    private static final int RUNNING = 0;
    private static final int HOOKS = 1;
    private static final int FINALIZERS = 2;
    private static int state;
    private static boolean runFinalizersOnExit;
    private static final int MAX_SYSTEM_HOOKS = 10;
    private static final Runnable[] hooks;
    private static int currentRunningHook;
    private static Object lock;
    private static Object haltLock;
    
    static void setRunFinalizersOnExit(final boolean runFinalizersOnExit) {
        synchronized (Shutdown.lock) {
            Shutdown.runFinalizersOnExit = runFinalizersOnExit;
        }
    }
    
    static void add(final int n, final boolean b, final Runnable runnable) {
        synchronized (Shutdown.lock) {
            if (Shutdown.hooks[n] != null) {
                throw new InternalError("Shutdown hook at slot " + n + " already registered");
            }
            if (!b) {
                if (Shutdown.state > 0) {
                    throw new IllegalStateException("Shutdown in progress");
                }
            }
            else if (Shutdown.state > 1 || (Shutdown.state == 1 && n <= Shutdown.currentRunningHook)) {
                throw new IllegalStateException("Shutdown in progress");
            }
            Shutdown.hooks[n] = runnable;
        }
    }
    
    private static void runHooks() {
        for (int i = 0; i < 10; ++i) {
            try {
                final Runnable runnable;
                synchronized (Shutdown.lock) {
                    Shutdown.currentRunningHook = i;
                    runnable = Shutdown.hooks[i];
                }
                if (runnable != null) {
                    runnable.run();
                }
            }
            catch (final Throwable runnable) {
                if (runnable instanceof ThreadDeath) {
                    throw (ThreadDeath)runnable;
                }
            }
        }
    }
    
    static native void beforeHalt();
    
    static void halt(final int n) {
        synchronized (Shutdown.haltLock) {
            halt0(n);
        }
    }
    
    static native void halt0(final int p0);
    
    private static native void runAllFinalizers();
    
    private static void sequence() {
        synchronized (Shutdown.lock) {
            if (Shutdown.state != 1) {
                return;
            }
        }
        runHooks();
        final boolean runFinalizersOnExit;
        synchronized (Shutdown.lock) {
            Shutdown.state = 2;
            runFinalizersOnExit = Shutdown.runFinalizersOnExit;
        }
        if (runFinalizersOnExit) {
            runAllFinalizers();
        }
    }
    
    static void exit(final int n) {
        boolean runFinalizersOnExit = false;
        synchronized (Shutdown.lock) {
            if (n != 0) {
                Shutdown.runFinalizersOnExit = false;
            }
            switch (Shutdown.state) {
                case 0: {
                    Shutdown.state = 1;
                }
                case 2: {
                    if (n != 0) {
                        halt(n);
                        break;
                    }
                    runFinalizersOnExit = Shutdown.runFinalizersOnExit;
                    break;
                }
            }
        }
        if (runFinalizersOnExit) {
            runAllFinalizers();
            halt(n);
        }
        synchronized (Shutdown.class) {
            beforeHalt();
            sequence();
            halt(n);
        }
    }
    
    static void shutdown() {
        synchronized (Shutdown.lock) {
            switch (Shutdown.state) {
                case 0: {
                    Shutdown.state = 1;
                    break;
                }
            }
        }
        synchronized (Shutdown.class) {
            sequence();
        }
    }
    
    static {
        Shutdown.state = 0;
        Shutdown.runFinalizersOnExit = false;
        hooks = new Runnable[10];
        Shutdown.currentRunningHook = 0;
        Shutdown.lock = new Lock();
        Shutdown.haltLock = new Lock();
    }
    
    private static class Lock
    {
    }
}
