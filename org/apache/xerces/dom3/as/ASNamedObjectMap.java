package org.apache.xerces.dom3.as;

import org.w3c.dom.DOMException;

public interface ASNamedObjectMap
{
    int getLength();
    
    ASObject getNamedItem(final String p0);
    
    ASObject getNamedItemNS(final String p0, final String p1);
    
    ASObject item(final int p0);
    
    ASObject removeNamedItem(final String p0) throws DOMException;
    
    ASObject removeNamedItemNS(final String p0, final String p1) throws DOMException;
    
    ASObject setNamedItem(final ASObject p0) throws DOMException;
    
    ASObject setNamedItemNS(final ASObject p0) throws DOMException;
}
