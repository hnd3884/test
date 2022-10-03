package org.tanukisoftware.wrapper.event;

public class WrapperControlEvent extends WrapperConsumableEvent
{
    private static final long serialVersionUID = -7033261694452001713L;
    private int m_controlEvent;
    private String m_controlEventName;
    
    public WrapperControlEvent(final int controlEvent, final String controlEventName) {
        this.m_controlEvent = controlEvent;
        this.m_controlEventName = controlEventName;
    }
    
    public long getFlags() {
        return super.getFlags() | 0x2L;
    }
    
    public int getControlEvent() {
        return this.m_controlEvent;
    }
    
    public String getControlEventName() {
        return this.m_controlEventName;
    }
    
    public String toString() {
        return "WrapperControlEvent[controlEvent=" + this.getControlEvent() + ", controlEventName=" + this.getControlEventName() + ", consumed=" + this.isConsumed() + "]";
    }
}
