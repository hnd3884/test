package org.apache.commons.collections4;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

public interface MultiSet<E> extends Collection<E>
{
    int getCount(final Object p0);
    
    int setCount(final E p0, final int p1);
    
    boolean add(final E p0);
    
    int add(final E p0, final int p1);
    
    boolean remove(final Object p0);
    
    int remove(final Object p0, final int p1);
    
    Set<E> uniqueSet();
    
    Set<Entry<E>> entrySet();
    
    Iterator<E> iterator();
    
    int size();
    
    boolean containsAll(final Collection<?> p0);
    
    boolean removeAll(final Collection<?> p0);
    
    boolean retainAll(final Collection<?> p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    public interface Entry<E>
    {
        E getElement();
        
        int getCount();
        
        boolean equals(final Object p0);
        
        int hashCode();
    }
}
