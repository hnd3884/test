package javax.mail;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.EventListener;
import java.util.Vector;
import javax.mail.event.MailEvent;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.BlockingQueue;

class EventQueue implements Runnable
{
    private volatile BlockingQueue<QueueElement> q;
    private Executor executor;
    private static WeakHashMap<ClassLoader, EventQueue> appq;
    
    EventQueue(final Executor ex) {
        this.executor = ex;
    }
    
    synchronized void enqueue(final MailEvent event, final Vector<? extends EventListener> vector) {
        if (this.q == null) {
            this.q = new LinkedBlockingQueue<QueueElement>();
            if (this.executor != null) {
                this.executor.execute(this);
            }
            else {
                final Thread qThread = new Thread(this, "JavaMail-EventQueue");
                qThread.setDaemon(true);
                qThread.start();
            }
        }
        this.q.add(new QueueElement(event, vector));
    }
    
    synchronized void terminateQueue() {
        if (this.q != null) {
            final Vector<EventListener> dummyListeners = new Vector<EventListener>();
            dummyListeners.setSize(1);
            this.q.add(new QueueElement(new TerminatorEvent(), dummyListeners));
            this.q = null;
        }
    }
    
    static synchronized EventQueue getApplicationEventQueue(final Executor ex) {
        final ClassLoader cl = Session.getContextClassLoader();
        if (EventQueue.appq == null) {
            EventQueue.appq = new WeakHashMap<ClassLoader, EventQueue>();
        }
        EventQueue q = EventQueue.appq.get(cl);
        if (q == null) {
            q = new EventQueue(ex);
            EventQueue.appq.put(cl, q);
        }
        return q;
    }
    
    @Override
    public void run() {
        final BlockingQueue<QueueElement> bq = this.q;
        if (bq == null) {
            return;
        }
        Label_0010: {
            break Label_0010;
            try {
            Label_0087:
                while (true) {
                    QueueElement qe = bq.take();
                    MailEvent e = qe.event;
                    Vector<? extends EventListener> v = qe.vector;
                    for (int i = 0; i < v.size(); ++i) {
                        try {
                            e.dispatch(v.elementAt(i));
                        }
                        catch (final Throwable t) {
                            if (t instanceof InterruptedException) {
                                break Label_0087;
                            }
                        }
                    }
                    qe = null;
                    e = null;
                    v = null;
                }
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    static class TerminatorEvent extends MailEvent
    {
        private static final long serialVersionUID = -2481895000841664111L;
        
        TerminatorEvent() {
            super(new Object());
        }
        
        @Override
        public void dispatch(final Object listener) {
            Thread.currentThread().interrupt();
        }
    }
    
    static class QueueElement
    {
        MailEvent event;
        Vector<? extends EventListener> vector;
        
        QueueElement(final MailEvent event, final Vector<? extends EventListener> vector) {
            this.event = null;
            this.vector = null;
            this.event = event;
            this.vector = vector;
        }
    }
}
