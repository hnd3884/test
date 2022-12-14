package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPoint
{
    float getX();
    
    void setX(final float p0) throws DOMException;
    
    float getY();
    
    void setY(final float p0) throws DOMException;
    
    SVGPoint matrixTransform(final SVGMatrix p0);
}
