package org.tanukisoftware.wrapper.event;

public abstract class WrapperRemoteControlEvent extends WrapperEvent
{
    public long getFlags() {
        return super.getFlags() | 0x8L;
    }
}
