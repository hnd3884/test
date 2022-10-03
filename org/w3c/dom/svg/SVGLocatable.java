package org.w3c.dom.svg;

public interface SVGLocatable
{
    SVGElement getNearestViewportElement();
    
    SVGElement getFarthestViewportElement();
    
    SVGRect getBBox();
    
    SVGMatrix getCTM();
    
    SVGMatrix getScreenCTM();
    
    SVGMatrix getTransformToElement(final SVGElement p0) throws SVGException;
}
