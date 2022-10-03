package java.awt;

import sun.awt.PeerEvent;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.TimerTask;
import java.util.Timer;
import sun.util.logging.PlatformLogger;

class WaitDispatchSupport implements SecondaryLoop
{
    private static final PlatformLogger log;
    private EventDispatchThread dispatchThread;
    private EventFilter filter;
    private volatile Conditional extCondition;
    private volatile Conditional condition;
    private long interval;
    private static Timer timer;
    private TimerTask timerTask;
    private AtomicBoolean keepBlockingEDT;
    private AtomicBoolean keepBlockingCT;
    private AtomicBoolean afterExit;
    private final Runnable wakingRunnable;
    
    private static synchronized void initializeTimer() {
        if (WaitDispatchSupport.timer == null) {
            WaitDispatchSupport.timer = new Timer("AWT-WaitDispatchSupport-Timer", true);
        }
    }
    
    public WaitDispatchSupport(final EventDispatchThread eventDispatchThread) {
        this(eventDispatchThread, null);
    }
    
    public WaitDispatchSupport(final EventDispatchThread dispatchThread, final Conditional extCondition) {
        this.keepBlockingEDT = new AtomicBoolean(false);
        this.keepBlockingCT = new AtomicBoolean(false);
        this.afterExit = new AtomicBoolean(false);
        this.wakingRunnable = new Runnable() {
            @Override
            public void run() {
                WaitDispatchSupport.log.fine("Wake up EDT");
                synchronized (getTreeLock()) {
                    WaitDispatchSupport.this.keepBlockingCT.set(false);
                    getTreeLock().notifyAll();
                }
                WaitDispatchSupport.log.fine("Wake up EDT done");
            }
        };
        if (dispatchThread == null) {
            throw new IllegalArgumentException("The dispatchThread can not be null");
        }
        this.dispatchThread = dispatchThread;
        this.extCondition = extCondition;
        this.condition = new Conditional() {
            @Override
            public boolean evaluate() {
                if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINEST)) {
                    WaitDispatchSupport.log.finest("evaluate(): blockingEDT=" + WaitDispatchSupport.this.keepBlockingEDT.get() + ", blockingCT=" + WaitDispatchSupport.this.keepBlockingCT.get());
                }
                final boolean b = WaitDispatchSupport.this.extCondition == null || WaitDispatchSupport.this.extCondition.evaluate();
                if (!WaitDispatchSupport.this.keepBlockingEDT.get() || !b) {
                    if (WaitDispatchSupport.this.timerTask != null) {
                        WaitDispatchSupport.this.timerTask.cancel();
                        WaitDispatchSupport.this.timerTask = null;
                    }
                    return false;
                }
                return true;
            }
        };
    }
    
    public WaitDispatchSupport(final EventDispatchThread eventDispatchThread, final Conditional conditional, final EventFilter filter, final long interval) {
        this(eventDispatchThread, conditional);
        this.filter = filter;
        if (interval < 0L) {
            throw new IllegalArgumentException("The interval value must be >= 0");
        }
        this.interval = interval;
        if (interval != 0L) {
            initializeTimer();
        }
    }
    
    @Override
    public boolean enter() {
        if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINE)) {
            WaitDispatchSupport.log.fine("enter(): blockingEDT=" + this.keepBlockingEDT.get() + ", blockingCT=" + this.keepBlockingCT.get());
        }
        if (!this.keepBlockingEDT.compareAndSet(false, true)) {
            WaitDispatchSupport.log.fine("The secondary loop is already running, aborting");
            return false;
        }
        try {
            if (this.afterExit.get()) {
                WaitDispatchSupport.log.fine("Exit was called already, aborting");
                return false;
            }
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    WaitDispatchSupport.log.fine("Starting a new event pump");
                    if (WaitDispatchSupport.this.filter == null) {
                        WaitDispatchSupport.this.dispatchThread.pumpEvents(WaitDispatchSupport.this.condition);
                    }
                    else {
                        WaitDispatchSupport.this.dispatchThread.pumpEventsForFilter(WaitDispatchSupport.this.condition, WaitDispatchSupport.this.filter);
                    }
                }
            };
            final Thread currentThread = Thread.currentThread();
            if (currentThread == this.dispatchThread) {
                if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINEST)) {
                    WaitDispatchSupport.log.finest("On dispatch thread: " + this.dispatchThread);
                }
                if (this.interval != 0L) {
                    if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINEST)) {
                        WaitDispatchSupport.log.finest("scheduling the timer for " + this.interval + " ms");
                    }
                    WaitDispatchSupport.timer.schedule(this.timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (WaitDispatchSupport.this.keepBlockingEDT.compareAndSet(true, false)) {
                                WaitDispatchSupport.this.wakeupEDT();
                            }
                        }
                    }, this.interval);
                }
                final SequencedEvent currentSequencedEvent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
                if (currentSequencedEvent != null) {
                    if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINE)) {
                        WaitDispatchSupport.log.fine("Dispose current SequencedEvent: " + currentSequencedEvent);
                    }
                    currentSequencedEvent.dispose();
                }
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        runnable.run();
                        return null;
                    }
                });
            }
            else {
                if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINEST)) {
                    WaitDispatchSupport.log.finest("On non-dispatch thread: " + currentThread);
                }
                this.keepBlockingCT.set(true);
                synchronized (getTreeLock()) {
                    if (this.afterExit.get()) {
                        return false;
                    }
                    if (this.filter != null) {
                        this.dispatchThread.addEventFilter(this.filter);
                    }
                    try {
                        this.dispatchThread.getEventQueue().postEvent(new PeerEvent(this, runnable, 1L));
                        if (this.interval > 0L) {
                            final long currentTimeMillis = System.currentTimeMillis();
                            while (this.keepBlockingCT.get() && (this.extCondition == null || this.extCondition.evaluate()) && currentTimeMillis + this.interval > System.currentTimeMillis()) {
                                getTreeLock().wait(this.interval);
                            }
                        }
                        else {
                            while (this.keepBlockingCT.get() && (this.extCondition == null || this.extCondition.evaluate())) {
                                getTreeLock().wait();
                            }
                        }
                        if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINE)) {
                            WaitDispatchSupport.log.fine("waitDone " + this.keepBlockingEDT.get() + " " + this.keepBlockingCT.get());
                        }
                    }
                    catch (final InterruptedException ex) {
                        if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINE)) {
                            WaitDispatchSupport.log.fine("Exception caught while waiting: " + ex);
                        }
                    }
                    finally {
                        if (this.filter != null) {
                            this.dispatchThread.removeEventFilter(this.filter);
                        }
                    }
                }
            }
            return true;
        }
        finally {
            this.keepBlockingEDT.set(false);
            this.keepBlockingCT.set(false);
            this.afterExit.set(false);
        }
    }
    
    @Override
    public boolean exit() {
        if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINE)) {
            WaitDispatchSupport.log.fine("exit(): blockingEDT=" + this.keepBlockingEDT.get() + ", blockingCT=" + this.keepBlockingCT.get());
        }
        this.afterExit.set(true);
        if (this.keepBlockingEDT.getAndSet(false)) {
            this.wakeupEDT();
            return true;
        }
        return false;
    }
    
    private static final Object getTreeLock() {
        return Component.LOCK;
    }
    
    private void wakeupEDT() {
        if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINEST)) {
            WaitDispatchSupport.log.finest("wakeupEDT(): EDT == " + this.dispatchThread);
        }
        this.dispatchThread.getEventQueue().postEvent(new PeerEvent(this, this.wakingRunnable, 1L));
    }
    
    static {
        log = PlatformLogger.getLogger("java.awt.event.WaitDispatchSupport");
    }
}
