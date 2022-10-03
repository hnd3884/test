package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPathSegLinetoVerticalAbs extends SVGPathSeg
{
    float getY();
    
    void setY(final float p0) throws DOMException;
}
