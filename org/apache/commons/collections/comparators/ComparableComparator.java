package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;

public class ComparableComparator implements Comparator, Serializable
{
    private static final long serialVersionUID = -291439688585137865L;
    private static final ComparableComparator instance;
    
    public static ComparableComparator getInstance() {
        return ComparableComparator.instance;
    }
    
    public int compare(final Object obj1, final Object obj2) {
        return ((Comparable)obj1).compareTo(obj2);
    }
    
    public int hashCode() {
        return "ComparableComparator".hashCode();
    }
    
    public boolean equals(final Object object) {
        return this == object || (null != object && object.getClass().equals(this.getClass()));
    }
    
    static {
        instance = new ComparableComparator();
    }
}
