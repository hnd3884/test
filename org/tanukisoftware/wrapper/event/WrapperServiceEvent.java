package org.tanukisoftware.wrapper.event;

public abstract class WrapperServiceEvent extends WrapperEvent
{
    protected WrapperServiceEvent() {
    }
    
    public long getFlags() {
        return super.getFlags() | 0x1L;
    }
}
