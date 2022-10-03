package sun.awt;

import java.awt.event.InvocationEvent;

public class PeerEvent extends InvocationEvent
{
    public static final long PRIORITY_EVENT = 1L;
    public static final long ULTIMATE_PRIORITY_EVENT = 2L;
    public static final long LOW_PRIORITY_EVENT = 4L;
    private long flags;
    
    public PeerEvent(final Object o, final Runnable runnable, final long n) {
        this(o, runnable, null, false, n);
    }
    
    public PeerEvent(final Object o, final Runnable runnable, final Object o2, final boolean b, final long flags) {
        super(o, runnable, o2, b);
        this.flags = flags;
    }
    
    public long getFlags() {
        return this.flags;
    }
    
    public PeerEvent coalesceEvents(final PeerEvent peerEvent) {
        return null;
    }
}
