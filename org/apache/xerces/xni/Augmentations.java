package org.apache.xerces.xni;

import java.util.Enumeration;

public interface Augmentations
{
    Object putItem(final String p0, final Object p1);
    
    Object getItem(final String p0);
    
    Object removeItem(final String p0);
    
    Enumeration keys();
    
    void removeAllItems();
}
