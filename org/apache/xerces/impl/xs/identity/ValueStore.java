package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.xs.ShortList;

public interface ValueStore
{
    void addValue(final Field p0, final boolean p1, final Object p2, final short p3, final ShortList p4);
    
    void reportError(final String p0, final Object[] p1);
    
    void setElementName(final String p0);
    
    String getElementName();
}
