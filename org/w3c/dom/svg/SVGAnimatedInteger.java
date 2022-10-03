package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGAnimatedInteger
{
    int getBaseVal();
    
    void setBaseVal(final int p0) throws DOMException;
    
    int getAnimVal();
}
