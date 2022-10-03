package javax.swing.text;

import sun.awt.AppContext;
import java.util.Vector;

public class LayoutQueue
{
    private static final Object DEFAULT_QUEUE;
    private Vector<Runnable> tasks;
    private Thread worker;
    
    public LayoutQueue() {
        this.tasks = new Vector<Runnable>();
    }
    
    public static LayoutQueue getDefaultQueue() {
        final AppContext appContext = AppContext.getAppContext();
        synchronized (LayoutQueue.DEFAULT_QUEUE) {
            LayoutQueue layoutQueue = (LayoutQueue)appContext.get(LayoutQueue.DEFAULT_QUEUE);
            if (layoutQueue == null) {
                layoutQueue = new LayoutQueue();
                appContext.put(LayoutQueue.DEFAULT_QUEUE, layoutQueue);
            }
            return layoutQueue;
        }
    }
    
    public static void setDefaultQueue(final LayoutQueue layoutQueue) {
        synchronized (LayoutQueue.DEFAULT_QUEUE) {
            AppContext.getAppContext().put(LayoutQueue.DEFAULT_QUEUE, layoutQueue);
        }
    }
    
    public synchronized void addTask(final Runnable runnable) {
        if (this.worker == null) {
            (this.worker = new LayoutThread()).start();
        }
        this.tasks.addElement(runnable);
        this.notifyAll();
    }
    
    protected synchronized Runnable waitForWork() {
        while (this.tasks.size() == 0) {
            try {
                this.wait();
                continue;
            }
            catch (final InterruptedException ex) {
                return null;
            }
            break;
        }
        final Runnable runnable = this.tasks.firstElement();
        this.tasks.removeElementAt(0);
        return runnable;
    }
    
    static {
        DEFAULT_QUEUE = new Object();
    }
    
    class LayoutThread extends Thread
    {
        LayoutThread() {
            super("text-layout");
            this.setPriority(1);
        }
        
        @Override
        public void run() {
            Runnable waitForWork;
            do {
                waitForWork = LayoutQueue.this.waitForWork();
                if (waitForWork != null) {
                    waitForWork.run();
                }
            } while (waitForWork != null);
        }
    }
}
