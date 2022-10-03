package java.awt.event;

import java.awt.AWTEvent;
import java.util.EventListenerProxy;

public class AWTEventListenerProxy extends EventListenerProxy<AWTEventListener> implements AWTEventListener
{
    private final long eventMask;
    
    public AWTEventListenerProxy(final long eventMask, final AWTEventListener awtEventListener) {
        super(awtEventListener);
        this.eventMask = eventMask;
    }
    
    @Override
    public void eventDispatched(final AWTEvent awtEvent) {
        this.getListener().eventDispatched(awtEvent);
    }
    
    public long getEventMask() {
        return this.eventMask;
    }
}
