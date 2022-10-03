package sun.awt;

import java.util.Iterator;
import sun.util.logging.PlatformLogger;
import sun.misc.ThreadGroupUtils;
import java.awt.AWTEvent;
import java.security.AccessController;
import java.util.IdentityHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AWTAutoShutdown implements Runnable
{
    private static final AWTAutoShutdown theInstance;
    private final Object mainLock;
    private final Object activationLock;
    private final Set<Thread> busyThreadSet;
    private boolean toolkitThreadBusy;
    private final Map<Object, Object> peerMap;
    private Thread blockerThread;
    private boolean timeoutPassed;
    private static final int SAFETY_TIMEOUT = 1000;
    
    private AWTAutoShutdown() {
        this.mainLock = new Object();
        this.activationLock = new Object();
        this.busyThreadSet = new HashSet<Thread>(7);
        this.toolkitThreadBusy = false;
        this.peerMap = new IdentityHashMap<Object, Object>();
        this.blockerThread = null;
        this.timeoutPassed = false;
    }
    
    public static AWTAutoShutdown getInstance() {
        return AWTAutoShutdown.theInstance;
    }
    
    public static void notifyToolkitThreadBusy() {
        getInstance().setToolkitBusy(true);
    }
    
    public static void notifyToolkitThreadFree() {
        getInstance().setToolkitBusy(false);
    }
    
    public void notifyThreadBusy(final Thread thread) {
        if (thread == null) {
            return;
        }
        synchronized (this.activationLock) {
            synchronized (this.mainLock) {
                if (this.blockerThread == null) {
                    this.activateBlockerThread();
                }
                else if (this.isReadyToShutdown()) {
                    this.mainLock.notifyAll();
                    this.timeoutPassed = false;
                }
                this.busyThreadSet.add(thread);
            }
        }
    }
    
    public void notifyThreadFree(final Thread thread) {
        if (thread == null) {
            return;
        }
        synchronized (this.activationLock) {
            synchronized (this.mainLock) {
                this.busyThreadSet.remove(thread);
                if (this.isReadyToShutdown()) {
                    this.mainLock.notifyAll();
                    this.timeoutPassed = false;
                }
            }
        }
    }
    
    void notifyPeerMapUpdated() {
        synchronized (this.activationLock) {
            synchronized (this.mainLock) {
                if (!this.isReadyToShutdown() && this.blockerThread == null) {
                    AccessController.doPrivileged(() -> {
                        this.activateBlockerThread();
                        return null;
                    });
                }
                else {
                    this.mainLock.notifyAll();
                    this.timeoutPassed = false;
                }
            }
        }
    }
    
    private boolean isReadyToShutdown() {
        return !this.toolkitThreadBusy && this.peerMap.isEmpty() && this.busyThreadSet.isEmpty();
    }
    
    private void setToolkitBusy(final boolean b) {
        if (b != this.toolkitThreadBusy) {
            synchronized (this.activationLock) {
                synchronized (this.mainLock) {
                    if (b != this.toolkitThreadBusy) {
                        if (b) {
                            if (this.blockerThread == null) {
                                this.activateBlockerThread();
                            }
                            else if (this.isReadyToShutdown()) {
                                this.mainLock.notifyAll();
                                this.timeoutPassed = false;
                            }
                            this.toolkitThreadBusy = b;
                        }
                        else {
                            this.toolkitThreadBusy = b;
                            if (this.isReadyToShutdown()) {
                                this.mainLock.notifyAll();
                                this.timeoutPassed = false;
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void run() {
        final Thread currentThread = Thread.currentThread();
        boolean b = false;
        synchronized (this.mainLock) {
            try {
                this.mainLock.notifyAll();
                while (this.blockerThread == currentThread) {
                    this.mainLock.wait();
                    this.timeoutPassed = false;
                    while (this.isReadyToShutdown()) {
                        if (this.timeoutPassed) {
                            this.timeoutPassed = false;
                            this.blockerThread = null;
                            break;
                        }
                        this.timeoutPassed = true;
                        this.mainLock.wait(1000L);
                    }
                }
            }
            catch (final InterruptedException ex) {
                b = true;
            }
            finally {
                if (this.blockerThread == currentThread) {
                    this.blockerThread = null;
                }
            }
        }
        if (!b) {
            AppContext.stopEventDispatchThreads();
        }
    }
    
    static AWTEvent getShutdownEvent() {
        return new AWTEvent(getInstance(), 0) {};
    }
    
    private void activateBlockerThread() {
        final Thread blockerThread = new Thread(ThreadGroupUtils.getRootThreadGroup(), this, "AWT-Shutdown");
        blockerThread.setContextClassLoader(null);
        blockerThread.setDaemon(false);
        (this.blockerThread = blockerThread).start();
        try {
            this.mainLock.wait();
        }
        catch (final InterruptedException ex) {
            System.err.println("AWT blocker activation interrupted:");
            ex.printStackTrace();
        }
    }
    
    final void registerPeer(final Object o, final Object o2) {
        synchronized (this.activationLock) {
            synchronized (this.mainLock) {
                this.peerMap.put(o, o2);
                this.notifyPeerMapUpdated();
            }
        }
    }
    
    final void unregisterPeer(final Object o, final Object o2) {
        synchronized (this.activationLock) {
            synchronized (this.mainLock) {
                if (this.peerMap.get(o) == o2) {
                    this.peerMap.remove(o);
                    this.notifyPeerMapUpdated();
                }
            }
        }
    }
    
    final Object getPeer(final Object o) {
        synchronized (this.activationLock) {
            synchronized (this.mainLock) {
                return this.peerMap.get(o);
            }
        }
    }
    
    final void dumpPeers(final PlatformLogger platformLogger) {
        if (platformLogger.isLoggable(PlatformLogger.Level.FINE)) {
            synchronized (this.activationLock) {
                synchronized (this.mainLock) {
                    platformLogger.fine("Mapped peers:");
                    for (final Object next : this.peerMap.keySet()) {
                        platformLogger.fine(next + "->" + this.peerMap.get(next));
                    }
                }
            }
        }
    }
    
    static {
        theInstance = new AWTAutoShutdown();
    }
}
