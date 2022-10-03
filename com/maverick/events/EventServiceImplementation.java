package com.maverick.events;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

public class EventServiceImplementation implements EventService
{
    private static final EventService c;
    private final Hashtable d;
    private Vector b;
    
    protected EventServiceImplementation() {
        this.b = new Vector();
        this.d = new Hashtable();
    }
    
    public static EventService getInstance() {
        return EventServiceImplementation.c;
    }
    
    public synchronized void addListener(final String s, final EventListener eventListener) {
        if (s.trim().equals("")) {
            this.b.addElement(eventListener);
        }
        else {
            this.d.put(s.trim(), eventListener);
        }
    }
    
    public synchronized void removeListener(final String s) {
        this.d.remove(s);
    }
    
    public synchronized void fireEvent(final Event event) {
        if (event == null) {
            return;
        }
        final Enumeration elements = this.b.elements();
        while (elements.hasMoreElements()) {
            final EventListener eventListener = (EventListener)elements.nextElement();
            try {
                eventListener.processEvent(event);
            }
            catch (final Throwable t) {}
        }
        final String name = Thread.currentThread().getName();
        final Enumeration keys = this.d.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            try {
                if (name.indexOf(45) <= -1 || !s.startsWith(name.substring(0, name.indexOf(45)))) {
                    continue;
                }
                ((EventListener)this.d.get(s)).processEvent(event);
            }
            catch (final Throwable t2) {}
        }
    }
    
    static {
        c = new EventServiceImplementation();
    }
}
