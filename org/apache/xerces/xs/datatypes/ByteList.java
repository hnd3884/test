package org.apache.xerces.xs.datatypes;

import org.apache.xerces.xs.XSException;
import java.util.List;

public interface ByteList extends List
{
    int getLength();
    
    boolean contains(final byte p0);
    
    byte item(final int p0) throws XSException;
    
    byte[] toByteArray();
}
