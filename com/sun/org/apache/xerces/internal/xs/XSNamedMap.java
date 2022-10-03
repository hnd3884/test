package com.sun.org.apache.xerces.internal.xs;

import java.util.Map;

public interface XSNamedMap extends Map
{
    int getLength();
    
    XSObject item(final int p0);
    
    XSObject itemByName(final String p0, final String p1);
}
