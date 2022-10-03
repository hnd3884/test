package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class SortOrder implements Serializable
{
    private static final long serialVersionUID = -2124469847758108312L;
    public static final SortOrder ASCENDING;
    public static final SortOrder DESCENDING;
    private String name;
    
    static {
        ASCENDING = new SortOrder("SortOrder.ASCENDING");
        DESCENDING = new SortOrder("SortOrder.DESCENDING");
    }
    
    private SortOrder(final String name) {
        this.name = name;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SortOrder)) {
            return false;
        }
        final SortOrder that = (SortOrder)obj;
        return this.name.equals(that.toString());
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(SortOrder.ASCENDING)) {
            return SortOrder.ASCENDING;
        }
        if (this.equals(SortOrder.DESCENDING)) {
            return SortOrder.DESCENDING;
        }
        return null;
    }
    
    public String toString() {
        return this.name;
    }
}
