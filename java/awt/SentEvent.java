package java.awt;

import sun.awt.SunToolkit;
import sun.awt.AppContext;

class SentEvent extends AWTEvent implements ActiveEvent
{
    private static final long serialVersionUID = -383615247028828931L;
    static final int ID = 1007;
    boolean dispatched;
    private AWTEvent nested;
    private AppContext toNotify;
    
    SentEvent() {
        this((AWTEvent)null);
    }
    
    SentEvent(final AWTEvent awtEvent) {
        this(awtEvent, null);
    }
    
    SentEvent(final AWTEvent nested, final AppContext toNotify) {
        super((nested != null) ? nested.getSource() : Toolkit.getDefaultToolkit(), 1007);
        this.nested = nested;
        this.toNotify = toNotify;
    }
    
    @Override
    public void dispatch() {
        try {
            if (this.nested != null) {
                Toolkit.getEventQueue().dispatchEvent(this.nested);
            }
        }
        finally {
            this.dispatched = true;
            if (this.toNotify != null) {
                SunToolkit.postEvent(this.toNotify, new SentEvent());
            }
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
    
    final void dispose() {
        this.dispatched = true;
        if (this.toNotify != null) {
            SunToolkit.postEvent(this.toNotify, new SentEvent());
        }
        synchronized (this) {
            this.notifyAll();
        }
    }
}
