package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPathSegCurvetoCubicSmoothAbs extends SVGPathSeg
{
    float getX();
    
    void setX(final float p0) throws DOMException;
    
    float getY();
    
    void setY(final float p0) throws DOMException;
    
    float getX2();
    
    void setX2(final float p0) throws DOMException;
    
    float getY2();
    
    void setY2(final float p0) throws DOMException;
}
