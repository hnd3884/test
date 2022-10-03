package sun.print;

import javax.print.event.PrintServiceAttributeEvent;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;
import javax.print.attribute.PrintServiceAttributeSet;
import java.util.Vector;
import javax.print.PrintService;

class ServiceNotifier extends Thread
{
    private PrintService service;
    private Vector listeners;
    private boolean stop;
    private PrintServiceAttributeSet lastSet;
    
    ServiceNotifier(final PrintService service) {
        super(service.getName() + " notifier");
        this.stop = false;
        this.service = service;
        this.listeners = new Vector();
        try {
            this.setPriority(4);
            this.setDaemon(true);
            this.start();
        }
        catch (final SecurityException ex) {}
    }
    
    void addListener(final PrintServiceAttributeListener printServiceAttributeListener) {
        synchronized (this) {
            if (printServiceAttributeListener == null || this.listeners == null) {
                return;
            }
            this.listeners.add(printServiceAttributeListener);
        }
    }
    
    void removeListener(final PrintServiceAttributeListener printServiceAttributeListener) {
        synchronized (this) {
            if (printServiceAttributeListener == null || this.listeners == null) {
                return;
            }
            this.listeners.remove(printServiceAttributeListener);
        }
    }
    
    boolean isEmpty() {
        return this.listeners == null || this.listeners.isEmpty();
    }
    
    void stopNotifier() {
        this.stop = true;
    }
    
    void wake() {
        try {
            this.interrupt();
        }
        catch (final SecurityException ex) {}
    }
    
    @Override
    public void run() {
        final long n = 15000L;
        long n2 = 2000L;
        while (!this.stop) {
            try {
                Thread.sleep(n2);
            }
            catch (final InterruptedException ex) {}
            synchronized (this) {
                if (this.listeners == null) {
                    continue;
                }
                final long currentTimeMillis = System.currentTimeMillis();
                if (this.listeners != null) {
                    PrintServiceAttributeSet set;
                    if (this.service instanceof AttributeUpdater) {
                        set = ((AttributeUpdater)this.service).getUpdatedAttributes();
                    }
                    else {
                        set = this.service.getAttributes();
                    }
                    if (set != null && !set.isEmpty()) {
                        for (int i = 0; i < this.listeners.size(); ++i) {
                            ((PrintServiceAttributeListener)this.listeners.elementAt(i)).attributeUpdate(new PrintServiceAttributeEvent(this.service, new HashPrintServiceAttributeSet(set)));
                        }
                    }
                }
                n2 = (System.currentTimeMillis() - currentTimeMillis) * 10L;
                if (n2 >= n) {
                    continue;
                }
                n2 = n;
            }
        }
    }
}
