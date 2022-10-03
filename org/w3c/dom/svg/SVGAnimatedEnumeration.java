package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGAnimatedEnumeration
{
    short getBaseVal();
    
    void setBaseVal(final short p0) throws DOMException;
    
    short getAnimVal();
}
