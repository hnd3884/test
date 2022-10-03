package org.jfree.data;

import java.util.List;

public interface KeyedValues extends Values
{
    Comparable getKey(final int p0);
    
    int getIndex(final Comparable p0);
    
    List getKeys();
    
    Number getValue(final Comparable p0);
}
