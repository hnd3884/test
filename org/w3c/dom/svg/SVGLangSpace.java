package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGLangSpace
{
    String getXMLlang();
    
    void setXMLlang(final String p0) throws DOMException;
    
    String getXMLspace();
    
    void setXMLspace(final String p0) throws DOMException;
}
