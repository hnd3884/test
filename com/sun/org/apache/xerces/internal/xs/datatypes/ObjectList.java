package com.sun.org.apache.xerces.internal.xs.datatypes;

import java.util.List;

public interface ObjectList extends List
{
    int getLength();
    
    boolean contains(final Object p0);
    
    Object item(final int p0);
}
