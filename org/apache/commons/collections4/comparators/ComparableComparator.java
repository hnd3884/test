package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;

public class ComparableComparator<E extends Comparable<? super E>> implements Comparator<E>, Serializable
{
    private static final long serialVersionUID = -291439688585137865L;
    public static final ComparableComparator INSTANCE;
    
    public static <E extends Comparable<? super E>> ComparableComparator<E> comparableComparator() {
        return ComparableComparator.INSTANCE;
    }
    
    @Override
    public int compare(final E obj1, final E obj2) {
        return obj1.compareTo(obj2);
    }
    
    @Override
    public int hashCode() {
        return "ComparableComparator".hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        return this == object || (null != object && object.getClass().equals(this.getClass()));
    }
    
    static {
        INSTANCE = new ComparableComparator();
    }
}
