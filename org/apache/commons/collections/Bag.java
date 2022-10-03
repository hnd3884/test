package org.apache.commons.collections;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

public interface Bag extends Collection
{
    int getCount(final Object p0);
    
    boolean add(final Object p0);
    
    boolean add(final Object p0, final int p1);
    
    boolean remove(final Object p0);
    
    boolean remove(final Object p0, final int p1);
    
    Set uniqueSet();
    
    int size();
    
    boolean containsAll(final Collection p0);
    
    boolean removeAll(final Collection p0);
    
    boolean retainAll(final Collection p0);
    
    Iterator iterator();
}
