package com.sun.org.apache.xerces.internal.xs;

import java.util.List;

public interface StringList extends List
{
    int getLength();
    
    boolean contains(final String p0);
    
    String item(final int p0);
}
