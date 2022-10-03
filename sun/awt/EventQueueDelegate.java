package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;

public class EventQueueDelegate
{
    private static final Object EVENT_QUEUE_DELEGATE_KEY;
    
    public static void setDelegate(final Delegate delegate) {
        AppContext.getAppContext().put(EventQueueDelegate.EVENT_QUEUE_DELEGATE_KEY, delegate);
    }
    
    public static Delegate getDelegate() {
        return (Delegate)AppContext.getAppContext().get(EventQueueDelegate.EVENT_QUEUE_DELEGATE_KEY);
    }
    
    static {
        EVENT_QUEUE_DELEGATE_KEY = new StringBuilder("EventQueueDelegate.Delegate");
    }
    
    public interface Delegate
    {
        AWTEvent getNextEvent(final EventQueue p0) throws InterruptedException;
        
        Object beforeDispatch(final AWTEvent p0) throws InterruptedException;
        
        void afterDispatch(final AWTEvent p0, final Object p1) throws InterruptedException;
    }
}
