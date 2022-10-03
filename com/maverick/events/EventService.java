package com.maverick.events;

public interface EventService
{
    void addListener(final String p0, final EventListener p1);
    
    void removeListener(final String p0);
    
    void fireEvent(final Event p0);
}
