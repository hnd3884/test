package org.apache.tomcat.util;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class MultiThrowable extends Throwable
{
    private static final long serialVersionUID = 1L;
    private List<Throwable> throwables;
    
    public MultiThrowable() {
        this.throwables = new ArrayList<Throwable>();
    }
    
    public void add(final Throwable t) {
        this.throwables.add(t);
    }
    
    public List<Throwable> getThrowables() {
        return Collections.unmodifiableList((List<? extends Throwable>)this.throwables);
    }
    
    public Throwable getThrowable() {
        if (this.size() == 0) {
            return null;
        }
        if (this.size() == 1) {
            return this.throwables.get(0);
        }
        return this;
    }
    
    public int size() {
        return this.throwables.size();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(": ");
        sb.append(this.size());
        sb.append(" wrapped Throwables: ");
        for (final Throwable t : this.throwables) {
            sb.append('[');
            sb.append(t.getMessage());
            sb.append(']');
        }
        return sb.toString();
    }
}
