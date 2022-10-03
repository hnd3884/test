package com.sun.org.apache.xerces.internal.xs;

import java.util.List;

public interface XSNamespaceItemList extends List
{
    int getLength();
    
    XSNamespaceItem item(final int p0);
}
