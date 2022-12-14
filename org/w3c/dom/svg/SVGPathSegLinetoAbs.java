package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPathSegLinetoAbs extends SVGPathSeg
{
    float getX();
    
    void setX(final float p0) throws DOMException;
    
    float getY();
    
    void setY(final float p0) throws DOMException;
}
