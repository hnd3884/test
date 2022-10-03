package com.maverick.events;

public class EventObject
{
    protected transient Object source;
    
    public EventObject(final Object source) {
        if (source == null) {
            throw new IllegalArgumentException("null source");
        }
        this.source = source;
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public String toString() {
        return this.getClass().getName() + "[source=" + this.source + "]";
    }
}
