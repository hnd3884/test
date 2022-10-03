package org.w3c.dom.events;

public interface Event
{
    public static final short CAPTURING_PHASE = 1;
    public static final short AT_TARGET = 2;
    public static final short BUBBLING_PHASE = 3;
    
    String getType();
    
    EventTarget getTarget();
    
    EventTarget getCurrentTarget();
    
    short getEventPhase();
    
    boolean getBubbles();
    
    boolean getCancelable();
    
    long getTimeStamp();
    
    void stopPropagation();
    
    void preventDefault();
    
    void initEvent(final String p0, final boolean p1, final boolean p2);
}
