package com.sun.mail.util.logging;

import java.util.logging.Level;
import java.io.Serializable;
import java.util.logging.LogRecord;
import java.util.Comparator;

public class SeverityComparator implements Comparator<LogRecord>, Serializable
{
    private static final long serialVersionUID = -2620442245251791965L;
    private static final Comparator<LogRecord> INSTANCE;
    
    static SeverityComparator getInstance() {
        return (SeverityComparator)SeverityComparator.INSTANCE;
    }
    
    public Throwable apply(final Throwable chain) {
        int limit = 0;
        Throwable root = chain;
        Throwable high = null;
        Throwable normal = null;
        for (Throwable cause = chain; cause != null; cause = cause.getCause()) {
            root = cause;
            if (this.isNormal(cause)) {
                normal = cause;
            }
            if (normal == null && cause instanceof Error) {
                high = cause;
            }
            if (++limit == 65536) {
                break;
            }
        }
        return (high != null) ? high : ((normal != null) ? normal : root);
    }
    
    public final int applyThenCompare(final Throwable tc1, final Throwable tc2) {
        return (tc1 == tc2) ? 0 : this.compareThrowable(this.apply(tc1), this.apply(tc2));
    }
    
    public int compareThrowable(final Throwable t1, final Throwable t2) {
        if (t1 == t2) {
            return 0;
        }
        if (t1 == null) {
            return this.isNormal(t2) ? 1 : -1;
        }
        if (t2 == null) {
            return this.isNormal(t1) ? -1 : 1;
        }
        if (t1.getClass() == t2.getClass()) {
            return 0;
        }
        if (this.isNormal(t1)) {
            return this.isNormal(t2) ? 0 : -1;
        }
        if (this.isNormal(t2)) {
            return 1;
        }
        if (t1 instanceof Error) {
            return (t2 instanceof Error) ? 0 : 1;
        }
        if (t1 instanceof RuntimeException) {
            return (t2 instanceof Error) ? -1 : ((t2 instanceof RuntimeException) ? 0 : 1);
        }
        return (t2 instanceof Error || t2 instanceof RuntimeException) ? -1 : 0;
    }
    
    @Override
    public int compare(final LogRecord o1, final LogRecord o2) {
        if (o1 == null || o2 == null) {
            throw new NullPointerException(toString(o1, o2));
        }
        if (o1 == o2) {
            return 0;
        }
        int cmp = this.compare(o1.getLevel(), o2.getLevel());
        if (cmp == 0) {
            cmp = this.applyThenCompare(o1.getThrown(), o2.getThrown());
            if (cmp == 0) {
                cmp = this.compare(o1.getSequenceNumber(), o2.getSequenceNumber());
                if (cmp == 0) {
                    cmp = this.compare(o1.getMillis(), o2.getMillis());
                }
            }
        }
        return cmp;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o.getClass() == this.getClass();
    }
    
    @Override
    public int hashCode() {
        return 31 * this.getClass().hashCode();
    }
    
    public boolean isNormal(final Throwable t) {
        if (t == null) {
            return false;
        }
        final Class<?> root = Throwable.class;
        final Class<?> error = Error.class;
        for (Class<?> c = t.getClass(); c != root; c = c.getSuperclass()) {
            if (error.isAssignableFrom(c)) {
                if (c.getName().equals("java.lang.ThreadDeath")) {
                    return true;
                }
            }
            else if (c.getName().contains("Interrupt")) {
                return true;
            }
        }
        return false;
    }
    
    private int compare(final Level a, final Level b) {
        return (a == b) ? 0 : this.compare(a.intValue(), b.intValue());
    }
    
    private static String toString(final Object o1, final Object o2) {
        return o1 + ", " + o2;
    }
    
    private int compare(final long x, final long y) {
        return (x < y) ? -1 : ((x > y) ? 1 : 0);
    }
    
    static {
        INSTANCE = new SeverityComparator();
    }
}
