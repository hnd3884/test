package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGAltGlyphElement extends SVGTextPositioningElement, SVGURIReference
{
    String getGlyphRef();
    
    void setGlyphRef(final String p0) throws DOMException;
    
    String getFormat();
    
    void setFormat(final String p0) throws DOMException;
}
