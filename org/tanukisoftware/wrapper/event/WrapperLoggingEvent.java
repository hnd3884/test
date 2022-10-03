package org.tanukisoftware.wrapper.event;

public abstract class WrapperLoggingEvent extends WrapperEvent
{
    protected WrapperLoggingEvent() {
    }
    
    public long getFlags() {
        return super.getFlags() | 0x4L;
    }
}
