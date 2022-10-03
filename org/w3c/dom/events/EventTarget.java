package org.w3c.dom.events;

public interface EventTarget
{
    void addEventListener(final String p0, final EventListener p1, final boolean p2);
    
    void removeEventListener(final String p0, final EventListener p1, final boolean p2);
    
    boolean dispatchEvent(final Event p0) throws EventException;
}
