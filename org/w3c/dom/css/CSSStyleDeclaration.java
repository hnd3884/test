package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public interface CSSStyleDeclaration
{
    String getCssText();
    
    void setCssText(final String p0) throws DOMException;
    
    String getPropertyValue(final String p0);
    
    CSSValue getPropertyCSSValue(final String p0);
    
    String removeProperty(final String p0) throws DOMException;
    
    String getPropertyPriority(final String p0);
    
    void setProperty(final String p0, final String p1, final String p2) throws DOMException;
    
    int getLength();
    
    String item(final int p0);
    
    CSSRule getParentRule();
}
