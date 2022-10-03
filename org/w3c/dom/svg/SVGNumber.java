package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGNumber
{
    float getValue();
    
    void setValue(final float p0) throws DOMException;
}
