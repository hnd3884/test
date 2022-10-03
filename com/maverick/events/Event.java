package com.maverick.events;

import java.util.Enumeration;
import java.util.Hashtable;

public class Event extends EventObject
{
    private final int d;
    private final boolean c;
    private final Hashtable b;
    
    public Event(final Object o, final int d, final boolean c) {
        super(o);
        this.b = new Hashtable();
        this.d = d;
        this.c = c;
    }
    
    public int getId() {
        return this.d;
    }
    
    public boolean getState() {
        return this.c;
    }
    
    public Object getAttribute(final String s) {
        return this.b.get(s);
    }
    
    public String getAllAttributes() {
        final StringBuffer sb = new StringBuffer();
        final Enumeration keys = this.b.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            final String string = this.b.get(s).toString();
            sb.append("|\r\n");
            sb.append(s);
            sb.append(" = ");
            sb.append(string);
        }
        return sb.toString();
    }
    
    public Event addAttribute(final String s, final Object o) {
        this.b.put(s, (o == null) ? "null" : o);
        return this;
    }
}
