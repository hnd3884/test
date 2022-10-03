package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.ShortList;

public interface ValueStore
{
    void addValue(final Field p0, final Object p1, final short p2, final ShortList p3);
    
    void reportError(final String p0, final Object[] p1);
}
