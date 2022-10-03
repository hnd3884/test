package org.tanukisoftware.wrapper.event;

public abstract class WrapperTickEvent extends WrapperCoreEvent
{
    protected WrapperTickEvent() {
    }
    
    public abstract int getTicks();
    
    public abstract int getTickOffset();
    
    public String toString() {
        return "WrapperTickEvent[ticks=" + this.getTicks() + ", tickOffset=" + this.getTickOffset() + "]";
    }
}
