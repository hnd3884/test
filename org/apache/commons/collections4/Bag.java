package org.apache.commons.collections4;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

public interface Bag<E> extends Collection<E>
{
    int getCount(final Object p0);
    
    boolean add(final E p0);
    
    boolean add(final E p0, final int p1);
    
    boolean remove(final Object p0);
    
    boolean remove(final Object p0, final int p1);
    
    Set<E> uniqueSet();
    
    int size();
    
    boolean containsAll(final Collection<?> p0);
    
    boolean removeAll(final Collection<?> p0);
    
    boolean retainAll(final Collection<?> p0);
    
    Iterator<E> iterator();
}
