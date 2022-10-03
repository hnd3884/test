package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPathSegLinetoHorizontalAbs extends SVGPathSeg
{
    float getX();
    
    void setX(final float p0) throws DOMException;
}
