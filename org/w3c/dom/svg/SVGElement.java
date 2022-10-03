package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public interface SVGElement extends Element
{
    String getId();
    
    void setId(final String p0) throws DOMException;
    
    String getXMLbase();
    
    void setXMLbase(final String p0) throws DOMException;
    
    SVGSVGElement getOwnerSVGElement();
    
    SVGElement getViewportElement();
}
