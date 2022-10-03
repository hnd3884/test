package com.sun.org.apache.xerces.internal.xs;

import java.util.List;

public interface ShortList extends List
{
    int getLength();
    
    boolean contains(final short p0);
    
    short item(final int p0) throws XSException;
}
