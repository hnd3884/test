package org.tanukisoftware.wrapper.event;

public interface WrapperEventListener
{
    public static final long EVENT_FLAG_SERVICE = 1L;
    public static final long EVENT_FLAG_CONTROL = 2L;
    public static final long EVENT_FLAG_LOGGING = 4L;
    public static final long EVENT_FLAG_REMOTE_CONTROL = 8L;
    public static final long EVENT_FLAG_CORE = -1152921504606846976L;
    
    void fired(final WrapperEvent p0);
}
