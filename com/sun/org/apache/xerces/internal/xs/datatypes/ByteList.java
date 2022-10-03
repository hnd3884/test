package com.sun.org.apache.xerces.internal.xs.datatypes;

import com.sun.org.apache.xerces.internal.xs.XSException;
import java.util.List;

public interface ByteList extends List
{
    int getLength();
    
    boolean contains(final byte p0);
    
    byte item(final int p0) throws XSException;
}
