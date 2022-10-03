package javax.swing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivilegedAction;
import java.util.EventListener;
import java.awt.event.ActionEvent;
import java.security.AccessController;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.ActionListener;
import java.security.AccessControlContext;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.event.EventListenerList;
import java.io.Serializable;

public class Timer implements Serializable
{
    protected EventListenerList listenerList;
    private final transient AtomicBoolean notify;
    private volatile int initialDelay;
    private volatile int delay;
    private volatile boolean repeats;
    private volatile boolean coalesce;
    private final transient Runnable doPostEvent;
    private static volatile boolean logTimers;
    private final transient Lock lock;
    transient TimerQueue.DelayedTimer delayedTimer;
    private volatile String actionCommand;
    private transient volatile AccessControlContext acc;
    
    public Timer(final int n, final ActionListener actionListener) {
        this.listenerList = new EventListenerList();
        this.notify = new AtomicBoolean(false);
        this.repeats = true;
        this.coalesce = true;
        this.lock = new ReentrantLock();
        this.delayedTimer = null;
        this.acc = AccessController.getContext();
        this.delay = n;
        this.initialDelay = n;
        this.doPostEvent = new DoPostEvent();
        if (actionListener != null) {
            this.addActionListener(actionListener);
        }
    }
    
    final AccessControlContext getAccessControlContext() {
        if (this.acc == null) {
            throw new SecurityException("Timer is missing AccessControlContext");
        }
        return this.acc;
    }
    
    public void addActionListener(final ActionListener actionListener) {
        this.listenerList.add(ActionListener.class, actionListener);
    }
    
    public void removeActionListener(final ActionListener actionListener) {
        this.listenerList.remove(ActionListener.class, actionListener);
    }
    
    public ActionListener[] getActionListeners() {
        return this.listenerList.getListeners(ActionListener.class);
    }
    
    protected void fireActionPerformed(final ActionEvent actionEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ActionListener.class) {
                ((ActionListener)listenerList[i + 1]).actionPerformed(actionEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
    
    private TimerQueue timerQueue() {
        return TimerQueue.sharedInstance();
    }
    
    public static void setLogTimers(final boolean logTimers) {
        Timer.logTimers = logTimers;
    }
    
    public static boolean getLogTimers() {
        return Timer.logTimers;
    }
    
    public void setDelay(final int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Invalid delay: " + delay);
        }
        this.delay = delay;
    }
    
    public int getDelay() {
        return this.delay;
    }
    
    public void setInitialDelay(final int initialDelay) {
        if (initialDelay < 0) {
            throw new IllegalArgumentException("Invalid initial delay: " + initialDelay);
        }
        this.initialDelay = initialDelay;
    }
    
    public int getInitialDelay() {
        return this.initialDelay;
    }
    
    public void setRepeats(final boolean repeats) {
        this.repeats = repeats;
    }
    
    public boolean isRepeats() {
        return this.repeats;
    }
    
    public void setCoalesce(final boolean coalesce) {
        final boolean coalesce2 = this.coalesce;
        this.coalesce = coalesce;
        if (!coalesce2 && this.coalesce) {
            this.cancelEvent();
        }
    }
    
    public boolean isCoalesce() {
        return this.coalesce;
    }
    
    public void setActionCommand(final String actionCommand) {
        this.actionCommand = actionCommand;
    }
    
    public String getActionCommand() {
        return this.actionCommand;
    }
    
    public void start() {
        this.timerQueue().addTimer(this, this.getInitialDelay());
    }
    
    public boolean isRunning() {
        return this.timerQueue().containsTimer(this);
    }
    
    public void stop() {
        this.getLock().lock();
        try {
            this.cancelEvent();
            this.timerQueue().removeTimer(this);
        }
        finally {
            this.getLock().unlock();
        }
    }
    
    public void restart() {
        this.getLock().lock();
        try {
            this.stop();
            this.start();
        }
        finally {
            this.getLock().unlock();
        }
    }
    
    void cancelEvent() {
        this.notify.set(false);
    }
    
    void post() {
        if (this.notify.compareAndSet(false, true) || !this.coalesce) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    SwingUtilities.invokeLater(Timer.this.doPostEvent);
                    return null;
                }
            }, this.getAccessControlContext());
        }
    }
    
    Lock getLock() {
        return this.lock;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        this.acc = AccessController.getContext();
        objectInputStream.defaultReadObject();
    }
    
    private Object readResolve() {
        final Timer timer = new Timer(this.getDelay(), null);
        timer.listenerList = this.listenerList;
        timer.initialDelay = this.initialDelay;
        timer.delay = this.delay;
        timer.repeats = this.repeats;
        timer.coalesce = this.coalesce;
        timer.actionCommand = this.actionCommand;
        return timer;
    }
    
    class DoPostEvent implements Runnable
    {
        @Override
        public void run() {
            if (Timer.logTimers) {
                System.out.println("Timer ringing: " + Timer.this);
            }
            if (Timer.this.notify.get()) {
                Timer.this.fireActionPerformed(new ActionEvent(Timer.this, 0, Timer.this.getActionCommand(), System.currentTimeMillis(), 0));
                if (Timer.this.coalesce) {
                    Timer.this.cancelEvent();
                }
            }
        }
        
        Timer getTimer() {
            return Timer.this;
        }
    }
}
