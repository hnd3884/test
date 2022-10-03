package org.tanukisoftware.wrapper.event;

public abstract class WrapperConsumableEvent extends WrapperEvent
{
    private boolean m_consumed;
    
    public void consume() {
        this.m_consumed = true;
    }
    
    public boolean isConsumed() {
        return this.m_consumed;
    }
}
