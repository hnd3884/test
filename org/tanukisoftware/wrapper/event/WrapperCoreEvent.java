package org.tanukisoftware.wrapper.event;

public abstract class WrapperCoreEvent extends WrapperEvent
{
    protected WrapperCoreEvent() {
    }
    
    public long getFlags() {
        return super.getFlags() | 0xF000000000000000L;
    }
}
