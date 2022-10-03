package org.jfree.util;

import java.io.Serializable;
import java.util.Comparator;

public class ClassComparator implements Comparator, Serializable
{
    private static final long serialVersionUID = -5225335361837391120L;
    
    public int compare(final Object o1, final Object o2) {
        final Class c1 = (Class)o1;
        final Class c2 = (Class)o2;
        if (c1.equals(o2)) {
            return 0;
        }
        if (c1.isAssignableFrom(c2)) {
            return -1;
        }
        if (!c2.isAssignableFrom(c2)) {
            throw new IllegalArgumentException("The classes share no relation");
        }
        return 1;
    }
    
    public boolean isComparable(final Class c1, final Class c2) {
        return c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1);
    }
}
